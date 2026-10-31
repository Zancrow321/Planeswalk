package de.lebensmittelampel.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.lebensmittelampel.app.design.AppDesign
import de.lebensmittelampel.app.design.DText
import de.lebensmittelampel.app.design.DTitle
import de.lebensmittelampel.app.design.DesignTile
import de.lebensmittelampel.app.design.LocalDesign
import de.lebensmittelampel.app.design.designBackground

@Composable
fun DesignPickerScreen(current: AppDesign, onPick: (AppDesign) -> Unit, onBack: () -> Unit) {
    val s = LocalDesign.current
    Box(Modifier.fillMaxSize().designBackground(s)) {
        Column(Modifier.fillMaxSize().safeDrawingPadding()) {
            Row(Modifier.fillMaxWidth().padding(horizontal = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück", tint = s.text)
                }
                DTitle(s.words.pickerTitle, s, Modifier.weight(1f), size = if (s.titleSize.value > 24) 24.sp else s.titleSize)
            }
            DText("Tippe auf ein Design.", s, Modifier.padding(horizontal = 16.dp), size = 13.sp, color = s.muted, glow = false)
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(14.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(AppDesign.entries) { d ->
                    DesignTile(d, d == current, s.text, onClick = { onPick(d) })
                }
            }
        }
    }
}
