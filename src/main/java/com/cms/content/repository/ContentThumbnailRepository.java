package com.cms.content.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cms.content.model.ContentThumbnail;

public interface ContentThumbnailRepository extends JpaRepository<ContentThumbnail, Integer> {

	List<ContentThumbnail> findByContentIdAndDeletedYnOrderByIdAsc(Integer contentId, String deletedYn);

	List<ContentThumbnail> findByContentIdInAndDeletedYnOrderByContentIdAscIdAsc(
			List<Integer> contentIds,
			String deletedYn);

	List<ContentThumbnail> findByContentIdAndIdInAndDeletedYn(
			Integer contentId,
			List<Integer> fileIds,
			String deletedYn);

	Optional<ContentThumbnail> findByIdAndContentIdAndDeletedYn(Integer fileId, Integer contentId, String deletedYn);
}
