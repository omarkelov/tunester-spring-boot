package com.whatever.tunester.database.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

@Entity
@Getter
@Setter
@Accessors(chain = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrackMetaComment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @JsonIgnore
    private UUID id;

    private Integer version;

    private Integer rating;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonProperty("cut")
    private TrackMetaCommentCut trackMetaCommentCut;

    public TrackMetaComment incrementedVersion() {
        if (version != null) {
            version++;
        } else {
            version = 1;
        }

        return this;
    }
}
