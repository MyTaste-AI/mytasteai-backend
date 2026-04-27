package myaimentor_api.mentor.category.dto;

import myaimentor_api.mentor.domain.Category;

/**
 * 카테고리 응답.
 * - parentId == null 이면 대분류, 아니면 중분류
 */
public record CategoryResponse(Long id, String name, Long parentId) {
	public static CategoryResponse from(Category c) {
		return new CategoryResponse(c.getId(), c.getName(), c.getParentId());
	}
}
