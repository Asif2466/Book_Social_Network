package com.book_social_network.book_network.book;

import com.book_social_network.book_network.common.PageResponse;
import com.book_social_network.book_network.file.FileStorageService;
import com.book_social_network.book_network.history.BookTransactionHistory;
import com.book_social_network.book_network.history.BookTransactionHistoryRepository;
import com.book_social_network.book_network.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookTransactionHistoryRepository transcationHistoryRepository;

    private final BookMapper bookMapper;

    private final FileStorageService fileStorageService;

    @Override
    public String
    saveBook(BookRequest request, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Book book = bookMapper.toBook(request);
        book.setOwner(user);
        return bookRepository.save(book).getId();
    }

    @Override
    public BookResponse findById(String id) {
        return bookRepository.findById(id)
                .map(book -> bookMapper.toBookResponse(book))
                .orElseThrow(() -> new BookNotFoundException("No book found with the id: "+id));
    }

    @Override
    public PageResponse<BookResponse> findAllBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllDisplayableBooks(pageable, user.getId());
        List<BookResponse> bookResponses = books.stream()
                .map(book -> bookMapper.toBookResponse(book))
                .toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );

    }

    @Override
    public PageResponse<BookResponse> findAllBooksByOwner(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Book> books = bookRepository.findAllBooksByOwner(pageable, user.getId()); // -> try with findById in future
        List<BookResponse> bookResponses = books.stream()
                .map(book -> bookMapper.toBookResponse(book))
                .toList();
        return new PageResponse<>(
                bookResponses,
                books.getNumber(),
                books.getSize(),
                books.getTotalElements(),
                books.getTotalPages(),
                books.isFirst(),
                books.isLast()
        );
    }

    @Override
    public PageResponse<BorrowedBookResponse> findAllBorrowedBooks(int page, int size, Authentication connectedUser) {

        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedbooks = transcationHistoryRepository.findAllBorrowedBooks(pageable, user.getName()); // -> try with findById in future
        List<BorrowedBookResponse> bookResponses = allBorrowedbooks.stream()
                .map(history -> bookMapper.toBorrowedBookResponse(history))
                .toList();
        return new PageResponse<>(
                bookResponses,
                allBorrowedbooks.getNumber(),
                allBorrowedbooks.getSize(),
                allBorrowedbooks.getTotalElements(),
                allBorrowedbooks.getTotalPages(),
                allBorrowedbooks.isFirst(),
                allBorrowedbooks.isLast()
        );
    }

    @Override
    public PageResponse<BorrowedBookResponse> findAllReturnedBooks(int page, int size, Authentication connectedUser) {
        User user = (User) connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<BookTransactionHistory> allBorrowedbooks = transcationHistoryRepository.findAllReturnedBooks(pageable, user.getName()); // -> try with findById in future
        List<BorrowedBookResponse> bookResponses = allBorrowedbooks.stream()
                .map(history -> bookMapper.toBorrowedBookResponse(history))
                .toList();
        return new PageResponse<>(
                bookResponses,
                allBorrowedbooks.getNumber(),
                allBorrowedbooks.getSize(),
                allBorrowedbooks.getTotalElements(),
                allBorrowedbooks.getTotalPages(),
                allBorrowedbooks.isFirst(),
                allBorrowedbooks.isLast()
        );
    }

    @Override
    public String updateShareableStatus(String bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("No book found with the id: "+bookId));

        if(!Objects.equals(book.getOwner(), connectedUser.getPrincipal())) {
            throw new OperationNotPermittedException("You are not permitted to update this book!!!");
        }
        book.setShareable(!book.isShareable());
        bookRepository.save(book);
        return bookId;
    }

    @Override
    public String updateArchivedStatus(String bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("No book found with the id: "+bookId));

        if(!Objects.equals(book.getOwner(), connectedUser.getPrincipal())) {
            throw new OperationNotPermittedException("You are not permitted to update this book!!!");
        }
        book.setShareable(!book.isArchived());
        bookRepository.save(book);
        return bookId;
    }

    @Override
    public String borrowBook(String bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("No book found with the id: "+bookId));

        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("You are not permitted to borrow this book!!!");
        }
        if(!Objects.equals(book.getOwner(), connectedUser.getPrincipal())) {
            throw new OperationNotPermittedException("You cannot borrow your own book!!!");
        }

        User user = (User) connectedUser.getPrincipal();
        boolean isAlreadyBorrowedByUser = transcationHistoryRepository.existsByBookIdAndUserId(bookId, user.getId());
        if (isAlreadyBorrowedByUser) {
            throw new OperationNotPermittedException("You already borrowed this book and it is still not returned or the return is not approved by the owner");
        }
        boolean isAlreadyBorrowedByOtherUser = transcationHistoryRepository.existsByBookId(bookId);
        if (isAlreadyBorrowedByOtherUser) {
            throw new OperationNotPermittedException("The Requested book is already borrowed by other user");
        }

        BookTransactionHistory bookTransactionHistory = BookTransactionHistory.builder()
                .book(book)
                .user(user)
                .returned(false)
                .returnApproved(false)
                .build();

        return transcationHistoryRepository.save(bookTransactionHistory).getId();
    }

    @Override
    public String returnBorrowedBook(String bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("No book found with the id: "+bookId));

        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("You are not permitted to update this book!!!");
        }
        if(!Objects.equals(book.getOwner(), connectedUser.getPrincipal())) {
            throw new OperationNotPermittedException("You cannot borrow your own book!!!");
        }

        User user = (User) connectedUser.getPrincipal();
        BookTransactionHistory bookTransactionHistory = transcationHistoryRepository.findByBookIdAndUserId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("You did not borrow this book"));
        bookTransactionHistory.setReturned(true);
        return transcationHistoryRepository.save(bookTransactionHistory).getId();
    }

    @Override
    public String approveReturnBorrowedBook(String bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("No book found with the id: "+bookId));

        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("You are not permitted to update this book!!!");
        }
        if(!Objects.equals(book.getOwner(), connectedUser.getPrincipal())) {
            throw new OperationNotPermittedException("You cannot borrow your own book!!!");
        }

        User user = (User) connectedUser.getPrincipal();
        BookTransactionHistory bookTransactionHistory = transcationHistoryRepository.findByBookIdAndOwnerId(bookId, user.getId())
                .orElseThrow(() -> new OperationNotPermittedException("The book is not yet returned, you can't approve it's return"));
        bookTransactionHistory.setReturnApproved(true);
        return transcationHistoryRepository.save(bookTransactionHistory).getId();
    }

    @Override
    public void uploadBookCoverPicture(MultipartFile file, String bookId, Authentication connectedUser) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new BookNotFoundException("No book found with the id: "+bookId));
        User user = (User) connectedUser.getPrincipal();
        var bookCover = fileStorageService.saveFile(file,  user.getId());
        book.setBookCover(bookCover);
        bookRepository.save(book);
    }


}
