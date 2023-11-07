package com.whatever.tunester.database.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.List;

@Converter
public class StringListConverter implements AttributeConverter<List<String>, String> {
    @Override
    public String convertToDatabaseColumn(List<String> strList) {
        if (strList == null || strList.isEmpty()) {
            return null;
        }

        try {
            return new ObjectMapper().writeValueAsString(strList);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public List<String> convertToEntityAttribute(String str) {
        if (str == null || str.equals("")) {
            return null;
        }

        try {
            return List.of(new ObjectMapper().readValue(str, String[].class));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }
}
