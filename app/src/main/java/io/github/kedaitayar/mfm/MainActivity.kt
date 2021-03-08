package io.github.kedaitayar.mfm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels

import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.data.entity.*
import io.github.kedaitayar.mfm.viewmodels.AccountViewModel
import io.github.kedaitayar.mfm.viewmodels.BudgetViewModel
import io.github.kedaitayar.mfm.viewmodels.TransactionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.time.OffsetDateTime
import java.time.ZoneOffset

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val budgetViewModel: BudgetViewModel by viewModels()
    private val transactionViewModel: TransactionViewModel by viewModels()
    private val accountViewModel: AccountViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        CoroutineScope(Dispatchers.IO).launch {
//            var budgetType = BudgetType(budgetTypeId = 1, budgetTypeName = "Monthly")
//            budgetViewModel.insert(budgetType)
//            budgetType = BudgetType(budgetTypeId = 2, budgetTypeName = "Yearly")
//            budgetViewModel.insert(budgetType)
//            budgetType = BudgetType(budgetTypeId = 3, budgetTypeName = "Goal/Debt")
//            budgetViewModel.insert(budgetType)
//
//            var transacitonType = TransactionType(transactionTypeId = 1, transactionTypeName = "Expense")
//            transactionViewModel.insert(transacitonType)
//            transacitonType = TransactionType(transactionTypeId = 2, transactionTypeName = "Income")
//            transactionViewModel.insert(transacitonType)
//            transacitonType = TransactionType(transactionTypeId = 3, transactionTypeName = "Transfer")
//            transactionViewModel.insert(transacitonType)

//            runBlocking {
//                val account1 = Account(accountId = 1, accountName = "Cash")
//                val account2 = Account(accountId = 2, accountName = "Bank")
//                accountViewModel.insert(account1)
//                accountViewModel.insert(account2)
//                val budget1 = Budget(budgetId = 1, budgetGoal = 100.0, budgetName = "Food", budgetType = 1)
//                val budget2 = Budget(budgetId = 2, budgetGoal = 100.0, budgetName = "Fuel", budgetType = 1)
//                val budget3 = Budget(budgetId = 3, budgetGoal = 20.0, budgetName = "Drink", budgetType = 1)
//                val budget4 = Budget(budgetId = 4, budgetGoal = 100.0, budgetName = "Internet", budgetType = 1)
//                val budget5 = Budget(budgetId = 5, budgetGoal = 1000.0, budgetName = "Vacation", budgetType = 2)
//                val budget6 = Budget(budgetId = 6, budgetGoal = 500.0, budgetName = "Car Maintenance", budgetType = 2)
//                budgetViewModel.insert(budget1)
//                budgetViewModel.insert(budget2)
//                budgetViewModel.insert(budget3)
//                budgetViewModel.insert(budget4)
//                budgetViewModel.insert(budget5)
//                budgetViewModel.insert(budget6)
//
//                //budgeting
//                val budgetTransaction1 = BudgetTransaction(
//                    budgetTransactionBudgetId = 1,
//                    budgetTransactionMonth = 5,
//                    budgetTransactionYear = 2021,
//                    budgetTransactionAmount = 10.0
//                )
//                val budgetTransaction2 = BudgetTransaction(
//                    budgetTransactionBudgetId = 1,
//                    budgetTransactionMonth = 6,
//                    budgetTransactionYear = 2021,
//                    budgetTransactionAmount = 20.0
//                )
//                val budgetTransaction3 = BudgetTransaction(
//                    budgetTransactionBudgetId = 2,
//                    budgetTransactionMonth = 5,
//                    budgetTransactionYear = 2021,
//                    budgetTransactionAmount = 30.0
//                )
//                val budgetTransaction4 = BudgetTransaction(
//                    budgetTransactionBudgetId = 2,
//                    budgetTransactionMonth = 6,
//                    budgetTransactionYear = 2021,
//                    budgetTransactionAmount = 40.0
//                )
//                val budgetTransaction5 = BudgetTransaction(
//                    budgetTransactionBudgetId = 3,
//                    budgetTransactionMonth = 5,
//                    budgetTransactionYear = 2021,
//                    budgetTransactionAmount = 50.0
//                )
//                val budgetTransaction6 = BudgetTransaction(
//                    budgetTransactionBudgetId = 5,
//                    budgetTransactionMonth = 4,
//                    budgetTransactionYear = 2021,
//                    budgetTransactionAmount = 300.0
//                )
//                val budgetTransaction7 = BudgetTransaction(
//                    budgetTransactionBudgetId = 5,
//                    budgetTransactionMonth = 5,
//                    budgetTransactionYear = 2021,
//                    budgetTransactionAmount = 50.0
//                )
//                val budgetTransaction8 = BudgetTransaction(
//                    budgetTransactionBudgetId = 5,
//                    budgetTransactionMonth = 6,
//                    budgetTransactionYear = 2021,
//                    budgetTransactionAmount = 100.0
//                )
//                budgetViewModel.insert(budgetTransaction1)
//                budgetViewModel.insert(budgetTransaction2)
//                budgetViewModel.insert(budgetTransaction3)
//                budgetViewModel.insert(budgetTransaction4)
//                budgetViewModel.insert(budgetTransaction5)
//                budgetViewModel.insert(budgetTransaction6)
//                budgetViewModel.insert(budgetTransaction7)
//                budgetViewModel.insert(budgetTransaction8)
//
//                //transaction
//                //income
//                val transaction1 = Transaction(
//                    transactionId = 1,
//                    transactionAmount = 1000.0,
//                    transactionTime = OffsetDateTime.of(2021, 5, 1, 0, 1, 0, 0, ZoneOffset.ofTotalSeconds(0)),
//                    transactionType = 2,
//                    transactionAccountId = 1,
//                    transactionBudgetId = null,
//                    transactionAccountTransferTo = null
//                )
//                //transfer
//                val transaction2 = Transaction(
//                    transactionId = 2,
//                    transactionAmount = 200.0,
//                    transactionTime = OffsetDateTime.of(2021, 5, 2, 0, 1, 0, 0, ZoneOffset.ofTotalSeconds(0)),
//                    transactionType = 3,
//                    transactionAccountId = 1,
//                    transactionBudgetId = null,
//                    transactionAccountTransferTo = 2
//                )
//                //expense
//                val transaction3 = Transaction(
//                    transactionId = 3,
//                    transactionAmount = 5.0,
//                    transactionTime = OffsetDateTime.of(2021, 5, 3, 0, 1, 0, 0, ZoneOffset.ofTotalSeconds(0)),
//                    transactionType = 1,
//                    transactionAccountId = 1,
//                    transactionBudgetId = 1,
//                    transactionAccountTransferTo = null
//                )
//                val transaction4 = Transaction(
//                    transactionId = 4,
//                    transactionAmount = 30.0,
//                    transactionTime = OffsetDateTime.of(2021, 5, 4, 0, 1, 0, 0, ZoneOffset.ofTotalSeconds(0)),
//                    transactionType = 1,
//                    transactionAccountId = 1,
//                    transactionBudgetId = 2,
//                    transactionAccountTransferTo = null
//                )
//                val transaction5 = Transaction(
//                    transactionId = 5,
//                    transactionAmount = 50.0,
//                    transactionTime = OffsetDateTime.of(2021, 5, 4, 0, 1, 0, 0, ZoneOffset.ofTotalSeconds(0)),
//                    transactionType = 1,
//                    transactionAccountId = 1,
//                    transactionBudgetId = 3,
//                    transactionAccountTransferTo = null
//                )
//                val transaction6 = Transaction(
//                    transactionId = 6,
//                    transactionAmount = 15.0,
//                    transactionTime = OffsetDateTime.of(2021, 6, 3, 0, 1, 0, 0, ZoneOffset.ofTotalSeconds(0)),
//                    transactionType = 1,
//                    transactionAccountId = 1,
//                    transactionBudgetId = 1,
//                    transactionAccountTransferTo = null
//                )
//                val transaction7 = Transaction(
//                    transactionId = 7,
//                    transactionAmount = 40.0,
//                    transactionTime = OffsetDateTime.of(2021, 6, 4, 0, 1, 0, 0, ZoneOffset.ofTotalSeconds(0)),
//                    transactionType = 1,
//                    transactionAccountId = 1,
//                    transactionBudgetId = 2,
//                    transactionAccountTransferTo = null
//                )
//                val transaction8 = Transaction(
//                    transactionId = 8,
//                    transactionAmount = 50.0,
//                    transactionTime = OffsetDateTime.of(2021, 6, 5, 0, 1, 0, 0, ZoneOffset.ofTotalSeconds(0)),
//                    transactionType = 1,
//                    transactionAccountId = 1,
//                    transactionBudgetId = 5,
//                    transactionAccountTransferTo = null
//                )
//                transactionViewModel.insert(transaction1)
//                transactionViewModel.insert(transaction2)
//                transactionViewModel.insert(transaction3)
//                transactionViewModel.insert(transaction4)
//                transactionViewModel.insert(transaction5)
//                transactionViewModel.insert(transaction6)
//                transactionViewModel.insert(transaction7)
//                transactionViewModel.insert(transaction8)
//            }

//            val account1 = Account(accountId = 1, accountName = "Cash")
//            val account2 = Account(accountId = 2, accountName = "Bank")
//            accountViewModel.insert(account1)
//            accountViewModel.insert(account2)
//            val budget1 = Budget(budgetId = 1, budgetGoal = 100.0, budgetName = "Food", budgetType = 1)
//            val budget2 = Budget(budgetId = 2, budgetGoal = 100.0, budgetName = "Fuel", budgetType = 1)
//            val budget3 = Budget(budgetId = 3, budgetGoal = 20.0, budgetName = "Drink", budgetType = 1)
//            val budget4 = Budget(budgetId = 4, budgetGoal = 100.0, budgetName = "Internet", budgetType = 1)
//            val budget5 = Budget(budgetId = 5, budgetGoal = 1000.0, budgetName = "Vacation", budgetType = 2)
//            val budget6 = Budget(budgetId = 6, budgetGoal = 500.0, budgetName = "Car Maintenance", budgetType = 2)
//            budgetViewModel.insert(budget1)
//            budgetViewModel.insert(budget2)
//            budgetViewModel.insert(budget3)
//            budgetViewModel.insert(budget4)
//            budgetViewModel.insert(budget5)
//            budgetViewModel.insert(budget6)
        }
    }

}