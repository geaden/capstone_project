package com.geaden.android.hackernewsreader.app.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.geaden.hackernewsreader.backend.hackernews.model.Story;

import java.util.List;

/**
 * Main entry point for accessing stories data.
 *
 * @author Gennady Denisov
 */
public interface StoriesDataSource {

    /**
     * Callback to handle story loaded events.
     */
    interface GetStoryCallback {

        /**
         * Called when story is loaded and ready to be shown.
         *
         * @param story loaded story.
         */
        void onStoryLoaded(Story story);

        /**
         * Called when no story is available.
         */
        void onDataNotAvailable();
    }

    @Nullable
    List<Story> getStories();

    @Nullable
    Story getStory(@NonNull String storyId);

    void saveStory(@NonNull Story story);

    void bookmarkStory(@NonNull String storyId);

    void unbookmarkStory(@NonNull String storyId);

    void refreshStories();

    void deleteAllStories();
}
