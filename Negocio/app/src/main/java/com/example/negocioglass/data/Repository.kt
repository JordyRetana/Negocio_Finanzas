package com.example.negocioglass.data

import kotlinx.coroutines.flow.Flow

class NegocioRepository(
    private val businessDao: BusinessDao,
    private val entryDao: EntryDao
) {
    fun getBusinesses(): Flow<List<BusinessEntity>> = businessDao.getBusinesses()

    fun getEntries(businessId: Long): Flow<List<EntryEntity>> = entryDao.getEntriesByBusiness(businessId)

    suspend fun addBusiness(name: String, icon: String, accent: String) {
        businessDao.insertBusiness(BusinessEntity(name = name, icon = icon, accent = accent))
    }

    suspend fun deleteBusiness(id: Long) {
        businessDao.deleteBusiness(id)
    }

    suspend fun addEntry(
        businessId: Long,
        title: String,
        category: String,
        amount: Double,
        quantity: Int,
        type: EntryType,
        note: String
    ) {
        entryDao.insertEntry(
            EntryEntity(
                businessId = businessId,
                title = title,
                category = category,
                amount = amount,
                quantity = quantity,
                type = type,
                note = note
            )
        )
    }

    suspend fun deleteEntry(id: Long) {
        entryDao.deleteEntry(id)
    }
}
