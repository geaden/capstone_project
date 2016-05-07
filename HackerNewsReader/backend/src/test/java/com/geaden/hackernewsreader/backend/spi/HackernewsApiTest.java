package com.geaden.hackernewsreader.backend.spi;

import com.geaden.hackernewsreader.backend.config.Constants;
import com.geaden.hackernewsreader.backend.domain.Comment;
import com.geaden.hackernewsreader.backend.domain.Story;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.users.User;
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

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.geaden.hackernewsreader.backend.service.OfyService.ofy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for Hackernews Api implementation.
 *
 * @author Gennady Denisov
 */
public class HackernewsApiTest {
    private static final String EMAIL = "testuser@example.com";
    private static final String USER_ID = "123456789";

    private HackernewsApi hackernewsApi;

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
                    .setDefaultHighRepJobPolicyUnappliedJobPercentage(100),
                    new LocalMemcacheServiceTestConfig(),
                    new LocalTaskQueueTestConfig());

    private User user;

    // New Objectify 5.1 Way. See https://groups.google.com/forum/#!topic/objectify-appengine/O4FHC_i7EGk
    private Closeable session;

    @Before
    public void setUp() throws Exception {
        session = ObjectifyService.begin();
        helper.setUp();
        hackernewsApi = new HackernewsApi();
        dumpStories();
        user = new User(EMAIL, "gmail.com", USER_ID);
    }

    @After
    public void tearDown() {
        AsyncCacheFilter.complete();
        ofy().clear();
        helper.tearDown();
        // New Objectify 5.1 Way. See https://groups.google.com/forum/#!topic/objectify-appengine/O4FHC_i7EGk
        if (null != session) {
            session.close();
        }
        session = null;
    }

    @Test
    public void testGetTopstories() throws Exception {
        List<Story> topStories = hackernewsApi.getTopstories();
        assertEquals(Math.min(3, Constants.NUMBER_OF_STORIES), topStories.size());
    }

    @Test
    public void testGetStory() throws Exception {
        Story story = hackernewsApi.getTopstory(1L);
        assertNotNull(story);
        assertEquals("foo", story.getTitle());
    }

    @Test(expected = NotFoundException.class)
    public void testGetBookmarkedStories() throws Exception {
        // TODO: Fix this...
        hackernewsApi.getBookmarkedStories(user);
    }

    @Test
    public void testGetStoryComments() throws Exception {

    }


    /**
     * Dumps several stories to a datastore.
     */
    private static void dumpStories() throws Exception {
        List<Story> dummyStories = Lists.newArrayList();
        for (String title : new String[]{"foo", "bar", "baz"}) {
            Key<Story> key = ofy().factory().allocateId(Story.class);
            Story story = new Story(key.getId());
            story.setTitle(title);
            story.setBy("foo");
            story.setScore(100500L);
            story.setUrl("http://foo.bar");
            story.setTime(new Date(System.currentTimeMillis() + new Random().nextLong()));
            dummyStories.add(story);
        }
        // Ugly way to process indices synchronously...
        Map<Key<Story>, Story> keys = ofy().save().entities(dummyStories).now();
        for (Key<Story> key : keys.keySet()) {
            DatastoreServiceFactory.getDatastoreService().get(null, key.getRaw());
            ofy().load().key(key).now();
        }
    }

    private List<Comment> dumpComments() {
        // TODO: dump comments...
        return null;

    }

}