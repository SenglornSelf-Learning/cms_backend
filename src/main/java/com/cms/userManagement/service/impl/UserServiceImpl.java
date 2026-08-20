package com.cms.userManagement.service.impl;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.cms.common.response.PageResponse;
import com.cms.common.web.HttpRequestUtils;
import com.cms.globleException.exception.UserException;
import com.cms.userManagement.dto.RoleDto.RoleRequest;
import com.cms.userManagement.dto.UserDto.UserRequest;
import com.cms.userManagement.dto.UserDto.UserResponse;
import com.cms.userManagement.mapper.UserMapper;
import com.cms.userManagement.model.Role;
import com.cms.userManagement.model.User;
import com.cms.userManagement.model.UserRole;
import com.cms.userManagement.repository.RoleRepository;
import com.cms.userManagement.repository.UserRepository;
import com.cms.userManagement.service.UserService;

import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

	private static final String NOT_DELETED = "N";

	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final UserMapper userMapper;

	@Override
	public PageResponse<UserResponse> findAll(
			Integer pageIndex,
			Integer pageSize,
			String orderBy,
			String username,
			String email,
			String phone,
			String role) {
		int setPageIndex = pageIndex == null || pageIndex < 1 ? 1 : pageIndex;
		int setPageSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
		Sort sort = buildSort(orderBy);

		PageRequest pageable = PageRequest.of(setPageIndex - 1, setPageSize, sort);
		Page<User> users = userRepository.findAll(buildUserFilter(username, email, phone, role), pageable);

		return new PageResponse<>(
				userMapper.toResponseList(users.getContent()),
				users.getTotalElements(),
				setPageIndex,
				setPageSize,
				users.getTotalPages());
	}

	@Override
	@Transactional
	public UserResponse create(UserRequest request, String clientIp, String clientName) {
		String username = requireText(request.getUsername(), "Username is required");
		String email = requireText(request.getEmail(), "Email is required");
		String password = requireText(request.getPassword(), "Password is required");
		String phone = requirePhone(request.getPhone());

		if (userRepository.existsByUsernameAndDeletedYn(username, NOT_DELETED)) {
			throw UserException.badRequest("Username already exists");
		}
		if (userRepository.existsByEmailAndDeletedYn(email, NOT_DELETED)) {
			throw UserException.badRequest("Email already exists");
		}

		User user = userMapper.toEntity(request);
		user.setUsername(username);
		user.setEmail(email);
		user.setPassword(password);
		user.setPhone(phone);
		user.setDeletedYn(NOT_DELETED);
		HttpRequestUtils.applyCreateAudit(user, clientIp, clientName);
		user.setRoles(toRoles(request.getRoles()));

		User saved = userRepository.save(user);
		return userMapper.toResponse(saved);
	}

	// resolve catalog roles from the request; empty when none are sent
	private Set<Role> toRoles(List<RoleRequest> roleRequests) {
		Set<String> uniqueRoles = new LinkedHashSet<>();
		if (roleRequests != null) {
			for (RoleRequest roleRequest : roleRequests) {
				String roleType = roleRequest == null ? null : roleRequest.getRoleType();
				if (!StringUtils.hasText(roleType)) {
					continue;
				}
				String roleName = roleType.trim().toUpperCase(Locale.ROOT);
				if (!uniqueRoles.add(roleName)) {
					throw UserException.badRequest("Duplicate role: " + roleName);
				}
			}
		}

		Set<Role> roles = new LinkedHashSet<>();
		for (String roleName : uniqueRoles) {
			Role role = roleRepository.findByRoleTypeNameAndDeletedYn(roleName, NOT_DELETED)
					.orElseThrow(() -> UserException.notFound("Role " + roleName + " was not found"));
			roles.add(role);
		}
		return roles;
	}

	// build user filter by username, email, phone, role
	private Specification<User> buildUserFilter(String username, String email, String phone, String role) {
		return (root, query, criteriaBuilder) -> {
			List<Predicate> predicates = new ArrayList<>();
			predicates.add(criteriaBuilder.equal(root.get("deletedYn"), NOT_DELETED));

			Class<?> resultType = query.getResultType();
			boolean isDataQuery = resultType == null || User.class.equals(resultType);
			if (isDataQuery) {
				Fetch<User, UserRole> userRoleFetch = root.fetch("userRoles", JoinType.LEFT);
				userRoleFetch.fetch("role", JoinType.LEFT);
				query.distinct(true);
			}

			if (StringUtils.hasText(username)) {
				predicates.add(criteriaBuilder.like(
						criteriaBuilder.lower(root.get("username")),
						"%" + username.trim().toLowerCase() + "%"));
			}
			if (StringUtils.hasText(email)) {
				predicates.add(criteriaBuilder.like(
						criteriaBuilder.lower(root.get("email")),
						"%" + email.trim().toLowerCase() + "%"));
			}
			if (StringUtils.hasText(phone)) {
				predicates.add(criteriaBuilder.like(
						criteriaBuilder.lower(root.get("phone")),
						"%" + phone.trim().toLowerCase() + "%"));
			}
			if (StringUtils.hasText(role)) {
				Subquery<Integer> roleSubquery = query.subquery(Integer.class);
				Root<User> userRoot = roleSubquery.from(User.class);
				Join<User, UserRole> userRoleJoin = userRoot.join("userRoles");
				Join<UserRole, Role> roleJoin = userRoleJoin.join("role");
				roleSubquery.select(userRoot.get("id"));
				roleSubquery.where(
						criteriaBuilder.equal(userRoot.get("id"), root.get("id")),
						criteriaBuilder.equal(roleJoin.get("roleTypeName"), role.trim()),
						criteriaBuilder.equal(roleJoin.get("deletedYn"), NOT_DELETED));
				predicates.add(criteriaBuilder.exists(roleSubquery));
			}

			return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
		};
	}

	// Update user by id
	@Override
	@Transactional
	public UserResponse update(Integer id, UserRequest request, String clientIp, String clientName) {
		User user = userRepository.findByIdAndDeletedYn(id, NOT_DELETED)
				.orElseThrow(() -> UserException.notFound("User " + id + " was not found"));

		String username = requireText(request.getUsername(), "Username is required");
		String email = requireText(request.getEmail(), "Email is required");
		String phone = requirePhone(request.getPhone());

		if (userRepository.existsByUsernameAndIdNot(username, id)) {
			throw UserException.badRequest("Username " + username + " already exists");
		}
		if (userRepository.existsByEmailAndIdNot(email, id)) {
			throw UserException.badRequest("Email " + email + " already exists");
		}

		user.setUsername(username);
		user.setEmail(request.getEmail());
		user.setPhone(request.getPhone());
		HttpRequestUtils.applyUpdateAudit(user, clientIp, clientName);
		user.setRoles(toRoles(request.getRoles()));
		User updated = userRepository.save(user);
		return userMapper.toResponse(updated);
	}

	// build sort by orderBy
	private Sort buildSort(String orderBy) {
		if (!StringUtils.hasText(orderBy)) {
			return Sort.by(Sort.Direction.DESC, "createdAt");
		}

		String[] parts = orderBy.split(",");
		String property = parts[0].trim();
		String direction = parts.length > 1 ? parts[1].trim() : "DESC";

		if (!StringUtils.hasText(property)) {
			property = "createdAt";
		}

		Sort.Direction sortDirection = "ASC".equalsIgnoreCase(direction)
				? Sort.Direction.ASC
				: Sort.Direction.DESC;

		return Sort.by(sortDirection, property);
	}

	// check if a string is not empty
	private String requireText(String value, String message) {
		if (!StringUtils.hasText(value)) {
			throw UserException.badRequest(message);
		}
		return value.trim();
	}

	private String requirePhone(String value) {
		String phone = requireText(value, "Phone is required");
		if (!phone.matches("\\d+")) {
			throw UserException.badRequest("Phone must be a number");
		}
		return phone;
	}
}
