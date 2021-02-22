package io.github.kedaitayar.mfm.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.kedaitayar.mfm2.data.entity.Budget
import io.github.kedaitayar.mfm2.data.entity.BudgetType
import java.util.*

@Dao
interface BudgetDao {

    //budget
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(budget: Budget): Long

    @Update
    suspend fun update(budget: Budget): Int

    @Delete
    suspend fun delete(budget: Budget): Int

    @Query("SELECT * FROM budget")
    fun getAllBudget(): LiveData<List<Budget>>

    //budgetType
    @Query("SELECT * FROM budgettype")
    fun getAllBudgetTypeLV(): LiveData<List<BudgetType>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(budgetType: BudgetType): Long

    @Update
    suspend fun update(budgetType: BudgetType): Int

    @Delete
    suspend fun delete(budgetType: BudgetType): Int

    @Query("Delete from budgettype")
    suspend fun deleteAll()

    @Query("SELECT * FROM budgettype")
    suspend fun getAllBudgetType(): List<BudgetType>


}