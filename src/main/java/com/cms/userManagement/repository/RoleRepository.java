package com.cms.userManagement.repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import com.cms.userManagement.model.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

	List<Role> findByDeletedYn(String deletedYn);

	boolean existsByRoleTypeName(String roleTypeName);

	boolean existsByRoleTypeNameAndIdNot(String roleTypeName, Integer id);

	Optional<Role> findByIdAndDeletedYn(Integer id, String deletedYn);

	Optional<Role> findByRoleTypeNameAndDeletedYn(String roleTypeName, String deletedYn);

	List<Role> findByRoleTypeNameInAndDeletedYn(Collection<String> roleTypeNames, String deletedYn);

}
