package com.geaden.android.hackernewsreader.app.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.geaden.hackernewsreader.backend.hackernews.model.Comment;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;

import java.util.List;

/**
 * Main entry point for accessing stories data.
 *
 * @author Gennady Denisov
 */
public interface StoriesDataSource {

    @Nullable
    List<Story> getStories();

    @Nullable
    Story getStory(@NonNull String storyId);

    @Nullable
    List<Comment> getComments(@NonNull String storyId);

    void saveComment(@NonNull String storyId, @NonNull Comment comment);

    @Nullable
    List<Story> getBookmarks(boolean update);

    void saveStory(@NonNull Story story);

    void addBookmark(@NonNull String storyId);

    void removeBookmark(@NonNull String storyId);

    void refreshStories();

    void deleteAllStories();
}
