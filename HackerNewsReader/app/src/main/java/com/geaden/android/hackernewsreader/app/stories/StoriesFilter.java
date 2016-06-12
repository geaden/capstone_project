package com.geaden.android.hackernewsreader.app.stories;

import android.os.Bundle;

import com.geaden.android.hackernewsreader.app.BuildConfig;

/**
 * Stories filter.
 * Basically just a wrapper of {@link Bundle} to get current value of {@link StoriesFilterType}.
 *
 * @author Gennady Denisov
 */
public class StoriesFilter {

    public final static String KEY_STORIES_FILTER = BuildConfig.APPLICATION_ID + "STORIES_FILTER";

    private StoriesFilterType storiesFilterType = StoriesFilterType.ALL_STORIES;
    private Bundle filterExtras;

    protected StoriesFilter(Bundle extras) {
        this.filterExtras = extras;
        this.storiesFilterType = (StoriesFilterType) extras.getSerializable(KEY_STORIES_FILTER);
    }

    public static StoriesFilter from(StoriesFilterType storiesFilterType) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(KEY_STORIES_FILTER, storiesFilterType);
        return new StoriesFilter(bundle);
    }

    public StoriesFilterType getStoriesFilterType() {
        return storiesFilterType;
    }

    public Bundle getFilterExtras() {
        return filterExtras;
    }
}
