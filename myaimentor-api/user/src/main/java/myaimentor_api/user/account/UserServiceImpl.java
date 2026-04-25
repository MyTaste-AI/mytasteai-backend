package myaimentor_api.user.account;

import lombok.RequiredArgsConstructor;
import myaimentor_api.user.account.dto.UpdateMeRequest;
import myaimentor_api.user.account.dto.UserResponse;
import myaimentor_api.user.domain.User;
import myaimentor_api.user.domain.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;

	@Override
	@Transactional(readOnly = true)
	public UserResponse findById(Long id) {
		User user = userRepository.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."));
		return UserResponse.from(user);
	}

	@Override
	@Transactional
	public UserResponse updateMe(Long userId, UpdateMeRequest request) {
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED));
		user.updateProfile(request.name());
		return UserResponse.from(user);
	}
}
