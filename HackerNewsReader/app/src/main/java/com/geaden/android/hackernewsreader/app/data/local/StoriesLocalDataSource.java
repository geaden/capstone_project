package com.geaden.android.hackernewsreader.app.data.local;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.geaden.android.hackernewsreader.app.data.StoriesDataSource;
import com.geaden.hackernewsreader.backend.hackernews.model.Comment;
import com.geaden.hackernewsreader.backend.hackernews.model.Story;
import com.google.api.client.util.Lists;
import com.raizlabs.android.dbflow.sql.language.SQLite;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Concrete implementation of a data source as a db.
 * <p/>
 * Note: this is a singleton and we are opening the database once and not closing it. The framework
 * cleans up the resources when the application closes so we don't need to close the db.
 *
 * @author Gennady Denisov
 */
@Singleton
public class StoriesLocalDataSource implements StoriesDataSource {

    @Inject
    public StoriesLocalDataSource(@NonNull Context context) {
        checkNotNull(context);
    }

    @Nullable
    @Override
    public List<Story> getStories() {
        List<StoryModel> storyModels = SQLite.select()
                .from(StoryModel.class)
                .queryList();

        List<Story> stories = Lists.newArrayList();

        for (StoryModel storyModel : storyModels) {
            stories.add(storyModel.getStory());
        }
        return stories;
    }

    @Nullable
    @Override
    public Story getStory(@NonNull String storyId) {
        StoryModel storyModel = SQLite.select().from(StoryModel.class)
                .where(StoryModel_Table.id.eq(Long.valueOf(storyId))).querySingle();
        if (storyModel != null) {
            return storyModel.getStory();
        }
        return null;
    }

    @Nullable
    @Override
    public List<Comment> getComments(@NonNull String storyId) {
        List<CommentModel> commentModels = SQLite.select().from(CommentModel.class)
                .where(CommentModel_Table.story_id.eq(Long.valueOf(storyId)))
                .queryList();

        List<Comment> comments = Lists.newArrayList();

        for (CommentModel commentModel : commentModels) {
            comments.add(commentModel.toModel());
        }

        return comments;
    }

    @Override
    public void saveComment(@NonNull String storyId, @NonNull Comment comment) {
        CommentModel commentModel = CommentModel.from(storyId, comment);
        commentModel.save();
    }

    @Override
    public void saveStory(@NonNull Story story) {
        StoryModel storyModel = StoryModel.from(story);
        storyModel.save();
    }

    @Override
    public void bookmarkStory(@NonNull String storyId) {
        BookmarkModel bookmarkModel = new BookmarkModel();
        bookmarkModel.story = Long.valueOf(storyId);
        bookmarkModel.insert();
    }

    @Override
    public void unbookmarkStory(@NonNull String storyId) {
        BookmarkModel bookmarkModel = SQLite.select().from(BookmarkModel.class)
                .where(BookmarkModel_Table.story_id.eq(Long.valueOf(storyId))).querySingle();
        if (null != bookmarkModel) {
            bookmarkModel.delete();
        }
    }

    @Override
    public void refreshStories() {
        // Not required because the {@link StoriesRepository} handles the logic of refreshing the
        // stories from all the available data sources.
    }

    @Override
    public void deleteAllStories() {
        SQLite.delete().from(StoryModel.class).query();
    }
}
