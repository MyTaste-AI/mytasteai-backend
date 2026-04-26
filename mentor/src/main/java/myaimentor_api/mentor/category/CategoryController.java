package myaimentor_api.mentor.category;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import myaimentor_api.mentor.category.dto.CategoryCreateRequest;
import myaimentor_api.mentor.category.dto.CategoryResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

/**
 * 카테고리 API.
 * - GET /categories         : 전체 목록 (인증 사용자 누구나)
 * - POST /categories        : 생성 (ADMIN)
 * - DELETE /categories/{id} : 삭제 (ADMIN)
 */
@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryController {

	private final CategoryService categoryService;

	/** 카테고리 목록 조회 — 봇 카테고리 필터 UI 등에 사용 */
	@GetMapping
	public List<CategoryResponse> findAll() {
		return categoryService.findAll();
	}

	/** 카테고리 생성 — 이름 중복 시 409 */
	@PostMapping
	public ResponseEntity<Void> create(@RequestBody @Valid CategoryCreateRequest request) {
		Long id = categoryService.create(request);
		URI location = UriComponentsBuilder.fromPath("/categories/{id}").buildAndExpand(id).toUri();
		return ResponseEntity.created(location).build();
	}

	/** 카테고리 삭제 — 존재하지 않으면 404 */
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		categoryService.delete(id);
	}
}
