package com.geaden.hackernewsreader.backend.firebase;

import com.firebase.client.Firebase;

/**
 * {}.
 *
 * @author Gennady Denisov
 */
public class FirebaseFactory {
    public static Firebase create(String api) {
        return new Firebase(api);
    }
}
