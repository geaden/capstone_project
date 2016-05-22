package com.geaden.android.hackernewsreader.app.storydetail;

import android.content.Intent;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.ImageView;

import com.geaden.android.hackernewsreader.app.R;
import com.geaden.android.hackernewsreader.app.data.local.BookmarkModel;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
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

    @Before
    public void setUp() {
        // Clean up bookmarks...
        SQLite.delete().from(BookmarkModel.class).query();
    }

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

    /**
     * A custom {@link Matcher} which matches an item with certain drawable.
     *
     * @param resourceId the resource id to match
     * @return Matcher that matches resource id in the given image view
     */
    public static Matcher<View> withDrawableId(final int resourceId) {
        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View target) {
                if (!(target instanceof ImageView)) {
                    return false;
                }
                ImageView imageView = (ImageView) target;
                if (resourceId < 0) {
                    return imageView.getDrawable() == null;
                }

                Integer resourceId = (Integer) imageView.getTag();

                return resourceId.equals(resourceId);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("with drawable from resource id: ");
                description.appendValue(resourceId);
            }
        };
    }

    @Test
    public void storyDetails_ableToBookmarkAStory() throws Exception {
        startActivityWithWithStubbedStory();

        onView(withId(R.id.story_bookmark_action)).check(matches(
                withDrawableId(R.drawable.ic_bookmark_border_white_24px)));

        onView(withId(R.id.story_bookmark_action)).perform(click());

        onView(withId(R.id.story_bookmark_action)).check(matches(
                withDrawableId(R.drawable.ic_bookmark_white_24px)));

    }
}
