package com.example.string_boot_4.question;

import com.example.string_boot_4.category.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Integer> {
    List<Category> findByCategory(String board);
    Category findCategory(String board);
    Question findBySubject(String subject);
    Question findBySubjectAndContent(String subject, String content);
    List<Question> findBySubjectLike(String subject);
    Page<Question> findAll(Pageable pageable);
    Page<Question> findAllByCategory(Pageable pageable, String board);
    Page<Question> findAll(Specification<Question> spec, Pageable pageable);

    @Query("select distinct q "
            + "from Question q "
            + "left join fetch q.author u1 "
            + "left join fetch q.answerList a "
            + "left join fetch a.author u2 "
            + "where q.category = :category "
            + "and (q.subject like %:kw% "
            + "or q.content like %:kw% "
            + "or u1.username like %:kw% "
            + "or a.content like %:kw% "
            + "or u2.username like %:kw%)")
    Page<Question> findAllByCategoryAndKeyword(@Param("category") Category category, @Param("kw") String kw, Pageable pageable);
}
