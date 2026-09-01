const functions = require("firebase-functions");
const admin = require("firebase-admin");
const axios = require("axios");

// Initialize Firebase Admin SDK
admin.initializeApp();
const db = admin.firestore();

// Configurable constants
const TEST_VALIDITY_DAYS = 30; // Standard serology validity in days
const ALERT_THRESHOLDS = [12, 7, 4, 2]; // Target alert milestones in days before expiration

// Optional external webhook / Telegram bot configuration (can be set via environment or secret manager)
const TELEGRAM_BOT_TOKEN = process.env.TELEGRAM_BOT_TOKEN || "";
const TELEGRAM_ADMIN_CHAT_ID = process.env.TELEGRAM_ADMIN_CHAT_ID || "";
const NOTIFICATION_WEBHOOK_URL = process.env.NOTIFICATION_WEBHOOK_URL || "";

/**
 * Parses a date string in various common formats (DD/MM/YYYY, DD/MM/YY, YYYY-MM-DD)
 * @param {string|number|Date} dateInput 
 * @returns {Date|null}
 */
function parseTestDate(dateInput) {
  if (!dateInput) return null;
  if (dateInput instanceof Date) return dateInput;
  if (typeof dateInput === "number") return new Date(dateInput);

  const str = String(dateInput).trim();
  if (str.toUpperCase().includes("PENDIENTE") || str === "") return null;

  // Format DD/MM/YYYY or DD/MM/YY
  const slashParts = str.split("/");
  if (slashParts.length === 3) {
    const day = parseInt(slashParts[0], 10);
    const month = parseInt(slashParts[1], 10) - 1;
    let year = parseInt(slashParts[2], 10);
    if (year < 100) year += 2000;
    const parsed = new Date(year, month, day);
    if (!isNaN(parsed.getTime())) return parsed;
  }

  // Format YYYY-MM-DD
  const dashParts = str.split("-");
  if (dashParts.length === 3) {
    const year = parseInt(dashParts[0], 10);
    const month = parseInt(dashParts[1], 10) - 1;
    const day = parseInt(dashParts[2], 10);
    const parsed = new Date(year, month, day);
    if (!isNaN(parsed.getTime())) return parsed;
  }

  const generic = new Date(str);
  return isNaN(generic.getTime()) ? null : generic;
}

/**
 * Calculates days remaining until expiration given a testDate.
 * @param {Date} testDate 
 * @param {Date} referenceDate 
 * @returns {{ expirationDate: Date, daysRemaining: number }}
 */
function calculateExpiration(testDate, referenceDate = new Date()) {
  const expirationDate = new Date(testDate.getTime() + TEST_VALIDITY_DAYS * 24 * 60 * 60 * 1000);
  
  // Set both to midnight for whole day comparison
  const refMidnight = new Date(referenceDate.getFullYear(), referenceDate.getMonth(), referenceDate.getDate());
  const expMidnight = new Date(expirationDate.getFullYear(), expirationDate.getMonth(), expirationDate.getDate());
  
  const diffMs = expMidnight.getTime() - refMidnight.getTime();
  const daysRemaining = Math.round(diffMs / (1000 * 60 * 60 * 24));

  return { expirationDate, daysRemaining };
}

/**
 * Formats a user-friendly notification message based on threshold
 * @param {object} participant 
 * @param {number} daysRemaining 
 * @param {Date} expirationDate 
 * @returns {{ title: String, body: String, urgency: String, tier: number }}
 */
function buildNotificationPayload(participant, daysRemaining, expirationDate) {
  const expStr = expirationDate.toLocaleDateString("es-ES", { day: "2-digit", month: "2-digit", year: "numeric" });
  const nick = participant.nick || participant.fullName || `Participante #${participant.id}`;

  if (daysRemaining === 12) {
    return {
      tier: 12,
      urgency: "INFORMATIVA",
      title: `📅 Recordatorio Analítica: Quedan 12 días (${nick})`,
      body: `Hola ${nick}, tu prueba médica serológica caduca el ${expStr}. Te quedan 12 días de validez para rodajes.`,
      action: "RENOVAR_PREVENTIVA"
    };
  } else if (daysRemaining === 7) {
    return {
      tier: 7,
      urgency: "MODERADA",
      title: `⚠️ Aviso 1 Semana: Analítica por caducar (${nick})`,
      body: `Atención ${nick}: Te quedan exactamente 7 días de validez médica (caduca el ${expStr}). Recuerda agendar tu cita en clínica.`,
      action: "AGENDAR_CITA"
    };
  } else if (daysRemaining === 4) {
    return {
      tier: 4,
      urgency: "ALTA",
      title: `🚨 Aviso Urgente: 4 días para caducidad (${nick})`,
      body: `¡Urgente ${nick}! Tu analítica médica caduca en 4 días (${expStr}). Para ser confirmado en próximos rodajes debes realizar la renovación ya.`,
      action: "RENOVACION_URGENTE"
    };
  } else if (daysRemaining === 2) {
    return {
      tier: 2,
      urgency: "CRÍTICA",
      title: `⛔ AVISO CRÍTICO: 2 DÍAS restantes (${nick})`,
      body: `CRÍTICO ${nick}: En 48 horas (el ${expStr}) tu estado pasará a EXPIRADO. No podrás participar en rodajes sin nuevo análisis negativo.`,
      action: "BLOQUEO_INMINENTE"
    };
  } else if (daysRemaining <= 0) {
    return {
      tier: 0,
      urgency: "EXPIRADO",
      title: `❌ Analítica Médica Expirada (${nick})`,
      body: `La prueba de ${nick} ha caducado el ${expStr}. El perfil queda en estado NO APTO hasta que se registre una nueva muestra.`,
      action: "EXPIRADO"
    };
  }

  return null;
}

/**
 * Dispatches notification through available channels:
 * 1. Cloud Firestore /notifications collection
 * 2. Telegram Bot API (if telegramUser or bot token available)
 * 3. External Webhook (if configured)
 */
async function dispatchNotification(participant, notification) {
  const timestamp = admin.firestore.FieldValue.serverTimestamp();

  // 1. Save in Firestore /notifications collection
  const notifRef = db.collection("notifications").doc();
  await notifRef.set({
    notificationId: notifRef.id,
    participantId: participant.id || null,
    nick: participant.nick || "",
    email: participant.email || "",
    phone: participant.phone || "",
    telegramUser: participant.telegramUser || "",
    title: notification.title,
    message: notification.body,
    urgency: notification.urgency,
    tier: notification.tier,
    action: notification.action,
    isRead: false,
    sentVia: ["FIRESTORE", participant.telegramUser ? "TELEGRAM" : "APP"],
    createdAt: timestamp
  });

  // 2. Telegram Dispatch if configured
  if (TELEGRAM_BOT_TOKEN && (participant.telegramUser || TELEGRAM_ADMIN_CHAT_ID)) {
    try {
      const chatId = participant.telegramUser || TELEGRAM_ADMIN_CHAT_ID;
      const tgText = `🔔 *${notification.title}*\n\n${notification.body}\n\n👤 *Participante:* ${participant.nick} (ID: ${participant.id})\n📱 *Tel:* ${participant.phone || "N/A"}\n📋 *Muestra:* ${participant.sampleNumber || "N/A"}`;
      
      await axios.post(`https://api.telegram.org/bot${TELEGRAM_BOT_TOKEN}/sendMessage`, {
        chat_id: chatId,
        text: tgText,
        parse_mode: "Markdown"
      });
    } catch (tgErr) {
      console.warn(`Error enviando notificación Telegram para ${participant.nick}:`, tgErr.message);
    }
  }

  // 3. Webhook Dispatch if configured
  if (NOTIFICATION_WEBHOOK_URL) {
    try {
      await axios.post(NOTIFICATION_WEBHOOK_URL, {
        event: "MEDICAL_EXPIRATION_ALERT",
        participant: {
          id: participant.id,
          nick: participant.nick,
          email: participant.email,
          phone: participant.phone,
          sampleNumber: participant.sampleNumber
        },
        notification
      });
    } catch (whErr) {
      console.warn("Error enviando webhook:", whErr.message);
    }
  }

  // 4. Update Participant document with notification metadata to prevent duplicates
  const partDocRef = db.collection("participants").document(String(participant.id));
  await partDocRef.set({
    lastNotifiedTier: notification.tier,
    lastNotificationDate: new Date().toISOString(),
    updatedAt: admin.firestore.FieldValue.serverTimestamp()
  }, { merge: true });
}

/**
 * Main verification process
 * @param {Date} [referenceDate] 
 */
async function processMedicalExpirations(referenceDate = new Date()) {
  const participantsSnapshot = await db.collection("participants").get();
  const summary = {
    totalScanned: participantsSnapshot.size,
    notifiedCount: 0,
    alertsByTier: {
      "12_days": 0,
      "7_days": 0,
      "4_days": 0,
      "2_days": 0,
      "expired": 0
    },
    details: []
  };

  for (const doc of participantsSnapshot.docs) {
    const data = doc.data();
    const testDate = parseTestDate(data.testDate);

    if (!testDate) {
      continue;
    }

    const { expirationDate, daysRemaining } = calculateExpiration(testDate, referenceDate);
    const lastTier = data.lastNotifiedTier;

    // Check if daysRemaining matches any target threshold (12, 7, 4, 2)
    let shouldNotify = false;
    let targetTier = null;

    if (ALERT_THRESHOLDS.includes(daysRemaining)) {
      // Only notify if we haven't already notified for this exact tier
      if (lastTier !== daysRemaining) {
        shouldNotify = true;
        targetTier = daysRemaining;
      }
    } else if (daysRemaining <= 0 && lastTier !== 0 && data.status !== "EXPIRADO") {
      // Also notify when it becomes expired
      shouldNotify = true;
      targetTier = 0;
    }

    if (shouldNotify && targetTier !== null) {
      const payload = buildNotificationPayload(data, daysRemaining, expirationDate);
      if (payload) {
        await dispatchNotification(data, payload);
        summary.notifiedCount++;
        
        if (targetTier === 12) summary.alertsByTier["12_days"]++;
        else if (targetTier === 7) summary.alertsByTier["7_days"]++;
        else if (targetTier === 4) summary.alertsByTier["4_days"]++;
        else if (targetTier === 2) summary.alertsByTier["2_days"]++;
        else if (targetTier === 0) summary.alertsByTier["expired"]++;

        summary.details.push({
          participantId: data.id,
          nick: data.nick,
          testDate: data.testDate,
          daysRemaining,
          tier: targetTier,
          urgency: payload.urgency
        });
      }
    }
  }

  // Record audit log entry in Firestore
  await db.collection("audit_logs").add({
    action: "CHECK_MEDICAL_EXPIRATIONS",
    summary,
    executedAt: admin.firestore.FieldValue.serverTimestamp()
  });

  return summary;
}

// -----------------------------------------------------------------------------
// 1. SCHEDULED CLOUD FUNCTION (Runs daily at 09:00 AM UTC)
// -----------------------------------------------------------------------------
exports.checkMedicalExpirationsScheduled = functions.pubsub
  .schedule("0 9 * * *")
  .timeZone("Europe/Madrid")
  .onRun(async (context) => {
    console.log("Iniciando verificación programada de caducidad de análisis médicos...");
    try {
      const result = await processMedicalExpirations();
      console.log("Verificación finalizada con éxito:", JSON.stringify(result));
      return result;
    } catch (error) {
      console.error("Error en verificación programada:", error);
      throw error;
    }
  });

// -----------------------------------------------------------------------------
// 2. HTTP CALLABLE / ON-REQUEST FUNCTION (For manual or app-triggered verification)
// -----------------------------------------------------------------------------
exports.checkMedicalExpirationsHttp = functions.https.onRequest(async (req, res) => {
  // Enable CORS
  res.set("Access-Control-Allow-Origin", "*");
  res.set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
  res.set("Access-Control-Allow-Headers", "Content-Type, Authorization");

  if (req.method === "OPTIONS") {
    return res.status(204).send("");
  }

  try {
    const customRefDateStr = req.query.referenceDate || req.body.referenceDate;
    const refDate = customRefDateStr ? new Date(customRefDateStr) : new Date();

    const result = await processMedicalExpirations(refDate);
    return res.status(200).json({
      success: true,
      message: "Verificación de caducidades médicas completada.",
      timestamp: new Date().toISOString(),
      result
    });
  } catch (error) {
    console.error("Error en endpoint HTTP:", error);
    return res.status(500).json({
      success: false,
      error: error.message
    });
  }
});
