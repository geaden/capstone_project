/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Endpoints Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloEndpoints
*/

package com.geaden.hackernewsreader.backend.spi;

import com.geaden.hackernewsreader.backend.config.Constants;
import com.geaden.hackernewsreader.backend.domain.Story;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;

import java.util.Collection;

import static com.geaden.hackernewsreader.backend.service.OfyService.ofy;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "hackernews",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.hackernewsreader.geaden.com",
                ownerName = "backend.hackernewsreader.geaden.com",
                packagePath = ""
        )
)
public class HackernewsApi {

    /**
     * Method to get all top stories (limited to 200 items).
     *
     * @return current top stories from the hacker news.
     */
    @ApiMethod(name = "getTopstories")
    public Collection<Story> getTopstories() {
        return ofy().load().type(Story.class).order("-time").limit(Constants.NUMBER_OF_STORIES).list();
    }
}
