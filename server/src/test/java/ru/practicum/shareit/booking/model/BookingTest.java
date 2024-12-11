package ru.practicum.shareit.booking.model;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class BookingTest {

    @Test
    void testGetId() {
        Booking booking = new Booking();
        booking.setId(1);
        assertEquals(1L, booking.getId());
    }

    @Test
    void testGetStart() {
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now());
        assertEquals(LocalDateTime.now(), booking.getStart());
    }

    @Test
    void testGetEnd() {
        Booking booking = new Booking();
        booking.setEnd(LocalDateTime.now().plusDays(1));
        assertEquals(LocalDateTime.now().plusDays(1), booking.getEnd());
    }

    @Test
    void testGetItem() {
        Booking booking = new Booking();
        Item item = new Item();
        booking.setItem(item);
        assertEquals(item, booking.getItem());
    }

    @Test
    void testGetBooker() {
        Booking booking = new Booking();
        User booker = new User();
        booking.setBooker(booker);
        assertEquals(booker, booking.getBooker());
    }

    @Test
    void testGetStatus() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.WAITING);
        assertEquals(BookingStatus.WAITING, booking.getStatus());
    }

    @Test
    void testSetId() {
        Booking booking = new Booking();
        booking.setId(1);
        assertEquals(1, booking.getId());
    }

    @Test
    void testSetStart() {
        Booking booking = new Booking();
        LocalDateTime start = LocalDateTime.now();
        booking.setStart(start);
        assertEquals(start, booking.getStart());
    }

    @Test
    void testSetEnd() {
        Booking booking = new Booking();
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        booking.setEnd(end);
        assertEquals(end, booking.getEnd());
    }

    @Test
    void testSetItem() {
        Booking booking = new Booking();
        Item item = new Item();
        booking.setItem(item);
        assertEquals(item, booking.getItem());
    }

    @Test
    void testSetBooker() {
        Booking booking = new Booking();
        User booker = new User();
        booking.setBooker(booker);
        assertEquals(booker, booking.getBooker());
    }

    @Test
    void testSetStatus() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.APPROVED);
        assertEquals(BookingStatus.APPROVED, booking.getStatus());
    }
}
