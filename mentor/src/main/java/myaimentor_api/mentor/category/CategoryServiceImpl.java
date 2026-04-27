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
 * 카테고리 서비스 구현 — 2단계 트리(대분류/중분류) CRUD.
 *
 * 규칙:
 *  - parentId == null  → 대분류 생성. 같은 레벨에서 이름 중복 금지.
 *  - parentId != null  → 중분류 생성. 부모는 반드시 대분류여야 함 (CAT-004).
 *  - 같은 부모 아래 이름 중복 금지 (다른 부모 아래에선 같은 이름 허용 — 예: "IT/백엔드", "디자인/백엔드 그래픽")
 *  - 자식이 있는 카테고리는 삭제 불가 (CAT-005)
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

	private final CategoryRepository categoryRepository;

	@Override
	@Transactional(readOnly = true)
	public List<CategoryResponse> findAll() {
		// 평탄화 응답 — UI 가 parentId 기준으로 그룹핑한다.
		return categoryRepository.findAll(Sort.by(Sort.Direction.ASC, "id"))
				.stream()
				.map(CategoryResponse::from)
				.toList();
	}

	@Override
	@Transactional
	public Long create(CategoryCreateRequest request) {
		Long parentId = request.parentId();

		if (parentId != null) {
			// 중분류 생성 — 부모 검증
			Category parent = categoryRepository.findById(parentId)
					.orElseThrow(() -> new BusinessException(ErrorCode.CATEGORY_INVALID));
			if (parent.getParentId() != null) {
				// 부모가 이미 중분류 — 깊이 초과
				throw new BusinessException(ErrorCode.CATEGORY_MAX_DEPTH);
			}
			if (categoryRepository.existsByNameAndParentId(request.name(), parentId)) {
				throw new BusinessException(ErrorCode.CATEGORY_ALREADY_EXISTS);
			}
		} else {
			// 대분류 생성 — 같은 레벨 이름 중복 검사
			if (categoryRepository.existsByNameAndParentIdIsNull(request.name())) {
				throw new BusinessException(ErrorCode.CATEGORY_ALREADY_EXISTS);
			}
		}

		Category saved = categoryRepository.save(
				Category.builder().name(request.name()).parentId(parentId).build()
		);
		return saved.getId();
	}

	@Override
	@Transactional
	public void delete(Long id) {
		if (!categoryRepository.existsById(id)) {
			throw new BusinessException(ErrorCode.CATEGORY_NOT_FOUND);
		}
		// 자식이 있으면 삭제 거부 — UI 에서 안내하고 사용자가 자식부터 정리
		if (categoryRepository.existsByParentId(id)) {
			throw new BusinessException(ErrorCode.CATEGORY_HAS_CHILDREN);
		}
		categoryRepository.deleteById(id);
	}
}
