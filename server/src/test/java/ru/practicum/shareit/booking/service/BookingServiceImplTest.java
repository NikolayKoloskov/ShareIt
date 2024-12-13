package ru.practicum.shareit.booking.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSaveDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exceptions.ForbiddenException;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.NotValidException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.Collection;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
class BookingServiceImplTest {
    private final EntityManager entityManager;
    private final BookingService bookingService;
    private final BookingMapper mapper;

    private Booking bookingExpected;
    private Item itemExpected;
    private User userExpected;

    private static final int UNAVAILABLE_ID = 999;

    @BeforeEach
    public void setUp() {

        userExpected = new User();
        userExpected.setId(30);
        userExpected.setName("user3");
        userExpected.setEmail("user3@somemail.ru");

        itemExpected = new Item();
        itemExpected.setId(30);
        itemExpected.setName("item3");
        itemExpected.setDescription("description3");
        itemExpected.setAvailable(true);
        itemExpected.setOwner(userExpected);

        bookingExpected = new Booking();
        bookingExpected.setId(1);
        bookingExpected.setItem(itemExpected);
        bookingExpected.setBooker(userExpected);
        bookingExpected.setStatus(BookingStatus.WAITING);
        bookingExpected.setStart(LocalDateTime.of(2024, 12, 11, 11, 11, 11));
        bookingExpected.setEnd(LocalDateTime.of(2024, 12, 11, 11, 12, 11));


    }

    @Test
    public void mappingTest() {
        Booking booking = null;

        BookingDto bookingDto = mapper.map(booking);
        assertThat(bookingDto, equalTo(null));
        BookingSaveDto bookingSaveDtoDto = null;
        Booking booking1 = mapper.map(bookingSaveDtoDto);
        assertThat(booking1, equalTo(null));
    }

    @Test
    public void addBookingTest() {
        int userId = userExpected.getId();
        BookingSaveDto bookingSaveDto = new BookingSaveDto();
        bookingSaveDto.setItemId(itemExpected.getId());
        bookingSaveDto.setStart(bookingExpected.getStart());
        bookingSaveDto.setEnd(bookingExpected.getEnd());

        bookingService.addBooking(userId, bookingSaveDto);
        TypedQuery<Booking> query =
                entityManager.createQuery("SELECT b FROM Booking b WHERE b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", bookingExpected.getId()).getSingleResult();
        BookingDto bookingDto = mapper.map(booking);

        BookingDto bookingDtoExpected = mapper.map(bookingExpected);
        assertThat(bookingDto, allOf(
                hasProperty("id",
                        equalTo(bookingDtoExpected.getId())),
                hasProperty("start",
                        equalTo(bookingDtoExpected.getStart())),
                hasProperty("end",
                        equalTo(bookingDtoExpected.getEnd())),
                hasProperty("item",
                        allOf(hasProperty("id", equalTo(bookingDtoExpected.getItem().getId())))),
                hasProperty("booker",
                        allOf(hasProperty("id", equalTo(bookingDtoExpected.getBooker().getId())))),
                hasProperty("status",
                        equalTo(bookingDtoExpected.getStatus()))
        ));
    }

    @Test
    void addBookingNotAvailableTest() {

        BookingSaveDto bookingSaveDto = new BookingSaveDto();
        // When
        bookingSaveDto.setItemId(UNAVAILABLE_ID);
        bookingSaveDto.setStart(bookingExpected.getStart());
        bookingSaveDto.setEnd(bookingExpected.getEnd());
        // Then
        assertThrows(ForbiddenException.class, () -> bookingService.addBooking(bookingExpected.getId(), bookingSaveDto));
    }

    @Test
    void addBookingUserNotFoundTest() {
        BookingSaveDto bookingSaveDto = new BookingSaveDto();
        bookingSaveDto.setItemId(itemExpected.getId());
        bookingSaveDto.setStart(bookingExpected.getStart());
        bookingSaveDto.setEnd(bookingExpected.getEnd());
        assertThrows(ForbiddenException.class, () -> bookingService.addBooking(UNAVAILABLE_ID, bookingSaveDto));
    }

    @Test
    void addBookingItemNotAvailableTest() {
        BookingSaveDto bookingSaveDto = new BookingSaveDto();
        bookingSaveDto.setItemId(50);
        bookingSaveDto.setStart(bookingExpected.getStart());
        bookingSaveDto.setEnd(bookingExpected.getEnd());
        assertThrows(NotValidException.class, () -> bookingService.addBooking(userExpected.getId(), bookingSaveDto));
    }

    @Test
    void addBookingWrongDateTest() {
        BookingSaveDto bookingSaveDto = new BookingSaveDto();
        bookingSaveDto.setItemId(itemExpected.getId());
        bookingSaveDto.setStart(bookingExpected.getEnd());
        bookingSaveDto.setEnd(bookingExpected.getStart());
        assertThrows(NotValidException.class, () -> bookingService.addBooking(userExpected.getId(), bookingSaveDto));
    }

    @Test
    void manageBookingTest() {
        int userId = 10;
        int bookingId = 10;
        boolean approved = true;

        bookingService.manageBooking(userId, bookingId, approved);
        TypedQuery<Booking> query = entityManager.createQuery("SELECT b FROM Booking AS b WHERE b.id = :id", Booking.class);
        Booking booking = query.setParameter("id", bookingId).getSingleResult();
        BookingDto bookingDto = mapper.map(booking);

        int bookerExpectedId = 20;
        assertThat(bookingDto, allOf(
                hasProperty("id", equalTo(bookingId)),
                hasProperty("booker", allOf(hasProperty("id", equalTo(bookerExpectedId)))),
                hasProperty("status", equalTo(BookingStatus.APPROVED))
        ));
    }

    @Test
    void manageBookingWrongUserTest() {
        int userId = 10;
        int bookingId = 20;
        boolean approved = true;
        assertThrows(NotValidException.class, () -> bookingService.manageBooking(userId, bookingId, approved));
    }

    @Test
    void getBookingTest() {
        int userId = 20;
        int bookingId = 10;

        BookingDto bookingDto = bookingService.getBooking(userId, bookingId);

        BookingDto bookingId10DtoExpected = new BookingDto();
        bookingId10DtoExpected.setId(10);
        LocalDateTime start = LocalDateTime.of(2024, 12, 11, 10, 10, 10);
        bookingId10DtoExpected.setStart(start);
        LocalDateTime end = LocalDateTime.of(2024, 12, 11, 11, 11, 11);
        bookingId10DtoExpected.setEnd(end);
        ItemDto item = new ItemDto();
        item.setId(10);
        bookingId10DtoExpected.setItem(item);
        UserDto booker = new UserDto();
        booker.setId(20);
        bookingId10DtoExpected.setBooker(booker);
        bookingId10DtoExpected.setStatus(BookingStatus.APPROVED);

        assertThat(bookingDto, allOf(
                hasProperty("id",
                        equalTo(bookingId10DtoExpected.getId())),
                hasProperty("start",
                        equalTo(bookingId10DtoExpected.getStart())),
                hasProperty("end",
                        equalTo(bookingId10DtoExpected.getEnd())),
                hasProperty("item",
                        allOf(hasProperty("id", equalTo(bookingId10DtoExpected.getItem().getId())))),
                hasProperty("booker",
                        allOf(hasProperty("id", equalTo(bookingId10DtoExpected.getBooker().getId())))),
                hasProperty("status",
                        equalTo(bookingId10DtoExpected.getStatus()))
        ));
    }

    @Test
    void getBookingWrongUserTest() {
        int userId = 50;
        int bookingId = 10;
        assertThrows(NotValidException.class, () -> bookingService.getBooking(userId, bookingId));
    }

    @Test
    void addBookingWrongItemTest() {
        BookingSaveDto bookingSaveDto = new BookingSaveDto();
        bookingSaveDto.setItemId(UNAVAILABLE_ID);
        bookingSaveDto.setEnd(bookingExpected.getEnd());
        bookingSaveDto.setStart(bookingExpected.getStart());
        assertThrows(ItemNotFoundException.class, () -> bookingService.addBooking(10, bookingSaveDto));
    }

    @Test
    void getAllUserItemsBookingsWaitingTest() {
        int userId = 10;
        BookingState state = BookingState.ALL;

        Collection<BookingDto> bookings = bookingService.getAllUserItemsBookings(userId, state);

        assertThat(bookings, hasSize(3));
    }

    @Test
    void getAllUserBookingsTest() {
        int userId = 20;
        BookingState state = BookingState.ALL;

        Collection<BookingDto> bookings = bookingService.getAllUserBookings(userId, state);
        bookings.forEach(booking -> assertThat(booking, allOf(
                hasProperty("booker", allOf(
                        hasProperty("id", equalTo(userId))
                )),
                hasProperty("status", notNullValue()))
        ));
    }

    @Test
    void getAllUserBookingsWaitingTest() {
        int userId = 20;
        BookingState state = BookingState.WAITING;

        Collection<BookingDto> bookings = bookingService.getAllUserBookings(userId, state);
        bookings.forEach(booking -> assertThat(booking, allOf(
                hasProperty("booker", allOf(
                        hasProperty("id", equalTo(userId))
                )),
                hasProperty("status", notNullValue())))
        );
    }

    @Test
    void getAllUserBookingsRejectedTest() {
        int userId = 20;
        BookingState state = BookingState.REJECTED;

        Collection<BookingDto> bookings = bookingService.getAllUserBookings(userId, state);
        bookings.forEach(booking -> assertThat(booking, allOf(
                hasProperty("booker", allOf(
                        hasProperty("id", equalTo(userId))
                )),
                hasProperty("status", notNullValue()))
        ));
    }

    @Test
    void getAllUserBookingsCurrentTest() {
        int userId = 20;
        BookingState state = BookingState.CURRENT;

        Collection<BookingDto> bookings = bookingService.getAllUserBookings(userId, state);
        bookings.forEach(booking -> assertThat(booking, allOf(
                hasProperty("booker", allOf(
                        hasProperty("id", equalTo(userId))
                )),
                hasProperty("status", notNullValue()))
        ));
    }
}