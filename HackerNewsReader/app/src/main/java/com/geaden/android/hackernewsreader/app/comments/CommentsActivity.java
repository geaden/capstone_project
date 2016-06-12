package com.geaden.android.hackernewsreader.app.comments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.geaden.android.hackernewsreader.app.R;
import com.geaden.android.hackernewsreader.app.StoriesApplication;
import com.geaden.android.hackernewsreader.app.data.LoaderProvider;
import com.geaden.android.hackernewsreader.app.data.StoriesRepository;
import com.geaden.android.hackernewsreader.app.util.ActivityUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Screen for list of comments.
 *
 * @author Gennady Denisov
 */
public class CommentsActivity extends AppCompatActivity {

    public static final String EXTRA_STORY_ID = "extra_story_id";
    public static final String EXTRA_STORY_TITLE = "extra_story_title";

    private StoriesRepository mStoriesRepository;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    private CommentsPresenter mCommentsPresenter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_act);

        ButterKnife.bind(this);

        // Get the requested story id and story title.
        Intent intent = getIntent();
        String storyId = intent.getStringExtra(EXTRA_STORY_ID);
        String storyTitle = intent.getStringExtra(EXTRA_STORY_TITLE);

        mStoriesRepository = ((StoriesApplication) getApplication())
                .getStoriesRepositoryComponent()
                .getStoriesRepository();

        // Set up the toolbar
        setSupportActionBar(mToolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(storyTitle);
        ab.setDisplayHomeAsUpEnabled(true);

        CommentsFragment commentsFragment =
                (CommentsFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);

        if (commentsFragment == null) {
            // Create the fragment
            commentsFragment = CommentsFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), commentsFragment, R.id.contentFrame);
        }

        // Create the presenter
        LoaderProvider loaderProvider = new LoaderProvider(getApplicationContext());

        mCommentsPresenter = new CommentsPresenter(
                storyId,
                loaderProvider,
                getSupportLoaderManager(),
                mStoriesRepository,
                commentsFragment
        );
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}
