package com.geaden.android.hackernewsreader.app.comments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.geaden.android.hackernewsreader.app.R;
import com.geaden.android.hackernewsreader.app.util.Utils;
import com.geaden.hackernewsreader.backend.hackernews.model.Comment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Displays list of {@link Comment}s for a story.
 *
 * @author Gennady Denisov
 */
public class CommentsFragment extends Fragment implements CommentsContract.View {

    private CommentsContract.Presenter mPresenter;

    @Bind(R.id.comments_list)
    ListView mCommentsList;

    @Bind(R.id.refresh_layout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    @Bind(R.id.noComments)
    View mNoCommentsView;

    private CommentsAdapter mListAdapter;

    public CommentsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mListAdapter = new CommentsAdapter(new ArrayList<Comment>(0));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setRetainInstance(true);
    }

    public static CommentsFragment newInstance() {
        return new CommentsFragment();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.comments_frag, container, false);

        ButterKnife.bind(this, root);

        mCommentsList.setAdapter(mListAdapter);

        mSwipeRefreshLayout.setColorSchemeColors(
                ContextCompat.getColor(getActivity(), R.color.colorPrimary),
                ContextCompat.getColor(getActivity(), R.color.colorAccent),
                ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));

        // Pull-to-refresh
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mPresenter.start();
            }
        });

        return root;
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
    public void showComments(List<Comment> comments) {
        mListAdapter.replaceData(comments);

        mCommentsList.setVisibility(View.VISIBLE);
        mNoCommentsView.setVisibility(View.GONE);

    }

    @Override
    public void showNoComments() {
        mCommentsList.setVisibility(View.GONE);
        mNoCommentsView.setVisibility(View.VISIBLE);

    }

    @Override
    public void setPresenter(CommentsContract.Presenter presenter) {
        mPresenter = presenter;
    }

    public static class CommentsAdapter extends BaseAdapter {

        private List<Comment> mComments;

        public CommentsAdapter(List<Comment> comments) {
            setList(comments);
        }

        public void replaceData(List<Comment> comments) {
            setList(comments);
            notifyDataSetChanged();
        }

        private void setList(List<Comment> comments) {
            mComments = checkNotNull(comments);
        }

        @Override
        public int getCount() {
            return mComments.size();
        }

        @Override
        public Comment getItem(int i) {
            return mComments.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View rowView = view;

            ViewHolder vh;

            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
                rowView = inflater.inflate(R.layout.item_comment, viewGroup, false);
                vh = new ViewHolder(rowView);
                rowView.setTag(vh);
            } else {
                vh = (ViewHolder) rowView.getTag();
            }

            final Comment comment = getItem(i);
            vh.commentAuthor.setText(comment.getAuthor());
            long now = new Date().getTime();
            vh.commentTime.setText(Utils.getRelativeTime(now, comment.getTime().getValue()));
            vh.commentText.setText(Html.fromHtml(comment.getText()));

            return rowView;
        }

        static class ViewHolder {

            @Bind(R.id.comment_author)
            TextView commentAuthor;

            @Bind(R.id.comment_time)
            TextView commentTime;

            @Bind(R.id.comment_text)
            TextView commentText;

            public ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

        }
    }

}
