package com.geaden.android.hackernewsreader.app.data.remote;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;

import com.geaden.android.hackernewsreader.app.AppConstants;
import com.geaden.android.hackernewsreader.app.data.StoriesDataSource;
import com.geaden.android.hackernewsreader.app.gcm.StoryBookmarkTaskService;
import com.geaden.android.hackernewsreader.app.util.Config;
import com.geaden.android.hackernewsreader.app.util.Utils;
import com.geaden.hackernewsreader.backend.hackernews.Hackernews;
import com.geaden.hackernewsreader.backend.hackernews.model.Comment;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.OneoffTask;
import com.google.android.gms.gcm.Task;
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

    private static final String HN_BOOKMARK_STORY_TASK =
            "com.geaden.android.hackernewsreader.app.BOOKMARK_STORY_TASK";

    private Hackernews mAPi;

    private static final String TAG = "StoriesRemoteDS";

    private final Context mContext;

    private final GcmNetworkManager mGcmNetworkManager;

    private StoriesRemoteDataSource(Context context) {
        buildApi(context);
        mGcmNetworkManager = GcmNetworkManager.getInstance(context);
        mContext = context;
    }

    private void buildApi(Context context) {
        String emailAccount = Utils.getEmailAccount(context);
        Log.d(TAG, "Email account: " + emailAccount);
        GoogleAccountCredential credential = GoogleAccountCredential.usingAudience(context,
                AppConstants.AUDIENCE);
        credential.setSelectedAccountName(emailAccount);

        Hackernews.Builder apiBuilder = new Hackernews.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), emailAccount != null ? credential : null)
                // options for running against local devappserver
                // - 10.0.2.2 is localhost's IP address in Android emulator
                // - turn off compression when running against local devappserver
                .setRootUrl(Config.ROOT_URL);
        if (Config.ROOT_URL.equals("https://10.0.0.2/_ah/api/")) {
            apiBuilder.setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                @Override
                public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest)
                        throws IOException {
                    abstractGoogleClientRequest.setDisableGZipContent(true);
                }
            });
        }
        apiBuilder.setApplicationName("hackernewsreader-api");
        mAPi = apiBuilder.build();
    }

    public Hackernews getApi() {
        return mAPi;
    }

    public static StoriesRemoteDataSource newInstance(Context context) {
        return new StoriesRemoteDataSource(context);
    }

    @Override
    public void getStories(@NonNull GetStoriesCallback callback) {
        try {
            List<Story> stories = mAPi.getTopstories().execute().getItems();
            callback.onStoriesLoaded(stories);
        } catch (IOException e) {
            Log.e(TAG, "Unable to get stories from remote", e);
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void getStory(@NonNull String storyId, @NonNull GetStoryCallback callback) {
        try {
            Story story = mAPi.getTopstory(Long.valueOf(storyId)).execute();
            callback.onStoryLoaded(story);
        } catch (IOException e) {
            Log.e(TAG, "Unable to get story from remote", e);
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void getComments(@NonNull String storyId, @NonNull GetCommentsCallback callback) {
        try {
            List<Comment> comments = mAPi.getComments(Long.valueOf(storyId)).execute().getItems();
            callback.onCommentsLoaded(comments);
        } catch (IOException e) {
            Log.e(TAG, "Unable to get comments for story", e);
            callback.onDataNotAvailable();
        }
    }

    @Override
    public void getBookmarks(@NonNull GetBookmarksCallback callback) {
        // Call this just to reinitialize mApi
        buildApi(mContext);
        try {
            List<Story> bookmarks = mAPi.getBookmarks().execute().getItems();
            callback.onBookmarksLoaded(bookmarks);
        } catch (IOException e) {
            Log.e(TAG, "Unable to get bookmarked stories", e);
        }
    }

    @Override
    public void saveComment(@NonNull String storyId, @NonNull Comment comment) {
        throw new UnsupportedOperationException("Remote save is not supported for a comment!");
    }

    @Override
    public void saveStory(@NonNull Story story) {
        throw new UnsupportedOperationException("Remote save is not supported for a story!");
    }

    @Override
    public void addBookmark(@NonNull String storyId) {
        startBookmarkTask(storyId, true);
    }

    @Override
    public void removeBookmark(@NonNull String storyId) {
        startBookmarkTask(storyId, false);
    }

    private void startBookmarkTask(String storyId, boolean addBookmark) {
        if (Utils.getEmailAccount(mContext) == null) {
            // Bookmarks are stored locally only.
            return;
        }
        Bundle extras = new Bundle();
        extras.putBoolean(StoryBookmarkTaskService.BOOKMARK_ACTION, addBookmark);
        extras.putString(StoryBookmarkTaskService.EXTRA_STORY_ID, storyId);
        OneoffTask oneoffTask = new OneoffTask.Builder()
                .setExtras(extras)
                .setRequiredNetwork(Task.NETWORK_STATE_CONNECTED)  // Execute only if user is connected to network.
                .setService(StoryBookmarkTaskService.class)
                .setUpdateCurrent(true)
                .setExecutionWindow(0L, 10L)    // Execute task withing first 10 seconds.
                .setTag(HN_BOOKMARK_STORY_TASK)
                .build();
        mGcmNetworkManager.schedule(oneoffTask);
    }

    @Override
    public void deleteAllStories() {
        throw new UnsupportedOperationException("Remote delete is not supported!");
    }
}
