package com.example.string_boot_4.question;

import com.example.string_boot_4.answer.AnswerRepository;
import com.example.string_boot_4.category.Category;
import com.example.string_boot_4.category.CategoryRepository;
import com.example.string_boot_4.domain.DataNotFoundException;
import com.example.string_boot_4.answer.Answer;
import com.example.string_boot_4.user.SiteUser;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final CategoryRepository categoryRepository;

    public Pageable sortDate(int page){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("createDate"));
        return PageRequest.of(page, 10, Sort.by(sorts));
    }

    public Pageable sortHit(int page){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("hit"));
        return PageRequest.of(page, 10, Sort.by(sorts));
    }

    public Pageable sortVote(int page){
        List<Sort.Order> sorts = new ArrayList<>();
        sorts.add(Sort.Order.desc("voteCount"));
        return PageRequest.of(page, 10, Sort.by(sorts));
    }

    public Page<Question> getList(int page, String kw, String sort) {
        Pageable pageable = null;
        if (sort.equals("Date")){
            pageable = sortDate(page);
        }
        else if (sort.equals("Hit")){
            pageable = sortHit(page);
        }else if (sort.equals("Vote")){
            pageable = sortVote(page);
        }
        return this.questionRepository.findAllByKeyword(kw, pageable);
    }

    public Page<Answer> getAnswersForQuestion(Question question, int page, String sort) {
        Pageable pageable = null;
        if (sort.equals("Date")){
            pageable = sortDate(page);
        }else if (sort.equals("Vote")){
            pageable = sortVote(page);
        }
        return answerRepository.findByQuestion(question, pageable);
    }

    public Question getQuestion(Integer id) {
        Optional<Question> question = this.questionRepository.findById(id);
        if (question.isPresent()) { //질문이 존재함
            return question.get();
        } else {
            throw new DataNotFoundException("question not found");
        }
    }

    public void create(String subject, String content, SiteUser user){
        Question question = new Question();
        question.setSubject(subject);
        question.setContent(content);
        question.setCreateDate(LocalDateTime.now());
        question.setAuthor(user);
        this.questionRepository.save(question);
    }

    public void modify(Question question, String subject, String content) {
        question.setSubject(subject);
        question.setContent(content);
        question.setModifyDate(LocalDateTime.now());
        this.questionRepository.save(question);
    }

    public void delete(Question question) {
        this.questionRepository.delete(question);
    }

    public void vote(Question question, SiteUser siteUser){
        question.getVoter().add(siteUser);
        int vote = question.getVoter().size();
        question.setVoteCount(vote);
        this.questionRepository.save(question);
    }

    private Specification<Question> search(String kw) {
        return new Specification<>() {
            private static final long serialVersionUID = 1L;
            @Override
            public Predicate toPredicate(Root<Question> q, CriteriaQuery<?> query, CriteriaBuilder cb) {
                query.distinct(true);  // 중복을 제거
                Join<Question, SiteUser> u1 = q.join("author", JoinType.LEFT);
                Join<Question, Answer> a = q.join("answerList", JoinType.LEFT);
                Join<Answer, SiteUser> u2 = a.join("author", JoinType.LEFT);
                return cb.or(cb.like(q.get("subject"), "%" + kw + "%"), // 제목
                        cb.like(q.get("content"), "%" + kw + "%"),      // 내용
                        cb.like(u1.get("username"), "%" + kw + "%"),    // 질문 작성자
                        cb.like(a.get("content"), "%" + kw + "%"),      // 답변 내용
                        cb.like(u2.get("username"), "%" + kw + "%"));   // 답변 작성자
            }
        };
    }
    public void hitPlus(Question question){
        int hit = question.getHit() + 1;
        question.setHit(hit);
        this.questionRepository.save(question);
    }

}
