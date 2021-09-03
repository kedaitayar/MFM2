package io.github.kedaitayar.mfm.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.kedaitayar.mfm.data.entity.*
import io.github.kedaitayar.mfm.data.entity.Transaction
import kotlinx.coroutines.flow.Flow

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

    @Query("SELECT * FROM account")
    fun getAllAccountFlow(): Flow<List<Account>>

    @Query("SELECT * FROM account WHERE accountId = :accountId")
    fun getAccountByIdFlow(accountId: Long): Flow<Account>

    @Query("SELECT * FROM account WHERE accountId = :accountId")
    suspend fun getAccountById(accountId: Long): Account

    //budget

    @Query("SELECT * FROM budget")
    fun getAllBudget(): LiveData<List<Budget>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(budget: Budget): Long

    @Update
    suspend fun update(budget: Budget): Int

    @Delete
    suspend fun delete(budget: Budget): Int

    @Query("SELECT * FROM budget")
    fun getAllBudgetFlow(): Flow<List<Budget>>

    @Query("SELECT * FROM budget WHERE budgetId = :budgetId")
    fun getBudgetByIdFlow(budgetId: Long): Flow<Budget>

    @Query("SELECT * FROM budget WHERE budgetId = :budgetId")
    suspend fun getBudgetById(budgetId: Long): Budget

    @Update(entity = Budget::class)
    suspend fun updatePosition(budgetPosition: BudgetPosition): Int

    @Update(entity = Budget::class)
    suspend fun updatePosition(budgetPosition: List<BudgetPosition>): Int

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

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(budgetTransaction: BudgetTransaction): Long

    @Update
    suspend fun update(budgetTransaction: BudgetTransaction): Int

    @Delete
    suspend fun delete(budgetTransaction: BudgetTransaction): Int

    @androidx.room.Transaction
    suspend fun upsert(budgetTransaction: BudgetTransaction): Boolean {
        val id = insert(budgetTransaction)
        return if (id == -1L) {
            val result = update(budgetTransaction)
            result >= 1
        } else {
            id >= 1
        }
    }


    //budget type

    @Query("SELECT * FROM budgettype")
    fun getAllBudgetType(): LiveData<List<BudgetType>>

    @Query("SELECT * FROM budgettype")
    fun getAllBudgetTypeFlow(): Flow<List<BudgetType>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(budgetType: BudgetType): Long

    @Update
    suspend fun update(budgetType: BudgetType): Int

    @Delete
    suspend fun delete(budgetType: BudgetType): Int

    //transaction

    @Query("SELECT * FROM `Transaction` ORDER BY transactionId DESC")
    fun getAllTransaction(): LiveData<List<Transaction>>

    @Query("SELECT * FROM `Transaction` WHERE transactionId = :transactionId")
    suspend fun getTransactionById(transactionId: Long): Transaction

    @Insert(onConflict = OnConflictStrategy.IGNORE)
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