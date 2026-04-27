package com.uniandes.appmoviles.vinilos

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasMinimumChildCount
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
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
class AlbumsListE2ETest {

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
    fun albumsListIsDisplayed() {
        onView(withId(R.id.recyclerAlbums))
            .check(matches(isDisplayed()))
    }

    @Test
    fun albumsAreLoadedSuccessfully() {
        onView(withId(R.id.recyclerAlbums))
            .check(matches(hasMinimumChildCount(1)))
    }

    @Test
    fun progressBarIsHiddenAfterLoading() {
        onView(withId(R.id.recyclerAlbums))
            .check(matches(hasMinimumChildCount(1)))
        onView(withId(R.id.progressBar))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun emptyViewIsHiddenWhenAlbumsExist() {
        onView(withId(R.id.recyclerAlbums))
            .check(matches(hasMinimumChildCount(1)))
        onView(withId(R.id.emptyView))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun clickingFirstAlbumNavigatesToDetail() {
        onView(withId(R.id.recyclerAlbums))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        onView(withId(R.id.albumName))
            .check(matches(isDisplayed()))
    }
}
