package com.cielo.cielopass.core.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.cielo.cielopass.features.splash.presentation.SplashScreen

@Composable
fun AppNavHost(modifier: Modifier = Modifier) {
    val backStack = remember { mutableStateListOf<Any>(Splash) }

    NavDisplay(
        backStack = backStack,
        modifier = modifier,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is Splash -> {
                    NavEntry(key) {
                        SplashScreen {
                            backStack.clear()
                            backStack.add(Events)
                        }
                    }
                }

                is Events -> {
                    NavEntry(key) {
                    }
                }

                else -> {
                    NavEntry(Unit) {
                        Text("Unknown route")
                    }
                }
            }
        },
    )
}
