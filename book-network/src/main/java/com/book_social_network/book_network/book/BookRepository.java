package com.book_social_network.book_network.book;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface BookRepository extends MongoRepository<Book, String>{

    /*
db.books.find({
  "archived": false,
  "shareable": true,
  "ownerId": { "$ne": "userIdValue" }
})
.skip(5)
.limit(5)
.sort({ "title": 1 });
    */
    @Query("{ 'archived': false, 'shareable': true, 'owner.id': { $ne: ?1 } }")
    Page<Book> findAllDisplayableBooks(Pageable pageable, String id);

    @Query("{ 'archived': false, 'shareable': true, 'owner.id': { $eq: ?1 } }")
    Page<Book> findAllBooksByOwner(Pageable pageable, String id);
}
