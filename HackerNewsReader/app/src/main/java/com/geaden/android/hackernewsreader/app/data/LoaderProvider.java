package com.geaden.android.hackernewsreader.app.data;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;

import com.geaden.android.hackernewsreader.app.data.local.BookmarkModel;
import com.geaden.android.hackernewsreader.app.data.local.BookmarkModel_Table;
import com.geaden.android.hackernewsreader.app.data.local.CommentModel;
import com.geaden.android.hackernewsreader.app.data.local.CommentModel_Table;
import com.geaden.android.hackernewsreader.app.data.local.StoryModel;
import com.geaden.android.hackernewsreader.app.data.local.StoryModel_Table;
import com.geaden.android.hackernewsreader.app.stories.StoriesFilter;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Class to provider loader type.
 *
 * @author Gennady Denisov
 */
public class LoaderProvider {

    @NonNull
    private final Context mContext;

    public LoaderProvider(@NonNull Context context) {
        mContext = checkNotNull(context, "context cannot be null");
    }

    public Loader<Cursor> createFilteredStoriesLoader(StoriesFilter storiesFilter, String query) {
        Uri contentUri = StoryModel.CONTENT_URI;
        String selection = null;
        String[] selectionArgs = null;

        switch (storiesFilter.getStoriesFilterType()) {
            case ALL_STORIES:
                break;
            case BOOKMARKED_STORIES:
                selection = StoryModel_Table.id + " IN (" +
                        SQLite.select(BookmarkModel_Table.story_id)
                                .from(BookmarkModel.class).getQuery() + ")";
                break;
        }

        if (null != query) {
            selection = DatabaseUtils.concatenateWhere(selection, "LOWER(" + StoryModel_Table.title
                    + ") LIKE LOWER(?)");
            selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[]{query + '%'});
        }

        return new CursorLoader(
                mContext,
                contentUri,
                null,
                selection,
                selectionArgs,
                StoryModel_Table.score + " DESC"
        );
    }


    /**
     * Creates single story cursor loader.
     *
     * @param storyId the storyId
     * @return {@link CursorLoader}
     */
    public Loader<Cursor> createStoryLoader(String storyId) {
        return new CursorLoader(mContext,
                StoryModel.withId(storyId),
                null,
                null,
                null,
                null
        );
    }

    public Loader<Cursor> createCommentsLoader(String storyId) {
        return new CursorLoader(mContext,
                CommentModel.withStoryId(Long.valueOf(storyId)),
                null,
                null,
                null,
                CommentModel_Table.time + " DESC");

    }
}
