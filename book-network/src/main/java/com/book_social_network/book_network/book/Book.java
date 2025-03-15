package com.book_social_network.book_network.book;

import com.book_social_network.book_network.common.BaseEntity;
import com.book_social_network.book_network.feedback.Feedback;
import com.book_social_network.book_network.history.BookTransactionHistory;
import com.book_social_network.book_network.user.User;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "books")
public class Book extends BaseEntity {

//    @Id
//    private String id;
    private String title;
    private String authorName;
    private String isbn;
    private String synopsis;
    private String bookCover;
    private boolean archived;
    private boolean shareable;

    // @ManytoOne
    @DBRef
    private User owner;

    @DBRef
    private List<Feedback> feedbacks;

    @DBRef
    private List<BookTransactionHistory> histories;

    @Transient
    public double getRate(){
        if(feedbacks == null || feedbacks.isEmpty()){
            return 0.0;
        }
        var rate = this.feedbacks.stream()
                .mapToDouble(feedback -> feedback.getScore())
                .average()
                .orElse(0.0);
        // if rate is 3.23 --> 3.0 || 3.65 --> 4.0
        double roundedRate = Math.round(rate * 10.0) / 10.0;

        return roundedRate;
    }
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
