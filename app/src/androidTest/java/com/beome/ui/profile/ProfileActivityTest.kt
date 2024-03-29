package com.beome.ui.profile

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
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
        const val USERNAME = "irgiahmadm"
        const val FULLNAME = "Irgi Ahmad M"
        const val EMAIL = "irgiahmadmmaulana@gmail.com"
        const val OLD_PASSWORD = "irgi160500"
        const val NEW_PASSWORD = "irgiamau160500"
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

    @Test
    fun editProfileTest(){
        //start activity
        val activityScenario = ActivityScenario.launch(
            AuthenticationActivity::class.java
        )
        //click menu profile
        onView(withId(R.id.navigation_profile)).perform(click())
        //click edit profile
        onView(withId(R.id.buttonEditProfile)).perform(click())
        //fill username
        onView(withId(R.id.editTextUsername)).perform(clearText(), replaceText(USERNAME))
        //fill fullname
        onView(withId(R.id.editTextFullname)).perform(replaceText(FULLNAME))
        //fill emaill
        onView(withId(R.id.editTextEmail)).perform(replaceText(EMAIL))
        //fill birthdate
        onView(withId(R.id.editTextBirthDate)).perform(click())
        onView(withText("OK")).perform(click())
        //click submit
        onView(withId(R.id.submit_menu)).perform(click())
        //delay
        Thread.sleep(2000)
        //check is usename changed
        onView(withText(FULLNAME)).check(matches(isDisplayed()))
        //close
        activityScenario.close()
    }

    @Test
    fun changePasswordTest(){
        //start activity
        val activityScenario = ActivityScenario.launch(
            AuthenticationActivity::class.java
        )
        //click menu profile
        onView(withId(R.id.navigation_profile)).perform(click())
        //click edit profile
        onView(withId(R.id.buttonEditProfile)).perform(click())
        //click button change password
        onView(withId(R.id.buttonChangePassword)).perform(click())
        //fill old password
        onView(withId(R.id.editTextOldPassword)).perform(replaceText(OLD_PASSWORD))
        //fill new password
        onView(withId(R.id.editTextNewPassword)).perform(replaceText(NEW_PASSWORD), closeSoftKeyboard())
        //submit
        onView(withId(R.id.buttonSubmit)).perform(click())
        //delay
        Thread.sleep(2000)
        //check if back to edit profile
        onView(withText("Edit Profile")).check(matches(isDisplayed()))
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