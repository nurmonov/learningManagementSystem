package org.example.learningmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.learningmanagementsystem.entity.roles.LessonType;

@Entity
@Table(name = "lessons")
@Getter @Setter
public class Lesson {


    @Id
    @GeneratedValue
    private Integer id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id")
    private Module module;


    private String title;
    private String duration;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private LessonType type;
    private Integer position;
    private boolean locked;
}

