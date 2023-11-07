package com.whatever.tunester.database.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Converter
public class LongByStringMapConverter implements AttributeConverter<Map<String, Long>, String> {
    @Override
    public String convertToDatabaseColumn(Map<String, Long> map) {
        if (map == null || map.isEmpty()) {
            return null;
        }

        try {
            return new ObjectMapper().writeValueAsString(map);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public Map<String, Long> convertToEntityAttribute(String str) {
        if (str == null) {
            return Collections.emptyMap();
        }

        try {
            return new ObjectMapper().readValue(str, new TypeReference<HashMap<String, Long>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return Collections.emptyMap();
    }
}
