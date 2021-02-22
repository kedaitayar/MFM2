package io.github.kedaitayar.mfm.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.kedaitayar.mfm.data.entity.TransactionType
import io.github.kedaitayar.mfm.data.podata.TransactionListAdapterData
import io.github.kedaitayar.mfm2.data.entity.Account
import io.github.kedaitayar.mfm2.data.entity.Budget
import io.github.kedaitayar.mfm2.data.entity.Transaction

@Dao
interface TransactionDao {
    @Query("SELECT * FROM `Transaction` ORDER BY transactionId DESC")
    fun getAllTransaction(): LiveData<List<Transaction>>

    @Insert
    suspend fun insert(transaction: Transaction): Long

    @Delete
    suspend fun delete(transaction: Transaction): Int

    @Query("Delete from `transaction`")
    suspend fun deleteAll()

    @Update
    suspend fun update(transaction: Transaction): Int

    @Query(
        """
        SELECT *, account.accountName AS transactionAccountName, budget.budgetName AS transactionBudgetName, account2.accountName AS transactionAccountTransferToName, transactionType.transactionTypeName as transactionTypeName
        FROM `transaction` 
        LEFT JOIN account ON transactionAccountId = account.accountId 
        LEFT JOIN budget ON transactionBudgetId = budget.budgetId 
        LEFT JOIN account AS account2 ON transactionAccountTransferTo = account2.accountId 
        LEFT JOIN transactiontype ON transactionTypeId = transactionType
        ORDER BY transactionId DESC
    """
    )
    fun getTransactionListData(): LiveData<List<TransactionListAdapterData>>

    @Query(
        """
        SELECT * FROM Account
    """
    )
    fun getAllAccount(): LiveData<List<Account>>

    @Query(
        """
        SELECT * FROM Budget
    """
    )
    fun getAllBudget(): LiveData<List<Budget>>


    // transaction type
    @Query("SELECT * FROM `TransactionType` ORDER BY transactionTypeId DESC")
    fun getAllTransactionType(): LiveData<List<TransactionType>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(transactionType: TransactionType): Long

    @Delete
    suspend fun delete(transactionType: TransactionType): Int

    @Query("Delete from `TransactionType`")
    suspend fun deleteAllTransactionType()

    @Update
    suspend fun update(transactionType: TransactionType): Int
}