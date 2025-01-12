package com.book_social_network.book_network.history;

import com.book_social_network.book_network.book.Book;
import com.book_social_network.book_network.common.BaseEntity;
import com.book_social_network.book_network.user.User;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "book_transaction_history")
public class BookTransactionHistory extends BaseEntity {

    //user relationship
    @DBRef
    private User user;
    //book relationship
    @DBRef
    private Book book;

    private boolean returned;
    private boolean returnApproved;


}
