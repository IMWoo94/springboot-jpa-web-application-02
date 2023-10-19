package jpabook.jpashop.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class MemberService {

	private final MemberRepository memberRepository;

	/**
	 * 회원가입
	 */
	@Transactional
	public Long join(Member member) {
		validateDuplicateMember(member); // 중복 회원 검증
		return memberRepository.save(member);
	}

	private void validateDuplicateMember(Member member) {
		List<Member> findMembers = memberRepository.findByName(member.getUsername());
		if (!findMembers.isEmpty()) {
			throw new IllegalStateException("이미 존재 하는 회원 입니다.");
		}
	}

	/**
	 * 회원 전체 조회
	 */
	public List<Member> findMember() {
		return memberRepository.findAll();
	}

	public Member findOne(Long id) {
		return memberRepository.find(id);
	}

}
