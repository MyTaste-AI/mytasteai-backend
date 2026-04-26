package myaimentor_api.mentor.category;

import myaimentor_api.mentor.category.dto.CategoryCreateRequest;
import myaimentor_api.mentor.category.dto.CategoryResponse;

import java.util.List;

/**
 * 카테고리 서비스 — 봇 분류용 카테고리의 CRUD.
 */
public interface CategoryService {

	List<CategoryResponse> findAll();

	Long create(CategoryCreateRequest request);

	void delete(Long id);
}
