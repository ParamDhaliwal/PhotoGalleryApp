package com.example.photogalleryapp;

import androidx.test.espresso.intent.rule.IntentsTestRule;

import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withHint;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class SearchActivityTest {

    @Rule
    public IntentsTestRule<SearchActivity> intentsRule = new IntentsTestRule<>(SearchActivity.class);

    @Test
    public void testLaunchActivity() {
        onView(withId(R.id.title_search)).check(matches(withText("PhotoGalleryApp")));
        onView(withId(R.id.search_fromDate)).check(matches(withHint("dd/mm/yyyy")));
        onView(withId(R.id.search_toDate)).check(matches(withHint("dd/mm/yyyy")));
        onView(withId(R.id.search_fromDateLabel)).check(matches(withText("From:")));
        onView(withId(R.id.search_toDateLabel)).check(matches(withText("To:")));
        onView(withId(R.id.search_cancel)).check(matches(withText("Cancel")));
        onView(withId(R.id.search_search)).check(matches(withText("Search")));
    }

    @Test
    public void testKeyboardTyping() {
        onView(withId(R.id.search_fromDate)).perform(typeText("01/01/18")).check(matches(withText("01/01/18")));
        onView(withId(R.id.search_toDate)).perform(typeText("31/01/18")).check(matches(withText("31/01/18")));
    }

    @Test
    public void testDateBasedSearch() {
        onView(withId(R.id.search_fromDate)).perform(typeText("01/01/18"), closeSoftKeyboard());
        onView(withId(R.id.search_toDate)).perform(typeText("31/01/18"), closeSoftKeyboard());
        for (int i = 0; i <= 5; i++) {
            onView(withId(R.id.search_search)).perform(click(), closeSoftKeyboard());
        }
    }
}
