package com.hotel_app.service;


import com.hotel_app.dtos.BookedRoom;
import com.hotel_app.exception.InternalServerException;
import com.hotel_app.model.RoomEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface RoomService {
    public RoomEntity addNewRoom(MultipartFile photo , String roomType, BigDecimal roomPrice) throws SQLException, IOException;
    public List<String> getAllRoomTypes();

    public List<RoomEntity> getAllRooms();

    public byte[] getRoomPhotoByRoomId(Long id) throws SQLException;

    public void deleteRoom(Long roomId);

    public RoomEntity updateRoom(Long roomId, String roomType, BigDecimal roomPrice, byte[] photoBytes) throws InternalServerException;

    public Optional<RoomEntity> getRoomById(Long roomId);

    public List<RoomEntity> getAvialableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType);

    public List<BookedRoom> getAllBookingsByRoomId(Long roomId);

    boolean addBooking(Long roomId);

    boolean updateIsBooked(Long roomId);
}