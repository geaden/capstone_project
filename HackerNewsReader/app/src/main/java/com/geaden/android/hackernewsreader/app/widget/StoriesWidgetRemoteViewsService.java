package com.geaden.android.hackernewsreader.app.widget;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Binder;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;
import com.geaden.android.hackernewsreader.app.R;
import com.geaden.android.hackernewsreader.app.data.local.StoryModel;
import com.geaden.android.hackernewsreader.app.data.local.StoryModel_Table;
import com.geaden.android.hackernewsreader.app.storydetail.StoryDetailActivity;
import com.geaden.android.hackernewsreader.app.util.DataUtils;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

import java.util.concurrent.ExecutionException;

/**
 * RemoteViewsService controlling the data being shown in the scrollable widget.
 *
 * @author Gennady Denisov
 */
public class StoriesWidgetRemoteViewsService extends RemoteViewsService {

    private static final String TAG = "StoriesWidgetRVS";

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;

            @Override
            public void onCreate() {
                // no-op
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                // This method is called by the app hosting the widget (e.g., the launcher)
                // However, our ContentProvider is not exported so it doesn't have access to the
                // data. Therefore we need to clear (and finally restore) the calling identity so
                // that calls use our process and permission
                final long identityToken = Binder.clearCallingIdentity();
                data = ContentUtils.query(getContentResolver(), StoryModel.CONTENT_URI,
                        ConditionGroup.clause(), StoryModel_Table.score + " DESC");
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }

            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int position) {
                if (position == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(position)) {
                    return null;
                }
                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_stories_list_item);

                final Story story = DataUtils.convertCursorToStory(data).first;

                Bitmap storyImage = null;
                if (story.getImageUrl() != null) {
                    try {
                        storyImage = Glide.with(StoriesWidgetRemoteViewsService.this)
                                .load(story.getImageUrl())
                                .asBitmap()
                                .error(R.drawable.story_image_default)
                                .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();

                    } catch (InterruptedException | ExecutionException e) {
                        Log.e(TAG, "Error retrieving large icon from " + story.getImageUrl(), e);
                    }
                }

                views.setTextViewText(R.id.widget_title, story.getTitle());
                views.setTextViewText(R.id.widget_score, String.format("%s", story.getScore()));
                views.setTextViewText(R.id.widget_comments, String.format("%s", story.getNoComments()));

                if (storyImage != null) {
                    views.setImageViewBitmap(R.id.widget_image, storyImage);
                } else {
                    views.setImageViewResource(R.id.widget_image, R.drawable.story_image_default);
                }

                setRemoteContentDescription(views, story.getTitle());

                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra(StoryDetailActivity.EXTRA_STORY_ID, Long.toString(story.getId()));
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_stories_list_item);
            }

            private void setRemoteContentDescription(RemoteViews views, String description) {
                views.setContentDescription(R.id.widget_image, description);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int position) {
                if (data.moveToPosition(position))
                    return DataUtils.convertCursorToStory(data).first.getId();
                return position;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }
        };
    }
}
