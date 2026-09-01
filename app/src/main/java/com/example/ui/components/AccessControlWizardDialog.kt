package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.ReportProblem
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AttendanceEntity
import com.example.data.model.ParticipantEntity
import com.example.data.model.ShootEventEntity
import com.example.data.model.TestAlertLevel
import com.example.ui.theme.BrandCoral
import com.example.ui.theme.BrandCoralDark
import com.example.ui.theme.MondayBlue
import com.example.ui.theme.MondayBlueLight
import com.example.ui.theme.MondayDarkGrey
import com.example.ui.theme.MondayGreen
import com.example.ui.theme.MondayGreenLight
import com.example.ui.theme.MondayGrey
import com.example.ui.theme.MondayGreyLight
import com.example.ui.theme.MondayOrange
import com.example.ui.theme.MondayOrangeLight
import com.example.ui.theme.MondayPurple
import com.example.ui.theme.MondayPurpleLight
import com.example.ui.theme.MondayRed
import com.example.ui.theme.MondayRedLight
import com.example.ui.theme.MondayYellow

@Composable
fun AccessControlWizardDialog(
    participant: ParticipantEntity,
    shoot: ShootEventEntity?,
    attendances: List<AttendanceEntity>,
    contractText: String,
    onCompleteCheckIn: (cashDelivered: Int, lockerNumber: Int, contractSigned: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    var currentStep by remember { mutableStateOf(1) } // 1: Cobro, 2: Contrato, 3: Analítica, 4: Taquilla

    // Step 1 State: Cash & Balance
    var paymentMode by remember { mutableStateOf("FULL") } // FULL, CUSTOM, ZERO
    var customCashInput by remember { mutableStateOf(participant.balance.toString()) }
    val cashDelivered = when (paymentMode) {
        "FULL" -> participant.balance
        "ZERO" -> 0
        else -> customCashInput.toIntOrNull() ?: 0
    }
    val remainingBalance = (participant.balance - cashDelivered).coerceAtLeast(0)

    // Step 2 State: Contract & Signature
    var hasTouchSignature by remember { mutableStateOf(false) }
    var isContractAccepted by remember { mutableStateOf(true) }

    // Step 3 State: Medical Test
    val shootDate = shoot?.date ?: "31/08/2026"
    val testAlert = participant.getTestAlertLevel(shootDate)
    val daysUntilExpiration = participant.calculateDaysUntilExpiration(shootDate)
    val isMedicalValid = !participant.isBanned && testAlert != TestAlertLevel.EXPIRED && participant.testResult == "NEGATIVO"

    // Step 4 State: Locker assignment
    val occupiedMap = remember(attendances) {
        attendances.filter { it.isPresent && it.lockerNumber != null }.associateBy { it.lockerNumber!! }
    }
    var manualLockerInput by remember {
        val existingAtt = attendances.find { it.participantId == participant.id }
        val defaultLocker = existingAtt?.lockerNumber ?: ((1..90).firstOrNull { it !in occupiedMap } ?: 1)
        mutableStateOf(defaultLocker.toString())
    }
    val selectedLockerNumber = manualLockerInput.toIntOrNull() ?: 1
    val isLockerOccupied = occupiedMap.containsKey(selectedLockerNumber) && occupiedMap[selectedLockerNumber]?.participantId != participant.id

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .height(680.dp)
                .padding(vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Header: Participant Summary
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(BrandCoral),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = participant.nick.take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = participant.nick,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                MondayStatusPill(
                                    text = "ID #${participant.id}",
                                    backgroundColor = MondayBlue
                                )
                            }
                            Text(
                                text = "${participant.fullName} • ${participant.docType} ${participant.docNumber}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Wizard Progress Stepper
                WizardStepIndicator(
                    currentStep = currentStep,
                    onStepClick = { step ->
                        if (step <= currentStep || (step == 2) || (step == 3 && hasTouchSignature) || (step == 4 && isMedicalValid)) {
                            currentStep = step
                        }
                    }
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Active Step Content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                ) {
                    AnimatedContent(
                        targetState = currentStep,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "stepAnim"
                    ) { step ->
                        when (step) {
                            1 -> Step1BalanceCash(
                                participant = participant,
                                paymentMode = paymentMode,
                                customCashInput = customCashInput,
                                cashDelivered = cashDelivered,
                                remainingBalance = remainingBalance,
                                onPaymentModeChange = { paymentMode = it },
                                onCustomCashChange = { customCashInput = it }
                            )
                            2 -> Step2ImageRightsContract(
                                participant = participant,
                                shoot = shoot,
                                contractText = contractText,
                                isAccepted = isContractAccepted,
                                onAcceptanceChange = { isContractAccepted = it },
                                onSignatureChanged = { hasTouchSignature = it }
                            )
                            3 -> Step3MedicalVerification(
                                participant = participant,
                                shoot = shoot,
                                testAlert = testAlert,
                                daysUntilExpiration = daysUntilExpiration,
                                isMedicalValid = isMedicalValid
                            )
                            4 -> Step4LockerAssignment(
                                participant = participant,
                                manualLockerInput = manualLockerInput,
                                selectedLockerNumber = selectedLockerNumber,
                                occupiedMap = occupiedMap,
                                isLockerOccupied = isLockerOccupied,
                                cashDelivered = cashDelivered,
                                remainingBalance = remainingBalance,
                                onLockerChange = { manualLockerInput = it }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Navigation Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (currentStep > 1) {
                        OutlinedButton(onClick = { currentStep-- }) {
                            Icon(Icons.Default.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Anterior")
                        }
                    } else {
                        OutlinedButton(onClick = onDismiss) {
                            Text("Cancelar")
                        }
                    }

                    if (currentStep < 4) {
                        Button(
                            onClick = { currentStep++ },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandCoral),
                            enabled = when (currentStep) {
                                1 -> true
                                2 -> isContractAccepted
                                3 -> isMedicalValid
                                else -> true
                            }
                        ) {
                            Text(
                                when (currentStep) {
                                    1 -> "Continuar a Contrato"
                                    2 -> "Continuar a Analítica"
                                    3 -> "Continuar a Taquilla"
                                    else -> "Siguiente"
                                }
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    } else {
                        Button(
                            onClick = {
                                onCompleteCheckIn(cashDelivered, selectedLockerNumber, isContractAccepted)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MondayGreen),
                            enabled = !isLockerOccupied && selectedLockerNumber in 1..99
                        ) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("COMPLETAR ACCESO Y ASIGNAR", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun WizardStepIndicator(
    currentStep: Int,
    onStepClick: (Int) -> Unit
) {
    val steps = listOf(
        1 to "1. Cobro Saldo",
        2 to "2. Contrato Imagen",
        3 to "3. Test Médico",
        4 to "4. Taquilla Única"
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        steps.forEach { (stepNum, title) ->
            val isActive = currentStep == stepNum
            val isDone = currentStep > stepNum

            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        when {
                            isActive -> BrandCoral
                            isDone -> MondayGreenLight
                            else -> Color.Transparent
                        }
                    )
                    .clickable { onStepClick(stepNum) }
                    .padding(vertical = 6.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isDone) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = null,
                            tint = MondayGreen,
                            modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                    }
                    Text(
                        text = title,
                        fontSize = 10.sp,
                        fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                        color = when {
                            isActive -> Color.White
                            isDone -> MondayGreen
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        },
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 1: COBRO Y LIQUIDACION DE BALANCE
// -------------------------------------------------------------
@Composable
fun Step1BalanceCash(
    participant: ParticipantEntity,
    paymentMode: String,
    customCashInput: String,
    cashDelivered: Int,
    remainingBalance: Int,
    onPaymentModeChange: (String) -> Unit,
    onCustomCashChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "PASO 1: COBRO Y LIQUIDACIÓN DEL BALANCE ACUMULADO",
            fontWeight = FontWeight.Black,
            fontSize = 13.sp,
            color = BrandCoralDark
        )
        Text(
            text = "El participante puede cobrar el total acumulado de eventos previos o una cantidad personalizada.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Balance Summary Hero Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MondayBlueLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "BALANCE PREVIO ACUMULADO",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MondayBlue
                    )
                    Text(
                        text = "${participant.balance} €",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Black,
                        color = MondayBlue
                    )
                    Text(
                        text = "Generado en ${participant.totalParticipations} participaciones anteriores",
                        fontSize = 10.sp,
                        color = MondayBlue
                    )
                }
                Icon(
                    imageVector = Icons.Default.MonetizationOn,
                    contentDescription = null,
                    tint = MondayBlue,
                    modifier = Modifier.size(42.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "OPCIÓN DE PAGO EN RECEPCIÓN:",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Option 1: Full Cash
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(if (paymentMode == "FULL") BrandCoral.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    width = if (paymentMode == "FULL") 1.5.dp else 0.dp,
                    color = if (paymentMode == "FULL") BrandCoral else Color.Transparent,
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable { onPaymentModeChange("FULL") }
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = paymentMode == "FULL",
                onClick = { onPaymentModeChange("FULL") }
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Cobro íntegro en efectivo (${participant.balance} €)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = "Se le entrega el balance completo en mano y su cuenta queda a 0€.",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "${participant.balance} €",
                fontWeight = FontWeight.Black,
                fontSize = 14.sp,
                color = BrandCoral
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Option 2: Custom Amount
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(if (paymentMode == "CUSTOM") BrandCoral.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    width = if (paymentMode == "CUSTOM") 1.5.dp else 0.dp,
                    color = if (paymentMode == "CUSTOM") BrandCoral else Color.Transparent,
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable { onPaymentModeChange("CUSTOM") }
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = paymentMode == "CUSTOM",
                onClick = { onPaymentModeChange("CUSTOM") }
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Cifra personalizada / parcial",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = "Especifica la cantidad exacta a entregar en efectivo hoy.",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (paymentMode == "CUSTOM") {
                OutlinedTextField(
                    value = customCashInput,
                    onValueChange = onCustomCashChange,
                    modifier = Modifier.width(90.dp),
                    singleLine = true,
                    label = { Text("€") }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Option 3: Zero / No payment today
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(if (paymentMode == "ZERO") BrandCoral.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant)
                .border(
                    width = if (paymentMode == "ZERO") 1.5.dp else 0.dp,
                    color = if (paymentMode == "ZERO") BrandCoral else Color.Transparent,
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable { onPaymentModeChange("ZERO") }
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = paymentMode == "ZERO",
                onClick = { onPaymentModeChange("ZERO") }
            )
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "No cobrar nada hoy (0 €)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    text = "Acumular todo el saldo junto con las corridas de la sesión actual.",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Text(
                text = "0 €",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MondayGrey
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live calculation result
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MondayGreenLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Efectivo entregado en mano: $cashDelivered €",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = Color(0xFF006633)
                    )
                    Text(
                        text = "Balance remanente en cuenta: $remainingBalance €",
                        fontSize = 11.sp,
                        color = Color(0xFF006633)
                    )
                }
                MondayStatusPill(
                    text = "Liquidado",
                    backgroundColor = MondayGreen
                )
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 2: CONTRATO DE CESION DE DERECHOS DE IMAGEN & FIRMA
// -------------------------------------------------------------
@Composable
fun Step2ImageRightsContract(
    participant: ParticipantEntity,
    shoot: ShootEventEntity?,
    contractText: String,
    isAccepted: Boolean,
    onAcceptanceChange: (Boolean) -> Unit,
    onSignatureChanged: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "PASO 2: CONTRATO DE CESIÓN DE DERECHOS DE IMAGEN",
            fontWeight = FontWeight.Black,
            fontSize = 13.sp,
            color = BrandCoralDark
        )
        Text(
            text = "Revisión de cláusulas legales, declaración jurada de salud y firma táctil digital.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Contract scrollable document box
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant)
                .padding(10.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = contractText,
                fontSize = 9.5.sp,
                fontFamily = FontFamily.Monospace,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Interactive Signature Pad
        SignaturePad(
            modifier = Modifier.fillMaxWidth(),
            onSignatureChanged = onSignatureChanged
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Explicit Acceptance Switch
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(if (isAccepted) MondayGreenLight else MaterialTheme.colorScheme.surfaceVariant)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = isAccepted,
                onCheckedChange = onAcceptanceChange
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(
                    text = "El participante acepta las cláusulas y cede sus derechos de imagen.",
                    fontWeight = FontWeight.Bold,
                    fontSize = 11.sp,
                    color = if (isAccepted) Color(0xFF006633) else MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Mayor de edad (+18) y en posesión de plena capacidad legal.",
                    fontSize = 9.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// -------------------------------------------------------------
// STEP 3: VERIFICACION DE VALIDEZ DE ANALITICA
// -------------------------------------------------------------
@Composable
fun Step3MedicalVerification(
    participant: ParticipantEntity,
    shoot: ShootEventEntity?,
    testAlert: TestAlertLevel,
    daysUntilExpiration: Int?,
    isMedicalValid: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "PASO 3: VERIFICACIÓN DE VALIDEZ DE ANALÍTICA",
            fontWeight = FontWeight.Black,
            fontSize = 13.sp,
            color = BrandCoralDark
        )
        Text(
            text = "El sistema verifica automáticamente que el test médico cumpla la regla legal de los 30 días de vigencia.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Verification Status Banner
        val bannerBg = if (isMedicalValid) MondayGreenLight else MondayRedLight
        val bannerColor = if (isMedicalValid) MondayGreen else MondayRed
        val bannerTitle = if (isMedicalValid) "ANALÍTICA VÁLIDA Y VERIFICADA" else "BLOQUEO: ANALÍTICA NO APTA"

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = bannerBg),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isMedicalValid) Icons.Default.VerifiedUser else Icons.Default.ReportProblem,
                    contentDescription = null,
                    tint = bannerColor,
                    modifier = Modifier.size(36.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = bannerTitle,
                        fontWeight = FontWeight.Black,
                        fontSize = 13.sp,
                        color = bannerColor
                    )
                    Text(
                        text = if (isMedicalValid) "El participante cumple todos los requisitos sanitarios para el rodaje." else "La analítica ha caducado (>30 días) o el participante está en estado de exclusión.",
                        fontSize = 10.5.sp,
                        color = Color(0xFF1E293B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Detailed Medical Checklist Box
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "PARÁMETROS ANALÍTICOS REGISTRADOS:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))

                MedicalCheckRow(
                    label = "Resultado Serológico:",
                    value = participant.testResult,
                    isPass = participant.testResult == "NEGATIVO"
                )
                MedicalCheckRow(
                    label = "Fecha de Test:",
                    value = "${participant.testDate} (Vigencia: ${daysUntilExpiration ?: 0} días restantes)",
                    isPass = (daysUntilExpiration ?: 0) > 0
                )
                MedicalCheckRow(
                    label = "Nº de Muestra de Laboratorio:",
                    value = participant.sampleNumber.ifBlank { "REGISTRADA" },
                    isPass = true
                )
                MedicalCheckRow(
                    label = "Estado de Usuario:",
                    value = if (participant.isBanned) "BANEADO / RESTRINGIDO" else "ACTIVO / AUTORIZADO",
                    isPass = !participant.isBanned
                )
                MedicalCheckRow(
                    label = "Condición de Renovación:",
                    value = participant.renewalStatus,
                    isPass = true
                )
            }
        }
    }
}

@Composable
fun MedicalCheckRow(label: String, value: String, isPass: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isPass) Icons.Default.CheckCircle else Icons.Default.Close,
                contentDescription = null,
                tint = if (isPass) MondayGreen else MondayRed,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = label, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurface)
        }
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = if (isPass) MondayGreen else MondayRed
        )
    }
}

// -------------------------------------------------------------
// STEP 4: ASIGNACION MANUAL DE NUMERO DE TAQUILLA UNICO
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Step4LockerAssignment(
    participant: ParticipantEntity,
    manualLockerInput: String,
    selectedLockerNumber: Int,
    occupiedMap: Map<Int, AttendanceEntity>,
    isLockerOccupied: Boolean,
    cashDelivered: Int,
    remainingBalance: Int,
    onLockerChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "PASO 4: ASIGNACIÓN DE NÚMERO DE TAQUILLA ÚNICO",
            fontWeight = FontWeight.Black,
            fontSize = 13.sp,
            color = BrandCoralDark
        )
        Text(
            text = "Introduce manualmente el número de taquilla/casillero físico asignado. Este identificador se vinculará a todas las grabaciones, corridas (+20€) y premios del rodaje.",
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Manual Input field with collision validator
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            OutlinedTextField(
                value = manualLockerInput,
                onValueChange = onLockerChange,
                label = { Text("Número de Taquilla (1..90)") },
                modifier = Modifier.weight(1f),
                singleLine = true,
                leadingIcon = {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = BrandCoral)
                },
                isError = isLockerOccupied
            )

            // Status Badge
            if (isLockerOccupied) {
                MondayStatusPill(
                    text = "OCUPADA",
                    backgroundColor = MondayRed,
                    modifier = Modifier.padding(top = 6.dp)
                )
            } else if (selectedLockerNumber in 1..99) {
                MondayStatusPill(
                    text = "DISPONIBLE",
                    backgroundColor = MondayGreen,
                    modifier = Modifier.padding(top = 6.dp)
                )
            }
        }

        if (isLockerOccupied) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "⚠️ ¡Error: La Taquilla #$selectedLockerNumber ya está asignada a otro participante en este rodaje!",
                color = MondayRed,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
            text = "MAPA RÁPIDO DE TAQUILLAS (SELECCIÓN RÁPIDA):",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(6.dp))

        // Quick Selector Matrix (1..32)
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            (1..28).forEach { num ->
                val isOccupied = occupiedMap.containsKey(num) && occupiedMap[num]?.participantId != participant.id
                val isSelected = selectedLockerNumber == num

                Box(
                    modifier = Modifier
                        .size(width = 44.dp, height = 36.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            when {
                                isSelected -> BrandCoral
                                isOccupied -> MondayRedLight
                                else -> MondayGreenLight
                            }
                        )
                        .border(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) BrandCoralDark else Color.Transparent,
                            shape = RoundedCornerShape(6.dp)
                        )
                        .clickable(enabled = !isOccupied) {
                            onLockerChange(num.toString())
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "#$num",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = when {
                            isSelected -> Color.White
                            isOccupied -> MondayRed
                            else -> Color(0xFF006633)
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Final Summary Card
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "RESUMEN DE REGISTRO DE ENTRADA:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = BrandCoralDark
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Participante:", fontSize = 11.sp)
                    Text("${participant.nick} (#${participant.id})", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Efectivo entregado en mano:", fontSize = 11.sp)
                    Text("$cashDelivered €", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BrandCoral)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Balance remanente guardado:", fontSize = 11.sp)
                    Text("$remainingBalance €", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Taquilla Asignada:", fontSize = 11.sp)
                    Text("Taquilla #$selectedLockerNumber", fontWeight = FontWeight.Black, fontSize = 12.sp, color = MondayGreen)
                }
            }
        }
    }
}
