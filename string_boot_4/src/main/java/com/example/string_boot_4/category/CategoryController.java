package com.example.string_boot_4.category;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequestMapping("/category")
@RequiredArgsConstructor
@Controller
public class CategoryController {
    private CategoryService categoryService;
    @GetMapping("list")
    private String list(@RequestParam(value = "id", defaultValue = "1") int id){
        return "redirect:/question/list/" + id;
    }

}
