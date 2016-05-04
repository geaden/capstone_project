package com.geaden.hackernewsreader.backend.domain;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.Date;
import java.util.List;

/**
 * POJO representing single story from Hacker News.
 *
 * @author Gennady Denisov
 */
@Entity
@Cache
public class Story {
    /**
     * The ID for datastore key.
     */
    @Index
    @Id
    private final long id;

    @Index
    private String author;

    @Index
    private Date time;

    @Index
    private String title;

    @Index
    private String type;

    private String content;

    private String imageUrl;

    @Index
    private long score;

    private List<Comment> comments;

    @Index
    private String url;

    public Story(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }

    public String getAuthor() {
        return author;
    }

    public Date getTime() {
        return time;
    }

    public String getTitle() {
        return title;
    }

    public String getType() {
        return type;
    }

    public long getScore() {
        return score;
    }

    public List<Comment> getComments() {
        return comments;
    }

    public String getUrl() {
        return url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public void setTime(Date time) {
        this.time = time;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setScore(long score) {
        this.score = score;
    }
}
