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
        DELETE t, tm
        FROM track t
        JOIN track_meta tm
            ON t.track_meta_id = tm.id
        WHERE t.path NOT IN :presentPaths
    """, nativeQuery = true)
    void removeNonPresent(@Param("presentPaths") List<String> presentPaths);
}
