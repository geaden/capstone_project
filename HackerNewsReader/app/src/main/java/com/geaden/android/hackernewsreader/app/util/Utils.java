package com.geaden.android.hackernewsreader.app.util;

import android.text.format.DateUtils;

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
}
