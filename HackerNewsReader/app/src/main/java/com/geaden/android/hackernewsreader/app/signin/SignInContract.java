package com.geaden.android.hackernewsreader.app.signin;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.geaden.android.hackernewsreader.app.BasePresenter;
import com.geaden.android.hackernewsreader.app.BaseView;
import com.geaden.android.hackernewsreader.app.data.AppProfile;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

/**
 * This specifies the contract between the view and the presenter.
 *
 * @author Gennady Denisov
 */
public interface SignInContract {

    interface View extends BaseView<Presenter> {
        /**
         * Shows user cover photo.
         *
         * @param coverPhotoUrl cover photo URL fro users's Google+ Account.
         */
        void showCoverPhoto(@NonNull String coverPhotoUrl);

        /**
         * Shows user photo.
         *
         * @param userPhotoUrl user's Google+ photo.
         */
        void showPhoto(@NonNull String userPhotoUrl);

        /**
         * Shows user's display name.
         *
         * @param displayName user's display name from Google+.
         */
        void showDisplayName(@NonNull String displayName);

        /**
         * User's email from selected Google+ account.
         *
         * @param email user's email.
         */
        void showEmail(String email);

        /**
         * Hides email to indicate user is not singed in.
         */
        void hideEmail();

        void hideDisplayName();

        void hideCoverPhoto();

        void hidePhoto();

        /**
         * Initiates starting of sign in intent.
         */
        void startSignInIntent();

        /**
         * Updates menu items accordingly.
         *
         * @param signedIn if user signed in.
         */
        void updateMenuItems(boolean signedIn);

        /**
         * Saves account profile to the preferences.
         *
         * @param appProfile {@link AppProfile} account profile.
         */
        void saveAccount(@Nullable AppProfile appProfile);

        /**
         * Loads bookmarks from backend.
         */
        void startLoadBookmarksTask();
    }

    interface Presenter extends BasePresenter {

        /**
         * Sign in user.
         */
        void signIn();

        /**
         * Sign out.
         */
        void signOut();

        /**
         * Loads profile.
         *
         * @param profile
         */
        void loadProfile(@Nullable AppProfile profile);

        /**
         * Handle sign in intent.
         *
         * @param result result of intent.
         */
        void handleSignIn(GoogleSignInResult result);
    }
}
