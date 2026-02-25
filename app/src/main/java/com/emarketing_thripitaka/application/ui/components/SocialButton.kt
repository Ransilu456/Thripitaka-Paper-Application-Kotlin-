package com.emarketing_thripitaka.application.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
fun SocialButton(label: String, icon: ImageVector, isDark: Boolean, onClick: () -> Unit) {
    androidx.compose.material3.Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f)
        )
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = if (isDark) Color.White else Color(0xFF333333)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                label,
                color = if (isDark) Color.White else Color(0xFF333333),
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SocialButtonPreview() {
    ThripitakaPaperApplicationTheme {
        SocialButton(
            label = "Share App",
            icon = Icons.Default.Share,
            isDark = false,
            onClick = {}
        )
    }
}
