package com.egorpoprotskiy.solobase.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun DeleteConfirmationDialog(
    title: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String = "Удалить",
    dismissText: String = "Отмена"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = null,
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TextButton(onClick = onDismiss) {
                    Text(dismissText)
                }
                TextButton(onClick = onConfirm) {
                    Text(
                        text = confirmText,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    )
}
