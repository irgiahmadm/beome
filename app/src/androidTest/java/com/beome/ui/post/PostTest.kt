package com.beome.ui.post

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.beome.R
import com.beome.ui.authentication.AuthenticationActivity
import com.beome.ui.utils.UtilsTest.atItem
import com.beome.ui.utils.UtilsTest.getText
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.containsString
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PostTest {

    @Test
    fun testLikePost(){
        //start activity
        val activityScenario = ActivityScenario.launch(
            AuthenticationActivity::class.java
        )
        //delay
        Thread.sleep(2000)
        //click recycler view
        onView(withId(R.id.recyclerRecentPost)).atItem(0, click())
        //delay
        Thread.sleep(2000)
        //get text like count before post is liked
        val likeBefore = getText(withId(R.id.textViewLikeCount))
        //click like button
        onView(withId(R.id.imageViewLikeInactiveButton)).perform(click())
        //check unlike button is displayed after liked
        onView(withId(R.id.imageViewLikeActiveButton)).check(matches(isDisplayed()))
        //delay
        Thread.sleep(2000)
        //get like count after post is liked
        val likeAfter = (likeBefore!!.toInt() + 1).toString()
        //check like counter is changed or not
        onView(withId(R.id.textViewLikeCount)).check(matches(withText(likeAfter)))
        //close activity
        activityScenario.close()
    }

    @Test
    fun testUnLikePost(){
        //start activity
        val activityScenario = ActivityScenario.launch(
            AuthenticationActivity::class.java
        )
        //delay
        Thread.sleep(2000)
        //click recycler view
        onView(withId(R.id.recyclerRecentPost)).atItem(0, click())
        //delay
        Thread.sleep(2000)
        //get text like count before post is liked
        val likeBefore = getText(withId(R.id.textViewLikeCount))
        //click unlike button
        onView(withId(R.id.imageViewLikeActiveButton)).perform(click())
        //check like button is displayed after unliked
        onView(withId(R.id.imageViewLikeInactiveButton)).check(matches(isDisplayed()))
        //delay
        Thread.sleep(2000)
        //get like count after post is liked
        val likeAfter = (likeBefore!!.toInt() - 1).toString()
        //check like counter is changed or not
        onView(withId(R.id.textViewLikeCount)).check(matches(withText(likeAfter)))
        //close activity
        activityScenario.close()
    }
}