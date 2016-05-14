package com.geaden.android.hackernewsreader.app.data;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

import com.geaden.hackernewsreader.backend.hackernews.model.Story;

/**
 * Custom {@link android.content.Loader} for a {@link Story}, using the
 * {@link StoriesRepository} as its source. This Loader is a {@link AsyncTaskLoader} so it queries
 * the data asynchronously. .
 *
 * @author Gennady Denisov
 */
public class StoryLoader extends AsyncTaskLoader<Story>
        implements StoriesRepository.StoriesRepositoryObserver {

    private final String mStoryId;

    private StoriesRepository mRepository;

    public StoryLoader(Context context, String mStoryId, StoriesRepository mRepository) {
        super(context);
        this.mStoryId = mStoryId;
        this.mRepository = mRepository;
    }

    @Override
    public Story loadInBackground() {
        return mRepository.getStory(mStoryId);
    }

    @Override
    public void deliverResult(Story data) {
        if (isReset()) {
            return;
        }

        if (isStarted()) {
            super.deliverResult(data);
        }
    }

    @Override
    protected void onStartLoading() {
        // Deliver any previously loaded data immediately if available.
        if (mRepository.cachedStoriesAvailable()) {
            deliverResult(mRepository.getCachedStory(mStoryId));
        }

        // Begin monitoring the underlying data source.
        mRepository.addContentObserver(this);

        if (takeContentChanged() || !mRepository.cachedStoriesAvailable()) {
            // When a change has  been delivered or the repository cache isn't available, we force
            // a load.
            forceLoad();
        }
    }

    @Override
    protected void onReset() {
        onStopLoading();
        mRepository.removeContentObserver(this);
    }

    @Override
    public void onStoriesChanged() {
        if (isStarted()) {
            forceLoad();
        }
    }
}
