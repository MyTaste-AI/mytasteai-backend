package myaimentor_api.chat.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatSessionRepository extends MongoRepository<ChatSession, String> {

	Page<ChatSession> findAllByUserId(Long userId, Pageable pageable);
}
