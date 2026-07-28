package com.cielo.cielopass.features.splash.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cielo.cielopass.R
import com.cielo.cielopass.core.constants.SplashConstants.DEFAULT_APP_VERSION
import com.cielo.cielopass.core.constants.SplashConstants.MSG_CHECKING_PENDING_TRANSACTIONS
import com.cielo.cielopass.core.theme.CieloPassTheme
import com.cielo.cielopass.features.splash.presentation.components.SplashFooterSection
import com.cielo.cielopass.features.splash.presentation.components.SplashHeaderSection
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = koinViewModel(),
    onNavigateToEvents: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(state.isLoading, state.config) {
        if (!state.isLoading && state.config != null) {
            onNavigateToEvents()
        }
    }

    CieloPassTheme(darkTheme = true) {
        SplashScreenContent(
            isLoading = state.isLoading,
            statusText = state.error ?: state.statusText,
            appVersion = state.config?.appVersion ?: DEFAULT_APP_VERSION,
        )
    }
}

@Composable
fun SplashScreenContent(
    isLoading: Boolean = true,
    statusText: String = stringResource(R.string.splash_default_status_text),
    appVersion: String = DEFAULT_APP_VERSION,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.primaryContainer,
                            MaterialTheme.colorScheme.secondaryContainer,
                        ),
                    ),
                ),
        ) {
            SplashHeaderSection(
                isLoading = isLoading,
                statusText = statusText,
                modifier = Modifier.align(Alignment.Center),
            )

            SplashFooterSection(
                appVersion = appVersion,
                modifier = Modifier.align(Alignment.BottomCenter),
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=480dp,height=800dp,dpi=240")
@Composable
fun SplashScreenPreview() {
    CieloPassTheme(darkTheme = true) {
        SplashScreenContent(
            statusText = MSG_CHECKING_PENDING_TRANSACTIONS,
            appVersion = DEFAULT_APP_VERSION,
        )
    }
}
