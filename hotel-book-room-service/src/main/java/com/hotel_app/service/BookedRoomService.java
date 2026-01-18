package com.hotel_app.service;

import com.hotel_app.model.BookedRoomEntity;

import java.time.LocalDate;
import java.util.List;

public interface BookedRoomService {

    public List<BookedRoomEntity> getAllBookings();

    public BookedRoomEntity findByBookingConfirmationCode(String confirmationCode);

    public String saveBooking(Long roomId, BookedRoomEntity bookingRequest);

    public void cancelBooking(Long roomId);

    public List<BookedRoomEntity> getAllBookingsByRoomId(Long roomId);

    public List<BookedRoomEntity> findBookingByUserId(String userId);

    List<Integer> getBookedRoomByDates(LocalDate checkinDate, LocalDate checkoutDate);

    boolean checkIfRoomIsBooked(Long roomId);
}