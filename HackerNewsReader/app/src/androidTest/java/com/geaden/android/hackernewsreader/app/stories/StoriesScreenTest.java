package com.geaden.android.hackernewsreader.app.stories;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.geaden.android.hackernewsreader.app.R;
import com.geaden.android.hackernewsreader.app.StoriesApplication;
import com.geaden.android.hackernewsreader.app.data.StoriesDataSource;

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
 * Tests for the stories screen, the main screen which contains a list of all top stories.
 *
 * @author Gennady Denisov
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class StoriesScreenTest {

    private final static String TITLE1 = "foo";

    private final static String TITLE2 = "bar";

    /**
     * A custom {@link Matcher} which matches an item in a {@link RecyclerView} by its text.
     * <p/>
     * <p/>
     * View constraints:
     * <ul>
     * <li>View must be a child of a {@link RecyclerView}
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
                        isDescendantOfA(isAssignableFrom(RecyclerView.class)),
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
    public ActivityTestRule<StoriesActivity> mTasksActivityTestRule =
            new ActivityTestRule<StoriesActivity>(StoriesActivity.class) {

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
                }
            };

    @Test
    public void showAllStories() {
        // Check title
        CharSequence title = InstrumentationRegistry.getTargetContext()
                .getString(R.string.top_stories_toolbar_title);
        matchToolbarTitle(title);

        // Add 2 stories
        createStory(TITLE1);
        createStory(TITLE2);

        // Verify that all stories are shown
        viewAllStories();
        onView(withItemText(TITLE1)).check(matches(isDisplayed()));
        onView(withItemText(TITLE2)).check(matches(isDisplayed()));
    }

    private void viewAllStories() {
        // TODO: Display all stories...
    }

    /**
     * Helper method that creates two stories with provided title.
     *
     * @param title the title of the story.
     */
    private void createStory(String title) {
        // TODO: Add stories here...

    }

}