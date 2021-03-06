package com.geaden.android.hackernewsreader.app.stories;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.geaden.android.hackernewsreader.app.R;
import com.geaden.android.hackernewsreader.app.storydetail.StoryDetailActivity;
import com.geaden.android.hackernewsreader.app.util.DataUtils;
import com.geaden.android.hackernewsreader.app.util.Utils;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Displays a grid of {@link Story}s. User can choose to view all or bookmarked stories.
 *
 * @author Gennady Denisov
 */
public class StoriesFragment extends Fragment implements StoriesContract.View,
        SharedPreferences.OnSharedPreferenceChangeListener {
    private StoriesContract.Presenter mPresenter;

    private StoriesAdapter mStoriesAdapter;

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.stories_grid)
    RecyclerView mRecyclerView;

    @Bind(R.id.noStories)
    View mNoStoriesView;

    @Bind(R.id.noStoriesMain)
    TextView mNoStoriesTextView;

    private MenuItem mSearchItem;
    private SearchView mSearchView;

    public StoriesFragment() {
        setHasOptionsMenu(true);
    }

    public static StoriesFragment newInstance() {
        return new StoriesFragment();
    }

    // Open artist details on click.
    StoryItemListener mItemListener = new StoryItemListener() {
        @Override
        public void onStoryClicked(Story clickedStory, View storyImage) {
            resetSearchView();
            mPresenter.openStoryDetails(clickedStory, storyImage);
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStoriesAdapter = new StoriesAdapter(getContext(), mItemListener);
    }

    @Override
    @SuppressWarnings("ResourceType")
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.stories_frag_menu, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getActivity().getSystemService(Context.SEARCH_SERVICE);

        mSearchItem = menu.findItem(R.id.menu_stories_search);
        mSearchView = (SearchView) mSearchItem.getActionView();

        mSearchView.setQueryHint(getString(R.string.stories_search_hint));
        mSearchView.setSearchableInfo(
                searchManager.getSearchableInfo(getActivity().getComponentName()));

        mSearchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                if (!TextUtils.isEmpty(mSearchView.getQuery())) {
                    mSearchView.setQuery(null, true);
                }
                return true;
            }
        });

        // Set search query text listener...
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextChange(String query) {
                if (!TextUtils.isEmpty(query)) {
                    mPresenter.loadStoriesByName(query);
                } else {
                    // Just load all artists.
                    mPresenter.loadStories(false);
                }
                return true;
            }

            @Override
            public boolean onQueryTextSubmit(String query) {
                // Don't care about this.
                return true;
            }
        });
    }

    /**
     * Helper methods that resets state of search view and it's menu item.
     */
    private void resetSearchView() {
        if (null != mSearchView) {
            mSearchView.setQuery(null, true);
        }
        if (null != mSearchItem) {
            MenuItemCompat.collapseActionView(mSearchItem);
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.stories_frag, container, false);
        ButterKnife.bind(this, root);

        int numColumns = getContext().getResources().getInteger(R.integer.num_stories_columns);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), numColumns));
        mRecyclerView.setAdapter(mStoriesAdapter);

        mSwipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));

        // Pull-to-refresh
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.loadStories(true);
            }
        });

        return root;
    }

    @Override
    public void onResume() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.registerOnSharedPreferenceChangeListener(this);
        super.onResume();
        mPresenter.setFiltering(Utils.getFilter(getActivity())
                ? StoriesFilter.from(StoriesFilterType.BOOKMARKED_STORIES) :
                StoriesFilter.from(StoriesFilterType.ALL_STORIES));
        mPresenter.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sp.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.pref_key_bookmarked_ony))) {
            if (Utils.getFilter(getActivity())) {
                mPresenter.setFiltering(StoriesFilter.from(StoriesFilterType.BOOKMARKED_STORIES));
            } else {
                mPresenter.setFiltering(StoriesFilter.from(StoriesFilterType.ALL_STORIES));
            }
            mPresenter.loadStories(false);
        }
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
    public void showStories(Cursor stories) {
        mStoriesAdapter.swapData(stories);
        mRecyclerView.setVisibility(View.VISIBLE);
        mNoStoriesView.setVisibility(View.GONE);

    }

    @Override
    public void showBookmarksFilterLabel() {
        ((StoriesActivity) getActivity()).showCurrentFilterLabel();
    }

    @Override
    public void showAllFilterLabel() {
        ((StoriesActivity) getActivity()).hideCurrentFilterLabel();
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
        mNoStoriesTextView.setText(R.string.no_stories_all);
    }

    @Override
    public void showNoBookmarks() {
        mRecyclerView.setVisibility(View.GONE);
        mNoStoriesView.setVisibility(View.VISIBLE);
        mNoStoriesTextView.setText(R.string.no_stories_bookmarked);
    }

    /**
     * Recycler view adapter to display stories.
     */
    static class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.ViewHolder> {

        private final Context mContext;
        private Cursor mCursor;
        private StoryItemListener mItemListener;

        public StoriesAdapter(Context context, StoryItemListener itemListener) {
            mContext = context;
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
            if (mCursor.isClosed()) {
                return;
            }
            mCursor.moveToPosition(position);

            Pair<Story, Boolean> storyBookmarksPair = DataUtils.convertCursorToStory(mCursor);

            Story story = storyBookmarksPair.first;
            boolean isBookmark = storyBookmarksPair.second;

            viewHolder.storyTitle.setText(story.getTitle());
            viewHolder.storyScore.setText(String.format("%s", story.getScore()));
            viewHolder.storyComments.setText(String.format("%s", story.getNoComments()));
            viewHolder.storyBookmark.setVisibility(isBookmark ? View.VISIBLE : View.GONE);

            // This app uses Glide for image loading
            if (null != story.getImageUrl()) {
                Glide.with(mContext)
                        .load(story.getImageUrl())
                        .asBitmap()
                        .placeholder(R.drawable.story_image_default)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(viewHolder.storyImage);
            } else {
                viewHolder.storyImage.setImageDrawable(ContextCompat.getDrawable(mContext,
                        R.drawable.story_image_default));
            }

        }

        @Override
        public int getItemCount() {
            if (null == mCursor) return 0;
            return mCursor.getCount();
        }

        public Story getItem(int position) {
            if (null == mCursor) return null;

            mCursor.moveToPosition(position);

            return DataUtils.convertCursorToStory(mCursor).first;
        }

        public Cursor getCursor() {
            return mCursor;
        }

        /**
         * Replaces data and notifies about data set change. Alternative to CursorAdapter#swapData.
         *
         * @param cursor cursor that holds {@link Story}s.
         */
        public void swapData(Cursor cursor) {
            mCursor = cursor;
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
                Story story = getItem(position);
                mItemListener.onStoryClicked(story, storyImage);
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
