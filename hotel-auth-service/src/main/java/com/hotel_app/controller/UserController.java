package com.hotel_app.controller;


import com.hotel_app.exception.UserAlreadyExistsException;
import com.hotel_app.model.UserEntity;
import com.hotel_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    @Autowired
    public UserService userService;

    @PostMapping("/register-user")
    public ResponseEntity<?> registeredUser(@RequestBody UserEntity user){
        try{
            userService.registerUser(user);
            return ResponseEntity.ok("User Successfully Registered");
        }
        catch(UserAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }


    @GetMapping("3000")
    public ResponseEntity<List<UserEntity>> getUsers(){
        return new ResponseEntity<>(userService.getUsers(), HttpStatus.FOUND);
    }

    //@PreAuthorize("hasRole('ROLE_USER') or hasRole('ROLE_ADMIN')")
    @GetMapping("/get-user-by-email/{email}")
    public ResponseEntity<?> getUserByEmail(@PathVariable("email") String email){
        try{
            UserEntity theUser = userService.getUserByEmail(email);
            return ResponseEntity.ok(theUser);
        }
//        catch(UsernameNotFoundException e){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Fetching User");
        }
    }

    //@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_USER') and #email == principal.username")
    @DeleteMapping("/delete/{email}")
    public ResponseEntity<String> deleteUser(@PathVariable("email") String email){
        try{
            userService.deleteUser(email);
            return ResponseEntity.ok("User Deleted Successfully");
        }
//        catch(UsernameNotFoundException e){
//            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
//        }
        catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error Deleting User");
        }
    }


}