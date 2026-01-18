package com.hotel_app.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FallbackController {

    @GetMapping("/room-fallback")
    public String roomFallback(){
        return "Room Service is currently unavailable. Please try again";
    }

    @GetMapping("/booked-room-fallback")
    public String bookedRoomFallback(){
        return "Room Service is currently unavailable. Please try again";
    }
}