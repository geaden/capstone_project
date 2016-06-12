package com.geaden.android.hackernewsreader.app.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import com.geaden.hackernewsreader.backend.hackernews.model.Comment;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Custom {@link android.content.Loader} for a list of {@link Comment}, using the
 * {@link StoriesRepository} as its source. This Loader is a {@link AsyncTaskLoader} so it queries
 * the data asynchronously.
 *
 * @author Gennady Denisov
 */
public class CommentsLoader extends AsyncTaskLoader<List<Comment>> {

    private StoriesRepository mRepository;

    private String mStoryId;

    public CommentsLoader(Context context, @NonNull String storyId,
                          @NonNull StoriesRepository storiesRepository) {
        super(context);
        checkNotNull(storiesRepository);
        checkNotNull(storyId);
        mRepository = storiesRepository;
        mStoryId = storyId;
    }

    @Override
    public List<Comment> loadInBackground() {
        return null; //mRepository.getComments(mStoryId);
    }

    @Override
    public void deliverResult(List<Comment> data) {
        if (isReset()) {
            return;
        }

        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();
    }
}
