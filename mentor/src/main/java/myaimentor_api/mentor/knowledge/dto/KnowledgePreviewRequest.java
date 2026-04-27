package myaimentor_api.mentor.knowledge.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * 지식 청킹 미리보기 요청.
 * 봇 설정의 청킹 파라미터(chunk_size/overlap/splitter) 는 서버가 봇에서 조회해 채움.
 */
public record KnowledgePreviewRequest(
		@NotBlank String content
) {
}
