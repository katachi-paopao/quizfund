package katachi.spring.quizFund.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import katachi.spring.quizFund.model.Quiz;
import katachi.spring.quizFund.model.Quizset;
import katachi.spring.quizFund.model.QuizsetSummary;

public interface QuizsetRepositoryCustom {
	Optional<Quizset> findByIdCustom(@Param("id") int id);
	Page<Quiz> createQuizsetPage(String uuid, @Param("id") int id, List<Integer> idList, String query, Pageable pageable);
	Map<String, Object> findAllQuizIdByQuizsetId(@Param("id") int id);
	Page<QuizsetSummary> createQuizsetIndexPage(@Param("uid") String uid, String query, Pageable pageable);
	Page<QuizsetSummary> createGameIndexPage(String query, Pageable pageable);
}
