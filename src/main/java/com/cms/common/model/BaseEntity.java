package com.cms.common.model;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

	@Schema(description = "User who created.")
	@Column(name = "cret_id", updatable = false, length = 100)
	private String createdBy;

	@Column(name = "cret_ip", updatable = false, length = 50)
	private String createdIp;

	@Schema(description = "Date when created.")
	@Column(name = "cret_date", updatable = false)
	private LocalDateTime createdAt;

	@Schema(description = "User who last updated.")
	@Column(name = "modi_id", insertable = false, length = 100)
	private String modifiedBy;

	@Column(name = "modi_ip", insertable = false, length = 50)
	private String modifiedIp;

	@Schema(description = "Date when last updated.")
	@Column(name = "modi_date", insertable = false)
	private LocalDateTime modifiedAt;

	@Schema(description = "Soft-delete 'N' means visible, 'Y' means deleted.")
	@Column(name = "del_yn", nullable = false, length = 1)
	private String deletedYn = "N";

	@PrePersist
	protected void onCreate() {
		if (deletedYn == null || deletedYn.isBlank()) {
			deletedYn = "N";
		}
		if (createdAt == null) {
			createdAt = LocalDateTime.now();
		}
	}

	@PreUpdate
	protected void onUpdate() {
		modifiedAt = LocalDateTime.now();
	}
}
