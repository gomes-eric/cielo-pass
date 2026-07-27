package com.cielo.cielopass

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.cielo.cielopass.core.cielo.presentation.CieloDeeplinkManager
import com.cielo.cielopass.core.navigation.AppNavHost
import com.cielo.cielopass.core.theme.CieloPassTheme
import org.koin.android.ext.android.inject

class MainActivity : ComponentActivity() {
    private val cieloDeeplinkManager: CieloDeeplinkManager by inject()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        if (savedInstanceState == null) {
            handleIntent(intent)
        }

        setContent {
            CieloPassTheme {
                AppNavHost(
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)

        setIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        intent?.data?.let { uri ->
            cieloDeeplinkManager.handleDeeplinkUri(uri.toString())
        }
    }
}
