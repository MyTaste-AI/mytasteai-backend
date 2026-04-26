package myaimentor_api.mentor.knowledge.dto;

import jakarta.validation.constraints.NotBlank;

public record KnowledgeCreateRequest(
		@NotBlank String content
) {
}
