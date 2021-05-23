package io.github.kedaitayar.mfm.data.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.kedaitayar.mfm.data.database.MFMDatabase
import io.github.kedaitayar.mfm.data.entity.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException
import java.time.OffsetDateTime
import io.github.kedaitayar.mfm.data.podata.TransactionListAdapterData
import javax.inject.Inject
import javax.inject.Named
import kotlin.jvm.Throws

@HiltAndroidTest
class TransactionDaoTest {
    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Inject
    @Named("test_database")
    lateinit var mfmDb: MFMDatabase
    private lateinit var transactionDao: TransactionDao
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
        transactionDao = mfmDb.transactionDao()
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
    fun getTransactionListData() {
        val account1 = Account(accountId = 1, accountName = "Cash")
        val account2 = Account(accountId = 2, accountName = "Bank")
        val budget = Budget(budgetId = 1, budgetGoal = 100.0, budgetName = "Food", budgetType = 1)
        val time = OffsetDateTime.now()
        // income
        val transaction1 = Transaction(
            transactionId = 1,
            transactionAccountId = 1,
            transactionType = 2,
            transactionAmount = 500.0,
            transactionTime = time
        )
        // expense
        val transaction2 = Transaction(
            transactionId = 2,
            transactionAccountId = 1,
            transactionBudgetId = 1,
            transactionType = 1,
            transactionAmount = 100.0,
            transactionTime = time
        )
        // transfer out
        val transaction3 = Transaction(
            transactionId = 3,
            transactionAccountId = 1,
            transactionType = 3,
            transactionAmount = 200.0,
            transactionTime = time,
            transactionAccountTransferTo = 2
        )
        // transfer in
        val transaction4 = Transaction(
            transactionId = 4,
            transactionAccountId = 2,
            transactionType = 3,
            transactionAmount = 50.0,
            transactionTime = time,
            transactionAccountTransferTo = 1
        )

        val transactionListData1 = TransactionListAdapterData(
            transactionId = 1,
            transactionAmount = 500.0,
            transactionTime = time,
            transactionTypeId = 2,
            transactionTypeName = "Income",
            transactionAccountId = 1,
            transactionBudgetId = null,
            transactionAccountTransferTo = null,
            transactionAccountName = "Cash",
            transactionBudgetName = null,
            transactionAccountTransferToName = null
        )

        val transactionListData2 = TransactionListAdapterData(
            transactionId = 2,
            transactionAmount = 100.0,
            transactionTime = time,
            transactionTypeId = 1,
            transactionTypeName = "Expense",
            transactionAccountId = 1,
            transactionBudgetId = 1,
            transactionAccountTransferTo = null,
            transactionAccountName = "Cash",
            transactionBudgetName = "Food",
            transactionAccountTransferToName = null
        )

        val transactionListData3 = TransactionListAdapterData(
            transactionId = 3,
            transactionAmount = 200.0,
            transactionTime = time,
            transactionTypeId = 3,
            transactionTypeName = "Transfer",
            transactionAccountId = 1,
            transactionBudgetId = null,
            transactionAccountTransferTo = 2,
            transactionAccountName = "Cash",
            transactionBudgetName = null,
            transactionAccountTransferToName = "Bank"
        )

        val transactionListData4 = TransactionListAdapterData(
            transactionId = 4,
            transactionAmount = 50.0,
            transactionTime = time,
            transactionTypeId = 3,
            transactionTypeName = "Transfer",
            transactionAccountId = 2,
            transactionBudgetId = null,
            transactionAccountTransferTo = 1,
            transactionAccountName = "Bank",
            transactionBudgetName = null,
            transactionAccountTransferToName = "Cash"
        )

        runBlocking {
            basicDao.insert(account1)
            basicDao.insert(account2)
            basicDao.insert(budget)
            basicDao.insert(transaction1)
            basicDao.insert(transaction2)
            basicDao.insert(transaction3)
            basicDao.insert(transaction4)



//            val list = transactionDao.getTransactionListData().getOrAwaitValue()
//            assertThat(list).contains(transactionListData1)
//            assertThat(list).contains(transactionListData2)
//            assertThat(list).contains(transactionListData3)
//            assertThat(list).contains(transactionListData4)
        }
    }
}