package com.geaden.android.hackernewsreader.app.signin;

import android.support.annotation.NonNull;

import com.geaden.android.hackernewsreader.app.BasePresenter;
import com.geaden.android.hackernewsreader.app.BaseView;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;

/**
 * This specifies the contract between the view and the presenter.
 *
 * @author Gennady Denisov
 */
public interface SignInContract {

    interface View extends BaseView<Presenter> {
        void showSignInProgress(boolean active);

        void showCoverPhoto(@NonNull String coverPhotoUrl);

        void showPhoto(@NonNull String userPhotoUrl);

        void showDisplayName(@NonNull String displayName);

        void showEmail(String email);

        void hideEmail();

        void hideDisplayName();

        void hideCoverPhoto();

        void hidePhoto();

        void startSignInIntent();

        void updateMenuItems(boolean signedIn);
    }

    interface Presenter extends BasePresenter {

        void signIn();

        void signOut();

        void handleSignIn(GoogleSignInResult result);

        boolean isSignedIn();
    }
}
