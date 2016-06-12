package com.geaden.android.hackernewsreader.app.data.local;

import android.content.ContentResolver;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.geaden.android.hackernewsreader.app.data.StoriesDataSource;
import com.geaden.hackernewsreader.backend.hackernews.model.Comment;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete implementation of a data source as a db.
 * <p/>
 * Note: this is a singleton and we are opening the database once and not closing it. The framework
 * cleans up the resources when the application closes so we don't need to close the db.
 *
 * @author Gennady Denisov
 */
@Singleton
public class StoriesLocalDataSource implements StoriesDataSource {

    private final ContentResolver mContentResolver;

    @Inject
    public StoriesLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
        mContentResolver = context.getContentResolver();
    }

    @Override
    public void getStories(@NonNull GetStoriesCallback callback) {
        // no-op since the data is loaded via Cursor Loader
    }

    @Override
    public void getStory(@NonNull String storyId, @NonNull GetStoryCallback callback) {
        // no-op since the data is loaded via Cursor Loader
    }

    @Override
    public void getComments(@NonNull String storyId, @NonNull GetCommentsCallback callback) {
        // no-op since the data is loaded via Cursor Loader
    }

    @Override
    public void getBookmarks(@NonNull GetBookmarksCallback callback) {
        // no-op since the data is loaded via Cursor Loader
    }

    @Override
    public void saveComment(@NonNull String storyId, @NonNull Comment comment) {
        CommentModel commentModel = CommentModel.from(storyId, comment);
        commentModel.save();
    }

    @Override
    public void saveStory(@NonNull Story story) {
        StoryModel storyModel = StoryModel.from(story);
        storyModel.save();
    }

    @Override
    public void addBookmark(@NonNull String storyId) {
        BookmarkModel bookmarkModel = new BookmarkModel();
        bookmarkModel.story = Long.valueOf(storyId);
        ContentUtils.insert(BookmarkModel.CONTENT_URI, bookmarkModel);
    }

    @Override
    public void removeBookmark(@NonNull String storyId) {
        int deleted = mContentResolver.delete(BookmarkModel.CONTENT_URI, "story_id = ?",
                new String[]{storyId});
        Log.d("SLDS", "deleted: " + deleted);
    }

    @Override
    public void deleteAllStories() {
        mContentResolver.delete(CommentModel.CONTENT_URI, null, null);
        // Delete all stories that are not bookmarked...
        SQLite.delete().from(StoryModel.class).where(StoryModel_Table.id.notIn(
                SQLite.select(BookmarkModel_Table.story_id).from(BookmarkModel.class)
        )).query();
    }
}
