package com.emarketing_thripitaka.application.ui.screens.pdfviewer

import android.app.Application
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.print.PrintManager
import android.util.LruCache
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import androidx.core.graphics.createBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emarketing_thripitaka.application.R
import com.emarketing_thripitaka.application.ViewModelFactory
import com.emarketing_thripitaka.application.ui.navigation.Screen
import com.emarketing_thripitaka.application.ui.theme.AppColors
import com.emarketing_thripitaka.application.util.PdfPrintAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerScreen(
    navController: NavController,
    paperLink: String,
    paperTitle: String,
    year: String?,
    part: String?,
    viewModel: PdfViewerViewModel = viewModel(
        factory = ViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val decodedTitle = remember(paperTitle) { Screen.decode(paperTitle) }
    val decodedLink = remember(paperLink) { Screen.decode(paperLink) }
    val decodedPart = remember(part) { if (part == null || part == "null") null else Screen.decode(part) }
    val decodedYear = remember(year) { if (year == null || year == "null") null else Screen.decode(year) }
    
    val loadState by viewModel.loadState.collectAsState()

    LaunchedEffect(decodedLink) {
        viewModel.loadPdf(decodedLink)
    }

    PdfViewerContent(
        loadState = loadState,
        title = decodedTitle,
        year = decodedYear,
        part = decodedPart,
        onBackClick = { navController.popBackStack() }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PdfViewerContent(
    loadState: PdfLoadState,
    title: String,
    year: String?,
    part: String?,
    onBackClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val context = LocalContext.current
    val fullTitle = if (part != null) "$title - Part $part" else title

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = fullTitle,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (year != null) {
                            Text(
                                text = year,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = AppColors.primary()
                        )
                    }
                },
                actions = {
                    if (loadState is PdfLoadState.Success) {
                        val pdfFile = loadState.file
                        IconButton(onClick = {
                            try {
                                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", pdfFile)
                                val intent = Intent(Intent.ACTION_SEND).apply {
                                    type = "application/pdf"
                                    putExtra(Intent.EXTRA_STREAM, uri)
                                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                                }
                                context.startActivity(Intent.createChooser(intent, "ප්‍රශ්න පත්‍රය බෙදාගන්න"))
                            } catch (_: Exception) {}
                        }) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = AppColors.primary())
                        }
                        IconButton(onClick = {
                            try {
                                val printManager = context.getSystemService(Context.PRINT_SERVICE) as PrintManager
                                val printAdapter = PdfPrintAdapter(pdfFile)
                                printManager.print(fullTitle, printAdapter, null)
                            } catch (_: Exception) {}
                        }) {
                            Icon(painterResource(id = R.drawable.ic_solar_printer), contentDescription = "Print", tint = AppColors.primary())
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AppColors.background(),
                    scrolledContainerColor = AppColors.background()
                )
            )
        },
        containerColor = AppColors.background()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (val state = loadState) {
                is PdfLoadState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = AppColors.primary())
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("ප්‍රශ්න පත්‍රය සාදන ලදී...", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                }
                is PdfLoadState.Success -> {
                    NativePdfViewer(file = state.file)
                }
                is PdfLoadState.Error -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = state.message,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> {}
            }
        }
    }
}

@Composable
fun NativePdfViewer(file: File) {
    val renderer = remember(file) {
        try {
            val input = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            PdfRenderer(input)
        } catch (_: Exception) {
            null
        }
    }
    
    if (renderer == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Error opening PDF file")
        }
        return
    }

    val pageCount = renderer.pageCount
    val listState = rememberLazyListState()
    val bitmapCache = remember { LruCache<Int, Bitmap>(15) }

    val currentPage by remember {
        derivedStateOf { listState.firstVisibleItemIndex + 1 }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(count = pageCount, key = { it }) { index ->
                var bitmap by remember(index) { mutableStateOf<Bitmap?>(bitmapCache.get(index)) }

                LaunchedEffect(index) {
                    if (bitmap == null) {
                        bitmap = withContext(Dispatchers.IO) {
                            val cached = bitmapCache.get(index)
                            if (cached != null) return@withContext cached

                            try {
                                val page = renderer.openPage(index)
                                val width = page.width * 2
                                val height = page.height * 2
                                val bmp = createBitmap(width, height)
                                val canvas = Canvas(bmp)
                                canvas.drawColor(android.graphics.Color.WHITE)
                                page.render(bmp, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
                                page.close()
                                bitmapCache.put(index, bmp)
                                bmp
                            } catch (_: Exception) {
                                null
                            }
                        }
                    }
                }

                if (bitmap != null) {
                    Image(
                        bitmap = bitmap!!.asImageBitmap(),
                        contentDescription = "Page ${index + 1}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(bitmap!!.width.toFloat() / bitmap!!.height.toFloat())
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White),
                        contentScale = ContentScale.Fit
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(450.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = AppColors.primary(), modifier = Modifier.size(32.dp))
                    }
                }
            }
        }
        
        Surface(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 24.dp),
            color = Color.Black.copy(alpha = 0.75f),
            shape = RoundedCornerShape(20.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Book, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Page $currentPage / $pageCount", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }

    DisposableEffect(renderer) {
        onDispose {
            try {
                renderer.close()
            } catch (_: Exception) {}
        }
    }
}
