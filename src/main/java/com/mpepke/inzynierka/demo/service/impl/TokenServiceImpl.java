package com.mpepke.inzynierka.demo.service.impl;

import com.mpepke.inzynierka.demo.model.RefreshToken;
import com.mpepke.inzynierka.demo.model.User;
import com.mpepke.inzynierka.demo.service.RefreshTokenService;
import com.mpepke.inzynierka.demo.service.TokenService;
import com.mpepke.inzynierka.demo.service.UUIDService;
import com.mpepke.inzynierka.demo.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Service
@Transactional
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    @Value("${jwt.token.expiration}")
    int accessTokenExpiration;
    @Value("${jwt.token.refresh.expiration}")
    int refreshTokenExpiration;
    @Value("{$jwt.secret}")
    String secret;


    private final UserService userService;
    private final RefreshTokenService refreshTokenService;
    private final Clock clock;
    private final UUIDService uuidService;

    @Override
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    @Override
    public String generateAccessToken(String username) throws Exception {
        Optional<User> user = userService.loadByUsername(username);
        if (user.isEmpty())
            throw new Exception();
        return createToken(new HashMap<>(), user.get(), accessTokenExpiration);
    }

    @Override
    public String generateRefreshToken(String username) throws Exception {
        Optional<User> user = userService.loadByUsername(username);
        if (user.isEmpty())
            throw new Exception();

        Instant expiratoinDate = clock.instant().plusSeconds(refreshTokenExpiration);
        String newResfreshToken = uuidService.randomUUID().toString();
        Optional<RefreshToken> foundRefresh = refreshTokenService.findByUser(user.get());
        if (foundRefresh.isEmpty()) {
            RefreshToken refreshToken = RefreshToken.builder().user(user.get())
                    .expiryDate(expiratoinDate).token(newResfreshToken).build();
            refreshTokenService.save(refreshToken);
        } else {
            foundRefresh.get().setExpiryDate(expiratoinDate);
            foundRefresh.get().setToken(newResfreshToken);
            refreshTokenService.save(foundRefresh.get());
        }
        Date currentTime = new Date(clock.instant().toEpochMilli());
        Date expiratoinTime = new Date(expiratoinDate.toEpochMilli());
        return Jwts.builder().setClaims(new HashMap<>())
                .setId(newResfreshToken)
                .setSubject(username).setIssuedAt(currentTime)
                .setExpiration(expiratoinTime)
                .signWith(SignatureAlgorithm.HS256, secret).compact();
    }

    private String createToken(Map<String, Object> claims, User user, int lifeTime) {
        Date currentTime = new Date(System.currentTimeMillis());
        Date expirationTime = new Date(System.currentTimeMillis() + (long) lifeTime * 1000);
        userService.saveUser(user.toBuilder().tokenExpiredAt(new Timestamp(expirationTime.getTime())).build());
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(user.getUsername())
                .setIssuedAt(currentTime)
                .setExpiration(expirationTime)
                .signWith(SignatureAlgorithm.HS256, secret).compact();
    }


    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).
                getBody();
    }
}
