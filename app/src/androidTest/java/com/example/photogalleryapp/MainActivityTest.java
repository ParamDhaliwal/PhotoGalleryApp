package com.example.photogalleryapp;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class MainActivityTest {

    @Rule
    public IntentsTestRule<MainActivity> intentsRule = new IntentsTestRule<>(MainActivity.class);

    @Test
    public void testLaunchActivity() {
        onView(withId(R.id.btnLeft)).check(matches(withText("left")));
        onView(withId(R.id.btnRight)).check(matches(withText("right")));
        onView(withId(R.id.btnFilter)).check(matches(withText("filter")));
        onView(withId(R.id.btnCamera)).check(matches(withText("snap")));
    }

    @Test
    public void validateCameraScenario() {
        // Create a bitmap we can use for our simulated camera image
        Bitmap icon = BitmapFactory.decodeResource(
            InstrumentationRegistry.getInstrumentation().getTargetContext().getResources(),
            R.mipmap.ic_launcher);

        // Build a result to return from the Camera app
        Intent resultData = new Intent();
        resultData.putExtra("data", icon);
        Instrumentation.ActivityResult result = new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);

        // Stub out the Camera. When an intent is sent to the Camera,
        // this tells Espresso to respond with the ActivityResult we just created
        intending(toPackage("com.android.camera2")).respondWith(result);

        // Now that we have the stub in place, click on the button in our app that launches into the Camera
        //onView(withId(R.id.btnCamera)).perform(click());

        // We can also validate that an intent resolving to the "camera" activity has been sent out by our app
        //intended(toPackage("com.android.camera"));

        Intents.assertNoUnverifiedIntents();
    }

    @Test
    public void testSearchActivityLaunch() {
        // click on show search button
        onView(withId(R.id.btnFilter)).perform(click());
        // check searchActivity launch
        intended(hasComponent(SearchActivity.class.getName()));
    }
}
