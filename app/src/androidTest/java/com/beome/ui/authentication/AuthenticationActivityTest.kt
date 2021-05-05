package com.beome.ui.authentication


import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.beome.R
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.Matcher
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class AuthenticationActivityTest {

    @Test
    fun registerTest() {
        //start activity
        val activityScenario = ActivityScenario.launch(
            AuthenticationActivity::class.java
        )
        //click text register
        onView(withText("Register Here")).perform(click())
        //fill full name
        onView(withId(R.id.editTextName)).perform(typeText("usertest"), closeSoftKeyboard())
        //fill birth date
        onView(withId(R.id.editTextBirthDate)).perform(click())
        onView(withText("OK")).perform(click())
        //fill username
        onView(withId(R.id.editTextUsername)).perform(typeText("usertest"), closeSoftKeyboard())
        //fill email
        onView(withId(R.id.editTextEmail)).perform(
            typeText("usertest@gmail.com"),
            closeSoftKeyboard()
        )
        //fill password
        onView(withId(R.id.editTextPassword)).perform(
            scrollTo(),
            typeText("usertest123"),
            closeSoftKeyboard()
        )
        //click register
        onView(withId(R.id.buttonSignup)).perform(click())

//        onView(withText("Beome")).check(matches(isDisplayed()))
        //close activity
        activityScenario.close()
    }

    @Test
    fun loginTest() {
        //start activity
        val activityScenario = ActivityScenario.launch(
            AuthenticationActivity::class.java
        )
        //fill email
        onView(withId(R.id.editTextEmail)).perform(
            typeText("usertest@gmail.com"),
            closeSoftKeyboard()
        )
        //fill password
        onView(withId(R.id.editTextPassword)).perform(typeText("usertest123"), closeSoftKeyboard())
        //click button login
        onView(withId(R.id.buttonLogin)).perform(click())
        //delay
        Thread.sleep(2000)
        //login is succes if text Beome at MainActivity is show
        onView(withText("Beome")).check(matches(isDisplayed()))
        activityScenario.close()
    }

    @Test
    fun searchTestPeople(){
        val activityScenario = ActivityScenario.launch(
            AuthenticationActivity::class.java
        )
        //click search menu
        onView(withId(R.id.navigation_search)).perform(click())
        //click section people
        onView(withText("People")).perform(click())
        //type search irgi
        onView(withId(R.id.searchViewPeople)).perform(typeSearchViewText("irgi"))
        //delay
        Thread.sleep(2000)
        //click recycler view
        onView(withId(R.id.recyclerSearchUser)).atItem(0, click())
        //check profile preview user
        onView(withText(containsString("irgi")))
        //close activity
        activityScenario.close()
    }


    private fun ViewInteraction.atItem(pos: Int, action: ViewAction) {
        perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                pos, action
            )
        )
    }

    private fun typeSearchViewText(text: String?): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                //Ensure that only apply if it is a SearchView and if it is visible.
                return allOf(isDisplayed(), isAssignableFrom(SearchView::class.java))
            }

            override fun getDescription(): String {
                return "Change view text"
            }

            override fun perform(uiController: UiController, view: View) {
                (view as SearchView).setQuery(text, true)
            }
        }
    }
}
