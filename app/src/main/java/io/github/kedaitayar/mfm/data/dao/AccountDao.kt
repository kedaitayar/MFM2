package io.github.kedaitayar.mfm.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.*
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.podata.*
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

@Dao
interface AccountDao {

    @Query("Delete FROM account")
    suspend fun deleteAll()

    @Query("SELECT * FROM account WHERE accountId = :accountId")
    suspend fun getAccountById(accountId: Long): Account

    @Query(
        """
            SELECT
                accountTransactionDate,
                SUM(accountTransactionExpense) AS accountTransactionExpense,
                SUM(accountTransactionIncome) AS accountTransactionIncome,
                SUM(accountTransactionTransferIn) AS accountTransactionTransferIn,
                SUM(accountTransactionTransferOut) AS accountTransactionTransferOut,
                (
                    SELECT
                        SUM(CASE WHEN transactionType = 2 THEN transactionAmount ELSE 0 END ) AS accountTransactionIncomePrevMonth 
                    FROM
                        `transaction` 
                    WHERE
                        transactionTime < :timeToPrevMonth 
                        AND transactionAccountId = :accountId 
                    GROUP BY
                        transactionAccountId 
                ) AS accountTransactionIncomePrevMonth, 
                (
                    SELECT
                        SUM( CASE WHEN transactionType = 1 THEN transactionAmount ELSE 0 END ) AS accountTransactionExpensePrevMonth 
                    FROM
                        `transaction` 
                    WHERE
                        transactionTime < :timeToPrevMonth 
                        AND transactionAccountId = :accountId 
                    GROUP BY
                        transactionAccountId 
                ) AS accountTransactionExpensePrevMonth, 
                (
                    SELECT
                        SUM( CASE WHEN transactionType = 3 THEN transactionAmount ELSE 0 END ) AS accountTransactionTransferInPrevMonth 
                    FROM
                        `transaction` 
                    WHERE
                        transactionTime < :timeToPrevMonth 
                        AND transactionAccountTransferTo = :accountId 
                    GROUP BY
                        transactionAccountTransferTo 
                ) AS accountTransactionTransferInPrevMonth, 
                (
                    SELECT
                        SUM( CASE WHEN transactionType = 3 THEN transactionAmount ELSE 0 END ) AS accountTransactionTransferOutPrevMonth 
                    FROM
                        `transaction` 
                    WHERE
                        transactionTime < :timeToPrevMonth 
                        AND transactionAccountId = :accountId 
                    GROUP BY
                        transactionAccountId 
                ) AS accountTransactionTransferOutPrevMonth 
            FROM
                (
                    SELECT
                        date(transactionTime) AS accountTransactionDate,
                        SUM(transactionAmount) AS accountTransactionExpense,
                        0 AS accountTransactionIncome,
                        0 AS accountTransactionTransferIn,
                        0 AS accountTransactionTransferOut 
                    FROM
                        `transaction` 
                    WHERE
                        transactionType = 1 
                        AND transactionTime BETWEEN :timeFrom AND :timeTo 
                        AND transactionAccountId = :accountId 
                    GROUP BY
                        date(transactionTime) 
                    UNION
                    SELECT
                        date(transactionTime) AS accountTransactionDate,
                        0 AS accountTransactionExpense,
                        SUM(transactionAmount) AS accountTransactionIncome,
                        0 AS accountTransactionTransferIn,
                        0 AS accountTransactionTransferOut 
                    FROM
                        `transaction` 
                    WHERE
                        transactionType = 2 
                        AND transactionTime BETWEEN :timeFrom AND :timeTo 
                        AND transactionAccountId = :accountId 
                    GROUP BY
                        date(transactionTime) 
                    UNION
                    SELECT
                        date(transactionTime) AS accountTransactionDate,
                        0 AS accountTransactionExpense,
                        0 AS accountTransactionIncome,
                        SUM(transactionAmount) AS accountTransactionTransferIn,
                        0 AS accountTransactionTransferOut 
                    FROM
                        `transaction` 
                    WHERE
                        transactionType = 3 
                        AND transactionTime BETWEEN :timeFrom AND :timeTo 
                        AND transactionAccountId = :accountId 
                    GROUP BY
                        date(transactionTime) 
                    UNION
                    SELECT
                        date(transactionTime) AS accountTransactionDate,
                        0 AS accountTransactionExpense,
                        0 AS accountTransactionIncome,
                        0 AS accountTransactionTransferIn,
                        SUM(transactionAmount) AS accountTransactionTransferOut 
                    FROM
                        `transaction` 
                    WHERE
                        transactionType = 3 
                        AND transactionTime BETWEEN :timeFrom AND :timeTo 
                        AND transactionAccountId = :accountId 
                    GROUP BY
                        date(transactionTime) 
                )
            GROUP BY
                accountTransactionDate
        """
    )
    fun getAccountTransactionChartData(
        accountId: Long,
        timeFrom: OffsetDateTime,
        timeTo: OffsetDateTime,
        timeToPrevMonth: OffsetDateTime
    ): Flow<List<AccountTransactionChartData>>

    @Query(
        """
        SELECT account.accountId, accountName, SUM(accountIncome) AS accountIncome, SUM(accountExpense) AS accountExpense, SUM(accountTransferIn) AS accountTransferIn, SUM(accountTransferOut) AS accountTransferOut 
        FROM account 
        LEFT JOIN 
            (SELECT transactionAccountId AS accountId, SUM(CASE WHEN transactionType = 2 THEN transactionAmount ELSE 0 END) AS accountIncome, SUM(CASE WHEN transactionType = 1 THEN transactionAmount ELSE 0 END) AS accountExpense, 0 AS accountTransferIn, SUM(CASE WHEN transactionType = 3 THEN transactionAmount ELSE 0 END) AS accountTransferOut 
            FROM `transaction` 
            GROUP BY transactionAccountId 
            UNION 
            SELECT transactionAccountTransferTo AS accountId, 0 AS accountIncome, 0 AS accountExpense, SUM(CASE WHEN transactionType = 3 THEN transactionAmount ELSE 0 END) AS accountTransferIn, 0 AS accountTransferOut 
            FROM `transaction` 
            WHERE transactionType = 3 
            GROUP BY transactionAccountTransferTo) AS tbl ON account.accountId = tbl.accountId 
        GROUP BY account.accountId"""
    )
    fun getAccountListData(): LiveData<List<AccountListAdapterData>>

    @Query(
        """
        SELECT account.accountId, accountName, SUM(accountIncome) AS accountIncome, SUM(accountExpense) AS accountExpense, SUM(accountTransferIn) AS accountTransferIn, SUM(accountTransferOut) AS accountTransferOut 
        FROM account 
        LEFT JOIN 
            (SELECT transactionAccountId AS accountId, SUM(CASE WHEN transactionType = 2 THEN transactionAmount ELSE 0 END) AS accountIncome, SUM(CASE WHEN transactionType = 1 THEN transactionAmount ELSE 0 END) AS accountExpense, 0 AS accountTransferIn, SUM(CASE WHEN transactionType = 3 THEN transactionAmount ELSE 0 END) AS accountTransferOut 
            FROM `transaction` 
            GROUP BY transactionAccountId 
            UNION 
            SELECT transactionAccountTransferTo AS accountId, 0 AS accountIncome, 0 AS accountExpense, SUM(CASE WHEN transactionType = 3 THEN transactionAmount ELSE 0 END) AS accountTransferIn, 0 AS accountTransferOut 
            FROM `transaction` 
            WHERE transactionType = 3 
            GROUP BY transactionAccountTransferTo) AS tbl ON account.accountId = tbl.accountId 
        GROUP BY account.accountId"""
    )
    fun getAccountListDataFlow(): Flow<List<AccountListAdapterData>>

    @Query("SELECT SUM(budgetTransactionAmount) FROM budgettransaction")
    fun getTotalBudgetedAmount(): Flow<Double?>

    @Query("SELECT SUM(transactionAmount) FROM `transaction` WHERE transactionType = 2")
    fun getTotalIncome(): Flow<Double?>

    @Query(
        """
        SELECT SUM(transactionAmount) as transactionAmount
        FROM `Transaction`
        WHERE transactionTime BETWEEN :timeFrom AND :timeTo
        AND transactionType = 1
    """
    )
    fun getMonthSpending(timeFrom: OffsetDateTime, timeTo: OffsetDateTime): Flow<Double?>

    @Query(
        """
        SELECT
            SUM(budgetTransactionAmount) as budgetTransactionAmount
        FROM
            budget 
            LEFT JOIN
                budgettype 
                ON budgetType = budgetTypeId 
            LEFT JOIN
                budgettransaction 
                ON budgetId = budgetTransactionBudgetId 
        WHERE
            (
                budgetTransactionMonth = :month 
                AND budgetTransactionYear = :year
            )
    """
    )
    fun getMonthBudgeted(month: Int, year: Int): Flow<Double>

    @Query(
        """
        SELECT
            SUM(budgetGoal) as budgetGoal,
            SUM(budgetTransactionAmount) as budgetTransactionAmount,
            SUM(uncompletedGoal) as uncompletedGoal
        FROM (
            SELECT
                budgetGoal,
                budgetTransactionAmount,
                CASE
                    WHEN (budgetGoal - budgetTransactionAmount) > 0 THEN  (budgetGoal - budgetTransactionAmount)
                    WHEN (budgetGoal - budgetTransactionAmount) <= 0 THEN 0
                END uncompletedGoal
            FROM
                budget 
                LEFT JOIN
                    budgettype 
                    ON budgetType = budgetTypeId 
                LEFT JOIN
                    budgettransaction 
                    ON budgetId = budgetTransactionBudgetId 
            WHERE
                (
                    budgetTransactionMonth = :month 
                    AND budgetTransactionYear = :year 
                    AND budgetTypeId != 2
                )
                /*OR 
                (
                    budgetTransactionYear = :year 
                    AND budgetTypeId = 2
                )*/
        )
    """
    )
    fun getUncompletedBudget(month: Int, year: Int): Flow<BudgetedAndGoal>

    @Query(
        """
        SELECT
            sum(transactionAmount) AS transactionAmount, 
            transactionType, 
            transactionBudgetId, 
            budgetGoal, 
            budgetName, 
            budgetType
        FROM `Transaction`
        LEFT JOIN Budget ON transactionBudgetId = budgetId
        WHERE transactionAccountId = :accountId
            AND NOT transactionType = 2
            AND NOT transactionType = 3
            AND transactionTime BETWEEN :timeFrom AND :timeTo
        GROUP BY transactionBudgetId
        ORDER BY transactionAmount DESC
    """
    )
    fun getAccountTransactionBudget(
        accountId: Long,
        timeFrom: OffsetDateTime,
        timeTo: OffsetDateTime
    ): Flow<List<AccountTransactionBudgetData>>

    @Query(
        """
        SELECT SUM(budgetTransactionAmount)
        FROM budgettransaction
        WHERE budgetTransactionMonth = strftime("%m", "now")
            AND budgetTransactionYear = strftime("%Y", "now")
        GROUP BY budgetTransactionMonth, budgetTransactionYear
    """
    )
    fun getThisMonthBudgetedAmount(): Flow<Float?>

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
        WHERE transactionAccountId = :accountId
        OR transactionAccountTransferTo = :accountId
        ORDER BY transactionTime DESC
    """
    )
    fun getTransactionListByAccountData(accountId: Long): PagingSource<Int, TransactionListAdapterData>

    @Query(
        """
        SELECT
            transactionWeek,
            SUM(transactionAmount) AS transactionAmount,
            SUM(transactionAmountPrevYear) AS transactionAmountPrevYear,
            transactionType,
            transactionAgeInWeek 
        FROM
            (
                SELECT
                    STRFTIME('%W', transactionTime) AS transactionWeek,
                    SUM(transactionAmount) AS transactionAmount,
                    0 AS transactionAmountPrevYear,
                    transactionType,
                    CAST((JulianDay("now") - JulianDay(transactionTime)) / 7 AS INTEGER) AS transactionAgeInWeek 
                FROM
                    `transaction` 
                WHERE
                    transactionTime > DATE('now', '-1 years') 
                    AND transactionType != 3 
                    AND transactionAccountId = :accountId 
                GROUP BY
                    CAST((JulianDay("now") - JulianDay(transactionTime)) / 7 AS INTEGER),
                    transactionType 
                UNION ALL
                SELECT
                    '-1' AS transactionWeek,
                    0 AS transactionAmount,
                    SUM(transactionAmount) AS transactionAmountPrevYear,
                    transactionType,
                    '-1' AS transactionAgeInWeek 
                FROM
                    (
                        SELECT
                            STRFTIME('%W', transactionTime) AS transactionWeek,
                            SUM(transactionAmount) AS transactionAmount,
                            transactionType,
                            transactionTime 
                        FROM
                            `transaction` 
                        WHERE
                            transactionTime < DATE('now', '-2 years') 
                            AND transactionType != 3 
                            AND transactionAccountId = :accountId 
                        GROUP BY
                            CAST((JulianDay("now") - JulianDay(transactionTime)) / 7 AS INTEGER),
                            transactionType 
                    )
                GROUP BY
                    transactionType 
                UNION ALL
                SELECT
                    STRFTIME('%W', transactionTime) AS transactionWeek,
                    SUM(transactionAmount) AS transactionAmount,
                    0 AS transactionAmountPrevYear,
                    2 AS transactionType,
                    CAST((JulianDay("now") - JulianDay(transactionTime)) / 7 AS INTEGER) AS transactionAgeInWeek 
                FROM
                    `transaction` 
                WHERE
                    transactionAccountTransferTo = :accountId 
                    AND transactionType = 3 
                GROUP BY
                    transactionWeek 
                UNION ALL
                SELECT
                    STRFTIME('%W', transactionTime) AS transactionWeek,
                    SUM(transactionAmount) AS transactionAmount,
                    0 AS transactionAmountPrevYear,
                    1 AS transactionType,
                    CAST((JulianDay("now") - JulianDay(transactionTime)) / 7 AS INTEGER) AS transactionAgeInWeek 
                FROM
                    `transaction` 
                WHERE
                    transactionAccountId = :accountId
                    AND transactionType = 3 
                GROUP BY
                    transactionWeek
            )
        GROUP BY
            transactionAgeInWeek,
            transactionType 
        ORDER BY
            transactionAgeInWeek
    """
    )
    fun getTransactionByAccountGraphData(accountId: Long): Flow<List<TransactionGraphData>>
}