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
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.uniandes.appmoviles.vinilos.network.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AddTrackE2ETest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun navigateToAddTrack() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        Thread.sleep(3000)
        onView(withId(R.id.recyclerAlbums))
            .check(matches(isDisplayed()))
        onView(withId(R.id.recyclerAlbums))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        onView(withId(R.id.btnAddTrack))
            .perform(scrollTo(), click())
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun addTrackFormIsDisplayed() {
        onView(withId(R.id.tilName)).check(matches(isDisplayed()))
        onView(withId(R.id.tilDuration)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSave)).check(matches(isDisplayed()))
    }

    @Test
    fun addTrackFlow() {
        onView(withId(R.id.etName))
            .perform(replaceText("Pinta el Mundo de Esperanza"), closeSoftKeyboard())
        onView(withId(R.id.etDuration))
            .perform(replaceText("4:15"), closeSoftKeyboard())
        onView(withId(R.id.btnSave)).perform(click())
        Thread.sleep(3000)
        onView(withId(R.id.albumName)).check(matches(isDisplayed()))
    }

    @Test
    fun addTrackValidationEmptyName() {
        onView(withId(R.id.btnSave)).perform(click())
        onView(withId(R.id.tilName)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSave)).check(matches(isDisplayed()))
    }

    @Test
    fun addTrackValidationInvalidDuration() {
        onView(withId(R.id.etName))
            .perform(replaceText("Test Track"), closeSoftKeyboard())
        onView(withId(R.id.etDuration))
            .perform(replaceText("duracion-invalida"), closeSoftKeyboard())
        onView(withId(R.id.btnSave)).perform(click())
        onView(withId(R.id.tilDuration)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSave)).check(matches(isDisplayed()))
    }
}
