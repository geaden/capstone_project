package com.geaden.hackernewsreader.backend.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.geaden.hackernewsreader.backend.domain.Story;

import org.junit.Test;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Date;

import static org.junit.Assert.assertTrue;

/**
 * Test for custom timestamp deserializer.
 *
 * @author Gennady Denisov
 */
public class CustomTimestampDeserializerTest {
    @Test
    public void testDeserializeTimestamp() throws URISyntaxException, IOException {
        Story story = new ObjectMapper().reader(Story.class).readValue("{\"time\": 1175714200}");
        assertTrue(story.getTime().equals(new Date(1175714200000L)));
    }
}