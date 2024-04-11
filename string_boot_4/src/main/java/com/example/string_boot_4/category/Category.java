package com.example.string_boot_4.category;

import com.example.string_boot_4.question.Question;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String board;

//    @OneToMany(mappedBy = "category", cascade = CascadeType.REMOVE)
//    private List<Question> questionList;
}
