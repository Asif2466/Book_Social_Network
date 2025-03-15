package com.book_social_network.book_network.book;

public record BorrowedBookResponse (
        String id,
        String title,
        String authorName,
        String isbn,
        double synopsis,
        boolean returned,
        boolean returnApproved
){
}
