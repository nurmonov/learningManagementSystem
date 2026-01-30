package org.example.learningmanagementsystem.mapper;


import org.example.learningmanagementsystem.dto.ModuleDTO;
import org.example.learningmanagementsystem.entity.Module;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ModuleMapper {

    ModuleDTO toDto(Module module); // <-- har bir element uchun

    Module toEntity(ModuleDTO moduleDTO);

    void updateModule(ModuleDTO dto, @MappingTarget Module module);
}

