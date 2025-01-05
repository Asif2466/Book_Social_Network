package com.book_social_network.book_network.repository;

import com.book_social_network.book_network.role.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface RoleRepository extends MongoRepository<Role, String> {

    Optional<Role> findByName(String role);

}
