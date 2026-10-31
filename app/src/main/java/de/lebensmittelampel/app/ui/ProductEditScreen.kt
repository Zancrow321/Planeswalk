package de.lebensmittelampel.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import de.lebensmittelampel.app.data.Product
import de.lebensmittelampel.app.data.TrafficLight
import de.lebensmittelampel.app.design.AppDesign
import de.lebensmittelampel.app.design.DText
import de.lebensmittelampel.app.design.DTitle
import de.lebensmittelampel.app.design.DateWheel
import de.lebensmittelampel.app.design.DesignButton
import de.lebensmittelampel.app.design.DesignPanel
import de.lebensmittelampel.app.design.DesignTextField
import de.lebensmittelampel.app.design.HazardStripe
import de.lebensmittelampel.app.design.LocalDesign
import de.lebensmittelampel.app.design.MascotBubble
import de.lebensmittelampel.app.design.QuantityWheel
import de.lebensmittelampel.app.design.WheelLabel
import de.lebensmittelampel.app.design.ZoneIndicator
import de.lebensmittelampel.app.design.designBackground
import de.lebensmittelampel.app.scan.BarcodeScanner
import de.lebensmittelampel.app.scan.OpenFoodFacts
import kotlinx.coroutines.launch
import java.time.LocalDate

@Composable
fun ProductEditScreen(
    viewModel: ProductViewModel,
    productId: Long?,
    onDone: () -> Unit,
) {
    val s = LocalDesign.current
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var existing by remember { mutableStateOf<Product?>(null) }
    var name by rememberSaveable { mutableStateOf("") }
    var barcode by rememberSaveable { mutableStateOf("") }
    var expiryEpochDay by rememberSaveable { mutableStateOf(LocalDate.now().toEpochDay()) }
    var quantity by rememberSaveable { mutableStateOf<Int?>(null) }
    var note by rememberSaveable { mutableStateOf("") }
    var status by remember { mutableStateOf<String?>(null) }
    var statusMood by remember { mutableStateOf(TrafficLight.GREEN) }
    var confirmDelete by remember { mutableStateOf(false) }
    var loaded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(productId) {
        if (productId != null && !loaded) {
            viewModel.load(productId)?.let { p ->
                existing = p
                name = p.name
                barcode = p.barcode.orEmpty()
                expiryEpochDay = p.expiryEpochDay
                quantity = p.quantity
                note = p.note.orEmpty()
            }
            loaded = true
        } else if (productId != null && existing == null) {
            existing = viewModel.load(productId)
        }
    }

    fun startScan() {
        BarcodeScanner.scan(
            context,
            onResult = { code ->
                barcode = code
                status = s.words.searching
                statusMood = TrafficLight.YELLOW
                scope.launch {
                    val found = viewModel.knownNameFor(code) ?: OpenFoodFacts.lookupName(code)
                    if (found == null) {
                        status = s.words.notFound
                        statusMood = TrafficLight.YELLOW
                    } else {
                        if (name.isBlank()) name = found
                        status = s.words.found(found)
                        statusMood = TrafficLight.GREEN
                    }
                }
            },
            onError = { status = it; statusMood = TrafficLight.RED },
        )
    }

    val expiry = LocalDate.ofEpochDay(expiryEpochDay)

    Box(Modifier.fillMaxSize().designBackground(s)) {
        Column(
            Modifier
                .fillMaxSize()
                .safeDrawingPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 14.dp, vertical = 6.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (s.design == AppDesign.ZEITREISE) HazardStripe()
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onDone) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück", tint = s.text)
                }
                DTitle(
                    if (productId == null) s.words.newTitle else s.words.editTitle, s,
                    Modifier.weight(1f),
                    size = if (s.design == AppDesign.RETRO || s.design == AppDesign.CARTOON) 24.sp else s.titleSize,
                )
                if (existing != null) {
                    IconButton(onClick = { confirmDelete = true }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Löschen", tint = s.text)
                    }
                }
            }

            DesignButton(s, s.words.scan, { startScan() }, primary = false)
            if (barcode.isNotBlank()) {
                DText("EAN $barcode", s, size = s.smallSize, color = s.muted, glow = false)
            }
            status?.let { msg ->
                if (s.design == AppDesign.KAWAII || s.design == AppDesign.CARTOON) {
                    MascotBubble(s, statusMood, msg)
                } else {
                    DesignPanel(s, Modifier.fillMaxWidth(), selected = true, padding = 8.dp) {
                        DText(msg, s, size = 13.sp, color = s.panelText)
                    }
                }
            }

            DesignTextField(s, s.words.name + " · antippen zum Ändern", name, { name = it })

            WheelLabel(s, s.words.mhd)
            DateWheel(s, expiry) { expiryEpochDay = it.toEpochDay() }
            ZoneIndicator(s, expiry, name)

            WheelLabel(s, s.words.quantity + " (optional)")
            QuantityWheel(s, quantity) { quantity = it }

            DesignTextField(s, s.words.note + " (optional)", note, { note = it }, singleLine = false)

            DesignButton(
                s, s.words.save,
                enabled = name.isNotBlank(),
                onClick = {
                    val base = existing ?: Product(name = "", expiryEpochDay = 0)
                    viewModel.save(
                        base.copy(
                            name = name.trim(),
                            barcode = barcode.ifBlank { null },
                            expiryEpochDay = expiryEpochDay,
                            quantity = quantity,
                            note = note.trim().ifBlank { null },
                        ),
                        onDone,
                    )
                },
                modifier = Modifier.padding(top = 6.dp, bottom = 16.dp),
            )
        }
    }

    if (confirmDelete) {
        AlertDialog(
            onDismissRequest = { confirmDelete = false },
            title = { Text("Produkt löschen?") },
            text = { Text("\"$name\" wird endgültig entfernt.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmDelete = false
                    existing?.let { viewModel.delete(it, onDone) }
                }) { Text("Löschen") }
            },
            dismissButton = { TextButton(onClick = { confirmDelete = false }) { Text("Abbrechen") } },
        )
    }
}

