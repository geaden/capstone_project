package com.geaden.android.hackernewsreader.app.comments;

import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.geaden.android.hackernewsreader.app.data.LoaderProvider;
import com.geaden.android.hackernewsreader.app.data.MockCursorProvider;
import com.geaden.android.hackernewsreader.app.data.StoriesRepository;
import com.geaden.hackernewsreader.backend.hackernews.model.Comment;
import com.google.api.client.util.Lists;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.verify;

/**
 * Unit-test for implementation of {@link CommentsPresenter}.
 *
 * @author Gennady Denisov
 */
public class CommentsPresenterTest {
    @Mock
    private CommentsContract.View mCommentsView;

    @Mock
    private StoriesRepository mStoriesRepository;

    @Mock
    private LoaderProvider mLoaderProvider;

    @Mock
    private LoaderManager mLoaderManager;

    @Mock
    private Loader mLoader;

    private CommentsPresenter mCommentsPresenter;

    private ArrayList<Comment> COMMENTS;

    private MockCursorProvider.CommentMockCursor mCommentsCursor;

    @Captor
    private ArgumentCaptor<Cursor> mShowCommentsArgumentCaptor;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        // Initialize stories
        COMMENTS = Lists.newArrayList();

        for (String text : new String[]{"foo", "bar", "baz"}) {
            Comment comment = new Comment();
            comment.setText(text);
            comment.setAuthor("foo");
            COMMENTS.add(comment);
        }

        mCommentsCursor = MockCursorProvider.createCommentsCursor();
    }

    @Test
    public void loadCommentsIntoView() {
        mCommentsPresenter = new CommentsPresenter("1", mLoaderProvider,
                mLoaderManager, mStoriesRepository, mCommentsView);

        mCommentsPresenter.onLoadFinished(mLoader, mCommentsCursor);

        verify(mCommentsView).setLoadingIndicator(false);

        verify(mCommentsView).showComments(mShowCommentsArgumentCaptor.capture());

        assertThat(mShowCommentsArgumentCaptor.getValue().getCount(), is(3));
    }


    @Test
    public void showEmptyComments() {
        mCommentsPresenter = new CommentsPresenter("1", mLoaderProvider, mLoaderManager,
                mStoriesRepository, mCommentsView);

        mCommentsPresenter.onLoadFinished(mLoader, null);

        verify(mCommentsView).showNoComments();
    }

}