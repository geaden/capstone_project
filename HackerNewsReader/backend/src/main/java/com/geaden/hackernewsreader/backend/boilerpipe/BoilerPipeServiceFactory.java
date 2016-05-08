package com.geaden.hackernewsreader.backend.boilerpipe;

/**
 * Factory method to instantiate {@link BoilerpipeContentExtractionService}.
 *
 * @author Gennady Denisov
 */
public class BoilerPipeServiceFactory {
    private BoilerPipeServiceFactory() {

    }

    public static BoilerpipeContentExtractionService create() {
        return new BoilerpipeContentExtractionService();
    }
}
