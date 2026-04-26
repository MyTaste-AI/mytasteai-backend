package myaimentor_api.mentor.category;

import lombok.RequiredArgsConstructor;
import myaimentor_api.common.error.BusinessException;
import myaimentor_api.common.error.ErrorCode;
import myaimentor_api.mentor.category.dto.CategoryCreateRequest;
import myaimentor_api.mentor.category.dto.CategoryResponse;
import myaimentor_api.mentor.domain.Category;
import myaimentor_api.mentor.domain.CategoryRepository;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 카테고리 서비스 구현 — 봇 분류용 카테고리의 단순 CRUD.
 * AI 서비스와 동기화 없음 (카테고리는 Spring DB 단독 관리).
 *
 * - findAll: id ASC 고정 정렬 (UI 일관성)
 * - create:  이름 중복 시 409 (CAT-002)
 * - delete:  미존재 시 404 (CAT-001) — 멱등 정책 아님
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;

	@Override
	@Transactional(readOnly = true)
	public List<CategoryResponse> findAll() {
		return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
				.stream()
				.map(CategoryResponse::from)
				.toList();
	}

	@Override
	@Transactional
	public Long create(CategoryCreateRequest request) {
		if (categoryRepository.existsByName(request.name())) {
			throw new BusinessException(ErrorCode.CATEGORY_ALREADY_EXISTS);
		}
		Category saved = categoryRepository.save(Category.builder().name(request.name()).build());
		return saved.getId();
	}

	@Override
	@Transactional
	public void delete(Long id) {
		if (!categoryRepository.existsById(id)) {
			throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
		}
		categoryRepository.deleteById(id);
	}
}
