package com.geaden.android.hackernewsreader.app.data.local;

import com.geaden.hackernewsreader.backend.hackernews.model.Story;
import com.google.api.client.util.DateTime;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

/**
 * Represents {@link Story} relation in database.
 *
 * @author Gennady Denisov
 */
@Table(database = StoriesDatabase.class, name = StoryModel.TABLE_NAME)
public class StoryModel extends BaseModel {

    public static final String TABLE_NAME = "stories";

    @PrimaryKey
    long id;

    @Column
    @Unique
    String title;

    @Column
    String content;

    @Column
    String author;

    @Column
    Date time;

    @Column
    long score;

    @Column
    String url;

    @Column
    String imageUrl;

    /**
     * Helper method to get a {@link Story} from it's model.
     *
     * @return a story.
     */
    public Story getStory() {
        Story story = new Story();
        story.setId(id);
        story.setAuthor(author);
        story.setTitle(title);
        story.setContent(content);
        story.setScore(score);
        story.setTime(new DateTime(time));
        story.setUrl(url);
        story.setImageUrl(imageUrl);
        return story;
    }

    /**
     * Factory method to construct model from a story.
     *
     * @param story a story.
     * @return conversion of {@link Story} to {@link StoryModel}.
     */
    public static StoryModel from(Story story) {
        StoryModel storyModel = new StoryModel();
        storyModel.id = story.getId();
        storyModel.title = story.getTitle();
        storyModel.content = story.getContent();
        storyModel.imageUrl = story.getImageUrl();
        storyModel.url = story.getUrl();
        storyModel.score = story.getScore();
        storyModel.time = new Date(story.getTime().getValue());
        storyModel.author = story.getAuthor();
        return storyModel;
    }

}
