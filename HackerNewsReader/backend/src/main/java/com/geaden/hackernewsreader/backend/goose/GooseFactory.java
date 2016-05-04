package com.geaden.hackernewsreader.backend.goose;

import com.gravity.goose.Configuration;
import com.gravity.goose.Goose;

/**
 * Factory class for initializing {@link Goose}.
 *
 * @author Gennady Denisov
 */
public class GooseFactory {
    /**
     * Creates new {@link Goose} instance.
     *
     * @return instance of goose.
     */
    public static Goose create() {
        // set my configuration options for goose
        Configuration configuration = new Configuration();
        configuration.setEnableImageFetching(true);
        return new Goose(configuration);
    }
}
