package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BrandCoralDark
import com.example.ui.theme.MondayBlue
import com.example.ui.theme.MondayGrey

@Composable
fun SignaturePad(
    modifier: Modifier = Modifier,
    onSignatureChanged: (hasSignature: Boolean) -> Unit = {}
) {
    val paths = remember { mutableStateListOf<List<Offset>>() }
    var currentPath by remember { mutableStateOf<List<Offset>>(emptyList()) }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Create,
                    contentDescription = null,
                    tint = BrandCoralDark,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Lienzo de Firma Digital Táctil",
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    fontSize = 12.sp,
                    color = BrandCoralDark
                )
            }
            OutlinedButton(
                onClick = {
                    paths.clear()
                    currentPath = emptyList()
                    onSignatureChanged(false)
                },
                modifier = Modifier.height(28.dp)
            ) {
                Icon(Icons.Default.Clear, contentDescription = "Limpiar", modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Limpiar", fontSize = 10.sp)
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFFFAFAFA))
                .border(1.5.dp, Color(0xFFD0D4DC), RoundedCornerShape(8.dp))
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentPath = listOf(offset)
                        },
                        onDrag = { change, _ ->
                            change.consume()
                            currentPath = currentPath + change.position
                        },
                        onDragEnd = {
                            if (currentPath.isNotEmpty()) {
                                paths.add(currentPath)
                                currentPath = emptyList()
                                onSignatureChanged(true)
                            }
                        },
                        onDragCancel = {
                            currentPath = emptyList()
                        }
                    )
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                // Baseline guide
                val baselineY = size.height * 0.75f
                drawLine(
                    color = Color(0xFFE0E0E0),
                    start = Offset(20f, baselineY),
                    end = Offset(size.width - 20f, baselineY),
                    strokeWidth = 2f
                )

                // Draw completed paths
                for (pathPoints in paths) {
                    if (pathPoints.size > 1) {
                        val path = Path().apply {
                            moveTo(pathPoints.first().x, pathPoints.first().y)
                            for (i in 1 until pathPoints.size) {
                                lineTo(pathPoints[i].x, pathPoints[i].y)
                            }
                        }
                        drawPath(
                            path = path,
                            color = Color(0xFF1E293B),
                            style = Stroke(
                                width = 4f,
                                cap = StrokeCap.Round,
                                join = StrokeJoin.Round
                            )
                        )
                    }
                }

                // Draw currently active stroke
                if (currentPath.size > 1) {
                    val path = Path().apply {
                        moveTo(currentPath.first().x, currentPath.first().y)
                        for (i in 1 until currentPath.size) {
                            lineTo(currentPath[i].x, currentPath[i].y)
                        }
                    }
                    drawPath(
                        path = path,
                        color = Color(0xFF1E293B),
                        style = Stroke(
                            width = 4f,
                            cap = StrokeCap.Round,
                            join = StrokeJoin.Round
                        )
                    )
                }
            }

            if (paths.isEmpty() && currentPath.isEmpty()) {
                Text(
                    text = "✍️ Dibuja tu firma aquí con el dedo o lápiz",
                    fontSize = 11.sp,
                    color = Color(0xFF9E9E9E),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    }
}
