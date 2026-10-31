package de.lebensmittelampel.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.lebensmittelampel.app.design.AppDesign
import de.lebensmittelampel.app.design.CountRow
import de.lebensmittelampel.app.design.DText
import de.lebensmittelampel.app.design.DesignButton
import de.lebensmittelampel.app.design.ListHeader
import de.lebensmittelampel.app.design.LocalDesign
import de.lebensmittelampel.app.design.ProductRow
import de.lebensmittelampel.app.design.designBackground

@Composable
fun ProductListScreen(
    viewModel: ProductViewModel,
    hintSeen: Boolean,
    onAdd: () -> Unit,
    onOpen: (Long) -> Unit,
    onPickDesign: () -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val s = LocalDesign.current

    Box(Modifier.fillMaxSize().designBackground(s)) {
        Column(Modifier.fillMaxSize().safeDrawingPadding()) {
            LazyColumn(
                Modifier.weight(1f).fillMaxWidth(),
                contentPadding = PaddingValues(start = 14.dp, end = 14.dp, top = 6.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.spacedBy(if (s.design == AppDesign.ENDZEIT) 2.dp else 8.dp),
            ) {
                item {
                    ListHeader(s, state.allProducts, state.counts, state.today, onLongPress = onPickDesign)
                }
                if (!hintSeen) {
                    item {
                        DText(
                            "Tipp: Lange auf den Titel drücken, um das Design zu wechseln.",
                            s, Modifier.fillMaxWidth(), size = 12.sp, color = s.muted, align = TextAlign.Center, glow = false,
                        )
                    }
                }
                item { CountRow(s, state.counts, state.filter, viewModel::toggleFilter) }
                item {
                    DText(
                        if (s.design == AppDesign.ENDZEIT) "> LISTE" else s.words.listCaption.uppercase(),
                        s, Modifier.padding(top = 4.dp), size = s.smallSize, color = s.muted, glow = false,
                    )
                }
                if (state.products.isEmpty()) {
                    item {
                        DText(
                            if (state.filter == null) s.words.empty else "–",
                            s, Modifier.fillMaxWidth().padding(24.dp), align = TextAlign.Center, color = s.muted,
                        )
                    }
                } else {
                    itemsIndexed(state.products, key = { _, p -> p.id }) { i, p ->
                        ProductRow(s, p, state.today, i) { onOpen(p.id) }
                    }
                }
            }
            DesignButton(s, s.words.add, onAdd, Modifier.padding(start = 14.dp, end = 14.dp, top = 4.dp, bottom = 14.dp))
        }
    }
}
