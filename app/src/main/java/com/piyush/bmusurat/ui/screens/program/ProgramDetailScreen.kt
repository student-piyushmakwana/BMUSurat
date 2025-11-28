package com.piyush.bmusurat.ui.screens.program

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil3.compose.SubcomposeAsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.piyush.bmusurat.data.models.GalleryAlbum
import com.piyush.bmusurat.data.models.ProgramResponse
import com.piyush.bmusurat.ui.screens.home.components.ErrorScreen
import com.piyush.bmusurat.ui.screens.home.components.SectionTitle
import com.piyush.bmusurat.ui.screens.home.components.ShimmerBox
import com.piyush.bmusurat.ui.screens.home.components.WavyProgressIndicator
import com.piyush.bmusurat.ui.screens.program.components.DirectorCard
import com.piyush.bmusurat.ui.screens.program.components.FullScreenImageDialog
import com.piyush.bmusurat.ui.screens.program.components.GallerySection
import com.piyush.bmusurat.ui.screens.program.components.PersonCard
import com.piyush.bmusurat.ui.screens.program.components.ProgramExpandableCard
import com.piyush.bmusurat.ui.screens.program.components.RecruitedStudentSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramDetailScreen(
    navController: NavController,
    viewModel: ProgramViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var selectedAlbum by remember { mutableStateOf<GalleryAlbum?>(null) }
    var selectedPersonImage by remember { mutableStateOf<String?>(null) }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.data?.programInfo?.shortName ?: "Institute Details",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            when {
                uiState.isLoading -> {
                    WavyProgressIndicator(Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    ErrorScreen(
                        message = uiState.error!!,
                        onRetry = { viewModel.loadData() }
                    )
                }
                uiState.data != null -> {
                    if (uiState.data!!.institute != null) {
                        ProgramContent(
                            data = uiState.data!!,
                            onAlbumClick = { album -> selectedAlbum = album },
                            onPersonImageLongClick = { imageUrl -> selectedPersonImage = imageUrl }
                        )
                    } else {
                        Text(
                            text = "No details available for this institute.",
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
        }

        if (selectedAlbum != null) {
            ModalBottomSheet(
                onDismissRequest = { selectedAlbum = null },
                sheetState = sheetState,
                containerColor = MaterialTheme.colorScheme.surface,
                dragHandle = { BottomSheetDefaults.DragHandle() },
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxHeight(0.85f)
                ) {
                    Text(
                        text = selectedAlbum?.title ?: "Gallery",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 16.dp)
                    )

                    LazyVerticalGrid(
                        columns = GridCells.Adaptive(minSize = 150.dp),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(selectedAlbum?.images ?: emptyList()) { imageUrl ->
                            Card(
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                            ) {
                                SubcomposeAsyncImage(
                                    model = ImageRequest.Builder(LocalContext.current)
                                        .data(imageUrl)
                                        .crossfade(true)
                                        .build(),
                                    contentDescription = null,
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop,
                                    loading = { ShimmerBox(Modifier.fillMaxSize()) }
                                )
                            }
                        }
                    }
                }
            }
        }

        if (selectedPersonImage != null) {
            FullScreenImageDialog(
                imageUrl = selectedPersonImage!!,
                onDismiss = { selectedPersonImage = null }
            )
        }
    }
}

@Composable
fun ProgramContent(
    data: ProgramResponse,
    onAlbumClick: (GalleryAlbum) -> Unit,
    onPersonImageLongClick: (String) -> Unit
) {
    val institute = data.institute!!

    var showAllPrograms by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = data.programInfo?.name ?: "Institute",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        if (institute.director != null) {
            item {
                DirectorCard(
                    director = institute.director,
                    onImageLongClick = onPersonImageLongClick
                )
                Spacer(Modifier.height(16.dp))
            }
        }

        if (!institute.programs.isNullOrEmpty()) {
            item {
                SectionTitle(title = "Programs Offered", modifier = Modifier.padding(horizontal = 16.dp))

                val programsList = institute.programs.toList()
                val displayedPrograms = if (showAllPrograms) programsList else programsList.take(4)

                displayedPrograms.forEach { (name, details) ->
                    ProgramExpandableCard(name, details)
                }

                if (programsList.size > 4) {
                    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)) {
                        TextButton(
                            onClick = { showAllPrograms = !showAllPrograms },
                            modifier = Modifier.align(Alignment.Center)
                        ) {
                            Text(
                                text = if (showAllPrograms) "Show Less" else "View More (+${programsList.size - 4})",
                                style = MaterialTheme.typography.labelLarge
                            )
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
            }
        }

        if (!institute.faculty.isNullOrEmpty()) {
            item {
                SectionTitle(title = "Our Faculty", modifier = Modifier.padding(horizontal = 16.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(institute.faculty) { faculty ->
                        PersonCard(
                            name = faculty.name ?: "Unknown Faculty",
                            role = faculty.designation ?: "",
                            photoUrl = faculty.photo ?: "",
                            qualification = faculty.qualification ?: faculty.specialization ?: "",
                            onImageLongClick = onPersonImageLongClick
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }

        if (!institute.gallery.isNullOrEmpty()) {
            item {
                SectionTitle(title = "Gallery", modifier = Modifier.padding(horizontal = 16.dp))
                GallerySection(
                    albums = institute.gallery,
                    onAlbumClick = onAlbumClick
                )
                Spacer(Modifier.height(24.dp))
            }
        }

        if (!institute.infrastructure.isNullOrEmpty()) {
            item {
                SectionTitle(title = "Infrastructure", modifier = Modifier.padding(horizontal = 16.dp))
                GallerySection(
                    albums = institute.infrastructure,
                    onAlbumClick = onAlbumClick
                )
                Spacer(Modifier.height(24.dp))
            }
        }

        if (!institute.studentsRecruited.isNullOrEmpty()) {
            item {
                RecruitedStudentSection(students = institute.studentsRecruited)
                Spacer(Modifier.height(24.dp))
            }
        }

        if (!institute.placement.isNullOrEmpty()) {
            item {
                SectionTitle(title = "Placement Team", modifier = Modifier.padding(horizontal = 16.dp))
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(institute.placement) { member ->
                        PersonCard(
                            name = member.name ?: "Unknown",
                            role = member.designation ?: "",
                            photoUrl = member.photo ?: "",
                            qualification = member.phone ?: "",
                            onImageLongClick = onPersonImageLongClick
                        )
                    }
                }
                Spacer(Modifier.height(24.dp))
            }
        }
    }
}