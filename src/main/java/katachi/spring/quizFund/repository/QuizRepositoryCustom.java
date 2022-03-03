package katachi.spring.quizFund.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import katachi.spring.quizFund.model.Quiz;

public interface QuizRepositoryCustom {
	Page<Quiz> findByUserPage(@Param("uid") String uuid, @Param("uid") String query, Pageable pageable);
	List<Quiz> findByIdInCustom(List<Integer> idList);
}
