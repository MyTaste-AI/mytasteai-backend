package myaimentor_api.mentor.domain;

/**
 * 봇이 채팅 시 사용하는 지식 검색 방식.
 *  - VECTOR : 의미 유사도 (임베딩 기반) — 동의어/문맥 매칭에 강함 (현재 기본)
 *  - KEYWORD: 키워드/BM25 — 정확한 용어/이름 매칭에 강함
 *  - HYBRID : 두 결과를 결합 (RRF 등) — 가장 견고하지만 비용↑
 *
 * AI 서비스가 실제 검색 알고리즘을 수행하며, mentor 는 봇 설정으로만 보관/전달.
 */
public enum SearchType {
	VECTOR,
	KEYWORD,
	HYBRID;
}
