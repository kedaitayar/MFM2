package io.github.kedaitayar.mfm.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.kedaitayar.mfm.data.entity.TransactionType
import io.github.kedaitayar.mfm.data.podata.TransactionListAdapterData
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.entity.Budget
import io.github.kedaitayar.mfm.data.entity.Transaction
import io.github.kedaitayar.mfm.data.podata.TransactionGraphData
import kotlinx.coroutines.flow.Flow

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
        ORDER BY transactionTime DESC
    """
    )
    fun getTransactionListData(): LiveData<List<TransactionListAdapterData>>

    @Query("""
        SELECT
            STRFTIME('%W', transactionTime) AS transactionWeek,
            Sum(transactionAmount) AS transactionAmount,
            0 AS transactionAmountPrevYear,
            transactionType 
        FROM
            `transaction` 
        WHERE
            transactionType != 3 
            AND STRFTIME('%Y', transactionTime) = :year
        GROUP BY
            STRFTIME('%W', transactionTime),
            transactionType 
        UNION ALL
        SELECT
            '-1' AS transactionWeek,
            0 AS transactionAmount,
            Sum(transactionAmount) AS transactionAmountPrevYear,
            transactionType 
        FROM
            (
                SELECT
                    STRFTIME('%W', transactionTime) AS transactionWeek,
                    Sum(transactionAmount) AS transactionAmount,
                    transactionType 
                FROM
                    `transaction` 
                WHERE
                    transactionType != 3 
                    AND STRFTIME('%Y', transactionTime) < :year
                GROUP BY
                    STRFTIME('%W', transactionTime),
                    transactionType 
                ORDER BY
                    STRFTIME('%W', transactionTime) 
            )
        GROUP BY
            transactionType
    """)
    fun getTransactionGraphData(year: String): Flow<List<TransactionGraphData>> // year as string is because of the STRFTIME function return string
}