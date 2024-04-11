package com.example.string_boot_4.question;

import com.example.string_boot_4.answer.Answer;
import com.example.string_boot_4.answer.AnswerForm;
import com.example.string_boot_4.answer.AnswerService;
import com.example.string_boot_4.category.Category;
import com.example.string_boot_4.category.CategoryService;
import com.example.string_boot_4.user.SiteUser;
import com.example.string_boot_4.user.UserService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@RequestMapping("/question")
@RequiredArgsConstructor
@Controller
public class QuestionController {
    private final QuestionService questionService;
    private final UserService userService;
    private final CategoryService categoryService;

    @GetMapping("/list")
    public String list(){
        return "redirect:/question/list/1";
    }

    @GetMapping("/list/{id}")
    public String list(Model model,
                       @PathVariable(value = "id") int id,
                       @RequestParam(value = "sort", defaultValue = "Date") String sort,
                       @RequestParam(value = "page", defaultValue = "0") int page,
                       @RequestParam(value = "kw", defaultValue = "") String kw){
        Page<Question> paging = this.questionService.getList(page, kw, sort, id);
        model.addAttribute("paging", paging);
        model.addAttribute("kw", kw);
        model.addAttribute("sort", sort);
        model.addAttribute("id", id);
        return "question_list";
    }
    @GetMapping(value = "/detail/{id}")
    public String detail(Model model,
                         @PathVariable("id") Integer id, AnswerForm answerForm,
                         @RequestParam(value = "sort", defaultValue = "Date") String sort,
                         @RequestParam(value = "page", defaultValue = "0") int page){
        Question question = this.questionService.getQuestion(id);
        this.questionService.hitPlus(question);
        Page<Answer> answerPaging = this.questionService.getAnswersForQuestion(question, page, sort);
        model.addAttribute("question", question);
        model.addAttribute("answerPaging", answerPaging);
        model.addAttribute("sort", sort);
        return "question_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create/{id}")
    public String questionCreate(@PathVariable("id") int id, QuestionForm questionForm){
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String questionCreate(@Valid QuestionForm questionForm, BindingResult bindingResult, Principal principal,
                                 @PathVariable("id") int id){ //폼 바인딩
        if (bindingResult.hasErrors()){
            return "question_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        Category category = this.categoryService.getCategory(id);
        this.questionService.create(questionForm.getSubject(), questionForm.getContent(), siteUser, category);
        return String.format("redirect:/question/list/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String modify(QuestionForm questionForm, @PathVariable("id") Integer id, Principal principal){
        Question question = this.questionService.getQuestion(id);
        if(!question.getAuthor().getUsername().equals(principal.getName())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        questionForm.setSubject(question.getSubject());
        questionForm.setContent(question.getContent());
        return "question_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String questionModify(@Valid QuestionForm questionForm, BindingResult bindingResult,
                                 Principal principal, @PathVariable("id") Integer id) {
        if (bindingResult.hasErrors()) {
            return "question_form";
        }
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.questionService.modify(question, questionForm.getSubject(), questionForm.getContent());
        return String.format("redirect:/question/detail/%s", id);
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String questionDelete(Principal principal, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        if (!question.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.questionService.delete(question);
        return "redirect:/";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/vote/{id}")
    public String questionVote(Principal principal, @PathVariable("id") Integer id){
        Question question = this.questionService.getQuestion(id);
        SiteUser siteUser = this.userService.getUser(principal.getName());
        this.questionService.vote(question, siteUser);
        return String.format("redirect:/question/detail/%s", id);
    }

    @GetMapping(value = "/comment/{id}")
    public String comment(Model model, @PathVariable("id") Integer id) {
        Question question = this.questionService.getQuestion(id);
        model.addAttribute("question", question);
        return "question_comment";
    }
}
