package com.geaden.hackernewsreader.backend.config;

/**
 * Constants holder class.
 *
 * @author Gennady Denisov
 */
public final class Constants {

    // Prevent instantiation
    private Constants() {

    }

    public static final String HACKER_NEWS_API = "https://hacker-news.firebaseio.com/v0/";

    public static final String HACKER_NEWS_API_STORIES = buildApiUrl("topstories");

    public static final String HACKER_NEWS_API_ITEM = buildApiUrl("item");

    public static final int NUMBER_OF_STORIES = 1;

    public static final String HACKER_NEWS_READER_BUCKET = "gs://hacker-news-reader/";

    /**
     * Helper method to build HN News API to access different data.
     *
     * @param path the path.
     * @return the path to HN News API.
     */
    private static String buildApiUrl(String path) {
        return HACKER_NEWS_API + path;
    }
}
