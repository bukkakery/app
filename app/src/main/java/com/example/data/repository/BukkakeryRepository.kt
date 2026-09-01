package com.example.data.repository

import android.content.Context
import androidx.room.Room
import androidx.sqlite.db.SimpleSQLiteQuery
import com.example.data.db.AppDatabase
import com.example.data.db.InitialDataProvider
import com.example.data.firebase.FirestoreManager
import com.example.data.model.AttendanceEntity
import com.example.data.model.CommentEntity
import com.example.data.model.ParticipantEntity
import com.example.data.model.ShootEventEntity
import com.example.data.model.ShotLogEntity
import com.example.data.model.StaffEntity
import com.example.data.model.TestRecordEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.withContext

class BukkakeryRepository private constructor(context: Context) {

    private val db: AppDatabase = Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java,
        "bukkakery_database.db"
    ).fallbackToDestructiveMigration().build()

    private val participantDao = db.participantDao()
    private val shootDao = db.shootDao()
    private val staffDao = db.staffDao()
    private val attendanceDao = db.attendanceDao()
    private val shotLogDao = db.shotLogDao()
    private val testRecordDao = db.testRecordDao()
    private val commentDao = db.commentDao()

    companion object {
        @Volatile
        private var INSTANCE: BukkakeryRepository? = null

        fun getInstance(context: Context): BukkakeryRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = BukkakeryRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }

    suspend fun initializeDatabaseIfEmpty() = withContext(Dispatchers.IO) {
        val count = participantDao.getCount()
        if (count == 0) {
            participantDao.insertParticipants(InitialDataProvider.getInitialParticipants())
            shootDao.insertShoot(InitialDataProvider.getInitialShoot())
            staffDao.insertStaffList(InitialDataProvider.getInitialStaff("20263108"))
            testRecordDao.insertRecords(InitialDataProvider.getInitialTestRecords())
            attendanceDao.insertAttendanceList(InitialDataProvider.getInitialAttendances("20263108"))
        }
    }

    // --- Participants ---
    fun getAllParticipants(): Flow<List<ParticipantEntity>> = participantDao.getAllParticipants()

    fun getParticipantById(id: Int): Flow<ParticipantEntity?> = participantDao.getParticipantById(id)

    suspend fun getParticipantByIdSync(id: Int): ParticipantEntity? = withContext(Dispatchers.IO) {
        participantDao.getParticipantByIdSync(id)
    }

    suspend fun findParticipantByQuery(query: String): ParticipantEntity? = withContext(Dispatchers.IO) {
        participantDao.findParticipantByQuery(query, query, "%$query%")
    }

    fun searchParticipants(query: String): Flow<List<ParticipantEntity>> {
        return if (query.isBlank()) {
            participantDao.getAllParticipants()
        } else {
            participantDao.searchParticipants(query)
        }
    }

    suspend fun insertParticipant(participant: ParticipantEntity) = withContext(Dispatchers.IO) {
        // Auto-check renewal condition (>5 events)
        val updated = if (participant.totalParticipations >= 5 && participant.renewalStatus.contains("No", ignoreCase = true)) {
            participant.copy(renewalStatus = "Renovar gratis")
        } else {
            participant
        }
        participantDao.insertParticipant(updated)
    }

    suspend fun updateParticipant(participant: ParticipantEntity) = withContext(Dispatchers.IO) {
        val updated = if (participant.totalParticipations >= 5 && participant.renewalStatus.contains("No", ignoreCase = true)) {
            participant.copy(renewalStatus = "Renovar gratis")
        } else {
            participant
        }
        participantDao.updateParticipant(updated)
    }

    suspend fun deleteParticipant(participant: ParticipantEntity) = withContext(Dispatchers.IO) {
        participantDao.deleteParticipant(participant)
    }

    suspend fun updateParticipantTest(id: Int, sample: String, testDate: String, result: String) = withContext(Dispatchers.IO) {
        participantDao.updateTestInfo(id, sample, testDate, result)
        testRecordDao.insertRecord(
            TestRecordEntity(
                participantId = id,
                sampleNumber = sample,
                testDate = testDate,
                result = result,
                periodLabel = "Actualizado",
                notes = "Actualizado por laboratorio"
            )
        )
    }

    suspend fun updateParticipantBalance(id: Int, newBalance: Int) = withContext(Dispatchers.IO) {
        participantDao.updateBalance(id, newBalance)
    }

    suspend fun toggleShootConfirmation(id: Int, isConfirmed: Boolean) = withContext(Dispatchers.IO) {
        participantDao.updateConfirmation(id, isConfirmed)
    }

    fun executeCustomSqlQuery(queryStr: String): Flow<List<ParticipantEntity>> {
        val safeQuery = if (queryStr.isBlank() || !queryStr.trim().startsWith("SELECT", ignoreCase = true)) {
            "SELECT * FROM participants ORDER BY id ASC"
        } else {
            queryStr
        }
        return participantDao.rawQueryParticipants(SimpleSQLiteQuery(safeQuery))
    }

    // --- Shoots & Staff ---
    fun getCurrentShoot(shootCode: String = "20263108"): Flow<ShootEventEntity?> = shootDao.getShootByCode(shootCode)

    suspend fun updateShoot(shoot: ShootEventEntity) = withContext(Dispatchers.IO) {
        shootDao.updateShoot(shoot)
    }

    fun getStaffForShoot(shootCode: String = "20263108"): Flow<List<StaffEntity>> = staffDao.getStaffForShoot(shootCode)

    suspend fun updateStaff(staff: StaffEntity) = withContext(Dispatchers.IO) {
        staffDao.updateStaff(staff)
    }

    suspend fun insertStaff(staff: StaffEntity) = withContext(Dispatchers.IO) {
        staffDao.insertStaff(staff)
    }

    // --- Access Control & Attendances ---
    fun getAttendances(shootCode: String = "20263108"): Flow<List<AttendanceEntity>> = attendanceDao.getAttendanceForShoot(shootCode)

    suspend fun checkInParticipant(
        shootCode: String,
        participantId: Int,
        lockerNumber: Int,
        cashDeliveredAtEntry: Int,
        contractSigned: Boolean
    ) = withContext(Dispatchers.IO) {
        val participant = participantDao.getParticipantByIdSync(participantId)
        val initialBal = participant?.balance ?: 0
        val remainingBal = (initialBal - cashDeliveredAtEntry).coerceAtLeast(0)

        val existing = attendanceDao.getAttendanceSync(shootCode, participantId)
        val attendance = if (existing != null) {
            existing.copy(
                lockerNumber = lockerNumber,
                isPresent = true,
                contractSigned = contractSigned,
                contractSignedAt = if (contractSigned) System.currentTimeMillis() else existing.contractSignedAt,
                initialBalance = initialBal,
                cashDeliveredAtEntry = cashDeliveredAtEntry,
                remainingBalance = remainingBal
            )
        } else {
            AttendanceEntity(
                shootCode = shootCode,
                participantId = participantId,
                lockerNumber = lockerNumber,
                isPresent = true,
                contractSigned = contractSigned,
                contractSignedAt = if (contractSigned) System.currentTimeMillis() else null,
                initialBalance = initialBal,
                cashDeliveredAtEntry = cashDeliveredAtEntry,
                remainingBalance = remainingBal,
                shotsCount = 0,
                bonusPrizes = 0
            )
        }
        attendanceDao.insertAttendance(attendance)

        // Update participant balance
        participantDao.updateBalance(participantId, remainingBal)
    }

    // --- Live Rodaje: Corridas y Premios ---
    suspend fun recordShot(
        shootCode: String,
        lockerNumber: Int,
        bonus: Int = 0,
        bonusReason: String = ""
    ): Result<String> = withContext(Dispatchers.IO) {
        val attendance = attendanceDao.getAttendanceByLockerSync(shootCode, lockerNumber)
            ?: return@withContext Result.failure(Exception("El casillero #$lockerNumber no está asignado a ningún participante en este rodaje."))

        // Check maximum 3 prizes total rule
        if (bonus > 0) {
            val currentPrizesCount = shotLogDao.getPrizesCountForShoot(shootCode)
            if (currentPrizesCount >= 3) {
                return@withContext Result.failure(Exception("Límite alcanzado: Ya se han otorgado los 3 premios máximos permitidos para este rodaje."))
            }
        }

        // Insert shot log
        shotLogDao.insertLog(
            ShotLogEntity(
                shootCode = shootCode,
                lockerNumber = lockerNumber,
                participantId = attendance.participantId,
                bonus = bonus,
                bonusReason = bonusReason,
                timestamp = System.currentTimeMillis()
            )
        )

        // Update attendance shot counts & prizes
        val newCount = attendance.shotsCount + 1
        val newBonusTotal = attendance.bonusPrizes + bonus
        attendanceDao.insertAttendance(
            attendance.copy(
                shotsCount = newCount,
                bonusPrizes = newBonusTotal,
                prizeReason = if (bonus > 0) bonusReason else attendance.prizeReason
            )
        )

        // Calculate and increment participant balance: +20€ per shot + bonus
        val participant = participantDao.getParticipantByIdSync(attendance.participantId)
        if (participant != null) {
            val additionalMoney = 20 + bonus
            val updatedBalance = participant.balance + additionalMoney
            val updatedTotalParticipations = participant.totalParticipations + 1
            val updatedMonthly = participant.monthlyParticipations + 1
            val qualifies = updatedTotalParticipations >= 5
            val updatedRenewal = if (qualifies) "Renovar gratis" else participant.renewalStatus

            participantDao.updateParticipant(
                participant.copy(
                    balance = updatedBalance,
                    totalParticipations = updatedTotalParticipations,
                    monthlyParticipations = updatedMonthly,
                    renewalStatus = updatedRenewal
                )
            )
        }

        Result.success("Corrida registrada para Casillero #$lockerNumber (${participant?.nick ?: ""}) +${20 + bonus}€")
    }

    fun getShotLogs(shootCode: String = "20263108"): Flow<List<ShotLogEntity>> = shotLogDao.getLogsForShoot(shootCode)

    suspend fun deleteShotLog(logId: Int, shootCode: String, lockerNumber: Int, bonus: Int) = withContext(Dispatchers.IO) {
        shotLogDao.deleteLog(logId)
        val attendance = attendanceDao.getAttendanceByLockerSync(shootCode, lockerNumber)
        if (attendance != null) {
            val updatedCount = (attendance.shotsCount - 1).coerceAtLeast(0)
            val updatedBonus = (attendance.bonusPrizes - bonus).coerceAtLeast(0)
            attendanceDao.insertAttendance(attendance.copy(shotsCount = updatedCount, bonusPrizes = updatedBonus))

            val participant = participantDao.getParticipantByIdSync(attendance.participantId)
            if (participant != null) {
                val toDeduct = 20 + bonus
                val newBal = (participant.balance - toDeduct).coerceAtLeast(0)
                participantDao.updateBalance(participant.id, newBal)
            }
        }
    }

    // --- Test Records & Comments ---
    fun getTestRecordsForParticipant(participantId: Int): Flow<List<TestRecordEntity>> = testRecordDao.getRecordsForParticipant(participantId)

    fun getCommentsForParticipant(participantId: Int): Flow<List<CommentEntity>> = commentDao.getCommentsForParticipant(participantId)

    suspend fun addComment(participantId: Int, authorRole: String, authorName: String, content: String) = withContext(Dispatchers.IO) {
        commentDao.insertComment(
            CommentEntity(
                participantId = participantId,
                authorRole = authorRole,
                authorName = authorName,
                content = content
            )
        )
    }

    // --- Export & Generators ---
    fun generateTelegramPreRodajeMessage(shoot: ShootEventEntity, confirmedParticipants: List<ParticipantEntity>, staff: List<StaffEntity>): String {
        val totalBukkakeroCash = confirmedParticipants.sumOf { it.balance }
        val staffCash = staff.filter { !it.isPaid }.sumOf { it.fee }
        val totalCashToWithdraw = totalBukkakeroCash + staffCash

        val sb = StringBuilder()
        sb.append("🎬 **BUKKAKERY - PREVISIÓN DE RODAJE** 🎬\n")
        sb.append("📅 Fecha: ${shoot.date} | ⏰ Hora: ${shoot.time}\n")
        sb.append("🆔 Código de Rodaje: `${shoot.shootCode}`\n")
        sb.append("⭐ Actriz: ${shoot.actressName} (${shoot.actressFee.toInt()}€ vía ${shoot.actressPaymentMethod})\n\n")

        sb.append("👥 **ASISTENTES CONFIRMADOS (${confirmedParticipants.size})**:\n")
        confirmedParticipants.forEachIndexed { i, p ->
            val alert = p.getTestAlertLevel(shoot.date)
            val alertEmoji = if (alert == com.example.data.model.TestAlertLevel.VALID) "✅" else "⚠️"
            sb.append("${i + 1}. @${p.telegramUser.ifBlank { p.nick }} (${p.nick}) - Balance: ${p.balance}€ $alertEmoji\n")
        }

        sb.append("\n💼 **STAFF TÉCNICO**:\n")
        staff.forEach { s ->
            sb.append("• ${s.role}: ${s.fee.toInt()}€ ${if (s.isPaid) "✅ Pagado" else "⏳ Pendiente"}\n")
        }

        sb.append("\n💰 **RESUMEN DE CAJA Y EFECTIVO**:\n")
        sb.append("💵 Efectivo Bukkakeros: ${totalBukkakeroCash}€\n")
        sb.append("💵 Efectivo Staff en mano: ${staffCash.toInt()}€\n")
        sb.append("🏦 Actriz (Transferencia): ${shoot.actressFee.toInt()}€\n")
        sb.append("🔥 **TOTAL EFECTIVO A RETIRAR: ${totalCashToWithdraw.toInt()}€**\n")
        return sb.toString()
    }

    fun generateTelegramPostRodajeMessage(
        shoot: ShootEventEntity,
        attendances: List<AttendanceEntity>,
        participantsMap: Map<Int, ParticipantEntity>,
        staff: List<StaffEntity>
    ): String {
        val totalShots = attendances.sumOf { it.shotsCount }
        val totalPrizes = attendances.sumOf { it.bonusPrizes }
        val totalEarnedByParticipants = (totalShots * 20) + totalPrizes
        val avgPerShot = if (totalShots > 0) String.format("%.2f", totalEarnedByParticipants.toDouble() / totalShots) else "20.00"
        val prizeWinners = attendances.filter { it.bonusPrizes > 0 }

        val sb = StringBuilder()
        sb.append("🏁 **BUKKAKERY - BALANCE FINAL DE RODAJE** 🏁\n")
        sb.append("📅 Fecha: ${shoot.date} | Código: `${shoot.shootCode}`\n")
        sb.append("⭐ Actriz: ${shoot.actressName} (${shoot.actressFee.toInt()}€ - ${if (shoot.actressIsPaid) "PAGADO" else "PENDIENTE"})\n\n")

        sb.append("📊 **MÉTRICAS CLAVE**:\n")
        sb.append("• Asistentes en rodaje: ${attendances.count { it.isPresent }}\n")
        sb.append("• Total de Corridas: $totalShots\n")
        sb.append("• Media generada por corrida: $avgPerShot €\n")
        sb.append("• Premios entregados (${prizeWinners.size}/3 máx): ${totalPrizes}€\n")

        if (prizeWinners.isNotEmpty()) {
            sb.append("\n🏆 **PREMIOS PERFORMANCE**:\n")
            prizeWinners.forEach { pw ->
                val p = participantsMap[pw.participantId]
                sb.append("⭐ Taquilla #${pw.lockerNumber} (${p?.nick ?: "N/A"}): +${pw.bonusPrizes}€ - ${pw.prizeReason.ifBlank { "Destacado" }}\n")
            }
        }

        sb.append("\n📋 **RESUMEN POR TAQUILLA**:\n")
        attendances.sortedBy { it.lockerNumber }.forEach { att ->
            val p = participantsMap[att.participantId]
            sb.append("T#${att.lockerNumber} ${p?.nick ?: "N/A"}: ${att.shotsCount} corridas (${att.shotsCount * 20}€) ${if (att.bonusPrizes > 0) "+${att.bonusPrizes}€ premio" else ""}\n")
        }

        sb.append("\n✅ Todo registrado y sincronizado en base de datos.")
        return sb.toString()
    }

    fun generateImageRightsContract(participant: ParticipantEntity, shoot: ShootEventEntity, cashDelivered: Int, remainingBalance: Int): String {
        return """
===================================================================
CONTRATO DE CESIÓN DE DERECHOS DE IMAGEN Y DECLARACIÓN DE SALUD
PRODUCCIÓN AUDIOVISUAL: BUKKAKERY
CÓDIGO DE RODAJE: ${shoot.shootCode} | FECHA: ${shoot.date}
===================================================================

REUNIDOS:
De una parte, LA DIRECCIÓN DE PRODUCCIÓN DE BUKKAKERY.
De otra parte, EL PARTICIPANTE:
• Nombre Completo: ${participant.fullName.ifBlank { participant.nick }}
• DNI / NIE / Pasaporte: ${participant.docType} ${participant.docNumber}
• Nick Artístico: ${participant.nick}
• Teléfono: ${participant.phone}
• Telegram: @${participant.telegramUser.ifBlank { "N/A" }}
• Fecha de Nacimiento: ${participant.birthDate.ifBlank { "Mayor de edad (+18)" }}

DECLARACIONES Y CLÁUSULAS:

1. MAYORÍA DE EDAD Y CAPACIDAD:
El participante declara bajo juramento ser mayor de dieciocho (18) años de edad y poseer plena capacidad civil y jurídica para obligarse mediante el presente acuerdo.

2. APTITUD SANITARIA Y ANALÍTICA MÉDICA:
El participante declara que se encuentra en perfecto estado de salud, habiéndose sometido a los análisis médicos y serológicos preceptivos (Muestra Nº ${participant.sampleNumber.ifBlank { "REGISTRADA" }}, Fecha de Test: ${participant.testDate.ifBlank { shoot.date }}, Resultado: ${participant.testResult}), no padeciendo ninguna enfermedad infectocontagiosa de transmisión sexual.

3. CESIÓN DE DERECHOS DE IMAGEN:
El participante cede de forma expresa, exclusiva y sin limitación temporal ni territorial los derechos sobre su imagen, voz y actuaciones grabadas en el rodaje '${shoot.shootCode}' a favor de BUKKAKERY para su fijación, reproducción, distribución, comunicación pública y comercialización en cualquier formato y plataforma.

4. LIQUIDACIÓN ECONÓMICA Y BALANCE DE CUENTA:
En el momento del acceso al recinto:
• Balance previo acumulado: ${participant.balance} €
• Efectivo entregado en mano en recepción: $cashDelivered €
• Balance remanente registrado en cuenta: $remainingBalance €
• Compensación por participación en rodaje: Veinte euros (20,00 €) por intervención completada más eventuales suplementos de performance autorizados (máximo 3 por sesión).

5. ASIGNACIÓN DE TAQUILLA Y CONFIDENCIALIDAD:
El participante asume la custodia de la llave y número de taquilla asignado para el rodaje, comprometiéndose a guardar la debida reserva sobre las instalaciones y participantes.

En prueba de conformidad con todas las cláusulas, firma digitalmente:

Firma del Participante: [FIRMADO DIGITALMENTE Y VALIDADO EN CONTROL DE ACCESO]
Fecha y Hora de Validación: ${shoot.date} - ${shoot.time}
Estado de Aprobación: ACEPTADO Y VERIFICADO
===================================================================
        """.trimIndent()
    }

    val firestoreManager = FirestoreManager()
    val firebaseAuthManager = com.example.data.firebase.FirebaseAuthManager()

    fun getCloudSyncInfo() = firestoreManager.syncInfo
    fun getAuthState() = firebaseAuthManager.authState

    suspend fun signInWithFirebase(email: String, pass: String) =
        firebaseAuthManager.signInWithEmail(email, pass)

    suspend fun createFirebaseUser(
        email: String,
        pass: String,
        role: com.example.data.model.UserRole,
        participantId: Int? = null,
        displayName: String = "",
        currentAdminRole: com.example.data.model.UserRole
    ) = firebaseAuthManager.createUserWithRole(
        email = email,
        pass = pass,
        role = role,
        participantId = participantId,
        displayName = displayName,
        currentAdminRole = currentAdminRole
    )

    fun signOutFirebase() = firebaseAuthManager.signOut()

    suspend fun syncAllWithFirestore(currentRole: com.example.data.model.UserRole): Result<String> = withContext(Dispatchers.IO) {
        val participants = participantDao.getAllParticipantsSync()
        val shoot = shootDao.getShootByCodeSync("20263108") ?: InitialDataProvider.getInitialShoot()
        val attendances = attendanceDao.getAttendanceForShootSync("20263108")
        val shotLogs = shotLogDao.getLogsForShootSync("20263108")

        firestoreManager.syncAllToFirestore(
            participants = participants,
            shoot = shoot,
            attendances = attendances,
            shotLogs = shotLogs,
            currentRole = currentRole
        )
    }

    suspend fun pullParticipantsFromFirestore(): Result<Int> = withContext(Dispatchers.IO) {
        val res = firestoreManager.fetchParticipants()
        if (res.isSuccess) {
            val list = res.getOrNull() ?: emptyList()
            if (list.isNotEmpty()) {
                participantDao.insertParticipants(list)
            }
            Result.success(list.size)
        } else {
            Result.failure(res.exceptionOrNull() ?: Exception("Error al descargar participantes de Firestore"))
        }
    }

    fun exportParticipantsCsv(participants: List<ParticipantEntity>): String {
        val sb = StringBuilder()
        sb.append("ID,DNI/NIE/PAS,Nº identificación,Muestra,Fecha TEST,NICK,RESULTADO,ESTADO,NOMBRE,Telefono,iban,Referido,Email,Telegram,f.nacimiento,RENOVAR ANALITICAS,Balance,CODIGO RODAJE,FECHA RODAJE,HORA\n")
        participants.forEach { p ->
            sb.append("${p.id},${p.docType},${p.docNumber},${p.sampleNumber},${p.testDate},${p.nick},${p.testResult},${p.status},\"${p.fullName}\",${p.phone},${p.iban},${p.referrer},${p.email},${p.telegramUser},${p.birthDate},${p.renewalStatus},${p.balance},${p.lastShootCode},31/08/2026,18:00\n")
        }
        return sb.toString()
    }
}
