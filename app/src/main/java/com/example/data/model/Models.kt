package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

enum class UserRole(val label: String, val description: String) {
    ADMIN("Administrador", "Control total del sistema, rodajes, usuarios y reportes"),
    LABORATORY("Laboratorio", "Actualización de analíticas, muestras y fechas"),
    PARTICIPANT("Visitante / Participante", "Consulta de saldo, estado de analítica y comentarios")
}

enum class TestAlertLevel(val label: String, val colorHex: Long) {
    VALID("Válido", 0xFF00C875),               // > 12 days left
    EXPIRING_12("Expira en ≤12d", 0xFFFDAB3D), // <= 12 days left
    EXPIRING_7("Expira en ≤7d", 0xFFFF8533),   // <= 7 days left
    EXPIRING_4("Expira en ≤4d", 0xFFFF5522),   // <= 4 days left
    EXPIRING_2("CRÍTICO (≤2d)", 0xFFE2445C),   // <= 2 days left
    EXPIRED("EXPIRADO", 0xFF990022),           // <= 0 days left
    PENDING("PENDIENTE", 0xFF787F8D)           // Sin fecha o pendiente
}

@Entity(tableName = "participants")
data class ParticipantEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val docType: String = "DNI",
    val docNumber: String = "",
    val sampleNumber: String = "",
    val testDate: String = "",
    val testResult: String = "NEGATIVO",
    val status: String = "ACTIVO",
    val nick: String = "",
    val fullName: String = "",
    val phone: String = "",
    val iban: String = "",
    val referrer: String = "MIRCO",
    val email: String = "",
    val telegramUser: String = "",
    val birthDate: String = "",
    val renewalStatus: String = "No renovar",
    val balance: Int = 0,
    val monthlyParticipations: Int = 0,
    val totalParticipations: Int = 0,
    val address: String = "",
    val comments: String = "",
    val lastShootCode: String = "20263108",
    val isConfirmedForShoot: Boolean = false
) {
    val isBanned: Boolean
        get() = status.contains("BANEADO", ignoreCase = true) || testResult.contains("POSITIV", ignoreCase = true) || testResult.contains("POSCLAM", ignoreCase = true)

    val qualifiesForFreeRenewal: Boolean
        get() = totalParticipations >= 5 || renewalStatus.contains("gratis", ignoreCase = true)

    fun calculateDaysUntilExpiration(referenceDateStr: String = "31/08/2026"): Int? {
        if (testDate.isBlank() || testDate.contains("PENDIENTE", ignoreCase = true)) return null
        return try {
            val format1 = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val format2 = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
            val format3 = SimpleDateFormat("dd/M/yy", Locale.getDefault())

            val cleanDate = testDate.trim()
            val parsedDate = try {
                format1.parse(cleanDate)
            } catch (e: Exception) {
                try {
                    format2.parse(cleanDate)
                } catch (e2: Exception) {
                    format3.parse(cleanDate)
                }
            } ?: return null

            val refDate = try {
                format1.parse(referenceDateStr) ?: Date()
            } catch (e: Exception) {
                Date()
            }

            // Expiration is 30 days from testDate
            val expireTime = parsedDate.time + TimeUnit.DAYS.toMillis(30)
            val diffMillis = expireTime - refDate.time
            val daysLeft = TimeUnit.MILLISECONDS.toDays(diffMillis).toInt()
            daysLeft
        } catch (e: Exception) {
            null
        }
    }

    fun getTestAlertLevel(referenceDateStr: String = "31/08/2026"): TestAlertLevel {
        if (testDate.isBlank() || testDate.contains("PENDIENTE", ignoreCase = true) || testResult.contains("PENDIENTE", ignoreCase = true)) {
            return TestAlertLevel.PENDING
        }
        val days = calculateDaysUntilExpiration(referenceDateStr) ?: return TestAlertLevel.PENDING
        return when {
            days <= 0 -> TestAlertLevel.EXPIRED
            days <= 2 -> TestAlertLevel.EXPIRING_2
            days <= 4 -> TestAlertLevel.EXPIRING_4
            days <= 7 -> TestAlertLevel.EXPIRING_7
            days <= 12 -> TestAlertLevel.EXPIRING_12
            else -> TestAlertLevel.VALID
        }
    }
}

@Entity(tableName = "test_records")
data class TestRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val participantId: Int,
    val sampleNumber: String,
    val testDate: String,
    val result: String,
    val periodLabel: String = "",
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "shoot_events")
data class ShootEventEntity(
    @PrimaryKey
    val shootCode: String = "20263108", // e.g. 20263108
    val date: String = "31/08/2026",
    val time: String = "18:00",
    val title: String = "Rodaje BUKKAKERY - 31/08/2026",
    val actressName: String = "Kitty Love",
    val actressFee: Double = 2000.0,
    val actressPaymentMethod: String = "Transferencia",
    val actressIsPaid: Boolean = false,
    val staffBudget: Double = 480.0,
    val expensesNotes: String = "Scooper (150), Maquillaje (60), Seguridad/Limpieza (70), Asistente (70), Cámara (10/50), Fotógrafo (100+100 deuda sábado), Comida (80), Emergencias (140)",
    val status: String = "EN_RODAJE", // PRERODAJE, EN_RODAJE, FINALIZADO
    val notes: String = ""
)

@Entity(tableName = "staff_members")
data class StaffEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val shootCode: String,
    val role: String,
    val name: String,
    val fee: Double,
    val isPaid: Boolean = false,
    val notes: String = ""
)

@Entity(tableName = "shoot_attendances")
data class AttendanceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val shootCode: String,
    val participantId: Int,
    val lockerNumber: Int? = null, // Casillero asignado (1..90)
    val isPresent: Boolean = false,
    val contractSigned: Boolean = false,
    val contractSignedAt: Long? = null,
    val initialBalance: Int = 0,      // Balance que traía
    val cashDeliveredAtEntry: Int = 0,// Dinero entregado en mano en recepción
    val remainingBalance: Int = 0,    // Balance ajustado tras la entrada
    val shotsCount: Int = 0,          // Veces que participó en este rodaje
    val bonusPrizes: Int = 0,         // Premios acumulados en € (10 o 20)
    val prizeReason: String = ""
)

@Entity(tableName = "shot_logs")
data class ShotLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val shootCode: String,
    val lockerNumber: Int,
    val participantId: Int,
    val bonus: Int = 0, // 0, 10, 20
    val bonusReason: String = "",
    val timestamp: Long = System.currentTimeMillis(),
    val orderIndex: Int = 0
)

@Entity(tableName = "comments")
data class CommentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val participantId: Int,
    val authorRole: String = "PARTICIPANT", // PARTICIPANT, ADMIN, LAB
    val authorName: String = "",
    val content: String,
    val timestamp: Long = System.currentTimeMillis()
)
