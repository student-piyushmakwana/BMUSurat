package com.piyush.bmusurat.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.SignalWifiBad
import androidx.compose.material.icons.rounded.Wifi
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.piyush.bmusurat.ui.screens.home.components.ErrorScreen
import com.piyush.bmusurat.ui.screens.home.components.HomeDataContent
import com.piyush.bmusurat.ui.screens.home.components.IconSnackBar
import com.piyush.bmusurat.ui.screens.home.components.NoInternetScreen
import com.piyush.bmusurat.ui.screens.home.components.SignInBottomSheet
import com.piyush.bmusurat.ui.screens.home.components.WavyProgressIndicator
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val snackBarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()
    var currentSnackIcon by remember { mutableStateOf(Icons.Rounded.Info) }
    val pullToRefreshState = rememberPullToRefreshState()
    var isFirstNetworkCheck by remember { mutableStateOf(true) }

    // Bottom Sheet State
    var showSignInSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    LaunchedEffect(uiState.isNetworkAvailable) {
        if (isFirstNetworkCheck) {
            isFirstNetworkCheck = false
            if (!uiState.isNetworkAvailable && uiState.data != null) {
                currentSnackIcon = Icons.Rounded.SignalWifiBad
                scope.launch {
                    snackBarHostState.showSnackbar("Your device is not connected to the internet.")
                }
            }
            return@LaunchedEffect
        }

        if (uiState.isNetworkAvailable) {
            currentSnackIcon = Icons.Rounded.Wifi
            scope.launch {
                snackBarHostState.currentSnackbarData?.dismiss()
                snackBarHostState.showSnackbar("You're back online.")
            }
        } else {
            if (uiState.data != null) {
                currentSnackIcon = Icons.Rounded.WifiOff
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar("You're offline right now.")
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(hostState = snackBarHostState,modifier = Modifier.padding(horizontal = 16.dp)) { data ->
                IconSnackBar(
                    icon = currentSnackIcon,
                    message = data.visuals.message
                )
            }
        },
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Bhagwan Mahavir University",
                        style = MaterialTheme.typography.headlineSmallEmphasized,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f)
                )
            )
        }
    ) { innerPadding ->
        val isRefreshing = uiState.isLoading && uiState.data != null

        PullToRefreshBox(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.loadHomeData() },
            state = pullToRefreshState,
            indicator = {
                PullToRefreshDefaults.Indicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    state = pullToRefreshState,
                    isRefreshing = isRefreshing
                )
            }
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    uiState.isLoading && uiState.data == null -> {
                        WavyProgressIndicator(modifier = Modifier.size(52.dp))
                    }

                    uiState.data != null -> {
                        HomeDataContent(
                            data = uiState.data!!,
                            snackBarHostState = snackBarHostState,
                            scope = scope,
                            navController = navController,
                            onSignInClick = { showSignInSheet = true }
                        )
                    }

                    !uiState.isNetworkAvailable -> {
                        NoInternetScreen()
                    }

                    uiState.error != null -> {
                        ErrorScreen(
                            message = uiState.error ?: "Unknown Error",
                            onRetry = { viewModel.loadHomeData() }
                        )
                    }
                }
            }
        }

        if (showSignInSheet) {
            SignInBottomSheet(
                onDismissRequest = { showSignInSheet = false },
                sheetState = sheetState,
                onGoogleSignInClick = {
                    // TODO: Implement Google Sign In logic
                    showSignInSheet = false
                },
                onStudentIdClick = {
                    // TODO: Implement Student ID Sign In logic
                    showSignInSheet = false
                }
            )
        }
    }
}