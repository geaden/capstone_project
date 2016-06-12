package com.geaden.android.hackernewsreader.app.data.local;

import android.net.Uri;

import com.geaden.android.hackernewsreader.app.util.ContentProviderUtils;
import com.geaden.hackernewsreader.backend.hackernews.model.Comment;
import com.google.api.client.util.DateTime;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.annotation.provider.ContentUri;
import com.raizlabs.android.dbflow.annotation.provider.TableEndpoint;
import com.raizlabs.android.dbflow.structure.provider.BaseSyncableProviderModel;
import com.raizlabs.android.dbflow.structure.provider.ContentUtils;

import java.util.Date;

/**
 * Represents comments stored locally.
 *
 * @author Gennady Denisov
 */
@TableEndpoint(name = CommentModel.NAME, contentProvider = StoriesDatabase.class)
@Table(database = StoriesDatabase.class, name = CommentModel.NAME)
public class CommentModel extends BaseSyncableProviderModel {

    public static final String NAME = "comments";

    @ContentUri(path = NAME, type = ContentUri.ContentType.VND_MULTIPLE + NAME)
    public static final Uri CONTENT_URI =
            ContentUtils.buildUriWithAuthority(StoriesDatabase.CONTENT_AUTHORITY, NAME);

    @ContentUri(type = ContentUri.ContentType.VND_MULTIPLE + NAME,
            path = NAME + "/#",
            segments = {@ContentUri.PathSegment(segment = 1, column = "story_id")}
    )
    public static Uri withStoryId(long storyId) {
        return ContentProviderUtils.buildUri(NAME, Long.toString(storyId));
    }

    @PrimaryKey
    long id;

    @ForeignKey(tableClass = StoryModel.class)
    long story;

    @Column
    String author;

    @Column
    String text;

    @Column
    Date time;

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

    public Comment getComment() {
        Comment comment = new Comment();
        comment.setId(id);
        comment.setAuthor(author);
        comment.setText(text);
        comment.setTime(new DateTime(time));
        return comment;
    }

    /**
     * Factory method to construct model from a comment.
     *
     * @param storyId the id of a story that comment belongs to.
     * @param comment a comment.
     * @return conversion of {@link Comment} to {@link CommentModel}.
     */
    public static CommentModel from(String storyId, Comment comment) {
        CommentModel commentModel = new CommentModel();
        commentModel.id = comment.getId();
        commentModel.author = comment.getAuthor();
        commentModel.text = comment.getText();
        commentModel.time = new Date(comment.getTime().getValue());
        commentModel.story = Long.valueOf(storyId);
        return commentModel;
    }
}
