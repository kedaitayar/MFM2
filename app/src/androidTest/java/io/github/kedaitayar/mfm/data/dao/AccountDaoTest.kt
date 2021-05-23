package io.github.kedaitayar.mfm.data.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.github.kedaitayar.mfm.data.database.MFMDatabase
import io.github.kedaitayar.mfm.data.entity.Account
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import com.google.common.truth.Truth.assertThat
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.kedaitayar.mfm.data.entity.TransactionType
import io.github.kedaitayar.mfm.data.podata.AccountListAdapterData
import io.github.kedaitayar.mfm.util.getOrAwaitValue
import io.github.kedaitayar.mfm.data.entity.Budget
import io.github.kedaitayar.mfm.data.entity.BudgetType
import io.github.kedaitayar.mfm.data.entity.Transaction
import kotlinx.coroutines.runBlocking
import org.junit.Rule
import java.time.OffsetDateTime
import javax.inject.Inject
import javax.inject.Named
import kotlin.jvm.Throws

//@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class AccountDaoTest {
    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_database")
    lateinit var mfmDb: MFMDatabase
    private lateinit var accountDao: AccountDao
    private lateinit var basicDao: BasicDao

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
        hiltAndroidRule.inject()

        accountDao = mfmDb.accountDao()
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
    fun getAccountById() {
        runBlocking {
            val account = Account(accountId = 1, accountName = "Cash")
            basicDao.insert(account)
            val getAccount = accountDao.getAccountById(1)
            assertThat(getAccount).isEqualTo(account)
        }
    }

    @Test
    @Throws(IOException::class)
    fun getAccountTransactionChartData() {
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

        val accountResult1 = AccountListAdapterData(
            accountId = account1.accountId,
            accountName = account1.accountName,
            accountIncome = transaction1.transactionAmount,
            accountExpense = transaction2.transactionAmount,
            accountTransferIn = transaction4.transactionAmount,
            accountTransferOut = transaction3.transactionAmount
        )
        val accountResult2 = AccountListAdapterData(
            accountId = account2.accountId,
            accountName = account2.accountName,
            accountIncome = 0.0,
            accountExpense = 0.0,
            accountTransferIn = transaction3.transactionAmount,
            accountTransferOut = transaction4.transactionAmount
        )

        runBlocking {
            basicDao.insert(account1)
            basicDao.insert(account2)
            basicDao.insert(budget)
            basicDao.insert(transaction1)
            basicDao.insert(transaction2)
            basicDao.insert(transaction3)
            basicDao.insert(transaction4)

            val accountList = accountDao.getAccountListData().getOrAwaitValue()

            assertThat(accountList.size).isEqualTo(2)
            assertThat(accountList).contains(accountResult1)
            assertThat(accountList).contains(accountResult2)
        }
    }
}