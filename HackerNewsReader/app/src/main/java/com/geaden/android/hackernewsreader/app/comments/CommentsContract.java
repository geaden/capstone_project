package com.geaden.android.hackernewsreader.app.comments;

import android.database.Cursor;

import com.geaden.android.hackernewsreader.app.BasePresenter;
import com.geaden.android.hackernewsreader.app.BaseView;

/**
 * This specifies the contract between the view and the presenter.
 *
 * @author Gennady Denisov
 */
public class CommentsContract {

    interface View extends BaseView<Presenter> {
        /**
         * Sets loading indicator state.
         *
         * @param active if true then loading will be shown.
         */
        void setLoadingIndicator(boolean active);

        void showComments(Cursor data);

        void showNoComments();

    }

    interface Presenter extends BasePresenter {
        void loadComments(String storyId, boolean forceUpdate);
    }
}
