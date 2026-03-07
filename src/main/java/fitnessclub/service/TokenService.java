// src/main/java/fitnessclub/service/TokenService.java
package fitnessclub.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import fitnessclub.dto.TokenResponse;
import fitnessclub.model.SessionStatus;
import fitnessclub.model.UserSession;
import fitnessclub.repository.UserSessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

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
        String refreshTokenHash = jwtTokenProvider.hashRefreshToken(refreshToken);

        UserSession session = new UserSession();
        session.setRefreshToken(refreshTokenHash);
        session.setUserId(username);
        session.setCreatedAt(LocalDateTime.now());
        session.setExpiresAt(LocalDateTime.now().plusDays(7));
        sessionRepository.save(session);

        return new TokenResponse(accessToken, refreshToken);
    }

    public TokenResponse refresh(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        String refreshTokenHash = jwtTokenProvider.hashRefreshToken(refreshToken);

        UserSession session = sessionRepository.findByRefreshToken(refreshTokenHash)
                .orElseThrow(() -> new IllegalArgumentException("Session not found"));

        if (!SessionStatus.ACTIVE.equals(session.getStatus())) {
            throw new IllegalArgumentException("Session is not active");
        }

        if (session.getExpiresAt().isBefore(LocalDateTime.now())) {
            session.setStatus(SessionStatus.EXPIRED);
            sessionRepository.save(session);
            throw new IllegalArgumentException("Refresh token expired");
        }

        // Отзываем старую сессию
        session.setStatus(SessionStatus.REVOKED);
        sessionRepository.save(session);

        return login(username);
    }
}
