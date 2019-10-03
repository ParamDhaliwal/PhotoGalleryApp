package com.example.photogalleryapp;

import androidx.test.espresso.intent.rule.IntentsTestRule;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

public class SearchActivityTest {

    @Rule
    public IntentsTestRule<SearchActivity> intentsRule = new IntentsTestRule<>(SearchActivity.class);

    @Test
    public void testLaunchActivity() {
        onView(withId(R.id.search_toDate)).perform(typeText("31/01/18"), closeSoftKeyboard());
        onView(withId(R.id.search_fromDate)).perform(typeText("01/01/18"), closeSoftKeyboard());
        for (int i = 0; i <= 5; i++) {
            onView(withId(R.id.search_search)).perform(click(), closeSoftKeyboard());
        }
    }
}
