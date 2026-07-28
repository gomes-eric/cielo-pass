package com.cielo.cielopass.features.splash.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cielo.cielopass.R

@Composable
fun SplashFooterSection(
    appVersion: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.padding(bottom = 24.dp),
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
