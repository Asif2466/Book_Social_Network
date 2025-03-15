package com.book_social_network.book_network.book;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;

public record BookRequest(
        String id,

        @NotEmpty(message = "100")
        @NotNull(message = "100")
        String title,

        @NotEmpty(message = "101")
        @NotNull(message = "101")
        String authorName,

        @NotEmpty(message = "102")
        @NotNull(message = "102")
        String isbn,

        @NotEmpty(message = "103")
        @NotNull(message = "103")
        String synopsis,

        boolean shareable
) {
}
