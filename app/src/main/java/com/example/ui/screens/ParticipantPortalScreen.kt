package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ParticipantEntity
import com.example.data.model.TestAlertLevel
import com.example.ui.BukkakeryViewModel
import com.example.ui.components.MetricKpiCard
import com.example.ui.components.MondayStatusPill
import com.example.ui.components.ParticipantQrPassCard
import com.example.ui.components.TestExpirationBadge
import com.example.ui.theme.BrandCoral
import com.example.ui.theme.MondayBlue
import com.example.ui.theme.MondayGreen
import com.example.ui.theme.MondayGreenLight
import com.example.ui.theme.MondayOrange
import com.example.ui.theme.MondayOrangeLight
import com.example.ui.theme.MondayPurple
import com.example.ui.theme.MondayPurpleLight
import com.example.ui.theme.MondayRed
import com.example.ui.theme.MondayRedLight
import com.example.ui.theme.MondayYellow

@Composable
fun ParticipantPortalScreen(
    viewModel: BukkakeryViewModel,
    modifier: Modifier = Modifier
) {
    val searchInput by viewModel.portalSearchInput.collectAsState()
    val participant by viewModel.currentPortalParticipant.collectAsState()
    val comments by viewModel.portalComments.collectAsState()
    val shoot by viewModel.currentShoot.collectAsState()

    var newCommentText by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        if (participant != null) {
            val p = participant!!
            val shootDate = shoot?.date ?: "31/08/2026"
            val alertLevel = p.getTestAlertLevel(shootDate)
            val daysLeft = p.calculateDaysUntilExpiration(shootDate)

            // Welcome Card
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(BrandCoral.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountCircle,
                                contentDescription = null,
                                tint = BrandCoral,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = p.nick,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp,
                                color = BrandCoral
                            )
                            Text(
                                text = "Doc: ${p.docType} ${p.docNumber} • ID #${p.id}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    MondayStatusPill(
                        text = p.status,
                        backgroundColor = if (p.status == "ACTIVO") MondayGreen else MondayRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Alert Banner if expiring
            if (alertLevel != TestAlertLevel.VALID) {
                val bannerBg = when (alertLevel) {
                    TestAlertLevel.EXPIRING_12 -> MondayYellow.copy(alpha = 0.18f)
                    TestAlertLevel.EXPIRING_7, TestAlertLevel.EXPIRING_4 -> MondayOrangeLight
                    TestAlertLevel.EXPIRING_2, TestAlertLevel.EXPIRED -> MondayRedLight
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = bannerBg),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = if (alertLevel == TestAlertLevel.EXPIRING_2 || alertLevel == TestAlertLevel.EXPIRED) MondayRed else MondayOrange,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "¡AVISO DE RENOVACIÓN DE ANALÍTICA!",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                color = if (alertLevel == TestAlertLevel.EXPIRING_2 || alertLevel == TestAlertLevel.EXPIRED) MondayRed else MondayOrange
                            )
                            Text(
                                text = if ((daysLeft ?: 0) > 0) "Quedan ${daysLeft} días para que venza tu test médico de 30 días. Por favor acude al laboratorio." else "Tu test médico está caducado. Necesitas renovarlo para participar en próximos rodajes.",
                                fontSize = 11.sp,
                                color = Color(0xFF333333)
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Participant Profile Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(44.dp)
                                    .clip(CircleShape)
                                    .background(BrandCoral.copy(alpha = 0.15f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = p.nick.take(1).uppercase(),
                                    color = BrandCoral,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(text = p.nick, fontWeight = FontWeight.Black, fontSize = 16.sp)
                                Text(
                                    text = "Usuario #${p.id} • ${p.docType} ${p.docNumber}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }

                        // Ban Status
                        if (p.isBanned) {
                            MondayStatusPill(text = "BANEADO", backgroundColor = MondayRed)
                        } else {
                            MondayStatusPill(text = "ACTIVO / APTO", backgroundColor = MondayGreen)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // KPIs Matrix
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        MetricKpiCard(
                            title = "Mi Balance Actual",
                            value = "${p.balance} €",
                            subtitle = "Acumulado disponible",
                            color = BrandCoral,
                            icon = Icons.Default.MonetizationOn,
                            modifier = Modifier.weight(1f)
                        )
                        MetricKpiCard(
                            title = "Participaciones",
                            value = "${p.totalParticipations}",
                            subtitle = if (p.qualifiesForFreeRenewal) "¡Renovación Gratis!" else "Faltan ${5 - p.totalParticipations} para gratis",
                            color = MondayPurple,
                            icon = Icons.Default.CheckCircle,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Medical Test Details Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(text = "INFORMACIÓN MÉDICA & ANALÍTICAS", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Fecha del último test:", fontSize = 12.sp)
                                Text(p.testDate.ifBlank { "Sin registrar" }, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Resultado analítica:", fontSize = 12.sp)
                                Text(
                                    p.testResult,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = if (p.testResult == "NEGATIVO") MondayGreen else MondayRed
                                )
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Número de muestra:", fontSize = 12.sp)
                                Text(p.sampleNumber.ifBlank { "N/A" }, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Condición de renovación:", fontSize = 12.sp)
                                Text(p.renewalStatus, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MondayPurple)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Confirm shoot attendance toggle
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (p.isConfirmedForShoot) MondayGreenLight else MaterialTheme.colorScheme.surfaceVariant)
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Confirmación para Rodaje ${shoot?.shootCode ?: "20263108"}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                            Text(
                                text = if (p.isConfirmedForShoot) "¡Has confirmado tu asistencia!" else "No has confirmado aún.",
                                fontSize = 10.sp
                            )
                        }
                        Switch(
                            checked = p.isConfirmedForShoot,
                            onCheckedChange = { viewModel.toggleConfirmation(p.id, it) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Digital QR Access Pass
            ParticipantQrPassCard(participant = p)

            Spacer(modifier = Modifier.height(14.dp))

            // Comments / Communication Section
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(text = "CONSULTAS & COMENTARIOS CON PRODUCCIÓN", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Comments list
                    if (comments.isEmpty()) {
                        Text(
                            text = "No hay comentarios previos. Puedes enviar dudas o consultas al equipo.",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    } else {
                        comments.forEach { c ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .padding(8.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(text = c.authorName, fontWeight = FontWeight.Bold, fontSize = 11.sp, color = BrandCoral)
                                        Text(text = c.authorRole, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(text = c.content, fontSize = 11.sp)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Send comment input
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = newCommentText,
                            onValueChange = { newCommentText = it },
                            placeholder = { Text("Escribe una consulta a producción...") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Button(
                            onClick = {
                                if (newCommentText.isNotBlank()) {
                                    viewModel.addPortalComment(
                                        participantId = p.id,
                                        authorRole = "PARTICIPANTE",
                                        authorName = p.nick,
                                        content = newCommentText.trim()
                                    )
                                    newCommentText = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = BrandCoral)
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}
