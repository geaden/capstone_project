package com.geaden.hackernewsreader.backend.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.geaden.hackernewsreader.backend.util.CustomTimestampDeserializer;
import com.google.api.server.spi.config.AnnotationBoolean;
import com.google.api.server.spi.config.ApiResourceProperty;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class Story {
    /**
     * The ID for datastore key.
     */
    @Id
    private long id;

    @JsonProperty("by")
    private String author;

    @Index
    @JsonDeserialize(using = CustomTimestampDeserializer.class)
    private Date time;

    private String title;

    private String type;

    private String content;

    private String imageUrl;

    private long score;

    /**
     * List of kids, returned from HN API.
     */
    @ApiResourceProperty(ignored = AnnotationBoolean.TRUE)
    private List<String> kids;

    private String url;

    /**
     * Private default constructor.
     */
    private Story() {
    }

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

    public void setUrl(String url) {
        this.url = url;
    }

    public List<String> getKids() {
        return kids;
    }

    public void setKids(List<String> kids) {
        this.kids = kids;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Story{");
        sb.append("id=").append(id);
        sb.append(", author='").append(author).append('\'');
        sb.append(", time=").append(time);
        sb.append(", title='").append(title).append('\'');
        sb.append(", type='").append(type).append('\'');
        sb.append(", content='").append(content).append('\'');
        sb.append(", imageUrl='").append(imageUrl).append('\'');
        sb.append(", score=").append(score);
        sb.append(", url='").append(url).append('\'');
        sb.append("}");
        return sb.toString();
    }
}
