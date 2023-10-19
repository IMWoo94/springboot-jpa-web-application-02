package jpabook.jpashop.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceUnit;

import org.springframework.stereotype.Repository;

import jpabook.jpashop.domain.Member;

@Repository
public class MemberRepository {

	@PersistenceContext
	private EntityManager em;

	@PersistenceUnit
	private EntityManagerFactory emf;

	public Long save(Member member) {
		em.persist(member);
		return member.getId();
	}

	public Member find(Long id) {
		return em.find(Member.class, id);
	}

	public List<Member> findAll() {
		return em.createQuery("select m from Member m ", Member.class)
			.getResultList();
	}

	public List<Member> findByName(String name) {
		return em.createQuery("select m from Member m where m.username = :name ", Member.class)
			.setParameter("name", name)
			.getResultList();
	}
}
