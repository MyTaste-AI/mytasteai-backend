package myaimentor_api.mentor.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BotAccessRepository extends JpaRepository<BotAccess, Long> {

	List<BotAccess> findAllByBotId(Long botId);

	List<BotAccess> findAllByUserId(Long userId);

	boolean existsByBotIdAndUserId(Long botId, Long userId);

	long deleteByBotIdAndUserId(Long botId, Long userId);

	void deleteAllByBotId(Long botId);
}
