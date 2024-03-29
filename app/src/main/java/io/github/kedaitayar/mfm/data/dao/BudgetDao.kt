package io.github.kedaitayar.mfm.data.dao

import androidx.lifecycle.LiveData
import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import io.github.kedaitayar.mfm.data.podata.*
import kotlinx.coroutines.flow.Flow
import java.time.OffsetDateTime

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
            0 AS budgetTotalPrevAllocation,
            budgetPosition
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
            0 AS budgetTotalPrevAllocation,
            budgetPosition
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
        ORDER BY budgetPosition ASC
    """
    )
    fun getBudgetMonthlyListAdapterFlow(
        month: Int,
        year: Int,
        timeFrom: OffsetDateTime,
        timeTo: OffsetDateTime
    ): Flow<List<BudgetListAdapterData>>

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
            SUM(budgetTotalPrevAllocation) AS budgetTotalPrevAllocation,
            budgetPosition
        FROM
            (
                SELECT
                    budgetId,
                    budgetName,
                    0 AS budgetAllocation,
                    budgetGoal,
                    0 AS budgetUsed,
                    budgetTypeId,
                    budgetTypeName,
                    SUM(budgetTransactionAmount) AS budgetTotalPrevAllocation,
                    budgetPosition
                FROM
                    Budget 
                    LEFT JOIN
                        budgettype 
                        ON budgetType = budgetTypeId 
                    LEFT JOIN
                        budgettransaction 
                        ON budgetId = budgetTransactionBudgetId 
                WHERE
                    budgetType = 2
                    AND budgetTransactionMonth < :month
                    AND budgetTransactionYear <= :year
                GROUP BY
                    budgetId 
                UNION ALL
                SELECT
                    budgetId,
                    budgetName,
                    budgetTransactionAmount AS budgetAllocation,
                    budgetGoal,
                    0 AS budgetUsed,
                    budgetTypeId,
                    budgetTypeName,
                    0 AS budgetTotalPrevAllocation,
                    budgetPosition
                FROM
                    Budget 
                    LEFT JOIN
                        budgettype 
                        ON budgetType = budgetTypeId 
                    LEFT JOIN
                        budgettransaction 
                        ON budgetId = budgetTransactionBudgetId 
                WHERE
                    budgetType = 2
                    AND budgetTransactionMonth = :month
                    AND budgetTransactionYear = :year
                UNION ALL
                SELECT
                    budgetId,
                    budgetName,
                    0 AS budgetAllocation,
                    budgetGoal,
                    transactionAmount AS budgetUsed,
                    budgetTypeId,
                    budgetTypeName,
                    0 AS budgetTotalPrevAllocation,
                    budgetPosition
                FROM
                    `Transaction` 
                    LEFT JOIN
                        Budget 
                        ON transactionBudgetId = budgetId 
                    LEFT JOIN
                        budgettype 
                        ON budgetType = budgetTypeId 
                WHERE
                    budgetType = 2
                    AND transactionTime BETWEEN :timeFrom AND :timeTo
                UNION
                SELECT
                    budgetId,
                    budgetName,
                    0 AS budgetAllocation,
                    budgetGoal,
                    0 AS budgetUsed,
                    budgetTypeId,
                    budgetTypeName,
                    0 AS budgetTotalPrevAllocation,
                    budgetPosition
                FROM
                    Budget 
                    LEFT JOIN
                        budgettype 
                        ON budgetType = budgetTypeId 
                    LEFT JOIN
                        budgettransaction 
                        ON budgetId = budgetTransactionBudgetId 
                WHERE
                    Budget.budgetType = 2
                    AND NOT EXISTS 
                    (
                        SELECT
                            * 
                        FROM
                            budgettransaction 
                        WHERE
                            BudgetTransaction.budgetTransactionBudgetId = Budget.budgetId 
                            AND budgetTransactionMonth = :month
                            AND budgetTransactionYear = :year
                    )
            )
        GROUP BY
            budgetId
        ORDER BY budgetPosition ASC
    """
    )
    fun getBudgetYearlyListAdapterFlow(
        month: Int,
        year: Int,
        timeFrom: OffsetDateTime,
        timeTo: OffsetDateTime
    ): Flow<List<BudgetListAdapterData>>

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
            0 AS budgetTotalPrevAllocation,
            budgetPosition
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
            0 AS budgetTotalPrevAllocation,
            budgetPosition
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
        ORDER BY budgetPosition ASC
    """
    )
    fun getBudgetMonthlyListAdapter(
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
            SUM(budgetTotalPrevAllocation) AS budgetTotalPrevAllocation,
            budgetPosition
        FROM
            (
                SELECT
                    budgetId,
                    budgetName,
                    0 AS budgetAllocation,
                    budgetGoal,
                    0 AS budgetUsed,
                    budgetTypeId,
                    budgetTypeName,
                    SUM(budgetTransactionAmount) AS budgetTotalPrevAllocation,
                    budgetPosition
                FROM
                    Budget 
                    LEFT JOIN
                        budgettype 
                        ON budgetType = budgetTypeId 
                    LEFT JOIN
                        budgettransaction 
                        ON budgetId = budgetTransactionBudgetId 
                WHERE
                    budgetType = 2
                    AND budgetTransactionMonth < :month
                    AND budgetTransactionYear <= :year
                GROUP BY
                    budgetId 
                UNION ALL
                SELECT
                    budgetId,
                    budgetName,
                    budgetTransactionAmount AS budgetAllocation,
                    budgetGoal,
                    0 AS budgetUsed,
                    budgetTypeId,
                    budgetTypeName,
                    0 AS budgetTotalPrevAllocation,
                    budgetPosition
                FROM
                    Budget 
                    LEFT JOIN
                        budgettype 
                        ON budgetType = budgetTypeId 
                    LEFT JOIN
                        budgettransaction 
                        ON budgetId = budgetTransactionBudgetId 
                WHERE
                    budgetType = 2
                    AND budgetTransactionMonth = :month
                    AND budgetTransactionYear = :year
                UNION ALL
                SELECT
                    budgetId,
                    budgetName,
                    0 AS budgetAllocation,
                    budgetGoal,
                    transactionAmount AS budgetUsed,
                    budgetTypeId,
                    budgetTypeName,
                    0 AS budgetTotalPrevAllocation,
                    budgetPosition
                FROM
                    `Transaction` 
                    LEFT JOIN
                        Budget 
                        ON transactionBudgetId = budgetId 
                    LEFT JOIN
                        budgettype 
                        ON budgetType = budgetTypeId 
                WHERE
                    budgetType = 2
                    AND transactionTime BETWEEN :timeFrom AND :timeTo
                UNION
                SELECT
                    budgetId,
                    budgetName,
                    0 AS budgetAllocation,
                    budgetGoal,
                    0 AS budgetUsed,
                    budgetTypeId,
                    budgetTypeName,
                    0 AS budgetTotalPrevAllocation,
                    budgetPosition
                FROM
                    Budget 
                    LEFT JOIN
                        budgettype 
                        ON budgetType = budgetTypeId 
                    LEFT JOIN
                        budgettransaction 
                        ON budgetId = budgetTransactionBudgetId 
                WHERE
                    Budget.budgetType = 2
                    AND NOT EXISTS 
                    (
                        SELECT
                            * 
                        FROM
                            budgettransaction 
                        WHERE
                            BudgetTransaction.budgetTransactionBudgetId = Budget.budgetId 
                            AND budgetTransactionMonth = :month
                            AND budgetTransactionYear = :year
                    )
            )
        GROUP BY
            budgetId
        ORDER BY budgetPosition ASC
    """
    )
    fun getBudgetYearlyListAdapter(
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
            SUM(budgetTotalPrevAllocation) AS budgetTotalPrevAllocation,
            budgetPosition
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
                    0 AS budgetTotalPrevAllocation,
                    budgetPosition
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
                    0 AS budgetTotalPrevAllocation,
                    budgetPosition
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
                    SUM(budgetTransactionAmount) AS budgetTotalPrevAllocation,
                    budgetPosition
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
            0 AS budgetTotalPrevAllocation,
            budgetPosition
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
                        transactionTime BETWEEN :timeFrom AND :timeTo 
                        AND budgetType = 1
                    GROUP BY
                        transactionBudgetId 
                )
                AS tbl 
                ON budgetId = transactionBudgetId 
        WHERE
            budgetTransactionMonth = :month 
            AND budgetTransactionYear = :year 
            AND budgetTypeId = 1
        UNION
            SELECT
                budgetId,
                budgetName,
                0 AS budgetAllocation,
                budgetGoal,
                tbl.transactionAmount AS budgetUsed,
                budgetTypeId,
                budgetTypeName,
                0 AS budgetTotalPrevAllocation,
                budgetPosition
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
                        transactionTime BETWEEN :timeFrom AND :timeTo 
                        AND budgetType = 1
                    GROUP BY
                        transactionBudgetId 
                )
                AS tbl 
                ON budgetId = transactionBudgetId
            WHERE budgetType = 1
                AND NOT EXISTS 
                    (
                        SELECT
                            * 
                        FROM
                            budgettransaction 
                        WHERE
                            BudgetTransaction.budgetTransactionBudgetId = Budget.budgetId 
                            AND budgetTransactionMonth = :month
                            AND budgetTransactionYear = :year
                    )
    """
    )
    fun getMonthlyBudgetingListAdapterDO(
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
            SUM(budgetTotalPrevAllocation) AS budgetTotalPrevAllocation,
            budgetPosition
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
                    0 AS budgetTotalPrevAllocation,
                    budgetPosition
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
                    0 AS budgetTotalPrevAllocation,
                    budgetPosition
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
                                    AND budgetType = 2 
                                )
                            GROUP BY
                                transactionBudgetId 
                        )
                        AS tbl 
                        ON budgetId = transactionBudgetId 
                WHERE
                    budget.budgetType = 2 
                    AND NOT EXISTS 
                    (
                        SELECT
                            * 
                        FROM
                            budgettransaction 
                        WHERE
                            BudgetTransaction.budgetTransactionBudgetId = Budget.budgetId 
                            AND budgetTransactionMonth = :month 
                            AND budgetTransactionYear = :year
                    )
                UNION All
                SELECT
                    budgetId,
                    budgetName,
                    0 AS budgetAllocation,
                    budgetGoal,
                    0 AS budgetUsed,
                    budgetTypeId,
                    budgetTypeName,
                    SUM(budgetTransactionAmount) AS budgetTotalPrevAllocation,
                    budgetPosition
                FROM
                    Budget 
                    LEFT JOIN
                        budgettype 
                        ON budgetType = budgetTypeId 
                    LEFT JOIN
                        budgettransaction 
                        ON budgetId = budgetTransactionBudgetId 
                WHERE
                    budgetType = 2 
                    AND budgetTransactionMonth < :month
                    AND budgetTransactionYear <= :year
                GROUP BY
                    budgetId 
            )
        GROUP BY
            budgetId
    """
    )
    fun getYearlyBudgetingListAdapterDO(
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
            budgetTransactionAmount AS budgetAllocation,
            budgetGoal,
            tbl.transactionAmount AS budgetUsed,
            budgetTypeId,
            budgetTypeName,
            0 AS budgetTotalPrevAllocation,
            budgetPosition
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
                        transactionTime BETWEEN :timeFrom AND :timeTo 
                        AND budgetType = 1
                    GROUP BY
                        transactionBudgetId 
                )
                AS tbl 
                ON budgetId = transactionBudgetId 
        WHERE
            budgetTransactionMonth = :month 
            AND budgetTransactionYear = :year 
            AND budgetTypeId = 1
        UNION
            SELECT
                budgetId,
                budgetName,
                0 AS budgetAllocation,
                budgetGoal,
                tbl.transactionAmount AS budgetUsed,
                budgetTypeId,
                budgetTypeName,
                0 AS budgetTotalPrevAllocation,
                budgetPosition
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
                        transactionTime BETWEEN :timeFrom AND :timeTo 
                        AND budgetType = 1
                    GROUP BY
                        transactionBudgetId 
                )
                AS tbl 
                ON budgetId = transactionBudgetId
            WHERE budgetType = 1
                AND NOT EXISTS 
                    (
                        SELECT
                            * 
                        FROM
                            budgettransaction 
                        WHERE
                            BudgetTransaction.budgetTransactionBudgetId = Budget.budgetId 
                            AND budgetTransactionMonth = :month
                            AND budgetTransactionYear = :year
                    )
        ORDER BY budgetPosition ASC
    """
    )
    fun getMonthlyBudgetingListAdapterFlow(
        month: Int,
        year: Int,
        timeFrom: OffsetDateTime,
        timeTo: OffsetDateTime
    ): Flow<List<BudgetListAdapterData>>

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
            SUM(budgetTotalPrevAllocation) AS budgetTotalPrevAllocation,
            budgetPosition
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
                    0 AS budgetTotalPrevAllocation,
                    budgetPosition
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
                    0 AS budgetTotalPrevAllocation,
                    budgetPosition
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
                                    AND budgetType = 2 
                                )
                            GROUP BY
                                transactionBudgetId 
                        )
                        AS tbl 
                        ON budgetId = transactionBudgetId 
                WHERE
                    budget.budgetType = 2 
                    AND NOT EXISTS 
                    (
                        SELECT
                            * 
                        FROM
                            budgettransaction 
                        WHERE
                            BudgetTransaction.budgetTransactionBudgetId = Budget.budgetId 
                            AND budgetTransactionMonth = :month 
                            AND budgetTransactionYear = :year
                    )
                UNION All
                SELECT
                    budgetId,
                    budgetName,
                    0 AS budgetAllocation,
                    budgetGoal,
                    0 AS budgetUsed,
                    budgetTypeId,
                    budgetTypeName,
                    SUM(budgetTransactionAmount) AS budgetTotalPrevAllocation,
                    budgetPosition
                FROM
                    Budget 
                    LEFT JOIN
                        budgettype 
                        ON budgetType = budgetTypeId 
                    LEFT JOIN
                        budgettransaction 
                        ON budgetId = budgetTransactionBudgetId 
                WHERE
                    budgetType = 2 
                    AND budgetTransactionMonth < :month
                    AND budgetTransactionYear <= :year
                GROUP BY
                    budgetId 
            )
        GROUP BY
            budgetId
        ORDER BY budgetPosition ASC
    """
    )
    fun getYearlyBudgetingListAdapterFlow(
        month: Int,
        year: Int,
        timeFrom: OffsetDateTime,
        timeTo: OffsetDateTime
    ): Flow<List<BudgetListAdapterData>>

    @Query(
        """
        SELECT budgetId, budgetName, budgetType, budgetTransactionMonth, budgetTransactionYear, budgetTransactionAmount, transactionAmount
        FROM Budget
        LEFT JOIN
            budgettype
            ON budgetType = budgetTypeId
        LEFT JOIN
            budgettransaction
            ON budgetId = budgetTransactionBudgetId
        LEFT JOIN (
            SELECT transactionBudgetId, transactionType, sum(transactionAmount) as transactionAmount, strftime('%m', transactionTime) as month, strftime('%Y', transactionTime) as year
            FROM `Transaction`
            WHERE transactionBudgetId != ''
            GROUP BY transactionBudgetId, month, year
            ORDER BY year DESC, month DESC, transactionBudgetId
        ) as tbl
            ON BudgetTransaction.budgetTransactionMonth = tbl.month
            AND BudgetTransaction.budgetTransactionYear = tbl.year
            AND Budget.budgetId = tbl.transactionBudgetId
    """
    )
    fun getBudgetTransactionJoinTransaction(): LiveData<List<BudgetTransactionJoinTransaction>>

    @Query(
        """
        SELECT budgetId, budgetName, budgetType, budgetTransactionMonth, budgetTransactionYear, budgetTransactionAmount, transactionAmount
        FROM Budget
        LEFT JOIN
            budgettype
            ON budgetType = budgetTypeId
        LEFT JOIN
            budgettransaction
            ON budgetId = budgetTransactionBudgetId
        LEFT JOIN (
            SELECT transactionBudgetId, transactionType, sum(transactionAmount) as transactionAmount, strftime('%m', transactionTime) as month, strftime('%Y', transactionTime) as year
            FROM `Transaction`
            WHERE transactionBudgetId != ''
            GROUP BY transactionBudgetId, month, year
            ORDER BY year DESC, month DESC, transactionBudgetId
        ) as tbl
            ON BudgetTransaction.budgetTransactionMonth = tbl.month
            AND BudgetTransaction.budgetTransactionYear = tbl.year
            AND Budget.budgetId = tbl.transactionBudgetId
    """
    )
    fun getBudgetTransactionJoinTransactionFlow(): Flow<List<BudgetTransactionJoinTransaction>>

    @Query(
        """
        SELECT budgetId, budgetName, budgetType, budgetTransactionMonth, budgetTransactionYear, budgetTransactionAmount, transactionAmount
        FROM Budget
        LEFT JOIN
            budgettype
            ON budgetType = budgetTypeId
        LEFT JOIN
            budgettransaction
            ON budgetId = budgetTransactionBudgetId
        LEFT JOIN (
            SELECT transactionBudgetId, transactionType, sum(transactionAmount) as transactionAmount, strftime('%m', transactionTime) as month, strftime('%Y', transactionTime) as year
            FROM `Transaction`
            WHERE transactionBudgetId != ''
            GROUP BY transactionBudgetId, month, year
            ORDER BY year DESC, month DESC, transactionBudgetId
        ) as tbl
            ON BudgetTransaction.budgetTransactionMonth = tbl.month
            AND BudgetTransaction.budgetTransactionYear = tbl.year
            AND Budget.budgetId = tbl.transactionBudgetId
    """
    )
    suspend fun getBudgetTransactionJoinTransactionSuspend(): List<BudgetTransactionJoinTransaction>

    @Query("SELECT SUM(budgetTransactionAmount) FROM budgettransaction")
    fun getTotalBudgetedAmount(): Flow<Double>

    @Query("SELECT SUM(transactionAmount) FROM `transaction` WHERE transactionType = 2")
    fun getTotalIncome(): Flow<Double>

    @Query(
        """
        SELECT
            sum(transactionAmount) AS transactionAmount,
            budgetId,
            budgetGoal,
            budgetName,
            budgetType,
            (
                Select
                    sum(transactionAmount) 
                FROM
                    `Transaction` 
                    LEFT JOIN
                        Budget 
                        ON transactionBudgetId = budgetId 
                WHERE
                    NOT transactionType = 2 
                    AND NOT transactionType = 3
            )
            as totalTransactionAmount 
        FROM
            `Transaction` 
            LEFT JOIN
                Budget 
                ON transactionBudgetId = budgetId 
        WHERE
            NOT transactionType = 2 
            AND NOT transactionType = 3 
        GROUP BY
            transactionBudgetId 
        ORDER BY
            transactionAmount DESC
    """
    )
    fun getBudgetTransactionAmountList(): Flow<List<BudgetTransactionAmountList>>

    @Query(
        """
        SELECT
            sum(transactionAmount) AS transactionAmount,
            budgetId,
            budgetGoal,
            budgetName,
            budgetType,
            (
                Select
                    sum(transactionAmount) 
                FROM
                    `Transaction` 
                    LEFT JOIN
                        Budget 
                        ON transactionBudgetId = budgetId 
                WHERE
                    NOT transactionType = 2 
                    AND NOT transactionType = 3
                    AND transactionTime BETWEEN :timeFrom AND :timeTo
            )
            as totalTransactionAmount 
        FROM
            `Transaction` 
            LEFT JOIN
                Budget 
                ON transactionBudgetId = budgetId 
        WHERE
            NOT transactionType = 2 
            AND NOT transactionType = 3
            AND transactionTime BETWEEN :timeFrom AND :timeTo
        GROUP BY
            transactionBudgetId 
        ORDER BY
            transactionAmount DESC
    """
    )
    fun getBudgetTransactionAmountList(
        timeFrom: OffsetDateTime,
        timeTo: OffsetDateTime
    ): Flow<List<BudgetTransactionAmountList>>

    @Query(
        """
        SELECT SUM(transactionAmount) as monthSpending , strftime('%Y-%m-%dT%H:%M:%SZ',transactionTime, 'start of month') as month
        FROM `transaction` 
        WHERE transactionType = 1 
            AND transactionTime > DATE('now', '-1 years')
            AND transactionBudgetId = :budgetId
        GROUP BY STRFTIME('%Y-%m', transactionTime)
        ORDER BY transactionTime DESC
    """
    )
    fun getMonthlySpendingGraphDataByBudget(budgetId: Long): Flow<List<MonthlySpendingData>>

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
        WHERE transactionBudgetId = :budgetId
        ORDER BY transactionTime DESC
    """
    )
    fun getTransactionListByBudgetData(budgetId: Long): PagingSource<Int, TransactionListAdapterData>
}