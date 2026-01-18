package com.hotel_app.service;


import com.hotel_app.client.RoomServiceClient;
import com.hotel_app.dto.Room;
import com.hotel_app.exception.InvalidBookingRequestException;
import com.hotel_app.exception.ResourceNotFoundException;
import com.hotel_app.model.BookedRoomEntity;
import com.hotel_app.repository.BookedRoomRepository;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BookedRoomServiceImpl implements BookedRoomService {

    @Autowired
    private BookedRoomRepository bookedRoomRepository;

    @Autowired
    private RoomServiceClient client;


    @Override
    public List<BookedRoomEntity> getAllBookings() {

        return bookedRoomRepository.findAll();
    }

    @Override
    public List<BookedRoomEntity> getAllBookingsByRoomId(Long roomId){
        return bookedRoomRepository.findByRoomId(roomId);
    }

    @Override
    public BookedRoomEntity findByBookingConfirmationCode(String confirmationCode){
        return bookedRoomRepository.findByBookingConfirmationCode(confirmationCode)
                .orElseThrow(() -> new ResourceNotFoundException("No Booking Found with Confirmation Code :" + confirmationCode));
    }

    @Override
    public List<BookedRoomEntity> findBookingByUserId(String userId){
        return bookedRoomRepository.findByGuestEmail(userId);

    }

    @Override
    public List<Integer> getBookedRoomByDates(LocalDate checkinDate, LocalDate checkoutDate) {
        return bookedRoomRepository.findBookedRoomByDates(checkinDate,checkoutDate);
    }

    @Override
    public boolean checkIfRoomIsBooked(Long roomId) {
        List<BookedRoomEntity> bookedRoomEntity = bookedRoomRepository.findBookedRoomByRoomId(roomId);
        return !bookedRoomEntity.isEmpty();
    }

    @Override
    public String saveBooking(Long roomId, BookedRoomEntity bookingRequest){
        System.out.println("save Booking()");
        System.out.println("Num of Adults" + bookingRequest.getNumOfAdults());
        System.out.println("Num of Children" + bookingRequest.getNumOfChildren());
        System.out.println("Total "+ bookingRequest.getTotalNumOfGuest());
        if(bookingRequest.getCheckOutDate().isBefore(bookingRequest.getCheckInDate())){
            throw new InvalidBookingRequestException("Check in Date must be come before check out");
        }
        //Room room = client.getRoomById(roomId);
        //List<BookedRoomEntity> existingBookings = room.getBookings();
        List<BookedRoomEntity> existingBookings = bookedRoomRepository.findBookedRoomByRoomId(roomId);
        boolean roomIsAvialable = roomIsAvialable(bookingRequest, existingBookings);
        if(roomIsAvialable){
            addBooking(bookingRequest,roomId);
        }
        else{
            throw new InvalidBookingRequestException("Sorry..! This room is not avialable for selected days");
        }
        System.out.println(bookingRequest.getBookingConfirmationCode());
        return bookingRequest.getBookingConfirmationCode();
    }

    @Override
    public void cancelBooking(Long bookingId){
        BookedRoomEntity bookedRoomEntity = bookedRoomRepository.findByBookingId(bookingId).get();
        Long roomId = bookedRoomEntity.getRoomId();
        if(client.changeRoomStatus(roomId)) {
            bookedRoomRepository.deleteById(bookingId);
        }
        else{
            throw new ResourceNotFoundException("Room not found");
        }
    }


    private boolean roomIsAvialable(BookedRoomEntity bookingRequest, List<BookedRoomEntity> existingBookings){
        return existingBookings.stream()
                .noneMatch(existingBooking ->
                        bookingRequest.getCheckInDate().isBefore(existingBooking.getCheckOutDate()) &&
                                bookingRequest.getCheckOutDate().isAfter(existingBooking.getCheckInDate())

                );
    }

    private void addBooking(BookedRoomEntity bookingRequest,Long roomId){
        BookedRoomEntity newBookedRoom = bookingRequest;
        newBookedRoom.setRoomId(roomId);
        Boolean res = client.addBooking(roomId);
        String confirmationCode = RandomStringUtils.randomNumeric(10);
        newBookedRoom.setBookingConfirmationCode(confirmationCode);
        bookedRoomRepository.save(newBookedRoom);
    }
}