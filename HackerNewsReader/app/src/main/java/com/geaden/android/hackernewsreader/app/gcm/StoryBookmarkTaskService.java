package com.geaden.android.hackernewsreader.app.gcm;

import android.os.Bundle;
import android.util.Log;

import com.geaden.android.hackernewsreader.app.data.remote.StoriesRemoteDataSource;
import com.geaden.hackernewsreader.backend.hackernews.Hackernews;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import java.io.IOException;

/**
 * GCM Task to manage story bookmark.
 *
 * @author Gennady Denisov
 */
public class StoryBookmarkTaskService extends GcmTaskService {

    public static final String BOOKMARK_ACTION =
            "com.geaden.android.hackernewsreader.app.BOOKMARK_ACTION";
    public static final String EXTRA_STORY_ID =
            "com.geaden.android.hackernewsreader.app.EXTRA_STORY_ID";

    private static final String TAG = "StoryBookmarkTS";

    @Override
    public int onRunTask(TaskParams taskParams) {
        Bundle extras = taskParams.getExtras();
        String storyId = extras.getString(EXTRA_STORY_ID);
        boolean addBookmark = taskParams.getExtras().getBoolean(BOOKMARK_ACTION);
        Hackernews api = StoriesRemoteDataSource.newInstance(this).getApi();
        boolean isSuccess;
        if (addBookmark) {
            // Add story bookmark
            try {
                Log.d(TAG, "Adding bookmark");
                isSuccess = api.addBookmark(Long.valueOf(storyId)).execute().getResult();
            } catch (IOException e) {
                Log.e(TAG, "Unable to add a bookmark", e);
                isSuccess = false;
            }
        } else {
            // Remove story bookmark
            try {
                Log.d(TAG, "Removing bookmark");
                isSuccess = api.removeBookmark(Long.valueOf(storyId)).execute().getResult();
            } catch (IOException e) {
                Log.e(TAG, "Unable to remove a bookmark", e);
                isSuccess = false;
            }
        }
        return isSuccess ? GcmNetworkManager.RESULT_SUCCESS : GcmNetworkManager.RESULT_FAILURE;
    }
}
