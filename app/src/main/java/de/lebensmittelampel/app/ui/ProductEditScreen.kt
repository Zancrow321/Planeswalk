package de.lebensmittelampel.app.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDatePickerState
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import de.lebensmittelampel.app.data.Product
import de.lebensmittelampel.app.data.TrafficLight
import de.lebensmittelampel.app.data.remainingText
import de.lebensmittelampel.app.scan.BarcodeScanner
import de.lebensmittelampel.app.scan.OpenFoodFacts
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

private val DATE = DateTimeFormatter.ofPattern("dd.MM.yyyy")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductEditScreen(
    viewModel: ProductViewModel,
    productId: Long?,
    onDone: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var existing by remember { mutableStateOf<Product?>(null) }
    var name by rememberSaveable { mutableStateOf("") }
    var barcode by rememberSaveable { mutableStateOf("") }
    var expiryEpochDay by rememberSaveable { mutableStateOf<Long?>(null) }
    var quantity by rememberSaveable { mutableStateOf("") }
    var note by rememberSaveable { mutableStateOf("") }
    var status by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var confirmDelete by remember { mutableStateOf(false) }
    var loaded by rememberSaveable { mutableStateOf(false) }

    LaunchedEffect(productId) {
        if (productId != null && !loaded) {
            viewModel.load(productId)?.let { p ->
                existing = p
                name = p.name
                barcode = p.barcode.orEmpty()
                expiryEpochDay = p.expiryEpochDay
                quantity = p.quantity?.toString().orEmpty()
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
                status = "Suche Produktnamen …"
                scope.launch {
                    val found = viewModel.knownNameFor(code) ?: OpenFoodFacts.lookupName(code)
                    status = when {
                        found == null -> "Kein Name zum Barcode gefunden, bitte selbst eintragen."
                        else -> {
                            if (name.isBlank()) name = found
                            "Gefunden: $found"
                        }
                    }
                }
            },
            onError = { status = it },
        )
    }

    val expiry = expiryEpochDay?.let(LocalDate::ofEpochDay)
    val canSave = name.isNotBlank() && expiry != null

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (productId == null) "Neues Produkt" else "Produkt bearbeiten") },
                navigationIcon = {
                    IconButton(onClick = onDone) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Zurück")
                    }
                },
                actions = {
                    if (existing != null) {
                        IconButton(onClick = { confirmDelete = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Löschen")
                        }
                    }
                },
            )
        },
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedButton(onClick = { startScan() }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.QrCodeScanner, contentDescription = null)
                Text("  Barcode scannen")
            }
            if (barcode.isNotBlank()) {
                Text("Barcode: $barcode", style = MaterialTheme.typography.bodySmall)
            }
            status?.let { Text(it, style = MaterialTheme.typography.bodySmall) }

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )

            OutlinedButton(onClick = { showDatePicker = true }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.CalendarMonth, contentDescription = null)
                Text(if (expiry == null) "  MHD auswählen" else "  MHD: ${expiry.format(DATE)}")
            }
            if (expiry != null) {
                val light = TrafficLight.of(expiry)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        "Ampel: ${light.label} · ${remainingText(expiry)}",
                        color = light.color(),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

            OutlinedTextField(
                value = quantity,
                onValueChange = { v -> quantity = v.filter(Char::isDigit).take(4) },
                label = { Text("Menge (optional)") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = note,
                onValueChange = { note = it },
                label = { Text("Notiz (optional)") },
                modifier = Modifier.fillMaxWidth(),
            )

            Button(
                enabled = canSave,
                onClick = {
                    val base = existing ?: Product(name = "", expiryEpochDay = 0)
                    viewModel.save(
                        base.copy(
                            name = name.trim(),
                            barcode = barcode.ifBlank { null },
                            expiryEpochDay = expiryEpochDay!!,
                            quantity = quantity.toIntOrNull(),
                            note = note.trim().ifBlank { null },
                        ),
                        onDone,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) { Text("Speichern") }
        }
    }

    if (showDatePicker) {
        val pickerState = rememberDatePickerState(
            initialSelectedDateMillis = (expiry ?: LocalDate.now())
                .atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    pickerState.selectedDateMillis?.let { ms ->
                        expiryEpochDay = Instant.ofEpochMilli(ms).atZone(ZoneOffset.UTC).toLocalDate().toEpochDay()
                    }
                    showDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Abbrechen") } },
        ) {
            DatePicker(state = pickerState)
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
