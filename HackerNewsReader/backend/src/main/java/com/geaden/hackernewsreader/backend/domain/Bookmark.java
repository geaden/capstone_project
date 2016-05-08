package com.geaden.hackernewsreader.backend.domain;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;

/**
 * POJO representing bookmark entity.
 *
 * @author Gennady Denisov
 */
@Entity
@Cache
public class Bookmark {
    private Key<Profile> profileKey;
    private Key<Story> storyKey;
}
