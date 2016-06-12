package com.geaden.android.hackernewsreader.app.data.remote;

import android.support.annotation.NonNull;

import com.geaden.android.hackernewsreader.app.data.StoriesDataSource;
import com.geaden.hackernewsreader.backend.hackernews.model.Comment;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;
import com.google.api.client.util.DateTime;
import com.google.common.collect.Lists;

import java.util.Date;
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

    private static final Map<String, List<Comment>> COMMENTS_SERVICE_DATA = new LinkedHashMap<>();

    static {
        // Add stories
        addStory(1L, "foo");
        addStory(2L, "bar");
        addStory(3L, "baz");

        // Add comments
        addComments(1L);
    }

    private static void addStory(long storyId, String storyTitle) {
        Story story = new Story();
        story.setId(storyId);
        story.setTitle(storyTitle);
        story.setAuthor("foo");
        story.setScore(42L);
        story.setTime(new DateTime(new Date()));
        story.setContent("lorem ipsum");
        story.setUrl("http://example.com");
        story.setNoComments(42);
        STORIES_SERVICE_DATA.put(Long.toString(storyId), story);
    }

    private static void addComments(long storyId) {
        List<Comment> comments = Lists.newArrayList();

        for (int i = 0; i < 3; i++) {
            Comment comment = new Comment();
            comment.setId((long) i + 1);
            comment.setAuthor("foo");
            comment.setText("lorem ipsum " + (i + 1));
            comment.setTime(new DateTime(new Date()));
            comments.add(comment);
        }

        COMMENTS_SERVICE_DATA.put(Long.toString(storyId), comments);
    }

    public MockStoriesRemoteDataSource() {

    }

    @Override
    public void getStories(@NonNull GetStoriesCallback callback) {
        callback.onStoriesLoaded(Lists.newArrayList(STORIES_SERVICE_DATA.values()));
    }

    @Override
    public void getStory(@NonNull String storyId, @NonNull GetStoryCallback callback) {
        callback.onStoryLoaded(STORIES_SERVICE_DATA.get(storyId));

    }

    @Override
    public void getComments(@NonNull String storyId, @NonNull GetCommentsCallback callback) {
        callback.onCommentsLoaded(COMMENTS_SERVICE_DATA.get(storyId));
    }

    @Override
    public void getBookmarks(@NonNull GetBookmarksCallback callback) {
        callback.onBookmarksLoaded(null);
    }

    @Override
    public void saveComment(@NonNull String storyId, @NonNull Comment comment) {
        // no-op

    }

    @Override
    public void saveStory(@NonNull Story story) {

    }

    @Override
    public void addBookmark(@NonNull String storyId) {

    }

    @Override
    public void removeBookmark(@NonNull String storyId) {

    }

    @Override
    public void deleteAllStories() {

    }
}
