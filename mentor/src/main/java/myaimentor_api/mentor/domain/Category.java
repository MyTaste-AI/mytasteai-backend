package myaimentor_api.mentor.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

/**
 * 봇 카테고리. 예) "연애/관계", "커리어", "학습".
 * 단순 분류용이라 봇과 1:N (한 봇은 한 카테고리).
 */
@Entity
@Table(name = "categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 100)
	private String name;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	@Builder
	private Category(String name) {
		this.name = name;
	}
}
