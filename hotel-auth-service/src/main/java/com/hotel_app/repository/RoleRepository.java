package com.hotel_app.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.hotel_app.model.Role;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role,Long> {
    public Optional<Role> findByRoleName(String role);
    public boolean existsByRoleName(Role role);
}