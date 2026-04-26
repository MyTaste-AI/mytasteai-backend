package myaimentor_api.mentor.bot;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import myaimentor_api.mentor.auth.AuthPrincipal;
import myaimentor_api.mentor.bot.dto.BotCreateRequest;
import myaimentor_api.mentor.bot.dto.BotResponse;
import myaimentor_api.mentor.bot.dto.BotSummaryResponse;
import myaimentor_api.mentor.bot.dto.BotUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

/**
 * 봇 API.
 * - GET /bots             : 목록 (categoryId 필터 가능)
 * - GET /bots/{id}        : 상세
 * - POST /bots            : 생성 (ADMIN, AI bot-vectors 동기화)
 * - PATCH /bots/{id}      : 수정 (ADMIN, description/provider 변경 시 AI 재호출)
 * - DELETE /bots/{id}     : 삭제 (ADMIN, AI bot-vectors 정리)
 */
@RestController
@RequestMapping("/bots")
@RequiredArgsConstructor
public class BotController {

	private final BotService botService;

	/** 봇 목록 (categoryId 옵션 필터, 페이징 지원) */
	@GetMapping
	public Page<BotSummaryResponse> findAll(
			@RequestParam(required = false) Long categoryId,
			@PageableDefault(size = 20, sort = "id", direction = Sort.Direction.DESC) Pageable pageable
	) {
		return botService.findAll(categoryId, pageable);
	}

	/** 봇 상세 */
	@GetMapping("/{id}")
	public BotResponse findById(@PathVariable Long id) {
		return botService.findById(id);
	}

	/** 봇 생성 — Spring DB 저장 + AI bot-vectors upsert */
	@PostMapping
	public ResponseEntity<Void> create(
			@AuthenticationPrincipal AuthPrincipal principal,
			@RequestBody @Valid BotCreateRequest request
	) {
		if (principal == null) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
		}
		Long id = botService.create(request, principal.userId());
		URI location = UriComponentsBuilder.fromPath("/bots/{id}").buildAndExpand(id).toUri();
		return ResponseEntity.created(location).build();
	}

	/** 봇 수정 — description/provider 변경 시 AI 재동기화 */
	@PatchMapping("/{id}")
	public BotResponse update(@PathVariable Long id, @RequestBody @Valid BotUpdateRequest request) {
		return botService.update(id, request);
	}

	/** 봇 삭제 — AI bot-vectors 도 같이 삭제 (멱등) */
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long id) {
		botService.delete(id);
	}
}
