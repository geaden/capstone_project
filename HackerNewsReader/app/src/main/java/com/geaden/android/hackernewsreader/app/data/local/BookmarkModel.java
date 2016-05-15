package com.geaden.android.hackernewsreader.app.data.local;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

/**
 * Represents model for storing bookmarks locally.
 *
 * @author Gennady Denisov
 */
@Table(database = StoriesDatabase.class, name = BookmarkModel.TABLE_NAME)
public class BookmarkModel extends BaseModel {
    public static final String TABLE_NAME = "bookmarks";

    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    @ForeignKey(tableClass = StoryModel.class)
    long story;
}
