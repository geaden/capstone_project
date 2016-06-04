package com.geaden.android.hackernewsreader.app.data;

import android.content.Context;

import com.geaden.android.hackernewsreader.app.data.local.StoriesLocalDataSource;
import com.geaden.android.hackernewsreader.app.data.remote.StoriesRemoteDataSource;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * This is used by Dagger to inject the required arguments into the {@link StoriesRepository}.
 *
 * @author Gennady Denisov
 */
@Module
public class StoriesRepositoryModule {

    @Singleton
    @Provides
    @Local
    StoriesDataSource provideStoriesLocalDataSource(Context context) {
        return new StoriesLocalDataSource(context);
    }

    @Singleton
    @Provides
    @Remote
    StoriesDataSource provideStoriesRemoteDataSource(Context context) {
        return StoriesRemoteDataSource.newInstance(context);
    }
}