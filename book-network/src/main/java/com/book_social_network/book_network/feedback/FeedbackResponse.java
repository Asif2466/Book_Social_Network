package com.book_social_network.book_network.feedback;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeedbackResponse {

    private Double score;
    private String comment;
    private boolean ownFeedback;

}
