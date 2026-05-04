package com.example.negocioglass.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.negocioglass.data.AppDatabase
import com.example.negocioglass.data.BusinessEntity
import com.example.negocioglass.data.BusinessSummary
import com.example.negocioglass.data.DateFilter
import com.example.negocioglass.data.EntryEntity
import com.example.negocioglass.data.EntryType
import com.example.negocioglass.data.NegocioRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class NegocioViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = NegocioRepository(db.businessDao(), db.entryDao())

    val businesses: StateFlow<List<BusinessEntity>> = repository.getBusinesses()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())


    fun entriesForBusiness(businessId: Long): StateFlow<List<EntryEntity>> {
        return repository.getEntries(businessId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    }

    fun summaryForBusiness(businessId: Long): StateFlow<BusinessSummary> {
        return repository.getEntries(businessId)
            .map { entries ->
                val totalSales = entries
                    .filter { it.type == EntryType.SALE }
                    .sumOf { it.amount }

                val totalExpenses = entries
                    .filter { it.type == EntryType.EXPENSE }
                    .sumOf { it.amount }

                BusinessSummary(
                    totalSales = totalSales,
                    totalExpenses = totalExpenses,
                    salesCount = entries.count { it.type == EntryType.SALE },
                    expenseCount = entries.count { it.type == EntryType.EXPENSE }
                )
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5_000),
                BusinessSummary()
            )
    }

    fun filteredEntriesForBusiness(
        businessId: Long,
        filter: DateFilter
    ): StateFlow<List<EntryEntity>> {
        return repository.getEntries(businessId)
            .map { list -> list.filterBy(filter) }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    }

    fun addBusiness(name: String, icon: String, accent: String) = viewModelScope.launch {
        if (name.isNotBlank()) {
            repository.addBusiness(
                name = name.trim(),
                icon = icon.ifBlank { "✨" },
                accent = accent.ifBlank { "blue" }
            )
        }
    }

    fun deleteBusiness(id: Long) = viewModelScope.launch {
        repository.deleteBusiness(id)
    }

    fun addEntry(
        businessId: Long,
        title: String,
        category: String,
        amountText: String,
        quantityText: String,
        type: EntryType,
        note: String
    ) = viewModelScope.launch {
        val unitAmount = amountText
            .replace("₡", "")
            .replace(",", ".")
            .trim()
            .toDoubleOrNull() ?: return@launch

        val quantity = quantityText.trim().toIntOrNull()?.coerceAtLeast(1) ?: 1

        if (title.isBlank() || category.isBlank()) return@launch

        val totalAmount = unitAmount * quantity

        repository.addEntry(
            businessId = businessId,
            title = title.trim(),
            category = category.trim(),
            amount = totalAmount,
            quantity = quantity,
            type = type,
            note = note.trim()
        )
    }

    fun deleteEntry(id: Long) = viewModelScope.launch {
        repository.deleteEntry(id)
    }


    private fun List<EntryEntity>.filterBy(filter: DateFilter): List<EntryEntity> {
        val now = System.currentTimeMillis()
        val day = 24L * 60 * 60 * 1000

        return when (filter) {
            DateFilter.ALL -> this
            DateFilter.TODAY -> filter { now - it.createdAt <= day }
            DateFilter.WEEK -> filter { now - it.createdAt <= day * 7 }
            DateFilter.MONTH -> filter { now - it.createdAt <= day * 30 }
        }
    }

    companion object {
        fun factory(application: Application): ViewModelProvider.Factory =
            object : ViewModelProvider.AndroidViewModelFactory(application) {}
    }
}