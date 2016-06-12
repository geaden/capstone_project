package com.geaden.android.hackernewsreader.app.comments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.geaden.android.hackernewsreader.app.data.LoaderProvider;
import com.geaden.android.hackernewsreader.app.data.StoriesDataSource;
import com.geaden.android.hackernewsreader.app.data.StoriesRepository;
import com.geaden.hackernewsreader.backend.hackernews.model.Comment;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Presenter for comments.
 *
 * @author Gennady Denisov
 */
public class CommentsPresenter implements CommentsContract.Presenter,
        LoaderManager.LoaderCallbacks<Cursor>, StoriesDataSource.GetCommentsCallback,
        StoriesRepository.LoadDataCallback {

    private final static int COMMENTS_LOADER = 3;

    private final LoaderManager mLoaderManager;

    private final CommentsContract.View mCommentsView;
    private final StoriesRepository mStoriesRepository;

    private LoaderProvider mLoaderProvider;

    private String mStoryId;

    public CommentsPresenter(
            @NonNull String storyId,
            @NonNull LoaderProvider loaderProvider,
            @NonNull LoaderManager loaderManager,
            @NonNull StoriesRepository storiesRepository,
            @NonNull CommentsContract.View commentsView) {
        mStoryId = checkNotNull(storyId, "storyId cannot be null!");
        mLoaderProvider = checkNotNull(loaderProvider, "loaderProvider cannot be null!");
        mLoaderManager = checkNotNull(loaderManager, "loaderManager cannot be null!");
        mStoriesRepository = checkNotNull(storiesRepository, "storiesRepository cannot be null!");
        mCommentsView = checkNotNull(commentsView, "commentsView cannot be null!");

        mCommentsView.setPresenter(this);

    }

    @Override
    public void loadComments(String storyId, boolean forceUpdate) {
        mCommentsView.setLoadingIndicator(true);
        mCommentsView.setLoadingIndicator(true);
        if (forceUpdate) {
            mStoriesRepository.getComments(storyId, this);
        }

        if (mLoaderManager.getLoader(COMMENTS_LOADER) == null) {
            mLoaderManager.initLoader(COMMENTS_LOADER, null, this);
        } else {
            mLoaderManager.restartLoader(COMMENTS_LOADER, null, this);
        }
    }

    @Override
    public void start() {
        loadComments(mStoryId, true);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return mLoaderProvider.createCommentsLoader(mStoryId);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCommentsView.setLoadingIndicator(false);

        if (data != null) {
            if (data.moveToFirst()) {
                onDataLoaded(data);
            } else {
                onDataEmpty();
            }
        } else {
            onDataNotAvailable();
        }
    }

    @Override
    public void onCommentsLoaded(List<Comment> comments) {

    }

    @Override
    public void onDataNotAvailable() {
        mCommentsView.setLoadingIndicator(false);
        mCommentsView.showNoComments();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    @Override
    public void onDataLoaded(Cursor data) {
        mCommentsView.setLoadingIndicator(false);
        mCommentsView.showComments(data);

    }

    @Override
    public void onDataEmpty() {
        mCommentsView.setLoadingIndicator(false);
        mCommentsView.showNoComments();
    }

    @Override
    public void onDataReset() {
    }
}
