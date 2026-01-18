package com.hotel_app.repository;


import com.hotel_app.model.BookedRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface BookedRoomRepository extends JpaRepository<BookedRoomEntity , Long>{
    List<BookedRoomEntity> findByRoomId(Long roomId);

    Optional<BookedRoomEntity> findByBookingId(Long bookingId);

    Optional<BookedRoomEntity> findByBookingConfirmationCode(String confirmationCode);

    List<BookedRoomEntity> findByGuestEmail(String email);

    @Query("select br from BookedRoomEntity br where br.roomId =:roomId")
    List<BookedRoomEntity> findBookedRoomByRoomId(Long roomId);

    @Query("select br.roomId from BookedRoomEntity br where(( br.checkInDate <= :checkoutDate) and (br.checkOutDate >= :checkinDate))")
    List<Integer> findBookedRoomByDates(LocalDate checkinDate,LocalDate checkoutDate);


}