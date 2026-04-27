package com.uniandes.appmoviles.vinilos

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
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
class AlbumDetailE2ETest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun navigateToFirstAlbum() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        onView(withId(R.id.recyclerAlbums))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun albumDetailScrollViewIsDisplayed() {
        onView(withId(R.id.contentLayout))
            .check(matches(isDisplayed()))
    }

    @Test
    fun albumNameIsDisplayedAndNotEmpty() {
        onView(withId(R.id.albumName))
            .check(matches(isDisplayed()))
            .check(matches(not(withText(""))))
    }

    @Test
    fun albumGenreIsDisplayed() {
        onView(withId(R.id.albumGenre))
            .check(matches(isDisplayed()))
    }

    @Test
    fun albumReleaseDateIsDisplayed() {
        onView(withId(R.id.albumReleaseDate))
            .check(matches(isDisplayed()))
    }

    @Test
    fun albumRecordLabelIsDisplayed() {
        onView(withId(R.id.albumRecordLabel))
            .check(matches(isDisplayed()))
    }

    @Test
    fun albumDescriptionIsDisplayed() {
        onView(withId(R.id.albumDescription))
            .check(matches(isDisplayed()))
    }

    @Test
    fun albumCoverIsDisplayed() {
        onView(withId(R.id.albumCover))
            .check(matches(isDisplayed()))
    }

    @Test
    fun progressBarIsHiddenAfterLoading() {
        onView(withId(R.id.progressBar))
            .check(matches(not(isDisplayed())))
    }
}
