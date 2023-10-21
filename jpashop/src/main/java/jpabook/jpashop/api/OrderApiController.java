package jpabook.jpashop.api;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.repository.OrderRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class OrderApiController {
	private final OrderRepository orderRepository;

	/**
	 * V1 엔티티 직접 노출
	 * - 엔티티가 변하며 API 스펙도 변한다.
	 * - 트랜잭션 안에서 지연 로딩 필요
	 * - 양뱡향 연관관계 문제 -> @JsonIgnore
	 * Hibernate5Module 모듈 등록, LAZY=null 처리
	 */
	@GetMapping("/api/v1/orders")
	public List<Order> ordersV1() {
		List<Order> all = orderRepository.findAllByCriteria(new OrderSearch());
		for (Order order : all) {
			order.getMember().getUsername();
			order.getDelivery().getAddress();
			List<OrderItem> orderItems = order.getOrderItems();
			// for (OrderItem orderItem : orderItems) {
			// 	orderItem.getItem().getName();
			//
			// }
			orderItems.stream().forEach(o -> o.getItem().getName());
		}
		return all;
	}
}
