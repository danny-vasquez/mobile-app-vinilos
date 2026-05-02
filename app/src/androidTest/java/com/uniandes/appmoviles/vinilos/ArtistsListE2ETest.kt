package com.uniandes.appmoviles.vinilos

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.assertion.ViewAssertions.matches
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
class ArtistsListE2ETest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        navigateToArtists()
    }

    @After
    fun teardown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    private fun navigateToArtists() {
        activityRule.scenario.onActivity { activity ->
            val navController = androidx.navigation.Navigation.findNavController(
                activity, R.id.nav_host_fragment
            )
            navController.navigate(R.id.artistsFragment)
        }
    }

    @Test
    fun progressBarIsGoneAfterLoad() {
        onView(withId(R.id.progressBar))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun swipeRefreshLayoutIsPresent() {
        onView(withId(R.id.swipeRefresh))
            .check(matches(isDisplayed()))
    }

    @Test
    fun emptyViewIsShownWhenNoArtists() {
        onView(withId(R.id.progressBar))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.emptyView))
            .check(matches(isDisplayed()))
    }

    @Test
    fun recyclerIsHiddenWhenNoArtists() {
        onView(withId(R.id.progressBar))
            .check(matches(not(isDisplayed())))
        onView(withId(R.id.recyclerArtists))
            .check(matches(not(isDisplayed())))
    }
}
