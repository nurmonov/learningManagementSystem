package org.example.learningmanagementsystem.service;


import lombok.RequiredArgsConstructor;
import org.example.learningmanagementsystem.dto.UserDTO;
import org.example.learningmanagementsystem.entity.User;
import org.example.learningmanagementsystem.mapper.UserMapper;
import org.example.learningmanagementsystem.repo.UserRepository;
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



    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserDTO getById(Integer id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            return userMapper.toDto(user.get());

        }else {
            throw new RuntimeException("Category not found with id: " + id);
        }
    }


    public UserDTO createUser(UserDTO userDTO) {
        // Admin foydalanuvchi uchun default parol
        String plainPassword = "123"; // admin foydalanuvchisi uchun parol

        User user = userMapper.toEntity(userDTO);

        // Parolni shifrlash
        String hashedPassword = passwordEncoder.encode(plainPassword);

        user.setPasswordHash(hashedPassword);

        userRepository.save(user);

        return userMapper.toDto(user);
    }


    //bu faqat 1 chi kirishida ishlaydi
    public UserDTO updatePassword(Integer userId, String newPassword) {
        // Foydalanuvchini id orqali olish
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));



        String hashedPassword = passwordEncoder.encode(newPassword);


        user.setPasswordHash(hashedPassword);


        userRepository.save(user);


        return userMapper.toDto(user);
    }

    public UserDTO changePassword(Integer userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Eski parolni tekshirish
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new RuntimeException("Old password does not match.");
        }

        // Yangi parolni shifrlash
        String hashedPassword = passwordEncoder.encode(newPassword);

        user.setPasswordHash(hashedPassword);
        userRepository.save(user);

        return userMapper.toDto(user);
    }



    public UserDTO updateUser( UserDTO userDTO) {
        User user = userRepository.findById(userDTO.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        user=userMapper.toEntity(userDTO);
        userRepository.save(user);
        return userMapper.toDto(user);
    }



    public UserDTO deleteUser(Integer userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            userRepository.delete(user.get());
            return userMapper.toDto(user.get());
        }
        throw new RuntimeException("User not found with id: " + userId);
    }



}
