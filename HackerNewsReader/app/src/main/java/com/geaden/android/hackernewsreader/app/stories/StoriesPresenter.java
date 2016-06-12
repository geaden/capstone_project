package com.geaden.android.hackernewsreader.app.stories;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.geaden.android.hackernewsreader.app.data.LoaderProvider;
import com.geaden.android.hackernewsreader.app.data.StoriesDataSource;
import com.geaden.android.hackernewsreader.app.data.StoriesRepository;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Presenter for stories.
 *
 * @author Gennady Denisov
 */
public class StoriesPresenter implements StoriesContract.Presenter, StoriesRepository.LoadDataCallback,
        StoriesDataSource.GetStoriesCallback, LoaderManager.LoaderCallbacks<Cursor> {

    public final static int STORIES_LOADER = 1;
    public final static String STORY_QUERY = "story_query";

    private final LoaderManager mLoaderManager;

    private final StoriesRepository mStoriesRepository;

    private final StoriesContract.View mStoriesView;

    private final LoaderProvider mLoaderProvider;

    private StoriesFilter mCurrentFiltering;

    public StoriesPresenter(@NonNull LoaderProvider loaderProvider,
                            @NonNull LoaderManager loaderManager,
                            @NonNull StoriesRepository storiesRepository,
                            @NonNull StoriesContract.View storiesView,
                            @NonNull StoriesFilter storiesFilter) {
        mLoaderProvider = checkNotNull(loaderProvider, "loaderProvider cannot be null!");
        mLoaderManager = checkNotNull(loaderManager, "loader manager cannot be null!");
        mStoriesRepository = checkNotNull(storiesRepository, "storiesRepository cannot be null!");
        mStoriesView = checkNotNull(storiesView, "storiesView cannot be null!");
        mCurrentFiltering = checkNotNull(storiesFilter, "storiesFilter cannot be null!");
        mStoriesView.setPresenter(this);
    }

    @Override
    public void start() {
        loadStories(true);
    }

    @Override
    public void loadStories(boolean forceUpdate) {
        mStoriesView.setLoadingIndicator(true);
        if (forceUpdate) {
            mStoriesRepository.getStories(this);
        }

        if (mLoaderManager.getLoader(STORIES_LOADER) == null) {
            mLoaderManager.initLoader(STORIES_LOADER, mCurrentFiltering.getFilterExtras(), this);
        } else {
            mLoaderManager.restartLoader(STORIES_LOADER, mCurrentFiltering.getFilterExtras(), this);
        }
    }

    @Override
    public void onStoriesLoaded(List<Story> stories) {

    }

    @Override
    public void onDataLoaded(Cursor data) {
        mStoriesView.setLoadingIndicator(false);
        // Show stories grid.
        mStoriesView.showStories(data);
        // Set the filter's label text.
        showFilterLabel();

    }

    private void showFilterLabel() {
        switch (mCurrentFiltering.getStoriesFilterType()) {
            case BOOKMARKED_STORIES:
                mStoriesView.showBookmarksFilterLabel();
                break;
            default:
                mStoriesView.showAllFilterLabel();
                break;
        }
    }

    @Override
    public void onDataEmpty() {
        mStoriesView.setLoadingIndicator(false);
        // Show a message indicating there are no tasks for that filter type.
        processEmptyStories();

    }

    @Override
    public void onDataNotAvailable() {
        mStoriesView.setLoadingIndicator(false);
        mStoriesView.showLoadingStoriesError();
    }

    @Override
    public void onDataReset() {
        mStoriesView.showStories(null);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String query = args.getString(STORY_QUERY);
        return mLoaderProvider.createFilteredStoriesLoader(mCurrentFiltering, query);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            if (data.moveToLast()) {
                onDataLoaded(data);
            } else {
                onDataEmpty();
            }
        } else {
            onDataNotAvailable();
        }
    }

    /**
     * Processes empty tasks and notify user accordingly.
     */
    private void processEmptyStories() {
        switch (mCurrentFiltering.getStoriesFilterType()) {
            case BOOKMARKED_STORIES:
                mStoriesView.showNoBookmarks();
                break;
            default:
                mStoriesView.showNoStories();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        onDataReset();
    }

    @Override
    public void loadStoriesByName(String query) {
        // Clone filtering bundles to prevent constant query...
        Bundle extras = new Bundle(mCurrentFiltering.getFilterExtras());
        extras.putString(STORY_QUERY, query);

        if (mLoaderManager.getLoader(STORIES_LOADER) == null) {
            mLoaderManager.initLoader(STORIES_LOADER, extras, this);
        } else {
            mLoaderManager.restartLoader(STORIES_LOADER, extras, this);
        }
    }

    @Override
    public void openStoryDetails(@NonNull Story requestedStory, @NonNull android.view.View storyImage) {
        checkNotNull(requestedStory, "requestedStory cannot be null!");
        mStoriesView.showStoryDetailsUi(Long.toString(requestedStory.getId()), storyImage);
    }

    /**
     * Sets the current stories filtering type.
     *
     * @param requestType {@link StoriesFilter}
     */
    @Override
    public void setFiltering(StoriesFilter requestType) {
        mCurrentFiltering = requestType;
    }

    public StoriesFilter getFiltering() {
        return mCurrentFiltering;
    }
}
