package com.cms.userManagement.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cms.userManagement.model.Role;
import com.cms.userManagement.model.RoleName;

public interface RoleRepository extends JpaRepository<Role, Integer> {

	boolean existsByName(RoleName name);

	Optional<Role> findByNameAndDeletedYn(RoleName name, String deletedYn);

	List<Role> findByNameInAndDeletedYn(Collection<RoleName> names, String deletedYn);
}
