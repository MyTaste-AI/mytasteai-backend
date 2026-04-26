package myaimentor_api.mentor.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

/**
 * 카테고리 생성 요청.
 * - parentId == null : 대분류 생성
 * - parentId != null : 중분류 생성 (부모는 반드시 대분류)
 */
public record CategoryCreateRequest(
		@NotBlank @Size(max = 100) String name,
		@Positive Long parentId
) {
}
