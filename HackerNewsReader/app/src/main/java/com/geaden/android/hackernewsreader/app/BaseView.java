package com.geaden.android.hackernewsreader.app;

/**
 * Base View interface that any view in the app should be extended from.
 *
 * @author Gennady Denisov
 */
public interface BaseView<T> {

    void setPresenter(T presenter);
}
