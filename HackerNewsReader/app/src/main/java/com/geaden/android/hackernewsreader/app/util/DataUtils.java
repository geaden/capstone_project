package com.geaden.android.hackernewsreader.app.util;

import android.database.Cursor;
import android.support.v4.util.Pair;

import com.geaden.android.hackernewsreader.app.data.local.CommentModel;
import com.geaden.android.hackernewsreader.app.data.local.StoryModel;
import com.geaden.hackernewsreader.backend.hackernews.model.Comment;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;
import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Helper to hold utility method to operate with data.
 *
 * @author Gennady Denisov
 */
public final class DataUtils {
    private DataUtils() {

    }

    /**
     * Gets {@link Story} from a provided {@link Cursor}.
     *
     * @param data {@link Cursor} to convert
     * @return instance of {@link Story}
     */
    public static Pair<Story, Boolean> convertCursorToStory(Cursor data) {
        StoryModel storyModel = FlowManager.getModelAdapter(StoryModel.class)
                .loadFromCursor(data);
        return new Pair<>(storyModel.getStory(), storyModel.isBookmark());
    }

    public static Comment convertCursorToComment(Cursor cursor) {
        CommentModel commentModel = FlowManager.getModelAdapter(CommentModel.class)
                .loadFromCursor(cursor);
        return commentModel.getComment();
    }
}
