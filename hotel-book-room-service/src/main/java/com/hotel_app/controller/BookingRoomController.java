package com.hotel_app.controller;

import com.hotel_app.client.RoomServiceClient;
import com.hotel_app.dto.BookingResponse;
import com.hotel_app.dto.Room;
import com.hotel_app.dto.RoomResponse;
import com.hotel_app.exception.InvalidBookingRequestException;
import com.hotel_app.exception.ResourceNotFoundException;
import com.hotel_app.model.BookedRoomEntity;
import com.hotel_app.service.BookedRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;



@RestController
@RequestMapping("/bookings")
public class BookingRoomController {

    @Autowired
    private BookedRoomService bookedRoomService;

    @Autowired
    private RoomServiceClient roomClient;


    @GetMapping("/all-bookings")
    public ResponseEntity<List<BookingResponse>> getAllBookings(){
        List<BookedRoomEntity> bookings = bookedRoomService.getAllBookings();
        List <BookingResponse> bookingResponses = new ArrayList<>();
        for(BookedRoomEntity booking : bookings){
            BookingResponse bookingResponse = getBookingResponse(booking);
            bookingResponses.add(bookingResponse);
        }
        return ResponseEntity.ok(bookingResponses);
    }


    @GetMapping("/user/{userId}/bookings")
    public ResponseEntity<List<BookingResponse>> getBookingsByUserId(@PathVariable("userId") String userId){

        List<BookedRoomEntity> bookings = bookedRoomService.findBookingByUserId(userId);
        List<BookingResponse> bookingResponses = new ArrayList<>();
        for(BookedRoomEntity booking: bookings ){
            BookingResponse bookingResponse = getBookingResponse(booking);
            bookingResponses.add(bookingResponse);
        }
        return ResponseEntity.ok(bookingResponses);


    }

    @GetMapping("/confirmation/{confirmationCode}")
    public ResponseEntity<?> getBookingByConfirmationCode(@PathVariable("confirmationCode") String confirmationCode){
        try{
            BookedRoomEntity booking = bookedRoomService.findByBookingConfirmationCode(confirmationCode);
            BookingResponse bookingResponse = getBookingResponse(booking);
            return ResponseEntity.ok(bookingResponse);
        }
        catch(ResourceNotFoundException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    @GetMapping("/is-room-booked/roomId")
    public ResponseEntity<Boolean> checkIfRoomIsBooked(@PathVariable("roomId")Long roomId){
        return ResponseEntity.ok(bookedRoomService.checkIfRoomIsBooked(roomId));
    }

    @PostMapping("/room/{roomId}/booking")
    public ResponseEntity<?> saveBooking(@PathVariable("roomId") Long roomId,
                                         @RequestBody BookedRoomEntity bookingRequest){
        System.out.println("BookedRoom Controller");
        System.out.println("Num of Adults" + bookingRequest.getNumOfAdults());
        System.out.println("Num of Children" + bookingRequest.getNumOfChildren());
        System.out.println("Total "+ bookingRequest.getTotalNumOfGuest());
        try{
            String confirmationCode = bookedRoomService.saveBooking(roomId, bookingRequest);
            return ResponseEntity.ok("Room Booked SuccessFully..! Your booking confirmation code is :" + confirmationCode);

        }
        catch(InvalidBookingRequestException e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all-bookings-by-room-id/{roomId}")
    public ResponseEntity<List<BookedRoomEntity>> getAllBookingsByRoomId(@PathVariable("roomId") Long roomId){
        List<BookedRoomEntity> allbookedRooms = bookedRoomService.getAllBookingsByRoomId(roomId);
        return new ResponseEntity<>(allbookedRooms,HttpStatus.OK);
    }

    @GetMapping("/booked-room-by-dates")
    public ResponseEntity<List<Integer>> getBookedRoomByDates(LocalDate checkinDate,LocalDate checkoutDate){
        List<Integer> roomsId =  bookedRoomService.getBookedRoomByDates(checkinDate,checkoutDate);
        return ResponseEntity.ok(roomsId);
    }

    @DeleteMapping("/booking/{bookingId}/delete")
    public ResponseEntity<String> cancelBooking(@PathVariable("bookingId") Long bookingId){
        bookedRoomService.cancelBooking(bookingId);
        return ResponseEntity.ok("Room with id" + bookingId + "Deleted Successfully");
    }

    private BookingResponse getBookingResponse(BookedRoomEntity booking){
        Room theRoom = roomClient.getRoomByRoomId(booking.getRoomId());
        RoomResponse room = new RoomResponse(theRoom.getId(), theRoom.getRoomType(), theRoom.getRoomPrice() , theRoom.isBooked());
        return new BookingResponse(booking.getBookingId(), booking.getCheckInDate(),
                booking.getCheckOutDate(),booking.getGuestFullName(), booking.getGuestEmail(),booking.getNumOfAdults(),
                booking.getNumOfChildren(), booking.getTotalNumOfGuest(), booking.getBookingConfirmationCode(), room);
    }

}