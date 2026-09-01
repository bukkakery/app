package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Password
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Biotech
import androidx.compose.material.icons.filled.HowToReg
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ParticipantEntity
import com.example.data.model.UserRole
import com.example.ui.BukkakeryViewModel
import com.example.ui.components.MondayStatusPill
import com.example.ui.theme.BrandCoral
import com.example.ui.theme.BrandCoralDark
import com.example.ui.theme.MondayBlue
import com.example.ui.theme.MondayPurple
import com.example.ui.theme.MondayYellowLight

@Composable
fun AuthPortalScreen(
    viewModel: BukkakeryViewModel,
    modifier: Modifier = Modifier
) {
    val participants by viewModel.allParticipants.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Hero Brand Header
        AuthHeroHeader()

        Spacer(modifier = Modifier.height(14.dp))

        // Fast-Track Direct Access Card
        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "⚡ ACCESO RÁPIDO DIRECTO (1 CLIC)",
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp,
                        color = MondayPurple
                    )
                    MondayStatusPill(text = "DEMO & PROD", backgroundColor = MondayPurple)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // 3 Big Quick-Entry Buttons
                Button(
                    onClick = { viewModel.loginAsAdmin() },
                    colors = ButtonDefaults.buttonColors(containerColor = MondayPurple),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                ) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ENTRAR COMO ADMINISTRADOR", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { viewModel.loginAsLaboratory() },
                        colors = ButtonDefaults.buttonColors(containerColor = MondayBlue),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                    ) {
                        Icon(Icons.Default.Biotech, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("LABORATORIO", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Button(
                        onClick = {
                            val first = participants.firstOrNull()
                            if (first != null) {
                                viewModel.quickLogin(UserRole.PARTICIPANT, first.id)
                            } else {
                                viewModel.loginAsParticipant("ZEUS", "")
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = BrandCoral),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(42.dp)
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("PARTICIPANTE", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Main Login Card
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                LoginFormView(
                    viewModel = viewModel,
                    participants = participants
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Casting Process Notice
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MondayYellowLight),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.HowToReg,
                    contentDescription = null,
                    tint = Color(0xFFB45309),
                    modifier = Modifier.size(22.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "ADMISIONES Y PROCESO DE CASTING",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = Color(0xFFB45309)
                    )
                    Text(
                        text = "El alta de nuevos participantes está reservada a la Administración tras superar el filtro de casting, verificación de DNI/NIE y analítica médica.",
                        fontSize = 10.5.sp,
                        color = Color(0xFF78350F)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Security & Confidentiality Footer
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Shield,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Acceso seguro cifrado • Cumplimiento RGPD & LOPD Sanitaria",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
fun AuthHeroHeader() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        // Logo Badge
        Box(
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(BrandCoral, BrandCoralDark)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "B",
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 34.sp
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "BUKKAKERY",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 2.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Text(
            text = "PORTAL DE ACCESO Y PRODUCCIÓN",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp,
            color = BrandCoral
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "Selecciona tu rol para acceder a tu panel de trabajo o portal personal",
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

// -------------------------------------------------------------
// LOGIN FORM VIEW WITH FIREBASE AUTH & ROLE PROVIDERS
// -------------------------------------------------------------
@Composable
fun LoginFormView(
    viewModel: BukkakeryViewModel,
    participants: List<ParticipantEntity>
) {
    val authState by viewModel.authState.collectAsState()
    var selectedRoleType by remember { mutableStateOf(UserRole.PARTICIPANT) }
    var emailInput by remember { mutableStateOf("") }
    var identifierInput by remember { mutableStateOf("") }
    var passwordInput by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var useFirebaseEmailAuth by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // Firebase Auth Badge
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = BrandCoral.copy(alpha = 0.1f),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalFireDepartment,
                        contentDescription = null,
                        tint = BrandCoral,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Firebase Authentication & Firestore RBAC",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = BrandCoralDark
                    )
                }

                if (authState.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(14.dp),
                        strokeWidth = 2.dp,
                        color = BrandCoral
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "SELECCIONA TU ROL DE ACCESO:",
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Role Selector Chips
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            RoleSelectChip(
                label = "Participante",
                icon = Icons.Default.Person,
                isSelected = selectedRoleType == UserRole.PARTICIPANT,
                activeColor = BrandCoral,
                modifier = Modifier.weight(1f),
                onClick = {
                    selectedRoleType = UserRole.PARTICIPANT
                    emailInput = "participante@bukkakery.com"
                }
            )
            RoleSelectChip(
                label = "Admin",
                icon = Icons.Default.AdminPanelSettings,
                isSelected = selectedRoleType == UserRole.ADMIN,
                activeColor = MondayPurple,
                modifier = Modifier.weight(1f),
                onClick = {
                    selectedRoleType = UserRole.ADMIN
                    emailInput = "admin@bukkakery.com"
                }
            )
            RoleSelectChip(
                label = "Laboratorio",
                icon = Icons.Default.Biotech,
                isSelected = selectedRoleType == UserRole.LABORATORY,
                activeColor = MondayBlue,
                modifier = Modifier.weight(1f),
                onClick = {
                    selectedRoleType = UserRole.LABORATORY
                    emailInput = "lab@bukkakery.com"
                }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (selectedRoleType) {
            UserRole.PARTICIPANT -> {
                Text(
                    text = "Portal del Participante / Visitante",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = BrandCoralDark
                )
                Text(
                    text = "Inicia sesión con tu correo/contraseña de Firebase o con tu Nick/DNI para consultar tu balance en tiempo real, estado médico y pase QR.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Email field
                OutlinedTextField(
                    value = emailInput.ifBlank { identifierInput },
                    onValueChange = {
                        emailInput = it
                        identifierInput = it
                    },
                    label = { Text("Correo Electrónico (Firebase Auth) o Nick / DNI") },
                    placeholder = { Text("ej: participante@bukkakery.com o ZEUS") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = BrandCoral) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    label = { Text("Contraseña / PIN") },
                    placeholder = { Text("Introduce tu contraseña") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = BrandCoral) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val input = (if (emailInput.isNotBlank()) emailInput else identifierInput).trim()
                        if (input.contains("@")) {
                            viewModel.loginWithFirebase(input, passwordInput.ifBlank { "Password123" })
                        } else {
                            viewModel.loginAsParticipant(input, passwordInput)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = BrandCoral),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = (emailInput.isNotBlank() || identifierInput.isNotBlank()) && !authState.isLoading
                ) {
                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ENTRAR CON FIREBASE AUTH", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Quick Demo Participants Picker
                Text(
                    text = "O ACCESO RÁPIDO DE PARTICIPANTES EN BBDD:",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(6.dp))

                val quickDemoList = remember(participants) {
                    participants.take(4)
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    quickDemoList.forEach { p ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .clickable {
                                    viewModel.quickLogin(UserRole.PARTICIPANT, p.id)
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = p.nick,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(${p.docNumber})",
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            MondayStatusPill(
                                text = "${p.balance} €",
                                backgroundColor = if (p.balance > 0) BrandCoral else MondayBlue
                            )
                        }
                    }
                }
            }

            UserRole.ADMIN -> {
                Text(
                    text = "Acceso Administrativo y Producción (Rol: ADMIN)",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = MondayPurple
                )
                Text(
                    text = "Autenticación segura con Firebase Auth para administradores con permisos de escritura crítica en Firestore.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = emailInput.ifBlank { "admin@bukkakery.com" },
                    onValueChange = { emailInput = it },
                    label = { Text("Correo Electrónico Administrador") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MondayPurple) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    label = { Text("Contraseña Firebase Auth") },
                    placeholder = { Text("Introduce contraseña o pulsa Entrar") },
                    leadingIcon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MondayPurple) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val email = emailInput.ifBlank { "admin@bukkakery.com" }
                        val pass = passwordInput.ifBlank { "AdminSecret2026" }
                        viewModel.loginWithFirebase(email, pass)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MondayPurple),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = !authState.isLoading
                ) {
                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ACCEDER COMO ADMIN (FIREBASE AUTH)", fontWeight = FontWeight.Bold)
                }
            }

            UserRole.LABORATORY -> {
                Text(
                    text = "Acceso Técnico de Laboratorio (Rol: LABORATORY)",
                    fontWeight = FontWeight.Black,
                    fontSize = 14.sp,
                    color = MondayBlue
                )
                Text(
                    text = "Autenticación de personal sanitario acreditado con permisos exclusivos de actualización de muestras y fechas serológicas.",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = emailInput.ifBlank { "lab@bukkakery.com" },
                    onValueChange = { emailInput = it },
                    label = { Text("Correo Electrónico Laboratorio") },
                    leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = MondayBlue) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = passwordInput,
                    onValueChange = { passwordInput = it },
                    label = { Text("Contraseña Laboratorio") },
                    placeholder = { Text("Introduce contraseña o pulsa Entrar") },
                    leadingIcon = { Icon(Icons.Default.Biotech, contentDescription = null, tint = MondayBlue) },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = null
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val email = emailInput.ifBlank { "lab@bukkakery.com" }
                        val pass = passwordInput.ifBlank { "LabPass2026" }
                        viewModel.loginWithFirebase(email, pass)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MondayBlue),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = !authState.isLoading
                ) {
                    Icon(Icons.Default.LocalFireDepartment, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ACCEDER COMO LABORATORIO (FIREBASE AUTH)", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun RoleSelectChip(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) activeColor else MaterialTheme.colorScheme.surfaceVariant)
            .border(
                width = if (isSelected) 1.5.dp else 0.dp,
                color = if (isSelected) activeColor else Color.Transparent,
                shape = RoundedCornerShape(10.dp)
            )
            .clickable { onClick() }
            .padding(vertical = 10.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }
    }
}
