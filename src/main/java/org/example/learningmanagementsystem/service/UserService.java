package org.example.learningmanagementsystem.service;


import lombok.RequiredArgsConstructor;
import org.example.learningmanagementsystem.dto.UserCreateDTO;
import org.example.learningmanagementsystem.dto.UserDTO;
import org.example.learningmanagementsystem.entity.User;
import org.example.learningmanagementsystem.entity.roles.Role;
import org.example.learningmanagementsystem.filter.JwtService;
import org.example.learningmanagementsystem.mapper.UserMapper;
import org.example.learningmanagementsystem.repo.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final JwtService jwtService;



    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserDTO getById(Integer id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return userMapper.toDto(user.get());

        }else {
            throw new RuntimeException("User not found with id: " + id);
        }
    }


    public UserDTO createUser(UserCreateDTO userDTO) {

        String plainPassword = "123";
        Role rl= Role.valueOf("USER");

        User user = userMapper.toEntityCreate(userDTO);


        String hashedPassword = passwordEncoder.encode(plainPassword);

        user.setPasswordHash(hashedPassword);

        user.setRole(rl);

        userRepository.save(user);

        return userMapper.toDto(user);
    }



    public UserDTO updatePassword(Integer userId, String newPassword) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));



        String hashedPassword = passwordEncoder.encode(newPassword);


        user.setPasswordHash(hashedPassword);


        userRepository.save(user);


        return userMapper.toDto(user);
    }

    public UserDTO changePassword(String oldPassword, String newPassword) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String token = (String) auth.getCredentials();
        Integer userId = jwtService.getUserIdFromToken(token); // endi null emas

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("Old password does not match");
        }

        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return userMapper.toDto(user);
    }




    public UserDTO updateUser( UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (userDTO.getEmail() != null) {
            user.setEmail(userDTO.getEmail());
        }
        if (userDTO.getUsername() != null) {
            user.setUsername(userDTO.getUsername());
        }
        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }



    public void deleteUser(Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        userRepository.delete(user);
    }




}
