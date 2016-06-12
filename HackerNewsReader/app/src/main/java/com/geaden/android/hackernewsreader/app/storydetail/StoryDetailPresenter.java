package com.geaden.android.hackernewsreader.app.storydetail;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;

import com.geaden.android.hackernewsreader.app.R;
import com.geaden.android.hackernewsreader.app.data.LoaderProvider;
import com.geaden.android.hackernewsreader.app.data.StoriesDataSource;
import com.geaden.android.hackernewsreader.app.data.StoriesRepository;
import com.geaden.android.hackernewsreader.app.util.DataUtils;
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
        StoriesRepository.LoadDataCallback, LoaderManager.LoaderCallbacks<Cursor>,
        StoriesDataSource.GetStoryCallback {

    private static final int STORY_LOADER = 2;

    private final StoriesRepository mStoriesRepository;

    private final StoryDetailContract.View mStoryDetailView;

    private final LoaderProvider mLoaderProvider;

    private LoaderManager mLoadManager;

    @Nullable
    private String mStoryId;

    @Nullable
    private String mStoryUrl;

    @Nullable
    private String mStoryTitle;
    private boolean mBookmark;

    public StoryDetailPresenter(@NonNull String storyId,
                                @NonNull LoaderProvider loaderProvider,
                                @NonNull StoriesRepository storiesRepository,
                                @NonNull StoryDetailContract.View storyDetailView,
                                @NonNull LoaderManager loadManager) {
        mStoryId = checkNotNull(storyId, "storyId cannot be null!");
        mLoaderProvider = checkNotNull(loaderProvider, "loaderProvider cannot be null!");
        mStoriesRepository = checkNotNull(storiesRepository, "storiesRepository cannot be null!");
        mStoryDetailView = checkNotNull(storyDetailView, "storyDetailView cannot be null!");
        mLoadManager = checkNotNull(loadManager, "loaderManager cannot be null!");

        mStoryDetailView.setPresenter(this);
    }

    @Override
    public void start() {
        loadStory();
    }

    private void loadStory() {
        mStoryDetailView.setLoadingIndicator(true);
        mLoadManager.initLoader(STORY_LOADER, null, this);
    }

    @Override
    public void onStoryLoaded(Story story) {
        // no-op, see #onDataLoaded

    }

    @Override
    public void onDataNotAvailable() {
        mStoryDetailView.showMissingStory();

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // no-op
    }


    /**
     * Loads story data into view.
     *
     * @param data {@link Cursor} containing story data.
     */
    private void showStory(Cursor data) {

        Pair<Story, Boolean> storyBookmarkPair = DataUtils.convertCursorToStory(data);

        Story story = storyBookmarkPair.first;
        mBookmark = storyBookmarkPair.second;

        String title = story.getTitle();
        String content = story.getContent();
        String author = story.getAuthor();
        DateTime published = story.getTime();

        mStoryUrl = story.getUrl();
        mStoryTitle = title;

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

        if (mBookmark) {
            mStoryDetailView.showStoryBookmarked();
        } else {
            mStoryDetailView.showStoryNotBookmarked();
        }

        String storyImageUrl = story.getImageUrl();

        if (storyImageUrl != null) {
            mStoryDetailView.showStoryImage(storyImageUrl);
        } else {
            mStoryDetailView.showStoryImage(R.drawable.story_image_default);
        }

        mStoryDetailView.setLoadingIndicator(false);
    }

    public void toggleStoryBookmark() {
        if (mBookmark) removeBookmark();
        else addBookmark();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mLoaderProvider.createStoryLoader(mStoryId);
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

    @Override
    public void onDataLoaded(Cursor data) {
        showStory(data);
    }

    @Override
    public void onDataEmpty() {
        mStoryDetailView.showMissingStory();
    }

    @Override
    public void onDataReset() {

    }

    @Override
    public void openStoryComments() {
        mStoryDetailView.showStoryCommentsUi(mStoryId, mStoryTitle);
    }

    @Override
    public void addBookmark() {
        mStoriesRepository.addBookmark(mStoryId);
        mStoryDetailView.showStoryBookmarked();
    }

    @Override
    public void removeBookmark() {
        mStoriesRepository.removeBookmark(mStoryId);
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
