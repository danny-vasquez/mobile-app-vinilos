package com.uniandes.appmoviles.vinilos

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.Espresso.pressBack
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
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
class NavigationE2ETest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun registerIdlingResource() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun albumListIsFirstScreen() {
        onView(withId(R.id.recyclerAlbums))
            .check(matches(isDisplayed()))
    }

    @Test
    fun navigateFromListToDetailAndBack() {
        onView(withId(R.id.recyclerAlbums))
            .check(matches(isDisplayed()))

        onView(withId(R.id.recyclerAlbums))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))

        onView(withId(R.id.contentLayout))
            .check(matches(isDisplayed()))

        pressBack()

        onView(withId(R.id.recyclerAlbums))
            .check(matches(isDisplayed()))
    }

    @Test
    fun navigateToSecondAlbumDetail() {
        onView(withId(R.id.recyclerAlbums))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(1, click()))

        onView(withId(R.id.albumName))
            .check(matches(isDisplayed()))
    }
}
