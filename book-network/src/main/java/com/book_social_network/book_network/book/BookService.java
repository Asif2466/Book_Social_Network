package com.book_social_network.book_network.book;

import com.book_social_network.book_network.common.PageResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

public interface BookService {


    String saveBook(BookRequest request, Authentication connectedUser);

    BookResponse findById(String id);

    PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser);

    PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser);

    PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser);

    PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser);

    String updateShareableStatus(String bookId, Authentication connectedUser);

    String updateArchivedStatus(String bookId, Authentication connectedUser);

    String borrowBook(String bookId, Authentication connectedUser);

    String returnBorrowedBook(String bookId, Authentication connectedUser);

    String approveReturnBorrowedBook(String bookId, Authentication connectedUser);

    void uploadBookCoverPicture(MultipartFile file, String bookId, Authentication connectedUser);
}
