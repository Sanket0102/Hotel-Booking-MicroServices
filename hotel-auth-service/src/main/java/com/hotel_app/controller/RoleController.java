package com.hotel_app.controller;

import com.hotel_app.exception.RoleAlreadyExistsException;
import com.hotel_app.model.Role;
import com.hotel_app.model.UserEntity;
import com.hotel_app.service.RoleService;
import com.hotel_app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.FOUND;


@RestController
@RequestMapping("/api/roles")
public class RoleController {
    @Autowired
    public RoleService roleService;

    @Autowired
    public UserService userService;

    @GetMapping("/all-roles")
    public ResponseEntity<List<Role>> getAllRoles(){
        return new ResponseEntity<>(roleService.getRoles(),FOUND);
    }

    @PostMapping("/create/role")
    public ResponseEntity<String> createRole(@RequestBody Role theRole){
        try {
            roleService.createRole(theRole);
            return ResponseEntity.ok("New Role created Successfully");
        }
        catch (RoleAlreadyExistsException e){
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{roleId}")
    public void deleteRole(@PathVariable("roleId") Long roleId){
        roleService.deleteRole(roleId);
    }

    @PostMapping("/remove/all-users")
    public Role removeAllUsersFromRole(@PathVariable("roleId") Long roleId){
        return roleService.removeAllUsersFromRole(roleId);
    }

    @PostMapping("/remove/user")
    public UserEntity removeUserFromRole(@RequestParam("userId") Long userId, @RequestParam("roleId") Long roleId){
        return roleService.removeUserFromRole(userId,roleId);
    }

    @PostMapping("/assign-role")
    public UserEntity assignRoleToUser(@RequestParam("userId") Long userId,@RequestParam("roleId") Long roleId){
        return roleService.assignRoleToUser(userId,roleId);
    }




}