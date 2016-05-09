package com.geaden.android.hackernewsreader.app.data;

import com.geaden.android.hackernewsreader.app.ApplicationModule;
import com.geaden.android.hackernewsreader.app.StoriesApplication;

import javax.inject.Singleton;

import dagger.Component;

/**
 * This is a Dagger component. Refer to {@link StoriesApplication} for the list of Dagger components
 * used in this application.
 *
 * @author Gennady Denisov
 */
@Singleton
@Component(modules = {ApplicationModule.class, StoriesRepositoryModule.class})
public interface StoriesRepositoryComponent {

    StoriesRepository getStoriesRepository();
}
