package jpabook.jpashop.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import jpabook.jpashop.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {

	List<Member> findByUsername(String username);
}
