package com.example.ksrtc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.BadgeAiravat
import com.example.ui.theme.BadgeAmbari
import com.example.ui.theme.BadgeElectric
import com.example.ui.theme.BadgeRajahamsa
import com.example.ui.theme.BadgeSarige
import com.example.ui.theme.BadgeSleeper
import com.example.ui.theme.BadgeVolvo

@Composable
fun BusTypeBadge(
    busType: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor) = when {
        busType.contains("Sarige", ignoreCase = true) -> BadgeSarige to Color.White
        busType.contains("Ordinary", ignoreCase = true) -> Color(0xFF2E7D32) to Color.White
        busType.contains("Limited", ignoreCase = true) -> Color(0xFF6A1B9A) to Color.White
        busType.contains("Express", ignoreCase = true) -> Color(0xFFE65100) to Color.White
        busType.contains("Rajahamsa", ignoreCase = true) -> BadgeRajahamsa to Color.White
        busType.contains("Airavat", ignoreCase = true) -> BadgeAiravat to Color.White
        busType.contains("Ambari", ignoreCase = true) -> BadgeAmbari to Color.White
        busType.contains("Electric", ignoreCase = true) || busType.contains("EV", ignoreCase = true) -> BadgeElectric to Color.White
        busType.contains("Sleeper", ignoreCase = true) -> BadgeSleeper to Color.White
        busType.contains("Volvo", ignoreCase = true) -> BadgeVolvo to Color.White
        else -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
    }

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = busType,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
