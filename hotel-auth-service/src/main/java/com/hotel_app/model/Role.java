package com.hotel_app.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "role")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;

    @Column(name = "role_name",unique = true)
    private String roleName;

    @JsonIgnore
    @ManyToMany(mappedBy = "roles")
    private Collection<UserEntity> users = new HashSet<>();

    public Role( String roleName){
        this.roleName = roleName;
    }

    public void assignRoleToUser(UserEntity user){
        user.getRoles().add(this);
        this.getUsers().add(user);
    }

    public void removeRolefromUser(UserEntity user){
        user.getRoles().remove(this);
        this.getUsers().remove(user);
    }

    public void removeAllUsersFromRole(){
        if(this.getUsers() != null){
            List<UserEntity> roleUsers = this.getUsers().stream().toList();
            roleUsers.forEach(this :: removeRolefromUser);
        }
    }

    public String getRoleName(){
        return roleName != null ? roleName : "";
    }
}