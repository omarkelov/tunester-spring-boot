package com.whatever.tunester.database.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Getter;

import java.util.UUID;

@Entity
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrackMetaCommentCut {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonIgnore
    private UUID id;

    private Double originalDuration;

    private Double start;

    private Double end;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    transient private Double fadeInStart;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    transient private Double fadeInDuration;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    transient private Double fadeOutStart;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    transient private Double fadeOutDuration;

    public TrackMetaCommentCut update(TrackMetaCommentCut other) {
        if (originalDuration == null) {
            originalDuration = other.originalDuration;
        }

        if (other.start != null) {
            start = start != null
                ? start + other.start
                : other.start;
        }

        if (other.end != null) {
            Double startValue = start != null ? start : 0;
            Double otherStartValue = other.start != null ? other.start : 0;
            Double newDuration = other.end - otherStartValue;
            end = startValue + newDuration;
        }

        return this;
    }
}
