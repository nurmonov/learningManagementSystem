package org.example.learningmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.example.learningmanagementsystem.entity.roles.Level;

import java.math.BigDecimal;


@Entity
@Table(name = "courses")
@Getter @Setter
public class Course {


    @Id
    @GeneratedValue
    private Integer id;


    private String title;


    @Column(columnDefinition = "text")
    private String description;


    private String instructor;

    private String duration;

    private Integer students;

    private BigDecimal rating;

    private String image;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Level level;

    private String category;
}

