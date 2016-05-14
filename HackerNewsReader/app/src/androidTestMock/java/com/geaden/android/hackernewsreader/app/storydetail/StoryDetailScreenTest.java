package com.geaden.android.hackernewsreader.app.storydetail;

import android.content.Intent;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.geaden.android.hackernewsreader.app.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

/**
 * Tests for the tasks screen, the main screen which contains a list of all tasks..
 *
 * @author Gennady Denisov
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class StoryDetailScreenTest {

    private static final String STORY_TITLE = "foo";
    private static final String STORY_CONTENT = "lorem ipsum";

    @Rule
    public ActivityTestRule<StoryDetailActivity> mTaskDetailActivityTestRule =
            new ActivityTestRule<>(StoryDetailActivity.class, true /* Initial touch mode  */,
                    false /* Lazily launch activity */);

    /**
     * Setup your test fixture with a mock story id. The {@link StoryDetailActivity} is started with
     * a particular story id, which is then loaded from the service API.
     */
    private void startActivityWithWithStubbedStory() {
        // Lazily start the Activity from the ActivityTestRule this time to inject the start Intent
        Intent startIntent = new Intent();
        startIntent.putExtra(StoryDetailActivity.EXTRA_STORY_ID, "1");
        mTaskDetailActivityTestRule.launchActivity(startIntent);
    }


    @Test
    public void storyDetails_DisplayedInUi() throws Exception {
        startActivityWithWithStubbedStory();

        // Check that the task title and description are displayed
        onView(withId(R.id.story_title)).check(matches(withText(STORY_TITLE)));
        onView(withId(R.id.story_content)).check(matches(withText(STORY_CONTENT)));
    }
}
