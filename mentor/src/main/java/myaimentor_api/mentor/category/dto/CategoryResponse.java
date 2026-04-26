package myaimentor_api.mentor.category.dto;

import myaimentor_api.mentor.domain.Category;

public record CategoryResponse(Long id, String name) {
	public static CategoryResponse from(Category c) {
		return new CategoryResponse(c.getId(), c.getName());
	}
}
