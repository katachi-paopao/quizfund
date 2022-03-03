package katachi.spring.quizFund.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import katachi.spring.quizFund.model.IntermediateQuizAndQuizset;
import katachi.spring.quizFund.model.IntermediateQuizAndQuizsetPK;

public interface IntermediateQuizAndQuizsetRepository
extends JpaRepository<IntermediateQuizAndQuizset,
IntermediateQuizAndQuizsetPK>, IntermediateQuizAndQuizsetRepositoryCustom {
	void deleteByKey(IntermediateQuizAndQuizsetPK key);
	void deleteAllByKeyQuizsetId(int qsid);
	List<IntermediateQuizAndQuizset> findByKeyQuizsetId(int qsid);
	Long countByKeyQuizsetId(int qsid);
}
