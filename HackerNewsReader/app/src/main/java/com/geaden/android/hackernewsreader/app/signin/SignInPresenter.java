package com.geaden.android.hackernewsreader.app.signin;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.VisibleForTesting;

import com.geaden.android.hackernewsreader.app.data.AppProfile;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Represent actions listener for signing in.
 *
 * @author Gennady Denisov
 */
public class SignInPresenter implements SignInContract.Presenter {

    @NonNull
    private SignInContract.View mSignInView;

    @NonNull
    private GoogleApiClient mGoogleApiClient;

    private AppProfile mAppProfile;

    public SignInPresenter(@NonNull GoogleApiClient googleApiClient, @NonNull SignInContract.View signInView) {
        checkNotNull(googleApiClient, "googleApiClient cannot be null!");
        checkNotNull(signInView, "signInView cannot be null!");
        mGoogleApiClient = googleApiClient;
        mSignInView = signInView;
        mSignInView.setPresenter(this);
    }

    @Override
    public void signIn() {
        mSignInView.startSignInIntent();
    }

    @Override
    public void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        hideProfile();
                    }
                });
        mSignInView.updateMenuItems(false);
        mSignInView.saveAccount(null);
    }

    @Override
    public void handleSignIn(GoogleSignInResult result) {
        boolean signedIn;
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                Uri photoUrl = acct.getPhotoUrl();
                mAppProfile = new AppProfile(acct.getDisplayName(), acct.getEmail(), photoUrl != null ?
                        photoUrl.toString() : null);
            } else {
                mAppProfile = new AppProfile();
            }
            loadProfile(mAppProfile);
            mSignInView.saveAccount(mAppProfile.getEmail());
            signedIn = true;
        } else {
            // Signed out, show unauthenticated UI.
            mSignInView.saveAccount(null);
            hideProfile();
            signedIn = false;
        }
        mSignInView.updateMenuItems(signedIn);
    }

    @VisibleForTesting
    void loadProfile(@NonNull final AppProfile appProfile) {
        Plus.PeopleApi.load(mGoogleApiClient, "me")
                .setResultCallback(new ResultCallback<People.LoadPeopleResult>() {
                    @Override
                    public void onResult(@NonNull People.LoadPeopleResult loadPeopleResult) {
                        PersonBuffer personBuffer = loadPeopleResult.getPersonBuffer();
                        if (personBuffer != null && personBuffer.getCount() > 0) {
                            Person currentUser = personBuffer.get(0);
                            personBuffer.release();
                            Person.Cover cover = currentUser.getCover();
                            if (cover != null) {
                                Person.Cover.CoverPhoto coverPhoto = cover.getCoverPhoto();
                                if (coverPhoto != null) {
                                    appProfile.setCoverUrl(coverPhoto.getUrl());
                                }
                            }
                        }
                        mSignInView.startLoadBookmarksTask();
                        showProfile(appProfile);
                    }
                });
    }

    private void showProfile(AppProfile profile) {
        String displayName = profile.getDisplayName();
        String email = profile.getEmail();
        String photoUrl = profile.getPhotoUrl();
        String coverUrl = profile.getCoverUrl();

        // Handle display name.
        if (null != displayName) {
            mSignInView.showDisplayName(displayName);
        } else {
            mSignInView.hideDisplayName();
        }

        // Handle email.
        if (null != email) {
            mSignInView.showEmail(email);
        } else {
            mSignInView.hideEmail();
        }

        // Handle photo.
        if (null != photoUrl) {
            mSignInView.showPhoto(photoUrl);
        } else {
            mSignInView.hidePhoto();
        }

        // Handle cover.
        if (null != coverUrl) {
            mSignInView.showCoverPhoto(coverUrl);
        } else {
            mSignInView.hideCoverPhoto();
        }
    }

    private void hideProfile() {
        mSignInView.hideDisplayName();
        mSignInView.hideEmail();
        mSignInView.hidePhoto();
        mSignInView.hideCoverPhoto();
    }

    @Override
    public void start() {
        if (!mGoogleApiClient.isConnected()) {
            mGoogleApiClient.connect();
        }
    }
}
