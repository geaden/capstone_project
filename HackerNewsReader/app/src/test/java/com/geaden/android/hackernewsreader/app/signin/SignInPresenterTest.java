package com.geaden.android.hackernewsreader.app.signin;

import android.text.TextUtils;

import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for sign in presenter.
 *
 * @author Gennady Denisov
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(TextUtils.class)
public class SignInPresenterTest {

    @Mock
    SignInContract.View mView;

    @Mock
    GoogleSignInResult mResult;

    @Mock
    GoogleApiClient mGoogleApiClient;

    SignInPresenter mSignInPresenter;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(TextUtils.class);
        PowerMockito.when(TextUtils.isEmpty(anyString())).thenReturn(false);
        mSignInPresenter = new SignInPresenter(mGoogleApiClient, mView);
    }

    @Test
    public void handleSignIn() {
        mSignInPresenter.handleSignIn(mResult);
        verify(mView).hideEmail();
    }

}