package com.emarketing_thripitaka.application.ui.screens.home

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emarketing_thripitaka.application.R
import com.emarketing_thripitaka.application.ViewModelFactory
import com.emarketing_thripitaka.application.data.BookmarkedPaper
import com.emarketing_thripitaka.application.model.Paper
import com.emarketing_thripitaka.application.ui.components.CustomButton
import com.emarketing_thripitaka.application.ui.navigation.Screen
import com.emarketing_thripitaka.application.ui.theme.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    drawerState: DrawerState,
    scope: CoroutineScope,
    viewModel: HomeViewModel = viewModel(
        factory = ViewModelFactory(LocalContext.current.applicationContext as Application)
    )
) {
    val uiState by viewModel.uiState.collectAsState()
    
    HomeScreenContent(
        uiState = uiState,
        onMenuClick = { scope.launch { drawerState.open() } },
        onExamSelected = { viewModel.onExamSelected(it) },
        onCategorySelected = { viewModel.onCategorySelected(it) },
        onYearSelected = { viewModel.onYearSelected(it) },
        onPartSelected = { viewModel.onPartSelected(it) },
        onSearchQueryChanged = { viewModel.onSearchQueryChanged(it) },
        onToggleSearchActive = { viewModel.toggleSearchActive(it) },
        onToggleBookmark = { exam, cat, paper -> viewModel.toggleBookmark(exam, cat, paper) },
        onToggleShowBookmarks = { viewModel.toggleShowBookmarksOnly(it) },
        onPaperClick = { exam, cat, paper ->
            navController.navigate(
                Screen.PaperInfo.createRoute(
                    exam = exam,
                    category = cat,
                    paperTitle = paper.paper_title,
                    paperLink = paper.link,
                    part = paper.part?.toString(),
                    year = paper.year.toString()
                )
            )
        },
        onNextClick = {
            if (uiState.selectedExam.isNotEmpty() && uiState.selectedCategory.isNotEmpty() && uiState.selectedPaper != null) {
                val paper = uiState.selectedPaper!!
                navController.navigate(
                    Screen.PaperInfo.createRoute(
                        exam = uiState.selectedExam,
                        category = uiState.selectedCategory,
                        paperTitle = paper.paper_title,
                        paperLink = paper.link,
                        part = uiState.selectedPart,
                        year = uiState.selectedYear
                    )
                )
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreenContent(
    uiState: HomeUiState,
    onMenuClick: () -> Unit,
    onExamSelected: (String) -> Unit,
    onCategorySelected: (String) -> Unit,
    onYearSelected: (String) -> Unit,
    onPartSelected: (String) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onToggleSearchActive: (Boolean) -> Unit,
    onToggleBookmark: (String, String, Paper) -> Unit,
    onToggleShowBookmarks: (Boolean) -> Unit,
    onPaperClick: (String, String, Paper) -> Unit,
    onNextClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val iconTint = if (isDark) Color.White else Color.Black
    var showValidationAlert by remember { mutableStateOf(false) }

    val backgroundColor = AppColors.background()
    val gradientColors = remember(isDark, backgroundColor) {
        if (isDark) {
            listOf(backgroundColor, Color(0xFF0A0A0A))
        } else {
            listOf(backgroundColor, Color(0xFFFFEAC6))
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(gradientColors))
    ) {
        Icon(
            painter = painterResource(id = R.drawable.ic_dharmachakra),
            contentDescription = null,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 20.dp, y = (-20).dp)
                .size(150.dp),
            tint = if (isDark) Color.White.copy(alpha = 0.05f) else Color(0xFFFFA601).copy(alpha = 0.08f)
        )

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        if (uiState.isSearchActive) {
                            OutlinedTextField(
                                value = uiState.searchQuery,
                                onValueChange = onSearchQueryChanged,
                                placeholder = { Text("ප්‍රශ්න පත්‍ර සෙවුම...", fontSize = 14.sp) },
                                singleLine = true,
                                trailingIcon = {
                                    if (uiState.searchQuery.isNotEmpty()) {
                                        IconButton(onClick = { onSearchQueryChanged("") }) {
                                            Icon(Icons.Default.Close, null, modifier = Modifier.size(18.dp))
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth(0.9f)
                                    .height(50.dp),
                                shape = RoundedCornerShape(25.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AppColors.primary(),
                                    unfocusedBorderColor = AppColors.unselected().copy(alpha = 0.3f),
                                    focusedContainerColor = AppColors.surface(),
                                    unfocusedContainerColor = AppColors.surface()
                                )
                            )
                        } else {
                            Text(
                                "මුල් පිටුව",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }
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
                                tint = iconTint
                            )
                        }
                    },
                    actions = {
                        IconButton(
                            onClick = { onToggleSearchActive(!uiState.isSearchActive) },
                            modifier = Modifier.padding(end = 4.dp)
                        ) {
                            Icon(
                                imageVector = if (uiState.isSearchActive) Icons.Default.Close else Icons.Default.Search,
                                contentDescription = "Search",
                                tint = iconTint
                            )
                        }
                        IconButton(
                            onClick = { onToggleShowBookmarks(!uiState.showBookmarksOnly) },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Icon(
                                imageVector = if (uiState.showBookmarksOnly) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmarks",
                                tint = if (uiState.showBookmarksOnly) AppColors.primary() else iconTint
                            )
                        }
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    ),
                    windowInsets = WindowInsets(0, 0, 0, 0)
                )
            },
            containerColor = Color.Transparent
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = AppColors.primary())
                } else if (uiState.isSearchActive) {
                    SearchModeView(
                        query = uiState.searchQuery,
                        results = uiState.searchResults,
                        onPaperClick = onPaperClick
                    )
                } else if (uiState.showBookmarksOnly) {
                    BookmarksView(
                        bookmarks = uiState.bookmarks,
                        onPaperClick = onPaperClick,
                        onRemoveBookmark = { item ->
                            onToggleBookmark(item.exam, item.category, item.paper)
                        }
                    )
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(16.dp))

                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                        ) {
                            Text(
                                text = "ශ්‍රී ලංකා ත්‍රිපිටක විභාගයට අදාළ පසුගිය ප්‍රශ්න පත්‍ර",
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.ExtraBold,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    textAlign = TextAlign.Center,
                                    lineHeight = 38.sp,
                                    fontSize = 26.sp
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(28.dp))

                            Text(
                                text = "ඔබගේ විභාගය තෝරන්න",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.SemiBold
                                )
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            AppDropdown(
                                label = "විභාගය",
                                options = uiState.exams.map { it.title },
                                selectedOption = uiState.selectedExam,
                                onOptionSelected = onExamSelected
                            )

                            AppDropdown(
                                label = "කාණ්ඩය තෝරන්න...",
                                options = uiState.availableCategories,
                                selectedOption = uiState.selectedCategory,
                                onOptionSelected = onCategorySelected
                            )

                            AppDropdown(
                                label = "වර්ෂය තෝරන්න...",
                                options = uiState.availableYears.map { "${it.paper_title} (${it.year})" },
                                selectedOption = if (uiState.selectedYear.isEmpty()) "" else "${uiState.selectedPaper?.paper_title ?: ""} (${uiState.selectedYear})",
                                onOptionSelected = onYearSelected
                            )

                            if (uiState.availableParts.isNotEmpty()) {
                                AppDropdown(
                                    label = "පත්‍රය තෝරන්න...",
                                    options = uiState.availableParts,
                                    selectedOption = if (uiState.selectedPart.isEmpty()) "" else "Part ${uiState.selectedPart}",
                                    onOptionSelected = onPartSelected
                                )
                            }

                            Spacer(modifier = Modifier.height(20.dp))

                            val selectionText = if (uiState.selectedExam.isNotEmpty() && uiState.selectedCategory.isNotEmpty() && (uiState.selectedYear.isNotEmpty())) {
                                 buildString {
                                    append("ඔබ තෝරාගත්තේ: ${uiState.selectedExam} → ${uiState.selectedCategory}")
                                    uiState.selectedPaper?.let { append(" → ${it.year} - ${it.paper_title}") }
                                    if (uiState.selectedPart.isNotEmpty()) append(" (Part ${uiState.selectedPart})")
                                }
                            } else {
                                "කරුණාකර සියල්ල තෝරන්න"
                            }

                            Text(
                                text = selectionText,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )

                            Spacer(modifier = Modifier.height(32.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CustomButton(
                                    title = "Next",
                                    onPress = {
                                        if (uiState.selectedExam.isNotEmpty() && uiState.selectedCategory.isNotEmpty() && uiState.selectedPaper != null) {
                                            onNextClick()
                                        } else {
                                            showValidationAlert = true
                                        }
                                    },
                                    modifier = Modifier.weight(1f)
                                )

                                if (uiState.selectedPaper != null) {
                                    val isBookmarked = uiState.bookmarks.any { it.paper.link == uiState.selectedPaper.link }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    IconButton(
                                        onClick = {
                                            onToggleBookmark(uiState.selectedExam, uiState.selectedCategory, uiState.selectedPaper)
                                        },
                                        modifier = Modifier
                                            .size(56.dp)
                                            .background(
                                                if (isBookmarked) AppColors.primary().copy(alpha = 0.15f) else AppColors.surface(),
                                                RoundedCornerShape(18.dp)
                                            )
                                    ) {
                                        Icon(
                                            imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                            contentDescription = "Bookmark",
                                            tint = AppColors.primary()
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(80.dp))
                        }
                    }
                }
            }
        }
    }

    if (showValidationAlert) {
        AlertDialog(
            onDismissRequest = { showValidationAlert = false },
            title = { Text("අසම්පූර්ණ තේරීමක්", fontWeight = FontWeight.Bold) },
            text = { Text("කරුණාකර ඉදිරියට යාමට පෙර විභාගය, කාණ්ඩය සහ වර්ෂය නිවැරදිව තෝරන්න.") },
            confirmButton = {
                TextButton(onClick = { showValidationAlert = false }) {
                    Text("හරි", color = AppColors.primary(), fontWeight = FontWeight.Bold)
                }
            },
            shape = RoundedCornerShape(16.dp),
            containerColor = AppColors.surface()
        )
    }
}

@Composable
fun SearchModeView(
    query: String,
    results: List<SearchResultPaper>,
    onPaperClick: (String, String, Paper) -> Unit
) {
    if (query.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("ප්‍රශ්න පත්‍ර නම, වර්ෂය හෝ විභාගය සූක්ෂ්මව සොයන්න...", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else if (results.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("ප්‍රතිඵල හමු නොවීය", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(results) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onPaperClick(item.examTitle, item.categoryName, item.paper) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AppColors.surface())
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = AppColors.primary().copy(alpha = 0.12f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(painterResource(id = R.drawable.ic_solar_document_text), null, tint = AppColors.primary(), modifier = Modifier.size(22.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.paper.paper_title, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("${item.examTitle} • ${item.categoryName} (${item.paper.year})", fontSize = 12.sp, color = AppColors.primary())
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookmarksView(
    bookmarks: List<BookmarkedPaper>,
    onPaperClick: (String, String, Paper) -> Unit,
    onRemoveBookmark: (BookmarkedPaper) -> Unit
) {
    if (bookmarks.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.BookmarkBorder, null, modifier = Modifier.size(64.dp), tint = AppColors.unselected())
                Spacer(modifier = Modifier.height(16.dp))
                Text("තවමත් ප්‍රියතම ප්‍රශ්න පත්‍ර නොමැත", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(bookmarks) { item ->
                Card(
                    modifier = Modifier.fillMaxWidth().clickable { onPaperClick(item.exam, item.category, item.paper) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = AppColors.surface())
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(44.dp),
                            shape = RoundedCornerShape(12.dp),
                            color = AppColors.primary().copy(alpha = 0.12f)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Bookmark, null, tint = AppColors.primary(), modifier = Modifier.size(22.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(item.paper.paper_title, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            Text("${item.exam} • ${item.category} (${item.paper.year})", fontSize = 12.sp, color = AppColors.primary())
                        }
                        IconButton(onClick = { onRemoveBookmark(item) }) {
                            Icon(Icons.Default.Close, null, tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f))
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDropdown(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
    ) {
        OutlinedTextField(
            value = selectedOption.ifEmpty { label },
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AppColors.primary(),
                unfocusedBorderColor = AppColors.unselected().copy(alpha = 0.5f),
                focusedContainerColor = AppColors.surface(),
                unfocusedContainerColor = AppColors.surface()
            ),
            modifier = Modifier
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
            shape = RoundedCornerShape(14.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(AppColors.surface())
        ) {
            options.forEach { selectionOption ->
                DropdownMenuItem(
                    text = { Text(selectionOption) },
                    onClick = {
                        onOptionSelected(selectionOption)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    ThripitakaPaperApplicationTheme {
        HomeScreenContent(
            uiState = HomeUiState(
                isLoading = false,
                selectedExam = "Pravachana",
                selectedCategory = "Preliminary"
            ),
            onMenuClick = {},
            onExamSelected = {},
            onCategorySelected = {},
            onYearSelected = {},
            onPartSelected = {},
            onSearchQueryChanged = {},
            onToggleSearchActive = {},
            onToggleBookmark = { _, _, _ -> },
            onToggleShowBookmarks = {},
            onPaperClick = { _, _, _ -> },
            onNextClick = {}
        )
    }
}
