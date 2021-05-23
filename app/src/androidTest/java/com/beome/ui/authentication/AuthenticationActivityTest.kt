package com.beome.ui.authentication

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.beome.R
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
        //delay
        Thread.sleep(2000)
        //register is succes if text Beome at MainActivity is show
        onView(withText("Beome")).check(matches(isDisplayed()))
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

}
