# Jetpack Compose MVVM + StateFlow Architecture Guide

## Goal
Refactor the existing Jetpack Compose application to:
- Use **MVVM architecture**
- Use **StateFlow** for UI state management
- Keep layouts **pure and stateless**
- Handle **safe areas correctly**
- Centralize **Navigation, TopBar, BottomBar, Drawer**
- Follow **modern Android best practices**

---

## Architecture Rules (STRICT)

1. UI (Composable) must be **STATELESS**
2. ViewModel is the **single source of truth**
3. UI reads state via **collectAsStateWithLifecycle()**
4. Events flow **UI → ViewModel**
5. Navigation & safe area handled at **App root**
6. No business logic inside Composables

---

## Recommended Layer Structure

```
ui/
 ├── layout/
 │    └── AppLayout.kt
 ├── navigation/
 │    └── AppNavGraph.kt
 ├── screens/
 │    ├── home/
 │    │    ├── HomeScreen.kt
 │    │    ├── HomeViewModel.kt
 │    │    ├── HomeState.kt
 │    │    └── HomeEvent.kt
 │    └── profile/
 └── components/
data/
 ├── repository/
domain/
 └── usecase/
```

---

## UI State (Immutable)

```kotlin
data class HomeState(
    val isLoading: Boolean = false,
    val title: String = "",
    val error: String? = null
)
```

---

## UI Events

```kotlin
sealed interface HomeEvent {
    data object OnRefresh : HomeEvent
    data class OnItemClick(val id: String) : HomeEvent
}
```

---

## ViewModel (StateFlow)

```kotlin
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: HomeRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.OnRefresh -> loadData()
            is HomeEvent.OnItemClick -> handleClick(event.id)
        }
    }

    private fun loadData() {
        _state.update { it.copy(isLoading = true) }
    }
}
```

---

## Screen (Pure UI)

```kotlin
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    HomeContent(
        state = state,
        onEvent = viewModel::onEvent
    )
}
```

---

## Content Composable (Stateless)

```kotlin
@Composable
fun HomeContent(
    state: HomeState,
    onEvent: (HomeEvent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        if (state.isLoading) {
            CircularProgressIndicator()
        }
        Text(text = state.title)
    }
}
```

---

## Root Layout (Safe Area + Navigation)

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLayout() {

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = { AppDrawer(navController) }
    ) {
        Scaffold(
            topBar = { AppTopBar { scope.launch { drawerState.open() } } },
            bottomBar = { AppBottomBar(navController) },
            contentWindowInsets = WindowInsets.systemBars
        ) { padding ->
            AppNavGraph(
                navController = navController,
                modifier = Modifier.padding(padding)
            )
        }
    }
}
```

---

## Navigation Rules

- Navigation triggered from UI events
- ViewModel exposes **navigation events (SharedFlow)** if needed
- UI collects navigation events safely

---

## Safe Area Rules

❌ DO NOT use systemBarsPadding() in screens  
✅ Handle all insets in AppLayout only  

---

## Final Result

- Clean MVVM separation
- Predictable state management
- Testable ViewModels
- Safe-area correct UI
- Scalable architecture

Apply this guide strictly to the existing application.
