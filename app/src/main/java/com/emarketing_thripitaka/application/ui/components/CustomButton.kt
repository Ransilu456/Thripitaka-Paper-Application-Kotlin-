package com.emarketing_thripitaka.application.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.emarketing_thripitaka.application.ui.theme.AppColors
import com.emarketing_thripitaka.application.ui.theme.ThripitakaPaperApplicationTheme

@Composable
fun CustomButton(
    title: String,
    onPress: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null,
    rightIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true,
    isDark: Boolean = false // Parameter kept for compatibility
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    // Smooth press animation
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        label = "ButtonScale"
    )
    
    val primaryColor = AppColors.primary()
    val pressedColor = AppColors.pressed()
    
    // Premium gradient: subtle transition from top-left to bottom-right
    val gradient = Brush.linearGradient(
        colors = if (isPressed) {
            listOf(pressedColor, primaryColor)
        } else {
            listOf(primaryColor, pressedColor)
        }
    )

    Box(
        modifier = modifier
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .alpha(if (enabled) 1f else 0.5f)
            .padding(vertical = 4.dp) // Space for shadow
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(18.dp))
                .clickable(
                    interactionSource = interactionSource,
                    indication = null, // Custom press effect via scale/gradient
                    enabled = enabled,
                    onClick = onPress
                ),
            shape = RoundedCornerShape(18.dp),
            color = Color.Transparent,
            shadowElevation = if (isPressed) 2.dp else 6.dp,
            tonalElevation = 2.dp
        ) {
            Box(
                modifier = Modifier
                    .background(gradient)
                    .padding(vertical = 16.dp, horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    if (icon != null) {
                        Box(modifier = Modifier.size(22.dp)) {
                            icon()
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                    }
                    
                    Text(
                        text = title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = 0.8.sp,
                            fontSize = 16.sp
                        )
                    )
                    
                    if (rightIcon != null) {
                        Spacer(modifier = Modifier.width(12.dp))
                        Box(modifier = Modifier.size(22.dp)) {
                            rightIcon()
                        }
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CustomButtonPreview() {
    ThripitakaPaperApplicationTheme {
        Box(modifier = Modifier.padding(16.dp)) {
            CustomButton(
                title = "Next",
                onPress = {},
                rightIcon = { Icon(Icons.Default.ArrowForward, null, tint = Color.White) }
            )
        }
    }
}
