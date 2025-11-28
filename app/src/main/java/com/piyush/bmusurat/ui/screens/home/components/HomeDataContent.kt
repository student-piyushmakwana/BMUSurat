package com.piyush.bmusurat.ui.screens.home.components

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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import androidx.navigation.NavController
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.piyush.bmusurat.data.models.HomeResponse
import com.piyush.bmusurat.navigation.Screen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomeDataContent(
    data: HomeResponse,
    snackBarHostState: SnackbarHostState,
    scope: CoroutineScope,
    navController: NavController,
    onSignInClick: () -> Unit
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
                onClick = onSignInClick,
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
                Text(
                    text = "Student Sign In",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyLargeEmphasized
                )

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
                        onClick = {
                            navController.navigate(Screen.ProgramDetail.createRoute(institution.shortName))
                        }
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