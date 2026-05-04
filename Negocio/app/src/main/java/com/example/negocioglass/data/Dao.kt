package com.example.negocioglass.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BusinessDao {
    @Query("SELECT * FROM businesses ORDER BY createdAt ASC")
    fun getBusinesses(): Flow<List<BusinessEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusiness(business: BusinessEntity): Long

    @Query("DELETE FROM businesses WHERE id = :id")
    suspend fun deleteBusiness(id: Long)
}

@Dao
interface EntryDao {
    @Query("SELECT * FROM entries WHERE businessId = :businessId ORDER BY createdAt DESC")
    fun getEntriesByBusiness(businessId: Long): Flow<List<EntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: EntryEntity)

    @Query("DELETE FROM entries WHERE id = :id")
    suspend fun deleteEntry(id: Long)
}
