package com.hotel_app.service;

import com.hotel_app.client.BookedRoomServiceClient;
import com.hotel_app.dtos.BookedRoom;
import com.hotel_app.exception.InternalServerException;
import com.hotel_app.exception.ResourceNotFoundException;
import com.hotel_app.model.RoomEntity;
import com.hotel_app.repositories.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.sql.rowset.serial.SerialBlob;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService{

    @Autowired
    private  RoomRepository roomRepository;


    @Autowired
    private BookedRoomServiceClient client;
    @Override
    public RoomEntity addNewRoom(MultipartFile photo , String roomType, BigDecimal roomPrice) throws SQLException, IOException {
        RoomEntity room = new RoomEntity();
        room.setRoomType(roomType);
        room.setRoomPrice(roomPrice);
        if(!photo.isEmpty()){
            byte[] photoBytes = photo.getBytes();
            Blob photoBlob = new SerialBlob((photoBytes));
            room.setPhoto(photoBlob);
        }
        return roomRepository.save(room);

    }

    @Override
    public List<String> getAllRoomTypes() {
        System.out.println(roomRepository.findDistinctRoomTypes());
        return roomRepository.findDistinctRoomTypes();

    }

    @Override
    public List<RoomEntity> getAllRooms() {
        return roomRepository.findAll();
    }

    @Override
    public byte[] getRoomPhotoByRoomId(Long id) throws SQLException {
        Optional<RoomEntity> theRoom = roomRepository.findById(id);
        if(theRoom.isEmpty()){
            throw new ResourceNotFoundException("Sorry...! Room not Found");
        }
        Blob photoBlob = theRoom.get().getPhoto();
        if(photoBlob != null){
            return photoBlob.getBytes(1,(int)photoBlob.length());
        }
        return null;
    }

    @Override
    public void deleteRoom(Long roomId){
        RoomEntity theRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room with id "+ roomId + "Not Found"));
        roomRepository.delete(theRoom);
    }

    @Override
    public RoomEntity updateRoom(Long roomId, String roomType, BigDecimal roomPrice, byte[] photoBytes) throws InternalServerException {
        RoomEntity room = roomRepository.findById(roomId)
                .orElseThrow(()-> new ResourceNotFoundException("Room not found Exception"));
        if(roomType != null){
            room.setRoomType(roomType);
        }
        if(roomPrice != null){
            room.setRoomPrice(roomPrice);
        }
        if(photoBytes != null && photoBytes.length > 0){
            try {
                room.setPhoto(new SerialBlob(photoBytes));
            }
            catch(SQLException e){
                throw new InternalServerException("Error updating room");
            }
        }
        return roomRepository.save(room);
    }

    @Override
    public List<BookedRoom> getAllBookingsByRoomId(Long roomId){
        return client.getAllBookingsByRoomId(roomId);
    }

    @Override
    public Optional<RoomEntity> getRoomById(Long roomId){
        return Optional.of(roomRepository.findById(roomId).get());
    }

    @Override
    public List<RoomEntity> getAvialableRooms(LocalDate checkInDate, LocalDate checkOutDate, String roomType) {
        List<RoomEntity> rooms = roomRepository.findRoomsByRoomType(roomType);
        List<Long> roomIds = client.getBookedRoomBetweenDates(checkInDate,checkOutDate);
        List<RoomEntity> availableRooms = rooms.stream()
                .filter(roomEntity -> !roomIds.contains(roomEntity.getId()))
                .toList();
        return availableRooms;
    }

    @Override
    public boolean addBooking(Long roomId) {
        Optional<RoomEntity> room = getRoomById(roomId);
        if(room.isEmpty()){
            return false;
        }else if(room.get().isBooked()){
            return false;
        }
        else{
            RoomEntity newRoom = room.get();
            newRoom.setBooked(true);
            roomRepository.save(newRoom);
            return true;
        }

    }

    @Override
    public boolean updateIsBooked(Long roomId) {
        RoomEntity room =  roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room not find excpetion"));
        room.setBooked(false);
        roomRepository.save(room);
        return true;
    }

}