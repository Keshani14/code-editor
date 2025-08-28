package com.example.codeeditor

import androidx.compose.ui.platform.ClipboardManager
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CompilerInterface(
    clipboardManager: ClipboardManager,
    compileOutput: String,
    onClose: () -> Unit
) {
    // Determine if compilation was successful
    val isSuccessful = compileOutput.contains("Kotlin file saved at")
    val statusMessage = if (isSuccessful) "File saved successfully!" else "File saving unsuccessful"
    val statusColor = if (isSuccessful) Color(0xFF4CAF50) else Color(0xFFF44336)
    val statusIcon = if (isSuccessful) Icons.Default.CheckCircle else Icons.Default.Warning
    
    AlertDialog(
        onDismissRequest = { onClose() },
        title = { 
            Text(
                text = "Compiler Result",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = { 
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Status message with icon
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = statusColor.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Icon(
                            imageVector = statusIcon,
                            contentDescription = if (isSuccessful) "Success" else "Warning",
                            tint = statusColor,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = statusMessage,
                            color = statusColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                // Compilation output
                if (compileOutput.isNotEmpty()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF5F5F5)
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = compileOutput,
                            modifier = Modifier.padding(16.dp),
                            fontSize = 14.sp,
                            color = Color(0xFF424242)
                        )
                    }
                }
            }
        },
        confirmButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Copy button on the left
                Button(
                    onClick = {
                        val pathOnly = compileOutput.substringAfter("Kotlin file saved at ").trim()
                        if (pathOnly.isNotEmpty()) {
                            clipboardManager.setText(AnnotatedString(pathOnly))
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2196F3)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Copy Path")
                }
                
                Spacer(modifier = Modifier.padding(horizontal = 8.dp))
                
                // OK button on the right
                Button(
                    onClick = { onClose() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4CAF50)
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("OK")
                }
            }
        }
    )
}