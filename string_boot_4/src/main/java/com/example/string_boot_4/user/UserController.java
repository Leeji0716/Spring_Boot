package com.example.string_boot_4.user;

import com.example.string_boot_4.answer.Answer;
import com.example.string_boot_4.answer.AnswerForm;
import com.example.string_boot_4.comment.Comment;
import com.example.string_boot_4.question.Question;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @GetMapping("/signup")
    public String signup(UserCreateForm userCreateForm){
        return "signup_form";
    }

    @PostMapping("/signup")
    public String signup(@Valid UserCreateForm userCreateForm, BindingResult bindingResult){
        if (bindingResult.hasErrors()){
            return "signup_form";
        }

        if(!userCreateForm.getPassword1().equals(userCreateForm.getPassword2())){
            bindingResult.rejectValue("password2", "passwordInCorrect", "2개의 패스워드가 일치하지 않습니다.");
            return "signup_form";
        }

        try {
            userService.create(userCreateForm.getName(), userCreateForm.getUsername(),
                    userCreateForm.getEmail(), userCreateForm.getPassword1());
        }catch(DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 등록된 사용자입니다.");
            return "signup_form";
        }catch(Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "signup_form";
        }

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login() {
        return "login_form";
    }

    @GetMapping("/profile")
    public String profile(Model model,
                          Principal principal){
        SiteUser user = this.userService.getUser(principal.getName());
        List<Question> questionList = this.userService.getQuestionList(user);
        List<Answer> answerList = this.userService.getAnswerList(user);
        List<Comment> commentList = this.userService.getCommentList(user);
        model.addAttribute("username", user.getUsername());
        model.addAttribute("name", user.getName());
        model.addAttribute("questionList", questionList);
        model.addAttribute("answerList", answerList);
        model.addAttribute("commentList", commentList);
        return "profile_form";
    }
//    @PreAuthorize("isAuthenticated()")
//    @GetMapping("/modify")
//    public String userModify(UserCreateForm userCreateForm, Principal principal) {
//        SiteUser user = this.userService.getUser(principal.getName());
//        if (!user.getPassword().equals()) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
//        }
//        answerForm.setContent(answer.getContent());
//        return "answer_form";
//    }

//    @PreAuthorize("isAuthenticated()")
//    @PostMapping("/modify/{id}")
//    public String answerModify(@Valid AnswerForm answerForm, BindingResult bindingResult,
//                               @PathVariable("id") Integer id, Principal principal) {
//        if (bindingResult.hasErrors()) {
//            return "answer_form";
//        }
//        Answer answer = this.answerService.getAnswer(id);
//        if (!answer.getAuthor().getUsername().equals(principal.getName())) {
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
//        }
//        this.answerService.modify(answer, answerForm.getContent());
//        return String.format("redirect:/question/detail/%s#answer_%s", answer.getQuestion().getId(), answer.getId());
//    }
}
