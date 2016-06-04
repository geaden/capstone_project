package com.geaden.android.hackernewsreader.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.geaden.android.hackernewsreader.app.data.AppProfile;

/**
 * {}.
 *
 * @author Gennady Denisov
 */
public final class ProfileUtils {
    public static final String PREF_PROFILE_DISPLAY_NAME = "pref_profile_display_name";
    public static final String PREF_PROFILE_PHOTO_URL = "pref_profile_photo_url";
    public static final String PREF_PROFILE_COVER_URL = "pref_profile_cover_url";

    private ProfileUtils() {

    }

    /**
     * Gets application profile stored in {@link SharedPreferences}
     *
     * @param context the {@link Context} to get {@link PreferenceManager} from.
     * @return application profile.
     */
    public static AppProfile getProfile(Context context) {
        String emailAccount = Utils.getEmailAccount(context);
        if (emailAccount != null) {
            // Account has already been stored.
            // Get other account fields.
            String displayName = Utils.getStringFromPreference(context, PREF_PROFILE_DISPLAY_NAME);
            String photoUrl = Utils.getStringFromPreference(context, PREF_PROFILE_PHOTO_URL);
            String coverUrl = Utils.getStringFromPreference(context, PREF_PROFILE_COVER_URL);
            return new AppProfile(displayName, emailAccount, photoUrl, coverUrl);
        } else {
            // No account stored.
            return null;
        }
    }
}
