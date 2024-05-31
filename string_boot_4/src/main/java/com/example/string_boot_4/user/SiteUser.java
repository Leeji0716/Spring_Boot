package com.example.string_boot_4.user;

import com.example.string_boot_4.question.Question;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class SiteUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String username;

    private String password;

    @Column(unique = true)
    private String email;

    @Column(length = 20)
    private String name;

    @OneToMany
    private List<Question> questionList;

    private String profileImagePath;
}
