package com.example.string_boot_4.comment;

import com.example.string_boot_4.answer.Answer;
import com.example.string_boot_4.answer.AnswerForm;
import com.example.string_boot_4.answer.AnswerService;
import com.example.string_boot_4.question.Question;
import com.example.string_boot_4.question.QuestionService;
import com.example.string_boot_4.user.SiteUser;
import com.example.string_boot_4.user.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

@RequestMapping("/comment")
@RequiredArgsConstructor
@Controller
public class CommentController {
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;
    private final CommentService commentService;

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/create")
    public String createComment(CommentForm commentForm) {
        return "comment_form";
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/create/{id}")
    public String createComment(Model model, @PathVariable("id") Integer id,
                               @Valid CommentForm commentForm , BindingResult bindingResult, Principal principal) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("commentForm", commentForm);
            return "comment_form";
        }
        SiteUser siteUser = this.userService.getUser(principal.getName());
        if (isQuestion(id)) {
            Question question = questionService.getQuestion(id);
            Comment comment = this.commentService.create((Question) question, commentForm.getContent(), siteUser);
            return String.format("redirect:/question/comment/%s", comment.getQuestion().getId());
        } else {
            Answer answer = answerService.getAnswer(id);
            Comment comment = this.commentService.create((Answer) answer, commentForm.getContent(), siteUser);
            return String.format("redirect:/answer/comment/%s", comment.getAnswer().getId());
        }
    }
    private boolean isQuestion(Integer parentId) {
        Question question = questionService.getQuestion(parentId);
        return question != null && question.getAnswerList() == null;
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/modify/{id}")
    public String commentModify(CommentForm commentForm, @PathVariable("id") Long id, Principal principal) {
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        commentForm.setContent(comment.getContent());
        return "comment_form";
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("/modify/{id}")
    public String commentModify(@Valid CommentForm commentForm, BindingResult bindingResult,
                               @PathVariable("id") Long id, Principal principal) {
        if (bindingResult.hasErrors()) {
            return "comment_form";
        }
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다.");
        }
        this.commentService.modify(comment, commentForm.getContent());
        return String.format("redirect:/answer/comment/%s", comment.getAnswer().getId());
    }

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/delete/{id}")
    public String commentDelete(Principal principal, @PathVariable("id") Long id) {
        Comment comment = this.commentService.getComment(id);
        if (!comment.getAuthor().getUsername().equals(principal.getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다.");
        }
        this.commentService.delete(comment);
        return String.format("redirect:/answer/comment/%s", comment.getAnswer().getId());
    }
}
