package com.geaden.android.hackernewsreader.app.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import com.geaden.android.hackernewsreader.app.R;
import com.geaden.android.hackernewsreader.app.StoriesApplication;
import com.geaden.android.hackernewsreader.app.data.StoriesDataSource;
import com.geaden.android.hackernewsreader.app.data.StoriesRepository;
import com.geaden.android.hackernewsreader.app.data.local.StoryModel;
import com.geaden.android.hackernewsreader.app.stories.StoriesActivity;
import com.geaden.android.hackernewsreader.app.util.Utils;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;
import com.google.android.gms.gcm.GcmNetworkManager;
import com.google.android.gms.gcm.GcmTaskService;
import com.google.android.gms.gcm.TaskParams;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

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

    public static final String ACTION_DATA_UPDATED =
            "com.geaden.android.hackernewsreader.app.ACTION_DATA_UPDATED";

    @Override
    public int onRunTask(TaskParams taskParams) {
        Log.d(TAG, "Loading stories periodic task.");
        // Inject stories repository
        mStoriesRepository = ((StoriesApplication) getApplication())
                .getStoriesRepositoryComponent()
                .getStoriesRepository();


        List<StoryModel> currentStories = ContentUtils.queryList(StoryModel.CONTENT_URI,
                StoryModel.class, ConditionGroup.clause(), null);

        final long latestStoryId = getLatestStoryId(currentStories);
        mStoriesRepository.deleteAllStories();
        mStoriesRepository.getStories(new StoriesDataSource.GetStoriesCallback() {
            @Override
            public void onStoriesLoaded(List<Story> stories) {
                List<StoryModel> updateStories = ContentUtils.queryList(StoryModel.CONTENT_URI,
                        StoryModel.class, ConditionGroup.clause(), null);

                final long updateStoryId = getLatestStoryId(updateStories);

                if (latestStoryId != updateStoryId) {
                    if (Utils.checkNotify(StoriesPeriodicTaskService.this)) {
                        showNotification();
                    }
                }

                updateWidgets();
            }

            @Override
            public void onDataNotAvailable() {

            }
        });

        if (Utils.getEmailAccount(this) != null) {
            // Request bookmarks only if user is signed in.
            mStoriesRepository.getBookmarks(new StoriesDataSource.GetBookmarksCallback() {
                @Override
                public void onBookmarksLoaded(List<Story> bookmarks) {

                }
            });
        }

        return GcmNetworkManager.RESULT_SUCCESS;
    }

    /**
     * Gets latest story id.
     *
     * @param stories list of stories.
     * @return latest story id.
     */
    private long getLatestStoryId(List<StoryModel> stories) {
        long latestStoryId = -1L;
        if (stories != null && stories.size() > 0) {
            latestStoryId = stories.get(0).getStory().getId();
        }
        return latestStoryId;
    }

    @SuppressWarnings("ResourceType")
    private void showNotification() {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_notify)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
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

    private void updateWidgets() {
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(getPackageName());
        sendBroadcast(dataUpdatedIntent);
    }
}
