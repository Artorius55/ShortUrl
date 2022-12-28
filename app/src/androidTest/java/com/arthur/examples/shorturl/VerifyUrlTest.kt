package com.arthur.examples.shorturl

import android.content.Intent
import androidx.annotation.StringRes
import androidx.test.espresso.Espresso.*
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.*
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.ActivityTestRule
import com.arthur.examples.shorturl.ui.MainActivity
import dagger.hilt.android.testing.HiltAndroidTest
import org.hamcrest.Matchers.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*

@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
@LargeTest
class VerifyUrlTest {
    private lateinit var wellFormattedUrl: String
    private lateinit var wrongFormattedUrl: String

    @get:Rule
    var activityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Before
    fun init() {
        wellFormattedUrl = "http://www.google.com.mx"
        wrongFormattedUrl = "this is not a URL"
    }

    @Test
    fun showErrorMessage_invalidUrl() {
        // Type an invalid URL in the EditText
        onView(withId(R.id.et_url_input)).perform(typeText(wrongFormattedUrl))
        // Click the button to submit the URL
        onView(withId(R.id.btn_action)).perform(click())
        // Check that the error message is displayed
        val errorMessage: String = getResourceString(R.string.bad_url_error_message)
        onView(withId(R.id.til_url)).check(
            matches(
                hasDescendant(
                    withText(errorMessage)
                )
            )
        )
    }

    @Test
    fun recyclerviewItems_validUrl() {
        activityRule.launchActivity(Intent())
        // Type an invalid URL in the EditText
        onView(withId(R.id.et_url_input)).perform(typeText(wellFormattedUrl))
        // Click the button to submit the URL
        onView(withId(R.id.btn_action)).perform(click())

        Thread.sleep(2000)

        onView(withId(R.id.rv_shortened_urls)).check(RecyclerViewItemCountAssertion(greaterThan(0)))
    }


    fun getResourceString(@StringRes id: Int): String {
        val resources = InstrumentationRegistry.getInstrumentation().targetContext.resources
        return resources.getString(id)
    }
}