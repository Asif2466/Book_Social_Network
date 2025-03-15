package com.book_social_network.book_network.history;

import com.book_social_network.book_network.book.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface BookTransactionHistoryRepository extends MongoRepository<BookTransactionHistory, String> {

    /* @Query("""
            SELECT history
            FROM BookTransactionHistory history
            WHERE history.userId = :userId
            """)*/
    @Query("{ 'user.email':  {$eq: ?1}}")
    Page<BookTransactionHistory> findAllBorrowedBooks(Pageable pageable, String name);

    /*@Query("""
            SELECT history
            FROM BookTransactionHistory history
            WHERE history.book.createdBy = :userId
            """)*/
    @Query("{ 'book.owner.email':  {$eq: ?1}}")
    Page<BookTransactionHistory> findAllReturnedBooks(Pageable pageable, String name);

    /* @Query("""
            SELECT
            (COUNT (*) > 0) AS isBorrowed
            FROM BookTransactionHistory bookTransactionHistory
            WHERE bookTransactionHistory.userId = :userId
            AND bookTransactionHistory.book.id = :bookId
            AND bookTransactionHistory.returnApproved = false
            """)*/
    @Query("{'book.id':  ?0, 'user.id': ?1, 'returnApproved': false}")
    boolean existsByBookIdAndUserId(String bookId, String userId);

    /*@Query("""
            SELECT
            (COUNT (*) > 0) AS isBorrowed
            FROM BookTransactionHistory bookTransactionHistory
            WHERE bookTransactionHistory.book.id = :bookId
            AND bookTransactionHistory.returnApproved = false
            """)*/
    @Query("{'book.id':  ?0, 'returnApproved': false}")
    boolean existsByBookId(String bookId);


    /*@Query("""
            SELECT transaction
            FROM BookTransactionHistory  transaction
            WHERE transaction.userId = :userId
            AND transaction.book.id = :bookId
            AND transaction.returned = false
            AND transaction.returnApproved = false
            """)*/
    @Query("{'book.id': ?0, 'user.id': ?1, 'returned':  false, 'returnApproved':  false}")
    Optional<BookTransactionHistory> findByBookIdAndUserId(String bookId, String id);

    @Query("{'book.id': ?0, 'book.owner.id': ?1, 'returned':  true, 'returnApproved':  false}")
    Optional<BookTransactionHistory> findByBookIdAndOwnerId(String bookId, String id);
}
