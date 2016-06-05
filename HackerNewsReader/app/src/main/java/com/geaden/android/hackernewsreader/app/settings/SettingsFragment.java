package com.geaden.android.hackernewsreader.app.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import com.geaden.android.hackernewsreader.app.R;
import com.geaden.android.hackernewsreader.app.util.Utils;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;

/**
 * Constructs about fragment.
 *
 * @author Gennady Denisov
 */
public class SettingsFragment extends PreferenceFragment implements
        GoogleApiClient.OnConnectionFailedListener, Preference.OnPreferenceChangeListener {

    private Preference mRevokePreference;
    private GoogleApiClient mGoogleApiClient;
    private SwitchPreference mNotifyPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.settings);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
        // options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage((AppCompatActivity) getActivity() /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END build_client

        mRevokePreference = findPreference(getString(R.string.pref_key_revoke));

        if (Utils.getEmailAccount(getActivity()) == null) {
            mRevokePreference.setEnabled(false);
        }

        mRevokePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                revokeAccess();
                return true;
            }
        });

        mNotifyPreference = (SwitchPreference) findPreference(getString(R.string.pref_key_notify));
        boolean ifNotify = Utils.checkNotify(getActivity());
        mNotifyPreference.setDefaultValue(ifNotify);
        mNotifyPreference.setChecked(ifNotify);
        updateNotify(ifNotify);
        mNotifyPreference.setOnPreferenceChangeListener(this);
    }

    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        mRevokePreference.setEnabled(false);
                    }
                });
        Utils.saveEmailAccount(getActivity(), null);
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference instanceof SwitchPreference) {
            boolean value = (Boolean) newValue;
            updateNotify(value);
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
            SharedPreferences.Editor spe = sp.edit();
            spe.putBoolean(preference.getKey(), value);
            spe.apply();
        }
        return true;
    }

    private void updateNotify(boolean ifNotify) {
        if (ifNotify) {
            mNotifyPreference.setSummary(getString(R.string.pref_notify_disable));
        } else {
            mNotifyPreference.setSummary(getString(R.string.pref_notify_enable));
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        // Do nothing...
    }
}
