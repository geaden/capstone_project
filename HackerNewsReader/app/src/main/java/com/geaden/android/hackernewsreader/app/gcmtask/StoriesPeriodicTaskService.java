package com.geaden.android.hackernewsreader.app.gcmtask;

import android.util.Log;

import com.geaden.android.hackernewsreader.app.StoriesApplication;
import com.geaden.android.hackernewsreader.app.data.StoriesRepository;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

/**
 * Periodically updates stories from API.
 *
 * @author Gennady Denisov
 */
public class StoriesPeriodicTaskService extends GcmTaskService {

    private static final String TAG = "StoriesPeriodicTS";
    StoriesRepository mStoriesRepository;

    @Override
    public int onRunTask(TaskParams taskParams) {
        Log.d(TAG, "Loading stories periodic task.");
        // Inject stories repository
        mStoriesRepository = ((StoriesApplication) getApplication())
                .getStoriesRepositoryComponent()
                .getStoriesRepository();
        mStoriesRepository.deleteAllStories();
        mStoriesRepository.getStories();
        mStoriesRepository.getBookmarkedStories(true);
        return GcmNetworkManager.RESULT_SUCCESS;
    }
}
