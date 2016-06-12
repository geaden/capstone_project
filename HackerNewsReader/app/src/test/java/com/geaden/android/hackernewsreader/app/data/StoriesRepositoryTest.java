package com.geaden.android.hackernewsreader.app.data;

import android.content.Context;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;

/**
 * Stories repository test.
 *
 * @author Gennady Denisov
 */
public class StoriesRepositoryTest {

    private StoriesRepository mStoriesRepository;

    @Mock
    private StoriesDataSource mStoriesRemoteDataSource;

    @Mock
    private StoriesDataSource mStoriesLocalDataSource;

    @Mock
    private Context mContext;

    @Mock
    private StoriesDataSource.GetStoriesCallback mGetStoriesCallback;

    @Mock
    private StoriesDataSource.GetStoryCallback mGetStoryCallback;

    @Before
    public void setupStoriesRepository() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test
        mStoriesRepository = new StoriesRepository(mStoriesRemoteDataSource,
                mStoriesLocalDataSource);
    }

    @Test
    public void getStories_requestsAllStoriesFromRemoteDataSource() {
        // When stories are requested from the stories repository
        mStoriesRepository.getStories(mGetStoriesCallback);

        // Then stories are loaded from the remote data source
        verify(mStoriesRemoteDataSource).getStories(any(StoriesDataSource.GetStoriesCallback.class));
    }

}