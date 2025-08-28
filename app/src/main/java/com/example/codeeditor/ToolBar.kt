package com.example.codeeditor

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * MiniToolbar Composable
 *
 * A modern, visually appealing toolbar UI that provides buttons for text editing actions:
 * - Cut
 * - Copy
 * - Paste
 *
 * Features:
 * - Gradient background with rounded corners
 * - Icons for each action
 * - Hover effects and modern styling
 * - Responsive layout with proper spacing
 */
@Composable
fun MiniToolbar(
    onCut: () -> Unit,
    onCopy: () -> Unit,
    onPaste: () -> Unit
) {
    // Professional dark blue gradient background
    val gradientBrush = Brush.horizontalGradient(
        colors = listOf(
            Color(0xFF1a237e),  // Deep dark blue start
            Color(0xFF0d47a1),  // Dark blue middle
            Color(0xFF1565c0)   // Professional blue end
        )
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 12.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(gradientBrush)
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Cut Button
            ToolbarButton(
                onClick = onCut,
                icon = Icons.Default.Create,
                label = "Cut"
            )
            
            // Copy Button
            ToolbarButton(
                onClick = onCopy,
                icon = Icons.Default.Star,
                label = "Copy"
            )
            
            // Paste Button
            ToolbarButton(
                onClick = onPaste,
                icon = Icons.Default.Done,
                label = "Paste"
            )
        }
    }
}

/**
 * Individual toolbar button with professional styling
 */
@Composable
private fun ToolbarButton(
    onClick: () -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String
) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2c3e50).copy(alpha = 0.9f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier.padding(4.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFFecf0f1),
                modifier = Modifier.size(20.dp)
            )
            
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.padding(horizontal = 6.dp))
            
            Text(
                text = label,
                color = Color(0xFFecf0f1),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}