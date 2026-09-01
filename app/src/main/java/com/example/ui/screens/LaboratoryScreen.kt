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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.window.Dialog
import com.example.data.model.ParticipantEntity
import com.example.ui.BukkakeryViewModel
import com.example.ui.components.MondayStatusPill
import com.example.ui.components.TestExpirationBadge
import com.example.ui.theme.BrandCoral
import com.example.ui.theme.MondayGreen
import com.example.ui.theme.MondayGreenLight
import com.example.ui.theme.MondayPurple
import com.example.ui.theme.MondayPurpleLight
import com.example.ui.theme.MondayRed
import com.example.ui.theme.MondayRedLight

@Composable
fun LaboratoryScreen(
    viewModel: BukkakeryViewModel,
    modifier: Modifier = Modifier
) {
    val participants by viewModel.allParticipants.collectAsState()
    var searchQuery by remember { mutableStateOf("") }
    var selectedParticipantForLab by remember { mutableStateOf<ParticipantEntity?>(null) }

    val filteredList = remember(searchQuery, participants) {
        if (searchQuery.isBlank()) {
            participants
        } else {
            participants.filter {
                it.nick.contains(searchQuery, ignoreCase = true) ||
                it.fullName.contains(searchQuery, ignoreCase = true) ||
                it.sampleNumber.contains(searchQuery, ignoreCase = true) ||
                it.docNumber.contains(searchQuery, ignoreCase = true) ||
                it.id.toString() == searchQuery.trim()
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Lab Header Banner
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MondayPurpleLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MondayPurple),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Biotech,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "VISTA DE LABORATORIO & CONTROL SEROLÓGICO",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = MondayPurple
                    )
                    Text(
                        text = "Actualización de números de muestra, fechas de test y resultados médicos.",
                        fontSize = 11.sp,
                        color = Color(0xFF4A154B)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Buscar por Nick, Nombre, Muestra o DNI...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "REGISTRO DE PARTICIPANTES (${filteredList.size})",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(filteredList, key = { it.id }) { p ->
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "#${p.id} ${p.nick}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                MondayStatusPill(
                                    text = "Muestra: ${p.sampleNumber.ifBlank { "Sin asignar" }}",
                                    backgroundColor = MondayPurple
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${p.docType} ${p.docNumber} | ${p.fullName}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                TestExpirationBadge(participant = p)
                                MondayStatusPill(
                                    text = p.testResult,
                                    backgroundColor = if (p.testResult == "NEGATIVO") MondayGreen else MondayRed
                                )
                                Text(
                                    text = "Fecha: ${p.testDate.ifBlank { "N/A" }}",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }

                        Button(
                            onClick = { selectedParticipantForLab = p },
                            colors = ButtonDefaults.buttonColors(containerColor = MondayPurple)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Actualizar", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    // Lab Edit Dialog
    if (selectedParticipantForLab != null) {
        val p = selectedParticipantForLab!!
        var sample by remember { mutableStateOf(p.sampleNumber) }
        var testDate by remember { mutableStateOf(p.testDate.ifBlank { "31/08/2026" }) }
        var result by remember { mutableStateOf(p.testResult) }

        Dialog(onDismissRequest = { selectedParticipantForLab = null }) {
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
                                text = "Actualizar Analítica de Laboratorio",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "${p.nick} (#${p.id}) - ${p.fullName}",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        IconButton(onClick = { selectedParticipantForLab = null }) {
                            Icon(Icons.Default.Close, contentDescription = "Cerrar")
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = sample,
                        onValueChange = { sample = it },
                        label = { Text("Número de Muestra") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = testDate,
                        onValueChange = { testDate = it },
                        label = { Text("Fecha de Test (dd/MM/yyyy)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text("Resultado del Test:", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf("NEGATIVO", "POSITIVO", "PENDIENTE", "POSCLAMIDA").forEach { res ->
                            val isSelected = result.equals(res, ignoreCase = true)
                            val bg = when (res) {
                                "NEGATIVO" -> MondayGreen
                                "POSITIVO", "POSCLAMIDA" -> MondayRed
                                else -> Color.Gray
                            }
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSelected) bg else bg.copy(alpha = 0.2f))
                                    .clickable { result = res }
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = res,
                                    color = if (isSelected) Color.White else Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(onClick = { selectedParticipantForLab = null }) {
                            Text("Cancelar")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                viewModel.updateParticipantTest(p.id, sample.trim(), testDate.trim(), result.trim().uppercase())
                                selectedParticipantForLab = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MondayPurple)
                        ) {
                            Text("Guardar Analítica")
                        }
                    }
                }
            }
        }
    }
}
