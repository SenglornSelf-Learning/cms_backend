package com.cms.content.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cms.content.model.Content;

public interface ContentRepository extends JpaRepository<Content, Integer> {

	List<Content> findAllByOrderByIdAsc();

	Optional<Content> findByIdAndDeletedYn(Integer id, String deletedYn);
}
