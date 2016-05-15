package com.geaden.android.hackernewsreader.app.data.local;

import com.raizlabs.android.dbflow.annotation.ConflictAction;
import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Represents local database.
 *
 * @author Gennady Denisov
 */
@Database(name = StoriesDatabase.NAME, version = StoriesDatabase.VERSION,
        insertConflict = ConflictAction.IGNORE, updateConflict = ConflictAction.REPLACE)
public class StoriesDatabase {
    public static final String NAME = "stories";

    public static final int VERSION = 1;
}
