package myaimentor_api.mentor.knowledge;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import myaimentor_api.mentor.ai.dto.KnowledgeResponse;
import myaimentor_api.mentor.knowledge.dto.KnowledgeCreateRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 봇 지식 API — AI 마이크로서비스로 그대로 위임 (Spring DB 미저장).
 * 응답 스키마는 AI 서비스가 제공하는 KnowledgeResponse 그대로.
 *
 * - GET    /bots/{botId}/knowledge          : 지식 목록
 * - POST   /bots/{botId}/knowledge          : 지식 등록 (ADMIN)
 * - DELETE /bots/{botId}/knowledge/{id}     : 지식 삭제 (ADMIN)
 */
@RestController
@RequestMapping("/bots/{botId}/knowledge")
@RequiredArgsConstructor
@Validated
public class KnowledgeController {

	private final KnowledgeService knowledgeService;

	@GetMapping
	public List<KnowledgeResponse> list(
			@PathVariable Long botId,
			@RequestParam(defaultValue = "20") @Min(1) @Max(100) int limit,
			@RequestParam(defaultValue = "0") @Min(0) int offset
	) {
		return knowledgeService.list(botId, limit, offset);
	}

	@PostMapping
	public ResponseEntity<KnowledgeResponse> create(
			@PathVariable Long botId,
			@RequestBody @Valid KnowledgeCreateRequest request
	) {
		KnowledgeResponse created = knowledgeService.create(botId, request);
		return ResponseEntity.status(HttpStatus.CREATED).body(created);
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Long botId, @PathVariable Long id) {
		knowledgeService.delete(botId, id);
	}
}
