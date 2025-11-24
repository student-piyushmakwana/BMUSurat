package com.piyush.bmusurat.ui.screens.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularWavyProgressIndicator
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.piyush.bmusurat.ui.screens.home.components.ErrorScreen
import com.piyush.bmusurat.ui.screens.home.components.HomeDataContent
import com.piyush.bmusurat.ui.screens.home.components.NoInternetScreen
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

    val pullToRefreshState = rememberPullToRefreshState()

    var isFirstNetworkCheck by remember { mutableStateOf(true) }

    LaunchedEffect(uiState.isNetworkAvailable) {
        if (isFirstNetworkCheck) {
            isFirstNetworkCheck = false
            if (!uiState.isNetworkAvailable && uiState.data != null) {
                scope.launch {
                    snackBarHostState.showSnackbar(
                        message = "Your device is not connected to the internet.",
                        withDismissAction = true
                    )
                }
            }
            return@LaunchedEffect
        }

        if (uiState.isNetworkAvailable) {
            scope.launch {
                snackBarHostState.currentSnackbarData?.dismiss()
                snackBarHostState.showSnackbar(
                    message = "Seems like you’re connected now.",
                    withDismissAction = true
                )
            }
        } else {
            if (uiState.data != null) {
                scope.launch {
                    snackBarHostState.currentSnackbarData?.dismiss()
                    snackBarHostState.showSnackbar(
                        message = "Looks like your connection is down.",
                        withDismissAction = true
                    )
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackBarHostState) },
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
                        val thickStrokeWidth = with(LocalDensity.current) { 8.dp.toPx() }
                        val thickStroke = remember(thickStrokeWidth) {
                            Stroke(width = thickStrokeWidth, cap = StrokeCap.Round)
                        }
                        CircularWavyProgressIndicator(
                            modifier = Modifier.size(62.dp),
                            stroke = thickStroke,
                            trackStroke = thickStroke
                        )
                    }

                    uiState.data != null -> {
                        HomeDataContent(
                            data = uiState.data!!,
                            snackBarHostState = snackBarHostState,
                            scope = scope,
                            navController = navController
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
    }
}