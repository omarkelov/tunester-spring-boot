package com.whatever.tunester.database.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.whatever.tunester.database.converters.LongByStringMapConverter;
import com.whatever.tunester.database.converters.StringListConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Directory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonIgnore
    private UUID id;

    @Column(unique = true)
    private String path;

    private Timestamp lastUpdated;

    private Long size;

    @Convert(converter = LongByStringMapConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, Long> ratingCountByRating;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    @JsonIgnore
    private List<String> directoriesFileNames;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    @JsonIgnore
    private List<String> tracksFileNames;

    @Setter
    private transient List<Directory> directories;

    @Setter
    private transient List<Track> tracks;
}
