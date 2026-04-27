package myaimentor_api.mentor.bot;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import myaimentor_api.mentor.bot.dto.BotAccessRequest;
import myaimentor_api.mentor.bot.dto.BotAccessResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 비공개 봇에 대한 사용자별 접근 허용 관리 (ADMIN 전용).
 * - GET    /bots/{id}/access            : 허용된 사용자 목록
 * - POST   /bots/{id}/access            : 사용자 추가 (멱등)
 * - DELETE /bots/{id}/access/{userId}   : 사용자 제거 (멱등)
 */
@RestController
@RequestMapping("/bots/{id}/access")
@RequiredArgsConstructor
public class BotAccessController {

	private final BotAccessService botAccessService;

	@GetMapping
	public List<BotAccessResponse> list(@PathVariable Long id) {
		return botAccessService.list(id);
	}

	@PostMapping
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void grant(@PathVariable Long id, @RequestBody @Valid BotAccessRequest request) {
		botAccessService.grant(id, request.userId());
	}

	@DeleteMapping("/{userId}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void revoke(@PathVariable Long id, @PathVariable Long userId) {
		botAccessService.revoke(id, userId);
	}
}
