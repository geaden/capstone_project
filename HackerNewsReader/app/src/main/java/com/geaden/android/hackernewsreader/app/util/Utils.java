package com.geaden.android.hackernewsreader.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;

import com.geaden.android.hackernewsreader.app.R;
import com.geaden.android.hackernewsreader.app.data.local.BookmarkModel;
import com.geaden.android.hackernewsreader.app.data.local.BookmarkModel_Table;
import com.google.api.client.util.Lists;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

/**
 * Holder for utility methods.
 *
 * @author Gennady Denisov
 */
public final class Utils {

    public final static String PREFS_KEY_EMAIL = "email_account";

    private Utils() {

    }

    /**
     * Gets relative time used in app.
     *
     * @param now  current time.
     * @param time time to get relative from.
     * @return relative time.
     */
    public static CharSequence getRelativeTime(long now, long time) {
        CharSequence ago = DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS,
                DateUtils.FORMAT_ABBREV_ALL);
        return ago;
    }

    /**
     * Helper method to find if story bookmarked or not.
     *
     * @param storyId           the story id.
     * @param bookmarkedStories list of bookmarked stories ids.
     * @return if story bookmarked.
     */
    public static boolean checkIfBookmarked(long storyId, List<Long> bookmarkedStories) {
        if (bookmarkedStories == null) {
            bookmarkedStories = Lists.newArrayList();
            Cursor cursor = SQLite.select(BookmarkModel_Table.story_id).from(BookmarkModel.class)
                    .query();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    bookmarkedStories.add(cursor.getLong(0));
                }
                cursor.close();
            }
        }
        return bookmarkedStories.contains(storyId);
    }

    /**
     * Sets bookmarked only filter to true.
     *
     * @param context  the Context to get PreferenceManger from.
     * @param filtered if currently filtered or not.
     */
    public static void setFilter(Context context, boolean filtered) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor spe = sp.edit();
        spe.putBoolean(context.getString(R.string.pref_key_bookmarked_ony), filtered);
        spe.apply();
    }

    /**
     * Gets bookmarked only filter.
     *
     * @param context the Context to get PreferenceManger from.
     * @return if currently filter is active.
     */
    public static boolean getFilter(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getBoolean(context.getString(R.string.pref_key_bookmarked_ony), false);
    }

    /**
     * Persists the email address to preference storage space.
     *
     * @param context the Context to get PreferenceManager.
     * @param email   account email.
     */
    public static void saveEmailAccount(Context context, String email) {
        saveStringToPreference(context, PREFS_KEY_EMAIL, email);
    }

    /**
     * Returns the persisted email account, or <code>null</code> if none found.
     *
     * @param context the Context to get PreferenceManager.
     * @return email account.
     */
    public static String getEmailAccount(Context context) {
        return getStringFromPreference(context, PREFS_KEY_EMAIL);
    }

    /**
     * Saves a string value under the provided key in the preference manager. If <code>value</code>
     * is <code>null</code>, then the provided key will be removed from the preferences.
     *
     * @param context the Context to get PreferenceManager.
     * @param key     preference key.
     * @param value   preference value.
     */
    public static void saveStringToPreference(Context context, String key, String value) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        if (null == value) {
            // we want to remove
            pref.edit().remove(key).apply();
        } else {
            pref.edit().putString(key, value).apply();
        }
    }

    /**
     * Retrieves a String value from preference manager. If no such key exists, it will return
     * <code>null</code>.
     *
     * @param context the Context to get PreferenceManager.
     * @param key     preference key.
     * @return the value that is stored under the key.
     */
    public static String getStringFromPreference(Context context, String key) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getString(key, null);
    }

    /**
     * Check if notifications are allowed by user.
     *
     * @param context the {@link Context} to get {@link PreferenceManager} for.
     * @return if notifications are turned on. By default always true.
     */
    public static boolean checkNotify(Context context) {
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        return pref.getBoolean(context.getString(R.string.pref_key_notify), true);
    }
}
