package io.github.kedaitayar.mfm.ui.dashboard

import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.internal.runner.junit4.statement.UiThreadStatement
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import io.github.kedaitayar.mfm.R
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
* [MainDashboardFragment]
*/
@HiltAndroidTest
class MainDashboardFragmentTest {
    @get:Rule
    var hiltAndroidRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltAndroidRule.inject()
    }

    @Test
    fun addAccount() {
        val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
        UiThreadStatement.runOnUiThread {
            navController.setGraph(R.navigation.nav_graph)
        }


    }
}