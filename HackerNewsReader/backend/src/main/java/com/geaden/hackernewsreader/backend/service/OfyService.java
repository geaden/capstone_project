package com.geaden.hackernewsreader.backend.service;

import com.geaden.hackernewsreader.backend.domain.AppEngineUser;
import com.geaden.hackernewsreader.backend.domain.Bookmark;
import com.geaden.hackernewsreader.backend.domain.Comment;
import com.geaden.hackernewsreader.backend.domain.Profile;
import com.geaden.hackernewsreader.backend.domain.Story;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.ObjectifyFactory;
import com.googlecode.objectify.ObjectifyService;

/**
 * Objectify Service to register entities.
 *
 * @author Gennady Denisov
 */
public class OfyService {
    /**
     * This static block ensure the entity registration.
     */
    static {
        register();
    }

    static void register() {
        factory().register(Story.class);
        factory().register(Comment.class);
        factory().register(Profile.class);
        factory().register(Bookmark.class);
        factory().register(AppEngineUser.class);
    }

    /**
     * Use this static method for getting the Objectify service object in order to make sure the
     * above static block is executed before using Objectify.
     *
     * @return Objectify service object.
     */
    public static Objectify ofy() {
        return ObjectifyService.ofy();
    }

    /**
     * Use this static method for getting the Objectify service factory.
     *
     * @return ObjectifyFactory.
     */
    public static ObjectifyFactory factory() {
        return ObjectifyService.factory();
    }
}
