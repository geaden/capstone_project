package com.geaden.android.hackernewsreader.app.comments;

import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.geaden.android.hackernewsreader.app.StoriesApplication;
import com.geaden.android.hackernewsreader.app.data.StoriesDataSource;
import com.geaden.android.hackernewsreader.app.data.local.CommentModel;
import com.geaden.android.hackernewsreader.app.storydetail.StoryDetailActivity;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static com.google.common.base.Preconditions.checkArgument;
import static org.hamcrest.Matchers.allOf;

/**
 * Test for comments list screen.
 *
 * @author Gennady Denisov
 */
@LargeTest
@RunWith(AndroidJUnit4.class)
public class CommentsScreentTest {

    /**
     * A custom {@link Matcher} which matches an item in a {@link ListView} by its text.
     * <p/>
     * <p/>
     * View constraints:
     * <ul>
     * <li>View must be a child of a {@link ListView}
     * <ul>
     *
     * @param itemText the text to match
     * @return Matcher that matches text in the given view
     */
    public static Matcher<View> withItemText(final String itemText) {
        checkArgument(!TextUtils.isEmpty(itemText), "itemText cannot be null or empty");
        return new TypeSafeMatcher<View>() {
            @Override
            public boolean matchesSafely(View item) {
                return allOf(
                        isDescendantOfA(isAssignableFrom(ListView.class)),
                        withText(itemText)).matches(item);
            }

            @Override
            public void describeTo(Description description) {
                description.appendText("is isDescendantOfA RV with text " + itemText);
            }
        };
    }

    /**
     * Matches toolbar title.
     *
     * @param title required title.
     * @return if toolbar title matches.
     */
    public static ViewInteraction matchToolbarTitle(CharSequence title) {
        return onView(allOf(isAssignableFrom(TextView.class),
                withParent(isAssignableFrom(Toolbar.class)))).check(matches(withText(title.toString())));
    }

    /**
     * {@link ActivityTestRule} is a JUnit {@link Rule @Rule} to launch your activity under test.
     * <p/>
     * Rules are interceptors which are executed for each test method and are important building
     * blocks of Junit tests.
     */
    @Rule
    public ActivityTestRule<CommentsActivity> mCommentsActivityTestRule =
            new ActivityTestRule<CommentsActivity>(CommentsActivity.class, true, false) {

                /**
                 * To avoid a long list of stories and the need to scroll through the list to find a
                 * story, we call {@link StoriesDataSource#deleteAllStories()} before each test.
                 */
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    // Doing this in @Before generates a race condition.
                    ((StoriesApplication) InstrumentationRegistry.getTargetContext()
                            .getApplicationContext()).getStoriesRepositoryComponent()
                            .getStoriesRepository().deleteAllStories();
                    SQLite.delete().from(CommentModel.class).query();
                }
            };

    /**
     * Setup your test fixture with a mock story id. The {@link StoryDetailActivity} is started with
     * a particular story id, which is then loaded from the service API.
     */
    private void startActivityWithWithStubbedStory() {
        // Lazily start the Activity from the ActivityTestRule this time to inject the start Intent
        Intent startIntent = new Intent();
        startIntent.putExtra(CommentsActivity.EXTRA_STORY_ID, "1");
        startIntent.putExtra(CommentsActivity.EXTRA_STORY_TITLE, "foo");
        mCommentsActivityTestRule.launchActivity(startIntent);
    }

    @Test
    public void showAllComments() {

        startActivityWithWithStubbedStory();

        // Check title
        CharSequence title = "foo";
        matchToolbarTitle(title);

        onView(withItemText("lorem ipsum 1")).check(matches(isDisplayed()));
        onView(withItemText("lorem ipsum 2")).check(matches(isDisplayed()));
        onView(withItemText("lorem ipsum 3")).check(matches(isDisplayed()));
    }
}
