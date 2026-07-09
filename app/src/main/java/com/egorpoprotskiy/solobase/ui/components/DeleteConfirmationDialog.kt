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
import androidx.compose.ui.res.stringResource
import com.egorpoprotskiy.solobase.R

@Composable
fun DeleteConfirmationDialog(
    title: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    confirmText: String? = null,
    dismissText: String? = null
) {
    val resolvedConfirmText = confirmText ?: stringResource(R.string.action_delete)
    val resolvedDismissText = dismissText ?: stringResource(R.string.action_cancel)

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
                    Text(resolvedDismissText)
                }
                TextButton(onClick = onConfirm) {
                    Text(
                        text = resolvedConfirmText,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    )
}
