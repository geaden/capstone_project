package com.geaden.android.hackernewsreader.app.comments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.geaden.android.hackernewsreader.app.data.CommentsLoader;
import com.geaden.hackernewsreader.backend.hackernews.model.Comment;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Presenter for comments.
 *
 * @author Gennady Denisov
 */
public class CommentsPresenter implements CommentsContract.Presenter,
        LoaderManager.LoaderCallbacks<List<Comment>> {

    private final static int COMMENTS_QUERY = 3;

    private final CommentsLoader mLoader;

    private final LoaderManager mLoaderManager;

    private final CommentsContract.View mCommentsView;

    private List<Comment> mCurrentComments;

    private boolean mFirstLoad;

    public CommentsPresenter(@NonNull CommentsLoader loader,
                             @NonNull LoaderManager loaderManager,
                             @NonNull CommentsContract.View commentsView) {
        mLoader = checkNotNull(loader, "loader cannot be null!");
        mLoaderManager = checkNotNull(loaderManager, "loaderManager cannot be null!");
        mCommentsView = checkNotNull(commentsView, "commentsView cannot be null!");

        mCommentsView.setPresenter(this);

    }

    @Override
    public void loadComments(String storyId) {
        if (mFirstLoad) {
            mFirstLoad = false;
        } else {
            processComments();
        }
    }

    @Override
    public void start() {
        mLoaderManager.initLoader(COMMENTS_QUERY, null, this);
    }

    @Override
    public Loader<List<Comment>> onCreateLoader(int id, Bundle args) {
        mCommentsView.setLoadingIndicator(true);
        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<List<Comment>> loader, List<Comment> data) {
        mCommentsView.setLoadingIndicator(false);

        mCurrentComments = data;

        processComments();

    }

    private void processComments() {
        if (mCurrentComments == null) {
            mCommentsView.showNoComments();
        } else {
            mCommentsView.showComments(mCurrentComments);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Comment>> loader) {
        // no-op
    }
}
