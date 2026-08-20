package com.cms.userManagement.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.cms.userManagement.model.User;

public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {

	boolean existsByUsernameAndDeletedYn(String username, String deletedYn);

	boolean existsByEmailAndDeletedYn(String email, String deletedYn);

	Optional<User> findByIdAndDeletedYn(Integer id, String deletedYn);
}
