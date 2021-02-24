package io.github.kedaitayar.mfm.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.kedaitayar.mfm.data.podata.BudgetListAdapterData
import java.time.OffsetDateTime
import java.util.*

@Dao
interface BudgetDao {
    @Query(
        """
        SELECT
            budgetId,
            budgetName,
            budgetTransactionAmount AS budgetAllocation,
            budgetGoal,
            tbl.transactionAmount AS budgetUsed,
            budgetTypeId,
            budgetTypeName,
            0 AS budgetTotalPrevAllocation
        FROM
            budget
            LEFT JOIN
                budgettype
                ON budgetType = budgetTypeId
            LEFT JOIN
                budgettransaction
                ON budgetId = budgetTransactionBudgetId
            LEFT JOIN
                (
                    SELECT
                        transactionBudgetId,
                        SUM(transactionAmount) AS transactionAmount
                    FROM
                        `transaction`
                    WHERE
                        transactionTime BETWEEN :timeFrom AND :timeTo
                    GROUP BY
                        transactionBudgetId
                )
                AS tbl
                ON budgetId = transactionBudgetId
        WHERE
            budgetTransactionMonth = :month
            AND budgetTransactionYear = :year
            AND budgetType = 1
        UNION
        SELECT
            budgetId,
            budgetName,
            0 AS budgetAllocation,
            budgetGoal,
            tbl.transactionAmount AS budgetUsed,
            budgetTypeId,
            budgetTypeName,
            0 AS budgetTotalPrevAllocation
        FROM
            budget
            LEFT JOIN
                budgettype
                ON budgetType = budgetTypeId
            LEFT JOIN
                (
                    SELECT
                        transactionBudgetId,
                        SUM(transactionAmount) AS transactionAmount
                    FROM
                        `transaction`
                    WHERE
                        transactionTime BETWEEN :timeFrom AND :timeTo
                    GROUP BY
                        transactionBudgetId
                )
                AS tbl
                ON budgetId = transactionBudgetId
        WHERE
            budgetType = 1
            AND NOT EXISTS
            (
                SELECT
                    *
                FROM
                    budgettransaction
                WHERE
                    budget.budgetId = budgettransaction.budgetTransactionBudgetId
                    AND budgetTransactionMonth = :month
                    AND budgetTransactionYear = :year
            )
            AND budgetType = 1
    """
    )
    fun getBudgetMonthlyListAdapterDO(
        month: Int,
        year: Int,
        timeFrom: OffsetDateTime,
        timeTo: OffsetDateTime
    ): LiveData<List<BudgetListAdapterData>>


    @Query(
        """
        SELECT
            budgetId,
            budgetName,
            SUM(budgetAllocation) AS budgetAllocation,
            budgetGoal,
            SUM(budgetUsed) AS budgetUsed,
            budgetTypeId,
            budgetTypeName,
            SUM(budgetTotalPrevAllocation) AS budgetTotalPrevAllocation 
        FROM
            (
                SELECT
                    budgetId,
                    budgetName,
                    budgetTransactionAmount AS budgetAllocation,
                    budgetGoal,
                    tbl.transactionAmount AS budgetUsed,
                    budgetTypeId,
                    budgetTypeName,
                    0 AS budgetTotalPrevAllocation 
                FROM
                    budget 
                    LEFT JOIN
                        budgettype 
                        ON budgetType = budgetTypeId 
                    LEFT JOIN
                        budgettransaction 
                        ON budgetId = budgetTransactionBudgetId 
                    LEFT JOIN
                        (
                            SELECT
                                transactionBudgetId,
                                SUM(transactionAmount) AS transactionAmount 
                            FROM
                                `transaction` 
                                LEFT JOIN
                                    Budget 
                                    ON transactionBudgetId = budgetId 
                            WHERE
                                (
                                    transactionTime BETWEEN :timeFrom AND :timeTo 
                                    AND budgetType != 2
                                )
                                OR 
                                (
                                    strftime('%Y', transactionTime) = :year 
                                    AND budgetType = 2
                                )
                            GROUP BY
                                transactionBudgetId 
                        )
                        AS tbl 
                        ON budgetId = transactionBudgetId 
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
                UNION
                SELECT
                    budgetId,
                    budgetName,
                    0 AS budgetAllocation,
                    budgetGoal,
                    tbl.transactionAmount AS budgetUsed,
                    budgetTypeId,
                    budgetTypeName,
                    0 AS budgetTotalPrevAllocation 
                FROM
                    budget 
                    LEFT JOIN
                        budgettype 
                        ON budgetType = budgetTypeId 
                    LEFT JOIN
                        (
                            SELECT
                                transactionBudgetId,
                                SUM(transactionAmount) AS transactionAmount 
                            FROM
                                `transaction` 
                                LEFT JOIN
                                    Budget 
                                    ON transactionBudgetId = budgetId 
                            WHERE
                                (
                                    transactionTime BETWEEN :timeFrom AND :timeTo 
                                    AND budgetType != 2
                                )
                                OR 
                                (
                                    strftime('%Y', transactionTime) = :year 
                                    AND budgetType = 2
                                )
                            GROUP BY
                                transactionBudgetId 
                        )
                        AS tbl 
                        ON budgetId = transactionBudgetId
                UNION ALL
                SELECT
                    budgetId,
                    budgetName,
                    0 AS budgetAllocation,
                    budgetGoal,
                    0 AS budgetUsed,
                    budgetTypeId,
                    budgetTypeName,
                    SUM(budgetTransactionAmount) AS budgetTotalPrevAllocation 
                FROM
                    Budget 
                    LEFT JOIN
                        budgettype 
                        ON budgetType = budgetTypeId 
                    LEFT JOIN
                        budgettransaction 
                        ON budgetId = budgetTransactionBudgetId 
                WHERE
                    budgetType = 3 
                    AND budgetTransactionMonth < :month 
                    AND budgetTransactionYear <= :year 
                GROUP BY
                    budgetId 
            )
        GROUP BY
            budgetId 
        ORDER BY
            budgetTypeId,
            budgetId
    """
    )
    fun getBudgetingListAdapterDO(
        month: Int,
        year: Int,
        timeFrom: OffsetDateTime,
        timeTo: OffsetDateTime
    ): LiveData<List<BudgetListAdapterData>>
}