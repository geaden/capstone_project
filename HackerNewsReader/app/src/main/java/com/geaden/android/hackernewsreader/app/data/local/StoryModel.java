package com.geaden.android.hackernewsreader.app.data.local;

import android.net.Uri;

import com.geaden.android.hackernewsreader.app.util.ContentProviderUtils;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;
import com.google.api.client.util.DateTime;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.Unique;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;
import com.raizlabs.android.dbflow.sql.language.ConditionGroup;
import com.raizlabs.android.dbflow.structure.provider.BaseSyncableProviderModel;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

import java.util.Date;

/**
 * Represents {@link Story} relation in database.
 *
 * @author Gennady Denisov
 */
@TableEndpoint(name = StoryModel.NAME, contentProvider = StoriesDatabase.class)
@Table(database = StoriesDatabase.class, name = StoryModel.NAME)
public class StoryModel extends BaseSyncableProviderModel {

    public static final String NAME = "stories";

    // stories/
    @ContentUri(path = NAME, type = ContentUri.ContentType.VND_MULTIPLE + NAME)
    public static final Uri CONTENT_URI =
            ContentUtils.buildUriWithAuthority(StoriesDatabase.CONTENT_AUTHORITY, NAME);

    // stories/#
    @ContentUri(type = ContentUri.ContentType.VND_SINGLE + NAME,
            path = NAME + "/#",
            segments = {@ContentUri.PathSegment(segment = 1, column = "id")}
    )
    public static Uri withId(String storyId) {
        return ContentProviderUtils.buildUri(NAME, storyId);
    }

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

    @Column
    int comments;

    @Override
    public Uri getDeleteUri() {
        return CONTENT_URI;
    }

    @Override
    public Uri getInsertUri() {
        return CONTENT_URI;
    }

    @Override
    public Uri getUpdateUri() {
        return CONTENT_URI;
    }

    @Override
    public Uri getQueryUri() {
        return CONTENT_URI;
    }

    /**
     * Checks if current story is bookmark.
     *
     * @return is story is bookmark.
     */
    public boolean isBookmark() {
        // TODO: Potential performance issue...
        BookmarkModel bookmarkModel = ContentUtils
                .querySingle(BookmarkModel.withStoryId(Long.toString(id)),
                        BookmarkModel.class, ConditionGroup.clause(), null);
        return bookmarkModel != null;
    }

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
        story.setNoComments(comments);
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
        storyModel.comments = story.getNoComments();
        storyModel.time = new Date(story.getTime().getValue());
        storyModel.author = story.getAuthor();
        return storyModel;
    }

}
