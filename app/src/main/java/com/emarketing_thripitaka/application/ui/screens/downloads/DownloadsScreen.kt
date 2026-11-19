package com.emarketing_thripitaka.application.ui.screens.downloads

import android.app.Application
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emarketing_thripitaka.application.R
import com.emarketing_thripitaka.application.ViewModelFactory
import com.emarketing_thripitaka.application.model.DownloadItem
import com.emarketing_thripitaka.application.ui.navigation.Screen
import com.emarketing_thripitaka.application.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreen(
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    viewModel: DownloadsViewModel = viewModel(
        factory = ViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    DownloadsScreenContent(
        uiState = uiState,
        onMenuClick = { scope.launch { drawerState.open() } },
        onOpenItem = { item ->
            navController.navigate(
                Screen.PdfViewer.createRoute(
                    paperLink = item.uri,
                    paperTitle = item.title,
                    year = item.year,
                    part = item.part
                )
            )
        },
        onDeleteClick = { viewModel.showDeleteConfirmation(it) },
        onExportClick = { viewModel.exportDownload(it) },
        onPrintClick = { viewModel.printDownload(context, it) },
        onConfirmDelete = { viewModel.deleteDownload(it) },
        onDismissDelete = { viewModel.hideDeleteConfirmation() },
        onClearMessage = { viewModel.clearMessage() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadsScreenContent(
    uiState: DownloadsUiState,
    onMenuClick: () -> Unit,
    onOpenItem: (DownloadItem) -> Unit,
    onDeleteClick: (DownloadItem) -> Unit,
    onExportClick: (DownloadItem) -> Unit,
    onPrintClick: (DownloadItem) -> Unit,
    onConfirmDelete: (DownloadItem) -> Unit,
    onDismissDelete: () -> Unit,
    onClearMessage: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val snackbarHostState = remember { SnackbarHostState() }

    val backgroundColor = AppColors.background()
    val gradientColors = remember(isDark, backgroundColor) {
        if (isDark) {
            listOf(backgroundColor, Color(0xFF0A0A0A))
        } else {
            listOf(backgroundColor, Color(0xFFFFEAC6))
        }
    }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            onClearMessage()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "බාගත කිරීම්",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onMenuClick,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .background(
                                if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.05f),
                                RoundedCornerShape(8.dp)
                            )
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_solar_menu),
                            contentDescription = "Menu",
                            tint = if (isDark) Color.White else Color.Black
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    scrolledContainerColor = Color.Transparent
                ),
                windowInsets = WindowInsets(0, 0, 0, 0)
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(gradientColors))
                .padding(innerPadding)
        ) {
            if (uiState.downloads.isEmpty() && !uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_solar_documents),
                            contentDescription = null,
                            modifier = Modifier.size(80.dp),
                            tint = AppColors.primary().copy(alpha = 0.5f)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "බාගත කිරීම් හමු නොවීය",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp, 8.dp, 16.dp, 80.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.downloads) { item ->
                        DownloadListItem(
                            item = item,
                            onOpen = { onOpenItem(item) },
                            onDelete = { onDeleteClick(item) },
                            onExport = { onExportClick(item) },
                            onPrint = { onPrintClick(item) }
                        )
                    }
                }
            }

            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = AppColors.primary())
            }
        }
    }

    uiState.showDeleteDialog?.let { item ->
        AlertDialog(
            onDismissRequest = onDismissDelete,
            title = { Text("ප්‍රශ්න පත්‍රය මකන්න (Delete Paper)") },
            text = { Text("'${item.title}' මකා දැමීමට ඔබට විශ්වාසද?") },
            confirmButton = {
                TextButton(
                    onClick = { onConfirmDelete(item) },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("මකන්න (Delete)")
                }
            },
            dismissButton = {
                TextButton(onClick = onDismissDelete) {
                    Text("අවලංගු කරන්න (Cancel)")
                }
            }
        )
    }
}

@Composable
fun DownloadListItem(
    item: DownloadItem,
    onOpen: () -> Unit,
    onDelete: () -> Unit,
    onExport: () -> Unit,
    onPrint: () -> Unit
) {
    var expanded by remember { mutableStateOf(true) }

    Card(
        modifier = Modifier.fillMaxWidth().animateContentSize(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = AppColors.surface()
        ),
        onClick = { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = AppColors.primary().copy(alpha = 0.12f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_solar_document_text),
                            contentDescription = null,
                            tint = AppColors.primary(),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${item.title} ${item.year ?: ""}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = if (expanded) Int.MAX_VALUE else 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = buildString {
                            append(item.exam)
                            if (item.part != null && item.part != "null") append(" • Part ${item.part}")
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = AppColors.primary(),
                        maxLines = if (expanded) Int.MAX_VALUE else 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onOpen,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        Icon(painterResource(id = R.drawable.ic_solar_magnifer), null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Open")
                    }
                    
                    Row {
                        IconButton(onClick = onPrint) {
                            Icon(painterResource(id = R.drawable.ic_solar_printer), null, tint = AppColors.unselected(), modifier = Modifier.size(22.dp))
                        }
                        IconButton(onClick = onExport) {
                            Icon(painterResource(id = R.drawable.ic_solar_download_square), null, tint = AppColors.unselected(), modifier = Modifier.size(22.dp))
                        }
                        IconButton(onClick = onDelete) {
                            Icon(painterResource(id = R.drawable.ic_solar_trash_bin_trash), null, tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f), modifier = Modifier.size(22.dp))
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DownloadsScreenPreview() {
    ThripitakaPaperApplicationTheme {
        DownloadsScreenContent(
            uiState = DownloadsUiState(
                isLoading = false,
                downloads = listOf(
                    DownloadItem(
                        title = "Dhamma Paper",
                        exam = "Pravachana",
                        year = "2023",
                        part = "1",
                        category = "Preliminary",
                        uri = "path/to/file.pdf"
                    )
                )
            ),
            onMenuClick = {},
            onOpenItem = {},
            onDeleteClick = {},
            onExportClick = {},
            onPrintClick = {},
            onConfirmDelete = {},
            onDismissDelete = {},
            onClearMessage = {}
        )
    }
}
