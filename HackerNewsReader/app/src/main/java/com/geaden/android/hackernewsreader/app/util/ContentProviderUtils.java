package com.geaden.android.hackernewsreader.app.util;

import android.net.Uri;

import com.geaden.android.hackernewsreader.app.data.local.StoriesDatabase;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

/**
 * Utils used to construct content provider.
 *
 * @author Gennady Denisov
 */
public final class ContentProviderUtils {

    private ContentProviderUtils() {

    }

    public static Uri buildUri(String... paths) {
        return ContentUtils.buildUri(ContentUtils.BASE_CONTENT_URI,
                StoriesDatabase.CONTENT_AUTHORITY, paths);
    }
}
