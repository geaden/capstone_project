package com.geaden.android.hackernewsreader.app;

import android.app.Application;

import com.geaden.android.hackernewsreader.app.data.DaggerStoriesRepositoryComponent;
import com.geaden.android.hackernewsreader.app.data.StoriesRepositoryComponent;
import com.geaden.android.hackernewsreader.app.data.StoriesRepositoryModule;

/**
 * Custom application to initialize Dagger components.
 *
 * @author Gennady Denisov
 */
public class StoriesApplication extends Application {

    private StoriesRepositoryComponent mRepositoryComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mRepositoryComponent = DaggerStoriesRepositoryComponent.builder()
                .storiesRepositoryModule(new StoriesRepositoryModule())
                .applicationModule(new ApplicationModule(getApplicationContext()))
                .build();

    }

    public StoriesRepositoryComponent getStoriesRepositoryComponent() {
        return mRepositoryComponent;
    }
}
