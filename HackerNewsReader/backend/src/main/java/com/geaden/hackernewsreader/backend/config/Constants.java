package com.geaden.hackernewsreader.backend.config;

import com.google.api.server.spi.Constant;

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

    public static final int NUMBER_OF_STORIES = 100;

    // Minimum width of the content image...
    public static final int IMAGE_MIN_WIDTH = 200;

    /**
     * Helper method to build HN News API to access different data.
     *
     * @param path the path.
     * @return the path to HN News API.
     */
    private static String buildApiUrl(String path) {
        return HACKER_NEWS_API + path;
    }

    public static final String WEB_CLIENT_ID = "872003973467-qv1du1k3aneqkd1ug0cj5vktbuejbng8.apps.googleusercontent.com";
    public static final String ANDROID_CLIENT_ID = "872003973467-iqq97rt1k8qhe9u174lakslr55dohjcv.apps.googleusercontent.com";
    public static final String IOS_CLIENT_ID = "";
    public static final String ANDROID_AUDIENCE = WEB_CLIENT_ID;
    public static final String EMAIL_SCOPE = Constant.API_EMAIL_SCOPE;
    public static final String API_EXPLORER_CLIENT_ID = Constant.API_EXPLORER_CLIENT_ID;
}
