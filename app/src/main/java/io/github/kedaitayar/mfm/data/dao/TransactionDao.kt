package io.github.kedaitayar.mfm.data.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import io.github.kedaitayar.mfm.data.podata.MonthlySpendingData
import io.github.kedaitayar.mfm.data.podata.TransactionGraphData
import io.github.kedaitayar.mfm.data.podata.TransactionListAdapterData
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Query(
        """
        SELECT 
            transactionId,
            transactionAmount,
            transactionTime,
            transactionTypeId,
            transactionTypeName,
            transactionAccountId,
            account.accountName AS transactionAccountName,
            transactionBudgetId,
            budget.budgetName AS transactionBudgetName,
            transactionAccountTransferTo,
            account2.accountName AS transactionAccountTransferToName, 
            transactionType.transactionTypeName as transactionTypeName,
            transactionNote
        FROM `transaction`
        LEFT JOIN account ON transactionAccountId = account.accountId
        LEFT JOIN budget ON transactionBudgetId = budget.budgetId
        LEFT JOIN account AS account2 ON transactionAccountTransferTo = account2.accountId
        LEFT JOIN transactiontype ON transactionTypeId = transactionType
        ORDER BY transactionTime DESC
    """
    )
    fun getTransactionListData(): PagingSource<Int, TransactionListAdapterData>
//    fun getTransactionListData(): Flow<List<TransactionListAdapterData>>

    @Query(
        """
        SELECT
            STRFTIME('%W', transactionTime) AS transactionWeek,
            Sum(transactionAmount) AS transactionAmount,
            0 AS transactionAmountPrevYear,
            transactionType,
            CAST((JulianDay("now") - JulianDay(transactionTime))/7 As Integer) AS transactionAgeInWeek
        FROM
            `transaction` 
        WHERE
            transactionType != 3 
            --AND STRFTIME('%Y', transactionTime) = :year
            AND transactionTime > DATE('now', '-1 years')
        GROUP BY
            --STRFTIME('%W', transactionTime),
            CAST((JulianDay("now") - JulianDay(transactionTime))/7 As Integer),
            transactionType 
        UNION ALL
        SELECT
            '-1' AS transactionWeek,
            0 AS transactionAmount,
            Sum(transactionAmount) AS transactionAmountPrevYear,
            transactionType,
            '-1' AS transactionAgeInWeek
        FROM
            (
                SELECT
                    STRFTIME('%W', transactionTime) AS transactionWeek,
                    Sum(transactionAmount) AS transactionAmount,
                    transactionType,
                    transactionTime
                FROM
                    `transaction` 
                WHERE
                    transactionType != 3 
                    --AND STRFTIME('%Y', transactionTime) < :year
                    AND transactionTime < DATE('now', '-1 years')
                GROUP BY
                    --STRFTIME('%W', transactionTime),
                    CAST((JulianDay("now") - JulianDay(transactionTime))/7 As Integer),
                    transactionType 
                --ORDER BY
                    --STRFTIME('%W', transactionTime) 
            )
        GROUP BY
            transactionType
        ORDER BY
            transactionAgeInWeek
    """
    )
    fun getTransactionGraphData(): Flow<List<TransactionGraphData>> // year as string is because of the STRFTIME function return string

    @Query(
        """
        SELECT SUM(transactionAmount) as monthSpending , strftime('%Y-%m-%dT%H:%M:%SZ',transactionTime, 'start of month') as month
        FROM `transaction` 
        WHERE transactionType = 1 
            AND transactionTime BETWEEN DATE('now', 'start of month', '-1 years', '+1 month') AND DATE('now', 'start of month', '+1 month')
        GROUP BY STRFTIME('%Y-%m', transactionTime)
        ORDER BY transactionTime DESC
    """
    )
    fun getMonthlySpendingGraphData(): Flow<List<MonthlySpendingData>>
}