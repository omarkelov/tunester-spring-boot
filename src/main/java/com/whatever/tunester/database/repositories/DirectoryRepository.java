package com.whatever.tunester.database.repositories;

import com.whatever.tunester.database.entities.Directory;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.UUID;

public interface DirectoryRepository extends CrudRepository<Directory, UUID> {
    Directory findByPath(String path);

    List<Directory> findAllByOrderByPath();
}
