package de.lebensmittelampel.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import de.lebensmittelampel.app.data.Product
import de.lebensmittelampel.app.data.TrafficLight
import de.lebensmittelampel.app.data.remainingText
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    viewModel: ProductViewModel,
    onAdd: () -> Unit,
    onOpen: (Long) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = { TopAppBar(title = { Text("Lebensmittel-Ampel") }) },
        floatingActionButton = {
            FloatingActionButton(onClick = onAdd) {
                Icon(Icons.Filled.Add, contentDescription = "Produkt hinzufügen")
            }
        },
    ) { padding ->
        Column(Modifier.padding(padding).fillMaxSize()) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                TrafficLight.entries.forEach { light ->
                    FilterChip(
                        selected = state.filter == light,
                        onClick = { viewModel.toggleFilter(light) },
                        label = { Text("${light.label}: ${state.counts[light] ?: 0}") },
                        leadingIcon = { Dot(light) },
                    )
                }
            }
            if (state.products.isEmpty()) {
                Box(Modifier.fillMaxSize().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text(
                        if (state.filter == null) {
                            "Noch keine Produkte. Tippe auf + um eins hinzuzufügen."
                        } else {
                            "Keine Produkte im Bereich ${state.filter!!.label}."
                        },
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(state.products, key = { it.id }) { product ->
                        ProductRow(product, state.today) { onOpen(product.id) }
                    }
                }
            }
        }
    }
}

@Composable
private fun Dot(light: TrafficLight, size: Int = 12) {
    Box(Modifier.size(size.dp).background(light.color(), CircleShape))
}

@Composable
private fun ProductRow(product: Product, today: LocalDate, onClick: () -> Unit) {
    val light = product.trafficLight(today)
    Card(Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Dot(light, size = 20)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                val qty = product.quantity?.let { " · ${it}x" } ?: ""
                Text(
                    "MHD ${product.expiry.format(DATE)}$qty",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    remainingText(product.expiry, today),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (light == TrafficLight.RED) light.color() else MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
