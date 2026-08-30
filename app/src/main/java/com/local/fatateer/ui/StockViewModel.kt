package com.local.fatateer.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.local.fatateer.data.AppDatabase
import com.local.fatateer.data.Categories
import com.local.fatateer.data.Item
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class MainTab { HOME, SPARE, SALES }

data class StockUiState(
    val items: List<Item> = emptyList(),
    val query: String = "",
    val selectedCategory: String? = null,
    val tab: MainTab = MainTab.HOME
) {
    private val scopeCategories: List<String>?
        get() = when (tab) {
            MainTab.HOME -> null
            MainTab.SPARE -> Categories.spareParts
            MainTab.SALES -> Categories.sales
        }

    val filtered: List<Item>
        get() {
            var list = items
            scopeCategories?.let { allowed ->
                list = list.filter { it.category in allowed }
            }
            selectedCategory?.let { cat ->
                list = list.filter { it.category == cat }
            }
            val q = query.trim()
            if (q.isNotEmpty()) {
                list = list.filter {
                    it.name.contains(q, true) ||
                        it.notes.contains(q, true) ||
                        it.category.contains(q, true) ||
                        it.subCategory.contains(q, true) ||
                        it.brand.contains(q, true)
                }
            }
            return list
        }

    val grouped: Map<String, List<Item>>
        get() = filtered.groupBy { item ->
            if (item.subCategory.isNotBlank()) {
                "${item.category} › ${item.subCategory}"
            } else {
                item.category
            }
        }

    val neededItems: List<Item>
        get() = items.filter { it.quantity <= it.minQuantity }

    val neededCount: Int get() = neededItems.size
    val spareCount: Int get() = items.count { it.category in Categories.spareParts }
    val salesCount: Int get() = items.count { it.category in Categories.sales }
    val totalQty: Int get() = items.sumOf { it.quantity }
}

class StockViewModel(app: Application) : AndroidViewModel(app) {
    private val dao = AppDatabase.get(app).itemDao()
    private val queryFlow = MutableStateFlow("")
    private val categoryFlow = MutableStateFlow<String?>(null)
    private val tabFlow = MutableStateFlow(MainTab.HOME)

    val state = combine(
        dao.observeAll(),
        queryFlow,
        categoryFlow,
        tabFlow
    ) { list, query, cat, tab ->
        StockUiState(items = list, query = query, selectedCategory = cat, tab = tab)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), StockUiState())

    fun setQuery(value: String) {
        queryFlow.value = value
    }

    fun setCategory(value: String?) {
        categoryFlow.value = value
    }

    fun setTab(tab: MainTab) {
        tabFlow.value = tab
        categoryFlow.value = null
        queryFlow.value = ""
    }

    fun save(item: Item) {
        viewModelScope.launch {
            if (item.id == 0L) dao.insert(item) else dao.update(item)
        }
    }

    fun delete(item: Item) {
        viewModelScope.launch { dao.delete(item) }
    }

    fun plus(item: Item) {
        viewModelScope.launch { dao.changeQuantity(item.id, 1) }
    }

    fun minus(item: Item) {
        if (item.quantity <= 0) return
        viewModelScope.launch { dao.changeQuantity(item.id, -1) }
    }
}
