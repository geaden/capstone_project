package com.geaden.android.hackernewsreader.app.stories;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.geaden.android.hackernewsreader.app.data.StoriesLoader;
import com.geaden.android.hackernewsreader.app.data.StoriesRepository;
import com.geaden.android.hackernewsreader.app.util.Utils;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Presenter for stories.
 *
 * @author Gennady Denisov
 */
public class StoriesPresenter implements StoriesContract.Presenter,
        LoaderManager.LoaderCallbacks<List<Story>> {

    private final static int STORIES_QUERY = 1;

    private final StoriesLoader mLoader;

    private final LoaderManager mLoaderManager;

    private final StoriesRepository mStoriesRepository;

    private final StoriesContract.View mStoriesView;

    private List<Story> mCurrentStories;

    private StoriesFilterType mCurrentFiltering = StoriesFilterType.ALL_STORIES;

    private boolean mFirstLoad;

    public StoriesPresenter(@NonNull StoriesLoader loader, @NonNull LoaderManager loaderManager,
                            @NonNull StoriesRepository storiesRepository,
                            @NonNull StoriesContract.View storiesView) {
        mLoader = checkNotNull(loader, "loader cannot be null!");
        mLoaderManager = checkNotNull(loaderManager, "loader manager cannot be null!");
        mStoriesRepository = checkNotNull(storiesRepository, "storiesRepository cannot be null!");
        mStoriesView = checkNotNull(storiesView, "storiesView cannot be null!");

        mStoriesView.setPresenter(this);
    }

    @Override
    public void start() {
        mLoaderManager.initLoader(STORIES_QUERY, null, this);
    }

    @Override
    public Loader<List<Story>> onCreateLoader(int id, Bundle args) {
        mStoriesView.setLoadingIndicator(true);
        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<Story>> loader, List<Story> data) {
        mStoriesView.setLoadingIndicator(false);

        mCurrentStories = data;
        if (mCurrentStories == null) {
            mStoriesView.showLoadingStoriesError();
        } else {
            showFilteredStories();
        }
    }

    /**
     * Method to show filtered stories.
     */
    private void showFilteredStories() {
        List<Story> storiesToDisplay = new ArrayList<>();

        List<Long> bookmarkedStories = null;

        if (mCurrentStories != null) {
            for (Story story : mCurrentStories) {
                switch (mCurrentFiltering) {
                    case BOOKMARKED_STORIES:
                        if (Utils.checkIfBookmarked(story.getId(), bookmarkedStories)) {
                            storiesToDisplay.add(story);
                        }
                        break;
                    default:
                        storiesToDisplay.add(story);
                }
            }
        }

        processStories(storiesToDisplay);

    }

    /**
     * Processes stories.
     *
     * @param stories the loaded stories.
     */
    private void processStories(List<Story> stories) {
        if (stories.isEmpty()) {
            // Show a message indicating there are no stories for that filter type.
            processEmptyStories();
        } else {
            // Show the list of stories
            mStoriesView.showStories(stories);
            // TODO: Check state of bookmarks...
        }
    }

    @Override
    public void loadStoriesByName(String query) {
        List<Story> storiesToDisplay = new ArrayList<>();

        List<Long> bookmarkedStories = null;

        if (mCurrentStories != null) {
            for (Story story : mCurrentStories) {
                if (story.getTitle().toLowerCase().contains(query.toLowerCase())) {
                    switch (mCurrentFiltering) {
                        case BOOKMARKED_STORIES:
                            if (Utils.checkIfBookmarked(story.getId(), bookmarkedStories)) {
                                storiesToDisplay.add(story);
                            }
                            break;
                        default:
                            storiesToDisplay.add(story);
                    }
                }
            }
        }

        processStories(storiesToDisplay);

    }

    /**
     * Processes empty tasks and notify user accordingly.
     */
    private void processEmptyStories() {
        switch (mCurrentFiltering) {
            case BOOKMARKED_STORIES:
                mStoriesView.showNoBookmarkedStories();
                break;
            default:
                mStoriesView.showNoStories();
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Story>> loader) {
        // no-op
    }

    @Override
    public void loadStories(boolean forceUpdate) {
        if (forceUpdate || mFirstLoad) {
            mFirstLoad = false;
            mStoriesRepository.refreshStories();
        } else {
            showFilteredStories();
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
     * @param requestType Can be {@link StoriesFilterType#ALL_STORIES} or
     *                    {@link StoriesFilterType#BOOKMARKED_STORIES}
     */
    @Override
    public void setFiltering(StoriesFilterType requestType) {
        mCurrentFiltering = requestType;
    }

    public StoriesFilterType getFiltering() {
        return mCurrentFiltering;
    }
}
