package ru.practicum.shareit.item;

import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.common.LogListener;
import ru.practicum.shareit.common.exception.ValidationException;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.common.CommonUtils.assertLogs;
import static ru.practicum.shareit.item.ItemUtils.makeCommentProxy;
import static ru.practicum.shareit.item.ItemUtils.makeTestBooking;

class CommentServiceImplTest {

    private static final LogListener logListener = new LogListener(CommentServiceImpl.class);

    private static final long USER_ID = 42L;
    private static final long ITEM_ID = 13L;

    private AutoCloseable openMocks;

    @Mock
    private CommentRepository mockRepository;

    @Mock
    private BookingService bookingService;

    private InOrder inOrder;

    private CommentService service;

    @BeforeEach
    void setUo() {
        openMocks = MockitoAnnotations.openMocks(this);
        service = new CommentServiceImpl(mockRepository, bookingService);
        logListener.startListen();
        logListener.reset();
        inOrder = Mockito.inOrder(mockRepository, bookingService);
    }

    @AfterEach
    void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockRepository, bookingService);
        openMocks.close();
    }

    @Test
    void testAddComment() throws JSONException, IOException {
        final Booking booking = makeTestBooking();
        booking.getBooker().setName("John Doe");
        when(bookingService.findAllCompleteBookingByUserIdAndItemId(USER_ID, ITEM_ID)).thenReturn(List.of(booking));
        when(mockRepository.save(makeCommentProxy().withId(null))).thenReturn(makeCommentProxy());

        final Comment actual = service.addComment(makeCommentProxy().withId(null).withAuthorName(null));

        inOrder.verify(bookingService).findAllCompleteBookingByUserIdAndItemId(USER_ID, ITEM_ID);
        inOrder.verify(mockRepository).save(makeCommentProxy().withId(null));
        assertThat(actual, equalTo(makeCommentProxy()));
        assertLogs(logListener.getEvents(), "add_comment.json", getClass());
    }

    @Test
    void testAddCommentWhenNoCompleteBooking() {
        when(bookingService.findAllCompleteBookingByUserIdAndItemId(USER_ID, ITEM_ID)).thenReturn(List.of());

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> service.addComment(makeCommentProxy().withId(null).withAuthorName(null)));

        verify(bookingService).findAllCompleteBookingByUserIdAndItemId(USER_ID, ITEM_ID);
        assertThat(exception.getProperty(), equalTo("item.id"));
        assertThat(exception.getViolation(), equalTo("no complete bookings of item by user"));
    }
}