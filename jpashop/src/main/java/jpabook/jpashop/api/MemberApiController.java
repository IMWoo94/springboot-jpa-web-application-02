package jpabook.jpashop.api;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class MemberApiController {

	private final MemberService memberService;

	/**
	 * 등록 V1: 요청 값으로 Member 엔티티를 직접 받는다.
	 * 문제점 - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
	 *      - 엔티티에 API 검증을 위한 로직이 들어간다. (@NotEmpty 등등)
	 *      - 실무에서는 회원 엔티티를 위한 API가 다양하게 만들어지는데, 한 엔티티에 각각의 API를 위 한 모든 요청 요구사항을 담기는 어렵다.
	 *      - 엔티티가 변경되면 API 스펙이 변한다.
	 *  결론 - API 요청 스펙에 맞추어 별도의 DTO를 파라미터로 받는다.
	 */
	@PostMapping("/api/v1/members")
	public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
		Long id = memberService.join(member);
		return new CreateMemberResponse(id);
	}

	/**
	 * 등록 V2: 요청 값으로 Member 엔티티 대신에 별도의 DTO를 받는다.
	 */
	@PostMapping("/api/v2/members")
	public CreateMemberResponse saveMembeV2(@RequestBody @Valid CreateMemberRequest request) {

		Member member = new Member();
		member.setUsername(request.getName());

		Long id = memberService.join(member);
		return new CreateMemberResponse(id);
	}

	@PutMapping("/api/v2/members/{id}")
	public UpdateMemberResponse updateMemberV2(
		@PathVariable("id") Long id,
		@RequestBody @Valid UpdateMemberRequest request
	) {
		memberService.update(id, request.getName());
		Member findMember = memberService.findOne(id);
		return new UpdateMemberResponse(findMember.getId(), findMember.getUsername());
	}

	@Data
	static class UpdateMemberResponse {
		private Long id;
		private String name;

		public UpdateMemberResponse(Long id, String name) {
			this.id = id;
			this.name = name;
		}
	}

	@Data
	static class UpdateMemberRequest {
		private String name;
	}

	@Data
	static class CreateMemberRequest {
		private String name;
	}

	@Data
	static class CreateMemberResponse {
		private Long id;

		public CreateMemberResponse(Long id) {
			this.id = id;
		}
	}
}
