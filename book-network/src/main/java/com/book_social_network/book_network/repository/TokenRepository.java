package com.book_social_network.book_network.repository;

import com.book_social_network.book_network.token.Token;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface TokenRepository extends MongoRepository<Token, String> {

    Optional<Token> findByToken(String token);

}
