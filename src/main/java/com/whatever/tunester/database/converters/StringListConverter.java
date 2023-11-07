package com.whatever.tunester.database.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
    @Override
    public String convertToDatabaseColumn(List<String> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        try {
            return new ObjectMapper().writeValueAsString(list);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<String> convertToEntityAttribute(String str) {
        if (str == null) {
            return Collections.emptyList();
        }

        try {
            return new ObjectMapper().readValue(str, new TypeReference<ArrayList<String>>() {});
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }
}
