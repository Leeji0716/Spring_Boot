package com.example.string_boot_4.answer;

import com.example.string_boot_4.question.Question;
import com.example.string_boot_4.user.SiteUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerRepository extends JpaRepository<Answer, Integer> {
    Page<Answer> findByQuestion(Question question, Pageable pageable);
    List<Answer> findByAuthor(SiteUser author);
}
