package org.example.learningmanagementsystem.service;


import lombok.RequiredArgsConstructor;
import org.example.learningmanagementsystem.dto.ModuleDTO;
import org.example.learningmanagementsystem.entity.Module;
import org.example.learningmanagementsystem.mapper.ModuleMapper;
import org.example.learningmanagementsystem.repo.ModuleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ModuleService {
    
    private final ModuleRepository moduleRepository;
    private  final ModuleMapper moduleMapper;
    
    public List<ModuleDTO> getAll() {
        List<Module> modules = moduleRepository.findAll();
       return modules.stream()
               .map(moduleMapper::toDto).toList();

    }


    public ModuleDTO getById(Integer id) {
      Optional<Module> module = Optional.ofNullable(moduleRepository.findById(id)
              .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + id)));
      return moduleMapper.toDto(module.orElseThrow(() -> new RuntimeException("Lesson not found with id: " + id)));
    }

    public ModuleDTO create(ModuleDTO moduleDTO) {
        Module module = moduleMapper.toEntity(moduleDTO);
        return moduleMapper.toDto(moduleRepository.save(module));

    }

    public ModuleDTO update(Integer id,ModuleDTO moduleDTO) {
         Module module=moduleRepository.findById(id)
                 .orElseThrow(() -> new RuntimeException("Lesson not found with id: " + id));

       moduleMapper.updateModule(moduleDTO, module);
       moduleRepository.save(module);
        return moduleMapper.toDto(moduleRepository.save(module));

    }

    public void deleteById(Integer id) {
        Module module=moduleRepository.findById(id)
                        .orElseThrow(()-> new RuntimeException("Module not found bu id "+id));
        moduleRepository.delete(module);


    }


    
    
}
