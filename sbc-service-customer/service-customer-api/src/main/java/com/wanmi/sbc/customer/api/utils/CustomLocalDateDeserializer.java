package com.wanmi.sbc.customer.api.utils;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.time.LocalDate;

/**
 * Custom Jackson deserializer for transforming a JSON object (using the ISO 8601 date format)
 * to a Joda LocalDate object.
 */
public class CustomLocalDateDeserializer extends JsonDeserializer<LocalDate> {
    @SuppressWarnings("deprecation")
    @Override
    public LocalDate deserialize(JsonParser jp, DeserializationContext ctxt)
            throws IOException {
        JsonToken t = jp.getCurrentToken();
        if (t == JsonToken.VALUE_STRING) {
            String text = jp.getText();
            if(StringUtils.isBlank(text)) {
                return null;
            }
            String str = text.trim();
            return LocalDate.parse(str);
        }
        if (t == JsonToken.VALUE_NUMBER_INT) {
            return new java.sql.Date(jp.getLongValue()).toLocalDate();
        }
        throw ctxt.mappingException(handledType());
    }
}
