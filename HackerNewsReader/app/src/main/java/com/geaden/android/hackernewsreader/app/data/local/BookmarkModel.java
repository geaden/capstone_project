package com.geaden.android.hackernewsreader.app.data.local;

import android.content.Context;
import android.net.Uri;

import com.geaden.android.hackernewsreader.app.util.ContentProviderUtils;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.Notify;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;
import com.raizlabs.android.dbflow.structure.provider.BaseSyncableProviderModel;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

/**
 * Represents model for storing bookmarks locally.
 *
 * @author Gennady Denisov
 */
@TableEndpoint(name = BookmarkModel.NAME, contentProvider = StoriesDatabase.class)
@Table(database = StoriesDatabase.class, name = BookmarkModel.NAME)
public class BookmarkModel extends BaseSyncableProviderModel {
    public static final String NAME = "bookmarks";

    @ContentUri(path = NAME, type = ContentUri.ContentType.VND_MULTIPLE + NAME)
    public static final Uri CONTENT_URI =
            ContentUtils.buildUriWithAuthority(StoriesDatabase.CONTENT_AUTHORITY, NAME);

    @ContentUri(type = ContentUri.ContentType.VND_SINGLE + NAME,
            path = NAME + "/#",
            segments = {@ContentUri.PathSegment(segment = 1, column = "story_id")}
    )
    public static Uri withStoryId(String storyId) {
        return ContentProviderUtils.buildUri(NAME, storyId);
    }

    @PrimaryKey(autoincrement = true)
    long id;

    @Column
    @ForeignKey(tableClass = StoryModel.class)
    long story;

    @Override
    public Uri getDeleteUri() {
        return CONTENT_URI;
    }

    @Override
    public Uri getInsertUri() {
        return CONTENT_URI;
    }

    @Override
    public Uri getUpdateUri() {
        return CONTENT_URI;
    }

    @Override
    public Uri getQueryUri() {
        return CONTENT_URI;
    }

    @Notify(method = Notify.Method.INSERT,
            paths = {NAME}) // specify paths that will call this method when specified.
    public static Uri[] onInsert(Context context, Uri uri) {
        return new Uri[]{
                StoryModel.CONTENT_URI
        };
    }

    @Notify(method = Notify.Method.DELETE,
            paths = {NAME}) // specify paths that will call this method when specified.
    public static Uri[] onDelete(Context context, Uri uri) {
        return new Uri[]{
                StoryModel.CONTENT_URI
        };
    }
}
