package com.geaden.android.hackernewsreader.app.storydetail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.geaden.android.hackernewsreader.app.R;
import com.geaden.android.hackernewsreader.app.comments.CommentsActivity;
import com.geaden.android.hackernewsreader.app.util.Utils;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Main UI for the story detail screen.
 *
 * @author Gennady Denisov
 */
public class StoryDetailFragment extends Fragment implements StoryDetailContract.View {

    public static final String EXTRA_STORY_ID = "story_id";

    private StoryDetailContract.Presenter mPresenter;

    @Bind(R.id.fab_share_story)
    FloatingActionButton mStoryShareFab;

    @Bind(R.id.story_image)
    ImageView mStoryImage;

    @Bind(R.id.story_title)
    TextView mStoryTitle;

    @Bind(R.id.story_author)
    TextView mStoryAuthor;

    @Bind(R.id.story_time)
    TextView mStoryTime;

    @Bind(R.id.story_content)
    TextView mStoryContent;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.story_detail_view_original)
    Button mOriginalButton;

    @Bind(R.id.appbar)
    AppBarLayout mAppBar;

    @Bind(R.id.collapsing_toolbar)
    CollapsingToolbarLayout mCollapsingToolbar;

    @Bind(R.id.story_bookmark_action)
    ImageButton mStoryBookmark;

    private String mTitle;

    public StoryDetailFragment() {
        setHasOptionsMenu(true);
    }

    public static StoryDetailFragment newInstance(String storyId) {
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_STORY_ID, storyId);
        StoryDetailFragment fragment = new StoryDetailFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.storydetail_frag, container, false);
        ButterKnife.bind(this, root);

        getActivityCast().setSupportActionBar(mToolbar);

        ActionBar ab = getActivityCast().getSupportActionBar();

        if (null != ab) {
            ab.setTitle("");
            ab.setDisplayHomeAsUpEnabled(true);
        }

        mStoryBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleStoryBookmark();
            }
        });

        mStoryShareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.shareStory();
            }
        });

        // Add scroll listener to show toolbar title when collapsed.
        mAppBar.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShown = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    mCollapsingToolbar.setTitle(mTitle);
                    mToolbar.setBackground(null);
                    hideStoryTitle();
                    mStoryTitle.setBackgroundColor(ContextCompat.getColor(getActivity(),
                            android.R.color.transparent));
                    isShown = true;
                } else if (isShown) {
                    mCollapsingToolbar.setTitle(null);
                    mStoryTitle.setBackgroundColor(ContextCompat.getColor(getActivity(),
                            R.color.story_detail_title_background));
                    showStoryTitle(mTitle);
                    isShown = false;
                }
            }
        });

        mOriginalButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.openOriginalStory();
            }
        });

        return root;
    }

    private void toggleStoryBookmark() {
        boolean storyBookmarked = Utils.checkIfBookmarked(Long.valueOf(
                getArguments().getString(EXTRA_STORY_ID)));
        if (!storyBookmarked) {
            mPresenter.bookmarkStory();
        } else {
            mPresenter.unbookmarkStory();
        }
    }

    private StoryDetailActivity getActivityCast() {
        return (StoryDetailActivity) getActivity();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_story_comments:
                mPresenter.openStoryComments();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.story_detail_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void setLoadingIndicator(boolean active) {
        if (active) {
            mStoryContent.setText("");
            mStoryContent.setText(getString(R.string.loading));
        }
    }

    @Override
    public void showMissingStory() {
        mStoryTitle.setText("");
        mStoryContent.setText(getString(R.string.no_data));
    }

    @Override
    public void showStoryCommentsUi(String storyId, String storyTitle) {
        Intent intent = new Intent(getActivity(), CommentsActivity.class);
        intent.putExtra(CommentsActivity.EXTRA_STORY_ID, storyId);
        intent.putExtra(CommentsActivity.EXTRA_STORY_TITLE, storyTitle);
        getActivity().startActivity(intent);
    }

    @Override
    public void showStoryImage(@NonNull String imageUrl) {
        Glide.with(getActivity())
                .load(imageUrl)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(mStoryImage);
    }

    @Override
    public void showStoryImage(int resourceId) {
        mStoryImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), resourceId));
    }

    @Override
    public void showStoryTitle(String title) {
        mTitle = title;
        mStoryTitle.setText(title);
    }

    @Override
    public void hideStoryTitle() {
        mStoryTitle.setText("");
    }

    @Override
    public void showStoryAuthor(String author) {
        mStoryAuthor.setText(getString(R.string.story_by, author));
    }

    @Override
    public void showStoryTime(Date date) {
        long now = new Date().getTime();
        CharSequence ago = Utils.getRelativeTime(now, date.getTime());
        mStoryTime.setText(ago);
    }

    @Override
    public void showStoryContent(String content) {
        mStoryContent.setText(content);
    }

    @Override
    public void showStoryBookmarked() {
        mStoryBookmark.setImageDrawable(
                ContextCompat.getDrawable(getActivity(), R.drawable.ic_bookmark_white_24px));
        mStoryBookmark.setTag(R.drawable.ic_bookmark_white_24px);
    }

    @Override
    public void showStoryNotBookmarked() {
        mStoryBookmark.setImageDrawable(
                ContextCompat.getDrawable(getActivity(), R.drawable.ic_bookmark_border_white_24px));
        mStoryBookmark.setTag(R.drawable.ic_bookmark_border_white_24px);

    }

    @Override
    public void launchShareStoryIntent(String url) {
        Intent shareIntent = ShareCompat.IntentBuilder.from(getActivity())
                .setText(url)
                .setType("text/plain")
                .setChooserTitle(R.string.share_story_intent_title)
                .createChooserIntent();
        getActivity().startActivity(shareIntent);
    }

    @Override
    public void launchOriginalStoryIntent(final String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            getActivity().startActivity(intent);
        }
    }

    @Override
    public void hideStoryContent() {
        mStoryContent.setText("");
    }

    @Override
    public void hideStoryAuthor() {
        mStoryAuthor.setText("");
    }

    @Override
    public void hideStoryTime() {
        mStoryTime.setText("");
    }

    @Override
    public void setPresenter(StoryDetailContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter, "presenter cannot be null!");
    }
}
