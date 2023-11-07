package com.whatever.tunester.database.converters;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Converter
public class ListConverter<T> implements AttributeConverter<List<T>, String> {

    @Override
    public String convertToDatabaseColumn(List<T> list) {
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
    public List<T> convertToEntityAttribute(String str) {
        if (str == null) {
            return Collections.emptyList();
        }

        try {
            return Arrays.stream(new ObjectMapper().readValue(str, Object[].class)).map(o -> (T) o).toList();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }
}
