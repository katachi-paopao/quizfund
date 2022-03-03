package katachi.spring.quizFund.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import katachi.spring.quizFund.model.TypingOption;

@Repository
public interface TypingOptionRepository extends JpaRepository<TypingOption, Integer> {
	void deleteAllById(int id);
}
