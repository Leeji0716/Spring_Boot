package com.example.string_boot_4.category;

import com.example.string_boot_4.question.Question;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RequestMapping("/category")
@RequiredArgsConstructor
@Controller
public class CategoryController {
    private CategoryService categoryService;

    @GetMapping("/list")
    private String list(@RequestParam(value = "brd", defaultValue = "QaA") String brd){
        return "category_list";
    }

}
