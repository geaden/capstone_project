package com.geaden.android.hackernewsreader.app.comments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.geaden.android.hackernewsreader.app.R;
import com.geaden.android.hackernewsreader.app.util.DataUtils;
import com.geaden.android.hackernewsreader.app.util.Utils;
import com.geaden.hackernewsreader.backend.hackernews.model.Comment;

import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;

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
        mListAdapter = new CommentsAdapter(getActivity(), null, 0);
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
    public void showComments(Cursor data) {
        mListAdapter.swapCursor(data);
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

    public static class CommentsAdapter extends CursorAdapter {

        public CommentsAdapter(Context context, Cursor c, int flags) {
            super(context, c, flags);
        }

        @Override
        public View newView(Context context, Cursor cursor, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View view = inflater.inflate(R.layout.item_comment, parent, false);
            ViewHolder vh = new ViewHolder(view);
            view.setTag(vh);
            return view;
        }

        @Override
        public Cursor swapCursor(Cursor newCursor) {
            if (newCursor == mCursor) {
                return null;
            }
            Cursor oldCursor = mCursor;
            if (oldCursor != null) {
                if (mChangeObserver != null) oldCursor.unregisterContentObserver(mChangeObserver);
                if (mDataSetObserver != null) oldCursor.unregisterDataSetObserver(mDataSetObserver);
            }
            mCursor = newCursor;
            if (newCursor != null) {
                if (mChangeObserver != null) newCursor.registerContentObserver(mChangeObserver);
                if (mDataSetObserver != null) newCursor.registerDataSetObserver(mDataSetObserver);
                mRowIDColumn = newCursor.getColumnIndexOrThrow("id");
                mDataValid = true;
                // notify the observers about the new cursor
                notifyDataSetChanged();
            } else {
                mRowIDColumn = -1;
                mDataValid = false;
                // notify the observers about the lack of a data set
                notifyDataSetInvalidated();
            }
            return oldCursor;
        }

        @Override
        public void bindView(View view, Context context, Cursor cursor) {
            ViewHolder vh = (ViewHolder) view.getTag();
            final Comment comment = DataUtils.convertCursorToComment(cursor);
            vh.commentAuthor.setText(comment.getAuthor());
            long now = new Date().getTime();
            vh.commentTime.setText(Utils.getRelativeTime(now, comment.getTime().getValue()));
            vh.commentText.setText(Html.fromHtml(comment.getText() != null ? comment.getText() : ""));
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
