package com.hotel_app.dtos;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.math.BigDecimal;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Getter
@Setter
@AllArgsConstructor
public class Room {

    private Long id;
    private String roomType;
    private BigDecimal roomPrice;
    private boolean isBooked = false;
    private Blob photo;


    private List<Integer> bookings;

    public Room(){
        this.bookings = new ArrayList<>();
    }

//    public void addBooking(BookedRoom booking){
//        if(bookings == null){
//            bookings = new ArrayList<>();
//        }
//
//        bookings.add(booking);
//        booking.setRoom(this);
//        isBooked = true;
//        String bookingCode = RandomStringUtils.randomNumeric(10);
//        booking.setBookingConfirmationCode(bookingCode);
//    }
}