package com.cms.userManagement.model;

import java.util.HashSet;
import java.util.Set;

import com.cms.common.model.BaseEntity;

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
@Table(name = "tb_roles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Role extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "role_type", nullable = false, unique = true, columnDefinition = "text", length = 30)
    private String roleTypeName;

    @OneToMany(mappedBy = "role", fetch = FetchType.LAZY)
    private Set<UserRole> userRoles = new HashSet<>();
}
