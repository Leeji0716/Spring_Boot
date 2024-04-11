package com.example.string_boot_4.category;

import com.example.string_boot_4.question.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class CategoryService {
    private CategoryRepository categoryRepository;
    public void create(String board){
        Category category = new Category();
        category.setBoard(board);
        this.categoryRepository.save(category);
    }

}
