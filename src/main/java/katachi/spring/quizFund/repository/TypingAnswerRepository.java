package katachi.spring.quizFund.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import katachi.spring.quizFund.model.TypingAnswer;
import katachi.spring.quizFund.model.TypingAnswerPK;

public interface TypingAnswerRepository extends JpaRepository<TypingAnswer, TypingAnswerPK> {
	List<TypingAnswer> findAllByKeyId(int id);
	void deleteAllByKeyId(int id);
	void deleteByKeyIdAndKeySerial(int id, int serial);
}