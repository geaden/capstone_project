package com.geaden.android.hackernewsreader.app.data.remote;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.geaden.android.hackernewsreader.app.data.StoriesDataSource;
import com.geaden.android.hackernewsreader.app.util.Config;
import com.geaden.hackernewsreader.backend.hackernews.Hackernews;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.List;

/**
 * Implementation of the data source to interact with backend.
 *
 * @author Gennady Denisov
 */
public class StoriesRemoteDataSource implements StoriesDataSource {

    private static final Hackernews sApi;

    private static final String TAG = "StoriesRemoteDS";

    static {
        // TODO: Add credentials and manage compression...
        sApi = new Hackernews.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), null)
                // options for running against local devappserver
                // - 10.0.2.2 is localhost's IP address in Android emulator
                // - turn off compression when running against local devappserver
                .setRootUrl(Config.ROOT_URL)
                .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                    @Override
                    public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                            throws IOException {
                        abstractGoogleClientRequest.setDisableGZipContent(true);
                    }
                })
                .build();
    }

    @Nullable
    @Override
    public List<Story> getStories() {
        try {
            return sApi.getTopstories().execute().getItems();
        } catch (IOException e) {
            Log.e(TAG, "Unable to get stories from remote", e);
        }
        return null;
    }

    @Nullable
    @Override
    public Story getStory(@NonNull String storyId) {
        try {
            return sApi.getTopstory(Long.valueOf(storyId)).execute();
        } catch (IOException e) {
            Log.e(TAG, "Unable to get story from remote", e);
        }
        return null;
    }

    @Override
    public void saveStory(@NonNull Story story) {
        throw new UnsupportedOperationException("Remote save is not supported!");

    }

    @Override
    public void bookmarkStory(@NonNull String storyId) {
        try {
            sApi.bookmarkStory(Long.valueOf(storyId));
        } catch (IOException e) {
            Log.e(TAG, "Unable to bookmark story", e);
        }
    }

    @Override
    public void unbookmarkStory(@NonNull String storyId) {
        try {
            sApi.unbookmarkStory(Long.valueOf(storyId));
        } catch (IOException e) {
            Log.e(TAG, "Unable to unbookmark story", e);
        }

    }

    @Override
    public void refreshStories() {
        // no-op
    }

    @Override
    public void deleteAllStories() {
        throw new UnsupportedOperationException("Remote delete is not supported!");
    }
}
