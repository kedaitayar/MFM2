package io.github.kedaitayar.mfm

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import io.github.kedaitayar.mfm.data.dao.BasicDao
import io.github.kedaitayar.mfm.data.database.MFMDatabase
import io.github.kedaitayar.mfm.data.entity.*
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
class BasicDaoTest {
    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

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
            .build()
        basicDao = mfmDb.basicDao()

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
    fun insertAndReadAccount() {
        runBlocking {
            val account1 = Account(accountId = 1, accountName = "Cash")
            basicDao.insert(account1)
            val account2 = Account(accountId = 2, accountName = "Bank")
            basicDao.insert(account2)

            val accountList = basicDao.getAllAccount().getOrAwaitValue()
            assertThat(accountList.size).isEqualTo(2)
            assertThat(accountList[0]).isEqualTo(account1)
            assertThat(accountList[1]).isEqualTo(account2)
        }
    }

    @Test
    @Throws(IOException::class)
    fun updateAccount() {
        runBlocking {
            val account = Account(accountId = 1, accountName = "Cash")
            basicDao.insert(account)
            account.accountName = "Cash2"
            basicDao.update(account)
            val accountList = basicDao.getAllAccount().getOrAwaitValue()
            assertThat(accountList[0]).isEqualTo(account)
        }
    }

    @Test
    @Throws(IOException::class)
    fun deleteAccount() {
        runBlocking {
            val account = Account(accountId = 1, accountName = "Cash")
            basicDao.insert(account)
            basicDao.delete(account)
            val accountList = basicDao.getAllAccount().getOrAwaitValue()
            assertThat(accountList).doesNotContain(account)
        }
    }

    @Test
    @Throws(IOException::class)
    fun insertAndReadBudget() {
        runBlocking {
            val budget1 =
                Budget(budgetId = 1, budgetGoal = 100.0, budgetName = "Food", budgetType = 1)
            basicDao.insert(budget1)
            val budget2 =
                Budget(budgetId = 2, budgetGoal = 60.0, budgetName = "Internet", budgetType = 1)
            basicDao.insert(budget2)

            val budgetList = basicDao.getAllBudget().getOrAwaitValue()
            assertThat(budgetList.size).isEqualTo(2)
            assertThat(budgetList[0]).isEqualTo(budget1)
            assertThat(budgetList[1]).isEqualTo(budget2)
        }
    }

    @Test
    @Throws(IOException::class)
    fun updateBudget() {
        runBlocking {
            val budget =
                Budget(budgetId = 1, budgetGoal = 100.0, budgetName = "Food", budgetType = 1)
            basicDao.insert(budget)
            budget.budgetName = "Food2"
            budget.budgetGoal = 120.0
            basicDao.update(budget)
            val budgetList = basicDao.getAllBudget().getOrAwaitValue()
            assertThat(budgetList[0]).isEqualTo(budget)
        }
    }

    @Test
    @Throws(IOException::class)
    fun deleteBudget() {
        runBlocking {
            val budget =
                Budget(budgetId = 1, budgetGoal = 100.0, budgetName = "Food", budgetType = 1)
            basicDao.insert(budget)
            basicDao.delete(budget)
            val budgetList = basicDao.getAllBudget().getOrAwaitValue()
            assertThat(budgetList).doesNotContain(budget)
        }
    }

    @Test
    @Throws(IOException::class)
    fun insertAndReadBudgetDeadline() {
        runBlocking {
            val budget =
                Budget(budgetId = 1, budgetGoal = 100.0, budgetName = "Food", budgetType = 3)
            basicDao.insert(budget)
            val budgetDeadline = BudgetDeadline(
                budgetId = 1,
                budgetDeadline = OffsetDateTime.of(
                    2020,
                    1,
                    1,
                    0,
                    0,
                    0,
                    0,
                    ZoneOffset.ofTotalSeconds(0)
                )
            )
            basicDao.insert(budgetDeadline)

            val budgetDeadlineList = basicDao.getAllBudgetDeadline().getOrAwaitValue()
            assertThat(budgetDeadlineList.size).isEqualTo(1)
            assertThat(budgetDeadlineList[0]).isEqualTo(budgetDeadline)
        }
    }

    @Test
    @Throws(IOException::class)
    fun updateBudgetDeadline() {
        runBlocking {
            val budget =
                Budget(budgetId = 1, budgetGoal = 100.0, budgetName = "Food", budgetType = 3)
            basicDao.insert(budget)
            var budgetDeadline = BudgetDeadline(
                budgetId = 1,
                budgetDeadline = OffsetDateTime.of(
                    2020,
                    1,
                    1,
                    0,
                    0,
                    0,
                    0,
                    ZoneOffset.ofTotalSeconds(0)
                )
            )
            basicDao.insert(budgetDeadline)

            budgetDeadline.budgetDeadline =
                OffsetDateTime.of(2020, 2, 1, 0, 0, 0, 0, ZoneOffset.ofTotalSeconds(0))
            basicDao.update(budgetDeadline)
            val budgetDeadlineList = basicDao.getAllBudgetDeadline().getOrAwaitValue()
            assertThat(budgetDeadlineList[0]).isEqualTo(budgetDeadline)
        }
    }

    @Test
    @Throws(IOException::class)
    fun deleteBudgetDeadline() {
        runBlocking {
            val budget =
                Budget(budgetId = 1, budgetGoal = 100.0, budgetName = "Food", budgetType = 3)
            basicDao.insert(budget)
            var budgetDeadline = BudgetDeadline(
                budgetId = 1,
                budgetDeadline = OffsetDateTime.of(
                    2020,
                    1,
                    1,
                    0,
                    0,
                    0,
                    0,
                    ZoneOffset.ofTotalSeconds(0)
                )
            )
            basicDao.insert(budgetDeadline)
            basicDao.delete(budgetDeadline)
            val budgetDeadlineList = basicDao.getAllBudgetDeadline().getOrAwaitValue()
            assertThat(budgetDeadlineList).doesNotContain(budgetDeadline)
        }
    }

    @Test
    @Throws(IOException::class)
    fun insertAndReadBudgetTransaction() {
        runBlocking {
            val budget =
                Budget(budgetId = 1, budgetGoal = 100.0, budgetName = "Food", budgetType = 1)
            basicDao.insert(budget)
            val budget2 =
                Budget(budgetId = 2, budgetGoal = 50.0, budgetName = "Fuel", budgetType = 1)
            basicDao.insert(budget2)
            val budgetTransaction = BudgetTransaction(
                budgetTransactionAmount = 50.0,
                budgetTransactionMonth = 1,
                budgetTransactionYear = 2020,
                budgetTransactionBudgetId = 1
            )
            basicDao.insert(budgetTransaction)
            val budgetTransaction1 = BudgetTransaction(
                budgetTransactionAmount = 100.0,
                budgetTransactionMonth = 2,
                budgetTransactionYear = 2020,
                budgetTransactionBudgetId = 2
            )
            basicDao.insert(budgetTransaction1)

            val list = basicDao.getAllBudgetTransaction().getOrAwaitValue()
            assertThat(list.size).isEqualTo(2)
            assertThat(list).contains(budgetTransaction)
            assertThat(list).contains(budgetTransaction1)
        }
    }

    @Test
    @Throws(IOException::class)
    fun updateBudgetTransaction() {
        runBlocking {
            val budget =
                Budget(budgetId = 1, budgetGoal = 100.0, budgetName = "Food", budgetType = 3)
            basicDao.insert(budget)
            val budgetTransaction = BudgetTransaction(
                budgetTransactionAmount = 50.0,
                budgetTransactionMonth = 1,
                budgetTransactionYear = 1,
                budgetTransactionBudgetId = 1
            )
            basicDao.insert(budgetTransaction)
            budgetTransaction.budgetTransactionAmount = 60.0
            basicDao.update(budgetTransaction)
            val list = basicDao.getAllBudgetTransaction().getOrAwaitValue()
            assertThat(list[0]).isEqualTo(budgetTransaction)
        }
    }

    @Test
    @Throws(IOException::class)
    fun deleteBudgetTransaction() {
        runBlocking {
            val budget =
                Budget(budgetId = 1, budgetGoal = 100.0, budgetName = "Food", budgetType = 3)
            basicDao.insert(budget)
            val budgetTransaction = BudgetTransaction(
                budgetTransactionAmount = 50.0,
                budgetTransactionMonth = 1,
                budgetTransactionYear = 1,
                budgetTransactionBudgetId = 1
            )
            basicDao.insert(budgetTransaction)
            basicDao.delete(budgetTransaction)
            val list = basicDao.getAllBudgetTransaction().getOrAwaitValue()
            assertThat(list).doesNotContain(budgetTransaction)
        }
    }

    @Test
    @Throws(IOException::class)
    fun readBudgetType() {
        runBlocking {

            val list = basicDao.getAllBudgetType().getOrAwaitValue()
            assertThat(list.size).isEqualTo(3)
            assertThat(list[0]).isEqualTo(budgetTypeList[0])
            assertThat(list[1]).isEqualTo(budgetTypeList[1])
            assertThat(list[2]).isEqualTo(budgetTypeList[2])
        }
    }

    @Test
    @Throws(IOException::class)
    fun updateBudgetType() {
        runBlocking {
            val item = BudgetType(budgetTypeId = 1, budgetTypeName = "Monthly2")
            basicDao.update(item)
            val list = basicDao.getAllBudgetType().getOrAwaitValue()
            assertThat(list[0]).isEqualTo(item)
        }
    }

    @Test
    @Throws(IOException::class)
    fun deleteBudgetType() {
        runBlocking {
            val item = BudgetType(budgetTypeId = 1, budgetTypeName = "Monthly")
            basicDao.delete(item)
            val list = basicDao.getAllBudgetType().getOrAwaitValue()
            assertThat(list).doesNotContain(item)
        }
    }

    @Test
    @Throws(IOException::class)
    fun insertAndReadTransaction() {
        runBlocking {
            val account1 = Account(accountId = 1, accountName = "Cash")
            val account2 = Account(accountId = 2, accountName = "Bank")
            val budget = Budget(budgetId = 1, budgetGoal = 100.0, budgetName = "Food", budgetType = 1)

            // income
            val transaction1 = Transaction(
                transactionId = 1,
                transactionAccountId = 1,
                transactionType = 2,
                transactionAmount = 500.0,
                transactionTime = OffsetDateTime.now()
            )
            // expense
            val transaction2 = Transaction(
                transactionId = 2,
                transactionAccountId = 1,
                transactionBudgetId = 1,
                transactionType = 1,
                transactionAmount = 100.0,
                transactionTime = OffsetDateTime.now()
            )
            // transfer out
            val transaction3 = Transaction(
                transactionId = 3,
                transactionAccountId = 1,
                transactionType = 3,
                transactionAmount = 200.0,
                transactionTime = OffsetDateTime.now(),
                transactionAccountTransferTo = 2
            )
            // transfer in
            val transaction4 = Transaction(
                transactionId = 4,
                transactionAccountId = 2,
                transactionType = 3,
                transactionAmount = 50.0,
                transactionTime = OffsetDateTime.now(),
                transactionAccountTransferTo = 1
            )
            basicDao.insert(account1)
            basicDao.insert(account2)
            basicDao.insert(budget)
            basicDao.insert(transaction1)
            basicDao.insert(transaction2)
            basicDao.insert(transaction3)
            basicDao.insert(transaction4)

            val list = basicDao.getAllTransaction().getOrAwaitValue()
            assertThat(list.size).isEqualTo(4)
            assertThat(list).contains(transaction1)
            assertThat(list).contains(transaction2)
            assertThat(list).contains(transaction3)
            assertThat(list).contains(transaction4)
        }
    }

    @Test
    @Throws(IOException::class)
    fun updateTransaction() {
        runBlocking {
            val account1 = Account(accountId = 1, accountName = "Cash")
            val account2 = Account(accountId = 2, accountName = "Bank")
            val budget = Budget(budgetId = 1, budgetGoal = 100.0, budgetName = "Food", budgetType = 1)
            val item = Transaction(
                transactionId = 1,
                transactionAccountId = 1,
                transactionType = 2,
                transactionAmount = 500.0,
                transactionTime = OffsetDateTime.now()
            )
            basicDao.insert(account1)
            basicDao.insert(account2)
            basicDao.insert(budget)
            basicDao.insert(item)
            item.transactionAmount = 800.0
            basicDao.update(item)
            val list = basicDao.getAllTransaction().getOrAwaitValue()
            assertThat(list[0]).isEqualTo(item)
        }
    }

    @Test
    @Throws(IOException::class)
    fun deleteTransaction() {
        runBlocking {
            val account1 = Account(accountId = 1, accountName = "Cash")
            val account2 = Account(accountId = 2, accountName = "Bank")
            val budget = Budget(budgetId = 1, budgetGoal = 100.0, budgetName = "Food", budgetType = 1)
            val item = Transaction(
                transactionId = 1,
                transactionAccountId = 1,
                transactionType = 2,
                transactionAmount = 500.0,
                transactionTime = OffsetDateTime.now()
            )
            basicDao.insert(account1)
            basicDao.insert(account2)
            basicDao.insert(budget)
            basicDao.insert(item)
            basicDao.delete(item)
            val list = basicDao.getAllTransaction().getOrAwaitValue()
            assertThat(list).doesNotContain(item)
        }
    }

    @Test
    @Throws(IOException::class)
    fun readTransactionType() {
        runBlocking {
            val list = basicDao.getAllTransactionType().getOrAwaitValue()
            assertThat(list.size).isEqualTo(3)
            assertThat(list[0]).isEqualTo(transactionTypeList[0])
            assertThat(list[1]).isEqualTo(transactionTypeList[1])
            assertThat(list[2]).isEqualTo(transactionTypeList[2])
        }
    }

    @Test
    @Throws(IOException::class)
    fun updateTransactionType() {
        runBlocking {
            val item = TransactionType(transactionTypeId = 1, transactionTypeName = "Spending")
            basicDao.update(item)
            val list = basicDao.getAllTransactionType().getOrAwaitValue()
            assertThat(list[0]).isEqualTo(item)
        }
    }

    @Test
    @Throws(IOException::class)
    fun deleteTransactionType() {
        runBlocking {
            val item = TransactionType(transactionTypeId = 1, transactionTypeName = "Expense")
            basicDao.delete(item)
            val list = basicDao.getAllTransactionType().getOrAwaitValue()
            assertThat(list).doesNotContain(item)
        }
    }

}