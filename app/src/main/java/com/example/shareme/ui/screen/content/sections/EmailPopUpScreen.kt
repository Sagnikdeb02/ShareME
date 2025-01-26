package com.example.shareme.ui.screen.content.sections

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

@Composable
fun EmailInputDialog(
    onDone: (String) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current
    var email by remember { mutableStateOf("") }

    androidx.compose.material.AlertDialog(
        onDismissRequest = { /* No-op */ },
        title = {
            Text(
                text = "Enter Receiver's Email",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        text = {
            Column {
                Text(
                    text = "Please enter the email address of the receiver.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Spacer(modifier = Modifier.height(16.dp))
                // Email Input Field
                androidx.compose.material3.OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Receiver's Email") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            androidx.compose.material.TextButton(
                onClick = {
                    if (email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                        onDone(email) // Pass the email to the onDone callback
                    } else {
                        Toast.makeText(context, "Invalid email address", Toast.LENGTH_SHORT).show()
                    }
                }
            ) {
                Text(text = "Done")
            }
        },
        dismissButton = {
            androidx.compose.material.TextButton(
                onClick = onCancel // Invoke the onCancel callback
            ) {
                Text(text = "Cancel")
            }
        }
    )
}
