package de.lebensmittelampel.app.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import de.lebensmittelampel.app.design.DesignSpec
import de.lebensmittelampel.app.design.LocalDesign

@Composable
fun AppTheme(spec: DesignSpec, content: @Composable () -> Unit) {
    val scheme = if (spec.dark) {
        darkColorScheme(
            primary = spec.accent, onPrimary = spec.onAccent, background = spec.bg, onBackground = spec.text,
            surface = spec.bg, onSurface = spec.text, surfaceContainerHigh = spec.bg,
        )
    } else {
        lightColorScheme(
            primary = spec.accent, onPrimary = spec.onAccent, background = spec.bg, onBackground = spec.text,
            surface = spec.bg, onSurface = spec.text, surfaceContainerHigh = spec.bg,
        )
    }
    CompositionLocalProvider(LocalDesign provides spec) {
        MaterialTheme(colorScheme = scheme, content = content)
    }
}
