package com.book_social_network.book_network.feedback;

import com.book_social_network.book_network.book.Book;
import com.book_social_network.book_network.book.BookNotFoundException;
import com.book_social_network.book_network.book.BookRepository;
import com.book_social_network.book_network.book.OperationNotPermittedException;
import com.book_social_network.book_network.common.PageResponse;
import com.book_social_network.book_network.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class FeedbackServiceImpl implements FeedbackService {

    private final BookRepository bookRepository;

    private final FeedbackMapper feedbackMapper;

    private final FeedbackRepository feedbackRepository;

    @Override
    public String saveFeedback(FeedbackRequest request, Authentication connectedUser) {

        Book book = bookRepository.findById(request.bookId())
                .orElseThrow(() -> new BookNotFoundException("No book found with the id: "+ request.bookId()));

        if(book.isArchived() || !book.isShareable()){
            throw new OperationNotPermittedException("You are not permitted to give a feedback for an archived or not shareable book!!!");
        }
        if(!Objects.equals(book.getOwner(), connectedUser.getPrincipal())) {
            throw new OperationNotPermittedException("You cannot give a feedback to your own book!!!");
        }

        Feedback feedback = feedbackMapper.toFeedback(request);
        return feedbackRepository.save(feedback).getId();
    }

    @Override
    public PageResponse<FeedbackResponse> findAllFeedbacksByBooks(String bookId, int page, int size, Authentication connectedUser) {
        User user = (User)connectedUser.getPrincipal();
        Pageable pageable = PageRequest.of(page, size);
        Page<Feedback> feedbacks = feedbackRepository.findAllByBookId(bookId, pageable);
        List<FeedbackResponse> feedbackResponses = feedbacks.stream()
                .map(f -> feedbackMapper.toFeedbackResponse(f, user.getId()))
                .toList();

        return new PageResponse<>(
                feedbackResponses,
                feedbacks.getNumber(),
                feedbacks.getSize(),
                feedbacks.getTotalElements(),
                feedbacks.getTotalPages(),
                feedbacks.isFirst(),
                feedbacks.isLast()
        );
    }
}
