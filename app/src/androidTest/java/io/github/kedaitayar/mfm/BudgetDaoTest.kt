package io.github.kedaitayar.mfm

import android.util.Log
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

private const val TAG = "BudgetDaoTest"

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

    private fun populateTestData(){
        runBlocking {
            val account1 = Account(accountId = 1, accountName = "Cash")
            val account2 = Account(accountId = 2, accountName = "Bank")
            basicDao.insert(account1)
            basicDao.insert(account2)
            val budget1 = Budget(budgetId = 1, budgetGoal = 100.0, budgetName = "Food", budgetType = 1)
            val budget2 = Budget(budgetId = 2, budgetGoal = 100.0, budgetName = "Fuel", budgetType = 1)
            val budget3 = Budget(budgetId = 3, budgetGoal = 20.0, budgetName = "Drink", budgetType = 1)
            val budget4 = Budget(budgetId = 4, budgetGoal = 100.0, budgetName = "Internet", budgetType = 1)
            val budget5 = Budget(budgetId = 5, budgetGoal = 1000.0, budgetName = "Vacation", budgetType = 2)
            val budget6 = Budget(budgetId = 6, budgetGoal = 500.0, budgetName = "Car Maintenance", budgetType = 2)
            basicDao.insert(budget1)
            basicDao.insert(budget2)
            basicDao.insert(budget3)
            basicDao.insert(budget4)
            basicDao.insert(budget5)
            basicDao.insert(budget6)

            //budgeting
            val budgetTransaction1 = BudgetTransaction(
                budgetTransactionBudgetId = 1,
                budgetTransactionMonth = 5,
                budgetTransactionYear = 2021,
                budgetTransactionAmount = 10.0
            )
            val budgetTransaction2 = BudgetTransaction(
                budgetTransactionBudgetId = 1,
                budgetTransactionMonth = 6,
                budgetTransactionYear = 2021,
                budgetTransactionAmount = 20.0
            )
            val budgetTransaction3 = BudgetTransaction(
                budgetTransactionBudgetId = 2,
                budgetTransactionMonth = 5,
                budgetTransactionYear = 2021,
                budgetTransactionAmount = 30.0
            )
            val budgetTransaction4 = BudgetTransaction(
                budgetTransactionBudgetId = 2,
                budgetTransactionMonth = 6,
                budgetTransactionYear = 2021,
                budgetTransactionAmount = 40.0
            )
            val budgetTransaction5 = BudgetTransaction(
                budgetTransactionBudgetId = 3,
                budgetTransactionMonth = 5,
                budgetTransactionYear = 2021,
                budgetTransactionAmount = 50.0
            )
            val budgetTransaction6 = BudgetTransaction(
                budgetTransactionBudgetId = 5,
                budgetTransactionMonth = 4,
                budgetTransactionYear = 2021,
                budgetTransactionAmount = 300.0
            )
            val budgetTransaction7 = BudgetTransaction(
                budgetTransactionBudgetId = 5,
                budgetTransactionMonth = 5,
                budgetTransactionYear = 2021,
                budgetTransactionAmount = 50.0
            )
            val budgetTransaction8 = BudgetTransaction(
                budgetTransactionBudgetId = 5,
                budgetTransactionMonth = 6,
                budgetTransactionYear = 2021,
                budgetTransactionAmount = 100.0
            )
            basicDao.insert(budgetTransaction1)
            basicDao.insert(budgetTransaction2)
            basicDao.insert(budgetTransaction3)
            basicDao.insert(budgetTransaction4)
            basicDao.insert(budgetTransaction5)
            basicDao.insert(budgetTransaction6)
            basicDao.insert(budgetTransaction7)
            basicDao.insert(budgetTransaction8)

            //transaction
            //income
            val transaction1 = Transaction(
                transactionId = 1,
                transactionAmount = 1000.0,
                transactionTime = OffsetDateTime.of(2021, 5, 1, 0, 1, 0, 0, ZoneOffset.ofTotalSeconds(0)),
                transactionType = 2,
                transactionAccountId = 1,
                transactionBudgetId = null,
                transactionAccountTransferTo = null
            )
            //transfer
            val transaction2 = Transaction(
                transactionId = 2,
                transactionAmount = 200.0,
                transactionTime = OffsetDateTime.of(2021, 5, 2, 0, 1, 0, 0, ZoneOffset.ofTotalSeconds(0)),
                transactionType = 3,
                transactionAccountId = 1,
                transactionBudgetId = null,
                transactionAccountTransferTo = 2
            )
            //expense
            val transaction3 = Transaction(
                transactionId = 3,
                transactionAmount = 5.0,
                transactionTime = OffsetDateTime.of(2021, 5, 3, 0, 1, 0, 0, ZoneOffset.ofTotalSeconds(0)),
                transactionType = 1,
                transactionAccountId = 1,
                transactionBudgetId = 1,
                transactionAccountTransferTo = null
            )
            val transaction4 = Transaction(
                transactionId = 4,
                transactionAmount = 30.0,
                transactionTime = OffsetDateTime.of(2021, 5, 4, 0, 1, 0, 0, ZoneOffset.ofTotalSeconds(0)),
                transactionType = 1,
                transactionAccountId = 1,
                transactionBudgetId = 2,
                transactionAccountTransferTo = null
            )
            val transaction5 = Transaction(
                transactionId = 5,
                transactionAmount = 50.0,
                transactionTime = OffsetDateTime.of(2021, 5, 4, 0, 1, 0, 0, ZoneOffset.ofTotalSeconds(0)),
                transactionType = 1,
                transactionAccountId = 1,
                transactionBudgetId = 3,
                transactionAccountTransferTo = null
            )
            val transaction6 = Transaction(
                transactionId = 6,
                transactionAmount = 15.0,
                transactionTime = OffsetDateTime.of(2021, 6, 3, 0, 1, 0, 0, ZoneOffset.ofTotalSeconds(0)),
                transactionType = 1,
                transactionAccountId = 1,
                transactionBudgetId = 1,
                transactionAccountTransferTo = null
            )
            val transaction7 = Transaction(
                transactionId = 7,
                transactionAmount = 40.0,
                transactionTime = OffsetDateTime.of(2021, 6, 4, 0, 1, 0, 0, ZoneOffset.ofTotalSeconds(0)),
                transactionType = 1,
                transactionAccountId = 1,
                transactionBudgetId = 2,
                transactionAccountTransferTo = null
            )
            val transaction8 = Transaction(
                transactionId = 8,
                transactionAmount = 50.0,
                transactionTime = OffsetDateTime.of(2021, 6, 5, 0, 1, 0, 0, ZoneOffset.ofTotalSeconds(0)),
                transactionType = 1,
                transactionAccountId = 1,
                transactionBudgetId = 5,
                transactionAccountTransferTo = null
            )
            basicDao.insert(transaction1)
            basicDao.insert(transaction2)
            basicDao.insert(transaction3)
            basicDao.insert(transaction4)
            basicDao.insert(transaction5)
            basicDao.insert(transaction6)
            basicDao.insert(transaction7)
            basicDao.insert(transaction8)
        }
    }

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
    fun getBudgetMonthlyListAdapter() {
        populateTestData()
        runBlocking {
            // expected result
            // result1
            val budgetMonthly1 = BudgetListAdapterData(
                budgetId = 1,
                budgetName = "Food",
                budgetAllocation = 0.0,
                budgetGoal = 100.0,
                budgetUsed = 0.0,
                budgetTypeId = 1,
                budgetTypeName = "Monthly",
                budgetTotalPrevAllocation = 0.0
            )

            val budgetMonthly2 = BudgetListAdapterData(
                budgetId = 2,
                budgetName = "Fuel",
                budgetAllocation = 0.0,
                budgetGoal = 100.0,
                budgetUsed = 0.0,
                budgetTypeId = 1,
                budgetTypeName = "Monthly",
                budgetTotalPrevAllocation = 0.0
            )

            val budgetMonthly3 = BudgetListAdapterData(
                budgetId = 3,
                budgetName = "Drink",
                budgetAllocation = 0.0,
                budgetGoal = 20.0,
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

            // result2
            val budgetMonthly5 = BudgetListAdapterData(
                budgetId = 1,
                budgetName = "Food",
                budgetAllocation = 10.0,
                budgetGoal = 100.0,
                budgetUsed = 5.0,
                budgetTypeId = 1,
                budgetTypeName = "Monthly",
                budgetTotalPrevAllocation = 0.0
            )

            val budgetMonthly6 = BudgetListAdapterData(
                budgetId = 2,
                budgetName = "Fuel",
                budgetAllocation = 30.0,
                budgetGoal = 100.0,
                budgetUsed = 30.0,
                budgetTypeId = 1,
                budgetTypeName = "Monthly",
                budgetTotalPrevAllocation = 0.0
            )

            val budgetMonthly7 = BudgetListAdapterData(
                budgetId = 3,
                budgetName = "Drink",
                budgetAllocation = 50.0,
                budgetGoal = 20.0,
                budgetUsed = 50.0,
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

            // result3
            val budgetMonthly9 = BudgetListAdapterData(
                budgetId = 1,
                budgetName = "Food",
                budgetAllocation = 20.0,
                budgetGoal = 100.0,
                budgetUsed = 15.0,
                budgetTypeId = 1,
                budgetTypeName = "Monthly",
                budgetTotalPrevAllocation = 0.0
            )

            val budgetMonthly10 = BudgetListAdapterData(
                budgetId = 2,
                budgetName = "Fuel",
                budgetAllocation = 40.0,
                budgetGoal = 100.0,
                budgetUsed = 40.0,
                budgetTypeId = 1,
                budgetTypeName = "Monthly",
                budgetTotalPrevAllocation = 0.0
            )

            val budgetMonthly11 = BudgetListAdapterData(
                budgetId = 3,
                budgetName = "Drink",
                budgetAllocation = 0.0,
                budgetGoal = 20.0,
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

            val timeFrom1 = OffsetDateTime.of(2021, 4, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0))
            val timeTo1 = timeFrom1.plusMonths(1).minusNanos(1)
            val result1 = budgetDao.getBudgetMonthlyListAdapter(4, 2021, timeFrom1, timeTo1).getOrAwaitValue()

            val timeFrom2 = OffsetDateTime.of(2021, 5, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0))
            val timeTo2 = timeFrom2.plusMonths(1).minusNanos(1)
            val result2 = budgetDao.getBudgetMonthlyListAdapter(5, 2021, timeFrom2, timeTo2).getOrAwaitValue()

            val timeFrom3 = OffsetDateTime.of(2021, 6, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0))
            val timeTo3 = timeFrom3.plusMonths(1).minusNanos(1)
            val result3 = budgetDao.getBudgetMonthlyListAdapter(6, 2021, timeFrom3, timeTo3).getOrAwaitValue()

            assertThat(result1.size).isEqualTo(4)
            assertThat(result1).contains(budgetMonthly1)
            assertThat(result1).contains(budgetMonthly2)
            assertThat(result1).contains(budgetMonthly3)
            assertThat(result1).contains(budgetMonthly4)

            assertThat(result2.size).isEqualTo(4)
            assertThat(result2).contains(budgetMonthly5)
            assertThat(result2).contains(budgetMonthly6)
            assertThat(result2).contains(budgetMonthly7)
            assertThat(result2).contains(budgetMonthly8)

            assertThat(result3.size).isEqualTo(4)
            assertThat(result3).contains(budgetMonthly9)
            assertThat(result3).contains(budgetMonthly10)
            assertThat(result3).contains(budgetMonthly11)
            assertThat(result3).contains(budgetMonthly12)
        }
    }

    @Test
    @Throws(IOException::class)
    fun getBudgetYearlyListAdapter() {
        populateTestData()
        runBlocking {
            // expected result
            // result1
            val budgetMonthly1 = BudgetListAdapterData(
                budgetId = 5,
                budgetName = "Vacation",
                budgetAllocation = 300.0,
                budgetGoal = 1000.0,
                budgetUsed = 0.0,
                budgetTypeId = 2,
                budgetTypeName = "Yearly",
                budgetTotalPrevAllocation = 0.0
            )
            val budgetMonthly2 = BudgetListAdapterData(
                budgetId = 6,
                budgetName = "Car Maintenance",
                budgetAllocation = 0.0,
                budgetGoal = 500.0,
                budgetUsed = 0.0,
                budgetTypeId = 2,
                budgetTypeName = "Yearly",
                budgetTotalPrevAllocation = 0.0
            )
            val budgetMonthly3 = BudgetListAdapterData(
                budgetId = 5,
                budgetName = "Vacation",
                budgetAllocation = 50.0,
                budgetGoal = 1000.0,
                budgetUsed = 0.0,
                budgetTypeId = 2,
                budgetTypeName = "Yearly",
                budgetTotalPrevAllocation = 300.0
            )
            val budgetMonthly4 = BudgetListAdapterData(
                budgetId = 5,
                budgetName = "Vacation",
                budgetAllocation = 100.0,
                budgetGoal = 1000.0,
                budgetUsed = 50.0,
                budgetTypeId = 2,
                budgetTypeName = "Yearly",
                budgetTotalPrevAllocation = 350.0
            )

            val timeFrom1 = OffsetDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0))
            val timeTo1 = OffsetDateTime.of(2021, 4, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0)).plusMonths(1).minusNanos(1)
            val result1 = budgetDao.getBudgetYearlyListAdapter(4, 2021, timeFrom1, timeTo1).getOrAwaitValue()

            val timeFrom2 = OffsetDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0))
            val timeTo2 = OffsetDateTime.of(2021, 5, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0)).plusMonths(1).minusNanos(1)
            val result2 = budgetDao.getBudgetYearlyListAdapter(5, 2021, timeFrom2, timeTo2).getOrAwaitValue()

            val timeFrom3 = OffsetDateTime.of(2021, 1, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0))
            val timeTo3 = OffsetDateTime.of(2021, 6, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0)).plusMonths(1).minusNanos(1)
            val result3 = budgetDao.getBudgetYearlyListAdapter(6, 2021, timeFrom3, timeTo3).getOrAwaitValue()

            assertThat(result1.size).isEqualTo(2)
            assertThat(result1).contains(budgetMonthly1)
            assertThat(result1).contains(budgetMonthly2)

            assertThat(result2.size).isEqualTo(2)
            assertThat(result2).contains(budgetMonthly3)
            assertThat(result2).contains(budgetMonthly2)

            assertThat(result3.size).isEqualTo(2)
            assertThat(result3).contains(budgetMonthly4)
            assertThat(result3).contains(budgetMonthly2)
        }
    }
}