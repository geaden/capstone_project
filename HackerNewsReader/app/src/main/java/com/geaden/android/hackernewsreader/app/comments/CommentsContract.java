package com.geaden.android.hackernewsreader.app.comments;

import com.geaden.android.hackernewsreader.app.BasePresenter;
import com.geaden.android.hackernewsreader.app.BaseView;
import com.geaden.hackernewsreader.backend.hackernews.model.Comment;

import java.util.List;

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

        void showComments(List<Comment> comments);

        void showNoComments();

    }

    interface Presenter extends BasePresenter {
        void loadComments(String storyId);
    }
}
