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
    // TODO: Put into build config
    public static final String WEB_CLIENT_ID = "872003973467-qv1du1k3aneqkd1ug0cj5vktbuejbng8.apps.googleusercontent.coms";
    /**
     * The audience is defined by the web client id, not the Android client id.
     */
    public static final String AUDIENCE = "server:client_id:" + WEB_CLIENT_ID;
}
