package com.example.string_boot_4.comment;


import com.example.string_boot_4.answer.Answer;
import com.example.string_boot_4.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByAnswer(Answer answer, Pageable pageable);
}
