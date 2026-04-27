package myaimentor_api.mentor.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BotRepository extends JpaRepository<Bot, Long> {

	Page<Bot> findAllByCategoryId(Long categoryId, Pageable pageable);

	/**
	 * 비-ADMIN 사용자가 접근 가능한 봇만 조회. (공개 봇 OR allowlist 에 등록된 봇)
	 */
	@Query("""
			select b from Bot b
			where b.isPublic = true
			   or b.id in (select a.botId from BotAccess a where a.userId = :userId)
			""")
	Page<Bot> findAllAccessibleByUser(@Param("userId") Long userId, Pageable pageable);

	@Query("""
			select b from Bot b
			where b.categoryId = :categoryId
			  and (b.isPublic = true
			       or b.id in (select a.botId from BotAccess a where a.userId = :userId))
			""")
	Page<Bot> findAllAccessibleByUserAndCategoryId(
			@Param("userId") Long userId,
			@Param("categoryId") Long categoryId,
			Pageable pageable
	);
}
