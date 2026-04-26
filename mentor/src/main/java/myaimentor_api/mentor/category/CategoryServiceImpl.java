package myaimentor_api.mentor.category;

import lombok.RequiredArgsConstructor;
import myaimentor_api.mentor.category.dto.CategoryCreateRequest;
import myaimentor_api.mentor.category.dto.CategoryResponse;
import myaimentor_api.mentor.domain.Category;
import myaimentor_api.mentor.domain.CategoryRepository;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

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
			throw new ResponseStatusException(HttpStatus.CONFLICT, "이미 존재하는 카테고리입니다.");
		}
		Category saved = categoryRepository.save(Category.builder().name(request.name()).build());
		return saved.getId();
	}

	@Override
	@Transactional
	public void delete(Long id) {
		if (!categoryRepository.existsById(id)) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "카테고리를 찾을 수 없습니다.");
		}
		categoryRepository.deleteById(id);
	}
}
