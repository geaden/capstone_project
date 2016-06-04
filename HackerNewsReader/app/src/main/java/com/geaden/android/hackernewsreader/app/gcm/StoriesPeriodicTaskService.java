package com.geaden.android.hackernewsreader.app.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.geaden.android.hackernewsreader.app.R;
import com.geaden.android.hackernewsreader.app.StoriesApplication;
import com.geaden.android.hackernewsreader.app.data.StoriesRepository;
import com.geaden.android.hackernewsreader.app.stories.StoriesActivity;
import com.geaden.android.hackernewsreader.app.util.Utils;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;

import java.util.List;

/**
 * Periodically updates stories from API.
 *
 * @author Gennady Denisov
 */
public class StoriesPeriodicTaskService extends GcmTaskService {

    private static final String TAG = "StoriesPeriodicTS";
    private static final int NEW_STORIES_NOTIF = 0;
    StoriesRepository mStoriesRepository;

    @Override
    public int onRunTask(TaskParams taskParams) {
        Log.d(TAG, "Loading stories periodic task.");
        List<Story> currentStories = mStoriesRepository.getStories();
        long latestStoryId = getLatestStoryId(currentStories);
        // Inject stories repository
        mStoriesRepository = ((StoriesApplication) getApplication())
                .getStoriesRepositoryComponent()
                .getStoriesRepository();
        mStoriesRepository.deleteAllStories();
        List<Story> updatedStories = mStoriesRepository.getStories();
        long updatedStoryId = getLatestStoryId(updatedStories);
        if (latestStoryId != updatedStoryId) {
            // Notify user about updates...
            if (Utils.checkNotify(this)) {
                showNotification();
            }
        }
        mStoriesRepository.getBookmarks(true);
        return GcmNetworkManager.RESULT_SUCCESS;
    }

    /**
     * Gets latest story id.
     *
     * @param stories list of stories.
     * @return latest story id.
     */
    private long getLatestStoryId(List<Story> stories) {
        long latestStoryId = -1L;
        if (stories != null && stories.size() > 0) {
            latestStoryId = stories.get(0).getId();
        }
        return latestStoryId;
    }

    @SuppressWarnings("ResourceType")
    private void showNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(getString(R.string.app_name))
                        .setAutoCancel(true)
                        .setContentText(getString(R.string.notif_text_new_stories_available));

        // Creates an explicit intent for an Activity in your app
        Intent resultIntent = new Intent(this, StoriesActivity.class);

        // The stack builder object will contain an artificial back stack for the
        // started Activity.
        // This ensures that navigating backward from the Activity leads out of
        // your application to the Home screen.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        // Adds the back stack for the Intent (but not the Intent itself)
        stackBuilder.addParentStack(StoriesActivity.class);
        // Adds the Intent that starts the Activity to the top of the stack
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        // mId allows you to update the notification later on.
        mNotificationManager.notify(NEW_STORIES_NOTIF, mBuilder.build());
    }
}
