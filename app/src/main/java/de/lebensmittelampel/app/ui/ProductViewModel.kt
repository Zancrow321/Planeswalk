package de.lebensmittelampel.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import de.lebensmittelampel.app.data.Category
import de.lebensmittelampel.app.data.Product
import de.lebensmittelampel.app.data.ProductRepository
import de.lebensmittelampel.app.data.TrafficLight
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate

data class ListState(
    val all: List<Product> = emptyList(),
    val filter: TrafficLight? = null,
    val today: LocalDate = LocalDate.now(),
) {
    fun page(cat: Category): PageState {
        val inCat = all.filter { it.cat == cat }
        return PageState(
            products = if (filter == null) inCat else inCat.filter { it.trafficLight(today) == filter },
            allProducts = inCat,
            counts = inCat.groupingBy { it.trafficLight(today) }.eachCount(),
        )
    }
}

data class PageState(
    val products: List<Product>,
    val allProducts: List<Product>,
    val counts: Map<TrafficLight, Int>,
)

class ProductViewModel(
    private val repository: ProductRepository,
    private val onSaved: () -> Unit,
) : ViewModel() {

    private val filter = MutableStateFlow<TrafficLight?>(null)

    val state: StateFlow<ListState> = combine(repository.products, filter) { all, f ->
        val today = LocalDate.now()
        ListState(all = all, filter = f, today = today)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ListState())

    fun toggleFilter(light: TrafficLight) {
        filter.value = if (filter.value == light) null else light
    }

    suspend fun load(id: Long): Product? = repository.byId(id)

    suspend fun knownNameFor(barcode: String): String? = repository.lastByBarcode(barcode)?.name

    fun save(product: Product, done: () -> Unit) {
        viewModelScope.launch {
            repository.save(product)
            onSaved()
            done()
        }
    }

    fun delete(product: Product, done: () -> Unit) {
        viewModelScope.launch {
            repository.delete(product)
            done()
        }
    }
}
