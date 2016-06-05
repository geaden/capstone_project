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
                .orderBy(StoryModel_Table.score, false)
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
                .orderBy(CommentModel_Table.time, false)
                .queryList();

        List<Comment> comments = Lists.newArrayList();

        for (CommentModel commentModel : commentModels) {
            comments.add(commentModel.getComment());
        }

        StoryModel storyModel = SQLite.select().from(StoryModel.class)
                .where(StoryModel_Table.id.eq(Long.valueOf(storyId))).querySingle();

        if (storyModel != null && storyModel.comments != commentModels.size()) {
            // Update story model comments...
            storyModel.comments = commentModels.size();
            storyModel.save();
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

    @Nullable
    @Override
    public List<Story> getBookmarks(boolean update) {
        List<StoryModel> storyModels = SQLite.select().from(StoryModel.class)
                .where(StoryModel_Table.id.in(
                        SQLite.select(BookmarkModel_Table.story_id).from(BookmarkModel.class)
                )).queryList();

        List<Story> bookmarkedStories = Lists.newArrayList();
        for (StoryModel storyModel : storyModels) {
            bookmarkedStories.add(storyModel.getStory());
        }

        return bookmarkedStories;
    }

    @Override
    public void addBookmark(@NonNull String storyId) {
        BookmarkModel bookmarkModel = new BookmarkModel();
        bookmarkModel.story = Long.valueOf(storyId);
        bookmarkModel.save();
    }

    @Override
    public void removeBookmark(@NonNull String storyId) {
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
        // Refresh comments
        SQLite.delete().from(CommentModel.class).query();
        // Delete all stories
        // Delete all stories that are not bookmarked...
        SQLite.delete().from(StoryModel.class).where(StoryModel_Table.id.notIn(
                SQLite.select(BookmarkModel_Table.story_id).from(BookmarkModel.class)
        )).query();
    }
}
