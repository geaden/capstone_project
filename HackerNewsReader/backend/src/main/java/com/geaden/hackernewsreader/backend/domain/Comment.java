package com.geaden.hackernewsreader.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.geaden.hackernewsreader.backend.util.CustomTimestampDeserializer;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Parent;

import java.util.Date;

/**
 * POJO representing a comment.
 *
 * @author Gennady Denisov
 */
@Entity
@Cache
@JsonIgnoreProperties(ignoreUnknown = true)
public class Comment {
    public static final String COMMENT_TYPE = "comment";

    @Id
    private long id;

    /**
     * Holds Story key as the parent.
     */
    @Parent
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private Key<Story> storyKey;

    private String text;

    @Index
    @JsonProperty("by")
    private String author;

    @JsonDeserialize(using = CustomTimestampDeserializer.class)
    private Date time;

    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private String type;

    public Comment() {

    }

    public Comment(long id, long storyId) {
        this.id = id;
        this.storyKey = Key.create(Story.class, storyId);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStoryKey(Key<Story> storyKey) {
        this.storyKey = storyKey;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Comment{");
        sb.append("id=").append(id);
        sb.append(", author='").append(author).append('\'');
        sb.append(", text='").append(text).append('\'');
        sb.append(", time=").append(time);
        sb.append(", type='").append(type).append('\'');
        sb.append("}");
        return sb.toString();
    }
}
