package jpabook.jpashop.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;

@SpringBootTest
@Transactional
class MemberServiceTest {

	@Autowired
	MemberService memberService;
	@Autowired
	MemberRepository memberRepository;

	@Test
	public void 회원가입() {
		// given
		Member member = new Member();
		member.setUsername("kim");

		// when
		Long saveId = memberService.join(member);

		// then
		assertThat(member).isEqualTo(memberService.findOne(saveId));

	}

	@Test
	public void 중복_회원_예외() {
		// given
		Member member1 = new Member();
		member1.setUsername("kim");
		Member member2 = new Member();
		member2.setUsername("kim");

		// when
		memberService.join(member1);

		try {
			memberService.join(member2); // 예외가 발생한다.
		} catch (IllegalStateException e) {
			return;
		}

		Assertions.assertThrows(IllegalStateException.class, () -> {
			memberService.join(member2);
		});

		// then
		fail("예외가 발생해야 한다.");

	}

}