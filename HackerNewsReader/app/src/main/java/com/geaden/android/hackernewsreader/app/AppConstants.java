package com.geaden.android.hackernewsreader.app;

/**
 * Application constants.
 *
 * @author Gennady Denisov
 */
public final class AppConstants {

    private AppConstants() {

    }

    /**
     * Your WEB CLIENT ID from the API Access screen of the Developer Console for your project. This
     * is NOT the Android client id from that screen.
     *
     * @see <a href="https://developers.google.com/console">https://developers.google.com/console</a>
     */
    public static final String WEB_CLIENT_ID = BuildConfig.GOOGLE_WEB_CLIENT_ID;
    /**
     * The audience is defined by the web client id, not the Android client id.
     */
    public static final String AUDIENCE = "server:client_id:" + WEB_CLIENT_ID;
}
