package com.example.string_boot_4.user;

import com.example.string_boot_4.answer.Answer;
import com.example.string_boot_4.answer.AnswerRepository;
import com.example.string_boot_4.comment.Comment;
import com.example.string_boot_4.comment.CommentRepository;
import com.example.string_boot_4.domain.DataNotFoundException;
import com.example.string_boot_4.question.Question;
import com.example.string_boot_4.question.QuestionRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
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
    private final HttpSession session;

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

    public SiteUser createkakao(){
        System.out.println(session.getAttribute("nickname"));
        String nickname = (String) session.getAttribute("nickname");

        if (nickname != null) {
            SiteUser user = new SiteUser();
            user.setUsername(nickname);
            List<SiteUser> users = userRepository.findAll();
            long id = users.size();
            user.setEmail(id + "@sbb.com");
            String temporaryPassword = generateTemporaryPassword();
            user.setPassword(passwordEncoder.encode(temporaryPassword));
//            user.setPassword("1234");
            this.userRepository.save(user);
            return user;
        } else {
            // 세션에 필요한 데이터가 없는 경우 예외 처리 또는 다른 로직 수행
            // 예를 들어, 로그를 남기거나 사용자에게 알림을 보내는 등의 처리를 할 수 있습니다.
            return null;
        }
    }
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
        return siteUser.orElse(null);
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

    public void modify(SiteUser user, String username, String name, String email, String profileImagePath){
        user.setUsername(username);
        user.setName(name);
        user.setEmail(email);
        user.setProfileImagePath(profileImagePath);

        this.userRepository.save(user);
    }
}
