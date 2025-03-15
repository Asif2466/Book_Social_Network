package com.book_social_network.book_network.book;

public record BookResponse(

        String id,
        String title,
        String authorName,
        String isbn,
        String synopsis,
        String owner,
        byte[] cover,
        double rate,
        boolean archived,
        boolean shareable

) {
}
