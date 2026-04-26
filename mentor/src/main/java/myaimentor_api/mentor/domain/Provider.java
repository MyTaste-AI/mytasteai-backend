package myaimentor_api.mentor.domain;

/**
 * LLM 공급자.
 * AI 서비스 호출 시 query 파라미터/JSON 필드로 소문자 형태(openai/gemini)가 사용되므로
 * {@link #toAiServiceValue()}로 변환한다.
 */
public enum Provider {
	OPENAI,
	GEMINI;

	public String toAiServiceValue() {
		return name().toLowerCase();
	}
}
