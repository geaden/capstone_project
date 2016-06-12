package com.geaden.android.hackernewsreader.app.data;

import android.test.mock.MockCursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Provides mock cursor.
 *
 * @author Gennady Denisov
 */
public class MockCursorProvider {

    private static Map<Integer, Object> createStoryCursorEntry() {
        Map<Integer, Object> entry = new HashMap<>();
        return entry;
    }

    private static Map<Integer, Object> createBookmarksCursorEntry() {
        Map<Integer, Object> entry = new HashMap<>();
        return entry;
    }

    private static Map<Integer, Object> createCommentCursorEntry() {
        Map<Integer, Object> entry = new HashMap<>();
        return entry;
    }

    public static StoryMockCursor createAllStoriesCursor() {
        List<Map<Integer, Object>> entryList = new ArrayList<>();
        int i = 0;
        while (i < 5) {
            entryList.add(createStoryCursorEntry());
            i++;
        }
        return new StoryMockCursor(entryList);
    }

    public static StoryMockCursor createStoryCursor() {
        List<Map<Integer, Object>> entryList = new ArrayList<>();
        entryList.add(createStoryCursorEntry());
        return new StoryMockCursor(entryList);
    }


    public static StoryMockCursor createBookmarksCursor() {
        List<Map<Integer, Object>> entryList = new ArrayList<>();
        int i = 0;
        while (i < 3) {
            entryList.add(createBookmarksCursorEntry());
            i++;
        }
        return new StoryMockCursor(entryList);
    }

    public static StoryMockCursor createEmptyStoriesCursor() {
        List<Map<Integer, Object>> entryList = new ArrayList<>();
        return new StoryMockCursor(entryList);
    }

    public static StoryMockCursor createQueryStoriesCursor() {
        List<Map<Integer, Object>> entryList = new ArrayList<>();
        entryList.add(createStoryCursorEntry());
        return new StoryMockCursor(entryList);
    }

    public static CommentMockCursor createCommentsCursor() {
        List<Map<Integer, Object>> entryList = new ArrayList<>();

        int i = 0;
        while (i++ < 3) {
            entryList.add(createCommentCursorEntry());
        }
        return new CommentMockCursor(entryList);
    }


    public static class StoryMockCursor extends MockCursor {

        Map<Integer, Object> entry;
        int cursorIndex;
        List<Map<Integer, Object>> entryList;
        Map<String, Integer> columnIndexes;

        {
            columnIndexes = new HashMap<>();
            // TODO: Fill column indexes

        }

        public StoryMockCursor(List<Map<Integer, Object>> entryList) {
            this.entryList = entryList;
        }

        @Override
        public int getCount() {
            return entryList.size();
        }

        @Override
        public boolean moveToFirst() {
            return entryList.size() > 0;
        }

        @Override
        public boolean moveToLast() {
            return cursorIndex < entryList.size();
        }

        @Override
        public boolean moveToNext() {
            cursorIndex++;
            return cursorIndex < entryList.size();
        }

        @Override
        public boolean isAfterLast() {
            return super.isAfterLast();
        }
    }

    public static class CommentMockCursor extends MockCursor {
        Map<Integer, Object> entry;
        int cursorIndex;
        List<Map<Integer, Object>> entryList;
        Map<String, Integer> columnIndexes;

        {
            columnIndexes = new HashMap<>();
            // TODO: Fill column indexes

        }

        public CommentMockCursor(List<Map<Integer, Object>> entryList) {
            this.entryList = entryList;
        }

        @Override
        public int getCount() {
            return entryList.size();
        }

        @Override
        public boolean moveToFirst() {
            return entryList.size() > 0;
        }

        @Override
        public boolean moveToLast() {
            return cursorIndex < entryList.size();
        }

        @Override
        public boolean moveToNext() {
            cursorIndex++;
            return cursorIndex < entryList.size();
        }

        @Override
        public boolean isAfterLast() {
            return super.isAfterLast();
        }

    }
}
