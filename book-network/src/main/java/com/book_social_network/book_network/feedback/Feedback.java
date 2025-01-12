package com.book_social_network.book_network.feedback;

import com.book_social_network.book_network.book.Book;
import com.book_social_network.book_network.common.BaseEntity;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "feedbacks")
public class Feedback extends BaseEntity {

//    @Id
//    private String id;
    private Double score; // 1-5 stars
    private String comment;

    @DBRef
    private Book book;

//    @CreatedDate
//    @NotNull
//    private LocalDate createdDate;
//
//    @LastModifiedDate
//    private LocalDate lastModifiedDate;
//
//    @CreatedBy
//    @NotNull
//    private String createdBy;
//
//    @LastModifiedBy
//    private String lastModifiedBy;

}
