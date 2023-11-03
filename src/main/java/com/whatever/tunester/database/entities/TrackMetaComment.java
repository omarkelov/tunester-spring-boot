package com.whatever.tunester.database.entities;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;

import java.util.UUID;

@Entity
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TrackMetaComment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Integer rating;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    private TrackMetaCommentCut cut;
}
