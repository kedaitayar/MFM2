package io.github.kedaitayar.mfm.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.kedaitayar.mfm.data.entity.*
import io.github.kedaitayar.mfm.data.entity.Transaction

@Dao
interface BasicDao {
    //account

    @Query("SELECT * FROM account")
    fun getAllAccount(): LiveData<List<Account>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(account: Account): Long

    @Update
    suspend fun update(account: Account): Int

    @Delete
    suspend fun delete(account: Account): Int

    //budget

    @Query("SELECT * FROM budget")
    fun getAllBudget(): LiveData<List<Budget>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(budget: Budget): Long

    @Update
    suspend fun update(budget: Budget): Int

    @Delete
    suspend fun delete(budget: Budget): Int

    //budget deadline

    @Query("SELECT * FROM budgetdeadline")
    fun getAllBudgetDeadline(): LiveData<List<BudgetDeadline>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(budgetDeadline: BudgetDeadline): Long

    @Update
    suspend fun update(budgetDeadline: BudgetDeadline): Int

    @Delete
    suspend fun delete(budgetDeadline: BudgetDeadline): Int

    //budget transaction

    @Query("SELECT * FROM budgettransaction")
    fun getAllBudgetTransaction(): LiveData<List<BudgetTransaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(budgetTransaction: BudgetTransaction): Long

    @Update
    suspend fun update(budgetTransaction: BudgetTransaction): Int

    @Delete
    suspend fun delete(budgetTransaction: BudgetTransaction): Int

    //budget type

    @Query("SELECT * FROM budgettype")
    fun getAllBudgetType(): LiveData<List<BudgetType>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(budgetType: BudgetType): Long

    @Update
    suspend fun update(budgetType: BudgetType): Int

    @Delete
    suspend fun delete(budgetType: BudgetType): Int

    //transaction

    @Query("SELECT * FROM `Transaction` ORDER BY transactionId DESC")
    fun getAllTransaction(): LiveData<List<Transaction>>

    @Insert
    suspend fun insert(transaction: Transaction): Long

    @Update
    suspend fun update(transaction: Transaction): Int

    @Delete
    suspend fun delete(transaction: Transaction): Int

    @Query("Delete from `transaction`")
    suspend fun deleteAllTransaction()

    //transaction type

    @Query("SELECT * FROM `transactiontype`")
    fun getAllTransactionType(): LiveData<List<TransactionType>>

    @Insert
    suspend fun insert(transactionType: TransactionType): Long

    @Update
    suspend fun update(transactionType: TransactionType): Int

    @Delete
    suspend fun delete(transactionType: TransactionType): Int

}