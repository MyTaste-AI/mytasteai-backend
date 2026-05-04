package myaimentor_api.user.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Entity
@Table(name = "users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 255)
	private String email;

	@Column(nullable = false, length = 255)
	private String password;

	@Column(nullable = false, length = 100)
	private String name;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private Role role;

	@CreationTimestamp
	@Column(nullable = false, updatable = false)
	private Instant createdAt;

	@UpdateTimestamp
	@Column(nullable = false)
	private Instant updatedAt;

	/**
	 * 회원 탈퇴 요청 시각. null 이면 정상 회원, 값이 있으면 탈퇴 진행 중 (30일 후 영구 삭제 예정).
	 * 별도 배치 모듈에서 이 시각 기준 30일 경과 사용자를 cascade 삭제한다.
	 */
	@Column
	private Instant withdrawnAt;

	/**
	 * 탈퇴 사유 — 사용자가 탈퇴 시 입력한 자유 텍스트 (선택). 서비스 개선을 위한 통계 용도.
	 */
	@Column(length = 500)
	private String withdrawalReason;

	@Builder
	private User(String email, String password, String name, Role role) {
		this.email = email;
		this.password = password;
		this.name = name;
		this.role = role == null ? Role.USER : role;
	}

	public void updateProfile(String name) {
		if (name != null && !name.isBlank()) {
			this.name = name;
		}
	}

	public void changePassword(String encodedPassword) {
		this.password = encodedPassword;
	}

	public boolean isWithdrawn() {
		return this.withdrawnAt != null;
	}

	/**
	 * 탈퇴 요청 처리. 이미 탈퇴 진행 중이면 호출자가 분기해야 함 — 여기선 단순히 시각/사유만 기록한다.
	 * reason 은 null/blank 허용 (선택 입력).
	 */
	public void withdraw(Instant at, String reason) {
		this.withdrawnAt = at;
		this.withdrawalReason = (reason == null || reason.isBlank()) ? null : reason.trim();
	}
}
