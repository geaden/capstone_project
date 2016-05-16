package com.geaden.android.hackernewsreader.app.storydetail;

import android.support.annotation.NonNull;

import com.geaden.android.hackernewsreader.app.BasePresenter;
import com.geaden.android.hackernewsreader.app.BaseView;

import java.util.Date;

/**
 * This specifies the contract between the view and the presenter.
 *
 * @author Gennady Denisov
 */
public class StoryDetailContract {

    interface View extends BaseView<Presenter> {

        /**
         * Sets state of loading indicator.
         *
         * @param active whether indicator should be shown.
         */
        void setLoadingIndicator(boolean active);

        /**
         * Shows missing story.
         */
        void showMissingStory();

        /**
         * Loads story comments UI.
         *
         * @param storyId    the id of story to load comments for.
         * @param storyTitle title of the story.
         */
        void showStoryCommentsUi(String storyId, String storyTitle);

        /**
         * Show story main image if applicabale.
         *
         * @param imageUrl the image url to load into view.
         */
        void showStoryImage(@NonNull String imageUrl);

        /**
         * Show story main image from the resource.
         *
         * @param resourceId resource id.
         */
        void showStoryImage(int resourceId);

        /**
         * Shows title of the story.
         *
         * @param title title of the story.
         */
        void showStoryTitle(String title);

        /**
         * Hides title of the story.
         */
        void hideStoryTitle();

        /**
         * Shows story author.
         *
         * @param author author of the story.
         */
        void showStoryAuthor(String author);

        /**
         * Shows when story was published.
         *
         * @param date publish date.
         */
        void showStoryTime(Date date);

        /**
         * Shows story content.
         *
         * @param content the content of story.
         */
        void showStoryContent(String content);

        /**
         * Shows if story is bookmarked.
         */
        void showStoryBookmarked();

        /**
         * Shows if story is not bookmarked.
         */
        void showStoryNotBookmarked();

        /**
         * Launches share story intent.
         *
         * @param url the story url.
         */
        void launchShareStoryIntent(String url);

        /**
         * Launches original story intent.
         *
         * @param url the story url.
         */
        void launchOriginalStoryIntent(String url);

        /**
         * Hides content of the story.
         */
        void hideStoryContent();

        /**
         * Hides story author.
         */
        void hideStoryAuthor();

        /**
         * Hides story timestamp.
         */
        void hideStoryTime();
    }

    interface Presenter extends BasePresenter {

        /**
         * Opens new activity with story comments.
         */
        void openStoryComments();

        /**
         * Bookmarks the story.
         */
        void bookmarkStory();

        /**
         * Un-bookmarks the story.
         */
        void unbookmarkStory();

        /**
         * Shares a story.
         */
        void shareStory();

        /**
         * Opens original story.
         */
        void openOriginalStory();
    }
}
