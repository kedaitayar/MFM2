package io.github.kedaitayar.mfm.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.kedaitayar.mfm.data.entity.TransactionType
import io.github.kedaitayar.mfm.data.podata.TransactionListAdapterData
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.entity.Budget
import io.github.kedaitayar.mfm.data.entity.Transaction

@Dao
interface TransactionDao {

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

}