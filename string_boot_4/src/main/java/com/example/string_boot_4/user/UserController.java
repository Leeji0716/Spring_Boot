package com.example.string_boot_4.user;

import com.example.string_boot_4.answer.Answer;
import com.example.string_boot_4.answer.AnswerForm;
import com.example.string_boot_4.comment.Comment;
import com.example.string_boot_4.question.Question;
import com.example.string_boot_4.question.QuestionForm;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final EmailService emailService;

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

    @GetMapping("/password")
    public String password(){
        return "password_form";
    }

    @PostMapping("/password/{username}/{email}")
    public String password(@PathVariable("username") String username,
                           @PathVariable("email") String email) {
        SiteUser user = userService.getUser(username);
        if (user == null || !user.getEmail().equals(email)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "사용자 정보가 올바르지 않습니다.");
        }

        // 임시 비밀번호 생성
        String temporaryPassword = generateTemporaryPassword();

        // 임시 비밀번호를 사용자 이메일로 전송
        emailService.sendTemporaryPassword(user.getEmail(), temporaryPassword);

        userService.modify(user, temporaryPassword);
        return "redirect:/login";
    }
    private String generateTemporaryPassword() {
        // 임시 비밀번호를 생성하는 로직을 작성해야 합니다.
        // 임시 비밀번호는 보안을 고려하여 무작위로 생성되어야 합니다.
        // 예를 들어, 랜덤 문자열 생성 또는 임시 비밀번호 제너레이터를 사용할 수 있습니다.
        // 이 예제에서는 임시 비밀번호를 랜덤 문자열로 생성하는 것으로 가정합니다.
        // 실제로는 더 강력한 보안 방법을 고려해야 합니다.
        String temporaryPassword = "RandomTemporaryPassword"; // 임시 비밀번호 생성 예시
        return temporaryPassword;
    }
}
