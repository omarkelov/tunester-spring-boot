package com.whatever.tunester.database.entities;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.UUID;

import static com.whatever.tunester.constants.AppConstants.NOT_PERSISTED_PREFIX;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Track {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String path;

    private Timestamp lastModified;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("meta")
    private TrackMeta trackMeta;

    @JsonIgnore
    public int getRating() {
        return
            trackMeta != null &&
            trackMeta.getTrackMetaComment() != null &&
            trackMeta.getTrackMetaComment().getRating() != null
                ? trackMeta.getTrackMetaComment().getRating()
                : -1;
    }

    @JsonGetter("id")
    private String getIdGeneratedOnMarshalling() {
        return id != null
            ? id.toString()
            : NOT_PERSISTED_PREFIX + UUID.randomUUID();
    }
}
