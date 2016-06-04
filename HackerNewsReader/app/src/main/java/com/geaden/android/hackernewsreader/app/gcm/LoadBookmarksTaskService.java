package com.geaden.android.hackernewsreader.app.gcm;

import android.util.Log;

import com.geaden.android.hackernewsreader.app.StoriesApplication;
import com.geaden.android.hackernewsreader.app.data.StoriesRepository;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import java.util.List;

/**
 * Gcm Task Service to get bookmarks.
 *
 * @author Gennady Denisov
 */
public class LoadBookmarksTaskService extends GcmTaskService {

    private static final String TAG = "LoadBookmarksTS";

    StoriesRepository mStoriesRepository;

    @Override
    public int onRunTask(TaskParams taskParams) {
        Log.d(TAG, "Loading bookmarks");

        mStoriesRepository = ((StoriesApplication) getApplication())
                .getStoriesRepositoryComponent()
                .getStoriesRepository();

        List<Story> bookmarks = mStoriesRepository.getBookmarks(true);

        return bookmarks != null ? GcmNetworkManager.RESULT_SUCCESS : GcmNetworkManager.RESULT_FAILURE;
    }
}
