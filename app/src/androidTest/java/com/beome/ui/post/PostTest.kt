package com.beome.ui.post

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
import com.beome.ui.utils.UtilsTest.getText
import org.hamcrest.CoreMatchers.containsString
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PostTest {

    companion object{
        const val CHANGE_TITLE_POST = "bar foo"
        const val CHANGE_DESCRIPTION = "bar foo"
    }

    @Test
    fun testLikePost(){
        //start activity
        val activityScenario = ActivityScenario.launch(
            AuthenticationActivity::class.java
        )
        //delay
        Thread.sleep(2000)
        //click recycler view
        onView(withId(R.id.recyclerRecentPost)).atItem(1, click())
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
        onView(withId(R.id.recyclerRecentPost)).atItem(1, click())
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

    @Test
    fun editPost(){
        val activityScenario = ActivityScenario.launch(
            AuthenticationActivity::class.java
        )
        //delay
        Thread.sleep(2000)
        //click profile menu
        onView(withId(R.id.navigation_profile)).perform(click())
        //delay
        Thread.sleep(2000)
        //click recycler
        onView(withId(R.id.recyclerProfilePost)).atItem(0, click())
        //click edit post
        onView(withId(R.id.buttonEditPost)).perform(click())
        //fill title
        onView(withId(R.id.editTextPostTitle)).perform(scrollTo(), replaceText(CHANGE_TITLE_POST))
        //fill description
        onView(withId(R.id.editTextPostDesc)).perform(scrollTo(), replaceText(CHANGE_DESCRIPTION))
        //click button edit post
        onView(withId(R.id.buttonEdit)).perform(click())
        //delay
        Thread.sleep(2000)
        //check if there any contain text that changed
        onView(withText(containsString(CHANGE_TITLE_POST)))
        onView(withText(containsString(CHANGE_DESCRIPTION)))
        //close activity
        activityScenario.close()
    }

    @Test
    fun testGiveFeedback(){
        val activityScenario = ActivityScenario.launch(
            AuthenticationActivity::class.java
        )
        //delay
        Thread.sleep(2000)
        //click recycler view
        onView(withId(R.id.recyclerRecentPost)).atItem(1, click())
        //click button give feedback
        onView(withId(R.id.buttonGiveFeedback)).perform(click())
        //click rate button 1
        onView(withId(R.id.recyclerViewFeedbackComponent)).atItem(0,
            UtilsTest.clickChildViewWithId(R.id.radioButton4)!!
        )
        //click rate button 2
        onView(withId(R.id.recyclerViewFeedbackComponent)).atItem(1,
            UtilsTest.clickChildViewWithId(R.id.radioButton5)!!
        )
        //add comment
        onView(withId(R.id.editTextComment)).perform(scrollTo(), typeText("Nice work, keep it up!"))
        //click button submit
        onView(withId(R.id.buttonSubmitFeedback)).perform(click())
        //delay
        Thread.sleep(2000)
        //feedback success if back to detail post after submit
        onView(withText("Post Detail")).check(matches(isDisplayed()))
        //close
        activityScenario.close()
    }
}