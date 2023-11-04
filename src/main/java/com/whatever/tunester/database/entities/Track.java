package com.whatever.tunester.database.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
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
    private TrackMeta trackMeta;

    public int getRating() {
        return
            trackMeta != null &&
            trackMeta.getTrackMetaComment() != null &&
            trackMeta.getTrackMetaComment().getRating() != null
                ? trackMeta.getTrackMetaComment().getRating()
                : -1;
    }
}
