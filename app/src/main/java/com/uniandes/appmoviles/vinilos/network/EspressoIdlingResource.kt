package com.uniandes.appmoviles.vinilos.network

import androidx.test.espresso.idling.CountingIdlingResource

object EspressoIdlingResource {
    private const val RESOURCE_NAME = "NETWORK"

    @JvmField
    val countingIdlingResource = CountingIdlingResource(RESOURCE_NAME)

    fun increment() {
        countingIdlingResource.increment()
    }

    fun decrement() {
        if (!countingIdlingResource.isIdleNow) {
            countingIdlingResource.decrement()
        }
    }
}
