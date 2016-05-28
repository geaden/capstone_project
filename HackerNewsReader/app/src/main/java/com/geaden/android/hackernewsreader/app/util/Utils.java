package com.geaden.android.hackernewsreader.app.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.format.DateUtils;

import com.geaden.android.hackernewsreader.app.R;
import com.geaden.android.hackernewsreader.app.data.local.BookmarkModel;
import com.geaden.android.hackernewsreader.app.data.local.BookmarkModel_Table;
import com.raizlabs.android.dbflow.sql.language.SQLite;

/**
 * Holder for utility methods.
 *
 * @author Gennady Denisov
 */
public final class Utils {

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
     * @param storyId the story id.
     * @return if story bookmarked.
     */
    public static boolean checkIfBookmarked(long storyId) {
        long bookmarkedStoryCount = SQLite.select().from(BookmarkModel.class)
                .where(BookmarkModel_Table.story_id.eq(storyId))
                .count();
        return bookmarkedStoryCount > 0;
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
     * Helper method to check if user is signed in the app.
     *
     * @param context the Context to get PreferenceManger from.
     * @return if user signed in.
     */
    public static boolean signedIn(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        String accountName = sp.getString(context.getString(R.string.pref_key_account_name), null);
        return accountName != null;
    }
}
