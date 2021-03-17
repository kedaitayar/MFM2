package io.github.kedaitayar.mfm.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.kedaitayar.mfm.data.podata.AccountListAdapterData
import io.github.kedaitayar.mfm.data.podata.AccountTransactionChartData
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.podata.AccountTransactionBudgetData
import io.github.kedaitayar.mfm.data.podata.BudgetedAndGoal
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
    suspend fun getAccountTransactionChartData(
        accountId: Long,
        timeFrom: OffsetDateTime,
        timeTo: OffsetDateTime,
        timeToPrevMonth: OffsetDateTime
    ): List<AccountTransactionChartData>

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

    @Query("SELECT SUM(budgetTransactionAmount) FROM budgettransaction")
    fun getTotalBudgetedAmount(): LiveData<Double>

    @Query("SELECT SUM(transactionAmount) FROM `transaction` WHERE transactionType = 2")
    fun getTotalIncome(): LiveData<Double>

    @Query(
        """
        SELECT SUM(transactionAmount) as transactionAmount
        FROM `Transaction`
        WHERE transactionTime BETWEEN :timeFrom AND :timeTo
        AND transactionType = 1
    """
    )
    fun getMonthSpending(timeFrom: OffsetDateTime, timeTo: OffsetDateTime): LiveData<Double>

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
                AND budgetTypeId != 2
            )
            OR 
            (
                budgetTransactionYear = :year 
                AND budgetTypeId = 2
            )
    """
    )
    fun getMonthBudgeted(month: Int, year: Int): LiveData<Double>

    @Query(
        """
        SELECT
            SUM(budgetGoal) as budgetGoal,
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
                AND budgetTypeId != 2
            )
            OR 
            (
                budgetTransactionYear = :year 
                AND budgetTypeId = 2
            )
    """
    )
    fun getUncompletedBudget(month: Int, year: Int): LiveData<BudgetedAndGoal>

    @Query("""
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
    """)
    suspend fun getAccountTransactionBudget(accountId: Long, timeFrom: OffsetDateTime, timeTo: OffsetDateTime): List<AccountTransactionBudgetData>

}