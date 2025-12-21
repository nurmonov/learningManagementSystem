package org.example.learningmanagementsystem.mapper;


import org.example.learningmanagementsystem.dto.ModuleDTO;
import org.example.learningmanagementsystem.entity.Module;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ModuleMapper {

    ModuleDTO toDto(Module module);

    Module  toEntity(ModuleDTO moduleDTO);
}
