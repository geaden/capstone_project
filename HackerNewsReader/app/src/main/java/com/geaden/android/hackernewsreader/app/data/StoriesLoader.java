package com.geaden.android.hackernewsreader.app.data;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.AsyncTaskLoader;

import com.geaden.hackernewsreader.backend.hackernews.model.Story;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Custom {@link android.content.Loader} for a list of {@link Story}, using the
 * {@link StoriesRepository} as its source. This Loader is a {@link AsyncTaskLoader} so it queries
 * the data asynchronously.
 *
 * @author Gennady Denisov
 */
public class StoriesLoader extends AsyncTaskLoader<List<Story>>
        implements StoriesRepository.StoriesRepositoryObserver {

    private StoriesRepository mRepository;

    public StoriesLoader(Context context, @NonNull StoriesRepository storiesRepository) {
        super(context);
        checkNotNull(storiesRepository);
        mRepository = storiesRepository;
    }

    @Override
    public List<Story> loadInBackground() {
        return mRepository.getStories();
    }

    @Override
    public void deliverResult(List<Story> data) {
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
            deliverResult(mRepository.getCachedStories());
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
    protected void onStopLoading() {
        super.onStopLoading();
        mRepository.removeContentObserver(this);
    }

    @Override
    public void onStoriesChanged() {
        if (isStarted()) {
            forceLoad();
        }
    }
}
