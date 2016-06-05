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
import com.google.appengine.repackaged.com.google.gson.Gson;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    public static final Pattern PATTERN =
            Pattern.compile(".*\\.(pdf|ods|odt|txt|swf|doc(x)?|xls(x)?)$", Pattern.CASE_INSENSITIVE);

    // Track updated stories and comments.
    private List<Story> updatedStories = Lists.newArrayList();
    private List<Comment> updatedComments = Lists.newArrayList();

    private final Semaphore semaphore = SemaphoreFactory.create();

    @Override
    protected void doGet(HttpServletRequest req, final HttpServletResponse resp) throws ServletException,
            IOException {
        log.entering(UploadTopstoriesServlet.class.getName(), "doGet");
        ObjectifyService.begin();
        // Create a new Boilerpipe instance
        final BoilerpipeContentExtractionService boilerpipeContentExtractionService =
                BoilerPipeServiceFactory.create();
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
            log.info("Cleaning up old data except bookmarks...");
            List<Key<Comment>> commentKeys = Lists.newArrayList();
            for (Key<Story> deletedStory : availableStories) {
                commentKeys.addAll(ofy().load().type(Comment.class).ancestor(deletedStory).keys().list());
            }
            ofy().delete().keys(availableStories).now();
            ofy().delete().keys(commentKeys).now();
        }

        final ResultWrapper resultWrapper = new ResultWrapper();

        topStories.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot storySnapshot : snapshot.getChildren()) {
                    final String storyId = storySnapshot.getValue(String.class);
                    final Firebase storyRef = FirebaseFactory.create(Constants.HACKER_NEWS_API_ITEM);
                    storyRef.child(storyId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot storySnapshot) {
                            final Story story = storySnapshot.getValue(Story.class);
                            if (story.getUrl() != null) {
                                // Extract story content if it has url and of appropriate type.
                                Matcher m = PATTERN.matcher(story.getUrl());
                                if (!m.matches()) {
                                    Content content = boilerpipeContentExtractionService.content(story.getUrl());
                                    if (content != null) {
                                        story.setContent(content.getContent());
                                        story.setImageUrl(content.getImage());
                                    }
                                }
                            }
                            story.setNoComments(story.getKids() != null ? story.getKids().size() : 0);
                            // Save story to database.
                            ObjectifyService.begin();
                            final Key<Story> storyKey = Key.create(Story.class, story.getId());
                            log.info(story.toString());
                            updatedStories.add(story);
                            if (story.getKids() != null) {
                                // Retrieve comments for a story if it has any...
                                for (String kid : story.getKids()) {
                                    final Firebase commentRef = FirebaseFactory.create(
                                            Constants.HACKER_NEWS_API_ITEM);
                                    commentRef.child(kid).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            Comment comment = dataSnapshot.getValue(Comment.class);
                                            if (comment.getType().equals(Comment.COMMENT_TYPE)) {
                                                comment.setStoryKey(storyKey);
                                                log.info(comment.toString());
                                                updatedComments.add(comment);
                                            }
                                            resultWrapper.setDone();
                                            resultWrapper.comments = updatedComments.size();
                                            resultWrapper.stories = updatedStories.size();
                                            semaphore.release();
                                        }

                                        @Override
                                        public void onCancelled(FirebaseError firebaseError) {
                                            log.warning("Failed to get comment. " + firebaseError.getMessage());
                                            resultWrapper.setError();
                                            semaphore.release();
                                        }
                                    });
                                }
                            }
                            resultWrapper.setDone();
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {
                            log.warning("Failed to get story. " + firebaseError.getMessage());
                            resultWrapper.setError();
                        }
                    });
                }
                ObjectifyService.begin();
                ofy().save().entities(updatedStories).now();
                ofy().save().entities(updatedComments).now();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                log.warning(firebaseError.getMessage());
                resultWrapper.setError();
            }
        });
        semaphore.acquireUninterruptibly();
        // We're done...
        resp.setContentType("application/json");
        resultWrapper.setDone();
        resp.getWriter().write(new Gson().toJson(resultWrapper));
    }

    public static class SemaphoreFactory {
        public static Semaphore create() {
            return new Semaphore(1, true);
        }
    }

    /**
     * Just a helper class to represent result of a cron job.
     */
    class ResultWrapper {
        String result;
        int comments;
        int stories;

        public void setError() {
            result = "error";
        }

        public void setDone() {
            result = "done";
        }
    }
}
