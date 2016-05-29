package com.geaden.android.hackernewsreader.app.stories;

import android.support.annotation.NonNull;

import com.geaden.android.hackernewsreader.app.BasePresenter;
import com.geaden.android.hackernewsreader.app.BaseView;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;

import java.util.List;

/**
 * This interface specifies the contract between the view and the presenter.
 *
 * @author Gennady Denisov
 */
public class StoriesContract {

    interface View extends BaseView<Presenter> {

        /**
         * Sets loading indicator state.
         *
         * @param active if true then loading will be shown.
         */
        void setLoadingIndicator(boolean active);

        /**
         * Show loaded stories in UI.
         *
         * @param stories list of loaded stories.
         */
        void showStories(List<Story> stories);

        /**
         * Shows story details by provided id of the story.
         *
         * @param storyId    the id of {@link Story}
         * @param storyImage the image of story to get transition name for.
         */
        void showStoryDetailsUi(String storyId, android.view.View storyImage);

        /**
         * Shows error if occurred during loading of stories.
         */
        void showLoadingStoriesError();

        /**
         * Shows message if no stories available.
         */
        void showNoStories();

        /**
         * Shows message if no bookmarked stories available.
         */
        void showNoBookmarkedStories();

    }

    interface Presenter extends BasePresenter {

        /**
         * Loads stories into view.
         *
         * @param forceUpdate if additional request to remote repository is needed.
         */
        void loadStories(boolean forceUpdate);

        /**
         * Loads stories by name.
         *
         * @param query query containing name of the story.
         */
        void loadStoriesByName(String query);

        /**
         * Opens story details.
         *
         * @param requestedStory the requested {@link Story}
         * @param storyImage     the image view to get transition name for.
         */
        void openStoryDetails(@NonNull Story requestedStory, @NonNull android.view.View storyImage);

        /**
         * Sets filtering.
         *
         * @param requestType the filtering type.
         */
        void setFiltering(StoriesFilterType requestType);

        /**
         * Getter for currently set filtering.
         *
         * @return the filtering.
         */
        StoriesFilterType getFiltering();
    }
}
