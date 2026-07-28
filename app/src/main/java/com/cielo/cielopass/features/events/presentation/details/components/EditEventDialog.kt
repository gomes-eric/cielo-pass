package com.cielo.cielopass.features.events.presentation.details.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.cielo.cielopass.R
import com.cielo.cielopass.core.event.domain.model.Event

@Composable
fun EditEventDialog(
    event: Event,
    onDismiss: () -> Unit,
    onConfirm: (
        title: String,
        description: String,
        date: String,
        venue: String,
        price: Double,
        totalTickets: Int,
        availableTickets: Int,
        imageUrl: String?,
    ) -> Unit,
) {
    var title by remember { mutableStateOf(event.title) }
    var description by remember { mutableStateOf(event.description) }
    var date by remember { mutableStateOf(event.date) }
    var venue by remember { mutableStateOf(event.venue) }
    var priceText by remember { mutableStateOf(event.price.toString()) }
    var totalTicketsText by remember { mutableStateOf(event.totalTickets.toString()) }
    var availableTicketsText by remember { mutableStateOf(event.availableTickets.toString()) }
    var imageUrl by remember { mutableStateOf(event.imageUrl ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.event_dialog_edit_title),
                fontWeight = FontWeight.Bold,
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(R.string.event_label_title)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text(stringResource(R.string.event_label_date)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = venue,
                    onValueChange = { venue = it },
                    label = { Text(stringResource(R.string.event_label_venue)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text(stringResource(R.string.event_label_price)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = totalTicketsText,
                        onValueChange = { totalTicketsText = it },
                        label = { Text(stringResource(R.string.event_label_total_tickets)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )

                    OutlinedTextField(
                        value = availableTicketsText,
                        onValueChange = { availableTicketsText = it },
                        label = { Text(stringResource(R.string.event_label_available_tickets)) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                    )
                }

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(R.string.event_label_description)) },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                )

                OutlinedTextField(
                    value = imageUrl,
                    onValueChange = { imageUrl = it },
                    label = { Text(stringResource(R.string.event_label_image_url)) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val price = priceText.replace(',', '.').toDoubleOrNull() ?: event.price
                    val total = totalTicketsText.toIntOrNull() ?: event.totalTickets
                    val avail = availableTicketsText.toIntOrNull() ?: event.availableTickets

                    onConfirm(
                        title,
                        description,
                        date,
                        venue,
                        price,
                        total,
                        avail,
                        imageUrl.ifBlank { null },
                    )
                },
            ) {
                Text(stringResource(R.string.action_update))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.action_cancel))
            }
        },
    )
}
