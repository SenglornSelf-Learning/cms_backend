package com.cms.userManagement.service.impl;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.cms.common.web.HttpRequestUtils;
import com.cms.globleException.exception.UserException;
import com.cms.userManagement.dto.RoleDto.RoleRequest;
import com.cms.userManagement.dto.RoleDto.RoleResponse;
import com.cms.userManagement.mapper.RoleMapper;
import com.cms.userManagement.model.Role;
import com.cms.userManagement.repository.RoleRepository;
import com.cms.userManagement.service.RoleService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

	private static final String NOT_DELETED = "N";
	private static final String DELETED = "Y";

	private final RoleRepository roleRepository;
	private final RoleMapper roleMapper;

	@Override
	public List<RoleResponse> findAll() {
		return roleMapper.toResponseList(roleRepository.findByDeletedYn(NOT_DELETED));
	}

	// Create role
	@Override
	@Transactional
	public RoleResponse create(RoleRequest request, String clientIp, String clientName) {
		String roleType = requireText(request == null ? null : request.getRoleType(), "Role type is required")
				.toUpperCase(Locale.ROOT);
		if (roleRepository.existsByRoleTypeName(roleType)) {
			throw UserException.badRequest("Role already exists");
		}

		Role role = roleMapper.toEntity(request);
		role.setRoleTypeName(roleType);
		role.setDeletedYn(NOT_DELETED);
		HttpRequestUtils.applyCreateAudit(role, clientIp, clientName);

		Role saved = roleRepository.save(role);
		return roleMapper.toResponse(saved);
	}

	// Update role by id
	@Override
	@Transactional
	public RoleResponse update(Integer id, RoleRequest request, String clientIp, String clientName) {
		Role role = roleRepository.findByIdAndDeletedYn(id, NOT_DELETED)
				.orElseThrow(() -> UserException.notFound("Role " + id + " was not found"));

		String roleType = requireText(request == null ? null : request.getRoleType(), "Role type is required")
				.toUpperCase(Locale.ROOT);

		if (roleRepository.existsByRoleTypeNameAndIdNot(roleType, id)) {
			throw UserException.badRequest("Role " + roleType + " already exists");
		}
		role.setRoleTypeName(roleType);
		HttpRequestUtils.applyUpdateAudit(role, clientIp, clientName);

		Role updated = roleRepository.save(role);
		return roleMapper.toResponse(updated);
	}

	// Delete role by id
	@Override
	@Transactional
	public void delete(Integer id, String clientIp, String clientName) {
		Role role = roleRepository.findByIdAndDeletedYn(id, NOT_DELETED)
				.orElseThrow(() -> UserException.notFound("Role " + id + " was not found"));
		role.setDeletedYn(DELETED);

		HttpRequestUtils.applyUpdateAudit(role, clientIp, clientName);
		roleRepository.save(role);
	}

	private String requireText(String value, String message) {
		if (!StringUtils.hasText(value)) {
			throw UserException.badRequest(message);
		}
		return value.trim();
	}
}
