package com.geaden.hackernewsreader.backend.servlets;


import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.geaden.hackernewsreader.backend.config.Constants;
import com.geaden.hackernewsreader.backend.domain.Story;
import com.geaden.hackernewsreader.backend.firebase.FirebaseFactory;
import com.geaden.hackernewsreader.backend.goose.GooseFactory;
import com.gravity.goose.Article;
import com.gravity.goose.Goose;

import java.io.IOException;
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
        // Create a new Firebase instance
        Query topStories = FirebaseFactory.create(Constants.HACKER_NEWS_API_STORIES)
                .limitToFirst(Constants.NUMBER_OF_STORIES);
        topStories.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot storySnapshot : snapshot.getChildren()) {
                    String storyId = storySnapshot.getValue(String.class);
                    Firebase storyRef = FirebaseFactory.create(Constants.HACKER_NEWS_API_ITEM);
                    storyRef.child(storyId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot storySnapshot) {
                            Story story = storySnapshot.getValue(Story.class);
                            Goose goose = GooseFactory.create();
                            Article extracted = goose.extractContent(story.getUrl());
                            story.setContent(extracted.cleanedArticleText());
                            story.setImageUrl(extracted.topImage().getImageSrc());
                            // TODO: retrieve comments...
                            // Save story to database.
                            ofy().save().entity(story).now();
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
    }
}
