package com.book_social_network.book_network.book;

import com.book_social_network.book_network.file.FileUtils;
import com.book_social_network.book_network.history.BookTransactionHistory;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    public Book toBook(BookRequest request) {
        return Book.builder()
                .id(request.id())
                .title(request.title())
                .authorName(request.authorName())
                .synopsis(request.synopsis())
                .archived(false)
                .shareable(request.shareable())
                .build();
    }

    public BookResponse toBookResponse( Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getAuthorName(),
                book.getIsbn(),
                book.getSynopsis(),
                book.getOwner().fullName(),
                FileUtils.readFileFromLocation(book.getBookCover()),
                book.getRate(),
                book.isArchived(),
                book.isShareable()
                );
    }

    public BorrowedBookResponse toBorrowedBookResponse(BookTransactionHistory history) {
        return new BorrowedBookResponse(
                history.getBook().getId(),
                history.getBook().getTitle(),
                history.getBook().getAuthorName(),
                history.getBook().getIsbn(),
                history.getBook().getRate(),
                history.isReturned(),
                history.isReturnApproved()
        );
    }
}
