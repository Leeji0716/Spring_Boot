package com.example.string_boot_4.answer;

import com.example.string_boot_4.comment.Comment;
import com.example.string_boot_4.question.Question;
import com.example.string_boot_4.user.SiteUser;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
public class Answer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createDate;

    @ManyToOne
    private Question question;

    @ManyToOne
    private SiteUser author;

    private LocalDateTime modifyDate;

    @ManyToMany
    Set<SiteUser> voter;

    @Column(nullable = false)
    private int voteCount; // 투표한 사용자 수를 나타내는 필드

    @OneToMany(mappedBy = "answer")
    private List<Comment> commentList;
}
