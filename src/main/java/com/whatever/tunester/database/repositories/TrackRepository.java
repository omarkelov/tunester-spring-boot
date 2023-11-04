package com.whatever.tunester.database.repositories;

import com.whatever.tunester.database.entities.Track;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface TrackRepository extends CrudRepository<Track, UUID> {
    Track findByPath(String path);

    @Transactional
    @Modifying
    @Query(value = """
        DELETE t, tm, tmc, tmcc
        FROM track t
        LEFT JOIN track_meta tm
            ON t.track_meta_id = tm.id
        LEFT JOIN track_meta_comment tmc
            ON tm.track_meta_comment_id = tmc.id
        LEFT JOIN track_meta_comment_cut tmcc
            ON tmc.track_meta_comment_cut_id = tmcc.id
        WHERE t.path NOT IN :presentPaths
    """, nativeQuery = true)
    void removeNonPresent(@Param("presentPaths") List<String> presentPaths);
}
