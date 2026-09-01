package com.example.data.firebase

import android.util.Log
import com.example.data.model.AttendanceEntity
import com.example.data.model.CommentEntity
import com.example.data.model.ParticipantEntity
import com.example.data.model.ShootEventEntity
import com.example.data.model.ShotLogEntity
import com.example.data.model.UserRole
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

enum class CloudSyncState {
    IDLE,
    SYNCING,
    SYNCED,
    ERROR,
    OFFLINE
}

data class FirestoreSyncInfo(
    val state: CloudSyncState = CloudSyncState.IDLE,
    val lastSyncTimestamp: Long = 0L,
    val message: String = "Listo para sincronizar con Firestore",
    val syncedParticipantsCount: Int = 0,
    val syncedShootsCount: Int = 0,
    val syncedLogsCount: Int = 0
)

class FirestoreManager {

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w("FirestoreManager", "Firestore not initialized or missing google-services: ${e.message}")
            null
        }
    }

    private val _syncInfo = MutableStateFlow(FirestoreSyncInfo())
    val syncInfo: StateFlow<FirestoreSyncInfo> = _syncInfo.asStateFlow()

    // -------------------------------------------------------------------------
    // SECURITY & PERMISSION VERIFICATION
    // Ensures only administrators can perform critical write operations
    // -------------------------------------------------------------------------
    fun canPerformCriticalWrite(role: UserRole): Boolean {
        return role == UserRole.ADMIN
    }

    fun canPerformMedicalUpdate(role: UserRole): Boolean {
        return role == UserRole.ADMIN || role == UserRole.LABORATORY
    }

    // -------------------------------------------------------------------------
    // 1. PARTICIPANTS SYNC (Admin: Full write / Lab: Medical update / Participant: No cloud write)
    // -------------------------------------------------------------------------
    suspend fun uploadParticipants(
        participants: List<ParticipantEntity>,
        currentRole: UserRole
    ): Result<Int> = withContext(Dispatchers.IO) {
        if (!canPerformCriticalWrite(currentRole) && !canPerformMedicalUpdate(currentRole)) {
            val errorMsg = "Acceso Denegado: Solo el Administrador o Laboratorio pueden sincronizar participantes con Firestore."
            _syncInfo.value = _syncInfo.value.copy(
                state = CloudSyncState.ERROR,
                message = errorMsg
            )
            return@withContext Result.failure(SecurityException(errorMsg))
        }

        val db = firestore ?: return@withContext Result.failure(
            IllegalStateException("Firestore no está disponible en este momento.")
        )

        try {
            _syncInfo.value = _syncInfo.value.copy(
                state = CloudSyncState.SYNCING,
                message = "Subiendo ${participants.size} fichas de participantes a Firestore..."
            )

            val collection = db.collection("participants")
            var uploadedCount = 0

            for (p in participants) {
                val docRef = collection.document(p.id.toString())

                if (currentRole == UserRole.ADMIN) {
                    // Full admin write (casting data, financials, balances, medical, personal info)
                    val data = mapOf(
                        "id" to p.id,
                        "nick" to p.nick,
                        "fullName" to p.fullName,
                        "docType" to p.docType,
                        "docNumber" to p.docNumber,
                        "phone" to p.phone,
                        "iban" to p.iban,
                        "email" to p.email,
                        "telegramUser" to p.telegramUser,
                        "birthDate" to p.birthDate,
                        "referrer" to p.referrer,
                        "balance" to p.balance,
                        "monthlyParticipations" to p.monthlyParticipations,
                        "totalParticipations" to p.totalParticipations,
                        "status" to p.status,
                        "testResult" to p.testResult,
                        "testDate" to p.testDate,
                        "sampleNumber" to p.sampleNumber,
                        "renewalStatus" to p.renewalStatus,
                        "lastShootCode" to p.lastShootCode,
                        "isConfirmedForShoot" to p.isConfirmedForShoot,
                        "comments" to p.comments,
                        "updatedByRole" to "ADMIN",
                        "updatedAt" to System.currentTimeMillis()
                    )
                    docRef.set(data, SetOptions.merge()).await()
                } else if (currentRole == UserRole.LABORATORY) {
                    // Restricted laboratory update: only analytical and sample fields
                    val medicalData = mapOf(
                        "sampleNumber" to p.sampleNumber,
                        "testDate" to p.testDate,
                        "testResult" to p.testResult,
                        "renewalStatus" to p.renewalStatus,
                        "updatedByRole" to "LABORATORY",
                        "updatedAt" to System.currentTimeMillis()
                    )
                    docRef.set(medicalData, SetOptions.merge()).await()
                }
                uploadedCount++
            }

            _syncInfo.value = _syncInfo.value.copy(
                state = CloudSyncState.SYNCED,
                lastSyncTimestamp = System.currentTimeMillis(),
                syncedParticipantsCount = uploadedCount,
                message = "Sincronización completada: $uploadedCount participantes guardados en Firestore."
            )
            Result.success(uploadedCount)
        } catch (e: Exception) {
            Log.e("FirestoreManager", "Error uploading participants: ${e.message}", e)
            _syncInfo.value = _syncInfo.value.copy(
                state = CloudSyncState.ERROR,
                message = "Error en sincronización: ${e.localizedMessage ?: e.message}"
            )
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------------------
    // 2. SHOOTS & EVENTS SYNC (Admin ONLY)
    // -------------------------------------------------------------------------
    suspend fun uploadShootEvent(
        shoot: ShootEventEntity,
        currentRole: UserRole
    ): Result<Boolean> = withContext(Dispatchers.IO) {
        if (!canPerformCriticalWrite(currentRole)) {
            val errorMsg = "Acceso Denegado: Solo el Administrador puede modificar rodajes en Firestore."
            _syncInfo.value = _syncInfo.value.copy(state = CloudSyncState.ERROR, message = errorMsg)
            return@withContext Result.failure(SecurityException(errorMsg))
        }

        val db = firestore ?: return@withContext Result.failure(
            IllegalStateException("Firestore no está disponible.")
        )

        try {
            _syncInfo.value = _syncInfo.value.copy(
                state = CloudSyncState.SYNCING,
                message = "Guardando evento de rodaje ${shoot.shootCode} en Firestore..."
            )

            val data = mapOf(
                "shootCode" to shoot.shootCode,
                "date" to shoot.date,
                "time" to shoot.time,
                "title" to shoot.title,
                "actressName" to shoot.actressName,
                "actressFee" to shoot.actressFee,
                "actressPaymentMethod" to shoot.actressPaymentMethod,
                "actressIsPaid" to shoot.actressIsPaid,
                "staffBudget" to shoot.staffBudget,
                "expensesNotes" to shoot.expensesNotes,
                "status" to shoot.status,
                "notes" to shoot.notes,
                "updatedAt" to System.currentTimeMillis()
            )

            db.collection("shoots")
                .document(shoot.shootCode)
                .set(data, SetOptions.merge())
                .await()

            _syncInfo.value = _syncInfo.value.copy(
                state = CloudSyncState.SYNCED,
                lastSyncTimestamp = System.currentTimeMillis(),
                syncedShootsCount = 1,
                message = "Rodaje ${shoot.shootCode} sincronizado en la nube."
            )
            Result.success(true)
        } catch (e: Exception) {
            Log.e("FirestoreManager", "Error uploading shoot: ${e.message}", e)
            _syncInfo.value = _syncInfo.value.copy(
                state = CloudSyncState.ERROR,
                message = "Error guardando rodaje: ${e.localizedMessage ?: e.message}"
            )
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------------------
    // 3. HISTORIC SHOT LOGS & AUDIT RECORDS SYNC (Admin ONLY)
    // -------------------------------------------------------------------------
    suspend fun uploadShotLogs(
        logs: List<ShotLogEntity>,
        currentRole: UserRole
    ): Result<Int> = withContext(Dispatchers.IO) {
        if (!canPerformCriticalWrite(currentRole)) {
            val errorMsg = "Acceso Denegado: Solo el Administrador puede registrar logs históricos en Firestore."
            return@withContext Result.failure(SecurityException(errorMsg))
        }

        val db = firestore ?: return@withContext Result.failure(
            IllegalStateException("Firestore no está disponible.")
        )

        try {
            val collection = db.collection("shot_logs")
            var count = 0
            for (log in logs) {
                val docId = if (log.id > 0) log.id.toString() else "${log.shootCode}_${log.lockerNumber}_${log.timestamp}"
                val data = mapOf(
                    "id" to log.id,
                    "shootCode" to log.shootCode,
                    "lockerNumber" to log.lockerNumber,
                    "participantId" to log.participantId,
                    "bonus" to log.bonus,
                    "bonusReason" to log.bonusReason,
                    "timestamp" to log.timestamp,
                    "orderIndex" to log.orderIndex
                )
                collection.document(docId).set(data, SetOptions.merge()).await()
                count++
            }

            _syncInfo.value = _syncInfo.value.copy(
                syncedLogsCount = count
            )
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------------------
    // 4. ATTENDANCES & FINANCIAL SETTLEMENTS (Admin ONLY)
    // -------------------------------------------------------------------------
    suspend fun uploadAttendances(
        attendances: List<AttendanceEntity>,
        currentRole: UserRole
    ): Result<Int> = withContext(Dispatchers.IO) {
        if (!canPerformCriticalWrite(currentRole)) {
            return@withContext Result.failure(
                SecurityException("Acceso Denegado: Modificación de asistencias y liquidaciones restringida a Administración.")
            )
        }

        val db = firestore ?: return@withContext Result.failure(
            IllegalStateException("Firestore no disponible.")
        )

        try {
            val collection = db.collection("attendances")
            var count = 0
            for (att in attendances) {
                val docId = "${att.shootCode}_${att.participantId}"
                val data = mapOf(
                    "id" to att.id,
                    "shootCode" to att.shootCode,
                    "participantId" to att.participantId,
                    "lockerNumber" to att.lockerNumber,
                    "isPresent" to att.isPresent,
                    "contractSigned" to att.contractSigned,
                    "contractSignedAt" to att.contractSignedAt,
                    "initialBalance" to att.initialBalance,
                    "cashDeliveredAtEntry" to att.cashDeliveredAtEntry,
                    "remainingBalance" to att.remainingBalance,
                    "shotsCount" to att.shotsCount,
                    "bonusPrizes" to att.bonusPrizes,
                    "prizeReason" to att.prizeReason,
                    "updatedAt" to System.currentTimeMillis()
                )
                collection.document(docId).set(data, SetOptions.merge()).await()
                count++
            }
            Result.success(count)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------------------
    // 5. FETCH PARTICIPANTS FROM FIRESTORE (Read-back into local cache)
    // -------------------------------------------------------------------------
    suspend fun fetchParticipants(): Result<List<ParticipantEntity>> = withContext(Dispatchers.IO) {
        val db = firestore ?: return@withContext Result.failure(
            IllegalStateException("Firestore no está disponible.")
        )

        try {
            val snapshot = db.collection("participants").get().await()
            val list = mutableListOf<ParticipantEntity>()

            for (doc in snapshot.documents) {
                val id = (doc.getLong("id") ?: doc.id.toLongOrNull() ?: 0L).toInt()
                if (id == 0) continue

                val p = ParticipantEntity(
                    id = id,
                    nick = doc.getString("nick") ?: "",
                    fullName = doc.getString("fullName") ?: "",
                    docType = doc.getString("docType") ?: "DNI",
                    docNumber = doc.getString("docNumber") ?: "",
                    phone = doc.getString("phone") ?: "",
                    iban = doc.getString("iban") ?: "",
                    referrer = doc.getString("referrer") ?: "MIRCO",
                    email = doc.getString("email") ?: "",
                    telegramUser = doc.getString("telegramUser") ?: "",
                    birthDate = doc.getString("birthDate") ?: "",
                    renewalStatus = doc.getString("renewalStatus") ?: "No renovar",
                    balance = (doc.getLong("balance") ?: 0L).toInt(),
                    monthlyParticipations = (doc.getLong("monthlyParticipations") ?: 0L).toInt(),
                    totalParticipations = (doc.getLong("totalParticipations") ?: 0L).toInt(),
                    status = doc.getString("status") ?: "ACTIVO",
                    testResult = doc.getString("testResult") ?: "NEGATIVO",
                    testDate = doc.getString("testDate") ?: "",
                    sampleNumber = doc.getString("sampleNumber") ?: "",
                    lastShootCode = doc.getString("lastShootCode") ?: "20263108",
                    isConfirmedForShoot = doc.getBoolean("isConfirmedForShoot") ?: false,
                    comments = doc.getString("comments") ?: ""
                )
                list.add(p)
            }
            Result.success(list)
        } catch (e: Exception) {
            Log.e("FirestoreManager", "Error fetching from Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    // -------------------------------------------------------------------------
    // 6. GLOBAL FULL CLOUD BACKUP & SYNC (Admin ONLY)
    // -------------------------------------------------------------------------
    suspend fun syncAllToFirestore(
        participants: List<ParticipantEntity>,
        shoot: ShootEventEntity,
        attendances: List<AttendanceEntity>,
        shotLogs: List<ShotLogEntity>,
        currentRole: UserRole
    ): Result<String> = withContext(Dispatchers.IO) {
        if (!canPerformCriticalWrite(currentRole)) {
            val err = "Acceso Denegado: Sincronización Global de Firestore restringida exclusivamente a Administradores."
            _syncInfo.value = _syncInfo.value.copy(state = CloudSyncState.ERROR, message = err)
            return@withContext Result.failure(SecurityException(err))
        }

        try {
            _syncInfo.value = _syncInfo.value.copy(
                state = CloudSyncState.SYNCING,
                message = "Iniciando respaldo completo de base de datos en Firestore..."
            )

            val partRes = uploadParticipants(participants, currentRole)
            val shootRes = uploadShootEvent(shoot, currentRole)
            val attRes = uploadAttendances(attendances, currentRole)
            val logRes = uploadShotLogs(shotLogs, currentRole)

            val successMsg = "Respaldo en la nube exitoso: ${partRes.getOrNull() ?: 0} usuarios, 1 rodaje, ${attRes.getOrNull() ?: 0} asistencias y ${logRes.getOrNull() ?: 0} registros guardados en Firestore."
            _syncInfo.value = _syncInfo.value.copy(
                state = CloudSyncState.SYNCED,
                lastSyncTimestamp = System.currentTimeMillis(),
                message = successMsg
            )
            Result.success(successMsg)
        } catch (e: Exception) {
            _syncInfo.value = _syncInfo.value.copy(
                state = CloudSyncState.ERROR,
                message = "Fallo en respaldo: ${e.message}"
            )
            Result.failure(e)
        }
    }
}
