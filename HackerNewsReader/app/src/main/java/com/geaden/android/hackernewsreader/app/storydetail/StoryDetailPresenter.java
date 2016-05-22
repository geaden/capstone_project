package com.geaden.android.hackernewsreader.app.storydetail;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.geaden.android.hackernewsreader.app.R;
import com.geaden.android.hackernewsreader.app.data.StoriesRepository;
import com.geaden.android.hackernewsreader.app.data.StoryLoader;
import com.geaden.android.hackernewsreader.app.util.Utils;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;
import com.google.api.client.util.DateTime;

import java.util.Date;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI ({@link StoryDetailFragment}), retrieves the data and updates
 * the UI as required.
 *
 * @author Gennady Denisov
 */
public class StoryDetailPresenter implements StoryDetailContract.Presenter,
        LoaderManager.LoaderCallbacks<Story> {

    private static final int STORY_QUERY = 2;

    private final StoriesRepository mStoriesRepository;

    private final StoryDetailContract.View mStoryDetailView;

    private StoryLoader mStoryLoader;

    private LoaderManager mLoadManager;

    @Nullable
    private String mStoryId;

    @Nullable
    private String mStoryUrl;

    @Nullable
    private String mStoryTitle;

    public StoryDetailPresenter(@Nullable String storyId,
                                @NonNull StoriesRepository storiesRepository,
                                @NonNull StoryDetailContract.View storyDetailView,
                                @NonNull StoryLoader storyLoader,
                                @NonNull LoaderManager loadManager) {
        mStoryId = storyId;
        mStoriesRepository = checkNotNull(storiesRepository, "storiesRepository cannot be null!");
        mStoryDetailView = checkNotNull(storyDetailView, "storyDetailView cannot be null!");
        mStoryLoader = checkNotNull(storyLoader, "storyLoader cannot be null!");
        mLoadManager = checkNotNull(loadManager, "loaderManager cannot be null!");

        mStoryDetailView.setPresenter(this);
    }

    @Override
    public void start() {
        mLoadManager.initLoader(STORY_QUERY, null, this);
    }

    /**
     * Loads story data into view.
     *
     * @param story the story to show.
     */
    private void showStory(Story story) {
        String title = story.getTitle();
        String content = story.getContent();
        String author = story.getAuthor();
        DateTime published = story.getTime();

        if (title != null && title.isEmpty()) {
            mStoryDetailView.hideStoryTitle();
        } else {
            mStoryDetailView.showStoryTitle(title);
        }

        if (content != null && content.isEmpty()) {
            mStoryDetailView.hideStoryContent();
        } else {
            mStoryDetailView.showStoryContent(content);
        }

        if (author != null && author.isEmpty()) {
            mStoryDetailView.hideStoryAuthor();
        } else {
            mStoryDetailView.showStoryAuthor(author);
        }

        if (published != null) {
            mStoryDetailView.showStoryTime(new Date(published.getValue()));
        } else {
            mStoryDetailView.hideStoryTime();
        }

        boolean bookmarked = Utils.checkIfBookmarked(story.getId());

        if (bookmarked) {
            mStoryDetailView.showStoryBookmarked();
        } else {
            mStoryDetailView.showStoryNotBookmarked();
        }

        String storyImageUrl = story.getImageUrl();

        if (storyImageUrl != null) {
            mStoryDetailView.showStoryImage(storyImageUrl);
        } else {
            mStoryDetailView.showStoryImage(R.drawable.overlay_bg_bottom_up);
        }

        mStoryDetailView.setLoadingIndicator(false);
    }

    @Override
    public Loader<Story> onCreateLoader(int id, Bundle args) {
        if (mStoryId == null) {
            return null;
        }
        mStoryDetailView.setLoadingIndicator(true);
        return mStoryLoader;
    }

    @Override
    public void onLoadFinished(Loader<Story> loader, Story story) {
        if (story != null) {
            mStoryUrl = story.getUrl();
            mStoryTitle = story.getTitle();
            showStory(story);
        } else {
            mStoryDetailView.showMissingStory();
        }
    }

    @Override
    public void onLoaderReset(Loader<Story> loader) {
        // no-op
    }

    @Override
    public void openStoryComments() {
        mStoryDetailView.showStoryCommentsUi(mStoryId, mStoryTitle);
    }

    @Override
    public void bookmarkStory() {
        mStoriesRepository.bookmarkStory(mStoryId);
        mStoryDetailView.showStoryBookmarked();
    }

    @Override
    public void unbookmarkStory() {
        mStoriesRepository.unbookmarkStory(mStoryId);
        mStoryDetailView.showStoryNotBookmarked();
    }

    @Override
    public void shareStory() {
        mStoryDetailView.launchShareStoryIntent(mStoryUrl);
    }

    @Override
    public void openOriginalStory() {
        mStoryDetailView.launchOriginalStoryIntent(mStoryUrl);
    }
}
