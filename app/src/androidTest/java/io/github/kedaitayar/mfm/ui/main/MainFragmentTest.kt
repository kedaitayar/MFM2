package io.github.kedaitayar.mfm.ui.main

import com.google.common.truth.Truth.assertThat
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.internal.runner.junit4.statement.UiThreadStatement.runOnUiThread
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.kedaitayar.mfm.R
import io.github.kedaitayar.mfm.launchFragmentInHiltContainer
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * [MainFragment]
 *
 */

@HiltAndroidTest
class MainFragmentTest {
    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltAndroidRule.inject()
    }

    @Test
    fun instantiateMainFragment() {
        launchFragmentInHiltContainer<MainFragment> {}
        onView(withId(R.id.main_fragment_root)).check(matches(isDisplayed()))
    }

    @Test
    fun clickAddTransactionFAB_OpenAddTransaction() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
       runOnUiThread {
           navController.setGraph(R.navigation.nav_graph)
       }

        launchFragmentInHiltContainer<MainFragment> {
            Navigation.setViewNavController(requireView(), navController)
        }
        onView(withId(R.id.fab)).check(matches(isDisplayed()))
        onView(withId(R.id.fab)).perform(click())
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.addTransactionFragment)
    }
}