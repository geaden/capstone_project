package com.geaden.android.hackernewsreader.app.storydetail;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.geaden.android.hackernewsreader.app.R;
import com.geaden.android.hackernewsreader.app.StoriesApplication;
import com.geaden.android.hackernewsreader.app.data.LoaderProvider;
import com.geaden.android.hackernewsreader.app.data.StoriesRepository;
import com.geaden.android.hackernewsreader.app.util.ActivityUtils;

/**
 * Displays story details screen.
 *
 * @author Gennady Denisov
 */
public class StoryDetailActivity extends AppCompatActivity {

    public static final String EXTRA_STORY_ID = "extra_story_id";

    /**
     * Helper method to the activity.
     *
     * @param activity   the activity to be launched.
     * @param storyId    requested storyId id.
     * @param storyImage artist's cover image.
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void launch(Activity activity, String storyId, View storyImage) {
        Intent intent = getLaunchIntent(activity, storyId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity, storyImage, storyImage.getTransitionName());
            ActivityCompat.startActivity(activity, intent, options.toBundle());
        } else {
            activity.startActivity(intent);
        }
    }

    /**
     * Helper method to get launching intent.
     *
     * @param context the Context to instantiate Intent with.
     * @param storyId requested story id to open activity for.
     * @return intent to be launched.
     */
    public static Intent getLaunchIntent(Context context, String storyId) {
        Intent intent = new Intent(context, StoryDetailActivity.class);
        intent.putExtra(EXTRA_STORY_ID, storyId);
        return intent;
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.storydetail_act);

        // Get the requested task id
        String storyId = getIntent().getStringExtra(EXTRA_STORY_ID);

        StoryDetailFragment storyDetailFragment = (StoryDetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (storyDetailFragment == null) {
            storyDetailFragment = StoryDetailFragment.newInstance(storyId);

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    storyDetailFragment, R.id.contentFrame);
        }

        StoriesRepository storiesRepository = ((StoriesApplication) getApplication())
                .getStoriesRepositoryComponent()
                .getStoriesRepository();

        // Create the presenter
        LoaderProvider loaderProvider = new LoaderProvider(getApplicationContext());

        // Create the presenter
        new StoryDetailPresenter(
                storyId,
                loaderProvider,
                storiesRepository,
                storyDetailFragment,
                getSupportLoaderManager());
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
