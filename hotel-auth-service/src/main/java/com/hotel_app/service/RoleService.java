package com.hotel_app.service;



import com.hotel_app.model.Role;
import com.hotel_app.model.UserEntity;

import java.util.List;

public interface RoleService {
    public List<Role> getRoles();
    public Role createRole(Role theRole);
    public Role findByRoleName(String roleName);
    public UserEntity removeUserFromRole(Long userId, Long roleId);
    public UserEntity assignRoleToUser(Long userId, Long roleId);
    public Role removeAllUsersFromRole(Long roleId);
    public void deleteRole(Long roleId);


}