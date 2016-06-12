package com.geaden.android.hackernewsreader.app.storydetail;

import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.util.Pair;

import com.geaden.android.hackernewsreader.app.data.LoaderProvider;
import com.geaden.android.hackernewsreader.app.data.MockCursorProvider;
import com.geaden.android.hackernewsreader.app.data.StoriesRepository;
import com.geaden.android.hackernewsreader.app.util.DataUtils;
import com.geaden.android.hackernewsreader.app.util.Utils;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;
import com.google.api.client.util.DateTime;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Date;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of {@link StoryDetailPresenter}.
 *
 * @author Gennady Denisov
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({Utils.class, DataUtils.class})
public class StoryDetailPresenterTest {

    private static final String TITLE_TEST = "foo";
    private static final String CONTENT_TEST = "lorem ipsum";
    private static final String AUTHOR_TEST = "bar";

    private static final String INVALID_STORY_ID = "";

    @Mock
    private StoriesRepository mStoriesRepository;

    @Mock
    private StoryDetailContract.View mStoryView;

    @Mock
    private LoaderProvider mLoaderProvider;

    @Mock
    private LoaderManager mLoaderManager;

    @Mock
    private Loader mLoader;

    private StoryDetailPresenter mStoryDetailPresenter;

    private MockCursorProvider.StoryMockCursor mStoryCursor;

    private Story mStory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(Utils.class, DataUtils.class);

        mStory = createStory();

        PowerMockito.when(DataUtils.convertCursorToStory(any(Cursor.class)))
                .thenReturn(new Pair<>(mStory, false));
        PowerMockito.when(Utils.checkIfBookmarked(anyLong(), anyList())).thenReturn(false);

        mStoryCursor = MockCursorProvider.createStoryCursor();
    }

    @Test
    public void getStoryFromRepositoryAndLoadIntoView() {
        // Get a reference to a class under test.
        mStoryDetailPresenter = new StoryDetailPresenter(Long.toString(mStory.getId()),
                mLoaderProvider, mStoriesRepository, mStoryView, mLoaderManager);

        // When tasks presenter is asked to open a story.
        mStoryDetailPresenter.onLoadFinished(mLoader, mStoryCursor);

        // Then progress indicator is hidden and title, content, author and publishing date
        // are shown in Ui.
        verify(mStoryView).setLoadingIndicator(false);
        verify(mStoryView).showStoryTitle(TITLE_TEST);
        verify(mStoryView).showStoryContent(CONTENT_TEST);
        verify(mStoryView).showStoryAuthor(AUTHOR_TEST);
    }

    @Test
    public void openStoryComments() {
        mStoryDetailPresenter = new StoryDetailPresenter(Long.toString(mStory.getId()),
                mLoaderProvider, mStoriesRepository, mStoryView, mLoaderManager);
        mStoryDetailPresenter.onLoadFinished(mLoader, mStoryCursor);

        mStoryDetailPresenter.openStoryComments();

        verify(mStoryView).showStoryCommentsUi(Long.toString(mStory.getId()), mStory.getTitle());
    }

    @Test
    public void bookmarkStory() {
        mStoryDetailPresenter = new StoryDetailPresenter(Long.toString(mStory.getId()),
                mLoaderProvider, mStoriesRepository, mStoryView, mLoaderManager);
        mStoryDetailPresenter.onLoadFinished(mLoader, mStoryCursor);

        mStoryDetailPresenter.addBookmark();

        verify(mStoriesRepository).addBookmark(Long.toString(mStory.getId()));
        verify(mStoryView).showStoryBookmarked();
    }

    @Test
    public void unbookmarkStory() {
        mStoryDetailPresenter = new StoryDetailPresenter(Long.toString(mStory.getId()),
                mLoaderProvider, mStoriesRepository, mStoryView, mLoaderManager);
        mStoryDetailPresenter.onLoadFinished(mLoader, mStoryCursor);

        mStoryDetailPresenter.removeBookmark();

        verify(mStoriesRepository).removeBookmark(Long.toString(mStory.getId()));
        verify(mStoryView, atLeast(2)).showStoryNotBookmarked();
    }

    @Test
    public void shareStory() {
        mStoryDetailPresenter = new StoryDetailPresenter(Long.toString(mStory.getId()),
                mLoaderProvider, mStoriesRepository, mStoryView, mLoaderManager);

        mStoryDetailPresenter.onLoadFinished(mLoader, mStoryCursor);

        mStoryDetailPresenter.shareStory();

        verify(mStoryView).launchShareStoryIntent(mStory.getUrl());
    }

    @Test
    public void openOriginalStory() {
        mStoryDetailPresenter = new StoryDetailPresenter(Long.toString(mStory.getId()),
                mLoaderProvider, mStoriesRepository, mStoryView, mLoaderManager);

        mStoryDetailPresenter.onLoadFinished(mLoader, mStoryCursor);

        mStoryDetailPresenter.openOriginalStory();

        verify(mStoryView).launchOriginalStoryIntent(mStory.getUrl());
    }

    @Test
    public void invalidStoryIsNotShown() {
        mStoryDetailPresenter = new StoryDetailPresenter(INVALID_STORY_ID,
                mLoaderProvider, mStoriesRepository, mStoryView, mLoaderManager);

        mStoryDetailPresenter.onLoadFinished(mLoader, null);

        verify(mStoryView).showMissingStory();
    }

    private static Story createStory() {
        Story story = new Story();
        story.setId(1L);
        story.setTitle(TITLE_TEST);
        story.setContent(CONTENT_TEST);
        story.setAuthor(AUTHOR_TEST);
        story.setUrl("http://foo/bar");
        story.setTime(new DateTime(new Date()));
        return story;
    }
}