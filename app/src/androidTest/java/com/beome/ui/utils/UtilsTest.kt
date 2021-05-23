package com.beome.ui.utils

import android.view.View
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.ViewInteraction
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isAssignableFrom
import org.hamcrest.CoreMatchers
import org.hamcrest.Matcher


object UtilsTest {
    // Buat ngeklik rv
    fun ViewInteraction.atItem(pos: Int, action: ViewAction) {
        perform(
            RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(
                pos, action
            )
        )
    }

    // Buat ngeklik child rv
    fun clickChildViewWithId(id: Int): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View>? {
                return null
            }

            override fun getDescription(): String {
                return "Click on a child view with specified id."
            }

            override fun perform(uiController: UiController, view: View) {
                val v = view.findViewById<View>(id)
                v.performClick()
            }
        }
    }


    fun getText(matcher: Matcher<View?>?): String? {
        val stringHolder = arrayOf<String?>(null)
        onView(matcher).perform(object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return isAssignableFrom(TextView::class.java)
            }

            override fun getDescription(): String {
                return "getting text from a TextView"
            }

            override fun perform(uiController: UiController?, view: View) {
                val tv = view as TextView
                stringHolder[0] = tv.text.toString()
            }
        })
        return stringHolder[0]
    }

    // Buat ngetik di searchView
    fun typeSearchViewText(text: String?): ViewAction? {
        return object : ViewAction {
            override fun getConstraints(): Matcher<View> {
                return CoreMatchers.allOf(
                    ViewMatchers.isDisplayed(),
                    isAssignableFrom(SearchView::class.java)
                )
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