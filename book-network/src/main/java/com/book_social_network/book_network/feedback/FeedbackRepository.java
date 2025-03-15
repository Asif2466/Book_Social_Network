package com.book_social_network.book_network.feedback;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface FeedbackRepository extends MongoRepository<Feedback, String> {

    /* @Query("""
            SELECT feedback
            FROM Feedback  feedback
            WHERE feedback.book.id = :bookId
""")*/
    @Query("{'book.id': ?0}")
    Page<Feedback> findAllByBookId(String bookId, Pageable pageable);

}
