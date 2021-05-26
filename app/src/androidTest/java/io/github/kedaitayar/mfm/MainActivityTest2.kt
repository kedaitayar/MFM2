package io.github.kedaitayar.mfm


import android.view.View
import android.view.ViewGroup
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.allOf
import org.hamcrest.TypeSafeMatcher
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@LargeTest
@RunWith(AndroidJUnit4::class)
class MainActivityTest2 {

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun mainActivityTest2() {
        val tabView = onView(
            allOf(
                withContentDescription("Budget"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.tab_layout),
                        0
                    ),
                    2
                ),
                isDisplayed()
            )
        )
        tabView.perform(click())

        val materialButton = onView(
            allOf(
                withId(R.id.button_budgeting), withText("budgeting"),
                childAtPosition(
                    childAtPosition(
                        withClassName(`is`("androidx.appcompat.widget.LinearLayoutCompat")),
                        2
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        materialButton.perform(click())

        val textInputEditText = onView(
            allOf(
                withId(R.id.text_input_edit_amount), withText("0.0"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.text_input_layout_amount),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        textInputEditText.perform(replaceText("10"))

        val textInputEditText2 = onView(
            allOf(
                withId(R.id.text_input_edit_amount), withText("10"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.text_input_layout_amount),
                        0
                    ),
                    1
                ),
                isDisplayed()
            )
        )
        textInputEditText2.perform(closeSoftKeyboard())

        val actionMenuItemView = onView(
            allOf(
                withId(R.id.save_budgeting), withContentDescription("Save"),
                childAtPosition(
                    childAtPosition(
                        withId(R.id.topAppBar),
                        0
                    ),
                    0
                ),
                isDisplayed()
            )
        )
        actionMenuItemView.perform(click())

        val textView = onView(
            allOf(
                withId(R.id.text_view_budgeted), withText("10.0"),
                withParent(withParent(withId(R.id.recycler_view))),
                isDisplayed()
            )
        )
        textView.check(matches(withText("10.0")))

        val textView2 = onView(
            allOf(
                withId(R.id.text_view_available), withText("10.0"),
                withParent(withParent(withId(R.id.recycler_view))),
                isDisplayed()
            )
        )
        textView2.check(matches(withText("10.0")))
    }

    private fun childAtPosition(
        parentMatcher: Matcher<View>, position: Int
    ): Matcher<View> {

        return object : TypeSafeMatcher<View>() {
            override fun describeTo(description: Description) {
                description.appendText("Child at position $position in parent ")
                parentMatcher.describeTo(description)
            }

            public override fun matchesSafely(view: View): Boolean {
                val parent = view.parent
                return parent is ViewGroup && parentMatcher.matches(parent)
                        && view == parent.getChildAt(position)
            }
        }
    }
}
