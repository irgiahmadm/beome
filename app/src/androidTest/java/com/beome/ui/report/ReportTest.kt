package com.beome.ui.report

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.beome.R
import com.beome.ui.authentication.AuthenticationActivity
import com.beome.ui.utils.UtilsTest
import com.beome.ui.utils.UtilsTest.atItem
import org.hamcrest.CoreMatchers.containsString
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ReportTest {
    companion object{
        const val SEARCH_PEOPLE_QUERY = "haikal"
    }
    @Test
    fun reportUser(){
        val activityScenario = ActivityScenario.launch(
            AuthenticationActivity::class.java
        )
        //click search menu
        onView(withId(R.id.navigation_search)).perform(click())
        //click section people
        onView(withText("People")).perform(click())
        //type search query
        onView(withId(R.id.searchViewPeople)).perform(
            UtilsTest.typeSearchViewText(
                SEARCH_PEOPLE_QUERY
            )
        )
        //delay
        Thread.sleep(2000)
        //click recycler view
        onView(withId(R.id.recyclerSearchUser)).atItem(0, click())
        //check profile preview user
        onView(withText(containsString(SEARCH_PEOPLE_QUERY)))
        //click overflow menu
        openContextualActionModeOverflowMenu()
        //click menu report
        inputReportReason("user")
        //check profile preview user
        onView(withText(containsString(SEARCH_PEOPLE_QUERY)))
        //close
        activityScenario.close()
    }

    @Test
    fun reportPost(){
        val activityScenario = ActivityScenario.launch(
            AuthenticationActivity::class.java
        )
        //delay
        Thread.sleep(2000)
        //click recycler
        onView(withId(R.id.recyclerRecentPost)).atItem(1, click())
        //click overflow menu
        openContextualActionModeOverflowMenu()
        //click menu report
        inputReportReason("post")
        //check if back to detail post
        onView(withText("Post Detail"))
        //close
        activityScenario.close()
    }

    @Test
    fun testReportFeedback(){
        val activityScenario = ActivityScenario.launch(
            AuthenticationActivity::class.java
        )
        //delay
        Thread.sleep(2000)
        //click recycler
        onView(withId(R.id.recyclerRecentPost)).atItem(1, click())
        //click overflow option menu recycler
        onView(withId(R.id.recyclerViewReview)).atItem(0,
            UtilsTest.clickChildViewWithId(R.id.textViewOptions)!!
        )
        inputReportReason("feedback")
        onView(withText("Post Detail"))
        //close
        activityScenario.close()
    }

    private fun inputReportReason(typeReport : String){
        onView(withText("Report")).perform(click())
        //click dialog reason
        onView(withText(R.string.select_a_reason)).perform(click())
        //click reason
        onView(withId(R.id.radioButton1)).perform(click())
        //click ok
        onView(withText("OK")).perform(click())
        //fill description
        onView(withId(R.id.editTextTextReportDetail)).perform(scrollTo(), typeText("Test report $typeReport"))
        //click submit
        onView(withId(R.id.buttonSubmitReport)).perform(click())
        //delay
        Thread.sleep(2000)
    }
}