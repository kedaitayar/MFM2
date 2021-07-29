package io.github.kedaitayar.mfm

import android.view.View
import android.view.ViewGroup
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onData
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.PerformException
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.idling.CountingIdlingResource
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.kedaitayar.mfm.data.entity.Account
import io.github.kedaitayar.mfm.data.podata.AccountListAdapterData
import io.github.kedaitayar.mfm.ui.budget.budget_list.BudgetListAdapter
import io.github.kedaitayar.mfm.ui.budget.budgeting.BudgetingListAdapter
import io.github.kedaitayar.mfm.ui.dashboard.account.main_account.AccountListAdapter
import io.github.kedaitayar.mfm.ui.transaction.transaction_list.TransactionListAdapter
import io.github.kedaitayar.mfm.util.childAtPosition
import io.github.kedaitayar.mfm.util.clickOnViewChild
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.*
import org.hamcrest.TypeSafeMatcher
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.lang.AssertionError
import java.lang.Exception
import java.util.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit


/**
 * [MainActivity]
 * */
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class MainActivityTest {
    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    private lateinit var activityScenario: ActivityScenario<MainActivity>

    @Before
    fun setUp() {
        hiltAndroidRule.inject()
        activityScenario = ActivityScenario.launch(MainActivity::class.java)
    }

    @After
    fun tearDown() {
        activityScenario.close()
    }

    /**
     * --- test flow
     * click add account button
     * add account page open
     * fill in the account name input
     * click add button
     * return back
     * snackbar show add account status
     * check added acccount exist
     * click more menu button
     * click detail
     * account detail page open
     * click back button
     * click more menu button
     * click edit
     * edit account page open
     * change account name
     * click save account
     * return back
     * snackbar show save account status
     * check account is updated
     * click more menu button
     * click edit
     * edit account page open
     * click delete account
     * confirmation dialog open
     * click delete
     * return back
     * snackbar show delete status
     * check account is deleted from the account list
     */
    @Test
    fun addAccount_accountDetail_editAccount_deleteAccount() {
        val accountName = "Bank"
        val newAccountName = "BankTest"

        // add acount
        onView(withId(R.id.main_activity_root)).check(matches(isDisplayed()))
        onView(withId(R.id.button_add_account))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withId(R.id.topAppBar))
            .check(matches(isDisplayed()))
            .check(matches(hasDescendant(withText("Add Account"))))

        onView(withId(R.id.text_input_edit_account_name))
            .check(matches(isDisplayed()))

        onView(withId(R.id.button_add_account))
            .check(matches(isDisplayed()))

        onView(withId(R.id.text_input_edit_account_name))
            .perform(typeText(accountName))
            .check(matches(withText(accountName)))

        onView(withId(R.id.button_add_account))
            .perform(click())

        onView(withText("Account added"))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))


        //accountDetail
        onView(withId(R.id.recycler_view_account_list))
            .perform(
                RecyclerViewActions.scrollTo<AccountListAdapter.AccountListViewHolder>(
                    hasDescendant(withText(accountName))
                )
            )
            .perform(
                RecyclerViewActions.actionOnItem<AccountListAdapter.AccountListViewHolder>(
                    hasDescendant(withText(accountName)), clickOnViewChild(R.id.button_more)
                )
            )

        onView(withText("Detail"))
            .perform(click())

        onView(
            childAtPosition(
                allOf(
                    withId(R.id.topAppBar),
                    childAtPosition(
                        withId(R.id.toolbar),
                        0
                    )
                ),
                0
            )
        )
            .perform(click())


        onView(withId(R.id.recycler_view_account_list))
            .perform(
                RecyclerViewActions.scrollTo<AccountListAdapter.AccountListViewHolder>(
                    hasDescendant(withText(accountName))
                )
            )
            // edit account
            .perform(
                RecyclerViewActions.actionOnItem<AccountListAdapter.AccountListViewHolder>(
                    hasDescendant(withText(accountName)), clickOnViewChild(R.id.button_more)
                )
            )

        onView(withText("Edit"))
            .perform(click())

        onView(withId(R.id.topAppBar))
            .check(matches(isDisplayed()))
            .check(matches(hasDescendant(withText("Edit Account"))))

        onView(withId(R.id.text_input_edit_account_name))
            .check(matches(withText(accountName)))
            .perform(replaceText(newAccountName))

        onView(withId(R.id.button_add_account))
            .perform(click())

        onView(withText("Account updated"))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withId(R.id.recycler_view_account_list))
            .perform(
                RecyclerViewActions.scrollTo<AccountListAdapter.AccountListViewHolder>(
                    hasDescendant(withText(newAccountName))
                )
            )
            // delete account
            .perform(
                RecyclerViewActions.actionOnItem<AccountListAdapter.AccountListViewHolder>(
                    hasDescendant(withText(newAccountName)),
                    clickOnViewChild(R.id.button_more)
                )
            )

        onView(withText("Edit"))
            .perform(click())

        onView(withId(R.id.delete))
            .perform(click())

        onView(withText("Delete account?"))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withText("Delete"))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            .perform(click())

        onView(withText("Account deleted"))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        try {
            onView(withId(R.id.recycler_view_account_list))
                .perform(
                    RecyclerViewActions.scrollTo<AccountListAdapter.AccountListViewHolder>(
                        hasDescendant(withText(newAccountName))
                    )
                )

            // if deleted account exist, throw AssertionError
            throw  AssertionError("Deleted account still exist.")
        } catch (e: PerformException) {
            /**
             * swallowing the exception because
             * in the try block, its searching for the deleted account, checking if the account still exist,
             * since the account is already deleted, exception occurs, which is the expected result
             */
        }
    }

    @Test
    fun addEditDeleteIncomeTransaction() {
        val account01 = "Cash"
        val account01String = "Cash (RM 0.00)"
        val amount = "1000"
        val amount2 = "2000"
        val waiter = CountDownLatch(1)
        onView(withId(R.id.view_pager_main)).perform(swipeLeft())
        onView(withId(R.id.fab)).perform(click())
        onView(withId(R.id.view_pager)).perform(swipeLeft())
        waiter.await(200, TimeUnit.MILLISECONDS)
        onView(
            allOf(
                withId(R.id.autoComplete_account),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.text_input_layout_account),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        ).perform(click())

        // click dropdown with name account01
        // .inRoot(RootMatchers.isPlatformPopup()) - https://stackoverflow.com/a/48869887/12528485
        onView(withText(account01String))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(click())

        onView(withId(R.id.autoComplete_account))
            .check(matches(withText(account01)))

        onView(withId(R.id.text_input_edit_amount))
            .check(matches(isDisplayed()))
            .perform(typeText(amount))
            .check(matches(withText(amount)))

        onView(withId(R.id.button_save))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withText("Transaction added"))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        // update income
        onView(withId(R.id.recycler_view_transaction_list))
            .perform(
                RecyclerViewActions.scrollTo<TransactionListAdapter.TransactionListViewHolder>(
                    hasDescendant(withText("Income"))
                )
            )
            .perform(
                click()
//                RecyclerViewActions.actionOnItem<TransactionListAdapter.TransactionListViewHolder>(
//                    hasDescendant(withText("Income")), clickOnViewChild(R.id.button_popup_menu)
//                )
            )

//        onView(withText("Edit"))
//            .perform(click())

        onView(withId(R.id.text_input_edit_amount))
            .perform(replaceText(amount2))

        onView(withId(R.id.button_save))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withText("Transaction updated"))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        // delete income
        onView(withId(R.id.recycler_view_transaction_list))
            .perform(
                RecyclerViewActions.scrollTo<TransactionListAdapter.TransactionListViewHolder>(
                    hasDescendant(withText("Income"))
                )
            )
            .perform(
                RecyclerViewActions.actionOnItem<TransactionListAdapter.TransactionListViewHolder>(
                    hasDescendant(withText("Income")), clickOnViewChild(R.id.button_popup_menu)
                )
            )

        onView(withText("Edit"))
            .perform(click())

        onView(withId(R.id.delete))
            .perform(click())

        onView(withText("Delete transaction?"))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withText("Delete"))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            .perform(click())

        onView(withText("Transaction deleted"))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        try {
            onView(withId(R.id.recycler_view_transaction_list))
                .perform(
                    RecyclerViewActions.scrollTo<AccountListAdapter.AccountListViewHolder>(
                        hasDescendant(withText("Income"))
                    )
                )

            // if deleted account exist, throw AssertionError
            throw  AssertionError("Deleted account still exist.")
        } catch (e: PerformException) {
            /**
             * swallowing the exception because
             * in the try block, its searching for the deleted account, checking if the account still exist,
             * since the account is already deleted, exception occurs, which is the expected result
             */
        }

//        waiter.await(2000, TimeUnit.MILLISECONDS)
    }

    @Test
    fun addEditDeleteExpenseTransaction() {
        val account01 = "Cash"
        val budget = "Food"
        val amount = "50"
        val amount2 = "100"
        val waiter = CountDownLatch(1)
        onView(withId(R.id.view_pager_main)).perform(swipeLeft())
        onView(withId(R.id.fab)).perform(click())
        waiter.await(100, TimeUnit.MILLISECONDS)
        //select account
        onView(
            allOf(
                withId(R.id.autoComplete_account),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.text_input_layout_account),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        ).perform(click())

        // click dropdown with name account01
        // .inRoot(RootMatchers.isPlatformPopup()) - https://stackoverflow.com/a/48869887/12528485
        onView(withText(account01))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(click())

        onView(withId(R.id.autoComplete_account))
            .check(matches(withText(account01)))

        //select budget
        onView(
            allOf(
                withId(R.id.autoComplete_budget),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.text_input_layout_budget),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        ).perform(click())

        onView(withText(budget))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(click())

        onView(withId(R.id.autoComplete_budget))
            .check(matches(withText(budget)))

        //input amount
        onView(withId(R.id.text_input_edit_amount))
            .check(matches(isDisplayed()))
            .perform(typeText(amount))
            .check(matches(withText(amount)))

        onView(withId(R.id.button_save))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withText("Transaction added"))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        // update income
        onView(withId(R.id.recycler_view_transaction_list))
            .perform(
                RecyclerViewActions.scrollTo<TransactionListAdapter.TransactionListViewHolder>(
                    hasDescendant(withText(budget))
                )
            )
            .perform(
                RecyclerViewActions.actionOnItem<TransactionListAdapter.TransactionListViewHolder>(
                    hasDescendant(withText(budget)), clickOnViewChild(R.id.button_popup_menu)
                )
            )

        onView(withText("Edit"))
            .perform(click())

        onView(withId(R.id.text_input_edit_amount))
            .perform(replaceText(amount2))

        onView(withId(R.id.button_save))
            .check(matches(isDisplayed()))
            .perform(click())

        onView(withText("Transaction updated"))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        // delete income
        onView(withId(R.id.recycler_view_transaction_list))
            .perform(
                RecyclerViewActions.scrollTo<TransactionListAdapter.TransactionListViewHolder>(
                    hasDescendant(withText(budget))
                )
            )
            .perform(
                RecyclerViewActions.actionOnItem<TransactionListAdapter.TransactionListViewHolder>(
                    hasDescendant(withText(budget)), clickOnViewChild(R.id.button_popup_menu)
                )
            )

        onView(withText("Edit"))
            .perform(click())

        onView(withId(R.id.delete))
            .perform(click())

        onView(withText("Delete transaction?"))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withText("Delete"))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            .perform(click())

        onView(withText("Transaction deleted"))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        try {
            onView(withId(R.id.recycler_view_transaction_list))
                .perform(
                    RecyclerViewActions.scrollTo<AccountListAdapter.AccountListViewHolder>(
                        hasDescendant(withText(budget))
                    )
                )

            // if deleted account exist, throw AssertionError
            throw  AssertionError("Deleted account still exist.")
        } catch (e: PerformException) {
            /**
             * swallowing the exception because
             * in the try block, its searching for the deleted account, checking if the account still exist,
             * since the account is already deleted, exception occurs, which is the expected result
             */
        }

//        waiter.await(2000, TimeUnit.MILLISECONDS)
    }

    @Test
    fun budgeting() {
        val budget = "Test"
        val goal = "50"
        val goal2 = "100"
        val type = "Monthly"
        val budgeted = "50"
        val budgetedAfter = "50.0"
        val waiter = CountDownLatch(1)
        onView(withId(R.id.view_pager_main))
            .perform(swipeLeft())
            .perform(swipeLeft())
        waiter.await(100, TimeUnit.MILLISECONDS)

        onView(withId(R.id.button_add_budget))
            .perform(click())

        onView(withId(R.id.text_input_edit_budget_name))
            .perform(typeText(budget))

        onView(withId(R.id.text_input_edit_budget_goal))
            .perform(typeText(goal))

        onView(
            allOf(
                withId(R.id.autoComplete_budget_type),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.text_input_layout_budget_type),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        ).perform(click())

        // click dropdown with name account01
        // .inRoot(RootMatchers.isPlatformPopup()) - https://stackoverflow.com/a/48869887/12528485
        onView(withText(type))
            .inRoot(RootMatchers.isPlatformPopup())
            .perform(click())

        onView(withId(R.id.autoComplete_budget_type))
            .check(matches(withText(type)))

        onView(withId(R.id.button_add_budget))
            .perform(click())

        onView(withText("Budget added"))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(
            allOf(
                hasSibling(allOf(withId(R.id.text_view_budget_name), withText(budget))),
                withId(R.id.text_view_budgeted)
            )
        )
            .check(matches(isDisplayed()))

        //budgeting
        onView(withId(R.id.button_budgeting))
            .check(matches(isDisplayed()))
            .perform(click())

        waiter.await(300, TimeUnit.MILLISECONDS)

        onView(
            allOf(
                isDescendantOfA(
                    allOf(
                        withId(R.id.text_input_layout_amount),
                        hasSibling(withText(budget))
                    )
                ),
                withId(R.id.text_input_edit_amount)
            )
        )
//            .check(matches(withText("0.0")))
            .perform(typeText(budgeted))

        onView(withId(R.id.save_budgeting))
            .perform(click())

        // edit budget
        onView(
            allOf(
                hasSibling(allOf(withId(R.id.text_view_budget_name), withText(budget))),
                withId(R.id.text_view_budgeted)
            )
        )
            .check(matches(withText(budgetedAfter)))

        onView(
            allOf(
                hasSibling(allOf(withId(R.id.text_view_budget_name), withText(budget))),
                withId(R.id.button_more)
            )
        )
            .perform(click())

        onView(withText("Edit"))
            .perform(click())

        onView(withId(R.id.text_input_edit_budget_goal))
            .perform(replaceText(goal2))

        onView(withId(R.id.button_add_budget))
            .perform(click())

        onView(withText("Budget updated"))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        //delete budget
        onView(
            allOf(
                hasSibling(allOf(withId(R.id.text_view_budget_name), withText(budget))),
                withId(R.id.text_view_budgeted)
            )
        )
            .check(matches(withText(budgetedAfter)))

        onView(
            allOf(
                hasSibling(allOf(withId(R.id.text_view_budget_name), withText(budget))),
                withId(R.id.button_more)
            )
        )
            .perform(click())

        onView(withText("Edit"))
            .perform(click())

        onView(withId(R.id.delete))
            .perform(click())

        onView(withText("Delete budget?"))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        onView(withText("Delete"))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))
            .perform(click())

        onView(withText("Budget deleted"))
            .check(matches(withEffectiveVisibility(Visibility.VISIBLE)))

        try {
            onView(
                allOf(
                    hasSibling(allOf(withId(R.id.text_view_budget_name), withText(budget))),
                    withId(R.id.text_view_budgeted)
                )
            )
                .check(matches(isDisplayed()))

            // if deleted budget exist, throw AssertionError
            throw  AssertionError("Deleted budget still exist.")
        } catch (e: NoMatchingViewException) {
            /**
             * swallowing the exception because
             * in the try block, its searching for the deleted budget, checking if the budget still exist,
             * since the budget is already deleted, exception occurs, which is the expected result
             */
        }

//        waiter.await(2000, TimeUnit.MILLISECONDS)
    }
}