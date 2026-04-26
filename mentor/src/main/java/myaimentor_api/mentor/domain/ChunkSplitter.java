package myaimentor_api.mentor.domain;

/**
 * 봇이 지식 등록 시 텍스트를 어떻게 청크로 쪼갤지 결정하는 분할 방식.
 *
 *  RECURSIVE  : LangChain 식 — \n\n → \n → ". " → 공백 순으로 fallback (가장 보편적, 기본)
 *  SENTENCE   : 문장 단위 ('.'/'?'/'!') 로 쪼개고 그룹핑
 *  PARAGRAPH  : 빈 줄(\n\n) 단위로 쪼갬 — 잘 정돈된 문서에 적합
 *  FIXED      : N자 단위로 무조건 자름 — 단순/빠름, 의미 경계 무시
 *
 * AI 서비스가 실제 분할 로직을 수행하며, mentor 는 봇 설정으로만 보관/전달.
 */
public enum ChunkSplitter {
	RECURSIVE,
	SENTENCE,
	PARAGRAPH,
	FIXED;
}
