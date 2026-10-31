package de.lebensmittelampel.app.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import de.lebensmittelampel.app.data.TrafficLight

val AmpelRot = Color(0xFFE53935)
val AmpelGelb = Color(0xFFFBC02D)
val AmpelGruen = Color(0xFF43A047)

fun TrafficLight.color(): Color = when (this) {
    TrafficLight.RED -> AmpelRot
    TrafficLight.YELLOW -> AmpelGelb
    TrafficLight.GREEN -> AmpelGruen
}

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val scheme = if (isSystemInDarkTheme()) {
        darkColorScheme(primary = Color(0xFF81C784), secondary = Color(0xFFFFD54F))
    } else {
        lightColorScheme(primary = Color(0xFF2E7D32), secondary = Color(0xFFF9A825))
    }
    MaterialTheme(colorScheme = scheme, content = content)
}
