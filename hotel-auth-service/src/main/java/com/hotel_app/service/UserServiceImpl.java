package com.hotel_app.service;


import com.hotel_app.exception.UserAlreadyExistsException;
import com.hotel_app.model.Role;
import com.hotel_app.model.UserEntity;
import com.hotel_app.repository.RoleRepository;
import com.hotel_app.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService{

    @Autowired
    public UserRepository userRepository;

    @Autowired
    public PasswordEncoder passwordEncoder;

    @Autowired
    public final RoleRepository roleRepository;



    @Transactional
    @Override
    public UserEntity registerUser(UserEntity user){
        Set<Role> assignedRoles = new HashSet<>();
        if(userRepository.existsByEmail(user.getEmail())){
            throw new UserAlreadyExistsException(user.getEmail() +"Already Exists");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        if(user.getRoles() == null || user.getRoles().isEmpty()){
            user.setRoles(Collections.singletonList(new Role("ROLE_USER")));
        }
        else{
            for(Role role : user.getRoles()){
                Role existingRole = roleRepository.findByRoleName(role.getRoleName()).orElseThrow(() -> new RuntimeException("User"));
                assignedRoles.add(existingRole);
            }

        }
        user.setRoles(assignedRoles);
        return userRepository.save(user);

    }

    @Override
    public List<UserEntity> getUsers() {
        return userRepository.findAll();
    }

    @Transactional
    @Override
    public void deleteUser(String email) {
        UserEntity theUser = getUserByEmail(email);
        if(theUser != null){
            userRepository.deleteByEmail(email);
        }

    }

    @Override
    public UserEntity getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(()-> new UsernameNotFoundException("User Not Found"));
    }

}