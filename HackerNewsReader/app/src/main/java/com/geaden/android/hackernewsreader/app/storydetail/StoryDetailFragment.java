package com.geaden.android.hackernewsreader.app.storydetail;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

    @Bind(R.id.story_bookmark_action)
    ImageButton mStoryBookmark;

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

        mStoryShareFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.shareStory();
            }
        });
        return root;
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
    }

    @Override
    public void showStoryNotBookmarked() {
        mStoryBookmark.setImageDrawable(
                ContextCompat.getDrawable(getActivity(), R.drawable.ic_bookmark_border_white_24px));

    }

    @Override
    public void launchShareStoryIntent(String url) {
        Intent shareIntent = ShareCompat.IntentBuilder.from(getActivity())
                .setText(url)
                .createChooserIntent();
        getActivity().startActivity(shareIntent);
    }

    @Override
    public void launchOriginalStoryIntent(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        intent.addCategory(Intent.CATEGORY_BROWSABLE);
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
