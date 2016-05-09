package com.geaden.android.hackernewsreader.app.stories;

import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;

import com.geaden.android.hackernewsreader.app.data.StoriesLoader;
import com.geaden.android.hackernewsreader.app.data.StoriesRepository;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;
import com.google.api.client.util.Lists;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

/**
 * Unit test for the implementation of {@link StoriesPresenter}.
 *
 * @author Gennady Denisov
 */
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
    private ArgumentCaptor<List> mShowStoriesArgumentCaptor;

    @Mock
    private StoriesLoader mStoriesLoader;

    @Mock
    private LoaderManager mLoaderManager;

    @Mock
    private Loader mLoader;

    private StoriesPresenter mStoriesPresenter;

    @Before
    public void setupStoriesPresenter() {
        // Inject mocks.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under under test.
        mStoriesPresenter = new StoriesPresenter(
                mStoriesLoader, mLoaderManager, mStoriesRepository, mStoriesView);

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
    public void loadAllStoriesFromRepositoryAndLoadIntoView() {
        // When the loader finishes with stories.
        mStoriesPresenter.onLoadFinished(mLoader, STORIES);

        // Then progress indicator is hidden and all stories are shown in UI.
        verify(mStoriesView).setLoadingIndicator(false);
        verify(mStoriesView).showStories(mShowStoriesArgumentCaptor.capture());
        assertThat(mShowStoriesArgumentCaptor.getValue().size(), is(3));
    }

    @Test
    public void clickOnStory_ShowsDetailUi() {
        // Given a stubbed story
        Story requestedStory = new Story();
        requestedStory.setId(1L);
        requestedStory.setTitle("Details Requested");
        requestedStory.setContent("For this story");

        // When open story details is requested
        mStoriesPresenter.openStoryDetails(requestedStory);

        // Then story detail UI is shown
        verify(mStoriesView).showStoryDetailsUi(any(String.class));
    }

    @Test
    public void unavailableStories_ShowsError() {
        // When the loader finishes with error
        mStoriesPresenter.setFiltering(StoriesFilterType.BOOKMARKED_STORIES);
        mStoriesPresenter.onLoadFinished(mLoader, null);

        // Then an error message is shown
        verify(mStoriesView).showLoadingStoriesError();
    }

}