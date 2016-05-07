package com.geaden.hackernewsreader.backend.service;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

/**
 * OfyHelper, a ServletContextListener, is setup in web.xml to run before a JSP is run.  This is
 * required to let JSP's access Ofy..
 *
 * @author Gennady Denisov
 */
public class OfyHelper implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // This will be invoked as part of a warmup request, or the first user request if no warmup
        // request.
        OfyService.register();
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // App Engine does not currently invoke this method.
    }
}
