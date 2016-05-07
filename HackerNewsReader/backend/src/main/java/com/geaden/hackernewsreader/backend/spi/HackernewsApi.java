/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.geaden.hackernewsreader.backend.spi;

import com.geaden.hackernewsreader.backend.config.Constants;
import com.geaden.hackernewsreader.backend.domain.AppEngineUser;
import com.geaden.hackernewsreader.backend.domain.Profile;
import com.geaden.hackernewsreader.backend.domain.Story;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.NotFoundException;
import com.google.api.server.spi.response.UnauthorizedException;
import com.google.appengine.api.users.User;
import com.google.appengine.repackaged.com.google.api.client.util.Lists;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;

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
        )
)
public class HackernewsApi {

    private static final Logger log = Logger.getLogger(HackernewsApi.class.getName());

    /**
     * Method to get all top stories (limited to 200 items).
     *
     * @return current top stories from the hacker news.
     */
    @ApiMethod(name = "getTopstories", path = "topstories", httpMethod = ApiMethod.HttpMethod.GET)
    public List<Story> getTopstories() {
        return ofy().load().type(Story.class)
                .order("-time")
                .limit(Constants.NUMBER_OF_STORIES)
                .list();
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
        Profile profile = ofy().load().key(Key.create(Profile.class, getUserId(user))).now();
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
