package org.example.learningmanagementsystem.repo;

import org.example.learningmanagementsystem.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ModuleRepository extends JpaRepository<Module,Integer> {
}
