package com.emarketing_thripitaka.application.ui.screens.home

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.emarketing_thripitaka.application.R
import com.emarketing_thripitaka.application.ViewModelFactory
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
    onNextClick: () -> Unit
) {
    val isDark = isSystemInDarkTheme()
    val iconTint = if (isDark) Color.White else Color.Black
    var showValidationAlert by remember { mutableStateOf(false) }

    val gradientColors = if (isDark) {
        listOf(AppColors.background(), Color(0xFF0A0A0A))
    } else {
        listOf(AppColors.background(), Color(0xFFFFEAC6))
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
                        Text(
                            "මුල් පිටුව",
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
                                tint = iconTint
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
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        Spacer(modifier = Modifier.height(20.dp))

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

                            Spacer(modifier = Modifier.height(32.dp))

                            Text(
                                text = "ඔබගේ විභාගය තෝරන්න",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                                    fontWeight = FontWeight.SemiBold
                                )
                            )

                            Spacer(modifier = Modifier.height(16.dp))

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

                            Spacer(modifier = Modifier.height(24.dp))

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

                            Spacer(modifier = Modifier.height(40.dp))

                            CustomButton(
                                title = "Next",
                                onPress = {
                                    if (uiState.selectedExam.isNotEmpty() && uiState.selectedCategory.isNotEmpty() && uiState.selectedPaper != null) {
                                        onNextClick()
                                    } else {
                                        showValidationAlert = true
                                    }
                                },
                                modifier = Modifier.fillMaxWidth()
                            )

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
            .padding(vertical = 8.dp)
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
            shape = RoundedCornerShape(12.dp)
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
            onNextClick = {}
        )
    }
}
