package com.geaden.android.hackernewsreader.app.gcmtask;

import com.geaden.android.hackernewsreader.app.StoriesApplication;
import com.geaden.android.hackernewsreader.app.data.StoriesRepository;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

/**
 * Gcm Task Service to get bookmarks.
 *
 * @author Gennady Denisov
 */
public class LoadBookmarksTaskService extends GcmTaskService {

    private StoriesRepository mStoriesRepository;

    @Override
    public int onRunTask(TaskParams taskParams) {
        mStoriesRepository = ((StoriesApplication) getApplication())
                .getStoriesRepositoryComponent()
                .getStoriesRepository();

        mStoriesRepository.getBookmarkedStories(true);

        return GcmNetworkManager.RESULT_SUCCESS;
    }
}
