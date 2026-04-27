package myaimentor_api.mentor.domain;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

	/** 같은 부모 아래에서의 이름 중복 검사 (parentId 가 null 이면 대분류 간 중복) */
	boolean existsByNameAndParentId(String name, Long parentId);

	boolean existsByNameAndParentIdIsNull(String name);

	/** 자식이 하나라도 있는지 — 삭제 가능 여부 판단용 */
	boolean existsByParentId(Long parentId);
}
