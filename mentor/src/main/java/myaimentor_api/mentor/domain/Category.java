package myaimentor_api.mentor.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * 봇 카테고리 — self-reference 로 2단계 트리(대분류/중분류) 구성.
 * - parentId == null  : 대분류
 * - parentId != null  : 중분류 (parent 는 반드시 대분류여야 함 — service 레이어에서 강제)
 *
 * 단순 분류용이라 봇과는 1:N (한 봇은 한 카테고리). 봇은 어느 레벨 카테고리든 가리킬 수 있다.
 */
@Entity
@Table(name = "categories", indexes = {
		@Index(name = "idx_categories_parent_id", columnList = "parent_id")
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String name;

	/** 대분류면 null, 중분류면 부모(대분류)의 id. */
	@Column(name = "parent_id")
	private Long parentId;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	@Builder
	private Category(String name, Long parentId) {
		this.name = name;
		this.parentId = parentId;
	}
}
