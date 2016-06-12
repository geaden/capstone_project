package com.geaden.android.hackernewsreader.app.data;

import android.support.annotation.NonNull;

import com.geaden.hackernewsreader.backend.hackernews.model.Comment;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;

import java.util.List;

/**
 * Main entry point for accessing stories data.
 *
 * @author Gennady Denisov
 */
public interface StoriesDataSource {

    interface DataCallback {

        void onDataNotAvailable();
    }

    interface GetStoriesCallback extends DataCallback {

        void onStoriesLoaded(List<Story> stories);
    }

    interface GetStoryCallback extends DataCallback {

        void onStoryLoaded(Story story);
    }

    interface GetCommentsCallback extends DataCallback {

        void onCommentsLoaded(List<Comment> comments);
    }

    interface GetBookmarksCallback {

        void onBookmarksLoaded(List<Story> bookmarks);
    }

    void getStories(@NonNull GetStoriesCallback callback);

    void getStory(@NonNull String storyId, @NonNull GetStoryCallback callback);

    void getComments(@NonNull String storyId, @NonNull GetCommentsCallback callback);

    void saveComment(@NonNull String storyId, @NonNull Comment comment);

    void getBookmarks(@NonNull GetBookmarksCallback callback);

    void saveStory(@NonNull Story story);

    void addBookmark(@NonNull String storyId);

    void removeBookmark(@NonNull String storyId);

    void deleteAllStories();
}
