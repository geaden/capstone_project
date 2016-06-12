package com.geaden.android.hackernewsreader.app.data.local;

import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.Database;
import com.raizlabs.android.dbflow.annotation.provider.ContentProvider;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

/**
 * Represents local database.
 *
 * @author Gennady Denisov
 */
@ContentProvider(authority = StoriesDatabase.CONTENT_AUTHORITY,
        database = StoriesDatabase.class,
        baseContentUri = ContentUtils.BASE_CONTENT_URI)
@Database(name = StoriesDatabase.NAME, version = StoriesDatabase.VERSION,
        insertConflict = ConflictAction.IGNORE, updateConflict = ConflictAction.REPLACE)
public class StoriesDatabase {
    public static final String NAME = "stories";

    public static final int VERSION = 1;

    public static final String CONTENT_AUTHORITY = "com.geaden.android.hackernewsreader.app.provider";
}
