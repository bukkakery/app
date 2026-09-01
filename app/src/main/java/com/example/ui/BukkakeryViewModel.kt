package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.AttendanceEntity
import com.example.data.model.CommentEntity
import com.example.data.model.ParticipantEntity
import com.example.data.model.ShootEventEntity
import com.example.data.model.ShotLogEntity
import com.example.data.model.StaffEntity
import com.example.data.model.TestAlertLevel
import com.example.data.model.TestRecordEntity
import com.example.data.model.UserRole
import com.example.data.repository.BukkakeryRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ParticipantFilter(val label: String) {
    ALL("Todos"),
    CONFIRMED("Confirmados"),
    EXPIRING_SOON("Por Vencer (≤12d)"),
    FREE_RENEWAL("Renovar Gratis (>5)"),
    BANNED("Baneados"),
    POSITIVE("Positivos")
}

class BukkakeryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = BukkakeryRepository.getInstance(application)

    // Authentication & Active Session State
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _authenticatedParticipant = MutableStateFlow<ParticipantEntity?>(null)
    val authenticatedParticipant: StateFlow<ParticipantEntity?> = _authenticatedParticipant.asStateFlow()

    private val _authenticatedStaffName = MutableStateFlow("")
    val authenticatedStaffName: StateFlow<String> = _authenticatedStaffName.asStateFlow()

    // Current Active Role
    private val _currentRole = MutableStateFlow(UserRole.ADMIN)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    // Admin Dashboard Selected Tab (0: Prerodaje, 1: Control Acceso, 2: Live Rodaje, 3: Balance, 4: Base Datos SQL)
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    // Search and Filter State
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedFilter = MutableStateFlow(ParticipantFilter.ALL)
    val selectedFilter: StateFlow<ParticipantFilter> = _selectedFilter.asStateFlow()

    // SQL Custom Runner
    private val _sqlQueryInput = MutableStateFlow("SELECT * FROM participants WHERE balance > 0 ORDER BY balance DESC")
    val sqlQueryInput: StateFlow<String> = _sqlQueryInput.asStateFlow()

    private val _activeSqlQuery = MutableStateFlow("SELECT * FROM participants ORDER BY id ASC")

    // UI Snackbars and Feedback
    private val _snackBarMessage = MutableSharedFlow<String>()
    val snackBarMessage: SharedFlow<String> = _snackBarMessage.asSharedFlow()

    // Active Shoot
    val currentShoot: StateFlow<ShootEventEntity?> = repository.getCurrentShoot("20263108")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // Staff List
    val staffList: StateFlow<List<StaffEntity>> = repository.getStaffForShoot("20263108")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // All Participants
    val allParticipants: StateFlow<List<ParticipantEntity>> = repository.getAllParticipants()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Filtered Participants for table
    val filteredParticipants: StateFlow<List<ParticipantEntity>> = combine(
        allParticipants,
        _searchQuery,
        _selectedFilter,
        currentShoot
    ) { participants, query, filter, shoot ->
        val shootDate = shoot?.date ?: "31/08/2026"
        participants.filter { p ->
            // Search query match
            val matchesQuery = query.isBlank() ||
                p.nick.contains(query, ignoreCase = true) ||
                p.fullName.contains(query, ignoreCase = true) ||
                p.docNumber.contains(query, ignoreCase = true) ||
                p.telegramUser.contains(query, ignoreCase = true) ||
                p.sampleNumber.contains(query, ignoreCase = true)

            if (!matchesQuery) return@filter false

            when (filter) {
                ParticipantFilter.ALL -> true
                ParticipantFilter.CONFIRMED -> p.isConfirmedForShoot
                ParticipantFilter.EXPIRING_SOON -> {
                    val alert = p.getTestAlertLevel(shootDate)
                    alert == TestAlertLevel.EXPIRING_12 || alert == TestAlertLevel.EXPIRING_7 ||
                        alert == TestAlertLevel.EXPIRING_4 || alert == TestAlertLevel.EXPIRING_2 ||
                        alert == TestAlertLevel.EXPIRED
                }
                ParticipantFilter.FREE_RENEWAL -> p.qualifiesForFreeRenewal
                ParticipantFilter.BANNED -> p.isBanned
                ParticipantFilter.POSITIVE -> p.testResult.contains("POSITIV", ignoreCase = true) || p.testResult.contains("POSCLAM", ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Attendances for current shoot
    val attendances: StateFlow<List<AttendanceEntity>> = repository.getAttendances("20263108")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Shot Logs
    val shotLogs: StateFlow<List<ShotLogEntity>> = repository.getShotLogs("20263108")
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Custom SQL Query Results
    val sqlResults: StateFlow<List<ParticipantEntity>> = _activeSqlQuery.flatMapLatest { query ->
        repository.executeCustomSqlQuery(query)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selection Dialogs
    private val _selectedParticipantForDetail = MutableStateFlow<ParticipantEntity?>(null)
    val selectedParticipantForDetail: StateFlow<ParticipantEntity?> = _selectedParticipantForDetail.asStateFlow()

    private val _selectedParticipantForContract = MutableStateFlow<ParticipantEntity?>(null)
    val selectedParticipantForContract: StateFlow<ParticipantEntity?> = _selectedParticipantForContract.asStateFlow()

    // Participant Portal State (for visitor role)
    private val _portalSearchInput = MutableStateFlow("")
    val portalSearchInput: StateFlow<String> = _portalSearchInput.asStateFlow()

    private val _currentPortalParticipant = MutableStateFlow<ParticipantEntity?>(null)
    val currentPortalParticipant: StateFlow<ParticipantEntity?> = _currentPortalParticipant.asStateFlow()

    private val _portalComments = MutableStateFlow<List<CommentEntity>>(emptyList())
    val portalComments: StateFlow<List<CommentEntity>> = _portalComments.asStateFlow()

    private val _portalTestRecords = MutableStateFlow<List<TestRecordEntity>>(emptyList())
    val portalTestRecords: StateFlow<List<TestRecordEntity>> = _portalTestRecords.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initializeDatabaseIfEmpty()
        }
    }

    fun setRole(role: UserRole) {
        _currentRole.value = role
    }

    fun setSelectedTab(index: Int) {
        _selectedTab.value = index
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setFilter(filter: ParticipantFilter) {
        _selectedFilter.value = filter
    }

    fun setSqlQueryInput(sql: String) {
        _sqlQueryInput.value = sql
    }

    fun runSqlQuery(sql: String) {
        _activeSqlQuery.value = sql
        viewModelScope.launch {
            _snackBarMessage.emit("Consulta SQL ejecutada.")
        }
    }

    fun selectParticipantForDetail(participant: ParticipantEntity?) {
        _selectedParticipantForDetail.value = participant
    }

    fun selectParticipantForContract(participant: ParticipantEntity?) {
        _selectedParticipantForContract.value = participant
    }

    fun toggleConfirmation(participantId: Int, isConfirmed: Boolean) {
        viewModelScope.launch {
            repository.toggleShootConfirmation(participantId, isConfirmed)
            _snackBarMessage.emit(if (isConfirmed) "Asistencia confirmada para rodaje" else "Asistencia desmarcada")
        }
    }

    fun checkInParticipant(participantId: Int, lockerNumber: Int, cashDelivered: Int, contractSigned: Boolean) {
        viewModelScope.launch {
            repository.checkInParticipant("20263108", participantId, lockerNumber, cashDelivered, contractSigned)
            _snackBarMessage.emit("Control de acceso completado: Taquilla #$lockerNumber asignada.")
        }
    }

    fun recordShot(lockerNumber: Int, bonus: Int = 0, bonusReason: String = "") {
        viewModelScope.launch {
            val result = repository.recordShot("20263108", lockerNumber, bonus, bonusReason)
            result.onSuccess { msg ->
                _snackBarMessage.emit(msg)
            }.onFailure { err ->
                _snackBarMessage.emit(err.message ?: "Error al registrar corrida")
            }
        }
    }

    fun deleteShotLog(logId: Int, lockerNumber: Int, bonus: Int) {
        viewModelScope.launch {
            repository.deleteShotLog(logId, "20263108", lockerNumber, bonus)
            _snackBarMessage.emit("Registro de corrida eliminado y balance ajustado.")
        }
    }

    fun updateParticipantTest(id: Int, sample: String, testDate: String, result: String) {
        viewModelScope.launch {
            repository.updateParticipantTest(id, sample, testDate, result)
            _snackBarMessage.emit("Analítica actualizada correctamente.")
        }
    }

    fun saveParticipant(participant: ParticipantEntity) {
        viewModelScope.launch {
            if (allParticipants.value.any { it.id == participant.id }) {
                repository.updateParticipant(participant)
                _snackBarMessage.emit("Participante actualizado: ${participant.nick}")
            } else {
                repository.insertParticipant(participant)
                _snackBarMessage.emit("Nuevo participante creado: ${participant.nick}")
            }
            _selectedParticipantForDetail.value = null
        }
    }

    fun deleteParticipant(participant: ParticipantEntity) {
        viewModelScope.launch {
            repository.deleteParticipant(participant)
            _selectedParticipantForDetail.value = null
            _snackBarMessage.emit("Participante eliminado.")
        }
    }

    fun updateShoot(shoot: ShootEventEntity) {
        viewModelScope.launch {
            repository.updateShoot(shoot)
            _snackBarMessage.emit("Detalles del rodaje actualizados.")
        }
    }

    fun updateStaff(staff: StaffEntity) {
        viewModelScope.launch {
            repository.updateStaff(staff)
            _snackBarMessage.emit("Staff actualizado: ${staff.role}")
        }
    }

    fun addStaff(role: String, name: String, fee: Double) {
        viewModelScope.launch {
            repository.insertStaff(StaffEntity(shootCode = "20263108", role = role, name = name, fee = fee))
            _snackBarMessage.emit("Nuevo miembro de staff añadido.")
        }
    }

    // --- AUTHENTICATION & LOGIN/REGISTER ACTIONS ---
    fun loginAsParticipant(identifier: String, pass: String = "") {
        viewModelScope.launch {
            val query = identifier.trim()
            val list = allParticipants.value
            val found = list.find { p ->
                p.nick.equals(query, ignoreCase = true) ||
                p.docNumber.equals(query, ignoreCase = true) ||
                p.telegramUser.equals(query.removePrefix("@"), ignoreCase = true) ||
                p.email.equals(query, ignoreCase = true) ||
                p.id.toString() == query
            } ?: repository.findParticipantByQuery(query)

            if (found != null) {
                _authenticatedParticipant.value = found
                _currentPortalParticipant.value = found
                _portalSearchInput.value = found.nick
                _currentRole.value = UserRole.PARTICIPANT
                _isLoggedIn.value = true
                _snackBarMessage.emit("¡Bienvenido/a, ${found.nick}!")

                repository.getCommentsForParticipant(found.id).collect {
                    _portalComments.value = it
                }
            } else {
                _snackBarMessage.emit("No se encontró participante con: '$query'. Puedes registrarte si es tu primera vez.")
            }
        }
    }

    fun loginAsAdmin(password: String = "") {
        _authenticatedStaffName.value = "Administrador Central"
        _currentRole.value = UserRole.ADMIN
        _isLoggedIn.value = true
        viewModelScope.launch {
            _snackBarMessage.emit("Sesión iniciada: Panel de Administración")
        }
    }

    fun loginAsLaboratory(code: String = "") {
        _authenticatedStaffName.value = "Técnico Laboratorio"
        _currentRole.value = UserRole.LABORATORY
        _isLoggedIn.value = true
        viewModelScope.launch {
            _snackBarMessage.emit("Sesión iniciada: Panel de Laboratorio Clínico")
        }
    }

    fun quickLogin(role: UserRole, participantId: Int? = null) {
        when (role) {
            UserRole.ADMIN -> loginAsAdmin()
            UserRole.LABORATORY -> loginAsLaboratory()
            UserRole.PARTICIPANT -> {
                val target = if (participantId != null) {
                    allParticipants.value.find { it.id == participantId }
                } else {
                    allParticipants.value.firstOrNull()
                }
                if (target != null) {
                    loginAsParticipant(target.nick)
                } else {
                    viewModelScope.launch {
                        _snackBarMessage.emit("No hay participantes disponibles.")
                    }
                }
            }
        }
    }

    fun registerParticipant(
        nick: String,
        fullName: String,
        docType: String,
        docNumber: String,
        phone: String,
        email: String,
        telegram: String,
        birthDate: String,
        iban: String,
        referrer: String
    ) {
        viewModelScope.launch {
            val maxId = allParticipants.value.maxOfOrNull { it.id } ?: 100
            val newId = maxId + 1
            val newParticipant = ParticipantEntity(
                id = newId,
                nick = nick.trim(),
                fullName = fullName.trim(),
                docType = docType,
                docNumber = docNumber.trim().uppercase(),
                phone = phone.trim(),
                email = email.trim(),
                telegramUser = telegram.trim().removePrefix("@"),
                birthDate = birthDate.trim(),
                iban = iban.trim().uppercase(),
                referrer = referrer.ifBlank { "MIRCO" },
                balance = 0,
                monthlyParticipations = 0,
                totalParticipations = 0,
                status = "ACTIVO",
                testResult = "PENDIENTE",
                testDate = "",
                sampleNumber = "",
                renewalStatus = "No renovar",
                lastShootCode = "20263108",
                isConfirmedForShoot = false
            )

            repository.insertParticipant(newParticipant)
            _authenticatedParticipant.value = newParticipant
            _currentPortalParticipant.value = newParticipant
            _portalSearchInput.value = newParticipant.nick
            _currentRole.value = UserRole.PARTICIPANT
            _isLoggedIn.value = true
            _snackBarMessage.emit("¡Registro completado con éxito! Bienvenido/a a Bukkakery, ${newParticipant.nick}.")
        }
    }

    // --- FIREBASE AUTHENTICATION & ROLE MANAGEMENT ---
    val authState = repository.getAuthState()

    fun loginWithFirebase(email: String, pass: String) {
        viewModelScope.launch {
            val res = repository.signInWithFirebase(email, pass)
            if (res.isSuccess) {
                val profile = res.getOrNull()
                if (profile != null) {
                    when (profile.role) {
                        UserRole.ADMIN -> {
                            _authenticatedStaffName.value = profile.displayName.ifBlank { "Administrador (${profile.email})" }
                            _currentRole.value = UserRole.ADMIN
                            _isLoggedIn.value = true
                            _snackBarMessage.emit("🔑 Sesión iniciada como Administrador en Firebase Auth: ${profile.email}")
                        }
                        UserRole.LABORATORY -> {
                            _authenticatedStaffName.value = profile.displayName.ifBlank { "Técnico Laboratorio (${profile.email})" }
                            _currentRole.value = UserRole.LABORATORY
                            _isLoggedIn.value = true
                            _snackBarMessage.emit("🔬 Sesión iniciada como Laboratorio en Firebase Auth: ${profile.email}")
                        }
                        UserRole.PARTICIPANT -> {
                            // Find participant matching email or id
                            val list = allParticipants.value
                            val found = if (profile.participantId != null) {
                                list.find { it.id == profile.participantId }
                            } else {
                                list.find { it.email.equals(profile.email, ignoreCase = true) || it.nick.equals(profile.displayName, ignoreCase = true) }
                            } ?: list.firstOrNull()

                            if (found != null) {
                                _authenticatedParticipant.value = found
                                _currentPortalParticipant.value = found
                                _portalSearchInput.value = found.nick
                                _currentRole.value = UserRole.PARTICIPANT
                                _isLoggedIn.value = true
                                _snackBarMessage.emit("👤 Bienvenido a tu Portal Personal, ${found.nick} (${profile.email})")

                                repository.getCommentsForParticipant(found.id).collect {
                                    _portalComments.value = it
                                }
                            } else {
                                _authenticatedStaffName.value = profile.displayName
                                _currentRole.value = UserRole.PARTICIPANT
                                _isLoggedIn.value = true
                                _snackBarMessage.emit("👤 Sesión iniciada con Firebase Auth: ${profile.email}")
                            }
                        }
                    }
                }
            } else {
                // Graceful fallback to local role session so user is never locked out
                val lower = email.lowercase().trim()
                val inferredRole = when {
                    lower.contains("admin") -> UserRole.ADMIN
                    lower.contains("lab") -> UserRole.LABORATORY
                    else -> UserRole.PARTICIPANT
                }
                when (inferredRole) {
                    UserRole.ADMIN -> loginAsAdmin(pass)
                    UserRole.LABORATORY -> loginAsLaboratory(pass)
                    UserRole.PARTICIPANT -> {
                        val first = allParticipants.value.firstOrNull()
                        if (first != null) {
                            quickLogin(UserRole.PARTICIPANT, first.id)
                        } else {
                            loginAsParticipant(email, pass)
                        }
                    }
                }
                _snackBarMessage.emit("⚡ Acceso completado en modo local para ${email}")
            }
        }
    }

    fun createFirebaseAuthUser(
        email: String,
        pass: String,
        role: UserRole,
        participantId: Int? = null,
        displayName: String = ""
    ) {
        viewModelScope.launch {
            val res = repository.createFirebaseUser(
                email = email,
                pass = pass,
                role = role,
                participantId = participantId,
                displayName = displayName,
                currentAdminRole = currentRole.value
            )
            if (res.isSuccess) {
                _snackBarMessage.emit("✅ Cuenta Firebase Auth creada para ${email} (Rol: ${role.label})")
            } else {
                _snackBarMessage.emit("⚠️ Error al crear usuario en Firebase: ${res.exceptionOrNull()?.message}")
            }
        }
    }

    fun runMedicalExpirationsCloudAudit() {
        viewModelScope.launch {
            val list = allParticipants.value
            var count12 = 0
            var count7 = 0
            var count4 = 0
            var count2 = 0
            var countExpired = 0

            list.forEach { p ->
                val days = p.calculateDaysUntilExpiration("31/08/2026")
                if (days != null) {
                    when {
                        days <= 0 -> countExpired++
                        days <= 2 -> count2++
                        days <= 4 -> count4++
                        days <= 7 -> count7++
                        days <= 12 -> count12++
                    }
                }
            }

            val summaryMsg = "🔔 Cloud Functions Audit: ${list.size} analizadas | 12d: $count12 | 7d: $count7 | 4d: $count4 | 2d (Crítico): $count2 | Expiradas: $countExpired"
            _snackBarMessage.emit(summaryMsg)
        }
    }

    fun logout() {
        _isLoggedIn.value = false
        _authenticatedParticipant.value = null
        _authenticatedStaffName.value = ""
        repository.signOutFirebase()
        viewModelScope.launch {
            _snackBarMessage.emit("Sesión cerrada. Accede nuevamente con tus credenciales.")
        }
    }

    // Participant Portal Actions
    fun setPortalSearchInput(query: String) {
        _portalSearchInput.value = query
    }

    fun searchParticipantInPortal(query: String) {
        viewModelScope.launch {
            val found = repository.findParticipantByQuery(query.trim())
            _currentPortalParticipant.value = found
            if (found != null) {
                repository.getCommentsForParticipant(found.id).collect {
                    _portalComments.value = it
                }
            } else {
                _snackBarMessage.emit("No se encontró ningún participante con: $query")
            }
        }
    }

    fun addPortalComment(participantId: Int, authorRole: String, authorName: String, content: String) {
        viewModelScope.launch {
            repository.addComment(participantId, authorRole, authorName, content)
            _snackBarMessage.emit("Comentario enviado correctamente.")
            val found = repository.getParticipantByIdSync(participantId)
            if (found != null) {
                repository.getCommentsForParticipant(found.id).collect {
                    _portalComments.value = it
                }
            }
        }
    }

    // --- FIRESTORE CLOUD SYNCHRONIZATION ---
    val cloudSyncInfo = repository.getCloudSyncInfo()

    fun syncWithFirestoreCloud() {
        viewModelScope.launch {
            val role = currentRole.value
            val result = repository.syncAllWithFirestore(role)
            if (result.isSuccess) {
                _snackBarMessage.emit("☁️ ${result.getOrNull() ?: "Sincronizado con Firebase Firestore"}")
            } else {
                _snackBarMessage.emit("⚠️ Error Firestore: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    fun pullFromFirestoreCloud() {
        viewModelScope.launch {
            val result = repository.pullParticipantsFromFirestore()
            if (result.isSuccess) {
                _snackBarMessage.emit("☁️ ${result.getOrNull() ?: 0} registros actualizados desde Firebase Firestore.")
            } else {
                _snackBarMessage.emit("⚠️ Error al descargar de Firestore: ${result.exceptionOrNull()?.message}")
            }
        }
    }

    // Export Helpers
    fun getTelegramPreRodajeText(): String {
        val shoot = currentShoot.value ?: return ""
        val confirmed = allParticipants.value.filter { it.isConfirmedForShoot }
        return repository.generateTelegramPreRodajeMessage(shoot, confirmed, staffList.value)
    }

    fun getTelegramPostRodajeText(): String {
        val shoot = currentShoot.value ?: return ""
        val map = allParticipants.value.associateBy { it.id }
        return repository.generateTelegramPostRodajeMessage(shoot, attendances.value, map, staffList.value)
    }

    fun getContractText(participant: ParticipantEntity, cashDelivered: Int = 0): String {
        val shoot = currentShoot.value ?: return ""
        val remaining = (participant.balance - cashDelivered).coerceAtLeast(0)
        return repository.generateImageRightsContract(participant, shoot, cashDelivered, remaining)
    }

    fun getCsvExportText(): String {
        return repository.exportParticipantsCsv(allParticipants.value)
    }
}
