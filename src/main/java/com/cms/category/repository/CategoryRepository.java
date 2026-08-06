package com.cms.category.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.cms.category.model.Category;

public interface CategoryRepository extends JpaRepository<Category, Integer>, JpaSpecificationExecutor<Category> {

	List<Category> findAllByOrderByIdAsc();

	Page<Category> findByDeletedYn(String deletedYn, Pageable pageable);

	Optional<Category> findByIdAndDeletedYn(Integer id, String deletedYn);
}
