
package org.example.learningmanagementsystem.controller;


import lombok.RequiredArgsConstructor;
import org.example.learningmanagementsystem.dto.ChangePasswordDTO;
import org.example.learningmanagementsystem.dto.UserCreateDTO;
import org.example.learningmanagementsystem.dto.UserDTO;
import org.example.learningmanagementsystem.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/user")
@PreAuthorize("hasRole('ADMIN')")
public class UserController {

    private final UserService userService;


    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Integer id) {
        Optional<UserDTO> userDTO= Optional.ofNullable(userService.getById(id));
        return userDTO.map(userDTO1 -> new ResponseEntity<>(userDTO1, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserCreateDTO userDTO) {
         UserDTO userDTO1 = userService.createUser(userDTO);
         return new ResponseEntity<>(userDTO1, HttpStatus.CREATED);
    }



//    @PostMapping("/addPassword/{id}")
//    public ResponseEntity<UserDTO> addPassword(@PathVariable Integer id, @RequestBody String password) {
//        UserDTO userDTO1 = userService.updatePassword(id, password);
//        return new ResponseEntity<>(userDTO1, HttpStatus.OK);
//    }

    @PutMapping
    public ResponseEntity<UserDTO> updateUser(@RequestBody UserDTO userDTO) {
        UserDTO userDTO1=userService.updateUser( userDTO);
        return new ResponseEntity<>(userDTO1, HttpStatus.OK);

    }

    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @PutMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordDTO request) {
        UserDTO userDTO1 = userService.changePassword( request.getOldPassword(), request.getNewPassword());
        return new ResponseEntity<>("Password changed successfully" ,HttpStatus.OK);
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id) {
               userService.deleteUser(id);
               return new ResponseEntity<>(HttpStatus.OK);
    }
}
