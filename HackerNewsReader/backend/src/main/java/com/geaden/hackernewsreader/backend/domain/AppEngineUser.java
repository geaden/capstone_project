package com.geaden.hackernewsreader.backend.domain;

import com.google.appengine.api.users.User;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * POJO representing AppEngine user entity.
 *
 * @author Gennady Denisov
 */
@Entity
public class AppEngineUser {
    @Id
    private String email;
    private User user;

    private AppEngineUser() {
    }

    public AppEngineUser(User user) {
        this.user = user;
        this.email = user.getEmail();
    }

    public User getUser() {
        return user;
    }

    public Key<AppEngineUser> getKey() {
        return Key.create(AppEngineUser.class, email);
    }
}
