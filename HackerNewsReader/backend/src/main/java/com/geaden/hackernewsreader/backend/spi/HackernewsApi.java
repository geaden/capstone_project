/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.geaden.hackernewsreader.backend.spi;

import com.geaden.hackernewsreader.backend.config.Constants;
import com.geaden.hackernewsreader.backend.domain.AppEngineUser;
import com.geaden.hackernewsreader.backend.domain.Bookmark;
import com.geaden.hackernewsreader.backend.domain.Comment;
import com.geaden.hackernewsreader.backend.domain.Profile;
import com.geaden.hackernewsreader.backend.domain.Story;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.ConflictException;
import com.google.api.server.spi.response.ForbiddenException;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.repackaged.com.google.api.client.util.Lists;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.VoidWork;
import com.googlecode.objectify.Work;

import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import javax.inject.Named;

import static com.geaden.hackernewsreader.backend.service.OfyService.ofy;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "hackernews",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.hackernewsreader.geaden.com",
                ownerName = "backend.hackernewsreader.geaden.com",
                packagePath = ""
        ),
        scopes = {Constants.EMAIL_SCOPE},
        clientIds = {Constants.WEB_CLIENT_ID, Constants.ANDROID_CLIENT_ID,
                Constants.API_EXPLORER_CLIENT_ID},
        audiences = {Constants.ANDROID_AUDIENCE},
        description = "Conference Central API for creating and querying conferences," +
                " and for creating and getting user Profiles"
)
public class HackernewsApi {

    private static final Logger log = Logger.getLogger(HackernewsApi.class.getName());

    /**
     * Method to extract user friendly name from email.
     *
     * @param email the email.
     * @return display name.
     */
    private static String extractDefaultDisplayNameFromEmail(String email) {
        return email == null ? null : email.substring(0, email.indexOf("@"));
    }

    /**
     * Helper method to get Profile from the provided user and its id.
     *
     * @param user   the User to get profile for.
     * @param userId the user id.
     * @return obtained profile.
     */
    private static Profile getProfileFromUser(User user, String userId) {
        // First fetch it from the datastore.
        Profile profile = ofy().load().key(
                Key.create(Profile.class, userId)).now();
        if (profile == null) {
            // Create a new Profile if not exist.
            String email = user.getEmail();
            profile = new Profile(userId,
                    extractDefaultDisplayNameFromEmail(email), email);
        }
        return profile;
    }

    /**
     * Just a wrapper for Boolean.
     */
    public static class WrappedBoolean {

        private final Boolean result;

        public WrappedBoolean(Boolean result) {
            this.result = result;
        }

        public Boolean getResult() {
            return result;
        }
    }

    /**
     * A wrapper class that can embrace a generic result or some kind of exception.
     * <p/>
     * Use this wrapper class for the return type of objectify transaction.
     * <pre>
     * {@code
     * // The transaction that returns Story object.
     * TxResult<Story> result = ofy().transact(new Work<TxResult<Story>>() {
     *     public TxResult<Story> run() {
     *         // Code here.
     *         // To throw 404
     *         return new TxResult<>(new NotFoundException("No such story"));
     *         // To return a story.
     *         Story story = somehow.getStory();
     *         return new TxResult<>(story);
     *     }
     * }
     * // Actually the NotFoundException will be thrown here.
     * return result.getResult();
     * </pre>
     *
     * @param <ResultType> The type of the actual return object.
     */
    private static class TxResult<ResultType> {

        private ResultType result;

        private Throwable exception;

        private TxResult(ResultType result) {
            this.result = result;
        }

        private TxResult(Throwable exception) {
            if (exception instanceof NotFoundException ||
                    exception instanceof ForbiddenException ||
                    exception instanceof ConflictException) {
                this.exception = exception;
            } else {
                throw new IllegalArgumentException("Exception not supported.");
            }
        }

        private ResultType getResult() throws NotFoundException, ForbiddenException, ConflictException {
            if (exception instanceof NotFoundException) {
                throw (NotFoundException) exception;
            }
            if (exception instanceof ForbiddenException) {
                throw (ForbiddenException) exception;
            }
            if (exception instanceof ConflictException) {
                throw (ConflictException) exception;
            }
            return result;
        }
    }


    /**
     * Method to get all top stories (limited to 200 items).
     *
     * @return current top stories from the hacker news.
     */
    @ApiMethod(name = "getTopstories", path = "topstories", httpMethod = ApiMethod.HttpMethod.GET)
    public List<Story> getTopstories() {
        final List<Story> topstories = Lists.newArrayList();
        ObjectifyService.run(new VoidWork() {
            @Override
            public void vrun() {
                List<Story> stories = ofy().load().type(Story.class)
                        .order("-time")
                        .limit(Constants.NUMBER_OF_STORIES)
                        .list();
                for (Story story : stories) {
                    topstories.add(story);
                }
            }
        });
        return topstories;
    }

    /**
     * Returns a top story object by provided story id.
     *
     * @param storyId the story id.
     * @return Story object.
     */
    @ApiMethod(
            name = "getTopstory",
            path = "topstories/{id}",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public Story getTopstory(@Named("id") long storyId) throws NotFoundException {
        Key<Story> storyKey = Key.create(Story.class, storyId);
        Story story = ofy().load().key(storyKey).now();
        if (null == story) {
            throw new NotFoundException("No story found with key: " + storyId);
        }
        return story;
    }

    /**
     * Returns list of comments by a story.
     *
     * @param storyId the story id.
     * @return list of comments for that story.
     * @throws NotFoundException
     */
    @ApiMethod(
            name = "getComments",
            path = "topstories/{id}/comments",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public List<Comment> getStoryComments(@Named("id") final long storyId) throws NotFoundException {
        final List<Comment> comments = Lists.newArrayList();
        ObjectifyService.run(new VoidWork() {
            @Override
            public void vrun() {
                Key<Story> storyKey = Key.create(Story.class, storyId);
                List<Comment> storyComments = ofy().load().type(Comment.class)
                        .ancestor(storyKey)
                        .order("-time")
                        .list();
                for (Comment storyComment : storyComments) {
                    comments.add(storyComment);
                }
            }
        });
        return comments;
    }

    /**
     * Returns a collection of bookmarked by a user stories from HN.
     *
     * @param user the user to get bookmarks for.
     * @return a {@link Collection} of {@link Story} the user bookmarked.
     */
    @ApiMethod(
            name = "getBookmarkedStories",
            path = "bookmarks",
            httpMethod = ApiMethod.HttpMethod.GET
    )
    public Collection<Story> getBookmarkedStories(final User user) throws UnauthorizedException, NotFoundException {
        if (null == user) {
            throw new UnauthorizedException("Authorization required.");
        }
        String userId = getUserId(user);
        Profile profile = getProfileFromUser(user, userId);
        if (null == profile) {
            throw new NotFoundException("Profile doesn't exist.");
        }
        List<String> bookmarkedStoriesKeys = profile.getBookmarkedStoriesKeys();
        List<Key<Story>> bookmarkedStories = Lists.newArrayList();
        for (String bookmarkedStoryKey : bookmarkedStoriesKeys) {
            bookmarkedStories.add(Key.<Story>create(bookmarkedStoryKey));
        }
        return ofy().load().keys(bookmarkedStories).values();
    }

    /**
     * Method to bookmark a story for certain user.
     *
     * @param user    the user to add bookmark to.
     * @param storyId the story id to bookmark.
     * @return result of bookmarking.
     * @throws UnauthorizedException
     * @throws NotFoundException
     * @throws ConflictException
     * @throws ForbiddenException
     */
    @ApiMethod(
            name = "bookmarkStory",
            path = "topstories/{id}/bookmark",
            httpMethod = ApiMethod.HttpMethod.POST
    )
    public WrappedBoolean bookmarkStory(final User user, @Named("id") final long storyId) throws UnauthorizedException,
            NotFoundException, ConflictException, ForbiddenException {
        if (null == user) {
            throw new UnauthorizedException("Authorization required.");
        }
        final String userId = getUserId(user);
        TxResult<Boolean> result = ofy().transact(new Work<TxResult<Boolean>>() {
            @Override
            public TxResult<Boolean> run() {
                Key<Story> storyKey = Key.create(Story.class, storyId);
                Story story = ofy().load().key(storyKey).now();
                // 404 when there is no story with the given storyId.
                if (story == null) {
                    return new TxResult<>(new NotFoundException(
                            "No Story found with key: " + storyId));
                }
                // Bookmarking happens here.
                Profile profile = getProfileFromUser(user, userId);
                String storyKeyString = KeyFactory.keyToString(storyKey.getRaw());
                if (profile.getBookmarkedStoriesKeys().contains(storyKeyString)) {
                    return new TxResult<>(new ConflictException("You have already bookmarked this story"));
                } else {
                    // Keep track of all bookmarks
                    Key<Bookmark> bookmarkKey = ofy().factory().allocateId(Bookmark.class);
                    Bookmark bookmark = new Bookmark(bookmarkKey.getId());
                    bookmark.setProfile(Key.create(Profile.class, userId));
                    bookmark.setStory(storyKey);
                    ofy().save().entity(bookmark).now();
                    profile.addToBookmarkedStoriesKeys(storyKeyString);
                    ofy().save().entity(profile).now();
                    return new TxResult<>(true);
                }
            }
        });
        // NotFoundException is actually thrown here.
        return new WrappedBoolean(result.getResult());
    }

    /**
     * Method to bookmark a story for certain user.
     *
     * @param user    the user to add bookmark to.
     * @param storyId the story id to bookmark.
     * @return result of bookmarking.
     * @throws UnauthorizedException
     * @throws NotFoundException
     * @throws ConflictException
     * @throws ForbiddenException
     */
    @ApiMethod(
            name = "unbookmarkStory",
            path = "topstories/{id}/bookmark",
            httpMethod = ApiMethod.HttpMethod.DELETE
    )
    public WrappedBoolean unbookmarkStory(final User user, @Named("id") final long storyId) throws
            UnauthorizedException, NotFoundException, ConflictException, ForbiddenException {
        // If not signed in, throw a 401 error.
        if (null == user) {
            throw new UnauthorizedException("Authorization required.");
        }
        final String userId = getUserId(user);
        TxResult<Boolean> result = ofy().transact(new Work<TxResult<Boolean>>() {
            @Override
            public TxResult<Boolean> run() {
                Key<Story> storyKey = Key.create(Story.class, storyId);
                Story story = ofy().load().key(storyKey).now();
                // 404 when there is no Story with the given storyId.
                if (story == null) {
                    return new TxResult<>(new NotFoundException(
                            "No Story found with key: " + storyId));
                }
                // Un-bookmarking the story.
                Profile profile = getProfileFromUser(user, userId);
                String storyKeyString = KeyFactory.keyToString(storyKey.getRaw());
                if (profile.getBookmarkedStoriesKeys().contains(storyKeyString)) {
                    // Get bookmark & delete
                    Bookmark bookmarkToDelete = ofy().load().type(Bookmark.class).ancestor(profile)
                            .filter("story", storyKey)
                            .first().now();

                    ofy().delete().type(Bookmark.class).id(bookmarkToDelete.getId()).now();
                    // Remove bookmarks from profile.
                    profile.removeBookmarkedStory(storyKeyString);
                    ofy().save().entities(profile).now();
                    return new TxResult<>(true);
                } else {
                    return new TxResult<>(false);
                }
            }
        });
        // NotFoundException is actually thrown here.
        return new WrappedBoolean(result.getResult());
    }

    /**
     * This is an ugly workaround for null userId for Android clients.
     *
     * @param user A User object injected by the cloud endpoints.
     * @return the App Engine userId for the user.
     */
    private static String getUserId(User user) {
        String userId = user.getUserId();
        if (userId == null) {
            log.info("userId is null, so trying to obtain it from the datastore.");
            AppEngineUser appEngineUser = new AppEngineUser(user);
            ofy().save().entity(appEngineUser).now();
            // Begin new session for not using session cache.
            Objectify objectify = ofy().factory().begin();
            AppEngineUser savedUser = objectify.load().key(appEngineUser.getKey()).now();
            userId = savedUser.getUser().getUserId();
            log.info("Obtained the userId: " + userId);
        }
        return userId;
    }
}
