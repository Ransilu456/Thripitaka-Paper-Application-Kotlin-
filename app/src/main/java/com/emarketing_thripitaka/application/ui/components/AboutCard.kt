package com.emarketing_thripitaka.application.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emarketing_thripitaka.application.ui.theme.ThripitakaPaperApplicationTheme

@Composable
fun AboutCard(
    title: String,
    icon: ImageVector,
    iconColor: Color,
    content: String,
    isDark: Boolean,
    actions: @Composable (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.8f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = iconColor, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(12.dp))
                Text(title, fontWeight = FontWeight.Bold, color = if (isDark) Color.White else Color.Black)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = content,
                color = if (isDark) Color(0xFFD1D5DB) else Color(0xFF4B5563),
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
            if (actions != null) {
                Spacer(modifier = Modifier.height(16.dp))
                actions()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AboutCardPreview() {
    ThripitakaPaperApplicationTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            AboutCard(
                title = "About App",
                icon = Icons.Default.Info,
                iconColor = Color.Blue,
                content = "This is a sample application for Thripitaka papers.",
                isDark = false
            )
        }
    }
}
