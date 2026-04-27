package myaimentor_api.mentor.bot;

import lombok.RequiredArgsConstructor;
import myaimentor_api.common.error.BusinessException;
import myaimentor_api.common.error.ErrorCode;
import myaimentor_api.mentor.bot.dto.BotAccessResponse;
import myaimentor_api.mentor.domain.BotAccess;
import myaimentor_api.mentor.domain.BotAccessRepository;
import myaimentor_api.mentor.domain.BotRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BotAccessServiceImpl implements BotAccessService {

	private final BotAccessRepository botAccessRepository;
	private final BotRepository botRepository;

	@Override
	@Transactional(readOnly = true)
	public List<BotAccessResponse> list(Long botId) {
		ensureBotExists(botId);
		return botAccessRepository.findAllByBotId(botId).stream()
				.map(BotAccessResponse::from)
				.toList();
	}

	@Override
	@Transactional
	public void grant(Long botId, Long userId) {
		ensureBotExists(botId);
		if (botAccessRepository.existsByBotIdAndUserId(botId, userId)) {
			return;
		}
		botAccessRepository.save(new BotAccess(botId, userId));
	}

	@Override
	@Transactional
	public void revoke(Long botId, Long userId) {
		ensureBotExists(botId);
		botAccessRepository.deleteByBotIdAndUserId(botId, userId);
	}

	private void ensureBotExists(Long botId) {
		if (!botRepository.existsById(botId)) {
			throw new BusinessException(ErrorCode.BOT_NOT_FOUND);
		}
	}
}
