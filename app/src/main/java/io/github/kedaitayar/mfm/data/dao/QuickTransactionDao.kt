package io.github.kedaitayar.mfm.data.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.RewriteQueriesToDropUnusedColumns
import io.github.kedaitayar.mfm.data.podata.QuickTransactionListAdapterData
import kotlinx.coroutines.flow.Flow

@Dao
interface QuickTransactionDao {
    @RewriteQueriesToDropUnusedColumns
    @Query(
        """
        SELECT 
            quickTransactionId,
            quickTransactionName,
            transactionAmount,
            transactionType,
            transactionTypeName,
            transactionAccountId,
            account.accountName AS transactionAccountName,
            transactionBudgetId,
            budget.budgetName AS transactionBudgetName,
            transactionAccountTransferTo,
            account2.accountName AS transactionAccountTransferToName, 
            transactionType.transactionTypeName as transactionTypeName,
            transactionNote
        FROM `QuickTransaction` 
        LEFT JOIN account ON transactionAccountId = account.accountId
        LEFT JOIN budget ON transactionBudgetId = budget.budgetId
        LEFT JOIN account AS account2 ON transactionAccountTransferTo = account2.accountId
        LEFT JOIN transactiontype ON transactionTypeId = transactionType
          """
    )
    fun getQuickTransactionList(): Flow<List<QuickTransactionListAdapterData>>
}