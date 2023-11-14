package com.whatever.tunester.database.repositories;

import com.whatever.tunester.database.entities.User;
import org.springframework.data.repository.CrudRepository;

import java.util.UUID;

public interface UserRepository extends CrudRepository<User, UUID> {
	User findByUsername(String username);
}
