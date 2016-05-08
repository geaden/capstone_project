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

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.geaden.hackernewsreader.backend.service.OfyService.ofy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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
        hackernewsApi.getBookmarkedStories(user);
    }

    @Test
    public void testGetStoryComments() throws Exception {
        Story story = ofy().load().type(Story.class).id(1L).now();
        dumpComments(story);
        List<Comment> comments = hackernewsApi.getStoryComments(1L);
        assertEquals(3, comments.size());
        comments = hackernewsApi.getStoryComments(2L);
        assertEquals(0, comments.size());
    }

    @Test
    public void testBookmarkStory() throws Exception {
        HackernewsApi.WrappedBoolean wrappedBoolean = hackernewsApi.bookmarkStory(user, 1L);
        assertTrue(wrappedBoolean.getResult());
        Collection<Story> bookmarks = hackernewsApi.getBookmarkedStories(user);
        assertEquals(1, bookmarks.size());
    }

    @Test
    public void testUnbookmarkStory() throws Exception {
        HackernewsApi.WrappedBoolean wrappedBoolean = hackernewsApi.unbookmarkStory(user, 1L);
        assertFalse(wrappedBoolean.getResult());
        // Bookmark a story...
        hackernewsApi.bookmarkStory(user, 1L);
        wrappedBoolean = hackernewsApi.unbookmarkStory(user, 1L);
        assertTrue(wrappedBoolean.getResult());
        Collection<Story> bookmarks = hackernewsApi.getBookmarkedStories(user);
        assertEquals(0, bookmarks.size());
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
            story.setType("story");
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

    /**
     * Generates dummy comments to story.
     *
     * @param story the story to create comments for.
     * @return comments.
     */
    private static void dumpComments(Story story) {
        List<Comment> dummyComments = Lists.newArrayList();
        for (String text : new String[]{"foo", "bar", "baz"}) {
            Key<Comment> key = ofy().factory().allocateId(Comment.class);
            Comment comment = new Comment(key.getId(), story.getId());
            comment.setText(text);
            comment.setAuthor("foo");
            comment.setTime(new Date(1175727286 * 1000L));
            dummyComments.add(comment);
        }
        ofy().save().entities(dummyComments).now();
    }

}