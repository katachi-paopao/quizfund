package katachi.spring.quizFund.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.persistence.EntityGraph;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import katachi.spring.quizFund.model.Quiz;
import katachi.spring.quizFund.model.Quizset;
import katachi.spring.quizFund.model.QuizsetSummary;

public class QuizsetRepositoryImpl implements QuizsetRepositoryCustom{

	@PersistenceContext
	private EntityManager em;
	@Override
	public Optional<Quizset> findByIdCustom(@Param("id") int id) {
		TypedQuery<Quizset> qs_query = em.createQuery(
				"select distinct qs from Quizset qs left outer join fetch qs.quizList where qs.id = :id", Quizset.class
		).setParameter("id", id);

		try {
			Quizset qs = qs_query.getSingleResult();

			List<Integer> ids= new ArrayList<Integer>();
			for (Quiz q : qs.getQuizList()) ids.add(q.getId());

			TypedQuery<Quiz> q_query = em.createQuery(
					"select q from Quiz q where q.id in (:ids)", Quiz.class
			).setParameter("ids", ids)
			.setHint("javax.persistence.fetchgraph", em.getEntityGraph("Quiz.all"));
			List<Quiz> list = q_query.getResultList();

			qs.setQuizList(list);

			return Optional.of(qs);
		} catch (NoResultException e) {
			return Optional.empty();
		}
	}

	@Override
	public Page<Quiz> createQuizsetPage(String uuid, @Param("id") int qsid, List<Integer> idList, String query, Pageable pageable) {

		/* リストの総数を求める */
		long total = 0;
		if (idList != null) {
			TypedQuery<Long> countQuery = em.createQuery(
					"select count(distinct q.id) from Quiz q"
					+ " join TypingAnswer a on q.id = a.key.id"
					+ " where q.user = :uuid"
					+ " and q.id in :idList"
					+ (query != null ? " and (q.text LIKE :query or a.answer LIKE :query)" : ""), Long.class
			)
			.setParameter("uuid", uuid)
			.setParameter("idList", idList);
			if (query != null) countQuery.setParameter("query", "%" + query + "%");
			total = countQuery.getSingleResult();
		}
		else {
			TypedQuery<Long> countQuery = em.createQuery(
					"select count(distinct inter.key.quizId) from IntermediateQuizAndQuizset inter where inter.key.quizsetId = :id"
					, Long.class
			).setParameter("id", qsid);
			total = countQuery.getSingleResult();
		}

		/* 取得するページのIDリストを求める */
		List<Integer> fetchIdList = new ArrayList<Integer>();

		if (idList != null) {
			String idListStr = "";
			for (int i=0 ,len=idList.size(); i<len; i++) {
				idListStr = idListStr + idList.get(i);
				if (i < len - 1) idListStr += ",";
			}

			TypedQuery<Integer> qid_query = em.createQuery(
					"select distinct q.id from Quiz q"
					+ " join TypingAnswer a on q.id = a.key.id"
					+ " where q.user = :uuid"
					+ " and q.id in :idList"
					+ (query != null ? " and (q.text LIKE :query or a.answer LIKE :query)" : "")
					+ " order by FIND_IN_SET(q.id, :idListStr)", Integer.class
			)
			.setParameter("uuid", uuid)
			.setParameter("idList", idList)
			.setParameter("idListStr", idListStr)
			.setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
			.setMaxResults(pageable.getPageSize());

			if (query != null) qid_query.setParameter("query", "%" + query + "%");

			fetchIdList = qid_query.getResultList();
		} else {
			TypedQuery<Integer> qid_query = em.createQuery(
					"select distinct inter.key.quizId from IntermediateQuizAndQuizset inter where inter.key.quizsetId = :qsid"
					+ (query == null ? "" : " and q.text LIKE :query or a.answer LIKE :query")
					+ " order by inter.serial", Integer.class
			).setParameter("qsid", qsid)
			.setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
			.setMaxResults(pageable.getPageSize());

			fetchIdList = qid_query.getResultList();
		}

		/* 取得するページの要素を取得する */
		String partialIdListStr= "";
		if (idList != null) {
			for (int i=0 ,len=fetchIdList.size(); i<len; i++) {
				partialIdListStr = partialIdListStr + fetchIdList.get(i);
				if (i < len - 1) partialIdListStr += ",";
			}
		}

		EntityGraph<?> graph = em.getEntityGraph("Quiz.all");
		TypedQuery<Quiz> q_query = em.createQuery(
				"select distinct q from Quiz q"
				+ " left outer join IntermediateQuizAndQuizset inter on q.id = inter.key.quizId"
				+ " where q.id in (:ids)"
				+ (idList == null ? " order by inter.serial" : " order by FIND_IN_SET(q.id, :partialIdListStr)")
				, Quiz.class
		)
		.setParameter("ids", fetchIdList)
		.setHint("javax.persistence.fetchgraph", graph);

		if (idList != null) q_query.setParameter("partialIdListStr", partialIdListStr);

		List<Quiz> quizList = q_query.getResultList();

		List<Integer> serialList = new ArrayList<Integer>();;
		if (idList == null) {
			TypedQuery<Integer> serialQuery = em.createQuery(
					"select distinct inter.serial from IntermediateQuizAndQuizset inter"
					+ " where inter.key.quizId in (:ids) and inter.key.quizsetId = :qsid"
					+ " order by inter.serial", Integer.class
			).setParameter("qsid", qsid)
			.setParameter("ids", fetchIdList);

			serialList = serialQuery.getResultList();
		} else {
			for (int id : fetchIdList) {
				serialList.add(idList.indexOf(id));
			}
		}

		for (int i = 0; i < serialList.size(); i++) {
			quizList.get(i).setSerial(serialList.get(i));
		}

		return new PageImpl<Quiz>(quizList, pageable, total);
	}

	@Override
	public Map<String, Object> findAllQuizIdByQuizsetId(@Param("id") int qsid) {
		TypedQuery<Integer> qs_query = em.createQuery(
				"select distinct inter.key.quizId from IntermediateQuizAndQuizset inter"
				+ " where inter.key.quizsetId = :qsid order by inter.serial", Integer.class
		).setParameter("qsid", qsid);

		List<Integer> idList = qs_query.getResultList();

		Object config = em.createQuery("select qs.name, qs.introduction, qs.isRandom, qs.num from Quizset qs"
				+ " where qs.id = :qsid").setParameter("qsid", qsid).getSingleResult();

		Map<String, Object> res = new HashMap<String, Object>();
		res.put("idList", idList);
		res.put("config", config);

		return res;
	}

	@Override
	public Page<QuizsetSummary> createQuizsetIndexPage(String uid, String query, Pageable pageable) {
		TypedQuery<Long> countQuery = em.createQuery(
				"select count(distinct qs) from Quizset qs where qs.user = :uid"
				+ (query == null ? "" : " and (qs.name LIKE :query or qs.introduction LIKE :query)")
				, Long.class
		).setParameter("uid", uid);

		if (query != null) countQuery.setParameter("query", "%" + query + "%");
		Long total = countQuery.getSingleResult();

		TypedQuery<Integer> qsidQuery = em.createQuery(
				"select distinct qs.id from Quizset qs where qs.user = :uid"
				+ (query == null ? "" : " and (qs.name LIKE :query or qs.introduction LIKE :query)"), Integer.class
		).setParameter("uid", uid)
		.setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
		.setMaxResults(pageable.getPageSize());

		if (query != null) qsidQuery.setParameter("query", "%" + query + "%");
		List<Integer> qsidList = qsidQuery.getResultList();

		EntityGraph<?> graph = em.getEntityGraph("Quizset.summary");

		TypedQuery<Quizset> qs_query = em.createQuery(
				"select qs from Quizset qs where qs.id in :qsid", Quizset.class
		).setParameter("qsid", qsidList)
		.setHint("javax.persistence.fetchgraph", graph);

		List<Quizset> quizsetList = qs_query.getResultList();
		List<QuizsetSummary> quizsetSummaryList = new ArrayList<QuizsetSummary>();
		for (Quizset qs : quizsetList) {
			QuizsetSummary qss = new QuizsetSummary(
					qs.getId(), qs.getName(), qs.getIntroduction(),
					qs.isOpen(), qs.isRandom(),
					qs.getQuizList().size(), qs.getNum(),
					qs.getPlayCount(), null
			);
			quizsetSummaryList.add(qss);
		}

		return new PageImpl<QuizsetSummary>(quizsetSummaryList, pageable, total);
	}

	@Override
	public Page<QuizsetSummary> createGameIndexPage(String query, Pageable pageable) {
		TypedQuery<Long> countQuery= em.createQuery(
				"select count(distinct qs) from Quizset qs where qs.isOpen = true"
				+ (query == null ? "" : " and (qs.name LIKE :query or qs.introduction LIKE :query)")
				, Long.class
		);
		if (query != null) countQuery.setParameter("query", "%" + query + "%");
		Long total = countQuery.getSingleResult();

		TypedQuery<Integer> qsidQuery = em.createQuery(
				"select distinct qs.id from Quizset qs where qs.isOpen = true"
				+ (query == null ? "" : " and (qs.name LIKE :query or qs.introduction LIKE :query)")
				, Integer.class
		).setFirstResult(pageable.getPageNumber() * pageable.getPageSize())
		.setMaxResults(pageable.getPageSize());
		if (query != null) qsidQuery.setParameter("query", "%" + query + "%");
		List<Integer> qsidList = qsidQuery.getResultList();

		EntityGraph<?> graph = em.getEntityGraph("Quizset.summary");

		TypedQuery<Quizset> qs_query = em.createQuery(
				"select qs from Quizset qs where qs.id in :qsid", Quizset.class
		).setParameter("qsid", qsidList)
		.setHint("javax.persistence.fetchgraph", graph);

		List<Quizset> quizsetList = qs_query.getResultList();

		TypedQuery<String> u_query = em.createQuery(
				"select u.name from User u"
				+ " join Quizset qs on u.uuid = qs.user"
				+ " where qs.id in :qsid", String.class
		).setParameter("qsid", qsidList);

		List<String> userList = u_query.getResultList();
		List<QuizsetSummary> quizsetSummaryList = new ArrayList<QuizsetSummary>();

		Iterator<Quizset> it = quizsetList.iterator();
		Iterator<String> u_it = userList.iterator();
		while (it.hasNext()) {
			Quizset qs = it.next();
			QuizsetSummary qss = new QuizsetSummary(
					qs.getId(), qs.getName(), qs.getIntroduction(),
					qs.isOpen(), qs.isRandom(),
					qs.getQuizList().size(), qs.getNum(),
					qs.getPlayCount(), u_it.next()
			);
			quizsetSummaryList.add(qss);
		}

		return new PageImpl<QuizsetSummary>(quizsetSummaryList, pageable, total);
	}
}
