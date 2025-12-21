package org.example.learningmanagementsystem.controller;


import lombok.RequiredArgsConstructor;
import org.example.learningmanagementsystem.dto.LoginRequestDTO;
import org.example.learningmanagementsystem.dto.LoginResponseDTO;
import org.example.learningmanagementsystem.dto.RegisterRequestDTO;
import org.example.learningmanagementsystem.repo.UserRepository;
import org.example.learningmanagementsystem.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;



    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequestDTO dto) {
        try {
            // RegisterService metodini chaqiramiz
            authService.register(dto.getUsername(), dto.getEmail(), dto.getPassword());
            return ResponseEntity.ok("User registered successfully");
        } catch (Exception e) {
            // Xatolik yuzaga kelganda, errorni loglash
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while registering the user");
        }
    }



    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @RequestBody LoginRequestDTO request
    ) {
        return ResponseEntity.ok(authService.login1(request));
    }
}

