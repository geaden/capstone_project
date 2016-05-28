package com.geaden.android.hackernewsreader.app.stories;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.geaden.android.hackernewsreader.app.R;
import com.geaden.android.hackernewsreader.app.StoriesApplication;
import com.geaden.android.hackernewsreader.app.data.StoriesLoader;
import com.geaden.android.hackernewsreader.app.data.StoriesRepository;
import com.geaden.android.hackernewsreader.app.signin.SignInContract;
import com.geaden.android.hackernewsreader.app.signin.SignInPresenter;
import com.geaden.android.hackernewsreader.app.util.ActivityUtils;
import com.geaden.android.hackernewsreader.app.util.Utils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.Plus;

import butterknife.Bind;
import butterknife.ButterKnife;

public class StoriesActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, SignInContract.View {

    private static final String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";
    private static final int RC_SIGN_IN = 0;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @Bind(R.id.nav_view)
    NavigationView mNavView;

    private ActionBarDrawerToggle mDrawerToggle;

    SignInContract.Presenter mSignInPresenter;

    StoriesPresenter mStoriesPresenter;

    StoriesRepository mStoriesRepository;

    private boolean mCurrentlyFiltered = false;
    private GoogleApiClient mGoogleApiClient;
    private NavigationViewHolder mNavViewHolder;

    private ProgressDialog mProgressDialog;
    private Menu mMenu;

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

            mNavViewHolder = new NavigationViewHolder(mNavView.getHeaderView(0));

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

        buildGoogleApiClient();

        mSignInPresenter = new SignInPresenter(mGoogleApiClient, this);

        // Load previously saved state, if available.
        if (savedInstanceState != null) {
            StoriesFilterType currentFiltering =
                    (StoriesFilterType) savedInstanceState.getSerializable(CURRENT_FILTERING_KEY);
            mStoriesPresenter.setFiltering(currentFiltering);
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
        if (opr.isDone()) {
            // If the user's cached credentials are valid, the OptionalPendingResult will be "done"
            // and the GoogleSignInResult will be available instantly.
            GoogleSignInResult result = opr.get();
            mSignInPresenter.handleSignIn(result);
        } else {
            // If the user has not previously signed in on this device or the sign-in has expired,
            // this asynchronous branch will attempt to sign in the user silently.  Cross-device
            // single sign-on will occur in this branch.
            showSignInProgress(true);
            opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                @Override
                public void onResult(GoogleSignInResult googleSignInResult) {
                    showSignInProgress(false);
                    mSignInPresenter.handleSignIn(googleSignInResult);
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        mMenu = menu;
        getMenuInflater().inflate(R.menu.stories_menu, menu);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from
        // GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            mSignInPresenter.handleSignIn(result);
        }
    }

    /**
     * Builds API client with required Google API's enabled.
     */
    private void buildGoogleApiClient() {
        // Configure sign-in to request the user's ID, email address, and basic profile. ID and
        // basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestScopes(Plus.SCOPE_PLUS_PROFILE, Plus.SCOPE_PLUS_LOGIN)
                .build();

        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addConnectionCallbacks(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSignInPresenter.start();
    }

    @Override
    public void updateMenuItems(boolean signedIn) {
        if (mMenu != null) {
            MenuItem signInItem = mMenu.findItem(R.id.item_sign_in);
            signInItem.setTitle(signedIn ? R.string.signOut : R.string.signIn);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (!connectionResult.hasResolution()) {
//            mGoogleApiClient.getErrorDialog(this, result.getErrorCode(),request_code).show();
            return;
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
        switch (item.getItemId()) {
            case R.id.item_sign_in:
                if (mSignInPresenter.isSignedIn()) {
                    mSignInPresenter.signOut();
                } else {
                    mSignInPresenter.signIn();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    @Override
    public void startSignInIntent() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void showSignInProgress(boolean active) {
        if (active) {
            if (mProgressDialog == null) {
                mProgressDialog = new ProgressDialog(this);
                mProgressDialog.setMessage(getString(R.string.loading));
                mProgressDialog.setIndeterminate(true);
            }
            mProgressDialog.show();
        } else {
            if (mProgressDialog != null && mProgressDialog.isShowing()) {
                mProgressDialog.hide();
            }
        }
    }

    @Override
    public void showCoverPhoto(String coverPhotoUrl) {
        mNavViewHolder.profileCoverPlaceholder.setVisibility(View.GONE);
        mNavViewHolder.profileCoverPhoto.setVisibility(View.VISIBLE);
        Glide.with(this)
                .load(coverPhotoUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mNavViewHolder.profileCoverPhoto);

    }

    @Override
    public void showPhoto(String userPhotoUrl) {
        Glide.with(this)
                .load(userPhotoUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mNavViewHolder.profilePhoto);
    }

    @Override
    public void showDisplayName(String displayName) {
        mNavViewHolder.profileName.setText(displayName);
    }

    @Override
    public void showEmail(String email) {
        mNavViewHolder.profileEmail.setText(email);
    }

    @Override
    public void setPresenter(SignInContract.Presenter presenter) {
        mSignInPresenter = presenter;
    }

    @Override
    public void hideEmail() {
        mNavViewHolder.profileEmail.setText("");
    }

    @Override
    public void hideDisplayName() {
        mNavViewHolder.profileName.setText(getString(R.string.navigation_view_header_title));
    }

    @Override
    public void hideCoverPhoto() {
        mNavViewHolder.profileCoverPhoto.setVisibility(View.GONE);
        mNavViewHolder.profileCoverPlaceholder.setVisibility(View.VISIBLE);
    }

    @Override
    public void hidePhoto() {
        mNavViewHolder.profilePhoto.setImageDrawable(ContextCompat.getDrawable(this,
                R.drawable.person_image_empty));
    }

    public static class NavigationViewHolder {

        TextView profileName;

        TextView profileEmail;

        ImageView profilePhoto;

        ImageView profileCoverPlaceholder;

        ImageView profileCoverPhoto;

        public NavigationViewHolder(View view) {
            ButterKnife.bind(view);
            profileName = (TextView) view.findViewById(R.id.profile_name_text);
            profileEmail = (TextView) view.findViewById(R.id.profile_email_text);
            profilePhoto = (ImageView) view.findViewById(R.id.profile_photo);
            profileCoverPlaceholder = (ImageView) view.findViewById(R.id.profile_cover_image_placeholder);
            profileCoverPhoto = (ImageView) view.findViewById(R.id.profile_cover_image);
        }
    }
}
