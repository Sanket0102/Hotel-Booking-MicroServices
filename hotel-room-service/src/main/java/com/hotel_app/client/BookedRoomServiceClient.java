package com.hotel_app.client;

import com.hotel_app.dtos.BookedRoom;
import com.hotel_app.dtos.Room;

import java.time.LocalDate;
import java.util.*;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
public class BookedRoomServiceClient {

    public static final String BOOKED_ROOM_SERVICE_URL = "http://localhost:1001/bookings";

    RestTemplate rt = new RestTemplate();

    @CircuitBreaker(name = "roomService",fallbackMethod = "getAllBookingsFallbackMethod")
    public List<BookedRoom> getAllBookingsByRoomId(Long roomId){
        List<BookedRoom> bookedRoom = rt.exchange(
                BOOKED_ROOM_SERVICE_URL+"/all-bookings-by-room-id/"+roomId,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<BookedRoom>>(){}
        ).getBody();
        return bookedRoom;
    }

    @CircuitBreaker(name = "roomService",fallbackMethod = "getBookRoomsByDateFallbackMethod")
    public List<Long> getBookedRoomBetweenDates(LocalDate checkInDate,LocalDate checkoutDate){
        UriComponents components = UriComponentsBuilder.fromHttpUrl(BOOKED_ROOM_SERVICE_URL+"/booked-room-by-dates")
                .queryParam("checkinDate",checkInDate)
                .queryParam("checkoutDate",checkoutDate)
                .build();
        return rt.exchange(components.toString(), HttpMethod.GET, null, new ParameterizedTypeReference<List<Long>>() {}).getBody();
    }

    public List<BookedRoom> getAllBookingsFallbackMethod(Long roomId, Throwable ex){
        return List.of();
    }

    public List<Long> getBookRoomsByDateFallbackMethod(LocalDate checkInDate,LocalDate checkoutDate,Throwable ex){
        return List.of();
    }


}