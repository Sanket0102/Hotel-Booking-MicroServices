package com.hotel_app.controller;


import com.hotel_app.dtos.BookedRoom;
import com.hotel_app.exception.PhotoRetrieverException;
import com.hotel_app.exception.ResourceNotFoundException;
import com.hotel_app.model.RoomEntity;
import com.hotel_app.response.RoomResponse;
import com.hotel_app.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.binary.Base64;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.awt.print.Book;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@RestController
@RequiredArgsConstructor
@RequestMapping("/rooms")
public class RoomController {
    private final RoomService roomService;



    @PostMapping("/add/new-room")
    public ResponseEntity<RoomResponse> addNewRoom(@RequestParam("photo") MultipartFile photo, @RequestParam("roomType") String roomType, @RequestParam("roomPrice") BigDecimal roomPrice) throws SQLException, IOException {
        RoomEntity savedRoom = roomService.addNewRoom(photo ,roomType,roomPrice);
        RoomResponse response = new RoomResponse(savedRoom.getId(), savedRoom.getRoomType(),savedRoom.getRoomPrice());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/room/room-types")
    public List<String> getRoomTypes(){
        return roomService.getAllRoomTypes();
    }

    @GetMapping("/all-rooms")
    public ResponseEntity<List<RoomResponse>> getAllRooms() throws SQLException{
        List<RoomEntity> rooms = roomService.getAllRooms();
        List<RoomResponse> roomResponses = new ArrayList<>();
        for(RoomEntity room: rooms){
            byte[] photoByte = roomService.getRoomPhotoByRoomId(room.getId());
            String base64 = Base64.encodeBase64String(photoByte);
            RoomResponse roomResponse = getRoomResponse(room);
            roomResponse.setPhoto(base64);
            roomResponses.add(roomResponse);
        }
        return ResponseEntity.ok(roomResponses);
    }


    @DeleteMapping("/delete/room/{roomId}")
    public ResponseEntity<String> deleteRoom(@PathVariable("roomId") Long roomId){
        roomService.deleteRoom(roomId);
        return new ResponseEntity<>("Room Deleted Successfully",HttpStatus.NO_CONTENT);
    }


    @PutMapping("/update/{roomId}")
    public ResponseEntity<RoomResponse> updateRoom(@PathVariable("roomId") Long roomId,
                                                   @RequestParam(required = false) String roomType,
                                                   @RequestParam(required = false) BigDecimal roomPrice ,
                                                   @RequestParam(required = false)MultipartFile photo) throws SQLException,IOException{
        //Client -> Server
        byte[] photoBytes = photo != null && !photo.isEmpty() ? photo.getBytes() : roomService.getRoomPhotoByRoomId(roomId);
        Blob photoBlob = photoBytes != null && photoBytes.length > 0 ? new SerialBlob(photoBytes) : null;
        RoomEntity theRoom = roomService.updateRoom(roomId, roomType,roomPrice, photoBytes);
        theRoom.setPhoto(photoBlob);

        //Server -> Client
        RoomResponse roomResponse = getRoomResponse(theRoom);
        return ResponseEntity.ok(roomResponse);
    }

    @GetMapping("/room/{roomId}")
    public ResponseEntity<Optional<RoomResponse>> getRoomById(@PathVariable("roomId") Long roomId){
        Optional<RoomEntity> theRoom = roomService.getRoomById(roomId);
        return theRoom.map(room ->  {
            RoomResponse roomResponse = getRoomResponse(room);
            return ResponseEntity.ok(Optional.of(roomResponse));
        }).orElseThrow(() -> new ResourceNotFoundException("Room not Found"));
    }

    @GetMapping("/avialable-rooms")
    public ResponseEntity<List<RoomResponse>> getAvialableRooms(@RequestParam("checkInDate")
                                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkInDate,
                                                                @RequestParam("checkOutDate")
                                                                @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate checkOutDate,
                                                                @RequestParam("roomType") String roomType) throws SQLException {
        List <RoomEntity> avialableRooms = roomService.getAvialableRooms(checkInDate,checkOutDate,roomType);
        List <RoomResponse> avialableRoomResponses = new ArrayList<>();
        for(RoomEntity room : avialableRooms){
            byte [] photoBytes = roomService.getRoomPhotoByRoomId(room.getId());
            if(photoBytes != null && photoBytes.length > 0){
                String photoBase64 = Base64.encodeBase64String(photoBytes);
                RoomResponse roomResponse = getRoomResponse(room);
                roomResponse.setPhoto(photoBase64);
                avialableRoomResponses.add(roomResponse);
            }
        }
        if(avialableRoomResponses.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        else{
            return ResponseEntity.ok(avialableRoomResponses);
        }
    }

    @PostMapping("/add-booking/{roomId}")
    public ResponseEntity<Boolean> addBooking(@PathVariable("roomId") Long roomId){
        boolean isSuccessfull = roomService.addBooking(roomId);
        return new ResponseEntity<>(isSuccessfull,HttpStatus.OK);
    }

    @PostMapping("/set-is-booked/{roomId}")
    public ResponseEntity<Boolean> updateIsBooked(@PathVariable("roomId") Long roomId){
        boolean isUpdated = roomService.updateIsBooked(roomId);
        return new ResponseEntity<>(isUpdated,HttpStatus.OK);
    }

    private RoomResponse getRoomResponse(RoomEntity room){
        List<BookedRoom> bookings = getAllBookingsByRoomId(room.getId());
        /*List<BookingResponse> bookingInfo = bookings
                .stream().
                map(booking -> new BookingResponse(booking.getBookingId(),
                                                   booking.getCheckInDate(),
                                                   booking.getCheckOutDate(),
                                                   booking.getBookingConformationCode())).toList();*/
        byte[] photoBytes = null;
        Blob photoBlob = room.getPhoto();
        if(photoBlob != null){
            try{
                photoBytes = photoBlob.getBytes(1,(int)photoBlob.length());
            }
            catch(SQLException e){
                throw new PhotoRetrieverException("Error Retrieving photo");
            }
        }
        return new RoomResponse(room.getId(), room.getRoomType(), room.getRoomPrice(),room.isBooked(),photoBytes);
    }



    private List<BookedRoom> getAllBookingsByRoomId(Long id){
        return roomService.getAllBookingsByRoomId(id);


    }
}