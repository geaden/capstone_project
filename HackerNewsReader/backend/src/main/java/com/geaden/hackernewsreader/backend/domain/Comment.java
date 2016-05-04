package com.geaden.hackernewsreader.backend.domain;

import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

/**
 * POJO representing a comment.
 *
 * @author Gennady Denisov
 */
@Entity
@Cache
public class Comment {

    @Id
    private final long id;

    public Comment(long id) {
        this.id = id;
    }

    /**
     * Holds Story key as the parent.
     */
    @Parent
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Key<Story> storyKey;

    private String text;

    @Index
    private String author;
}
