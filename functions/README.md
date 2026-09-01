# Firebase Cloud Functions: Verificador de Caducidades Médicas 🏥

Esta función en la nube automatiza la auditoría de vigencia de las analíticas serológicas (validez estándar de 30 días) y envía notificaciones preventivas y escalonadas antes de que expire la prueba médica de cada participante.

---

## ⏰ Calendario de Notificaciones Escalonadas

El script evalúa diariamente el número de días restantes hasta la fecha límite (`fecha_test + 30 días`):

| Días Restantes | Nivel de Alerta | Mensaje / Acción | Canal de Envío |
| :--- | :--- | :--- | :--- |
| **12 días** | ℹ️ Informativa | Recordatorio preventivo de caducidad en 12 días | Firestore `/notifications`, Telegram, Webhook |
| **7 días** | ⚠️ Moderada | Aviso 1 semana: recordar solicitar cita en clínica | Firestore `/notifications`, Telegram, Webhook |
| **4 días** | 🚨 Alta | Alerta urgente: 4 días para perder aptitud de rodaje | Firestore `/notifications`, Telegram, Webhook |
| **2 días** | ⛔ Crítica | Aviso crítico: 48h para bloqueo inminente | Firestore `/notifications`, Telegram, Webhook |
| **0 días** | ❌ Expirado | Notificación de bloqueo de pase hasta nueva analítica | Firestore `/notifications`, Telegram, Webhook |

---

## 🛠️ Funciones Implementadas (`functions/index.js`)

1. **`checkMedicalExpirationsScheduled`**
   - **Trigger**: Cron diario (`0 9 * * *` a las 09:00 AM hora Europa/Madrid).
   - **Proceso**: Recorre la colección `/participants` en Firestore, calcula la diferencia de días y envía alertas a quienes cumplan el criterio de 12, 7, 4 o 2 días.
   - **Antispam**: Registra `lastNotifiedTier` en el documento del participante para evitar notificaciones duplicadas en el mismo tramo.

2. **`checkMedicalExpirationsHttp`**
   - **Trigger**: Petición HTTP (`GET` o `POST`).
   - **Uso**: Permite disparar la verificación manualmente desde el panel de Administración de la app o un webhook externo.
   - Permite pasar un parámetro `referenceDate` opcional para realizar simulaciones o auditorías retrospectivas.

---

## 🚀 Despliegue en Firebase

Para desplegar las Cloud Functions en tu proyecto de Firebase:

```bash
# 1. Instalar dependencias de functions
cd functions
npm install

# 2. Iniciar sesión en Firebase CLI (si no lo has hecho)
firebase login

# 3. Desplegar solo funciones
firebase deploy --only functions
```

### Configuración de Variables de Entorno (Opcional)
```bash
firebase functions:config:set telegram.bot_token="TU_TELEGRAM_BOT_TOKEN" telegram.admin_chat_id="TU_CHAT_ID"
```
