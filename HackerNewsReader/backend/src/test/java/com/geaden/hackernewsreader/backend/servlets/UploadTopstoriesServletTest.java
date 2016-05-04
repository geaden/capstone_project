package com.geaden.hackernewsreader.backend.servlets;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.geaden.hackernewsreader.backend.firebase.FirebaseFactory;
import com.geaden.hackernewsreader.backend.goose.GooseFactory;
import com.google.appengine.repackaged.com.google.api.client.util.Lists;
import com.google.appengine.tools.development.testing.LocalDatastoreServiceTestConfig;
import com.google.appengine.tools.development.testing.LocalServiceTestHelper;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cache.AsyncCacheFilter;
import com.googlecode.objectify.util.Closeable;
import com.gravity.goose.Goose;

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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Tests for Upload servlet.
 *
 * @author Gennady Denisov
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({FirebaseFactory.class, GooseFactory.class})
public class UploadTopstoriesServletTest {

    private final LocalServiceTestHelper helper =
            new LocalServiceTestHelper(new LocalDatastoreServiceTestConfig());

    private UploadTopstoriesServlet uploadTopstoriesServlet;

    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    Firebase firebase;

    @Mock
    Goose goose;

    @Mock
    private DataSnapshot snapshot;

    @Mock
    private Query query;

    @Captor
    private ArgumentCaptor<ValueEventListener> eventListenerArgumentCaptor;

    private Closeable session;

    @Before
    public void setUp() throws Exception {
        helper.setUp();
        session = ObjectifyService.begin();
        MockitoAnnotations.initMocks(this);
        PowerMockito.mockStatic(FirebaseFactory.class, GooseFactory.class);
        PowerMockito.when(FirebaseFactory.create(anyString())).thenReturn(firebase);
        PowerMockito.when(GooseFactory.create()).thenReturn(goose);
        when(firebase.limitToFirst(anyInt())).thenReturn(query);
        uploadTopstoriesServlet = new UploadTopstoriesServlet();
    }

    @After
    public void tearDown() throws Exception {
        AsyncCacheFilter.complete();
        session.close();
        helper.tearDown();
    }

    @Test
    public void testDoGet() throws Exception {
        uploadTopstoriesServlet.doGet(request, response);
        verify(query).addValueEventListener(eventListenerArgumentCaptor.capture());
        when(snapshot.getChildren()).thenReturn(Lists.<DataSnapshot>newArrayList());
        eventListenerArgumentCaptor.getValue().onDataChange(snapshot);
    }
}