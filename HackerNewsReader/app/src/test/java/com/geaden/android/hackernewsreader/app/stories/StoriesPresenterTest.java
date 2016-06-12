package com.geaden.android.hackernewsreader.app.stories;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.View;

import com.geaden.android.hackernewsreader.app.data.LoaderProvider;
import com.geaden.android.hackernewsreader.app.data.MockCursorProvider;
import com.geaden.android.hackernewsreader.app.data.StoriesRepository;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;
import com.google.api.client.util.Lists;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Unit test for the implementation of {@link StoriesPresenter}.
 *
 * @author Gennady Denisov
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Bundle.class)
public class StoriesPresenterTest {

    private static List<Story> STORIES;

    @Mock
    private StoriesRepository mStoriesRepository;

    @Mock
    private StoriesContract.View mStoriesView;

    /**
     * {@link ArgumentCaptor} is a powerful Mockito API to capture argument values and use them to
     * perform further actions or assertions on them.
     */
    @Captor
    private ArgumentCaptor<Cursor> mShowStoriesArgumentCaptor;

    @Mock
    private LoaderManager mLoaderManager;

    @Mock
    private View mStoryImageView;

    @Mock
    private Bundle mBundle;

    @Mock
    private Loader mLoader;

    @Mock
    private LoaderProvider mLoaderProvider;

    private StoriesPresenter mStoriesPresenter;

    private MockCursorProvider.StoryMockCursor mAllStoriesCursor;
    private MockCursorProvider.StoryMockCursor mBookmarksCursor;
    private MockCursorProvider.StoryMockCursor mEmptyStoriesCursor;
    private MockCursorProvider.StoryMockCursor mQueryStoriesCursor;

    @Before
    public void setupStoriesPresenter() throws Exception {
        // Inject mocks.
        MockitoAnnotations.initMocks(this);

        // Given a story filter
        when(mBundle.getSerializable(StoriesFilter.KEY_STORIES_FILTER)).thenReturn(
                StoriesFilterType.ALL_STORIES);
        StoriesFilter storiesFilter = new StoriesFilter(mBundle);

        // Get a reference to the class under under test.
        mStoriesPresenter = new StoriesPresenter(
                mLoaderProvider, mLoaderManager, mStoriesRepository, mStoriesView, storiesFilter);


        // Initialize cursors
        mAllStoriesCursor = MockCursorProvider.createAllStoriesCursor();
        mBookmarksCursor = MockCursorProvider.createBookmarksCursor();
        mEmptyStoriesCursor = MockCursorProvider.createEmptyStoriesCursor();
        mQueryStoriesCursor = MockCursorProvider.createQueryStoriesCursor();

        PowerMockito.whenNew(Bundle.class).withAnyArguments().thenReturn(mBundle);

        // Initialize stories
        STORIES = Lists.newArrayList();

        for (String title : new String[]{"foo", "bar", "baz"}) {
            Story story = new Story();
            story.setTitle(title);
            story.setContent("lorem ipsum");
            story.setAuthor("far");
            STORIES.add(story);
        }
    }

    @Test
    public void forceLoadAllStoriesRefreshesDataFromRepository() {
        mStoriesPresenter.loadStories(true);

        // Then the repository refreshes the data
        verify(mStoriesRepository).getStories(any(StoriesRepository.GetStoriesCallback.class));
    }

    @Test
    public void loadAllStoriesRepositoryNotCalled() {
        // When the loader finishes with stories.
        mStoriesPresenter.mFirstLoad = false;
        mStoriesPresenter.loadStories(false);

        // Then the repository does not refresh the data
        verifyZeroInteractions(mStoriesRepository);
    }

    @Test
    public void loadAllStoriesInitsLoaderFirstTime() {
        mStoriesPresenter.loadStories(true);

        when(mLoaderManager.getLoader(StoriesPresenter.STORIES_LOADER)).thenReturn(null);
        verify(mLoaderManager).initLoader(
                anyInt(), any(Bundle.class), any(LoaderManager.LoaderCallbacks.class));
    }

    @Test
    public void loadAllStoriesFromRepositoryAndLoadIntoView() {
        // When the loader finishes with stories and filter is set to all
        when(mBundle.getSerializable(StoriesFilter.KEY_STORIES_FILTER))
                .thenReturn(StoriesFilterType.ALL_STORIES);
        StoriesFilter storiesFilter = new StoriesFilter(mBundle);

        mStoriesPresenter.setFiltering(storiesFilter);

        mStoriesPresenter.onLoadFinished(mLoader, mAllStoriesCursor);

        // Then progress indicator is hidden and all tasks are shown in UI
        verify(mStoriesView).setLoadingIndicator(false);
        verify(mStoriesView).showStories(mShowStoriesArgumentCaptor.capture());
        assertThat(mShowStoriesArgumentCaptor.getValue().getCount(), is(5));
    }

    @Test
    public void loadAllStoriesReturnsNothingShowsEmptyMessage() {
        // When the loader finishes with stories and filter is set to bookmarks
        when(mBundle.getSerializable(StoriesFilter.KEY_STORIES_FILTER))
                .thenReturn(StoriesFilterType.ALL_STORIES);
        StoriesFilter storiesFilter = new StoriesFilter(mBundle);

        mStoriesPresenter.setFiltering(storiesFilter);
        mStoriesPresenter.onLoadFinished(mLoader, mEmptyStoriesCursor);

        // Then progress indicator is hidden and completed tasks are shown in UI
        verify(mStoriesView).setLoadingIndicator(false);
        verify(mStoriesView).showNoStories();
    }

    @Test
    public void loadBookmarksReturnsNothingShowsEmptyMessage() {
        // When the loader finishes with stories and filter is set to bookmarks
        when(mBundle.getSerializable(StoriesFilter.KEY_STORIES_FILTER))
                .thenReturn(StoriesFilterType.BOOKMARKED_STORIES);
        StoriesFilter storiesFilter = new StoriesFilter(mBundle);

        mStoriesPresenter.setFiltering(storiesFilter);
        mStoriesPresenter.onLoadFinished(mLoader, mEmptyStoriesCursor);

        // Then progress indicator is hidden and completed tasks are shown in UI
        verify(mStoriesView).setLoadingIndicator(false);
        verify(mStoriesView).showNoBookmarks();
    }


    @Test
    public void loadBookmarksFromRepositoryAndLoadIntoView() {
        // When the loader finishes with stories and filter is set to bookmarks
        when(mBundle.getSerializable(StoriesFilter.KEY_STORIES_FILTER))
                .thenReturn(StoriesFilterType.BOOKMARKED_STORIES);
        StoriesFilter storiesFilter = new StoriesFilter(mBundle);

        mStoriesPresenter.setFiltering(storiesFilter);
        mStoriesPresenter.onLoadFinished(mLoader, mBookmarksCursor);

        // Then progress indicator is hidden and bookmarks are shown in UI
        verify(mStoriesView).setLoadingIndicator(false);
        verify(mStoriesView).showStories(mShowStoriesArgumentCaptor.capture());
        verify(mStoriesView).showBookmarksFilterLabel();
        assertThat(mShowStoriesArgumentCaptor.getValue().getCount(), is(3));
    }

    @Test
    @Ignore
    public void queryByNameAllStoriesAndLoadIntoView() {
        // When the loader finishes with stories by name and filter is set to all
        when(mBundle.getSerializable(StoriesFilter.KEY_STORIES_FILTER))
                .thenReturn(StoriesFilterType.BOOKMARKED_STORIES);
        StoriesFilter storiesFilter = new StoriesFilter(mBundle);
        mStoriesPresenter.setFiltering(storiesFilter);
        mStoriesPresenter.loadStoriesByName("foo");
        verify(mBundle).putString(StoriesPresenter.STORY_QUERY, "foo");
    }

    @Test
    public void clickOnStory_ShowsDetailUi() {
        // Given a stubbed story
        Story requestedStory = new Story();
        requestedStory.setId(1L);
        requestedStory.setTitle("Details Requested");
        requestedStory.setContent("For this story");

        // When open story details is requested
        mStoriesPresenter.openStoryDetails(requestedStory, mStoryImageView);

        // Then story detail UI is shown
        verify(mStoriesView).showStoryDetailsUi("1", mStoryImageView);
    }

    @Test
    public void unavailableStories_ShowsError() {
        // When the loader finishes with tasks and filter is set to completed
        when(mBundle.getSerializable(StoriesFilter.KEY_STORIES_FILTER))
                .thenReturn(StoriesFilterType.ALL_STORIES);
        StoriesFilter storiesFilter = new StoriesFilter(mBundle);

        // When the loader finishes with the error.
        mStoriesPresenter.setFiltering(storiesFilter);
        mStoriesPresenter.onLoadFinished(mLoader, null);

        // Then an error message is shown
        verify(mStoriesView).showLoadingStoriesError();
    }
}