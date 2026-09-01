package com.example.ui.screens

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Warning
import com.example.data.firebase.CloudSyncState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import com.example.data.model.UserRole
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.RadioButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AttendanceEntity
import com.example.data.model.ParticipantEntity
import com.example.data.model.ShootEventEntity
import com.example.data.model.ShotLogEntity
import com.example.data.model.StaffEntity
import com.example.data.model.TestAlertLevel
import com.example.ui.BukkakeryViewModel
import com.example.ui.ParticipantFilter
import com.example.ui.components.AccessControlWizardDialog
import com.example.ui.components.DigitalContractDialog
import com.example.ui.components.FilterPillsRow
import com.example.ui.components.MetricKpiCard
import com.example.ui.components.MondayStatusPill
import com.example.ui.components.ParticipantEditorDialog
import com.example.ui.components.QrScannerDialog
import com.example.ui.components.TelegramPreviewDialog
import com.example.ui.components.TestExpirationBadge
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
import com.example.ui.theme.MondayPurple
import com.example.ui.theme.MondayPurpleLight
import com.example.ui.theme.MondayRed
import com.example.ui.theme.MondayRedLight
import com.example.ui.theme.MondayYellow
import com.example.ui.theme.MondayYellowLight

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: BukkakeryViewModel,
    modifier: Modifier = Modifier
) {
    val selectedTab by viewModel.selectedTab.collectAsState()
    val shoot by viewModel.currentShoot.collectAsState()
    val allParticipants by viewModel.allParticipants.collectAsState()
    val filteredParticipants by viewModel.filteredParticipants.collectAsState()
    val attendances by viewModel.attendances.collectAsState()
    val staffList by viewModel.staffList.collectAsState()
    val shotLogs by viewModel.shotLogs.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()

    val selectedParticipantForDetail by viewModel.selectedParticipantForDetail.collectAsState()
    val selectedParticipantForContract by viewModel.selectedParticipantForContract.collectAsState()

    var showNewParticipantDialog by remember { mutableStateOf(false) }
    var showTelegramPreDialog by remember { mutableStateOf(false) }
    var showTelegramPostDialog by remember { mutableStateOf(false) }
    var showExportCsvDialog by remember { mutableStateOf(false) }

    val tabTitles = listOf(
        "1. Prerodaje & Caja",
        "2. Control Acceso",
        "3. Live Rodaje",
        "4. Balance & Reporte",
        "5. Base Datos & SQL",
        "6. ☁️ Firestore & Cloud"
    )

    Column(modifier = modifier.fillMaxSize()) {
        // Navigation Tabs (Monday style)
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            edgePadding = 12.dp,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = BrandCoral
        ) {
            tabTitles.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { viewModel.setSelectedTab(index) },
                    text = {
                        Text(
                            text = title,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                )
            }
        }

        // Tab Content
        Box(modifier = Modifier.weight(1f)) {
            when (selectedTab) {
                0 -> PreRodajeTab(
                    shoot = shoot,
                    participants = allParticipants,
                    staff = staffList,
                    onToggleConfirm = { id, confirmed -> viewModel.toggleConfirmation(id, confirmed) },
                    onOpenTelegram = { showTelegramPreDialog = true }
                )
                1 -> ControlAccesoTab(
                    shoot = shoot,
                    participants = allParticipants,
                    attendances = attendances,
                    onOpenContract = { viewModel.selectParticipantForContract(it) }
                )
                2 -> LiveRodajeTab(
                    shoot = shoot,
                    attendances = attendances,
                    participants = allParticipants,
                    shotLogs = shotLogs,
                    onRecordShot = { locker, bonus, reason -> viewModel.recordShot(locker, bonus, reason) },
                    onDeleteLog = { id, locker, bonus -> viewModel.deleteShotLog(id, locker, bonus) }
                )
                3 -> BalancePostRodajeTab(
                    shoot = shoot,
                    attendances = attendances,
                    participants = allParticipants,
                    staff = staffList,
                    shotLogs = shotLogs,
                    onOpenTelegram = { showTelegramPostDialog = true },
                    onExportCsv = { showExportCsvDialog = true },
                    onToggleActressPaid = {
                        if (shoot != null) {
                            viewModel.updateShoot(shoot!!.copy(actressIsPaid = !shoot!!.actressIsPaid))
                        }
                    },
                    onToggleStaffPaid = { staff ->
                        viewModel.updateStaff(staff.copy(isPaid = !staff.isPaid))
                    }
                )
                4 -> DatabaseSqlTab(
                    viewModel = viewModel,
                    participants = filteredParticipants,
                    searchQuery = searchQuery,
                    selectedFilter = selectedFilter,
                    onSearchChange = { viewModel.setSearchQuery(it) },
                    onFilterSelect = { viewModel.setFilter(it) },
                    onSelectParticipant = { viewModel.selectParticipantForDetail(it) },
                    onAddNew = { showNewParticipantDialog = true }
                )
                5 -> FirebaseFirestoreTab(
                    viewModel = viewModel
                )
            }
        }
    }

    // Dialogs
    if (selectedParticipantForContract != null) {
        val p = selectedParticipantForContract!!
        val contractText = viewModel.getContractText(p, p.balance)
        AccessControlWizardDialog(
            participant = p,
            shoot = shoot,
            attendances = attendances,
            contractText = contractText,
            onCompleteCheckIn = { cash, locker, contractSigned ->
                viewModel.checkInParticipant(p.id, locker, cash, contractSigned)
                viewModel.selectParticipantForContract(null)
            },
            onDismiss = { viewModel.selectParticipantForContract(null) }
        )
    }

    if (selectedParticipantForDetail != null) {
        ParticipantEditorDialog(
            participant = selectedParticipantForDetail,
            onSave = { viewModel.saveParticipant(it) },
            onDelete = { viewModel.deleteParticipant(it) },
            onDismiss = { viewModel.selectParticipantForDetail(null) }
        )
    }

    if (showNewParticipantDialog) {
        ParticipantEditorDialog(
            participant = null,
            onSave = {
                viewModel.saveParticipant(it)
                showNewParticipantDialog = false
            },
            onDismiss = { showNewParticipantDialog = false }
        )
    }

    if (showTelegramPreDialog) {
        TelegramPreviewDialog(
            title = "Previsión de Rodaje (Telegram)",
            messageText = viewModel.getTelegramPreRodajeText(),
            onDismiss = { showTelegramPreDialog = false }
        )
    }

    if (showTelegramPostDialog) {
        TelegramPreviewDialog(
            title = "Balance Final de Rodaje (Telegram)",
            messageText = viewModel.getTelegramPostRodajeText(),
            onDismiss = { showTelegramPostDialog = false }
        )
    }

    if (showExportCsvDialog) {
        val context = LocalContext.current
        val csvText = viewModel.getCsvExportText()
        AlertDialog(
            onDismissRequest = { showExportCsvDialog = false },
            title = { Text("Exportar Datos (CSV / Excel)") },
            text = {
                Column {
                    Text("Los datos de todos los participantes han sido formateados en CSV compatible con Excel y Google Sheets.", fontSize = 13.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(8.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        Text(text = csvText, fontSize = 10.sp, fontFamily = FontFamily.Monospace)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Bukkakery CSV", csvText))
                        Toast.makeText(context, "CSV copiado al portapapeles!", Toast.LENGTH_SHORT).show()
                        showExportCsvDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MondayGreen)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copiar CSV")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showExportCsvDialog = false }) {
                    Text("Cerrar")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// TAB 1: PRERODAJE & CALCULO DE EFECTIVO
// -------------------------------------------------------------
@Composable
fun PreRodajeTab(
    shoot: ShootEventEntity?,
    participants: List<ParticipantEntity>,
    staff: List<StaffEntity>,
    onToggleConfirm: (Int, Boolean) -> Unit,
    onOpenTelegram: () -> Unit
) {
    val confirmedParticipants = participants.filter { it.isConfirmedForShoot }
    val totalBukkakeroCash = confirmedParticipants.sumOf { it.balance }
    val staffCashToPay = staff.filter { !it.isPaid }.sumOf { it.fee }
    val totalCashToWithdraw = totalBukkakeroCash + staffCashToPay
    val actressTransfer = shoot?.actressFee ?: 2000.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // KPI Summary Cards
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricKpiCard(
                title = "Total Efectivo a Retirar",
                value = "${totalCashToWithdraw.toInt()} €",
                subtitle = "${totalBukkakeroCash}€ bukkakeros + ${staffCashToPay.toInt()}€ staff",
                color = BrandCoral,
                icon = Icons.Default.MonetizationOn,
                modifier = Modifier.weight(1f)
            )
            MetricKpiCard(
                title = "Asistentes Confirmados",
                value = "${confirmedParticipants.size}",
                subtitle = "Previsión ~${confirmedParticipants.size * 2} corridas",
                color = MondayBlue,
                icon = Icons.Default.People,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Telegram Action Banner
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MondayBlueLight),
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
                    Text(
                        text = "Telegram Pre-Rodaje",
                        fontWeight = FontWeight.Bold,
                        color = MondayBlue,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Generar y enviar lista al grupo de Telegram con balances y analíticas.",
                        fontSize = 11.sp,
                        color = Color(0xFF003D7A)
                    )
                }
                Button(
                    onClick = onOpenTelegram,
                    colors = ButtonDefaults.buttonColors(containerColor = MondayBlue)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Generar Bot", fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Presupuesto Actriz & Staff
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "PRESUPUESTO ACTRIZ & STAFF TÉCNICO",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = BrandCoralDark
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Actriz Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(MondayPurpleLight)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "⭐ Actriz Principal: ${shoot?.actressName ?: "Kitty Love"}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MondayPurple
                        )
                        Text(
                            text = "Modo de pago: ${shoot?.actressPaymentMethod ?: "Transferencia"} (No requiere efectivo de caja)",
                            fontSize = 11.sp,
                            color = MondayPurple
                        )
                    }
                    Text(
                        text = "${actressTransfer.toInt()} €",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = MondayPurple
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Staff items list
                staff.forEach { s ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(text = s.role, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            if (s.notes.isNotBlank()) {
                                Text(text = s.notes, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        Text(
                            text = "${s.fee.toInt()} €",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = if (s.isPaid) MondayGreen else BrandCoral
                        )
                    }
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 0.5.dp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Confirmed Participants Checklist
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LISTA DE ASISTENCIA TELEGRAM (${confirmedParticipants.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Balance: ${totalBukkakeroCash} €",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandCoral
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                participants.forEach { p ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { onToggleConfirm(p.id, !p.isConfirmedForShoot) }
                            .padding(vertical = 4.dp, horizontal = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Checkbox(
                            checked = p.isConfirmedForShoot,
                            onCheckedChange = { onToggleConfirm(p.id, it) }
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "#${p.id} ${p.nick}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                if (p.telegramUser.isNotBlank()) {
                                    Text(
                                        text = "@${p.telegramUser}",
                                        fontSize = 11.sp,
                                        color = MondayBlue
                                    )
                                }
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                TestExpirationBadge(participant = p, referenceDate = shoot?.date ?: "31/08/2026")
                                Spacer(modifier = Modifier.width(6.dp))
                                if (p.qualifiesForFreeRenewal) {
                                    MondayStatusPill(text = "Renov. Gratis", backgroundColor = MondayPurple)
                                }
                            }
                        }
                        Text(
                            text = "${p.balance} €",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (p.balance > 0) BrandCoral else MondayGrey
                        )
                    }
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 0.5.dp)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 2: CONTROL DE ACCESO (EN RODAJE)
// -------------------------------------------------------------
@Composable
fun ControlAccesoTab(
    shoot: ShootEventEntity?,
    participants: List<ParticipantEntity>,
    attendances: List<AttendanceEntity>,
    onOpenContract: (ParticipantEntity) -> Unit
) {
    var query by remember { mutableStateOf("") }
    var showQrScannerDialog by remember { mutableStateOf(false) }
    val attendanceMap = attendances.associateBy { it.participantId }

    val checkedInCount = attendances.count { it.isPresent }
    val totalCashDelivered = attendances.sumOf { it.cashDeliveredAtEntry }
    val occupiedLockerCount = attendances.count { it.isPresent && it.lockerNumber != null }

    val searchResults = remember(query, participants) {
        if (query.isBlank()) {
            participants.sortedByDescending { it.isConfirmedForShoot }
        } else {
            participants.filter {
                it.nick.contains(query, ignoreCase = true) ||
                it.fullName.contains(query, ignoreCase = true) ||
                it.docNumber.contains(query, ignoreCase = true) ||
                it.id.toString() == query.trim()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Reception KPI Summary Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricKpiCard(
                title = "Presentes en Sala",
                value = "$checkedInCount",
                subtitle = "De ${participants.count { it.isConfirmedForShoot }} confirmados",
                color = MondayGreen,
                icon = Icons.Default.CheckCircle,
                modifier = Modifier.weight(1f)
            )
            MetricKpiCard(
                title = "Taquillas Asignadas",
                value = "$occupiedLockerCount / 90",
                subtitle = "${90 - occupiedLockerCount} disponibles",
                color = MondayBlue,
                icon = Icons.Default.Lock,
                modifier = Modifier.weight(1f)
            )
            MetricKpiCard(
                title = "Efectivo Cobrado",
                value = "$totalCashDelivered €",
                subtitle = "Entregado en mano",
                color = BrandCoral,
                icon = Icons.Default.MonetizationOn,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Access Initiation Header (2 ways: QR Scan & Nickname Search)
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "INICIAR CONTROL DE ACCESO (RECEPCIÓN)",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = BrandCoralDark
                )
                Text(
                    text = "Inicia el flujo de 4 pasos (Cobro de Balance → Contrato Imagen → Test Médico → Taquilla Única):",
                    fontSize = 10.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Option 1: QR Scanner button
                    Button(
                        onClick = { showQrScannerDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E293B)),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(
                            imageVector = Icons.Default.QrCodeScanner,
                            contentDescription = null,
                            tint = BrandCoral,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("1. Escanear Pase QR", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Quick Clear / Info indicator
                    if (query.isNotEmpty()) {
                        OutlinedButton(onClick = { query = "" }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpiar", modifier = Modifier.size(14.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Option 2: Search by Nickname / Doc
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = { Text("2. O escribe el Nickname, DNI o Nombre...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = BrandCoral) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "PARTICIPANTES DISPONIBLES PARA ACCESO (${searchResults.size}):",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(searchResults, key = { it.id }) { p ->
                val att = attendanceMap[p.id]
                val isCheckedIn = att?.isPresent == true

                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isCheckedIn) MondayGreenLight else MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
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
                                if (isCheckedIn) {
                                    MondayStatusPill(
                                        text = "Taquilla #${att?.lockerNumber}",
                                        backgroundColor = MondayGreen
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "${p.docType}: ${p.docNumber.ifBlank { "Sin doc" }} | ${p.fullName}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                TestExpirationBadge(participant = p, referenceDate = shoot?.date ?: "31/08/2026")
                                if (p.isBanned) {
                                    MondayStatusPill(text = "BANEADO", backgroundColor = MondayRed)
                                } else {
                                    MondayStatusPill(text = "Apto", backgroundColor = MondayGreen)
                                }
                                Text(
                                    text = "Balance: ${p.balance}€",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = BrandCoral
                                )
                            }
                        }

                        Button(
                            onClick = { onOpenContract(p) },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isCheckedIn) MondayGreen else BrandCoral
                            ),
                            enabled = !p.isBanned
                        ) {
                            Icon(
                                imageVector = if (isCheckedIn) Icons.Default.CheckCircle else Icons.Default.Description,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isCheckedIn) "Revisar" else "Iniciar Acceso", fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }

    if (showQrScannerDialog) {
        QrScannerDialog(
            participants = participants,
            onParticipantScanned = { scannedParticipant ->
                showQrScannerDialog = false
                onOpenContract(scannedParticipant)
            },
            onDismiss = { showQrScannerDialog = false }
        )
    }
}

// -------------------------------------------------------------
// TAB 3: LIVE RODAJE (CORRIDAS Y PREMIOS)
// -------------------------------------------------------------
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun LiveRodajeTab(
    shoot: ShootEventEntity?,
    attendances: List<AttendanceEntity>,
    participants: List<ParticipantEntity>,
    shotLogs: List<ShotLogEntity>,
    onRecordShot: (lockerNumber: Int, bonus: Int, reason: String) -> Unit,
    onDeleteLog: (id: Int, lockerNumber: Int, bonus: Int) -> Unit
) {
    var selectedLocker by remember { mutableStateOf(1) }
    var selectedBonus by remember { mutableStateOf(0) }
    var bonusReason by remember { mutableStateOf("Performance destacada") }
    var showBonusDialog by remember { mutableStateOf(false) }

    val participantsMap = remember(participants) { participants.associateBy { it.id } }
    val presentAttendances = attendances.filter { it.isPresent }
    val totalShots = shotLogs.size
    val totalPrizesCount = shotLogs.count { it.bonus > 0 }
    val totalEarned = (totalShots * 20) + shotLogs.sumOf { it.bonus }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
    ) {
        // Live Revenue Ticker
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricKpiCard(
                title = "Total Corridas",
                value = "$totalShots",
                subtitle = "Acumulado: $totalEarned €",
                color = BrandCoral,
                icon = Icons.Default.PlayArrow,
                modifier = Modifier.weight(1f)
            )
            MetricKpiCard(
                title = "Premios Otorgados",
                value = "$totalPrizesCount / 3 máx",
                subtitle = "Suplementos (+10€ / +20€)",
                color = MondayYellow,
                icon = Icons.Default.EmojiEvents,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Keypad / Locker Logger
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "REGISTRO RÁPIDO DE CORRIDAS (SELECCIONA TAQUILLA)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    TextButton(onClick = { showBonusDialog = true }) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = MondayYellow, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Añadir Premio", fontSize = 11.sp, color = MondayYellow)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Locker Grid (1..36 for present actors)
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val assignedLockers = presentAttendances.mapNotNull { it.lockerNumber }.sorted()
                    val lockersToShow = if (assignedLockers.isNotEmpty()) assignedLockers else (1..32).toList()

                    lockersToShow.forEach { lockerNum ->
                        val att = presentAttendances.find { it.lockerNumber == lockerNum }
                        val participant = if (att != null) participantsMap[att.participantId] else null
                        val shotsForLocker = att?.shotsCount ?: 0

                        Box(
                            modifier = Modifier
                                .size(width = 64.dp, height = 48.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (shotsForLocker > 0) MondayGreenLight else MondayGreyLight)
                                .border(
                                    width = 1.5.dp,
                                    color = if (shotsForLocker > 0) MondayGreen else Color.Transparent,
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    onRecordShot(lockerNum, 0, "")
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "T#$lockerNum",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = if (shotsForLocker > 0) MondayGreen else MondayDarkGrey
                                )
                                Text(
                                    text = "${shotsForLocker}x (${shotsForLocker * 20}€)",
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (shotsForLocker > 0) Color(0xFF006633) else MondayGrey
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Chronological Live Feed
        Text(
            text = "FEED CRONOLÓGICO DE CORRIDAS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(6.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(shotLogs, key = { it.id }) { log ->
                val p = participantsMap[log.participantId]
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (log.bonus > 0) MondayYellowLight else MaterialTheme.colorScheme.surface
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MondayStatusPill(
                                text = "T#${log.lockerNumber}",
                                backgroundColor = BrandCoral
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = p?.nick ?: "Participante #${log.participantId}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                if (log.bonus > 0) {
                                    Text(
                                        text = "⭐ PREMIO: +${log.bonus}€ (${log.bonusReason})",
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MondayYellow
                                    )
                                }
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "+${20 + log.bonus} €",
                                fontWeight = FontWeight.Black,
                                fontSize = 13.sp,
                                color = BrandCoral
                            )
                            IconButton(onClick = { onDeleteLog(log.id, log.lockerNumber, log.bonus) }) {
                                Icon(Icons.Default.Delete, contentDescription = "Eliminar", tint = MondayRed, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }

    // Performance Prize Dialog
    if (showBonusDialog) {
        AlertDialog(
            onDismissRequest = { showBonusDialog = false },
            title = { Text("Otorgar Premio Performance (Máx 3)") },
            text = {
                Column {
                    Text("Selecciona el casillero / taquilla y el valor del suplemento por desempeño:", fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = selectedLocker.toString(),
                        onValueChange = { selectedLocker = it.toIntOrNull() ?: 1 },
                        label = { Text("Nº Taquilla") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(selected = selectedBonus == 10, onClick = { selectedBonus = 10 })
                        Text("+10 € de Suplemento", fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        RadioButton(selected = selectedBonus == 20, onClick = { selectedBonus = 20 })
                        Text("+20 € de Suplemento", fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = bonusReason,
                        onValueChange = { bonusReason = it },
                        label = { Text("Motivo del premio") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val finalBonus = if (selectedBonus == 0) 10 else selectedBonus
                        onRecordShot(selectedLocker, finalBonus, bonusReason)
                        showBonusDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MondayYellow)
                ) {
                    Text("Asignar Premio")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showBonusDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

// -------------------------------------------------------------
// TAB 4: BALANCE POST-RODAJE & REPORTES
// -------------------------------------------------------------
@Composable
fun BalancePostRodajeTab(
    shoot: ShootEventEntity?,
    attendances: List<AttendanceEntity>,
    participants: List<ParticipantEntity>,
    staff: List<StaffEntity>,
    shotLogs: List<ShotLogEntity>,
    onOpenTelegram: () -> Unit,
    onExportCsv: () -> Unit,
    onToggleActressPaid: () -> Unit,
    onToggleStaffPaid: (StaffEntity) -> Unit
) {
    val participantsMap = remember(participants) { participants.associateBy { it.id } }
    val totalShots = shotLogs.size
    val totalPrizes = shotLogs.sumOf { it.bonus }
    val totalBukkakeroEarned = (totalShots * 20) + totalPrizes
    val avgPerShot = if (totalShots > 0) String.format("%.2f", totalBukkakeroEarned.toDouble() / totalShots) else "20.00"

    val totalStaff = staff.sumOf { it.fee }
    val actressFee = shoot?.actressFee ?: 2000.0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Header & Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "BALANCE FINAL & CIERRE DE SESIÓN",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
            Row {
                Button(
                    onClick = onExportCsv,
                    colors = ButtonDefaults.buttonColors(containerColor = MondayGreen)
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Excel/CSV", fontSize = 11.sp)
                }
                Spacer(modifier = Modifier.width(6.dp))
                Button(
                    onClick = onOpenTelegram,
                    colors = ButtonDefaults.buttonColors(containerColor = MondayBlue)
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Telegram", fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Financial Summary Matrix
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(text = "RESUMEN ECONÓMICO DEL RODAJE", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandCoralDark)
                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total Corridas registradas:", fontSize = 12.sp)
                    Text("$totalShots corridas", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Media de precio por corrida:", fontSize = 12.sp)
                    Text("$avgPerShot €", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MondayGreen)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Total generado bukkakeros:", fontSize = 12.sp)
                    Text("$totalBukkakeroEarned €", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = BrandCoral)
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Premios performance (suplementos):", fontSize = 12.sp)
                    Text("$totalPrizes €", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MondayYellow)
                }

                Divider(modifier = Modifier.padding(vertical = 8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Honorarios Actriz (${shoot?.actressName ?: "N/A"}):", fontSize = 12.sp)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("${actressFee.toInt()} €", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        MondayStatusPill(
                            text = if (shoot?.actressIsPaid == true) "PAGADO" else "PENDIENTE",
                            backgroundColor = if (shoot?.actressIsPaid == true) MondayGreen else MondayRed,
                            modifier = Modifier.clickable { onToggleActressPaid() }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Coste Staff & Extras:", fontSize = 12.sp)
                    Text("${totalStaff.toInt()} €", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Breakdown by Locker
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(text = "DESGLOSE POR TAQUILLA & PARTICIPANTE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))

                attendances.filter { it.isPresent }.sortedBy { it.lockerNumber }.forEach { att ->
                    val p = participantsMap[att.participantId]
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            MondayStatusPill(text = "T#${att.lockerNumber}", backgroundColor = MondayGreen)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(text = p?.nick ?: "N/A", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                Text(
                                    text = "${att.shotsCount} corridas (${att.shotsCount * 20}€) ${if (att.bonusPrizes > 0) "+${att.bonusPrizes}€ premio" else ""}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        Text(
                            text = "${(att.shotsCount * 20) + att.bonusPrizes} €",
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp,
                            color = BrandCoral
                        )
                    }
                    Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 0.5.dp)
                }
            }
        }
    }
}

// -------------------------------------------------------------
// TAB 5: BASE DE DATOS MONDAY & CONSOLA SQL
// -------------------------------------------------------------
@Composable
fun DatabaseSqlTab(
    viewModel: BukkakeryViewModel,
    participants: List<ParticipantEntity>,
    searchQuery: String,
    selectedFilter: ParticipantFilter,
    onSearchChange: (String) -> Unit,
    onFilterSelect: (ParticipantFilter) -> Unit,
    onSelectParticipant: (ParticipantEntity) -> Unit,
    onAddNew: () -> Unit
) {
    var showSqlConsole by remember { mutableStateOf(false) }
    var sqlText by remember { mutableStateOf("SELECT * FROM participants WHERE balance > 0 ORDER BY balance DESC") }

    Column(modifier = Modifier.fillMaxSize()) {
        // Search & SQL Toggle Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchChange,
                placeholder = { Text("Buscar en base de datos...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            IconButton(
                onClick = { showSqlConsole = !showSqlConsole },
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (showSqlConsole) MondayPurpleLight else MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Icon(
                    imageVector = Icons.Default.Storage,
                    contentDescription = "SQL Console",
                    tint = if (showSqlConsole) MondayPurple else MaterialTheme.colorScheme.onSurface
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Button(
                onClick = onAddNew,
                colors = ButtonDefaults.buttonColors(containerColor = BrandCoral),
                shape = RoundedCornerShape(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Nuevo Casting", tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Alta Casting", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        // SQL Query Drawer
        AnimatedVisibility(visible = showSqlConsole) {
            Card(
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("CONSOLA DE CONSULTAS SQL (ROOM / SQLITE)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MondayPurple)
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = sqlText,
                        onValueChange = { sqlText = it },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        textStyle = androidx.compose.ui.text.TextStyle(fontFamily = FontFamily.Monospace, fontSize = 11.sp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            TextButton(onClick = {
                                sqlText = "SELECT * FROM participants WHERE renewalStatus LIKE '%gratis%'"
                                viewModel.runSqlQuery(sqlText)
                            }) {
                                Text("Gratis", fontSize = 10.sp)
                            }
                            TextButton(onClick = {
                                sqlText = "SELECT * FROM participants WHERE status LIKE '%BANEADO%'"
                                viewModel.runSqlQuery(sqlText)
                            }) {
                                Text("Baneados", fontSize = 10.sp)
                            }
                        }
                        Button(
                            onClick = { viewModel.runSqlQuery(sqlText) },
                            colors = ButtonDefaults.buttonColors(containerColor = MondayPurple)
                        ) {
                            Text("Ejecutar SQL", fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // Filters row
        FilterPillsRow(
            selectedFilter = selectedFilter,
            onFilterSelected = onFilterSelect
        )

        // Monday-like Data Table
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(participants, key = { it.id }) { p ->
                Card(
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                        .clickable { onSelectParticipant(p) }
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "#${p.id}",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = p.nick,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                            Text(
                                text = "${p.balance} €",
                                fontWeight = FontWeight.Black,
                                fontSize = 14.sp,
                                color = BrandCoral
                            )
                        }

                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "${p.docType} ${p.docNumber.ifBlank { "N/A" }} | ${p.fullName.ifBlank { "Sin nombre" }} | Tel: ${p.phone.ifBlank { "N/A" }}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TestExpirationBadge(participant = p)
                            MondayStatusPill(
                                text = p.testResult,
                                backgroundColor = if (p.testResult == "NEGATIVO") MondayGreen else MondayRed
                            )
                            if (p.qualifiesForFreeRenewal) {
                                MondayStatusPill(text = "Renov. Gratis", backgroundColor = MondayPurple)
                            }
                            if (p.isBanned) {
                                MondayStatusPill(text = "BANEADO", backgroundColor = MondayRed)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FirebaseFirestoreTab(
    viewModel: BukkakeryViewModel,
    modifier: Modifier = Modifier
) {
    val syncInfo by viewModel.cloudSyncInfo.collectAsState()
    val participants by viewModel.allParticipants.collectAsState()
    val shoot by viewModel.currentShoot.collectAsState()
    val attendances by viewModel.attendances.collectAsState()
    val shotLogs by viewModel.shotLogs.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(12.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Hero Header Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = MondayPurple),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(26.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "FIREBASE FIRESTORE CLOUD",
                            fontWeight = FontWeight.Black,
                            fontSize = 15.sp,
                            color = Color.White
                        )
                        Text(
                            text = "Base de datos en la nube & Seguridad de Escritura",
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.85f)
                        )
                    }
                }

                val statusColor = when (syncInfo.state) {
                    CloudSyncState.SYNCED -> MondayGreen
                    CloudSyncState.SYNCING -> MondayOrange
                    CloudSyncState.ERROR -> MondayRed
                    CloudSyncState.IDLE -> MondayBlue
                    CloudSyncState.OFFLINE -> Color.Gray
                }
                MondayStatusPill(
                    text = when (syncInfo.state) {
                        CloudSyncState.SYNCED -> "SINCRONIZADO"
                        CloudSyncState.SYNCING -> "EN PROCESO..."
                        CloudSyncState.ERROR -> "FALLO / ERROR"
                        CloudSyncState.IDLE -> "EN ESPERA"
                        CloudSyncState.OFFLINE -> "OFFLINE"
                    },
                    backgroundColor = statusColor
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Status & Messages
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (syncInfo.state == CloudSyncState.SYNCED) Icons.Default.CheckCircle else Icons.Default.Storage,
                        contentDescription = null,
                        tint = if (syncInfo.state == CloudSyncState.SYNCED) MondayGreen else MondayBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Estado del Servicio Cloud",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = syncInfo.message,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (syncInfo.lastSyncTimestamp > 0) {
                    Spacer(modifier = Modifier.height(4.dp))
                    val dateFormatted = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.getDefault())
                        .format(java.util.Date(syncInfo.lastSyncTimestamp))
                    Text(
                        text = "Última sincronización con Firestore: $dateFormatted",
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium,
                        color = MondayPurple
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Actions & Controls
        Text(
            text = "OPERACIONES DE ESCRITURA Y SINCRONIZACIÓN",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { viewModel.syncWithFirestoreCloud() },
                colors = ButtonDefaults.buttonColors(containerColor = BrandCoral),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.CloudUpload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Subir a Firestore (Admin Write)", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = { viewModel.pullFromFirestoreCloud() },
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                Icon(Icons.Default.CloudDownload, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Descargar de Nube", fontSize = 11.5.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Security Policies Summary (RBAC & Write Protection)
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = null,
                        tint = MondayRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Políticas de Seguridad Firestore (firestore.rules)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                FirestorePolicyRow(
                    collectionName = "/participants/{id}",
                    description = "Fichas, balances y castings de usuarios",
                    readRule = "Autenticados",
                    writeRule = "🔒 EXCLUSIVO ADMIN (Lab solo actualiza test)",
                    isWriteCritical = true
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))

                FirestorePolicyRow(
                    collectionName = "/shoots/{shootCode}",
                    description = "Eventos de rodaje, actrices, cachés y presupuesto",
                    readRule = "Autenticados",
                    writeRule = "🔒 EXCLUSIVO ADMIN",
                    isWriteCritical = true
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))

                FirestorePolicyRow(
                    collectionName = "/attendances/{id}",
                    description = "Control de entrada, taquillas asignadas y balances",
                    readRule = "Autenticados",
                    writeRule = "🔒 EXCLUSIVO ADMIN",
                    isWriteCritical = true
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))

                FirestorePolicyRow(
                    collectionName = "/shot_logs/{id}",
                    description = "Registro histórico de corridas y premios (10€/20€)",
                    readRule = "Autenticados",
                    writeRule = "🔒 EXCLUSIVO ADMIN",
                    isWriteCritical = true
                )
                Divider(modifier = Modifier.padding(vertical = 8.dp))

                FirestorePolicyRow(
                    collectionName = "/comments/{id}",
                    description = "Notas y comentarios de soporte",
                    readRule = "Autenticados",
                    writeRule = "Escritura: Usuario / Borrado: Admin",
                    isWriteCritical = false
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Spacer(modifier = Modifier.height(16.dp))

        // FIREBASE AUTH & ROLE MANAGEMENT PROVISIONER
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            val authState by viewModel.authState.collectAsState()
            var newEmail by remember { mutableStateOf("") }
            var newPass by remember { mutableStateOf("") }
            var newDisplayName by remember { mutableStateOf("") }
            var selectedNewRole by remember { mutableStateOf(UserRole.PARTICIPANT) }
            var selectedParticipantId by remember { mutableStateOf<Int?>(null) }

            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.VerifiedUser,
                        contentDescription = null,
                        tint = MondayPurple,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Gestión de Usuarios y Roles en Firebase Auth (/users)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Alta de credenciales con asignación de rol RBAC (Administrador, Laboratorio o Participante) en Firebase Authentication y Firestore.",
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                if (authState.userProfile != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MondayPurple.copy(alpha = 0.1f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "Sesión Activa: ${authState.userProfile?.email}",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.5.sp,
                                    color = MondayPurple
                                )
                                Text(
                                    text = "UID: ${authState.userProfile?.uid} • Rol: ${authState.userProfile?.role?.label}",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            MondayStatusPill(
                                text = authState.userProfile?.role?.name ?: "UNKNOWN",
                                backgroundColor = when (authState.userProfile?.role) {
                                    UserRole.ADMIN -> MondayPurple
                                    UserRole.LABORATORY -> MondayBlue
                                    else -> BrandCoral
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = newEmail,
                    onValueChange = { newEmail = it },
                    label = { Text("Correo Electrónico (Firebase Auth)") },
                    placeholder = { Text("ej: usuario@bukkakery.com") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newPass,
                        onValueChange = { newPass = it },
                        label = { Text("Contraseña") },
                        placeholder = { Text("Mínimo 6 caracteres") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = newDisplayName,
                        onValueChange = { newDisplayName = it },
                        label = { Text("Nombre / Alias") },
                        placeholder = { Text("ej: Zeus o Dr. Gómez") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "ROL ASIGNADO EN FIRESTORE:",
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    FilterChip(
                        selected = selectedNewRole == UserRole.PARTICIPANT,
                        onClick = { selectedNewRole = UserRole.PARTICIPANT },
                        label = { Text("Participante", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedNewRole == UserRole.ADMIN,
                        onClick = { selectedNewRole = UserRole.ADMIN },
                        label = { Text("Administrador", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                    FilterChip(
                        selected = selectedNewRole == UserRole.LABORATORY,
                        onClick = { selectedNewRole = UserRole.LABORATORY },
                        label = { Text("Laboratorio", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        if (newEmail.isNotBlank() && newPass.isNotBlank()) {
                            viewModel.createFirebaseAuthUser(
                                email = newEmail.trim(),
                                pass = newPass.trim(),
                                role = selectedNewRole,
                                participantId = selectedParticipantId,
                                displayName = newDisplayName.trim()
                            )
                            newEmail = ""
                            newPass = ""
                            newDisplayName = ""
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MondayPurple),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth(),
                    enabled = newEmail.isNotBlank() && newPass.length >= 6
                ) {
                    Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("REGISTRAR USUARIO Y CREAR ROL EN FIRESTORE", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // CLOUD FUNCTIONS MEDICAL EXPIRATIONS AUTOMATION CARD
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
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = MondayOrange,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Cloud Functions: Alertas de Caducidad Médica",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }

                    MondayStatusPill(text = "Cron Diario 09:00", backgroundColor = MondayGreen)
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Script serverless que analiza diariamente las analíticas de participantes y envía notificaciones escalonadas antes de su vencimiento.",
                    fontSize = 11.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    NotificationTierBadge(days = "12d", label = "Preventivo", color = MondayOrange, modifier = Modifier.weight(1f))
                    NotificationTierBadge(days = "7d", label = "Aviso 1 Sem.", color = Color(0xFFFF8533), modifier = Modifier.weight(1f))
                    NotificationTierBadge(days = "4d", label = "Urgente", color = Color(0xFFFF5522), modifier = Modifier.weight(1f))
                    NotificationTierBadge(days = "2d", label = "Crítico (48h)", color = MondayRed, modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = { viewModel.runMedicalExpirationsCloudAudit() },
                    colors = ButtonDefaults.buttonColors(containerColor = MondayOrange),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("EJECUTAR AUDITORÍA DE CADUCIDADES EN VIVO", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Data Metrics in Memory ready for Cloud
        Text(
            text = "VOLUMEN DE DATOS LISTOS PARA CLOUD",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MetricMiniCard(title = "Participantes", value = "${participants.size}", color = MondayBlue, modifier = Modifier.weight(1f))
            MetricMiniCard(title = "Rodajes", value = if (shoot != null) "1" else "0", color = MondayPurple, modifier = Modifier.weight(1f))
            MetricMiniCard(title = "Asistencias", value = "${attendances.size}", color = MondayOrange, modifier = Modifier.weight(1f))
            MetricMiniCard(title = "Logs Corridas", value = "${shotLogs.size}", color = MondayGreen, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun FirestorePolicyRow(
    collectionName: String,
    description: String,
    readRule: String,
    writeRule: String,
    isWriteCritical: Boolean
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = collectionName,
                fontWeight = FontWeight.Bold,
                fontSize = 12.5.sp,
                color = MondayPurple
            )
            MondayStatusPill(
                text = if (isWriteCritical) "Admin Only" else "Restringido",
                backgroundColor = if (isWriteCritical) MondayRed else MondayOrange
            )
        }
        Text(
            text = description,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Lectura: $readRule",
                fontSize = 10.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = writeRule,
                fontSize = 10.5.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isWriteCritical) MondayRed else MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun MetricMiniCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.1f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp,
                color = color
            )
            Text(
                text = title,
                fontSize = 9.5.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun NotificationTierBadge(
    days: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f)),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(vertical = 6.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = days,
                fontWeight = FontWeight.Black,
                fontSize = 13.sp,
                color = color
            )
            Text(
                text = label,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = color,
                textAlign = TextAlign.Center
            )
        }
    }
}

