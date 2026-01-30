package org.example.learningmanagementsystem.controller;

import lombok.RequiredArgsConstructor;
import org.example.learningmanagementsystem.dto.ModuleDTO;
import org.example.learningmanagementsystem.service.ModuleService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/modules")
@RequiredArgsConstructor
public class ModuleController {

    private final ModuleService moduleService;


    @GetMapping
    public HttpEntity<List<ModuleDTO>> findAll() {
       List<ModuleDTO> moduleDTOs = moduleService.getAll();
       return new ResponseEntity<>(moduleDTOs, HttpStatus.OK);

    }

    @GetMapping("/{id}")
    public HttpEntity<ModuleDTO> findById(@PathVariable Integer id) {
        ModuleDTO moduleDTO = moduleService.getById(id);
        return new ResponseEntity<>(moduleDTO, HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public HttpEntity<ModuleDTO> save(@RequestBody ModuleDTO moduleDTO) {
        ModuleDTO module=moduleService.create(moduleDTO);
        return new ResponseEntity<>(module, HttpStatus.CREATED);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public HttpEntity<ModuleDTO> update(@PathVariable Integer id,@RequestBody ModuleDTO moduleDTO) {
        ModuleDTO moduleDTO1 = moduleService.update(id,moduleDTO);
        return new ResponseEntity<>(moduleDTO1, HttpStatus.OK);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public HttpEntity<Void> delete(@PathVariable Integer id) {
        moduleService.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
