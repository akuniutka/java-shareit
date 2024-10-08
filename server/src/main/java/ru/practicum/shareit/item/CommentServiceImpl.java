package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.common.exception.ValidationException;

import java.util.List;
import java.util.Objects;

@Service
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
class CommentServiceImpl implements CommentService {

    private final CommentRepository repository;
    private final BookingService bookingService;

    @Override
    @Transactional
    public Comment addComment(final Comment comment, final long id, final long userId) {
        Objects.requireNonNull(comment, "Cannot create comment: is null");
        final List<Booking> bookings = bookingService.findAllCompleteBookingByUserIdAndItemId(userId, id);
        if (bookings.isEmpty()) {
            throw new ValidationException("id", "no complete bookings of item by user");
        }
        final Booking booking = bookings.getFirst();
        comment.setItem(booking.getItem());
        comment.setAuthor(booking.getBooker());
        final Comment createdComment = repository.save(comment);
        log.info("Created comment with id = {}: {}", createdComment.getId(), createdComment);
        return createdComment;
    }
}
