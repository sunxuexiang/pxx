package com.wanmi.ms.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Custom Jackson deserializer for transforming a JSON object to a Joda DateTime object.
 */
public class CustomDateTimeDeserializer extends JsonDeserializer<Date> {

    @SuppressWarnings("deprecation")
    @Override
    public Date deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_STRING) {
            String str = jp.getText().trim();
            try {
                return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").parse(str);
            } catch (ParseException e) {
                throw ctxt.mappingException(handledType());
            }
        }
        if (t == JsonToken.VALUE_NUMBER_INT) {
            return new Date(jp.getLongValue());
        }
        throw ctxt.mappingException(handledType());
    }
}
