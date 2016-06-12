package com.geaden.android.hackernewsreader.app.data.local;

import android.content.ContentResolver;
import android.database.Cursor;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.runner.AndroidJUnit4;

import com.geaden.android.hackernewsreader.app.util.DataUtils;
import com.geaden.hackernewsreader.backend.hackernews.model.Comment;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;
import com.google.api.client.util.DateTime;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Integration test for the {@link StoriesLocalDataSource}, which uses generated content provider.
 *
 * @author Gennady Denisov
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class StoriesLocalDataSourceTest {

    StoriesLocalDataSource mStoriesDataSource;

    private static final String TEST_TITLE = "foo";
    private ContentResolver mContentResolver;

    @Before
    public void setUp() {
        mContentResolver = InstrumentationRegistry.getTargetContext().getContentResolver();
        mStoriesDataSource = new StoriesLocalDataSource(InstrumentationRegistry.getTargetContext());
    }

    @After
    public void cleanUp() {
        mContentResolver.delete(StoryModel.CONTENT_URI, null, null);
        mContentResolver.delete(BookmarkModel.CONTENT_URI, null, null);
        mContentResolver.delete(CommentModel.CONTENT_URI, null, null);
    }

    private static Story createStory() {
        Story newStory = new Story();
        newStory.setId(1L);
        newStory.setTitle(TEST_TITLE);
        newStory.setScore(42L);
        newStory.setNoComments(42);
        newStory.setTime(new DateTime(new Date()));
        return newStory;
    }

    @Test
    public void saveStory_retrievesStory() {
        // Given a new story
        final Story newStory = createStory();

        // When saved into the persistent repository
        mStoriesDataSource.saveStory(newStory);

        // Then the story can be retrieved from the persistent repository.
        Cursor savedStoryCursor = mContentResolver.query(
                StoryModel.withId(String.valueOf(newStory.getId())),
                null,
                null,
                null,
                null);

        assertNotNull(savedStoryCursor);
        savedStoryCursor.moveToFirst();

        Story savedStory = DataUtils.convertCursorToStory(savedStoryCursor).first;
        assertThat(savedStory, is(newStory));
    }

    @Test
    public void addBookmark_retrievedStoryIsBookmark() {
        // Given a new story
        final Story newStory = createStory();

        mStoriesDataSource.saveStory(newStory);

        // Add bookmark
        mStoriesDataSource.addBookmark(Long.toString(newStory.getId()));

        // Retrieve story
        StoryModel savedStory = new StoryModel();
        savedStory.id = newStory.getId();
        savedStory.load();

        assertTrue(savedStory.isBookmark());
    }

    @Test
    public void removeBookmark_retrieveStoryIsNotBookmark() {
        // Given a new story
        final Story newStory = createStory();

        mStoriesDataSource.saveStory(newStory);

        // Add bookmark
        mStoriesDataSource.addBookmark(Long.toString(newStory.getId()));

        // Remove bookmark
        mStoriesDataSource.removeBookmark(Long.toString(newStory.getId()));

        // Retrieve story
        StoryModel savedStory = new StoryModel();
        savedStory.id = newStory.getId();
        savedStory.load();

        assertFalse(savedStory.isBookmark());
    }

    @Test
    public void queryCommentsByStory() {
        final Story newStory = createStory();

        mStoriesDataSource.saveStory(newStory);

        long i = 0L;
        for (String comment : new String[]{"foo", "bar", "baz"}) {
            Comment newComment = new Comment();
            newComment.setId(++i);
            newComment.setAuthor("author");
            newComment.setTime(new DateTime(new Date()));
            newComment.setText(comment);
            mStoriesDataSource.saveComment(Long.toString(newStory.getId()), newComment);
        }

        Cursor commentCursor = ContentUtils.query(
                mContentResolver,
                CommentModel.withStoryId(newStory.getId()),
                ConditionGroup.clause(),
                null);

        assertNotNull(commentCursor);

        assertThat(commentCursor.getCount(), is(3));
    }
}