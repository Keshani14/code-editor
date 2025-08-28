package com.example.codeeditor

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun FindReplaceBar(
    editorState: TextEditorState,
    onClose: () -> Unit
) {
    var findText = remember { mutableStateOf("") }
    var replaceText = remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = { onClose() },
        title = { Text("Find and Replace") },
        text = {
            Column {
                OutlinedTextField(
                    value = findText.value,
                    onValueChange = { findText.value = it },
                    label = { Text("Find") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = replaceText.value,
                    onValueChange = { replaceText.value = it },
                    label = { Text("Replace") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                 editorState.replace(findText.value, replaceText.value)
                onClose()
            }) {
                Text("Replace")
            }
            Button(onClick = {
                editorState.replaceAll(findText.value, replaceText.value)
                onClose()
            }) {
                Text("Replace All")
            }
        }

    )
}

