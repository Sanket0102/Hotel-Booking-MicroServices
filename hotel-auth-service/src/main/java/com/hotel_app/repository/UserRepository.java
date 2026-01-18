package com.hotel_app.repository;

import com.hotel_app.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
    public boolean existsByEmail(String email);

    public void deleteByEmail(String email);

    public Optional<UserEntity> findByEmail(String email);
}