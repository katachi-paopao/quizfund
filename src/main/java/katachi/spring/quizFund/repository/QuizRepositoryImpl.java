package katachi.spring.quizFund.repository;

import java.util.List;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import katachi.spring.quizFund.model.Quiz;

public class QuizRepositoryImpl implements QuizRepositoryCustom {

	@PersistenceContext
	private EntityManager em;

	@Override
	public Page<Quiz> findByUserPage(String uuid, String query, Pageable pageable) {
		TypedQuery<Long> countQuery = em.createQuery(
				"select count(distinct q.id) from Quiz q"
				+ (query == null ? "" : " join TypingAnswer a on q.id = a.key.id")
				+ " where q.user = :uuid"
				+ (query == null ? "" : " and (q.text LIKE :query or a.answer LIKE :query)")
				+ " order by q.id", Long.class
		).setParameter("uuid", uuid);

		if (query != null) countQuery.setParameter("query", "%" + query + "%");
		Long total = countQuery.getSingleResult();

		TypedQuery<Integer> query1 = em.createQuery(
				"select distinct q.id from Quiz q join TypingAnswer a on q.id = a.key.id"
				+ (query == null ? "" : " join TypingAnswer a on q.id = a.key.id")
				+ " where q.user = :uuid"
				+ (query == null ? "" : " and (q.text LIKE :query or a.answer LIKE :query)")
				+ " order by q.id ", Integer.class
		)
		.setParameter("uuid", uuid);

		if (query != null) query1.setParameter("query", "%" + query + "%");

		query1.setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
		.setMaxResults(pageable.getPageSize());
		List<Integer> ids = query1.getResultList();

		EntityGraph<?> graph = em.getEntityGraph("Quiz.all");
		TypedQuery<Quiz> query2 = em.createQuery(
				"select distinct q from Quiz q where q.id in (:ids) order by q.id", Quiz.class
		).setParameter("ids", ids)
		.setHint("javax.persistence.fetchgraph", graph);
		List<Quiz> res = query2.getResultList();
		return new PageImpl<Quiz>(res, pageable, total == null ? 0 : total);
	}

	@Override
	public List<Quiz> findByIdInCustom(List<Integer> idList) {
		String idListStr = "";
		for (int i=0 ,len=idList.size(); i<len; i++) {
			idListStr = idListStr + idList.get(i);
			if (i < len - 1) idListStr += ",";
		}

		EntityGraph<?> graph = em.getEntityGraph("Quiz.all");
		TypedQuery<Quiz> q_query = em.createQuery(
				"select distinct q from Quiz q"
				+ " where q.id in (:ids)"
				+ " order by FIND_IN_SET(q.id, :idListStr)"
				, Quiz.class
		)
		.setParameter("ids", idList)
		.setParameter("idListStr", idListStr)
		.setHint("javax.persistence.fetchgraph", graph);

		return q_query.getResultList();
	};
}
