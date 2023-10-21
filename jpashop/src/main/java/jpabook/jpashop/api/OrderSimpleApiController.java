package jpabook.jpashop.api;

import static java.util.stream.Collectors.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRespositroy;
import lombok.Data;
import lombok.RequiredArgsConstructor;

/**
 * XToOne ( ManyToOne, OenToOne )
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
public class OrderSimpleApiController {

	private final OrderRepository orderRepository;
	private final OrderSimpleQueryRespositroy orderSimpleQueryRespositroy;

	/**
	 * V1. 엔티티 직접 노출
	 * - Hibernate5Module 모듈 등록, LAZY=null 처리
	 * 연관관계 지연로딩의 경우 실제 객체가 아닌 프록시 객체가 들어 있고 사용이 되어야만 프록시 초기화가 되어 값이 들어 있다.
	 * 이로 인해서 연관관계에 대해서 값을 Json 으로 던지게 되면 InvalidDefinitionException 예외가 발생한다.
	 * - 양방향 관계 문제 발생 -> @JsonIgnore
	 * 일반적으로 양뱡향 관계에서 서로 타고타고 들어가서 무한 루프가 발생하게 된다.
	 * 이를 해결하기 위해서 값을 보낼때 한쪽 방향은 보내지 않도록 명시할 것
	 *
	 * 사실 엔티티를 직접 노출 시키는 경우가 없기 때문에 큰 걱정을 하지 않아도 되지만 왜 이런 문제가 발생할 수 있다는 걸 알고 있도록 하자
	 */
	@GetMapping("/api/v1/simple-orders")
	public List<Order> orderV1() {
		List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
		for (Order order : all) {
			order.getMember().getUsername(); // Lazy 강제 초기화
			order.getDelivery().getAddress(); // Lazy 강제 초기화
		}
		return all;
	}

	@GetMapping("/api/v2/simple-orders")
	public List<SimpleOrderDto> ordersV2() {
		// order 2개 조회
		// N + 1 -> 1 + 회원 N + 배송 N
		// List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
		// 각각의 order 마다 member, delivery 조회 추기
		// List<SimpleOrderDto> collect = orders.stream().map(o -> new SimpleOrderDto(o)).collect(Collectors.toList());
		// return collect;
		return orderRepository.findAllByCriteria(new OrderSearch()).stream()
			.map(SimpleOrderDto::new)
			.collect(toList());
	}

	@GetMapping("/api/v3/simple-orders")
	public List<SimpleOrderDto> ordersV3() {
		List<Order> orders = orderRepository.findAllWithMemberDelivery();
		List<SimpleOrderDto> collect = orders.stream().map(o -> new SimpleOrderDto(o)).collect(Collectors.toList());
		return collect;
	}

	@GetMapping("/api/v4/simple-orders")
	public List<OrderSimpleQueryDto> ordersV4() {
		return orderSimpleQueryRespositroy.findOrderDtos();
	}

	@Data
	static class SimpleOrderDto {
		private Long orderId;
		private String name;
		private LocalDateTime orderDate;
		private OrderStatus orderStatus;
		private Address address;

		public SimpleOrderDto(Order order) {
			orderId = order.getId();
			System.out.println("===================");
			name = order.getMember().getUsername(); // Lazy 초기화
			orderDate = order.getOrderDate();
			orderStatus = order.getStatus();
			System.out.println("===================");
			address = order.getDelivery().getAddress();  // Lazy 초기화

		}
	}

}
