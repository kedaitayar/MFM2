package io.github.kedaitayar.mfm

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels

import dagger.hilt.android.AndroidEntryPoint
import io.github.kedaitayar.mfm.data.entity.BudgetType
import io.github.kedaitayar.mfm.data.entity.TransactionType
import io.github.kedaitayar.mfm.viewmodels.BudgetViewModel
import io.github.kedaitayar.mfm.viewmodels.TransactionViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private val budgetViewModel: BudgetViewModel by viewModels()
    private val transactionViewModel: TransactionViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

//        CoroutineScope(Dispatchers.IO).launch {
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
//        }
    }

}