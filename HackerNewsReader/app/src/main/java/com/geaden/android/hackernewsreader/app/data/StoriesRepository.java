package com.geaden.android.hackernewsreader.app.data;

import android.database.Cursor;
import android.support.annotation.NonNull;

import com.geaden.hackernewsreader.backend.hackernews.model.Comment;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete implementation to load stories from the data sources into a cache.
 * <p/>
 * By marking the constructor with {@code @Inject} and the class with {@code @Singleton}, Dagger
 * injects the dependencies required to create an instance of the TasksRespository (if it fails, it
 * emits a compiler error). It uses {@link StoriesRepositoryModule} to do so, and the constructed
 * instance is available in {@link StoriesRepositoryModule}.
 * <p/>
 * Dagger generated code doesn't require public access to the constructor or class, and
 * therefore, to ensure the developer doesn't instantiate the class manually and bypasses Dagger,
 * it's good practice minimise the visibility of the class/constructor as much as possible.
 */
@Singleton
public class StoriesRepository implements StoriesDataSource {

    private final StoriesDataSource mStoriesRemoteDataSource;

    private final StoriesDataSource mStoriesLocalDataSource;

    /**
     * By marking the constructor with {@code @Inject}, Dagger will try to inject the dependencies
     * required to create an instance of the TasksRepository. Because {@link StoriesDataSource} is an
     * interface, we must provide to Dagger a way to build those arguments, this is done in
     * {@link StoriesRepository}.
     * <p/>
     * When two arguments or more have the same type, we must provide to Dagger a way to
     * differentiate them. This is done using a qualifier.
     * <p/>
     * Dagger strictly enforces that arguments not marked with {@code @Nullable} are not injected
     * with {@code @Nullable} values.
     */
    @Inject
    StoriesRepository(@Remote StoriesDataSource storiesRemoteDataSource,
                      @Local StoriesDataSource storiesLocalDataSource) {
        mStoriesRemoteDataSource = storiesRemoteDataSource;
        mStoriesLocalDataSource = storiesLocalDataSource;
    }

    @Override
    public void getStories(@NonNull final GetStoriesCallback callback) {
        checkNotNull(callback);

        // Load from server
        mStoriesRemoteDataSource.getStories(new GetStoriesCallback() {
            @Override
            public void onStoriesLoaded(List<Story> stories) {
                refreshLocalDataSourceWithStories(stories);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });
    }

    @Override
    public void getStory(@NonNull String storyId, @NonNull final GetStoryCallback callback) {
        checkNotNull(storyId);
        checkNotNull(callback);

        // Load from server
        mStoriesRemoteDataSource.getStory(storyId, new GetStoryCallback() {
            @Override
            public void onStoryLoaded(Story story) {
                callback.onStoryLoaded(story);
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });

    }

    @Override
    public void getComments(@NonNull final String storyId, @NonNull final GetCommentsCallback callback) {
        checkNotNull(storyId);
        checkNotNull(callback);

        // Load from server
        mStoriesRemoteDataSource.getComments(storyId, new GetCommentsCallback() {
            @Override
            public void onCommentsLoaded(List<Comment> comments) {
                if (comments != null) {
                    refreshLocalDataSourceWithComments(storyId, comments);
                } else {
                    callback.onDataNotAvailable();
                }
            }

            @Override
            public void onDataNotAvailable() {
                callback.onDataNotAvailable();
            }
        });

    }

    @Override
    public void getBookmarks(@NonNull final GetBookmarksCallback callback) {
        checkNotNull(callback);

        // Load from server
        mStoriesRemoteDataSource.getBookmarks(new GetBookmarksCallback() {
            @Override
            public void onBookmarksLoaded(List<Story> bookmarks) {
                refreshLocalDataSourceWithBookmarks(bookmarks);
            }
        });

    }

    private void refreshLocalDataSourceWithStories(List<Story> stories) {
        for (Story story : stories) {
            mStoriesLocalDataSource.saveStory(story);
        }
    }

    private void refreshLocalDataSourceWithComments(String storyId, List<Comment> comments) {
        for (Comment comment : comments) {
            mStoriesLocalDataSource.saveComment(storyId, comment);
        }
    }

    private void refreshLocalDataSourceWithBookmarks(List<Story> bookmarks) {
        for (Story story : bookmarks) {
            mStoriesLocalDataSource.addBookmark(Long.toString(story.getId()));
        }
    }

    @Override
    public void saveComment(@NonNull String storyId, @NonNull Comment comment) {
        // no-op
    }

    @Override
    public void saveStory(@NonNull Story story) {
        checkNotNull(story);
        mStoriesLocalDataSource.saveStory(story);
    }

    @Override
    public void addBookmark(@NonNull String storyId) {
        mStoriesLocalDataSource.addBookmark(storyId);
        mStoriesRemoteDataSource.addBookmark(storyId);
    }

    @Override
    public void removeBookmark(@NonNull String storyId) {
        mStoriesLocalDataSource.removeBookmark(storyId);
        mStoriesRemoteDataSource.removeBookmark(storyId);
    }

    @Override
    public void deleteAllStories() {
        mStoriesLocalDataSource.deleteAllStories();
    }

    public interface LoadDataCallback {
        void onDataLoaded(Cursor data);

        void onDataEmpty();

        void onDataNotAvailable();

        void onDataReset();
    }
}

