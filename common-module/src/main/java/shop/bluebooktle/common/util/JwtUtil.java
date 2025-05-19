package shop.bluebooktle.common.util;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import shop.bluebooktle.common.domain.auth.UserType;

@Component
public class JwtUtil {

	private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

	@Value("${jwt.secret}")
	private String secretKeyString;

	@Value("${jwt.access-token-validity-seconds}")
	private long accessTokenValiditySeconds;

	@Value("${jwt.refresh-token-validity-seconds}")
	private long refreshTokenValiditySeconds;

	private SecretKey secretKey;

	@PostConstruct
	public void init() {
		this.secretKey = Keys.hmacShaKeyFor(secretKeyString.getBytes(StandardCharsets.UTF_8));
	}

	public String createAccessToken(long userId, String userNickname, UserType userType) {
		return createToken(userId, userNickname, userType, accessTokenValiditySeconds * 1000);
	}

	public String createRefreshToken(long userId, String userNickname, UserType userType) {
		return createToken(userId, userNickname, userType, refreshTokenValiditySeconds * 1000);
	}

	private String createToken(long userId, String userNickname, UserType userType, long validityInMilliseconds) {
		Date now = new Date();
		Date validity = new Date(now.getTime() + validityInMilliseconds);

		return Jwts.builder()
			.subject(String.valueOf(userId))
			.claim("userNickname", userNickname)
			.claim("userType", userType.name())
			.issuedAt(now)
			.expiration(validity)
			.signWith(secretKey, Jwts.SIG.HS256)
			.compact();
	}

	public Claims getClaims(String token) {
		return Jwts.parser()
			.verifyWith(secretKey)
			.build()
			.parseSignedClaims(token)
			.getPayload();
	}

	public boolean validateToken(String token) {
		try {
			Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token);
			return true;
		} catch (SecurityException | MalformedJwtException e) {
			logger.error("Invalid JWT signature: {}", e.getMessage());
		} catch (ExpiredJwtException e) {
			logger.warn("Expired JWT token: {}", e.getMessage());
		} catch (UnsupportedJwtException e) {
			logger.error("Unsupported JWT token: {}", e.getMessage());
		} catch (IllegalArgumentException e) {
			logger.error("JWT claims string is empty: {}", e.getMessage());
		}
		return false;
	}

	public String getUserNicknameFromToken(String token) {
		return getClaims(token).get("userNickname", String.class);
	}

	public Long getUserIdFromToken(String token) {
		return getClaims(token).get("userId", Long.class);
	}

	public UserType getUserTypeFromToken(String token) {
		return UserType.valueOf(getClaims(token).get("userType", String.class));
	}

	public long getRefreshTokenExpirationMillis() {
		return refreshTokenValiditySeconds * 1000;
	}

	public boolean isTokenExpired(String token) {
		try {
			return getClaims(token).getExpiration().before(new Date());
		} catch (ExpiredJwtException e) {
			return true;
		} catch (Exception e) {
			logger.error("Could not determine token expiration: {}", e.getMessage());
			return true;
		}
	}

}