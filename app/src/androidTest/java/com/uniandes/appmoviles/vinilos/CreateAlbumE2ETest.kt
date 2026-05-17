package com.uniandes.appmoviles.vinilos

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.closeSoftKeyboard
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isPlatformPopup
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.uniandes.appmoviles.vinilos.network.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateAlbumE2ETest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun navigateToCreateAlbum() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        Thread.sleep(3000)
        onView(withId(R.id.recyclerAlbums))
            .check(matches(isDisplayed()))
        onView(withId(R.id.fab)).perform(click())
    }

    @After
    fun unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun createAlbumFormIsDisplayed() {
        onView(withId(R.id.tilName)).check(matches(isDisplayed()))
        onView(withId(R.id.tilCover)).check(matches(isDisplayed()))
        onView(withId(R.id.tilReleaseDate)).check(matches(isDisplayed()))
        onView(withId(R.id.tilGenre)).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.tilRecordLabel)).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.tilDescription)).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withId(R.id.btnSave)).perform(scrollTo()).check(matches(isDisplayed()))
    }

    @Test
    fun createAlbumFlow() {
        onView(withId(R.id.etName))
            .perform(replaceText("Buscando América"), closeSoftKeyboard())
        onView(withId(R.id.etCover))
            .perform(replaceText("https://i.pinimg.com/564x/aa/5f/ed/aa5fed7fac61c2f3b5b1b0f679fc7508.jpg"), closeSoftKeyboard())
        onView(withId(R.id.etReleaseDate)).perform(click())
        onView(withId(com.google.android.material.R.id.confirm_button)).perform(click())
        onView(withId(R.id.actvGenre)).perform(scrollTo(), click())
        onView(withText("Salsa")).inRoot(isPlatformPopup()).perform(click())
        onView(withId(R.id.actvRecordLabel)).perform(scrollTo(), click())
        onView(withText("Elektra")).inRoot(isPlatformPopup()).perform(click())
        onView(withId(R.id.etDescription))
            .perform(scrollTo(), replaceText("Álbum de prueba creado desde tests E2E"), closeSoftKeyboard())
        onView(withId(R.id.btnSave)).perform(scrollTo(), click())
        Thread.sleep(3000)
        onView(withId(R.id.recyclerAlbums)).check(matches(isDisplayed()))
    }

    @Test
    fun createAlbumValidationEmptyName() {
        onView(withId(R.id.btnSave)).perform(scrollTo(), click())
        onView(withId(R.id.tilName)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSave)).perform(scrollTo()).check(matches(isDisplayed()))
    }

    @Test
    fun createAlbumValidationInvalidCoverUrl() {
        onView(withId(R.id.etName))
            .perform(replaceText("Test Album"), closeSoftKeyboard())
        onView(withId(R.id.etCover))
            .perform(replaceText("no-es-una-url"), closeSoftKeyboard())
        onView(withId(R.id.btnSave)).perform(scrollTo(), click())
        onView(withId(R.id.tilCover)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSave)).perform(scrollTo()).check(matches(isDisplayed()))
    }

    @Test
    fun createAlbumValidationEmptyDate() {
        onView(withId(R.id.etName))
            .perform(replaceText("Test Album"), closeSoftKeyboard())
        onView(withId(R.id.etCover))
            .perform(replaceText("https://example.com/cover.jpg"), closeSoftKeyboard())
        onView(withId(R.id.btnSave)).perform(scrollTo(), click())
        onView(withId(R.id.tilReleaseDate)).check(matches(isDisplayed()))
        onView(withId(R.id.btnSave)).perform(scrollTo()).check(matches(isDisplayed()))
    }
}
