package com.geaden.android.hackernewsreader.app.data.local;

import com.geaden.hackernewsreader.backend.hackernews.model.Comment;
import com.google.api.client.util.DateTime;
import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.util.Date;

/**
 * Represents comments stored locally.
 *
 * @author Gennady Denisov
 */
@Table(database = StoriesDatabase.class, name = CommentModel.TABLE_NAME)
public class CommentModel extends BaseModel {

    public static final String TABLE_NAME = "comments";

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

    public Comment toModel() {
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
