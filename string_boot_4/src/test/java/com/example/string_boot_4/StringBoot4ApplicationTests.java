package com.example.string_boot_4;

import com.example.string_boot_4.answer.Answer;
import com.example.string_boot_4.answer.AnswerRepository;
import com.example.string_boot_4.answer.AnswerService;
import com.example.string_boot_4.category.Category;
import com.example.string_boot_4.category.CategoryRepository;
import com.example.string_boot_4.category.CategoryService;
import com.example.string_boot_4.question.Question;
import com.example.string_boot_4.question.QuestionRepository;
import com.example.string_boot_4.question.QuestionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class StringBoot4ApplicationTests {

	@Autowired
	private QuestionRepository questionRepository;

	@Test
	void testJpa() {
		Question q1 = new Question();
		q1.setSubject("sbb가 무엇인가요?");
		q1.setContent("sbb에 대해서 알고 싶습니다.");
		q1.setCreateDate(LocalDateTime.now());
		this.questionRepository.save(q1);  // 첫번째 질문 저장

		Question q2 = new Question();
		q2.setSubject("스프링부트 모델 질문입니다.");
		q2.setContent("id는 자동으로 생성되나요?");
		q2.setCreateDate(LocalDateTime.now());
		this.questionRepository.save(q2);  // 두번째 질문 저장
	}

	@Test
	void test1(){
		List<Question> all = this.questionRepository.findAll();
		assertEquals(2, all.size());

		Question q = all.get(0);
		assertEquals("sbb가 무엇인가요?", q.getSubject());
	}
	@Autowired
	private AnswerRepository answerRepository;

	@Test
	void test2() {
		Optional<Question> oq = this.questionRepository.findById(2);
		assertTrue(oq.isPresent());
		Question q = oq.get();

		Answer a = new Answer();
		a.setContent("네 자동으로 생성됩니다.");
		a.setQuestion(q);  // 어떤 질문의 답변인지 알기위해서 Question 객체가 필요하다.
		a.setCreateDate(LocalDateTime.now());
		this.answerRepository.save(a);
	}
	@Autowired
	private QuestionService questionService;

	@Test
	void test3() {
		Question question = new Question();
		for (int i = 1; i <= 300; i++) {
			String subject = String.format("질문답변 테스트 데이터입니다:[%03d]", i);
			String content = "내용무";
			Category category = categoryRepository.findById(2);
			this.questionService.create(subject, content, null, category);
		}
	}
	@Test
	void test8() {
		for (int i = 1; i <= 200; i++) {
			String subject = String.format("자유게시판 테스트 데이터입니다:[%03d]", i);
			String content = "내용무";
			Category category = categoryRepository.findById(1);
			Question question = new Question();
			question.setSubject(subject);
			question.setContent(content);
			question.setCategory(category); // 카테고리 설정
			questionRepository.save(question);
		}
	}

	@Autowired
	private AnswerService answerService;
	@Test
	void test4() {
		for (int i = 1; i <= 100; i++) {
			String content = String.format("답변 페이징 테스트 데이터입니다:[%03d]", i);
			Question q = questionService.getQuestion(501);
			this.answerService.create(q, content, null);
		}
	}

	@Autowired
	private CategoryRepository categoryRepository;
	@Test
	void test5(){
		Category category = new Category();
		category.setBoard("강좌");
		this.categoryRepository.save(category);
	}
}
