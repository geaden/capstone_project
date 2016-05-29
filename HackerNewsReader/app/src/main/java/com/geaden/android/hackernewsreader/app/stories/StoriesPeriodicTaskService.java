package com.geaden.android.hackernewsreader.app.stories;

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

    StoriesRepository mStoriesRepository;

    @Override
    public int onRunTask(TaskParams taskParams) {
        mStoriesRepository = ((StoriesApplication) getApplication()).getStoriesRepositoryComponent()
                .getStoriesRepository();
        mStoriesRepository.deleteAllStories();
        mStoriesRepository.refreshStories();
        return GcmNetworkManager.RESULT_SUCCESS;
    }
}
