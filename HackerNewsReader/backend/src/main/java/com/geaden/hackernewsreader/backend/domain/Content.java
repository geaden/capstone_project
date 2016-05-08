package com.geaden.hackernewsreader.backend.domain;

/**
 * POJO to hold extracted article content.
 *
 * @author Gennady Denisov
 */
public class Content {
    // The title of the content.
    private String title;

    // The content itself.
    private String content;

    // The main image of the content.
    private String image;

    public Content(String title, String content, String image) {
        this.title = title;
        this.content = content;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public String getImage() {
        return image;
    }
}
