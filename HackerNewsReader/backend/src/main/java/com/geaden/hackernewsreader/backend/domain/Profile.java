package com.geaden.hackernewsreader.backend.domain;

import com.google.appengine.repackaged.com.google.api.client.util.Lists;
import com.google.common.collect.ImmutableList;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.List;

/**
 * Profile class stores user's profile data..
 *
 * @author Gennady Denisov
 */
@Entity
public class Profile {
    /**
     * Use userId as the datastore key.
     */
    @Id
    private String userId;

    /**
     * Any string user wants us to display him/her on this system.
     */
    private String displayName;

    /**
     * User's main e-mail address.
     */
    private String mainEmail;

    /**
     * List of stories' keys that user bookmarked.
     */
    private List<String> bookmarkedStoriesKeys = Lists.newArrayList();

    /**
     * Just making the default constructor private.
     */
    private Profile() {
    }

    /**
     * Public constructor for Profile.
     *
     * @param userId      The datastore key.
     * @param displayName Any string user wants us to display him/her on this system.
     * @param mainEmail   User's main e-mail address.
     */
    public Profile(String userId, String displayName, String mainEmail) {
        this.userId = userId;
        this.displayName = displayName;
        this.mainEmail = mainEmail;
    }

    /**
     * Getter for bookmarked stories by user.
     *
     * @return an immutable copy of bookmarked stories.
     */
    public List<String> getBookmarksKeys() {
        return ImmutableList.copyOf(bookmarkedStoriesKeys);
    }

    /**
     * Add story to a bookmarked stories.
     *
     * @param storyKey a websafe String representation of the Story key.
     */
    public void addToBookmarkedStoriesKeys(String storyKey) {
        bookmarkedStoriesKeys.add(storyKey);
    }

    /**
     * Removes story from a user bookmarks.
     *
     * @param storyKey a websafe String representation of the Story key.
     */
    public void removeBookmarkedStory(String storyKey) {
        if (bookmarkedStoriesKeys.contains(storyKey)) {
            bookmarkedStoriesKeys.remove(storyKey);
        } else {
            throw new IllegalArgumentException("Invalid story key: " + storyKey);
        }
    }
}
