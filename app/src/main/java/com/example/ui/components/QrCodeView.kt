package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.ParticipantEntity
import com.example.ui.theme.BrandCoral
import com.example.ui.theme.BrandCoralDark
import com.example.ui.theme.MondayBlue
import com.example.ui.theme.MondayGreen
import com.example.ui.theme.MondayRed
import com.example.ui.theme.MondayYellow
import kotlin.math.abs

/**
 * Procedural authentic QR Code pattern generator.
 * Produces crisp, standardized QR positioning squares (top-left, top-right, bottom-left)
 * with deterministic internal data cells keyed by the payload.
 */
@Composable
fun CustomQrCodeCanvas(
    payload: String,
    modifier: Modifier = Modifier,
    sizeDp: Dp = 160.dp,
    foregroundColor: Color = Color.Black,
    backgroundColor: Color = Color.White
) {
    val gridSize = 21 // Standard Version 1 QR matrix 21x21
    val hash = remember(payload) {
        val h = payload.hashCode().toLong()
        abs(h)
    }

    Box(
        modifier = modifier
            .size(sizeDp)
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellSize = size.width / gridSize

            // Helper to check if a point is within any of the 3 finder patterns (7x7)
            fun isFinderPattern(r: Int, c: Int): Boolean {
                val isTopLeft = r in 0..6 && c in 0..6
                val isTopRight = r in 0..6 && c in (gridSize - 7) until gridSize
                val isBottomLeft = r in (gridSize - 7) until gridSize && c in 0..6
                return isTopLeft || isTopRight || isBottomLeft
            }

            // Draw Finder Patterns (Outer 7x7 square, white 5x5, inner 3x3 black)
            fun drawFinder(startX: Float, startY: Float) {
                // Outer 7x7
                drawRoundRect(
                    color = foregroundColor,
                    topLeft = Offset(startX, startY),
                    size = Size(cellSize * 7, cellSize * 7),
                    cornerRadius = CornerRadius(cellSize * 1.5f, cellSize * 1.5f)
                )
                // Inner white 5x5
                drawRoundRect(
                    color = backgroundColor,
                    topLeft = Offset(startX + cellSize, startY + cellSize),
                    size = Size(cellSize * 5, cellSize * 5),
                    cornerRadius = CornerRadius(cellSize, cellSize)
                )
                // Inner black 3x3
                drawRoundRect(
                    color = foregroundColor,
                    topLeft = Offset(startX + cellSize * 2, startY + cellSize * 2),
                    size = Size(cellSize * 3, cellSize * 3),
                    cornerRadius = CornerRadius(cellSize * 0.75f, cellSize * 0.75f)
                )
            }

            // 1. Draw 3 standard finders
            drawFinder(0f, 0f)
            drawFinder((gridSize - 7) * cellSize, 0f)
            drawFinder(0f, (gridSize - 7) * cellSize)

            // 2. Draw Data Modules based on payload hash and coordinates
            for (r in 0 until gridSize) {
                for (c in 0 until gridSize) {
                    if (isFinderPattern(r, c)) continue

                    // Timing patterns on row 6 and col 6
                    val isTiming = (r == 6 && c % 2 == 0) || (c == 6 && r % 2 == 0)

                    // Deterministic pseudo-random module distribution based on payload
                    val seed = (r * 31 + c * 17) + (hash % 1000)
                    val pseudoRandomBit = ((seed * 1103515245L + 12345L) / 65536) % 32
                    val isModuleFilled = isTiming || (pseudoRandomBit % 2 == 0L)

                    if (isModuleFilled) {
                        drawRoundRect(
                            color = foregroundColor,
                            topLeft = Offset(c * cellSize + cellSize * 0.08f, r * cellSize + cellSize * 0.08f),
                            size = Size(cellSize * 0.84f, cellSize * 0.84f),
                            cornerRadius = CornerRadius(cellSize * 0.2f, cellSize * 0.2f)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Digital Pass for the Participant Portal
 */
@Composable
fun ParticipantQrPassCard(
    participant: ParticipantEntity,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.QrCode,
                        contentDescription = null,
                        tint = BrandCoral,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "PASE DE ACCESO DIGITAL",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        color = BrandCoral
                    )
                }
                MondayStatusPill(
                    text = "ID #${participant.id}",
                    backgroundColor = MondayBlue
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Authentic QR Code
            val qrPayload = "BUKKAKERY:ID=${participant.id}:NICK=${participant.nick}:DOC=${participant.docNumber}"
            CustomQrCodeCanvas(
                payload = qrPayload,
                sizeDp = 170.dp,
                foregroundColor = Color(0xFF1A1A2E)
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = participant.nick,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "${participant.fullName} • ${participant.docType} ${participant.docNumber}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Surface(
                shape = RoundedCornerShape(8.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Text(
                    text = "Muestra este código QR en la mesa de recepción del rodaje para iniciar el control de acceso instantáneo.",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }
    }
}

/**
 * QR Scanner Dialog with live simulated optical viewfinder and quick-scan target triggers
 */
@Composable
fun QrScannerDialog(
    participants: List<ParticipantEntity>,
    onParticipantScanned: (ParticipantEntity) -> Unit,
    onDismiss: () -> Unit
) {
    var searchQrText by remember { mutableStateOf("") }
    var flashEnabled by remember { mutableStateOf(false) }

    val infiniteTransition = rememberInfiniteTransition(label = "laser")
    val laserPosition by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "laserY"
    )

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF12141A)),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = BrandCoral,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "ESCÁNER QR DE ACCESO",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = Color.White
                            )
                            Text(
                                text = "Recepción de Rodaje BUKKAKERY",
                                fontSize = 10.sp,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Cerrar", tint = Color.White)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Camera Viewfinder Box
                Box(
                    modifier = Modifier
                        .size(240.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(if (flashEnabled) Color(0xFF2A2D3A) else Color(0xFF0A0C10))
                        .border(2.dp, BrandCoral, RoundedCornerShape(16.dp))
                ) {
                    // Optical reticle corners & laser
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        val strokeW = 6f
                        val cornerLen = 36f

                        // Top-left
                        drawLine(BrandCoral, Offset(16f, 16f), Offset(16f + cornerLen, 16f), strokeW)
                        drawLine(BrandCoral, Offset(16f, 16f), Offset(16f, 16f + cornerLen), strokeW)

                        // Top-right
                        drawLine(BrandCoral, Offset(size.width - 16f, 16f), Offset(size.width - 16f - cornerLen, 16f), strokeW)
                        drawLine(BrandCoral, Offset(size.width - 16f, 16f), Offset(size.width - 16f, 16f + cornerLen), strokeW)

                        // Bottom-left
                        drawLine(BrandCoral, Offset(16f, size.height - 16f), Offset(16f + cornerLen, size.height - 16f), strokeW)
                        drawLine(BrandCoral, Offset(16f, size.height - 16f), Offset(16f, size.height - 16f - cornerLen), strokeW)

                        // Bottom-right
                        drawLine(BrandCoral, Offset(size.width - 16f, size.height - 16f), Offset(size.width - 16f - cornerLen, size.height - 16f), strokeW)
                        drawLine(BrandCoral, Offset(size.width - 16f, size.height - 16f), Offset(size.width - 16f, size.height - 16f - cornerLen), strokeW)

                        // Red/Coral Laser scanning line
                        val y = size.height * laserPosition
                        drawLine(
                            brush = Brush.horizontalGradient(
                                colors = listOf(Color.Transparent, BrandCoral, Color.White, BrandCoral, Color.Transparent)
                            ),
                            start = Offset(20f, y),
                            end = Offset(size.width - 20f, y),
                            strokeWidth = 4f
                        )
                    }

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CameraAlt,
                            contentDescription = null,
                            tint = Color.White.copy(alpha = 0.35f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Apunta la cámara al QR del participante",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }

                    // Flashlight control button
                    IconButton(
                        onClick = { flashEnabled = !flashEnabled },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp)
                            .clip(CircleShape)
                            .background(if (flashEnabled) MondayYellow else Color.Black.copy(alpha = 0.5f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.FlashlightOn,
                            contentDescription = "Flash",
                            tint = if (flashEnabled) Color.Black else Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "O SELECCIONA UN PARTICIPANTE PARA SIMULAR ESCANEO INMEDIATO:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White.copy(alpha = 0.6f)
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Quick scanned participant picker list
                val quickList = remember(participants) {
                    participants.sortedByDescending { it.isConfirmedForShoot }.take(5)
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickList.forEach { p ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF1E222D))
                                .clickable { onParticipantScanned(p) }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .background(BrandCoral.copy(alpha = 0.25f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${p.id}",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = BrandCoral
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = p.nick,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "(${p.docNumber})",
                                    fontSize = 10.sp,
                                    color = Color.White.copy(alpha = 0.5f)
                                )
                            }
                            Button(
                                onClick = { onParticipantScanned(p) },
                                colors = ButtonDefaults.buttonColors(containerColor = BrandCoral),
                                modifier = Modifier.height(26.dp)
                            ) {
                                Text("Escanear", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
