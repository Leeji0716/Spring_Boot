package com.example.string_boot_4.user;

import com.example.string_boot_4.answer.Answer;
import com.example.string_boot_4.answer.AnswerRepository;
import com.example.string_boot_4.comment.Comment;
import com.example.string_boot_4.comment.CommentRepository;
import com.example.string_boot_4.domain.DataNotFoundException;
import com.example.string_boot_4.question.Question;
import com.example.string_boot_4.question.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CommentRepository commentRepository;

    public SiteUser create(String name, String username, String email, String password){
        SiteUser user = new SiteUser();
        user.setName(name);
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        this.userRepository.save(user);
        return user;
    }

    public SiteUser getUser(String username) {
        Optional<SiteUser> siteUser = this.userRepository.findByusername(username);
        if (siteUser.isPresent()) {
            return siteUser.get();
        } else {
            throw new DataNotFoundException("siteuser not found");
        }
    }
    public List<Question> getQuestionList(SiteUser user){
        List<Question> questionList = this.questionRepository.findByAuthor(user);
        return questionList;
    }

    public List<Answer> getAnswerList(SiteUser user){
        List<Answer> answerList = this.answerRepository.findByAuthor(user);
        return answerList;
    }

    public List<Comment> getCommentList(SiteUser user){
        List<Comment> commentList = this.commentRepository.findByAuthor(user);
        return commentList;
    }

    public void passwordModify(SiteUser user, String temporaryPassword) {
        user.setPassword(passwordEncoder.encode(temporaryPassword));
        this.userRepository.save(user);
    }

    public void modify(SiteUser user, String username, String name, String password, String email, String profileImagePath){
        user.setUsername(username);
        user.setName(name);
        user.setPassword(passwordEncoder.encode(password));
        user.setEmail(email);
        user.setProfileImagePath(profileImagePath);

        this.userRepository.save(user);
    }
}
