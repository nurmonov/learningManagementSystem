package org.example.learningmanagementsystem.service;


import lombok.RequiredArgsConstructor;
import org.example.learningmanagementsystem.dto.LoginRequestDTO;
import org.example.learningmanagementsystem.dto.LoginResponseDTO;
import org.example.learningmanagementsystem.entity.User;
import org.example.learningmanagementsystem.entity.roles.Role;
import org.example.learningmanagementsystem.repo.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private static final Logger logger =  LoggerFactory.getLogger(AuthService.class);

    public void register(String username, String email, String password) {
        // 1. Email va username'ni tekshirish (masalan, takrorlanmasligini tekshirish)
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("Username already exists");
        }

        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("Email already exists");
        }

        // 2. Parolni saqlash (masalan, bcrypt bilan)
        String passwordHash = passwordEncoder.encode(password);

        // 3. Yangi foydalanuvchini yaratish
        User newUser = new User();
        newUser.setUsername(username);
        newUser.setEmail(email);
        newUser.setPasswordHash(passwordHash);
        newUser.setRole(Role.USER); // Default role
        newUser.setCreatedAt(Instant.now());

        // 4. Foydalanuvchini saqlash
        userRepository.save(newUser);
    }


    public LoginResponseDTO login(LoginRequestDTO request) {
        try {
            // Userni olish
            User user = userRepository
                    .findByUsernameOrEmail(
                            request.getUsernameOrEmail(),
                            request.getUsernameOrEmail()
                    )
                    .orElseThrow(() -> {
                        logger.error("User not found: {}", request.getUsernameOrEmail());
                        return new RuntimeException("User not found");
                    });

            // Parolni tekshirish
            if (!passwordEncoder.matches(
                    request.getPassword(),   // plain
                    user.getPasswordHash()   // DB hash
            )) {
                throw new RuntimeException("Invalid password");
            }


            // JWT token yaratish
            String token = jwtService.generateToken(user);
            logger.info("User {} logged in successfully", user.getUsername());

            LoginResponseDTO response = new LoginResponseDTO();
            response.setToken(token);
            return response;
        } catch (Exception e) {
            logger.error("Error during login process: {}", e.getMessage(), e);
            throw e;  // xatolikni qaytarish
        }
    }

    public LoginResponseDTO login1(LoginRequestDTO request) {
        try {
            // Userni username orqali olish
            User user = userRepository
                    .findByUsername(request.getUsernameOrEmail())  // emailni olib tashladik
                    .orElseThrow(() -> {
                        logger.error("User not found: {}", request.getUsernameOrEmail());
                        return new RuntimeException("User not found");
                    });

            // Parolni tekshirish
            if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
                logger.warn("Invalid password for user: {}", request.getUsernameOrEmail());
                throw new RuntimeException("Invalid password");
            }

            // JWT token yaratish
            String token = jwtService.generateToken(user);
            logger.info("User {} logged in successfully", user.getUsername());
            System.out.println(token);
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

