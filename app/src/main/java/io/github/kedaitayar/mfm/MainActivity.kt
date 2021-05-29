package io.github.kedaitayar.mfm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.data.entity.*
import kotlinx.coroutines.runBlocking
import java.time.OffsetDateTime
import java.time.ZoneOffset

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val mainActivityViewModel: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        initialData()
//        testData()
//        runBlocking {
//            var budgetType = BudgetType(budgetTypeId = 1, budgetTypeName = "Monthly")
//            mainActivityViewModel.insert(budgetType)
//            budgetType = BudgetType(budgetTypeId = 2, budgetTypeName = "Yearly")
//            mainActivityViewModel.insert(budgetType)
//
//            var transacitonType = TransactionType(transactionTypeId = 1, transactionTypeName = "Expense")
//            mainActivityViewModel.insert(transacitonType)
//            transacitonType = TransactionType(transactionTypeId = 2, transactionTypeName = "Income")
//            mainActivityViewModel.insert(transacitonType)
//            transacitonType = TransactionType(transactionTypeId = 3, transactionTypeName = "Transfer")
//            mainActivityViewModel.insert(transacitonType)
//
//            val account1 = Account(accountId = 1, accountName = "Cash")
//            mainActivityViewModel.insert(account1)
//        }
    }

    private fun initialData() {
        runBlocking {

            var budgetType = BudgetType(budgetTypeId = 1, budgetTypeName = "Monthly")
            mainActivityViewModel.insert(budgetType)
            budgetType = BudgetType(budgetTypeId = 2, budgetTypeName = "Yearly")
            mainActivityViewModel.insert(budgetType)

            var transacitonType = TransactionType(transactionTypeId = 1, transactionTypeName = "Expense")
            mainActivityViewModel.insert(transacitonType)
            transacitonType = TransactionType(transactionTypeId = 2, transactionTypeName = "Income")
            mainActivityViewModel.insert(transacitonType)
            transacitonType = TransactionType(transactionTypeId = 3, transactionTypeName = "Transfer")
            mainActivityViewModel.insert(transacitonType)

            val account1 = Account(accountId = 1, accountName = "Cash")
            mainActivityViewModel.insert(account1)


        }
    }

    private fun testData() {
        runBlocking {
            val now = OffsetDateTime.now()

//            var budgetType = BudgetType(budgetTypeId = 1, budgetTypeName = "Monthly")
//            mainActivityViewModel.insert(budgetType)
//            budgetType = BudgetType(budgetTypeId = 2, budgetTypeName = "Yearly")
//            mainActivityViewModel.insert(budgetType)
//            budgetType = BudgetType(budgetTypeId = 3, budgetTypeName = "Goal/Debt")
//            mainActivityViewModel.insert(budgetType)
//
//            var transacitonType = TransactionType(transactionTypeId = 1, transactionTypeName = "Expense")
//            mainActivityViewModel.insert(transacitonType)
//            transacitonType = TransactionType(transactionTypeId = 2, transactionTypeName = "Income")
//            mainActivityViewModel.insert(transacitonType)
//            transacitonType = TransactionType(transactionTypeId = 3, transactionTypeName = "Transfer")
//            mainActivityViewModel.insert(transacitonType)

            val account1 = Account(accountId = 1, accountName = "Cash")
            val account2 = Account(accountId = 2, accountName = "Bank")
//            mainActivityViewModel.insert(account1)
            mainActivityViewModel.insert(account2)

            mainActivityViewModel.update(Budget(budgetId = 1, budgetGoal = 450.0, budgetName = "Food", budgetType = 1))
            mainActivityViewModel.update(
                Budget(
                    budgetId = 2,
                    budgetGoal = 240.0,
                    budgetName = "Groceries",
                    budgetType = 1
                )
            )
            mainActivityViewModel.update(Budget(budgetId = 3, budgetGoal = 800.0, budgetName = "Rent", budgetType = 1))
            mainActivityViewModel.update(
                Budget(
                    budgetId = 4,
                    budgetGoal = 60.0,
                    budgetName = "Electric",
                    budgetType = 1
                )
            )
            mainActivityViewModel.update(Budget(budgetId = 5, budgetGoal = 40.0, budgetName = "Water", budgetType = 1))
            mainActivityViewModel.update(
                Budget(
                    budgetId = 6,
                    budgetGoal = 240.0,
                    budgetName = "Transportation",
                    budgetType = 1
                )
            )
            mainActivityViewModel.update(Budget(budgetId = 7, budgetGoal = 100.0, budgetName = "Bills", budgetType = 1))
            mainActivityViewModel.update(
                Budget(
                    budgetId = 12,
                    budgetGoal = 50.0,
                    budgetName = "Fun Fund",
                    budgetType = 1
                )
            )
            mainActivityViewModel.update(
                Budget(
                    budgetId = 8,
                    budgetGoal = 1000.0,
                    budgetName = "Car Maintenance",
                    budgetType = 2
                )
            )
            mainActivityViewModel.update(
                Budget(
                    budgetId = 9,
                    budgetGoal = 1000.0,
                    budgetName = "Home Maintenance",
                    budgetType = 2
                )
            )
            mainActivityViewModel.update(
                Budget(
                    budgetId = 10,
                    budgetGoal = 2000.0,
                    budgetName = "Medical",
                    budgetType = 2
                )
            )
            mainActivityViewModel.update(
                Budget(
                    budgetId = 11,
                    budgetGoal = 500.0,
                    budgetName = "Clothing",
                    budgetType = 2
                )
            )
            mainActivityViewModel.update(
                Budget(
                    budgetId = 13,
                    budgetGoal = 2000.0,
                    budgetName = "Vacation",
                    budgetType = 2
                )
            )
            mainActivityViewModel.update(
                Budget(
                    budgetId = 14,
                    budgetGoal = 500.0,
                    budgetName = "Stuff I Forgot to Budget for",
                    budgetType = 2
                )
            )
            mainActivityViewModel.update(
                Budget(
                    budgetId = 15,
                    budgetGoal = 1000.0,
                    budgetName = "Emergency Funds",
                    budgetType = 2
                )
            )

            //budgeting
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(3).monthValue,
                    budgetTransactionYear = now.minusMonths(3).year,
                    budgetTransactionAmount = 285.0,
                    budgetTransactionBudgetId = 1
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(3).monthValue,
                    budgetTransactionYear = now.minusMonths(3).year,
                    budgetTransactionAmount = 180.0,
                    budgetTransactionBudgetId = 2
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(3).monthValue,
                    budgetTransactionYear = now.minusMonths(3).year,
                    budgetTransactionAmount = 800.0,
                    budgetTransactionBudgetId = 3
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(3).monthValue,
                    budgetTransactionYear = now.minusMonths(3).year,
                    budgetTransactionAmount = 60.0,
                    budgetTransactionBudgetId = 4
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(3).monthValue,
                    budgetTransactionYear = now.minusMonths(3).year,
                    budgetTransactionAmount = 40.0,
                    budgetTransactionBudgetId = 5
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(3).monthValue,
                    budgetTransactionYear = now.minusMonths(3).year,
                    budgetTransactionAmount = 180.0,
                    budgetTransactionBudgetId = 6
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(3).monthValue,
                    budgetTransactionYear = now.minusMonths(3).year,
                    budgetTransactionAmount = 100.0,
                    budgetTransactionBudgetId = 7
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(3).monthValue,
                    budgetTransactionYear = now.minusMonths(3).year,
                    budgetTransactionAmount = 60.0,
                    budgetTransactionBudgetId = 12
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(3).monthValue,
                    budgetTransactionYear = now.minusMonths(3).year,
                    budgetTransactionAmount = 0.0,
                    budgetTransactionBudgetId = 8
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(3).monthValue,
                    budgetTransactionYear = now.minusMonths(3).year,
                    budgetTransactionAmount = 0.0,
                    budgetTransactionBudgetId = 9
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(3).monthValue,
                    budgetTransactionYear = now.minusMonths(3).year,
                    budgetTransactionAmount = 0.0,
                    budgetTransactionBudgetId = 10
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(3).monthValue,
                    budgetTransactionYear = now.minusMonths(3).year,
                    budgetTransactionAmount = 0.0,
                    budgetTransactionBudgetId = 11
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(3).monthValue,
                    budgetTransactionYear = now.minusMonths(3).year,
                    budgetTransactionAmount = 0.0,
                    budgetTransactionBudgetId = 13
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(3).monthValue,
                    budgetTransactionYear = now.minusMonths(3).year,
                    budgetTransactionAmount = 0.0,
                    budgetTransactionBudgetId = 14
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(3).monthValue,
                    budgetTransactionYear = now.minusMonths(3).year,
                    budgetTransactionAmount = 0.0,
                    budgetTransactionBudgetId = 15
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(2).monthValue,
                    budgetTransactionYear = now.minusMonths(2).year,
                    budgetTransactionAmount = 240.0,
                    budgetTransactionBudgetId = 2
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(2).monthValue,
                    budgetTransactionYear = now.minusMonths(2).year,
                    budgetTransactionAmount = 450.0,
                    budgetTransactionBudgetId = 1
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(2).monthValue,
                    budgetTransactionYear = now.minusMonths(2).year,
                    budgetTransactionAmount = 800.0,
                    budgetTransactionBudgetId = 3
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(2).monthValue,
                    budgetTransactionYear = now.minusMonths(2).year,
                    budgetTransactionAmount = 60.0,
                    budgetTransactionBudgetId = 4
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(2).monthValue,
                    budgetTransactionYear = now.minusMonths(2).year,
                    budgetTransactionAmount = 100.0,
                    budgetTransactionBudgetId = 7
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(2).monthValue,
                    budgetTransactionYear = now.minusMonths(2).year,
                    budgetTransactionAmount = 80.0,
                    budgetTransactionBudgetId = 12
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(2).monthValue,
                    budgetTransactionYear = now.minusMonths(2).year,
                    budgetTransactionAmount = 100.0,
                    budgetTransactionBudgetId = 8
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(2).monthValue,
                    budgetTransactionYear = now.minusMonths(2).year,
                    budgetTransactionAmount = 100.0,
                    budgetTransactionBudgetId = 9
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(2).monthValue,
                    budgetTransactionYear = now.minusMonths(2).year,
                    budgetTransactionAmount = 100.0,
                    budgetTransactionBudgetId = 10
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(2).monthValue,
                    budgetTransactionYear = now.minusMonths(2).year,
                    budgetTransactionAmount = 50.0,
                    budgetTransactionBudgetId = 11
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(2).monthValue,
                    budgetTransactionYear = now.minusMonths(2).year,
                    budgetTransactionAmount = 100.0,
                    budgetTransactionBudgetId = 13
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(2).monthValue,
                    budgetTransactionYear = now.minusMonths(2).year,
                    budgetTransactionAmount = 50.0,
                    budgetTransactionBudgetId = 14
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(2).monthValue,
                    budgetTransactionYear = now.minusMonths(2).year,
                    budgetTransactionAmount = 100.0,
                    budgetTransactionBudgetId = 15
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(2).monthValue,
                    budgetTransactionYear = now.minusMonths(2).year,
                    budgetTransactionAmount = 40.0,
                    budgetTransactionBudgetId = 5
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(2).monthValue,
                    budgetTransactionYear = now.minusMonths(2).year,
                    budgetTransactionAmount = 300.0,
                    budgetTransactionBudgetId = 6
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(1).monthValue,
                    budgetTransactionYear = now.minusMonths(1).year,
                    budgetTransactionAmount = 375.0,
                    budgetTransactionBudgetId = 1
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(1).monthValue,
                    budgetTransactionYear = now.minusMonths(1).year,
                    budgetTransactionAmount = 180.0,
                    budgetTransactionBudgetId = 2
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(1).monthValue,
                    budgetTransactionYear = now.minusMonths(1).year,
                    budgetTransactionAmount = 800.0,
                    budgetTransactionBudgetId = 3
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(1).monthValue,
                    budgetTransactionYear = now.minusMonths(1).year,
                    budgetTransactionAmount = 60.0,
                    budgetTransactionBudgetId = 4
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(1).monthValue,
                    budgetTransactionYear = now.minusMonths(1).year,
                    budgetTransactionAmount = 40.0,
                    budgetTransactionBudgetId = 5
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(1).monthValue,
                    budgetTransactionYear = now.minusMonths(1).year,
                    budgetTransactionAmount = 100.0,
                    budgetTransactionBudgetId = 8
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(1).monthValue,
                    budgetTransactionYear = now.minusMonths(1).year,
                    budgetTransactionAmount = 100.0,
                    budgetTransactionBudgetId = 10
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(1).monthValue,
                    budgetTransactionYear = now.minusMonths(1).year,
                    budgetTransactionAmount = 50.0,
                    budgetTransactionBudgetId = 11
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(1).monthValue,
                    budgetTransactionYear = now.minusMonths(1).year,
                    budgetTransactionAmount = 100.0,
                    budgetTransactionBudgetId = 13
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(1).monthValue,
                    budgetTransactionYear = now.minusMonths(1).year,
                    budgetTransactionAmount = 240.0,
                    budgetTransactionBudgetId = 6
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(1).monthValue,
                    budgetTransactionYear = now.minusMonths(1).year,
                    budgetTransactionAmount = 50.0,
                    budgetTransactionBudgetId = 12
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(1).monthValue,
                    budgetTransactionYear = now.minusMonths(1).year,
                    budgetTransactionAmount = 100.0,
                    budgetTransactionBudgetId = 7
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(1).monthValue,
                    budgetTransactionYear = now.minusMonths(1).year,
                    budgetTransactionAmount = 100.0,
                    budgetTransactionBudgetId = 15
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(1).monthValue,
                    budgetTransactionYear = now.minusMonths(1).year,
                    budgetTransactionAmount = 100.0,
                    budgetTransactionBudgetId = 9
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(1).monthValue,
                    budgetTransactionYear = now.minusMonths(1).year,
                    budgetTransactionAmount = 50.0,
                    budgetTransactionBudgetId = 14
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(0).monthValue,
                    budgetTransactionYear = now.minusMonths(0).year,
                    budgetTransactionAmount = 450.0,
                    budgetTransactionBudgetId = 1
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(0).monthValue,
                    budgetTransactionYear = now.minusMonths(0).year,
                    budgetTransactionAmount = 180.0,
                    budgetTransactionBudgetId = 2
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(0).monthValue,
                    budgetTransactionYear = now.minusMonths(0).year,
                    budgetTransactionAmount = 800.0,
                    budgetTransactionBudgetId = 3
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(0).monthValue,
                    budgetTransactionYear = now.minusMonths(0).year,
                    budgetTransactionAmount = 60.0,
                    budgetTransactionBudgetId = 4
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(0).monthValue,
                    budgetTransactionYear = now.minusMonths(0).year,
                    budgetTransactionAmount = 40.0,
                    budgetTransactionBudgetId = 5
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(0).monthValue,
                    budgetTransactionYear = now.minusMonths(0).year,
                    budgetTransactionAmount = 240.0,
                    budgetTransactionBudgetId = 6
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(0).monthValue,
                    budgetTransactionYear = now.minusMonths(0).year,
                    budgetTransactionAmount = 100.0,
                    budgetTransactionBudgetId = 7
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(0).monthValue,
                    budgetTransactionYear = now.minusMonths(0).year,
                    budgetTransactionAmount = 50.0,
                    budgetTransactionBudgetId = 12
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(0).monthValue,
                    budgetTransactionYear = now.minusMonths(0).year,
                    budgetTransactionAmount = 100.0,
                    budgetTransactionBudgetId = 8
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(0).monthValue,
                    budgetTransactionYear = now.minusMonths(0).year,
                    budgetTransactionAmount = 100.0,
                    budgetTransactionBudgetId = 9
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(0).monthValue,
                    budgetTransactionYear = now.minusMonths(0).year,
                    budgetTransactionAmount = 50.0,
                    budgetTransactionBudgetId = 11
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(0).monthValue,
                    budgetTransactionYear = now.minusMonths(0).year,
                    budgetTransactionAmount = 100.0,
                    budgetTransactionBudgetId = 13
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(0).monthValue,
                    budgetTransactionYear = now.minusMonths(0).year,
                    budgetTransactionAmount = 50.0,
                    budgetTransactionBudgetId = 14
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(0).monthValue,
                    budgetTransactionYear = now.minusMonths(0).year,
                    budgetTransactionAmount = 100.0,
                    budgetTransactionBudgetId = 15
                )
            )
            mainActivityViewModel.insert(
                BudgetTransaction(
                    budgetTransactionMonth = now.minusMonths(0).monthValue,
                    budgetTransactionYear = now.minusMonths(0).year,
                    budgetTransactionAmount = 100.0,
                    budgetTransactionBudgetId = 10
                )
            )

            //transaction
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 3000.0,
                    transactionTime = now.minusDays(100),
                    transactionType = 2,
                    transactionAccountId = 1,
                    transactionBudgetId = null,
                    transactionAccountTransferTo = null,
                    transactionNote = "paper come in"
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 150.0,
                    transactionTime = now.minusDays(100),
                    transactionType = 3,
                    transactionAccountId = 1,
                    transactionBudgetId = null,
                    transactionAccountTransferTo = 2
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(100),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 60.0,
                    transactionTime = now.minusDays(100),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 2,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(100),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(99),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(98),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(97),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(96),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(95),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(95),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(94),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(93),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 800.0,
                    transactionTime = now.minusDays(93),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 3,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 60.0,
                    transactionTime = now.minusDays(93),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 4,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 40.0,
                    transactionTime = now.minusDays(93),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 5,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 100.0,
                    transactionTime = now.minusDays(93),
                    transactionType = 1,
                    transactionAccountId = 2,
                    transactionBudgetId = 7,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 60.0,
                    transactionTime = now.minusDays(93),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 12,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(93),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(92),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 60.0,
                    transactionTime = now.minusDays(92),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 2,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(91),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(90),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(90),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(89),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(88),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(87),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(87),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(86),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(85),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 60.0,
                    transactionTime = now.minusDays(85),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 2,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(84),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(84),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(83),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(82),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(81),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(80),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(80),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(79),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(78),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 60.0,
                    transactionTime = now.minusDays(78),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 2,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(77),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(77),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(76),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(75),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(74),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(74),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(73),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(72),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(71),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(71),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 3000.0,
                    transactionTime = now.minusDays(70),
                    transactionType = 2,
                    transactionAccountId = 1,
                    transactionBudgetId = null,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 150.0,
                    transactionTime = now.minusDays(70),
                    transactionType = 3,
                    transactionAccountId = 1,
                    transactionBudgetId = null,
                    transactionAccountTransferTo = 2
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(70),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 60.0,
                    transactionTime = now.minusDays(70),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 2,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(69),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(69),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(68),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(67),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(66),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(65),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(65),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(64),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(63),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 60.0,
                    transactionTime = now.minusDays(63),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 2,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 800.0,
                    transactionTime = now.minusDays(62),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 3,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 60.0,
                    transactionTime = now.minusDays(62),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 4,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 40.0,
                    transactionTime = now.minusDays(62),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 5,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 100.0,
                    transactionTime = now.minusDays(62),
                    transactionType = 1,
                    transactionAccountId = 2,
                    transactionBudgetId = 7,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 80.0,
                    transactionTime = now.minusDays(62),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 12,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(61),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(61),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(60),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(59),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(58),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(58),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 60.0,
                    transactionTime = now.minusDays(58),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 2,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(57),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(56),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(55),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(55),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(54),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(53),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(52),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(52),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(51),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(50),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 60.0,
                    transactionTime = now.minusDays(50),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 2,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(49),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(49),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(48),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(47),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(46),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(45),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(44),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(43),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(42),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(42),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(41),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(40),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(39),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(38),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 3000.0,
                    transactionTime = now.minusDays(37),
                    transactionType = 2,
                    transactionAccountId = 1,
                    transactionBudgetId = null,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 150.0,
                    transactionTime = now.minusDays(37),
                    transactionType = 3,
                    transactionAccountId = 1,
                    transactionBudgetId = null,
                    transactionAccountTransferTo = 2
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(37),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 60.0,
                    transactionTime = now.minusDays(37),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 2,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(36),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(36),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(35),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(34),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(33),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(32),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(32),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(31),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(30),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 60.0,
                    transactionTime = now.minusDays(30),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 2,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 800.0,
                    transactionTime = now.minusDays(29),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 3,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 60.0,
                    transactionTime = now.minusDays(29),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 4,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 40.0,
                    transactionTime = now.minusDays(29),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 5,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 100.0,
                    transactionTime = now.minusDays(29),
                    transactionType = 1,
                    transactionAccountId = 2,
                    transactionBudgetId = 7,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 50.0,
                    transactionTime = now.minusDays(29),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 12,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(28),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(28),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(27),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(26),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(25),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(25),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(24),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(23),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(22),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 60.0,
                    transactionTime = now.minusDays(22),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 2,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(22),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(21),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(20),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(19),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(19),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(18),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(17),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(16),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(16),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 60.0,
                    transactionTime = now.minusDays(15),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 2,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(15),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(14),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(13),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(13),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(12),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(11),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(10),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(10),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(9),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(8),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(7),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(7),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 3000.0,
                    transactionTime = now.minusDays(6),
                    transactionType = 2,
                    transactionAccountId = 1,
                    transactionBudgetId = null,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 150.0,
                    transactionTime = now.minusDays(6),
                    transactionType = 3,
                    transactionAccountId = 1,
                    transactionBudgetId = null,
                    transactionAccountTransferTo = 2
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(6),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 60.0,
                    transactionTime = now.minusDays(6),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 2,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(6),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(5),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(4),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(3),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(2),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 30.0,
                    transactionTime = now.minusDays(1),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 6,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(1),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
            mainActivityViewModel.insert(
                Transaction(
                    transactionAmount = 15.0,
                    transactionTime = now.minusDays(0),
                    transactionType = 1,
                    transactionAccountId = 1,
                    transactionBudgetId = 1,
                    transactionAccountTransferTo = null
                )
            )
        }
    }
}