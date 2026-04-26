package myaimentor_api.mentor.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BotRepository extends JpaRepository<Bot, Long> {

	Page<Bot> findAllByCategoryId(Long categoryId, Pageable pageable);
}
