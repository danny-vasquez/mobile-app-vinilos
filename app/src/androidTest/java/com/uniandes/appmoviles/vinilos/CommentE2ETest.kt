package com.uniandes.appmoviles.vinilos

import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.uniandes.appmoviles.vinilos.network.EspressoIdlingResource
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CommentE2ETest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setUp() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
        
        // Pausa de seguridad para mitigar la inestabilidad de inyección de eventos en API 35 (Android 15)
        Thread.sleep(3000)
        
        // Esperar a que la lista sea visible
        onView(withId(R.id.recyclerAlbums))
            .check(matches(isDisplayed()))

        // Navegar al detalle
        onView(withId(R.id.recyclerAlbums))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun testAddCommentFlow() {
        // 1. Scroll al botón y abrir diálogo
        onView(withId(R.id.btnAddComment))
            .perform(scrollTo(), click())

        // 2. Llenar formulario con replaceText (más estable que typeText en API 35)
        onView(withId(R.id.etName))
            .perform(replaceText("Test User"), closeSoftKeyboard())

        onView(withId(R.id.etTelephone))
            .perform(replaceText("123456789"), closeSoftKeyboard())

        onView(withId(R.id.etEmail))
            .perform(replaceText("test@example.com"), closeSoftKeyboard())

        onView(withId(R.id.etDescription))
            .perform(replaceText("Comentario de prueba estable para Android 15"), closeSoftKeyboard())

        // 3. Calificación
        onView(withId(R.id.ratingBar)).perform(click())

        // 4. Guardar
        onView(withText(R.string.btn_save)).perform(click())

        // 5. Verificar éxito
        onView(withText(R.string.msg_comment_added))
            .check(matches(isDisplayed()))
    }

    @Test
    fun testAddCommentValidation() {
        onView(withId(R.id.btnAddComment))
            .perform(scrollTo(), click())

        onView(withText(R.string.btn_save)).perform(click())

        onView(withText(R.string.error_empty_fields))
            .check(matches(isDisplayed()))
    }
}
