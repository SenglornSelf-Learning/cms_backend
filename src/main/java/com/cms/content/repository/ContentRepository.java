package com.cms.content.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.cms.content.model.Content;

public interface ContentRepository extends JpaRepository<Content, Integer>, JpaSpecificationExecutor<Content> {

	List<Content> findAllByOrderByIdAsc();

	Optional<Content> findByIdAndDeletedYn(Integer id, String deletedYn);
}
