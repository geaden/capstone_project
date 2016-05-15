package com.geaden.android.hackernewsreader.app.data.local;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

/**
 * Represents comments stored locally.
 *
 * @author Gennady Denisov
 */
@Table(database = StoriesDatabase.class, name = CommentModel.TABLE_NAME)
public class CommentModel extends BaseModel {

    public static final String TABLE_NAME = "comments";

    @PrimaryKey
    long id;

    @ForeignKey(tableClass = StoryModel.class)
    long story;

    @Column
    String author;

    @Column
    String text;

    @Column
    Date time;
}
