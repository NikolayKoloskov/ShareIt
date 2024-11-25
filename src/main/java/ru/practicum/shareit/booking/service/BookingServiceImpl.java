package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingSaveDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.ItemNotFoundException;
import ru.practicum.shareit.exceptions.NotValidException;
import ru.practicum.shareit.exceptions.UserNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collection;

@Slf4j
@Service
@AllArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    public BookingDto addBooking(int userId, BookingSaveDto bookingSaveDto) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new UserNotFoundException("Пользователь с ID " + userId + " не найден"));

        int itemId = bookingSaveDto.getItemId();
        Item item = itemRepository.findById(itemId).orElseThrow(
                () -> new ItemNotFoundException("Предмет с ID " + itemId + " не найден"));
        if (!item.isAvailable()) {
            throw new NotValidException(Item.class, "Не доступно для бронирования");
        }

        Booking booking = bookingMapper.map(bookingSaveDto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(BookingStatus.WAITING);
        Booking savedBooking = bookingRepository.save(booking);
        log.info("booking saved to repo - {}", savedBooking);

        return bookingMapper.map(savedBooking);
    }

    @Override
    public BookingDto manageBooking(int userId, int bookingId, boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ItemNotFoundException("Бронирование  с ID " + bookingId + " не найдено"));

        if (booking.getItem().getOwner().getId() != userId) {
            throw new NotValidException(Booking.class, "Только владелец может изменять бронирование");
        }
        booking.setStatus(approved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        Booking savedBooking = bookingRepository.save(booking);

        return bookingMapper.map(savedBooking);
    }

    @Override
    public BookingDto getBooking(int userId, int bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ItemNotFoundException("Бронирование  с ID " + bookingId + " не найдено"));

        if (booking.getItem().getOwner().getId() != userId && booking.getBooker().getId() != userId) {
            throw new NotValidException(Booking.class, "owner id " + booking.getItem().getOwner().getId() +
                    " or booker id " + booking.getBooker().getId() + " does not match with user id " + userId);
        }

        return bookingMapper.map(booking);
    }

    @Override
    public Collection<BookingDto> getAllUserBookings(int userId, BookingState state) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("Пользователь с ID " + userId + " не найден"));

        final Collection<Booking> bookings;
        final LocalDateTime current = LocalDateTime.now();
        switch (state) {
            case WAITING -> {
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING);
            }
            case REJECTED -> {
                bookings = bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED);
            }
            case CURRENT -> {
                bookings = bookingRepository.findAllCurrentBookings(userId, current);
            }
            case PAST -> {
                bookings = bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, current);
            }
            case FUTURE -> {
                bookings = bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, current);
            }
            case ALL -> {
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(userId);
            }
            default -> {
                throw new NotValidException(BookingState.class, "invalid");
            }
        }

        return bookingMapper.map(bookings);
    }

    @Override
    public Collection<BookingDto> getAllUserItemsBookings(int userId, BookingState state) {
        Collection<Integer> itemIds = itemRepository.findAllByOwnerId(userId).stream()
                .map(Item::getId)
                .toList();

        return getAllUserBookings(userId, state).stream()
                .filter(booking -> itemIds.contains(booking.getItem().getId()))
                .toList();
    }
}
