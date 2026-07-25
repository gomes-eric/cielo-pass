package com.cielo.cielopass.features.splash.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.cielo.cielopass.R
import com.cielo.cielopass.core.theme.CieloPassTheme
import com.cielo.cielopass.features.splash.presentation.SplashEffect.NavigateToEvents
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashScreen(
    viewModel: SplashViewModel = koinViewModel(),
    onNavigateToEvents: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.effect.collect { effect ->
            when (effect) {
                NavigateToEvents -> onNavigateToEvents()
            }
        }
    }

    CieloPassTheme(darkTheme = true) {
        SplashScreenContent(
            isLoading = state.isLoading,
            statusText = state.error ?: state.statusText,
            appVersion = state.config?.appVersion ?: "1.0.0",
        )
    }
}

@Composable
fun SplashScreenContent(
    isLoading: Boolean = true,
    statusText: String = stringResource(R.string.splash_default_status_text),
    appVersion: String = "1.0.0",
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.secondaryContainer,
                    ),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_cielopass_logo),
                contentDescription = stringResource(R.string.splash_logo_content_description),
                modifier = Modifier.height(72.dp),
            )

            Spacer(modifier = Modifier.height(120.dp))

            Box(
                modifier = Modifier.height(52.dp),
                contentAlignment = Alignment.TopCenter,
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(36.dp),
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp,
                    )
                }
            }

            Text(
                text = statusText,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = stringResource(R.string.splash_app_version, appVersion),
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = stringResource(R.string.splash_cielo_ecosystem_subtitle),
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.outline,
            )
        }
    }
}

@Preview(showBackground = true, device = "spec:width=480dp,height=800dp,dpi=240")
@Composable
fun SplashScreenPreview() {
    CieloPassTheme(darkTheme = true) {
        SplashScreenContent(
            statusText = "Verificando transações pendentes...",
            appVersion = "1.0.0",
        )
    }
}
