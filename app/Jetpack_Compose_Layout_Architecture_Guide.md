# Jetpack Compose Layout & Architecture Refactor Guide

## Goal
Refactor the existing Jetpack Compose application to:
- Use a pure, clean layout system
- Handle safe areas (status bar, navigation bar, gestures, notches) correctly
- Implement TopBar, BottomBar, Drawer, and Navigation
- Follow modern Android best architecture
- Avoid duplicated padding and layout bugs

---

## Core Principles (STRICT)

1. Handle safe areas ONLY at the app root
2. Never apply systemBarsPadding() inside screens
3. Screens must contain content only
4. Navigation, bars, and drawer must be centralized
5. Use Material 3 + Navigation Compose
6. Layout must be edge-to-edge compatible

---

## Root Layout (Single Source of Truth)

```kotlin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLayout() {

    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AppDrawer(
                onNavigate = { route ->
                    navController.navigate(route)
                    scope.launch { drawerState.close() }
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                AppTopBar(
                    onMenuClick = {
                        scope.launch { drawerState.open() }
                    }
                )
            },
            bottomBar = {
                AppBottomBar(navController)
            },
            contentWindowInsets = WindowInsets.systemBars
        ) { innerPadding ->

            AppNavGraph(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
```

---

## Navigation Graph

```kotlin
@Composable
fun AppNavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = modifier
    ) {
        composable("home") { HomeScreen() }
        composable("profile") { ProfileScreen() }
        composable("settings") { SettingsScreen() }
    }
}
```

---

## Screen Rule

Never use systemBarsPadding(), statusBarsPadding(), or navigationBarsPadding() inside screens.

```kotlin
@Composable
fun HomeScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("Home Screen")
    }
}
```

---

## Folder Structure

```
ui/
 ├── layout/
 ├── navigation/
 ├── components/
 └── screens/
```

---

## Result

- Clean layout
- Proper safe area handling
- Scalable navigation
- Production-ready architecture
