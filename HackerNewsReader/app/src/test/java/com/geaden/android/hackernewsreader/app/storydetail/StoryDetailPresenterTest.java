package com.geaden.android.hackernewsreader.app.storydetail;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.geaden.android.hackernewsreader.app.data.StoriesRepository;
import com.geaden.android.hackernewsreader.app.data.StoryLoader;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;
import com.google.api.client.util.DateTime;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Date;

import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of {@link StoryDetailPresenter}.
 *
 * @author Gennady Denisov
 */
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
    private StoryLoader mStoryLoader;

    @Mock
    private LoaderManager mLoaderManager;

    @Mock
    private Loader mLoader;

    private StoryDetailPresenter mStoryDetailPresenter;

    private Story mStory;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mStory = createStory();
    }

    @Test
    public void getStoryFromRepositoryAndLoadIntoView() {
        mStoryDetailPresenter = new StoryDetailPresenter(Long.toString(mStory.getId()),
                mStoriesRepository, mStoryView, mStoryLoader, mLoaderManager);
        mStoryDetailPresenter.onLoadFinished(mLoader, mStory);

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
                mStoriesRepository, mStoryView, mStoryLoader, mLoaderManager);
        mStoryDetailPresenter.onLoadFinished(mStoryLoader, mStory);

        mStoryDetailPresenter.openStoryComments();

        verify(mStoryView).showStoryCommentsUi(Long.toString(mStory.getId()));
    }

    @Test
    public void bookmarkStory() {
        mStoryDetailPresenter = new StoryDetailPresenter(Long.toString(mStory.getId()),
                mStoriesRepository, mStoryView, mStoryLoader, mLoaderManager);
        mStoryDetailPresenter.onLoadFinished(mStoryLoader, mStory);

        mStoryDetailPresenter.bookmarkStory();

        verify(mStoriesRepository).bookmarkStory(Long.toString(mStory.getId()));
        verify(mStoryView).showStoryBookmarked();
    }

    @Test
    public void unbookmarkStory() {
        mStoryDetailPresenter = new StoryDetailPresenter(Long.toString(mStory.getId()),
                mStoriesRepository, mStoryView, mStoryLoader, mLoaderManager);
        mStoryDetailPresenter.onLoadFinished(mStoryLoader, mStory);

        mStoryDetailPresenter.unbookmarkStory();

        verify(mStoriesRepository).unbookmarkStory(Long.toString(mStory.getId()));
        verify(mStoryView, atLeast(2)).showStoryNotBookmarked();
    }

    @Test
    public void shareStory() {
        mStoryDetailPresenter = new StoryDetailPresenter(Long.toString(mStory.getId()),
                mStoriesRepository, mStoryView, mStoryLoader, mLoaderManager);

        mStoryDetailPresenter.onLoadFinished(mStoryLoader, mStory);

        mStoryDetailPresenter.shareStory();

        verify(mStoryView).launchShareStoryIntent(mStory.getUrl());
    }

    @Test
    public void openOriginalStory() {
        mStoryDetailPresenter = new StoryDetailPresenter(Long.toString(mStory.getId()),
                mStoriesRepository, mStoryView, mStoryLoader, mLoaderManager);

        mStoryDetailPresenter.onLoadFinished(mStoryLoader, mStory);

        mStoryDetailPresenter.openOriginalStory();

        verify(mStoryView).launchOriginalStoryIntent(mStory.getUrl());
    }

    @Test
    public void invalidStoryIsNotShown() {
        mStoryDetailPresenter = new StoryDetailPresenter(INVALID_STORY_ID,
                mStoriesRepository, mStoryView, mStoryLoader, mLoaderManager);

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