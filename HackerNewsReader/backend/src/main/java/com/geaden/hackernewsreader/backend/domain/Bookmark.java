package com.geaden.hackernewsreader.backend.domain;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

/**
 * POJO representing bookmark entity.
 *
 * @author Gennady Denisov
 */
@Entity
@Cache
public class Bookmark {

    @Id
    private long id;

    @Parent
    private Key<Profile> profile;

    @Index
    private Key<Story> story;

    /** GAE & Objectify want this */
    public Bookmark() {

    }

    public Bookmark(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setProfile(Key<Profile> profile) {
        this.profile = profile;
    }

    public void setStory(Key<Story> story) {
        this.story = story;
    }

    public Key<Profile> getProfile() {
        return profile;
    }

    public Key<Story> getStory() {
        return story;
    }
}
