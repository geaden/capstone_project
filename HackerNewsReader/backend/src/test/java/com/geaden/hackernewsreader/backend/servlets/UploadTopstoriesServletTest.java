package com.geaden.hackernewsreader.backend.servlets;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.geaden.hackernewsreader.backend.boilerpipe.BoilerPipeServiceFactory;
import com.geaden.hackernewsreader.backend.boilerpipe.BoilerpipeContentExtractionService;
import com.geaden.hackernewsreader.backend.domain.Comment;
import com.geaden.hackernewsreader.backend.domain.Content;
import com.geaden.hackernewsreader.backend.domain.Story;
import com.geaden.hackernewsreader.backend.firebase.FirebaseFactory;
import com.google.appengine.repackaged.com.google.common.collect.Lists;
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
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.PrintWriter;
import java.util.Date;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.geaden.hackernewsreader.backend.service.OfyService.ofy;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for Upload servlet.
 *
 * @author Gennady Denisov
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FirebaseFactory.class, BoilerPipeServiceFactory.class})
public class UploadTopstoriesServletTest {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig()
                    .setDefaultHighRepJobPolicyUnappliedJobPercentage(100),
                    new LocalMemcacheServiceTestConfig(),
                    new LocalTaskQueueTestConfig());

    private UploadTopstoriesServlet uploadTopstoriesServlet;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    Firebase firebase;

    @Mock
    BoilerpipeContentExtractionService boilerpipeContentExtractionService;

    @Mock
    Content content;

    @Mock
    DataSnapshot snapshot;

    @Mock
    Query query;

    @Mock
    PrintWriter writer;

    @Mock
    private DataSnapshot commentSnapshot;

    @Captor
    private ArgumentCaptor<ValueEventListener> eventListenerArgumentCaptor;

    private Closeable session;
    private Story dummyStory;

    @Before
    public void setUp() throws Exception {
        helper.setUp();
        session = ObjectifyService.begin();
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(FirebaseFactory.class, BoilerPipeServiceFactory.class);
        PowerMockito.when(FirebaseFactory.create(anyString())).thenReturn(firebase);
        PowerMockito.when(BoilerPipeServiceFactory.create()).thenReturn(boilerpipeContentExtractionService);
        when(boilerpipeContentExtractionService.content(anyString())).thenReturn(content);
        when(firebase.limitToFirst(anyInt())).thenReturn(query);
        // Mock child
        when(firebase.child(anyString())).thenReturn(firebase);
        uploadTopstoriesServlet = new UploadTopstoriesServlet();
        dummyStory = createDummyStory();
    }

    /**
     * Just creates dummy story.
     *
     * @return the dummy story.
     */
    private static Story createDummyStory() {
        Key<Story> key = ofy().factory().allocateId(Story.class);
        Story story = new Story(key.getId());
        story.setTitle("foo");
        story.setBy("foo");
        story.setScore(100500L);
        story.setUrl("http://foo.bar");
        story.setTime(new Date(System.currentTimeMillis() + new Random().nextLong()));
        story.setKids(Lists.<String>newArrayList("111", "222", "333"));
        return story;
    }

    /**
     * Just create dummy comment.
     *
     * @return the comment.
     */
    private static Comment createDummyComment(String commentId) {
        Comment comment = new Comment();
        comment.setId(Long.valueOf(commentId));
        comment.setText("foo");
        comment.setAuthor("bar");
        comment.setType(Comment.COMMENT_TYPE);
        comment.setTime(new Date());
        return comment;
    }

    @After
    public void tearDown() throws Exception {
        AsyncCacheFilter.complete();
        session.close();
        helper.tearDown();
    }

    @Test
    public void testDoGet() throws Exception {
        when(response.getWriter()).thenReturn(writer);
        uploadTopstoriesServlet.doGet(request, response);
        verify(query).addValueEventListener(eventListenerArgumentCaptor.capture());

        when(snapshot.getChildren()).thenReturn(Lists.newArrayList(snapshot));
        eventListenerArgumentCaptor.getValue().onDataChange(snapshot);
        verify(firebase).addListenerForSingleValueEvent(eventListenerArgumentCaptor.capture());

        when(snapshot.getValue(Story.class)).thenReturn(dummyStory);
        eventListenerArgumentCaptor.getValue().onDataChange(snapshot);

        Story story = ofy().load().key(Key.create(Story.class, dummyStory.getId())).now();
        assertNotNull(story);

        // We actually listening for comments...
        verify(firebase, atLeast(3)).addListenerForSingleValueEvent(eventListenerArgumentCaptor.capture());

        when(commentSnapshot.getValue(Comment.class))
                .thenReturn(createDummyComment("111"))
                .thenReturn(createDummyComment("222"))
                .thenReturn(createDummyComment("333"));

        // Capture arguments for each comment
        for (String key : story.getKids()) {
            eventListenerArgumentCaptor.getValue().onDataChange(commentSnapshot);
        }

        for (String commentId : new String[]{"111", "222", "333"}) {
            Key<Comment> commentKey = Key.create(Key.create(Story.class, story.getId()),
                    Comment.class, Long.valueOf(commentId));
            Comment comment = ofy().load().key(commentKey).now();
            assertNotNull(comment);
        }
    }
}