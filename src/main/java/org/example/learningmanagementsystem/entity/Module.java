package org.example.learningmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Entity
@Table(name = "modules")
@Getter @Setter
public class Module {


    @Id
    @GeneratedValue
    private Integer id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;


    private String title;
    private Integer position;
}

