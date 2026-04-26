package myaimentor_api.user.account;

import lombok.RequiredArgsConstructor;
import myaimentor_api.common.error.BusinessException;
import myaimentor_api.common.error.ErrorCode;
import myaimentor_api.user.account.dto.UpdateMeRequest;
import myaimentor_api.user.account.dto.UserResponse;
import myaimentor_api.user.domain.User;
import myaimentor_api.user.domain.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 사용자 서비스 구현.
 * - findById:  공개 필드(id/email/name/role) 만 응답으로 변환. 비밀번호는 노출 금지.
 * - updateMe:  토큰의 userId 로 본인 엔티티를 로드해 dirty checking 으로 부분 업데이트.
 *              userId 가 토큰에는 있는데 DB 에 없으면 토큰은 유효하나 사용자 삭제 — 401 처리.
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	@Transactional(readOnly = true)
	public UserResponse findById(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
		return UserResponse.from(user);
	}

	@Override
	@Transactional
	public UserResponse updateMe(Long userId, UpdateMeRequest request) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new BusinessException(ErrorCode.AUTH_REQUIRED));
		user.updateProfile(request.name());
		return UserResponse.from(user);
	}
}
