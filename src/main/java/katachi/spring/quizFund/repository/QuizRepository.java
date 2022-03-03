package katachi.spring.quizFund.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import katachi.spring.quizFund.model.Quiz;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Integer>, QuizRepositoryCustom {
	@EntityGraph(value = "Quiz.all")
	Optional<Quiz> findById(int id);
	@EntityGraph(value = "Quiz.all")
	List<Quiz> findByUser(@Param("uid") String uuid);
	List<Quiz> findAll();
	long count();
	long countByUser(@Param("uid") String uuid);
 }