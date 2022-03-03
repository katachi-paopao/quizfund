package katachi.spring.quizFund.repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class IntermediateQuizAndQuizsetRepositoryImpl
implements IntermediateQuizAndQuizsetRepositoryCustom {

	@PersistenceContext
	private EntityManager em;

	@Override
	public void deleteAllByKeyQuizsetIdCustom(int qsid) {
		em.createQuery("delete from IntermediateQuizAndQuizset inter"
				+ " where inter.key.quizsetId = :qsid")
		.setParameter("qsid", qsid)
		.executeUpdate();
	}

	@Override
	public List<Integer> findQuizIdByKeyQuizsetIdCustom(int qsid, boolean isRandom, int getNum) {
		List<Integer> fullIdList = em.createQuery("select distinct inter.key.quizId from IntermediateQuizAndQuizset inter"
				+ " where inter.key.quizsetId = :qsid"
				+ " order by serial", Integer.class)
		.setParameter("qsid", qsid)
		.getResultList();

		if (isRandom) Collections.shuffle(fullIdList);

		List<Integer> res = new ArrayList<Integer>();
		for (int i = 0, len = getNum < fullIdList.size() ? getNum : fullIdList.size(); i < len ; i++) {
			res.add(fullIdList.get(i));
		}

		return res;
	}

}
