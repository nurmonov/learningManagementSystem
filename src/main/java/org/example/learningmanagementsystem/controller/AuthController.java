package org.example.learningmanagementsystem.controller;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.learningmanagementsystem.dto.LoginRequestDTO;
import org.example.learningmanagementsystem.dto.LoginResponseDTO;
import org.example.learningmanagementsystem.dto.RegisterRequestDTO;
import org.example.learningmanagementsystem.entity.RefreshToken;
import org.example.learningmanagementsystem.entity.User;
import org.example.learningmanagementsystem.repo.UserRepository;
import org.example.learningmanagementsystem.service.AuthService;
import org.example.learningmanagementsystem.filter.JwtService;
import org.example.learningmanagementsystem.service.RefreshTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;




    @PostMapping("/register")
    public ResponseEntity<LoginResponseDTO> register(
            @RequestBody RegisterRequestDTO dto,
            HttpServletResponse response
    ) {
        LoginResponseDTO loginResponse =
                authService.register(dto, response);

        return ResponseEntity.ok(loginResponse);
    }




    @PostMapping("/login")
    public LoginResponseDTO login(
            @RequestBody LoginRequestDTO request,
            HttpServletResponse response
    ) {
        return authService.login(request, response);
    }

    @PostMapping("/refresh")
    public LoginResponseDTO refreshToken(@CookieValue("refreshToken") String refreshToken) {

        RefreshToken token = refreshTokenService.verify(refreshToken);

        User user = userRepository.findById(token.getUserId()).orElseThrow();

        String newAccessToken = jwtService.generateToken(user);

        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(newAccessToken);

        return response;
    }
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(value = "refreshToken", required = false) String refreshToken,
            HttpServletResponse response
    ) {
        if (refreshToken != null) {
            RefreshToken token = refreshTokenService.verify(refreshToken);
            refreshTokenService.deleteByUserId(token.getUserId());
        }

        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setHttpOnly(true);
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/test")
    public Map<String, String> test() {
        return Map.of("status", "OK");
    }




    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) {
            throw new RuntimeException("No cookies found");
        }

        for (Cookie cookie : request.getCookies()) {
            if ("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        throw new RuntimeException("Refresh token not found");
    }
}

