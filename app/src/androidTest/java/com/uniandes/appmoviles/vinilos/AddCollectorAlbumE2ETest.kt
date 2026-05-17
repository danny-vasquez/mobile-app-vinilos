package com.uniandes.appmoviles.vinilos

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.uniandes.appmoviles.vinilos.network.EspressoIdlingResource
import org.hamcrest.Matchers.not
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddCollectorAlbumE2ETest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)

        Thread.sleep(3000)

        onView(withId(R.id.recyclerAlbums))
            .check(matches(isDisplayed()))

        onView(withId(R.id.recyclerAlbums))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        onView(withId(R.id.btnAddToCollector))
            .perform(scrollTo(), click())
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun addCollectorAlbumFormIsDisplayed() {
        onView(withId(R.id.tilCollector))
            .check(matches(isDisplayed()))
        onView(withId(R.id.tilPrice))
            .check(matches(isDisplayed()))
        onView(withId(R.id.tilStatus))
            .check(matches(isDisplayed()))
        onView(withId(R.id.btnSave))
            .check(matches(isDisplayed()))
    }

    @Test
    fun addCollectorAlbumFlow() {
        onView(withId(R.id.actvCollector))
            .perform(replaceText("Manolo Bellon"), closeSoftKeyboard())

        onView(withId(R.id.etPrice))
            .perform(replaceText("25000"), closeSoftKeyboard())

        onView(withId(R.id.actvStatus))
            .perform(replaceText("Active"), closeSoftKeyboard())

        onView(withId(R.id.btnSave)).perform(click())

        onView(withText(R.string.msg_collector_album_added))
            .check(matches(isDisplayed()))
    }

    @Test
    fun addCollectorAlbumValidationNoCollector() {
        onView(withId(R.id.etPrice))
            .perform(replaceText("25000"), closeSoftKeyboard())

        onView(withId(R.id.actvStatus))
            .perform(replaceText("Active"), closeSoftKeyboard())

        onView(withId(R.id.btnSave)).perform(click())

        onView(withId(R.id.tilCollector))
            .check(matches(isDisplayed()))
    }

    @Test
    fun addCollectorAlbumValidationNoPrice() {
        onView(withId(R.id.actvCollector))
            .perform(replaceText("Manolo Bellon"), closeSoftKeyboard())

        onView(withId(R.id.actvStatus))
            .perform(replaceText("Active"), closeSoftKeyboard())

        onView(withId(R.id.btnSave)).perform(click())

        onView(withId(R.id.tilPrice))
            .check(matches(isDisplayed()))
    }

    @Test
    fun progressBarIsHiddenAfterCollectorsLoad() {
        onView(withId(R.id.progressIndicator))
            .check(matches(not(isDisplayed())))
    }
}
