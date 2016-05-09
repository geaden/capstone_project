package com.geaden.android.hackernewsreader.app.data.local;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.geaden.android.hackernewsreader.app.data.StoriesDataSource;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;

import java.util.List;

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

    @Inject
    public StoriesLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
    }

    @Nullable
    @Override
    public List<Story> getStories() {
        // TODO: Implement this...
        return null;
    }

    @Nullable
    @Override
    public Story getStory(@NonNull String storyId) {
        // TODO: Implement this...
        return null;
    }

    @Override
    public void saveStory(@NonNull Story story) {
        // TODO: Implement this...
    }

    @Override
    public void bookmarkStory(@NonNull String storyId) {
        // TODO: Implement this...
    }

    @Override
    public void unbookmarkStory(@NonNull String storyId) {
        // TODO: Implement this...
    }

    @Override
    public void refreshStories() {
        // Not required because the {@link StoriesRepository} handles the logic of refreshing the
        // stories from all the available data sources.
    }

    @Override
    public void deleteAllStories() {
        // TODO: Implement this...
    }
}
