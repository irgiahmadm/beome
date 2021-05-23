package com.beome.ui.search

import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.beome.R
import com.beome.ui.authentication.AuthenticationActivity
import com.beome.ui.utils.UtilsTest.atItem
import com.beome.ui.utils.UtilsTest.typeSearchViewText
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FragmentSearchTest {

    companion object{
        const val SEARCH_POST_QUERY = "my"
        const val SEARCH_PEOPLE_QUERY = "irgi"
    }
    @Test
    fun searchUserTest(){
        val activityScenario = ActivityScenario.launch(
            AuthenticationActivity::class.java
        )
        //click search menu
        onView(withId(R.id.navigation_search)).perform(click())
        //click section people
        onView(ViewMatchers.withText("People")).perform(click())
        //type search query
        onView(withId(R.id.searchViewPeople)).perform(typeSearchViewText(
            SEARCH_PEOPLE_QUERY
        ))
        //delay
        Thread.sleep(2000)
        //click recycler view
        onView(withId(R.id.recyclerSearchUser)).atItem(0, click())
        //check profile preview user
        onView(ViewMatchers.withText(CoreMatchers.containsString(SEARCH_PEOPLE_QUERY)))
        //close activity
        activityScenario.close()
    }

    @Test
    fun searchPostTest(){
        val activityScenario = ActivityScenario.launch(
            AuthenticationActivity::class.java
        )
        //click search menu
        onView(withId(R.id.navigation_search)).perform(click())
        //type search query
        onView(withId(R.id.searchView)).perform(typeSearchViewText(
            SEARCH_POST_QUERY
        ))
        //delay
        Thread.sleep(2000)
        //click recycler view
        onView(withId(R.id.recyclerSearchPost)).atItem(0, click())
        //check profile preview user
        onView(ViewMatchers.withText(CoreMatchers.containsString(SEARCH_POST_QUERY)))
        //close activity
        activityScenario.close()
    }


}