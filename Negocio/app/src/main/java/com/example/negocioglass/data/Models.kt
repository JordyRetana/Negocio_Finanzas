package com.example.negocioglass.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "businesses")
data class BusinessEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val icon: String,
    val accent: String,
    val createdAt: Long = System.currentTimeMillis()
)

enum class EntryType { EXPENSE, SALE }

enum class DateFilter { ALL, TODAY, WEEK, MONTH }

@Entity(
    tableName = "entries",
    foreignKeys = [
        ForeignKey(
            entity = BusinessEntity::class,
            parentColumns = ["id"],
            childColumns = ["businessId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("businessId")]
)
data class EntryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val businessId: Long,
    val title: String,
    val category: String,
    val amount: Double,
    val quantity: Int = 1,
    val type: EntryType,
    val note: String = "",
    val createdAt: Long = System.currentTimeMillis()
)

data class BusinessSummary(
    val totalSales: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val salesCount: Int = 0,
    val expenseCount: Int = 0
) {
    val balance: Double get() = totalSales - totalExpenses
}
