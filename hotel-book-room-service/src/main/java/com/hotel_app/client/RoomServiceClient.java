package com.hotel_app.client;

import com.hotel_app.dto.Room;
import com.hotel_app.exception.ResourceNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class RoomServiceClient {

    public static final String ROOM_SERVICE_URL = "http://localhost:2000/rooms";

    RestTemplate rt = new RestTemplate();

    public boolean addBooking(Long roomId){
        Boolean res =  rt.exchange(ROOM_SERVICE_URL + "/add-booking/" + roomId, HttpMethod.POST, null, Boolean.class)
                .getBody();
        return res;
    }

    @CircuitBreaker(name ="bookedRoomService", fallbackMethod = "getRoomFallback")
    public Room getRoomByRoomId(Long roomId){
        ResponseEntity<Room> roomentity =  rt.exchange(ROOM_SERVICE_URL + "/room/"+roomId,HttpMethod.GET,null,Room.class);
        if(roomentity.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR){
            throw new ResourceNotFoundException("Room Not Found");
        }
        return roomentity.getBody();
    }

    @CircuitBreaker(name = "bookedRoomService",fallbackMethod = "changeRoomFallback")
    public Boolean changeRoomStatus(Long roomId){
        Boolean isUpdate =  rt.exchange(ROOM_SERVICE_URL + "/set-is-booked/" + roomId,HttpMethod.POST,null,Boolean.class).getBody();
        return isUpdate;
    }

    public Room getRoomFallback(Long roomId,Throwable ex){
        return new Room();
    }

    public boolean changeRoomFallback(Long roomId,Throwable ex){
        return false;
    }



}