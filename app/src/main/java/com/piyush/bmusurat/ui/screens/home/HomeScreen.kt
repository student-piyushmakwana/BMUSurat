package com.piyush.bmusurat.ui.screens.home

import android.content.Intent
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.rounded.BrokenImage
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.piyush.bmusurat.data.models.HomeResponse
import com.piyush.bmusurat.ui.screens.home.components.ExpandableInfoSection
import com.piyush.bmusurat.ui.screens.home.components.InfoItem
import com.piyush.bmusurat.ui.screens.home.components.InstitutionCard
import com.piyush.bmusurat.ui.screens.home.components.SectionTitle
import com.piyush.bmusurat.ui.screens.home.components.ShimmerBox
import com.piyush.bmusurat.ui.screens.home.components.TestimonialCard
import com.piyush.bmusurat.ui.screens.home.components.institutionList
import kotlinx.coroutines.CoroutineScope
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

@Composable
fun NoInternetScreen() {
    androidx.compose.foundation.layout.Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(32.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.WifiOff,
            contentDescription = "No Internet",
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "Your signal’s gone !",
            style = MaterialTheme.typography.headlineSmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Network’s back—give it another try.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
fun ErrorScreen(
    message: String,
    onRetry: () -> Unit
) {
    androidx.compose.foundation.layout.Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(24.dp)
    ) {
        Icon(
            imageVector = Icons.Rounded.Warning,
            contentDescription = "Error",
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "Oops! Something went wrong",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurface
        )
        Log.e("HomeScreen", "ErrorScreen: $message")
        Spacer(Modifier.height(24.dp))
        FilledTonalButton(onClick = onRetry) {
            Text("Retry")
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeDataContent(
    data: HomeResponse,
    snackBarHostState: SnackbarHostState,
    scope: CoroutineScope,
    navController: NavController
) {
    val context = LocalContext.current

    val openUrl = { url: String ->
        try {
            if (url.isBlank() || !url.startsWith("http")) {
                scope.launch {
                    snackBarHostState.showSnackbar("No valid link available")
                }
                Log.w("HomeScreen", "Invalid URL: $url")
            } else {
                val intent = Intent(Intent.ACTION_VIEW, url.toUri())
                context.startActivity(intent)
                Log.d("HomeScreen", "Opening URL: $url")
            }
        } catch (e: Exception) {
            Log.e("HomeScreen", "Failed to open URL: $url", e)
            scope.launch {
                snackBarHostState.showSnackbar("Could not open link browser")
            }
        }
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            SubcomposeAsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(data.imageUrl)
                    .crossfade(true)
                    .build(),
                contentDescription = "BMU Header Image",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop,
                loading = {
                    ShimmerBox(modifier = Modifier.fillMaxSize())
                },
                error = {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.BrokenImage,
                            contentDescription = "Image load error",
                            modifier = Modifier.size(48.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            )
            Spacer(Modifier.height(16.dp))
        }

        item {
            FilledTonalButton(
                onClick = {
                    Log.d("HomeScreen", "Student Sign-in Button clicked")
                },
                shapes = ButtonDefaults.shapes(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .height(50.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Login,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(Modifier.width(8.dp))
                Text(text = "Student Sign In", fontWeight = FontWeight.Medium)
            }
            Spacer(Modifier.height(24.dp))
        }

        item {
            SectionTitle(
                title = "Our Institutions",
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(institutionList) { institution ->
                    InstitutionCard(
                        institution = institution,
                        onClick = {}
                    )
                }
            }
            Spacer(Modifier.height(24.dp))
        }

        item {
            val newsItems = data.data.latestNews.map {
                InfoItem(date = it.date, description = it.description, link = it.link)
            }
            if (newsItems.isNotEmpty()) {
                ExpandableInfoSection(
                    title = "Latest News",
                    items = newsItems,
                    onItemClick = { openUrl(it.link) }
                )
                Spacer(Modifier.height(24.dp))
            }
        }

        item {
            val eventItems = data.data.upcomingEvents.map {
                InfoItem(date = it.date, description = it.description, link = it.link)
            }
            if (eventItems.isNotEmpty()) {
                ExpandableInfoSection(
                    title = "Upcoming Events",
                    items = eventItems,
                    onItemClick = { openUrl(it.link) }
                )
                Spacer(Modifier.height(24.dp))
            }
        }

        item {
            if (data.data.studentTestimonials.isNotEmpty()) {
                SectionTitle(
                    title = "Student Testimonials",
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(data.data.studentTestimonials) { testimonial ->
                        TestimonialCard(testimonial = testimonial)
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}