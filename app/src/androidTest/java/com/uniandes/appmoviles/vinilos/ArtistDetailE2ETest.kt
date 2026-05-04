package com.uniandes.appmoviles.vinilos

import androidx.navigation.Navigation
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
class ArtistDetailE2ETest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun navigateToFirstArtist() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        activityRule.scenario.onActivity { activity ->
            Navigation.findNavController(activity, R.id.nav_host_fragment)
                .navigate(R.id.artistsFragment)
        }
        onView(withId(R.id.recyclerArtists))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun artistDetailContentLayoutIsDisplayed() {
        onView(withId(R.id.contentLayout))
            .check(matches(isDisplayed()))
    }

    @Test
    fun artistNameIsDisplayedAndNotEmpty() {
        onView(withId(R.id.artistName))
            .check(matches(isDisplayed()))
            .check(matches(not(withText(""))))
    }

    @Test
    fun artistBirthDateIsDisplayed() {
        onView(withId(R.id.artistBirthDate))
            .check(matches(isDisplayed()))
    }

    @Test
    fun artistDescriptionIsDisplayed() {
        onView(withId(R.id.artistDescription))
            .check(matches(isDisplayed()))
    }

    @Test
    fun artistImageIsDisplayed() {
        onView(withId(R.id.artistImage))
            .check(matches(isDisplayed()))
    }

    @Test
    fun progressBarIsHiddenAfterLoading() {
        onView(withId(R.id.progressBar))
            .check(matches(not(isDisplayed())))
    }
}
