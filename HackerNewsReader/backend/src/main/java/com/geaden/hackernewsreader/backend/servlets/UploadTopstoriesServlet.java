package com.geaden.hackernewsreader.backend.servlets;


import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.geaden.hackernewsreader.backend.boilerpipe.BoilerPipeServiceFactory;
import com.geaden.hackernewsreader.backend.boilerpipe.BoilerpipeContentExtractionService;
import com.geaden.hackernewsreader.backend.config.Constants;
import com.geaden.hackernewsreader.backend.domain.Bookmark;
import com.geaden.hackernewsreader.backend.domain.Comment;
import com.geaden.hackernewsreader.backend.domain.Content;
import com.geaden.hackernewsreader.backend.domain.Story;
import com.geaden.hackernewsreader.backend.firebase.FirebaseFactory;
import com.google.appengine.repackaged.com.google.api.client.util.Lists;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.geaden.hackernewsreader.backend.service.OfyService.ofy;

/**
 * The servlet to upload top stories from HN api to local storage with extraction of main content.
 *
 * @author Gennady Denisov
 */
public class UploadTopstoriesServlet extends HttpServlet {

    static final Logger log = Logger.getLogger(UploadTopstoriesServlet.class.getName());

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.entering(UploadTopstoriesServlet.class.getName(), "doGet");
        ObjectifyService.begin();
        // Create a new Firebase instance
        Query topStories = FirebaseFactory.create(Constants.HACKER_NEWS_API_STORIES)
                .limitToFirst(Constants.NUMBER_OF_STORIES);

        List<Bookmark> availableBookmarks = ofy().load().type(Bookmark.class).list();
        List<Key<Story>> availableStories = ofy().load().type(Story.class).keys().list();
        List<Key<Story>> keepStoriesKeys = Lists.newArrayList();

        for (Bookmark bookmark : availableBookmarks) {
            keepStoriesKeys.add(bookmark.getStory());
        }

        boolean removed = availableStories.removeAll(keepStoriesKeys);

        if (removed) {
            List<Key<Comment>> commentKeys = Lists.newArrayList();
            for (Key<Story> deletedStory : availableStories) {
                commentKeys.addAll(ofy().load().type(Comment.class).ancestor(deletedStory).keys().list());
            }
            ofy().delete().keys(availableStories).now();
            ofy().delete().keys(commentKeys).now();
        }

        topStories.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot storySnapshot : snapshot.getChildren()) {
                    String storyId = storySnapshot.getValue(String.class);
                    final Firebase storyRef = FirebaseFactory.create(Constants.HACKER_NEWS_API_ITEM);
                    storyRef.child(storyId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot storySnapshot) {
                            final Story story = storySnapshot.getValue(Story.class);
                            // TODO: Work on special cases, like PDF, or other file...
                            if (!story.getUrl().toLowerCase().endsWith(".pdf")) {
                                BoilerpipeContentExtractionService boilerpipeContentExtractionService =
                                        BoilerPipeServiceFactory.create();
                                Content content = boilerpipeContentExtractionService.content(story.getUrl());
                                story.setContent(content.getContent());
                                story.setNoComments(story.getKids().size());
                                story.setImageUrl(content.getImage());
                                log.info(story.toString());
                            }
                            // Save story to database.
                            ObjectifyService.begin();
                            final Key<Story> storyKey = ofy().save().entity(story).now();
                            // Retrieve comments for a story...
                            for (String kid : story.getKids()) {
                                final Firebase commentRef = FirebaseFactory.create(Constants.HACKER_NEWS_API_ITEM);
                                commentRef.child(kid).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        Comment comment = dataSnapshot.getValue(Comment.class);
                                        if (comment.getType().equals(Comment.COMMENT_TYPE)) {
                                            comment.setStoryKey(storyKey);
                                            log.info("Saving comment... " + comment.toString());
                                            ObjectifyService.begin();
                                            ofy().save().entity(comment).now();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(FirebaseError firebaseError) {
                                        log.warning("Failed to get comment. " + firebaseError.getMessage());
                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                log.warning(firebaseError.getMessage());
            }
        });
        // We're done...
        resp.setContentType("application/json");
        resp.getWriter().write("{\"result\": \"done\"}");
    }
}
