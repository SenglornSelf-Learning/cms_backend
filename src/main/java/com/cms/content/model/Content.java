package com.cms.content.model;

import com.cms.category.model.Category;
import com.cms.common.model.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_contents")
@Getter
@Setter
public class Content extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(unique = true)
	private String uuid;

	@Column(unique = true, columnDefinition = "text")
	private String slug;

	private String keyword;

	@Column(columnDefinition = "text", nullable = false, length = 50)
	private String title;

	@Column(columnDefinition = "text", length = 255)
	private String description;

	@Column(columnDefinition = "text", length = 255)
	private String editor;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "category_id", nullable = false)
	private Category category;
}
