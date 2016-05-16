package com.geaden.android.hackernewsreader.app.stories;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.geaden.android.hackernewsreader.app.R;
import com.geaden.android.hackernewsreader.app.storydetail.StoryDetailActivity;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Displays a grid of {@link Story}s. User can choose to view all or bookmarked stories.
 *
 * @author Gennady Denisov
 */
public class StoriesFragment extends Fragment implements StoriesContract.View {

    private StoriesContract.Presenter mPresenter;

    private StoriesAdapter mStoriesAdapter;

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.comments_list)
    RecyclerView mRecyclerView;

    @Bind(R.id.noStories)
    View mNoStoriesView;

    public StoriesFragment() {
        // Requires empty public constructor
    }

    public static StoriesFragment newInstance() {
        return new StoriesFragment();
    }

    // Open artist details on click.
    StoryItemListener mItemListener = new StoryItemListener() {
        @Override
        public void onStoryClicked(Story clickedStory, View storyImage) {
            mPresenter.openStoryDetails(clickedStory, storyImage);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStoriesAdapter = new StoriesAdapter(getContext(), new ArrayList<Story>(0), mItemListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.stories_frag, container, false);
        ButterKnife.bind(this, root);

        int numColumns = getContext().getResources().getInteger(R.integer.num_artists_columns);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), numColumns));
        mRecyclerView.setAdapter(mStoriesAdapter);

        // Pull-to-refresh
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadStories(false);
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(StoriesContract.Presenter presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Override
    public void setLoadingIndicator(final boolean active) {

        if (getView() == null) {
            return;
        }

        // Make sure setRefreshing() is called after the layout is done with everything else.
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(active);
            }
        });

    }

    @Override
    public void showStories(List<Story> stories) {
        mStoriesAdapter.replaceData(stories);

        mRecyclerView.setVisibility(View.VISIBLE);
        mNoStoriesView.setVisibility(View.GONE);
    }

    @Override
    public void showStoryDetailsUi(String storyId, View storyImage) {
        // in it's own Activity, since it makes more sense that way and it gives us the flexibility
        // to show some Intent stubbing.
        StoryDetailActivity.launch(getActivity(), storyId, storyImage);
    }

    @Override
    public void showLoadingStoriesError() {
        showMessage(getString(R.string.loading_stories_error));
    }

    private void showMessage(String message) {
        Snackbar.make(getView(), message, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showNoStories() {
        mRecyclerView.setVisibility(View.GONE);
        mNoStoriesView.setVisibility(View.VISIBLE);
    }

    @Override
    public void showNoBookmarkedStories() {

    }

    /**
     * Recycler view adapter to display stories.
     */
    static class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.ViewHolder> {

        private final Context mContext;
        private List<Story> mStories;
        private StoryItemListener mItemListener;

        public StoriesAdapter(Context context, List<Story> artists, StoryItemListener itemListener) {
            mContext = context;
            setList(artists);
            mItemListener = itemListener;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = LayoutInflater.from(context);
            View artistView = inflater.inflate(R.layout.item_story, parent, false);

            return new ViewHolder(artistView, mItemListener);
        }

        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {
            Story story = mStories.get(position);

            viewHolder.storyTitle.setText(story.getTitle());
            viewHolder.storyScore.setText(String.format("%s", story.getScore()));
            // TODO: Set story number of comments...
            viewHolder.storyComments.setText("42");
            // TODO: Check if story is bookmarked
            boolean storyIsBookmarked = false;
            if (storyIsBookmarked) {
                viewHolder.storyBookmark.setVisibility(View.VISIBLE);
            } else {
                viewHolder.storyBookmark.setVisibility(View.GONE);
            }

            // This app uses Glide for image loading
            if (null != story.getImageUrl()) {
                Glide.with(mContext)
                        .load(story.getImageUrl())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(new GlideDrawableImageViewTarget(viewHolder.storyImage) {
                            @Override
                            public void onResourceReady(GlideDrawable resource,
                                                        GlideAnimation<? super GlideDrawable> animation) {
                                super.onResourceReady(resource, animation);
                            }
                        });
            } else {
                // TODO: Load random image for the story then...
            }

        }

        @Override
        public int getItemCount() {
            return mStories.size();
        }

        private void setList(List<Story> artists) {
            mStories = checkNotNull(artists);
        }

        public Story getItem(int position) {
            return mStories.get(position);
        }

        /**
         * Replaces data and notifies about data set change. Alternative to CursorAdapter#swapData.
         *
         * @param stories list or stories to replace.
         */
        public void replaceData(@NonNull List<Story> stories) {
            setList(stories);
            notifyDataSetChanged();
        }

        /**
         * Use of ViewHolder pattern.
         */
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
            @Bind(R.id.story_title)
            TextView storyTitle;

            @Bind(R.id.story_score)
            TextView storyScore;

            @Bind(R.id.story_comments)
            TextView storyComments;

            @Bind(R.id.story_image)
            ImageView storyImage;

            @Bind(R.id.story_bookmark)
            ImageView storyBookmark;

            private StoryItemListener mItemListener;

            public ViewHolder(View itemView, StoryItemListener listener) {
                super(itemView);
                ButterKnife.bind(this, itemView);
                mItemListener = listener;
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = getAdapterPosition();
                Story artist = getItem(position);
                mItemListener.onStoryClicked(artist, storyImage);
            }
        }
    }

    /**
     * Listener for story item clicks.
     */
    interface StoryItemListener {
        void onStoryClicked(Story clickedStory, View storyImage);
    }
}
