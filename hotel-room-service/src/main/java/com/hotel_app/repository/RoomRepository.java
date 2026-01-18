package com.hotel_app.repositories;


import com.hotel_app.model.RoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity,Long> {
    @Query("SELECT DISTINCT r.roomType FROM RoomEntity r")
    List<String> findDistinctRoomTypes();

    @Query("SELECT r FROM RoomEntity r "+
            " WHERE r.roomType LIKE %:roomType%")
    List <RoomEntity> findRoomsByRoomType(String roomType);


}