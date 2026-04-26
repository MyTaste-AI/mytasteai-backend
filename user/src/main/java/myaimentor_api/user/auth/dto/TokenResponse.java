package myaimentor_api.user.auth.dto;

public record TokenResponse(
		String tokenType,
		String accessToken,
		long expiresIn
) {
	public static TokenResponse bearer(String accessToken, long expiresInSeconds) {
		return new TokenResponse("Bearer", accessToken, expiresInSeconds);
	}
}
