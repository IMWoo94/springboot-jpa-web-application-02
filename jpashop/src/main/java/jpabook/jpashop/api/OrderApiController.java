package jpabook.jpashop.api;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import lombok.Data;
import lombok.Getter;
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

	/**
	 * V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용X)
	 * - 트랜잭션 안에서 지연 로딩 필요
	 */
	@GetMapping("/api/v2/orders")
	public List<OrderDto> ordersV2() {
		List<Order> orders = orderRepository.findAllByCriteria(new OrderSearch());
		List<OrderDto> collect = orders.stream()
			.map(o -> new OrderDto(o))
			.collect(Collectors.toList());
		return collect;
	}

	/**
	 * V3. 엔티티를 조회해서 DTO로 변환(fetch join 사용O)
	 * 페이징 시에는 N 부분을 포기해야함(대신에 batch fetch size? 옵션 주면 N -> 1 쿼리로 변경가능)
	 */
	@GetMapping("/api/v3/orders")
	public List<OrderDto> ordersV3() {
		List<Order> orders = orderRepository.findAllWithItem();

		for (Order order : orders) {
			System.out.println("order ref =" + order + " id = " + order.getId());
		}
		List<OrderDto> collect = orders.stream()
			.map(o -> new OrderDto(o))
			.collect(Collectors.toList());
		return collect;
	}

	@Getter
	static class OrderDto {

		private Long orderId;
		private String name;
		private LocalDateTime orderDate;
		private OrderStatus orderStatus;
		private Address address;
		private List<OrderItemDto> orderItems;

		public OrderDto(Order order) {
			this.orderId = order.getId();
			this.name = order.getMember().getUsername();
			this.orderDate = order.getOrderDate();
			this.orderStatus = order.getStatus();
			this.address = order.getDelivery().getAddress();
			// order.getOrderItems().stream().forEach(o -> o.getItem().getName());
			// this.orderItems = order.getOrderItems();
			this.orderItems = order.getOrderItems().stream()
				.map(orderItem -> new OrderItemDto(orderItem))
				.collect(Collectors.toList());
		}
	}

	@Data
	static class OrderItemDto {
		private String itemName;
		private int orderPrice;
		private int count;

		public OrderItemDto(OrderItem orderItem) {
			this.itemName = orderItem.getItem().getName();
			this.orderPrice = orderItem.getOrderPrice();
			this.count = orderItem.getCount();
		}
	}
}
