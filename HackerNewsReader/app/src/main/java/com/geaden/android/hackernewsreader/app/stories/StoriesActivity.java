package com.geaden.android.hackernewsreader.app.stories;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.CompoundButton;

import com.geaden.android.hackernewsreader.app.R;
import com.geaden.android.hackernewsreader.app.StoriesApplication;
import com.geaden.android.hackernewsreader.app.data.StoriesLoader;
import com.geaden.android.hackernewsreader.app.data.StoriesRepository;
import com.geaden.android.hackernewsreader.app.util.ActivityUtils;
import com.geaden.android.hackernewsreader.app.util.Utils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StoriesActivity extends AppCompatActivity {

    private static final String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.nav_view)
    NavigationView mNavView;

    private ActionBarDrawerToggle mDrawerToggle;

    StoriesPresenter mStoriesPresenter;

    StoriesRepository mStoriesRepository;

    private boolean mCurrentlyFiltered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.stories_act);
        ButterKnife.bind(this);

        mStoriesRepository = ((StoriesApplication) getApplication()).getStoriesRepositoryComponent()
                .getStoriesRepository();

        // Set up the toolbar
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.top_stories_toolbar_title);
        ab.setDisplayHomeAsUpEnabled(true);

        // Set up the navigation drawer.
        mDrawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);

        mDrawerToggle = setupDrawerToggle();

        if (mNavView != null) {
            setupDrawerContent(mNavView);

            final MenuItem menuItem = mNavView.getMenu().getItem(0);
            SwitchCompat actionView = (SwitchCompat) MenuItemCompat.getActionView(menuItem);

            mCurrentlyFiltered = Utils.getFilter(this);
            actionView.setChecked(mCurrentlyFiltered);
            menuItem.setChecked(mCurrentlyFiltered);

            actionView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    setFilter(menuItem, isChecked);
                }
            });
        }

        StoriesFragment storiesFragment =
                (StoriesFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (storiesFragment == null) {
            // Create the fragment
            storiesFragment = StoriesFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), storiesFragment, R.id.contentFrame);
        }

        // Create the presenter
        StoriesLoader storiesLoader = new StoriesLoader(getApplicationContext(), mStoriesRepository);

        mStoriesPresenter = new StoriesPresenter(
                storiesLoader,
                getSupportLoaderManager(),
                mStoriesRepository,
                storiesFragment
        );

        // Load previously saved state, if available.
        if (savedInstanceState != null) {
            StoriesFilterType currentFiltering =
                    (StoriesFilterType) savedInstanceState.getSerializable(CURRENT_FILTERING_KEY);
            mStoriesPresenter.setFiltering(currentFiltering);
        }
    }

    // Sets up drawer toggle.
    private ActionBarDrawerToggle setupDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,
                R.string.drawer_close);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(CURRENT_FILTERING_KEY, mStoriesPresenter.getFiltering());
        super.onSaveInstanceState(outState);
    }

    // `onPostCreate` called when activity start-up is complete after `onStart()`
    // NOTE! Make sure to override the method with only a single `Bundle` argument
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sets up the drawer content.
     *
     * @param navigationView the navigation view.
     */
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(final MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.settings_navigation_menu_item:
                                // Close the navigation drawer when an item is selected.
                                mDrawerLayout.closeDrawers();
                                break;
                            case R.id.bookmarks_navigation_menu_item:
                                SwitchCompat actionView = (SwitchCompat) MenuItemCompat.getActionView(menuItem);
                                final boolean filterd = Utils.getFilter(StoriesActivity.this);
                                actionView.setChecked(!filterd);
                                setFilter(menuItem, !filterd);
                                break;
                            default:
                                // Close the navigation drawer when an item is selected.
                                mDrawerLayout.closeDrawers();
                                break;
                        }
                        return true;
                    }
                });
    }

    private void setFilter(MenuItem menuItem, boolean filtered) {
        Utils.setFilter(StoriesActivity.this, filtered);
        menuItem.setChecked(filtered);
    }
}
