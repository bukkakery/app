package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ParticipantEntity
import com.example.data.model.ShootEventEntity
import com.example.data.model.TestAlertLevel
import com.example.data.model.UserRole
import com.example.ui.ParticipantFilter
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
import com.example.ui.theme.MondayYellowLight

@Composable
fun AppBrandHeader(
    currentRole: UserRole,
    shoot: ShootEventEntity?,
    authenticatedParticipant: ParticipantEntity? = null,
    authenticatedStaffName: String = "",
    onLogout: () -> Unit
) {
    Surface(
        color = when (currentRole) {
            UserRole.ADMIN -> MondayPurple
            UserRole.LABORATORY -> MondayBlue
            UserRole.PARTICIPANT -> BrandCoral
        },
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "B",
                            color = when (currentRole) {
                                UserRole.ADMIN -> MondayPurple
                                UserRole.LABORATORY -> MondayBlue
                                UserRole.PARTICIPANT -> BrandCoral
                            },
                            fontWeight = FontWeight.Black,
                            fontSize = 22.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "BUKKAKERY",
                            color = Color.White,
                            fontWeight = FontWeight.Black,
                            fontSize = 17.sp,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = when (currentRole) {
                                UserRole.ADMIN -> "Panel de Administración & Producción"
                                UserRole.LABORATORY -> "Panel Técnico de Laboratorio"
                                UserRole.PARTICIPANT -> "Portal Personal del Participante"
                            },
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Active User Badge + Logout Action Button
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val userLabel = when (currentRole) {
                        UserRole.PARTICIPANT -> authenticatedParticipant?.nick ?: "Participante"
                        UserRole.ADMIN -> authenticatedStaffName.ifBlank { "Admin" }
                        UserRole.LABORATORY -> authenticatedStaffName.ifBlank { "Laboratorio" }
                    }

                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = Color.Black.copy(alpha = 0.25f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = when (currentRole) {
                                    UserRole.PARTICIPANT -> Icons.Default.Person
                                    UserRole.ADMIN -> Icons.Default.Lock
                                    UserRole.LABORATORY -> Icons.Default.Info
                                },
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = userLabel,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Logout Button
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.White.copy(alpha = 0.22f),
                        modifier = Modifier.clickable { onLogout() }
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Cerrar sesión",
                                tint = Color.White,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "Salir",
                                color = Color.White,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            if (shoot != null && currentRole == UserRole.ADMIN) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(start = 46.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(MondayGreen)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Rodaje en curso: ${shoot.shootCode} (${shoot.date})",
                        fontSize = 10.sp,
                        color = Color.White.copy(alpha = 0.85f),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun MondayStatusPill(
    text: String,
    backgroundColor: Color,
    textColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(4.dp))
            .background(backgroundColor)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun TestExpirationBadge(
    participant: ParticipantEntity,
    referenceDate: String = "31/08/2026"
) {
    val level = participant.getTestAlertLevel(referenceDate)
    val daysLeft = participant.calculateDaysUntilExpiration(referenceDate)

    val (bg, label) = when (level) {
        TestAlertLevel.VALID -> MondayGreen to "Válido (${daysLeft}d)"
        TestAlertLevel.EXPIRING_12 -> MondayYellow to "Expira ≤12d (${daysLeft}d)"
        TestAlertLevel.EXPIRING_7 -> MondayOrange to "Expira ≤7d (${daysLeft}d)"
        TestAlertLevel.EXPIRING_4 -> Color(0xFFFF5522) to "Expira ≤4d (${daysLeft}d)"
        TestAlertLevel.EXPIRING_2 -> MondayRed to "CRÍTICO ≤2d (${daysLeft}d)"
        TestAlertLevel.EXPIRED -> Color(0xFF88001B) to "EXPIRADO"
        TestAlertLevel.PENDING -> MondayGrey to "PENDIENTE"
    }

    MondayStatusPill(text = label, backgroundColor = bg)
}

@Composable
fun MetricKpiCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    icon: ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title.uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = value,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    color = color
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
fun FilterPillsRow(
    selectedFilter: ParticipantFilter,
    onFilterSelected: (ParticipantFilter) -> Unit,
    counts: Map<ParticipantFilter, Int> = emptyMap()
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        ParticipantFilter.values().forEach { filter ->
            val isSelected = selectedFilter == filter
            val count = counts[filter]
            val countText = if (count != null) " ($count)" else ""

            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        text = "${filter.label}$countText",
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = BrandCoral.copy(alpha = 0.15f),
                    selectedLabelColor = BrandCoralDark
                )
            )
        }
    }
}

// Telegram Dialog Preview
@Composable
fun TelegramPreviewDialog(
    title: String,
    messageText: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Send,
                            contentDescription = null,
                            tint = MondayBlue,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .heightIn(max = 340.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(12.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = messageText,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cerrar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Telegram Bukkakery", messageText)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Copiado al portapapeles para Telegram!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MondayBlue)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copiar para Telegram")
                    }
                }
            }
        }
    }
}

// Digital Rights Contract Dialog
@Composable
fun DigitalContractDialog(
    participant: ParticipantEntity,
    contractText: String,
    initialCashDelivered: Int,
    onConfirmSign: (cashDelivered: Int, lockerNumber: Int) -> Unit,
    onDismiss: () -> Unit
) {
    var cashDelivered by remember { mutableStateOf(initialCashDelivered.toString()) }
    var selectedLocker by remember { mutableStateOf((participant.id % 90 + 1).toString()) }
    var isAccepted by remember { mutableStateOf(true) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Contrato de Cesión de Imagen",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = "Participante: ${participant.nick} (#${participant.id})",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Cash Delivered & Locker Assignment Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = cashDelivered,
                        onValueChange = { cashDelivered = it },
                        label = { Text("Efectivo dado (€)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = selectedLocker,
                        onValueChange = { selectedLocker = it },
                        label = { Text("Nº Taquilla (1..90)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Contract content box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(10.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = contractText,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Signature Check
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MondayGreenLight)
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Switch(
                        checked = isAccepted,
                        onCheckedChange = { isAccepted = it }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "El participante firma y acepta la cesión de imagen y analíticas.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF006633)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Cancelar")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val cash = cashDelivered.toIntOrNull() ?: 0
                            val locker = selectedLocker.toIntOrNull() ?: 1
                            onConfirmSign(cash, locker)
                        },
                        enabled = isAccepted,
                        colors = ButtonDefaults.buttonColors(containerColor = BrandCoral)
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Firmar & Asignar Taquilla")
                    }
                }
            }
        }
    }
}

// Participant Add / Edit Dialog
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ParticipantEditorDialog(
    participant: ParticipantEntity?,
    onSave: (ParticipantEntity) -> Unit,
    onDelete: ((ParticipantEntity) -> Unit)? = null,
    onDismiss: () -> Unit
) {
    val isNew = participant == null

    var id by remember { mutableStateOf(participant?.id?.toString() ?: "") }
    var docType by remember { mutableStateOf(participant?.docType ?: "DNI") }
    var docNumber by remember { mutableStateOf(participant?.docNumber ?: "") }
    var sampleNumber by remember { mutableStateOf(participant?.sampleNumber ?: "") }
    var testDate by remember { mutableStateOf(participant?.testDate ?: "31/08/2026") }
    var testResult by remember { mutableStateOf(participant?.testResult ?: "NEGATIVO") }
    var status by remember { mutableStateOf(participant?.status ?: "ACTIVO") }
    var nick by remember { mutableStateOf(participant?.nick ?: "") }
    var fullName by remember { mutableStateOf(participant?.fullName ?: "") }
    var phone by remember { mutableStateOf(participant?.phone ?: "") }
    var iban by remember { mutableStateOf(participant?.iban ?: "") }
    var referrer by remember { mutableStateOf(participant?.referrer ?: "MIRCO") }
    var email by remember { mutableStateOf(participant?.email ?: "") }
    var telegramUser by remember { mutableStateOf(participant?.telegramUser ?: "") }
    var birthDate by remember { mutableStateOf(participant?.birthDate ?: "") }
    var renewalStatus by remember { mutableStateOf(participant?.renewalStatus ?: "No renovar") }
    var balance by remember { mutableStateOf(participant?.balance?.toString() ?: "0") }
    var totalParticipations by remember { mutableStateOf(participant?.totalParticipations?.toString() ?: "0") }
    var comments by remember { mutableStateOf(participant?.comments ?: "") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isNew) "Nuevo Casting / Alta Participante" else "Editar Ficha #${participant?.id} (${participant?.nick})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = if (isNew) "Exclusivo Administración: Admisión tras superar el proceso de casting." else "Actualización de datos y estado médico.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = id,
                        onValueChange = { id = it },
                        label = { Text("Nº Usuario ID") },
                        modifier = Modifier.weight(1f),
                        enabled = isNew,
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = nick,
                        onValueChange = { nick = it },
                        label = { Text("Nick / Alias") },
                        modifier = Modifier.weight(1.5f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = docType,
                        onValueChange = { docType = it },
                        label = { Text("Tipo Doc") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = docNumber,
                        onValueChange = { docNumber = it },
                        label = { Text("Nº Identificación") },
                        modifier = Modifier.weight(2f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nombre Completo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = phone,
                        onValueChange = { phone = it },
                        label = { Text("Teléfono") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = telegramUser,
                        onValueChange = { telegramUser = it },
                        label = { Text("Telegram (@usuario)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = sampleNumber,
                        onValueChange = { sampleNumber = it },
                        label = { Text("Nº Muestra") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = testDate,
                        onValueChange = { testDate = it },
                        label = { Text("Fecha Test (dd/MM/yyyy)") },
                        modifier = Modifier.weight(1.5f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = testResult,
                        onValueChange = { testResult = it },
                        label = { Text("Resultado Analítica") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = status,
                        onValueChange = { status = it },
                        label = { Text("Estado") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = balance,
                        onValueChange = { balance = it },
                        label = { Text("Balance (€)") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = totalParticipations,
                        onValueChange = { totalParticipations = it },
                        label = { Text("Total Eventos") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = renewalStatus,
                        onValueChange = { renewalStatus = it },
                        label = { Text("Renovación analítica") },
                        modifier = Modifier.weight(1.5f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = referrer,
                        onValueChange = { referrer = it },
                        label = { Text("Referido de") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = iban,
                    onValueChange = { iban = it },
                    label = { Text("IBAN Bancario") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = comments,
                    onValueChange = { comments = it },
                    label = { Text("Observaciones / Notas") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!isNew && onDelete != null && participant != null) {
                        TextButton(
                            onClick = { onDelete(participant) },
                            colors = ButtonDefaults.textButtonColors(contentColor = MondayRed)
                        ) {
                            Text("Eliminar")
                        }
                    } else {
                        Spacer(modifier = Modifier.width(1.dp))
                    }

                    Row {
                        OutlinedButton(onClick = onDismiss) {
                            Text("Cancelar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val parsedId = id.toIntOrNull() ?: (System.currentTimeMillis() % 10000).toInt()
                                val parsedBalance = balance.toIntOrNull() ?: 0
                                val parsedParts = totalParticipations.toIntOrNull() ?: 0

                                val entity = ParticipantEntity(
                                    id = parsedId,
                                    docType = docType.trim(),
                                    docNumber = docNumber.trim(),
                                    sampleNumber = sampleNumber.trim(),
                                    testDate = testDate.trim(),
                                    testResult = testResult.trim().uppercase(),
                                    status = status.trim().uppercase(),
                                    nick = nick.trim().ifBlank { "PARTICIPANTE_$parsedId" },
                                    fullName = fullName.trim(),
                                    phone = phone.trim(),
                                    iban = iban.trim(),
                                    referrer = referrer.trim(),
                                    email = email.trim(),
                                    telegramUser = telegramUser.trim(),
                                    birthDate = birthDate.trim(),
                                    renewalStatus = renewalStatus.trim(),
                                    balance = parsedBalance,
                                    totalParticipations = parsedParts,
                                    monthlyParticipations = 1,
                                    comments = comments.trim()
                                )
                                onSave(entity)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandCoral)
                        ) {
                            Text(if (isNew) "Guardar Ficha de Casting" else "Guardar Cambios")
                        }
                    }
                }
            }
        }
    }
}
