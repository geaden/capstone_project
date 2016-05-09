package com.geaden.hackernewsreader.backend.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.util.Date;

/**
 * Custom timestamp deserializer to convert time to {@link Date}.
 *
 * @author Gennady Denisov
 */
public class CustomTimestampDeserializer extends JsonDeserializer<Date> {
    @Override
    public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException,
            JsonProcessingException {
        long value = jp.getLongValue();
        return new Date(value * 1000);
    }
}
