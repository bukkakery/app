package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.data.model.UserRole
import com.example.ui.BukkakeryViewModel
import com.example.ui.components.AppBrandHeader
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AuthPortalScreen
import com.example.ui.screens.LaboratoryScreen
import com.example.ui.screens.ParticipantPortalScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    private val viewModel: BukkakeryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                BukkakeryApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun BukkakeryApp(viewModel: BukkakeryViewModel) {
    val isLoggedIn by viewModel.isLoggedIn.collectAsState()
    val currentRole by viewModel.currentRole.collectAsState()
    val shoot by viewModel.currentShoot.collectAsState()
    val authenticatedParticipant by viewModel.authenticatedParticipant.collectAsState()
    val authenticatedStaffName by viewModel.authenticatedStaffName.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.snackBarMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (isLoggedIn) {
                AppBrandHeader(
                    currentRole = currentRole,
                    shoot = shoot,
                    authenticatedParticipant = authenticatedParticipant,
                    authenticatedStaffName = authenticatedStaffName,
                    onLogout = { viewModel.logout() }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (!isLoggedIn) {
                AuthPortalScreen(viewModel = viewModel)
            } else {
                when (currentRole) {
                    UserRole.ADMIN -> AdminDashboardScreen(viewModel = viewModel)
                    UserRole.LABORATORY -> LaboratoryScreen(viewModel = viewModel)
                    UserRole.PARTICIPANT -> ParticipantPortalScreen(viewModel = viewModel)
                }
            }
        }
    }
}

