package com.example.string_boot_4.user;

import com.example.string_boot_4.answer.Answer;
import com.example.string_boot_4.comment.Comment;
import com.example.string_boot_4.mail.EmailService;
import com.example.string_boot_4.question.Question;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.security.SecureRandom;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final EmailService emailService;
    private final FileUploadUtil fileUploadUtil;

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int PASSWORD_LENGTH = 10;

    public static String generateTemporaryPassword() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(PASSWORD_LENGTH);
        for (int i = 0; i < PASSWORD_LENGTH; i++) {
            int randomIndex = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(randomIndex));
        }
        return sb.toString();
    }

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
        model.addAttribute("profileImagePath", user.getProfileImagePath());
        model.addAttribute("username", user.getUsername());
        model.addAttribute("name", user.getName());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("questionList", questionList);
        model.addAttribute("answerList", answerList);
        model.addAttribute("commentList", commentList);
        return "profile_form";
    }

    @GetMapping("/password")
    public String password(){
        return "password_form";
    }

    @PostMapping("/password")
    public String password(Model model,
                           @RequestParam("username") String username,
                           @RequestParam("email") String email) {
        System.out.println(username);
        System.out.println(email);
        SiteUser user = userService.getUser(username);
        if (user == null || !user.getEmail().equals(email)) {
            boolean error=true;
            model.addAttribute("error", error);
            return "password_check";
        }
        // 임시 비밀번호 생성
        String temporaryPassword = generateTemporaryPassword();
        // 임시 비밀번호를 사용자 이메일로 전송
        emailService.sendTemporaryPassword(user.getEmail(), temporaryPassword);
        boolean success=true;
        model.addAttribute("success", success);

        userService.passwordModify(user, temporaryPassword);
        return "password_check";

    }
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/passModify")
    public String passwordModify(UserCreateForm userCreateForm, Principal principal){
        SiteUser user = this.userService.getUser(principal.getName());
        userCreateForm.setPassword1(user.getPassword());
        userCreateForm.setPassword2(user.getPassword());
        return "password_modify";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/passModify")
    public String passwordModify(@Valid UserCreateForm userCreateForm, BindingResult bindingResult, Principal principal){
        if (bindingResult.hasErrors()) {
            System.out.println("error");
            return "password_modify";
        }
        SiteUser user = this.userService.getUser(principal.getName());
        userService.passwordModify(user, userCreateForm.getPassword1());
        return "profile_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/proDetail")
    public String profileDetail(UserCreateForm userCreateForm, Principal principal){
        SiteUser user = this.userService.getUser(principal.getName());
        userCreateForm.setUsername(user.getUsername());
        userCreateForm.setEmail(user.getEmail());
        userCreateForm.setName(user.getName());
        userCreateForm.setProfileImagePath(user.getProfileImagePath());
        return "profile_detail";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/proDetail")
    public String profileDetail(@Valid UserCreateForm userCreateForm, BindingResult bindingResult, Principal principal,
                                @RequestParam("profileImage") MultipartFile profileImage) {

        if (bindingResult.hasErrors()) {
            return "profile_detail";
        }
        // 사용자가 새로운 파일을 선택한 경우에만 파일을 업로드하고 경로를 설정합니다.
        if (!profileImage.isEmpty()) {
            String fileName = StringUtils.cleanPath(profileImage.getOriginalFilename());
            String uploadDir = fileUploadUtil.getUploadDirPath() + principal.getName();
            try {
                userCreateForm.setProfileImagePath(fileName);
                this.fileUploadUtil.saveFile(uploadDir, fileName, profileImage);
            } catch (IOException e) {
                e.printStackTrace();
                bindingResult.reject("fileUploadError", "프로필 이미지 업로드 중 오류가 발생했습니다.");
                return "profile_detail";
            }
        }
        // 사용자가 프로필 사진을 변경하지 않은 경우에는 이전 파일 경로를 유지합니다.
        else {
            String previousProfileImagePath = userService.getUser(principal.getName()).getProfileImagePath();
            userCreateForm.setProfileImagePath(previousProfileImagePath);
        }

        SiteUser user = this.userService.getUser(principal.getName());

        try {
            userService.modify(user, userCreateForm.getUsername(), userCreateForm.getName(), user.getPassword(),
                    userCreateForm.getEmail(), userCreateForm.getProfileImagePath());
        } catch (DataIntegrityViolationException e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", "이미 사용중인 ID 혹은 Email 입니다.");
            return "profile_detail";
        } catch (Exception e) {
            e.printStackTrace();
            bindingResult.reject("signupFailed", e.getMessage());
            return "profile_detail";
        }
        return "redirect:/";
    }


}
