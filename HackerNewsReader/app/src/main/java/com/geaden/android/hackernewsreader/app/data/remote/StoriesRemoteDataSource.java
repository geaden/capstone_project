package com.geaden.android.hackernewsreader.app.data.remote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.geaden.android.hackernewsreader.app.AppConstants;
import com.geaden.android.hackernewsreader.app.BuildConfig;
import com.geaden.android.hackernewsreader.app.data.StoriesDataSource;
import com.geaden.android.hackernewsreader.app.util.Config;
import com.geaden.android.hackernewsreader.app.util.Utils;
import com.geaden.hackernewsreader.backend.hackernews.Hackernews;
import com.geaden.hackernewsreader.backend.hackernews.model.Comment;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
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

    private static Hackernews sApi;

    private static final String TAG = "StoriesRemoteDS";

    private StoriesRemoteDataSource(Context context) {
        String emailAccount = Utils.getEmailAccount(context);
        GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(context,
                AppConstants.AUDIENCE);
        credential.setSelectedAccountName(emailAccount);

        Hackernews.Builder apiBuilder = new Hackernews.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), emailAccount != null ? credential : null)
                // options for running against local devappserver
                // - 10.0.2.2 is localhost's IP address in Android emulator
                // - turn off compression when running against local devappserver
                .setRootUrl(Config.ROOT_URL);
        if (BuildConfig.DEBUG) {
            apiBuilder.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                @Override
                public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                        throws IOException {
                    abstractGoogleClientRequest.setDisableGZipContent(true);
                }
            });
        }
        apiBuilder.setApplicationName("hackernews");
        sApi = apiBuilder.build();
    }

    public static StoriesRemoteDataSource getInstance(Context context) {
        return new StoriesRemoteDataSource(context);
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

    @Nullable
    @Override
    public List<Comment> getComments(@NonNull String storyId) {
        try {
            return sApi.getComments(Long.valueOf(storyId)).execute().getItems();
        } catch (IOException e) {
            Log.e(TAG, "Unable to get comments for story", e);
        }
        return null;
    }

    @Override
    public void saveComment(@NonNull String storyId, @NonNull Comment comment) {
        throw new UnsupportedOperationException("Remote save is not supported for a comment!");
    }

    @Override
    public void saveStory(@NonNull Story story) {
        throw new UnsupportedOperationException("Remote save is not supported for a story!");

    }

    @Nullable
    @Override
    public List<Story> getBookmarkedStories(boolean update) {
        try {
            sApi.getBookmarkedStories().execute().getItems();
        } catch (IOException e) {
            Log.e(TAG, "Unable to get bookmarked stories", e);
        }
        return null;
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
