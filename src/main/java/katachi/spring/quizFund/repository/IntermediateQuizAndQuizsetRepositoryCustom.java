package katachi.spring.quizFund.repository;

import java.util.List;

public interface IntermediateQuizAndQuizsetRepositoryCustom {
	void deleteAllByKeyQuizsetIdCustom(int qsid);
	List<Integer> findQuizIdByKeyQuizsetIdCustom(int qsid, boolean isRandom,  int getNum);
}
