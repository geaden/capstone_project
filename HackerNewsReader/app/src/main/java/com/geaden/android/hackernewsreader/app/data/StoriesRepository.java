package com.geaden.android.hackernewsreader.app.data;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.geaden.hackernewsreader.backend.hackernews.model.Comment;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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

    private List<StoriesRepositoryObserver> mObservers = new ArrayList<StoriesRepositoryObserver>();

    /**
     * This variable has package local visibility so it can be accessed from tests.
     */
    Map<String, Story> mCachedStories;

    /**
     * Marks the cache as invalid, to force an update the next time data is requested. This variable
     * has package local visibility so it can be accessed from tests.
     */
    boolean mCacheIsDirty;

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

    // Observer pattern implementation here.
    public void addContentObserver(StoriesRepositoryObserver observer) {
        if (!mObservers.contains(observer)) {
            mObservers.add(observer);
        }
    }

    public void removeContentObserver(StoriesRepositoryObserver observer) {
        if (mObservers.contains(observer)) {
            mObservers.remove(observer);
        }
    }

    private void notifyContentObserver() {
        for (StoriesRepositoryObserver observer : mObservers) {
            observer.onStoriesChanged();
        }
    }

    /**
     * Gets stories from cache, local data source (SQLite) or remote data source, whichever is
     * available first. This is done synchronously because it's used by the {@link StoriesLoader},
     * which implements the async mechanism.
     */
    @Nullable
    @Override
    public List<Story> getStories() {

        List<Story> stories = null;

        if (!mCacheIsDirty) {
            // Respond immediately with cache if available and not dirty
            if (mCachedStories != null) {
                stories = getCachedStories();
                return stories;
            } else {
                // Query the local storage if available.
                stories = mStoriesLocalDataSource.getStories();
            }
        }

        // Consider the local data source fresh when it has data.
        if (stories == null || stories.isEmpty()) {
            // Grab remote data.
            stories = mStoriesRemoteDataSource.getStories();
            // We copy the data to the device so we don't need to query the network next time
            saveStoriesInLocalDataSource(stories);
        }

        processLoadedStories(stories);

        return getCachedStories();
    }

    @Nullable
    @Override
    public List<Comment> getComments(@NonNull String storyId) {
        List<Comment> comments = mStoriesLocalDataSource.getComments(storyId);

        if (comments == null || comments.isEmpty()) {
            comments = mStoriesRemoteDataSource.getComments(storyId);
            saveCommentInLocalDataSource(storyId, comments);
        }

        return comments;
    }

    private void saveCommentInLocalDataSource(String storyId, List<Comment> comments) {
        if (comments != null) {
            for (Comment comment : comments) {
                mStoriesLocalDataSource.saveComment(storyId, comment);
            }
        }

    }

    @Override
    public void saveComment(@NonNull String storyId, @NonNull Comment comment) {
        // no-op
    }

    public boolean cachedStoriesAvailable() {
        return mCachedStories != null && !mCacheIsDirty;
    }

    public List<Story> getCachedStories() {
        return mCachedStories == null ? null : new ArrayList<>(mCachedStories.values());
    }

    public Story getCachedStory(String storyId) {
        return mCachedStories.get(storyId);
    }

    /**
     * Processes loaded stories by storing them into local cache.
     *
     * @param stories list of {@link Story}s.
     */
    private void processLoadedStories(List<Story> stories) {
        if (stories == null) {
            mCachedStories = null;
            mCacheIsDirty = false;
            return;
        }
        if (mCachedStories == null) {
            mCachedStories = new LinkedHashMap<>();
        }
        mCachedStories.clear();
        for (Story story : stories) {
            mCachedStories.put(Long.toString(story.getId()), story);
        }
        mCacheIsDirty = false;
    }

    private void saveStoriesInLocalDataSource(List<Story> stories) {
        if (stories != null) {
            for (Story story : stories) {
                mStoriesLocalDataSource.saveStory(story);
            }
        }
    }

    @Override
    public void saveStory(@NonNull Story story) {
        checkNotNull(story);

        mStoriesLocalDataSource.saveStory(story);

        // Update the UI.
        notifyContentObserver();
    }

    /**
     * Gets story from local data source (sqlite) unless the table is new or empty. In that case it
     * uses the network data source.
     */
    @Nullable
    @Override
    public Story getStory(@NonNull String storyId) {
        checkNotNull(storyId);

        Story cachedStory = getStoryWithId(storyId);

        // Respond immediately with cache if we have one
        if (cachedStory != null) {
            return cachedStory;
        }

        // Is the task in the local data source? If not, query the network.
        Story story = mStoriesLocalDataSource.getStory(storyId);
        if (story == null) {
            story = mStoriesRemoteDataSource.getStory(storyId);
        }

        return story;
    }

    @Nullable
    private Story getStoryWithId(@NonNull String id) {
        checkNotNull(id);
        if (mCachedStories == null || mCachedStories.isEmpty()) {
            return null;
        } else {
            return mCachedStories.get(id);
        }
    }

    @Override
    public void bookmarkStory(@NonNull String storyId) {
        mStoriesLocalDataSource.bookmarkStory(storyId);
        mStoriesRemoteDataSource.bookmarkStory(storyId);
    }

    @Override
    public void unbookmarkStory(@NonNull String storyId) {
        mStoriesLocalDataSource.unbookmarkStory(storyId);
        mStoriesRemoteDataSource.unbookmarkStory(storyId);
    }

    @Override
    public void refreshStories() {
        mCacheIsDirty = true;
        notifyContentObserver();
    }

    @Override
    public void deleteAllStories() {
        mStoriesLocalDataSource.deleteAllStories();
    }

    /**
     * Observes when stories are changed.
     */
    public interface StoriesRepositoryObserver {

        void onStoriesChanged();
    }
}

