package com.cms.userManagement.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.cms.userManagement.model.Role;
import com.cms.userManagement.model.RoleName;
import com.cms.userManagement.repository.RoleRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RoleInitializer implements ApplicationRunner {

	private static final String NOT_DELETED = "N";

	private final RoleRepository roleRepository;

	@Override
	@Transactional
	public void run(ApplicationArguments args) {
		for (RoleName roleName : RoleName.values()) {
			if (roleRepository.existsByName(roleName)) {
				continue;
			}
			Role role = new Role();
			role.setName(roleName);
			role.setDeletedYn(NOT_DELETED);
			roleRepository.save(role);
		}
	}
}
