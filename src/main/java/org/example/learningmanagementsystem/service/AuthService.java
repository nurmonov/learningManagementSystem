package org.example.learningmanagementsystem.service;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.learningmanagementsystem.dto.LoginRequestDTO;
import org.example.learningmanagementsystem.dto.LoginResponseDTO;
import org.example.learningmanagementsystem.dto.RegisterRequestDTO;
import org.example.learningmanagementsystem.entity.RefreshToken;
import org.example.learningmanagementsystem.entity.User;
import org.example.learningmanagementsystem.entity.roles.Role;
import org.example.learningmanagementsystem.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private static final Logger logger =  LoggerFactory.getLogger(AuthService.class);

    public LoginResponseDTO register(
            RegisterRequestDTO dto,
            HttpServletResponse response
    ) {

        // 1️⃣ User yaratish
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);

        // 2️⃣ Access token
        String accessToken = jwtService.generateToken(user);

        // 3️⃣ Refresh token
        Optional<RefreshToken> refreshToken = refreshTokenService.createOrGetRefreshToken(user.getId());
        System.out.println(accessToken);
        System.out.println(refreshToken);

        // 4️⃣ Cookie
        Cookie cookie = new Cookie("refreshToken", refreshToken.get().getToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true); // localda false bo‘lishi mumkin
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(14 * 24 * 60 * 60);
        response.addCookie(cookie);
        System.out.println(accessToken);
        System.out.println(refreshToken.get().getToken());
        // 5️⃣ Response
        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setToken(accessToken);

        return loginResponse;
    }



    public LoginResponseDTO login(
            LoginRequestDTO request,
            HttpServletResponse response) {

        User user = userRepository
                .findByUsernameOrEmail(
                        request.getUsername(),
                        request.getUsername()
                )
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid password");
        }

        // 1️⃣ Access token
        String accessToken = jwtService.generateToken(user);

        // 2️⃣ Refresh token (DB + random)
        Optional<RefreshToken> refreshToken = refreshTokenService.createOrGetRefreshToken(user.getId());

        // 3️⃣ Cookie
        Cookie cookie = new Cookie("refreshToken", refreshToken.get().getToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(true);              // prod = true
        cookie.setPath("/auth/refresh");
        cookie.setMaxAge(14 * 24 * 60 * 60); // 14 kun
        response.addCookie(cookie);

        // 4️⃣ Response
        LoginResponseDTO dto = new LoginResponseDTO();
        dto.setToken(accessToken);
        dto.setRefreshToken(refreshToken.get().getToken());
        System.out.println(accessToken);
        System.out.println(refreshToken.get().getToken());
        return dto;
    }

    public LoginResponseDTO login1(LoginRequestDTO request) {
        try {
            // Userni username orqali olish
            User user = userRepository
                    .findByUsername(request.getUsername())  // emailni olib tashladik
                    .orElseThrow(() -> {
                        logger.error("User not found: {}", request.getUsername());
                        return new RuntimeException("User not found");
                    });

            // Parolni tekshirish
            if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                logger.warn("Invalid password for user: {}", request.getUsername());
                throw new RuntimeException("Invalid password");
            }

            // JWT token yaratish
            String token = jwtService.generateToken(user);
            logger.info("User {} logged in successfully", user.getUsername());

            // Javobni yaratish
            LoginResponseDTO response = new LoginResponseDTO();
            response.setToken(token);
            return response;
        } catch (Exception e) {
            logger.error("Error during login process: {}", e.getMessage(), e);
            throw e;  // xatolikni qaytarish
        }
    }


}

