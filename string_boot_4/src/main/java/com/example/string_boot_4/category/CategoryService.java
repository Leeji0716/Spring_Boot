package com.example.string_boot_4.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;



@RequiredArgsConstructor
@Service
public class CategoryService {
    private final CategoryRepository categoryRepository;
    public void create(String board){
        Category category = new Category();
        category.setBoard(board);
        this.categoryRepository.save(category);
    }
    public Category getCategory(int id) {
        Category category = this.categoryRepository.findById(id);

        return category;
    }
}
