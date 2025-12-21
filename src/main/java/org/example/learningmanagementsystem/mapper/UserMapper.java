package org.example.learningmanagementsystem.mapper;


import org.example.learningmanagementsystem.dto.UserDTO;
import org.example.learningmanagementsystem.dto.UserProfileDTO;
import org.example.learningmanagementsystem.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {


    UserDTO toDto(User user);

    UserProfileDTO toProfileDto(User user);

    User toEntity(UserDTO dto);
}
