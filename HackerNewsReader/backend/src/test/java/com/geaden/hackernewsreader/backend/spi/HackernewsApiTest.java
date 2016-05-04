package com.geaden.hackernewsreader.backend.spi;

import com.geaden.hackernewsreader.backend.domain.Story;
import com.google.appengine.repackaged.com.google.api.client.util.Lists;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalMemcacheServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.google.appengine.tools.development.testing.LocalTaskQueueTestConfig;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cache.AsyncCacheFilter;
import com.googlecode.objectify.util.Closeable;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;

import static com.geaden.hackernewsreader.backend.service.OfyService.ofy;
import static org.junit.Assert.assertEquals;

/**
 * Tests for Hackernews Api implementation.
 *
 * @author Gennady Denisov
 */
public class HackernewsApiTest {
    private HackernewsApi hackernewsApi;

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
                    .setDefaultHighRepJobPolicyUnappliedJobPercentage(100),
                    new LocalMemcacheServiceTestConfig(),
                    new LocalTaskQueueTestConfig());

    // New Objectify 5.1 Way. See https://groups.google.com/forum/#!topic/objectify-appengine/O4FHC_i7EGk
    private Closeable session;

    @Before
    public void setUp() {
        session = ObjectifyService.begin();
        helper.setUp();
        hackernewsApi = new HackernewsApi();
        dumpStories();
    }

    @After
    public void tearDown() {
        AsyncCacheFilter.complete();
        ofy().clear();
        // New Objectify 5.1 Way. See https://groups.google.com/forum/#!topic/objectify-appengine/O4FHC_i7EGk
        session.close();
        helper.tearDown();
    }

    @Test
    public void testGetTopstories() throws Exception {
        Collection<Story> topStories = hackernewsApi.getTopstories();
        assertEquals(3, topStories.size());
    }

    @Test
    public void testGetStory() throws Exception {

    }

    @Test
    public void testGetStoryComments() throws Exception {

    }


    /**
     * Dumps several stories to a datastore.
     */
    private static void dumpStories() {
        List<Story> dummyStories = Lists.newArrayList();
        for (String title : new String[]{"foo", "bar", "baz"}) {
            Key<Story> key = ofy().factory().allocateId(Story.class);
            Story story = new Story(key.getId());
            story.setTitle(title);
            story.setAuthor("foo");
            story.setTime(new Date(System.currentTimeMillis() + new Random().nextInt(1000)));
            dummyStories.add(story);
        }
        ofy().save().entities(dummyStories).now();
    }

}