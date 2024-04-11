package com.example.string_boot_4.category;

import com.example.string_boot_4.question.Question;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
}
