package com.hotel_app.service;


import com.hotel_app.model.UserEntity;

import java.util.List;

public interface UserService {
    UserEntity registerUser(UserEntity user);
    List<UserEntity> getUsers();
    void deleteUser(String email);
    UserEntity getUserByEmail(String email);


}