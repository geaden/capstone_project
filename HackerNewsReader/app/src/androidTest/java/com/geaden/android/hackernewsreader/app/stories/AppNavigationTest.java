package com.geaden.android.hackernewsreader.app.stories;


import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v4.widget.DrawerLayout;
import android.test.suitebuilder.annotation.LargeTest;
import android.view.Gravity;

import com.geaden.android.hackernewsreader.app.R;
import com.geaden.android.hackernewsreader.app.StoriesApplication;
import com.geaden.android.hackernewsreader.app.data.StoriesDataSource;
import com.geaden.android.hackernewsreader.app.data.StoriesRepository;
import com.geaden.android.hackernewsreader.app.util.Utils;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.contrib.DrawerActions.open;
import static android.support.test.espresso.contrib.DrawerMatchers.isClosed;
import static android.support.test.espresso.contrib.DrawerMatchers.isOpen;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

/**
 * Tests for the {@link DrawerLayout} layout component in {@link StoriesActivity} which manages
 * navigation within the app.
 *
 * @author Gennady Denisov
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class AppNavigationTest {

    private StoriesRepository mRepository;
    /**
     * {@link ActivityTestRule} is a JUnit {@link Rule @Rule} to launch your activity under test.
     * <p/>
     * Rules are interceptors which are executed for each test method and are important building
     * blocks of Junit tests.
     */
    @Rule
    public ActivityTestRule<StoriesActivity> mStoriesActivityTestRule =
            new ActivityTestRule<StoriesActivity>(StoriesActivity.class) {

                /**
                 * To avoid a long list of stories and the need to scroll through the list to find a
                 * story, we call {@link StoriesDataSource#deleteAllStories()} before each test.
                 */
                @Override
                protected void beforeActivityLaunched() {
                    super.beforeActivityLaunched();
                    // Doing this in @Before generates a race condition.
                    mRepository = ((StoriesApplication) InstrumentationRegistry.getTargetContext()
                            .getApplicationContext()).getStoriesRepositoryComponent()
                            .getStoriesRepository();
                    mRepository.deleteAllStories();
                }
            };

    @Before
    public void setUp() {
        // Setup initial filter
        Utils.setFilter(InstrumentationRegistry.getTargetContext(), false);
    }

    @Test
    public void clickOnBookmarksOnlyNavigationItem_LoadsBookmarksOnly() {


        mRepository.bookmarkStory("1");

        // Open Drawer to click on navigation.
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(open()); // Open Drawer

        // Set bookmarks filter.
        onView(withId(R.id.bookmarks_navigation_menu_item))
                .perform(click());

        boolean filtered = Utils.getFilter(InstrumentationRegistry.getTargetContext());

        assertThat(filtered, is(true));
    }

    @Test
    public void clickOnAndroidHomeIcon_OpensNavigation() {
        // Check that left drawer is closed at startup
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))); // Left Drawer should be closed.

        // Open Drawer
        onView(withId(R.id.drawer_layout))
                .check(matches(isClosed(Gravity.LEFT))) // Left Drawer should be closed.
                .perform(open());

        // Check if drawer is open
        onView(withId(R.id.drawer_layout))
                .check(matches(isOpen(Gravity.LEFT))); // Left drawer is open open.
    }

}

