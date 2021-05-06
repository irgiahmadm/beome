package com.beome.ui.profile

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.beome.R
import com.beome.ui.authentication.AuthenticationActivity
import com.beome.ui.utils.UtilsTest
import com.beome.ui.utils.UtilsTest.atItem
import com.beome.ui.utils.UtilsTest.typeSearchViewText
import org.hamcrest.CoreMatchers.containsString
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileActivityTest {

    companion object{
        const val SEARCH_PEOPLE_QUERY = "haikal"
    }

    @Test
    fun followTest(){
        //start activity
        val activityScenario = ActivityScenario.launch(
            AuthenticationActivity::class.java
        )
        //search people
        searchPeople()
        //click follow button
        onView(withText("FOLLOW")).perform(click())
        //get follower before
        val followerBefore = UtilsTest.getText(withId(R.id.textViewFollowersCount))
        //delay
        Thread.sleep(1500)
        //check is unfollow text is displayed after follow
        onView(withText("UNFOLLOW")).check(matches(isDisplayed()))
        Thread.sleep(2000)
        //get follower count after follow
        val followerAfter = (followerBefore!!.toInt() + 1).toString()
        //check follower count after follow
        onView(withId(R.id.textViewFollowersCount)).check(matches(withText(followerAfter)))
        //close activity
        activityScenario.close()
    }

    @Test
    fun unfollowTest(){
        //start activity
        val activityScenario = ActivityScenario.launch(
            AuthenticationActivity::class.java
        )
        //search people
        searchPeople()
        //click unfollow button
        onView(withText("UNFOLLOW")).perform(click())
        //get follower before
        val followerBefore = UtilsTest.getText(withId(R.id.textViewFollowersCount))
        //delay
        Thread.sleep(1500)
        //check is follow text is displayed after unfollow
        onView(withText("FOLLOW")).check(matches(isDisplayed()))
        Thread.sleep(2000)
        //get follower count after unfollow
        val followerAfter = (followerBefore!!.toInt() - 1).toString()
        //check follower count after unfollow
        onView(withId(R.id.textViewFollowersCount)).check(matches(withText(followerAfter)))
        //close activity
        activityScenario.close()
    }

    private fun searchPeople(){
        //click search menu
        onView(withId(R.id.navigation_search)).perform(click())
        //click section people
        onView(withText("People")).perform(click())
        //type search query
        onView(withId(R.id.searchViewPeople)).perform(typeSearchViewText(SEARCH_PEOPLE_QUERY
        ))
        //delay
        Thread.sleep(2000)
        //click recycler view
        onView(withId(R.id.recyclerSearchUser)).atItem(0, click())
        //check profile preview user
        onView(withText(containsString(SEARCH_PEOPLE_QUERY)))
    }
}