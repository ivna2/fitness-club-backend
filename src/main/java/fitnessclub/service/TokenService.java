package fitnessclub.service;

import fitnessclub.dto.TokenResponse;
import fitnessclub.model.SessionStatus;
import fitnessclub.model.UserSession;
import fitnessclub.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserSessionRepository sessionRepository;
    private final UserDetailsService userDetailsService;

    public TokenResponse login(String username) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String accessToken = jwtTokenProvider.generateAccessToken(username, userDetails.getAuthorities());
        String refreshToken = jwtTokenProvider.generateRefreshToken(username);

        LocalDateTime now = LocalDateTime.now();
        UserSession session = new UserSession();
        session.setRefreshToken(jwtTokenProvider.hashRefreshToken(refreshToken));
        session.setUserId(username);
        session.setCreatedAt(now);
        session.setExpiresAt(now.plus(Duration.ofMillis(jwtTokenProvider.getRefreshTokenExpiration())));
        sessionRepository.save(session);

        return new TokenResponse(accessToken, refreshToken);
    }

    public TokenResponse refresh(String refreshToken) {
        String username = jwtTokenProvider.getUsernameFromRefreshToken(refreshToken);
        if (username == null) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        UserSession session = sessionRepository.findByRefreshToken(jwtTokenProvider.hashRefreshToken(refreshToken))
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (!SessionStatus.ACTIVE.equals(session.getStatus())) {
            throw new IllegalArgumentException("Session is not active");
        }

        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            session.setStatus(SessionStatus.EXPIRED);
            sessionRepository.save(session);
            throw new IllegalArgumentException("Refresh token expired");
        }

        // Ротация: старая сессия отзывается, выдаётся новая пара токенов
        session.setStatus(SessionStatus.REVOKED);
        sessionRepository.save(session);

        return login(username);
    }
}
