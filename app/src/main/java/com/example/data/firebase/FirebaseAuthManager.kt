package com.example.data.firebase

import android.util.Log
import com.example.data.model.UserRole
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

data class FirebaseUserProfile(
    val uid: String = "",
    val email: String = "",
    val role: UserRole = UserRole.PARTICIPANT,
    val participantId: Int? = null,
    val displayName: String = "",
    val isAnonymous: Boolean = false
)

data class AuthState(
    val isAuthenticated: Boolean = false,
    val userProfile: FirebaseUserProfile? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class FirebaseAuthManager {

    private val auth: FirebaseAuth? by lazy {
        try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.w("FirebaseAuthManager", "Firebase Auth not initialized: ${e.message}")
            null
        }
    }

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w("FirebaseAuthManager", "Firestore not initialized: ${e.message}")
            null
        }
    }

    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        checkCurrentFirebaseUser()
    }

    private fun checkCurrentFirebaseUser() {
        try {
            val user = auth?.currentUser
            if (user != null) {
                // If there's an existing cached session, load their profile asynchronously
                _authState.value = AuthState(
                    isAuthenticated = true,
                    userProfile = FirebaseUserProfile(
                        uid = user.uid,
                        email = user.email ?: "",
                        role = inferRoleFromEmail(user.email ?: ""),
                        displayName = user.displayName ?: user.email ?: ""
                    )
                )
            }
        } catch (e: Exception) {
            Log.e("FirebaseAuthManager", "Error checking current user: ${e.message}")
        }
    }

    private fun inferRoleFromEmail(email: String): UserRole {
        val lower = email.lowercase().trim()
        return when {
            lower.contains("admin") || lower == "admin@bukkakery.com" -> UserRole.ADMIN
            lower.contains("lab") || lower.contains("clinica") || lower.contains("laboratorio") || lower == "lab@bukkakery.com" -> UserRole.LABORATORY
            else -> UserRole.PARTICIPANT
        }
    }

    // -------------------------------------------------------------------------
    // 1. INICIAR SESIÓN CON EMAIL Y CONTRASEÑA
    // -------------------------------------------------------------------------
    suspend fun signInWithEmail(email: String, pass: String): Result<FirebaseUserProfile> = withContext(Dispatchers.IO) {
        val cleanEmail = email.trim()
        val cleanPass = pass.trim()

        _authState.value = _authState.value.copy(isLoading = true, errorMessage = null)

        val fbAuth = auth
        val db = firestore

        if (fbAuth == null) {
            // Offline/Local Simulation Fallback
            val role = inferRoleFromEmail(cleanEmail)
            val profile = FirebaseUserProfile(
                uid = "local_${System.currentTimeMillis()}",
                email = cleanEmail,
                role = role,
                displayName = cleanEmail.substringBefore("@")
            )
            _authState.value = AuthState(
                isAuthenticated = true,
                userProfile = profile,
                isLoading = false
            )
            return@withContext Result.success(profile)
        }

        try {
            val authResult = fbAuth.signInWithEmailAndPassword(cleanEmail, cleanPass).await()
            val fbUser = authResult.user ?: throw IllegalStateException("Usuario no obtenido tras login.")

            // Fetch role and details from Firestore /users/{uid}
            val profile = fetchProfileFromFirestore(fbUser, cleanEmail)

            _authState.value = AuthState(
                isAuthenticated = true,
                userProfile = profile,
                isLoading = false
            )
            Result.success(profile)
        } catch (e: Exception) {
            Log.e("FirebaseAuthManager", "Login failed: ${e.message}", e)
            val friendlyError = parseAuthError(e)
            _authState.value = AuthState(
                isAuthenticated = false,
                userProfile = null,
                isLoading = false,
                errorMessage = friendlyError
            )
            Result.failure(Exception(friendlyError, e))
        }
    }

    // -------------------------------------------------------------------------
    // 2. CREAR NUEVO USUARIO CON ROL EN FIRESTORE (EXCLUSIVO ADMIN / CASTING)
    // -------------------------------------------------------------------------
    suspend fun createUserWithRole(
        email: String,
        pass: String,
        role: UserRole,
        participantId: Int? = null,
        displayName: String = "",
        currentAdminRole: UserRole
    ): Result<FirebaseUserProfile> = withContext(Dispatchers.IO) {
        if (currentAdminRole != UserRole.ADMIN) {
            val err = "Acceso Denegado: Solo el Administrador puede registrar nuevas cuentas con roles."
            return@withContext Result.failure(SecurityException(err))
        }

        val cleanEmail = email.trim()
        val cleanPass = pass.trim()

        val fbAuth = auth
        val db = firestore

        if (fbAuth == null || db == null) {
            val profile = FirebaseUserProfile(
                uid = "offline_${System.currentTimeMillis()}",
                email = cleanEmail,
                role = role,
                participantId = participantId,
                displayName = displayName.ifBlank { cleanEmail.substringBefore("@") }
            )
            return@withContext Result.success(profile)
        }

        try {
            val result = fbAuth.createUserWithEmailAndPassword(cleanEmail, cleanPass).await()
            val fbUser = result.user ?: throw IllegalStateException("Error al crear usuario en Firebase Auth")

            val profileData = mapOf(
                "uid" to fbUser.uid,
                "email" to cleanEmail,
                "role" to role.name,
                "participantId" to participantId,
                "displayName" to displayName.ifBlank { cleanEmail.substringBefore("@") },
                "createdAt" to System.currentTimeMillis(),
                "updatedAt" to System.currentTimeMillis()
            )

            // Save role mapping in Firestore /users/{uid}
            db.collection("users").document(fbUser.uid).set(profileData, SetOptions.merge()).await()

            val profile = FirebaseUserProfile(
                uid = fbUser.uid,
                email = cleanEmail,
                role = role,
                participantId = participantId,
                displayName = displayName.ifBlank { cleanEmail.substringBefore("@") }
            )
            Result.success(profile)
        } catch (e: Exception) {
            Log.e("FirebaseAuthManager", "Error creating user: ${e.message}", e)
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------------------
    // 3. ASIGNAR / ACTUALIZAR ROL EN FIRESTORE (/users/{uid})
    // -------------------------------------------------------------------------
    suspend fun setUserRoleInFirestore(
        targetUid: String,
        email: String,
        role: UserRole,
        participantId: Int? = null,
        displayName: String = "",
        executorRole: UserRole
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        if (executorRole != UserRole.ADMIN) {
            return@withContext Result.failure(SecurityException("Solo el Administrador puede modificar roles en Firestore."))
        }

        val db = firestore ?: return@withContext Result.failure(IllegalStateException("Firestore no disponible."))

        try {
            val data = mapOf(
                "uid" to targetUid,
                "email" to email,
                "role" to role.name,
                "participantId" to participantId,
                "displayName" to displayName,
                "updatedAt" to System.currentTimeMillis()
            )
            db.collection("users").document(targetUid).set(data, SetOptions.merge()).await()
            Result.success(true)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------------------
    // 4. RECUPERAR PERFIL Y ROL DESDE FIRESTORE
    // -------------------------------------------------------------------------
    private suspend fun fetchProfileFromFirestore(fbUser: FirebaseUser, email: String): FirebaseUserProfile {
        val db = firestore
        if (db == null) {
            return FirebaseUserProfile(
                uid = fbUser.uid,
                email = email,
                role = inferRoleFromEmail(email),
                displayName = fbUser.displayName ?: email.substringBefore("@")
            )
        }

        return try {
            val doc = db.collection("users").document(fbUser.uid).get().await()
            if (doc.exists()) {
                val roleStr = doc.getString("role") ?: inferRoleFromEmail(email).name
                val role = try {
                    UserRole.valueOf(roleStr.uppercase())
                } catch (e: Exception) {
                    inferRoleFromEmail(email)
                }
                val pId = doc.getLong("participantId")?.toInt()
                val dName = doc.getString("displayName") ?: fbUser.displayName ?: email.substringBefore("@")

                FirebaseUserProfile(
                    uid = fbUser.uid,
                    email = email,
                    role = role,
                    participantId = pId,
                    displayName = dName
                )
            } else {
                // Auto-create initial profile document in Firestore
                val role = inferRoleFromEmail(email)
                val newProfile = FirebaseUserProfile(
                    uid = fbUser.uid,
                    email = email,
                    role = role,
                    displayName = fbUser.displayName ?: email.substringBefore("@")
                )
                val initialData = mapOf(
                    "uid" to fbUser.uid,
                    "email" to email,
                    "role" to role.name,
                    "displayName" to newProfile.displayName,
                    "createdAt" to System.currentTimeMillis()
                )
                db.collection("users").document(fbUser.uid).set(initialData, SetOptions.merge()).await()
                newProfile
            }
        } catch (e: Exception) {
            Log.w("FirebaseAuthManager", "Error fetching user doc: ${e.message}")
            FirebaseUserProfile(
                uid = fbUser.uid,
                email = email,
                role = inferRoleFromEmail(email),
                displayName = fbUser.displayName ?: email.substringBefore("@")
            )
        }
    }

    // -------------------------------------------------------------------------
    // 5. CERRAR SESIÓN
    // -------------------------------------------------------------------------
    fun signOut() {
        try {
            auth?.signOut()
        } catch (e: Exception) {
            Log.e("FirebaseAuthManager", "Sign out error: ${e.message}")
        }
        _authState.value = AuthState(
            isAuthenticated = false,
            userProfile = null,
            isLoading = false
        )
    }

    private fun parseAuthError(e: Exception): String {
        val msg = e.message ?: ""
        return when {
            msg.contains("password", ignoreCase = true) || msg.contains("credential", ignoreCase = true) ->
                "Contraseña incorrecta o credencial inválida."
            msg.contains("no user", ignoreCase = true) || msg.contains("user-not-found", ignoreCase = true) ->
                "No existe ninguna cuenta registrada con este correo."
            msg.contains("badly formatted", ignoreCase = true) || msg.contains("invalid-email", ignoreCase = true) ->
                "Formato de correo electrónico inválido."
            msg.contains("network", ignoreCase = true) ->
                "Error de conexión de red al conectar con Firebase."
            else -> "Error de autenticación: ${e.localizedMessage ?: msg}"
        }
    }
}
