package com.cms.userManagement.model;


import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import com.cms.common.model.BaseEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_users")
public class User extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, columnDefinition = "text", length = 50)
    private String username;
    
    @Column(nullable = false, columnDefinition = "text", length = 100)
    private String password;
    
    @Column(nullable = false, columnDefinition = "text", length = 100)
    private String email;

    @Column(nullable = true, columnDefinition = "text", length = 15)
    private String phone;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UserRole> userRoles = new HashSet<>();

    public Set<Role> getRoles() {
        Set<Role> roles = new LinkedHashSet<>();
        for (UserRole userRole : userRoles) {
            if (userRole.getRole() != null) {
                roles.add(userRole.getRole());
            }
        }
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        if (userRoles == null) {
            userRoles = new HashSet<>();
        }
        userRoles.clear();
        if (roles == null) {
            return;
        }
        for (Role role : roles) {
            UserRole userRole = new UserRole();
            userRole.setUser(this);
            userRole.setRole(role);
            userRoles.add(userRole);
        }
    }
}
