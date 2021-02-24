package io.github.kedaitayar.mfm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import io.github.kedaitayar.mfm.data.dao.BasicDao
import io.github.kedaitayar.mfm.data.dao.BudgetDao
import io.github.kedaitayar.mfm.data.database.MFMDatabase
import io.github.kedaitayar.mfm.data.entity.*
import io.github.kedaitayar.mfm.data.podata.BudgetListAdapterData
import io.github.kedaitayar.mfm.util.getOrAwaitValue
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.OffsetDateTime
import java.time.ZoneOffset

@RunWith(AndroidJUnit4::class)
class BudgetDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var budgetDao: BudgetDao
    private lateinit var basicDao: BasicDao
    private lateinit var mfmDb: MFMDatabase

    private val budgetTypeList = listOf(
        BudgetType(budgetTypeId = 1, budgetTypeName = "Monthly"),
        BudgetType(budgetTypeId = 2, budgetTypeName = "Yearly"),
        BudgetType(budgetTypeId = 3, budgetTypeName = "Goal/Debt"),
    )
    private val transactionTypeList = listOf(
        TransactionType(transactionTypeId = 1, transactionTypeName = "Expense"),
        TransactionType(transactionTypeId = 2, transactionTypeName = "Income"),
        TransactionType(transactionTypeId = 3, transactionTypeName = "Transfer"),
    )

    @Before
    fun createDb() {
        mfmDb = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            MFMDatabase::class.java
        )
            .allowMainThreadQueries()
//            .createFromAsset("database/mfm_db.db")
            .build()
        basicDao = mfmDb.basicDao()
        budgetDao = mfmDb.budgetDao()

        // insert prepopulate data
        runBlocking {
            basicDao.insert(budgetTypeList[0])
            basicDao.insert(budgetTypeList[1])
            basicDao.insert(budgetTypeList[2])
            basicDao.insert(transactionTypeList[0])
            basicDao.insert(transactionTypeList[1])
            basicDao.insert(transactionTypeList[2])
        }
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        mfmDb.close()
    }

    @Test
    @Throws(IOException::class)
    fun getBudgetMonthlyListAdapterDO() {
        runBlocking {
            val account1 = Account(accountId = 1, accountName = "Cash")
            basicDao.insert(account1)
            val budget1 =
                Budget(budgetId = 1, budgetGoal = 100.0, budgetName = "Food", budgetType = 1)
            val budget2 =
                Budget(budgetId = 2, budgetGoal = 100.0, budgetName = "Fuel", budgetType = 1)
            val budget3 =
                Budget(budgetId = 3, budgetGoal = 100.0, budgetName = "Drink", budgetType = 1)
            val budget4 =
                Budget(budgetId = 4, budgetGoal = 100.0, budgetName = "Internet", budgetType = 1)
            basicDao.insert(budget1)
            basicDao.insert(budget2)
            basicDao.insert(budget3)
            basicDao.insert(budget4)
            val budgetTransaction1 = BudgetTransaction(
                budgetTransactionBudgetId = 1,
                budgetTransactionMonth = 1,
                budgetTransactionYear = 2021,
                budgetTransactionAmount = 10.0
            )
            val budgetTransaction2 = BudgetTransaction(
                budgetTransactionBudgetId = 1,
                budgetTransactionMonth = 2,
                budgetTransactionYear = 2021,
                budgetTransactionAmount = 20.0
            )
            val budgetTransaction3 = BudgetTransaction(
                budgetTransactionBudgetId = 2,
                budgetTransactionMonth = 1,
                budgetTransactionYear = 2021,
                budgetTransactionAmount = 30.0
            )
            val budgetTransaction4 = BudgetTransaction(
                budgetTransactionBudgetId = 2,
                budgetTransactionMonth = 2,
                budgetTransactionYear = 2021,
                budgetTransactionAmount = 40.0
            )
            val budgetTransaction5 = BudgetTransaction(
                budgetTransactionBudgetId = 3,
                budgetTransactionMonth = 1,
                budgetTransactionYear = 2021,
                budgetTransactionAmount = 50.0
            )
            basicDao.insert(budgetTransaction1)
            basicDao.insert(budgetTransaction2)
            basicDao.insert(budgetTransaction3)
            basicDao.insert(budgetTransaction4)
            basicDao.insert(budgetTransaction5)

            val budgetMonthly1 = BudgetListAdapterData(
                budgetId = 1,
                budgetName = "Food",
                budgetAllocation = 10.0,
                budgetGoal = 100.0,
                budgetUsed = 0.0,
                budgetTypeId = 1,
                budgetTypeName = "Monthly",
                budgetTotalPrevAllocation = 0.0
            )

            val budgetMonthly2 = BudgetListAdapterData(
                budgetId = 2,
                budgetName = "Fuel",
                budgetAllocation = 30.0,
                budgetGoal = 100.0,
                budgetUsed = 0.0,
                budgetTypeId = 1,
                budgetTypeName = "Monthly",
                budgetTotalPrevAllocation = 0.0
            )

            val budgetMonthly3 = BudgetListAdapterData(
                budgetId = 3,
                budgetName = "Drink",
                budgetAllocation = 50.0,
                budgetGoal = 100.0,
                budgetUsed = 0.0,
                budgetTypeId = 1,
                budgetTypeName = "Monthly",
                budgetTotalPrevAllocation = 0.0
            )

            val budgetMonthly4 = BudgetListAdapterData(
                budgetId = 4,
                budgetName = "Internet",
                budgetAllocation = 0.0,
                budgetGoal = 100.0,
                budgetUsed = 0.0,
                budgetTypeId = 1,
                budgetTypeName = "Monthly",
                budgetTotalPrevAllocation = 0.0
            )

            val budgetMonthly5 = BudgetListAdapterData(
                budgetId = 1,
                budgetName = "Food",
                budgetAllocation = 20.0,
                budgetGoal = 100.0,
                budgetUsed = 0.0,
                budgetTypeId = 1,
                budgetTypeName = "Monthly",
                budgetTotalPrevAllocation = 0.0
            )

            val budgetMonthly6 = BudgetListAdapterData(
                budgetId = 2,
                budgetName = "Fuel",
                budgetAllocation = 40.0,
                budgetGoal = 100.0,
                budgetUsed = 0.0,
                budgetTypeId = 1,
                budgetTypeName = "Monthly",
                budgetTotalPrevAllocation = 0.0
            )

            val budgetMonthly7 = BudgetListAdapterData(
                budgetId = 3,
                budgetName = "Drink",
                budgetAllocation = 0.0,
                budgetGoal = 100.0,
                budgetUsed = 0.0,
                budgetTypeId = 1,
                budgetTypeName = "Monthly",
                budgetTotalPrevAllocation = 0.0
            )

            val budgetMonthly8 = BudgetListAdapterData(
                budgetId = 4,
                budgetName = "Internet",
                budgetAllocation = 0.0,
                budgetGoal = 100.0,
                budgetUsed = 0.0,
                budgetTypeId = 1,
                budgetTypeName = "Monthly",
                budgetTotalPrevAllocation = 0.0
            )

            val budgetMonthly9 = BudgetListAdapterData(
                budgetId = 1,
                budgetName = "Food",
                budgetAllocation = 0.0,
                budgetGoal = 100.0,
                budgetUsed = 0.0,
                budgetTypeId = 1,
                budgetTypeName = "Monthly",
                budgetTotalPrevAllocation = 0.0
            )

            val budgetMonthly10 = BudgetListAdapterData(
                budgetId = 2,
                budgetName = "Fuel",
                budgetAllocation = 0.0,
                budgetGoal = 100.0,
                budgetUsed = 0.0,
                budgetTypeId = 1,
                budgetTypeName = "Monthly",
                budgetTotalPrevAllocation = 0.0
            )

            val budgetMonthly11 = BudgetListAdapterData(
                budgetId = 3,
                budgetName = "Drink",
                budgetAllocation = 0.0,
                budgetGoal = 100.0,
                budgetUsed = 0.0,
                budgetTypeId = 1,
                budgetTypeName = "Monthly",
                budgetTotalPrevAllocation = 0.0
            )

            val budgetMonthly12 = BudgetListAdapterData(
                budgetId = 4,
                budgetName = "Internet",
                budgetAllocation = 0.0,
                budgetGoal = 100.0,
                budgetUsed = 0.0,
                budgetTypeId = 1,
                budgetTypeName = "Monthly",
                budgetTotalPrevAllocation = 0.0
            )


            val timeFrom1 = OffsetDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0))
            val timeFrom2 = OffsetDateTime.of(2021, 2, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0))
            val timeTo1 = timeFrom1.plusMonths(1).minusNanos(1)
            val timeTo2 = timeFrom2.plusMonths(1).minusNanos(1)
            val realResult1 = budgetDao.getBudgetMonthlyListAdapterDO(1, 2021, timeFrom1, timeTo1).getOrAwaitValue()
            val realResult2 = budgetDao.getBudgetMonthlyListAdapterDO(2, 2021, timeFrom2, timeTo2).getOrAwaitValue()
            val realResult3 = budgetDao.getBudgetMonthlyListAdapterDO(3, 2021, timeFrom2, timeTo2).getOrAwaitValue()

            assertThat(realResult1).contains(budgetMonthly1)
            assertThat(realResult1).contains(budgetMonthly2)
            assertThat(realResult1).contains(budgetMonthly3)
            assertThat(realResult1).contains(budgetMonthly4)
            assertThat(realResult2).contains(budgetMonthly5)
            assertThat(realResult2).contains(budgetMonthly6)
            assertThat(realResult2).contains(budgetMonthly7)
            assertThat(realResult2).contains(budgetMonthly8)
            assertThat(realResult3).contains(budgetMonthly9)
            assertThat(realResult3).contains(budgetMonthly10)
            assertThat(realResult3).contains(budgetMonthly11)
            assertThat(realResult3).contains(budgetMonthly12)
        }


    }
}