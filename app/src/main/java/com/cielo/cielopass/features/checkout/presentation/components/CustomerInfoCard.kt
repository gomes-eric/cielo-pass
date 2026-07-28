package com.cielo.cielopass.features.checkout.presentation.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.cielo.cielopass.R
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CustomerInfoCard(
    name: String,
    nameError: String?,
    email: String,
    emailError: String?,
    document: String,
    documentError: String?,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onDocumentChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val nameRequester = remember { BringIntoViewRequester() }
    val emailRequester = remember { BringIntoViewRequester() }
    val documentRequester = remember { BringIntoViewRequester() }

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                text = stringResource(R.string.checkout_customer_info_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                label = { Text(stringResource(R.string.checkout_label_full_name)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                    )
                },
                isError = nameError != null,
                supportingText = nameError?.let { errorMessage ->
                    { Text(errorMessage) }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .bringIntoViewRequester(nameRequester)
                    .onFocusEvent { focusState ->
                        if (focusState.isFocused) {
                            coroutineScope.launch {
                                nameRequester.bringIntoView()
                            }
                        }
                    },
            )

            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text(stringResource(R.string.checkout_label_email)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Email,
                        contentDescription = null,
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = emailError != null,
                supportingText = emailError?.let { errorMessage ->
                    { Text(errorMessage) }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .bringIntoViewRequester(emailRequester)
                    .onFocusEvent { focusState ->
                        if (focusState.isFocused) {
                            coroutineScope.launch {
                                emailRequester.bringIntoView()
                            }
                        }
                    },
            )

            OutlinedTextField(
                value = document,
                onValueChange = onDocumentChange,
                label = { Text(stringResource(R.string.checkout_label_document)) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Badge,
                        contentDescription = null,
                    )
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = documentError != null,
                supportingText = documentError?.let { errorMessage ->
                    { Text(errorMessage) }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .bringIntoViewRequester(documentRequester)
                    .onFocusEvent { focusState ->
                        if (focusState.isFocused) {
                            coroutineScope.launch {
                                documentRequester.bringIntoView()
                            }
                        }
                    },
            )
        }
    }
}
