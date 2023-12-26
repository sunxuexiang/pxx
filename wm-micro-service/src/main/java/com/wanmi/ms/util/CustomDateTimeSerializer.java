package com.wanmi.ms.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Custom Jackson serializer for transforming a Joda DateTime object to JSON.
 */
public class CustomDateTimeSerializer extends JsonSerializer<Date> {

    @Override
    public void serialize(Date value, JsonGenerator generator,
                          SerializerProvider serializerProvider)
            throws IOException {
        if(value == null){
            generator.writeNull();
        }else {
            generator.writeString(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(value));
        }
    }

}
