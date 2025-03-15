package com.book_social_network.book_network.feedback;

import com.book_social_network.book_network.common.PageResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;

public interface FeedbackService {

    String saveFeedback(@Valid FeedbackRequest request, Authentication connectedUser);

    PageResponse<FeedbackResponse> findAllFeedbacksByBooks(String bookId, int page, int size, Authentication connectedUser);
}
