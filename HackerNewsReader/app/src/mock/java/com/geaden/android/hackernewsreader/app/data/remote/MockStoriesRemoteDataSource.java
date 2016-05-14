package com.geaden.android.hackernewsreader.app.data.remote;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.geaden.android.hackernewsreader.app.data.StoriesDataSource;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;
import com.google.common.collect.Lists;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of a remote data source with static access to the data for easy testing.
 *
 * @author Gennady Denisov
 */
public class MockStoriesRemoteDataSource implements StoriesDataSource {

    private static final Map<String, Story> STORIES_SERVICE_DATA = new LinkedHashMap<>();

    static {
        addStory(1L, "foo");
        addStory(2L, "bar");
        addStory(3L, "baz");
    }

    private static void addStory(long storyId, String storyTitle) {
        Story story = new Story();
        story.setId(storyId);
        story.setTitle(storyTitle);
        story.setAuthor("foo");
        story.setScore(42L);
        story.setContent("lorem ipsum");
        STORIES_SERVICE_DATA.put(Long.toString(storyId), story);
    }

    public MockStoriesRemoteDataSource() {

    }

    @Nullable
    @Override
    public List<Story> getStories() {
        return Lists.newArrayList(STORIES_SERVICE_DATA.values());
    }

    @Nullable
    @Override
    public Story getStory(@NonNull String storyId) {
        Story story = STORIES_SERVICE_DATA.get(storyId);
        return story;
    }

    @Override
    public void saveStory(@NonNull Story story) {

    }

    @Override
    public void bookmarkStory(@NonNull String storyId) {

    }

    @Override
    public void unbookmarkStory(@NonNull String storyId) {

    }

    @Override
    public void refreshStories() {

    }

    @Override
    public void deleteAllStories() {

    }
}
