package jpabook.jpashop.service;

import static org.assertj.core.api.Assertions.*;

import java.util.List;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderSearch;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.domain.item.Book;
import jpabook.jpashop.exception.NotEnoughStockException;
import jpabook.jpashop.repository.OrderRepository;

@SpringBootTest
@Transactional
class OrderServiceTest {

	@Autowired
	EntityManager em;

	@Autowired
	public OrderRepository orderRepository;
	@Autowired
	public OrderService orderService;

	// 상품 주문
	@Test
	public void 상품주문() {
		// given
		Member member = createMember();

		Book book = createBook("시골 JPA", 10000, 10);

		int orderCount = 2;

		// when
		Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

		// then
		Order getOrder = orderRepository.findOne(orderId);

		Assertions.assertEquals(OrderStatus.ORDER, getOrder.getStatus(), "상품 주문시 상태는 ORDER");
		Assertions.assertEquals(1, getOrder.getOrderItems().size(), "주문한 상품 종류 수가 정확해야 한다.");
		Assertions.assertEquals(10000 * 2, getOrder.getTotalPrice(), "주문 가격은 가격 * 수량이다.");
		Assertions.assertEquals(8, book.getStockQuantity(), "주문 수량만큼 재고가 줄어야 한다.");
	}

	// 주문 취소
	@Test
	public void 주문취소() {
		Member member = createMember();
		Book book = createBook("시골 JPA", 10000, 10);

		int orderCount = 2;

		Long orderId = orderService.order(member.getId(), book.getId(), orderCount);

		orderService.cancelOrder(orderId);

		Order getOrder = orderRepository.findOne(orderId);

		Assertions.assertEquals(OrderStatus.CANCEL, getOrder.getStatus(), "상품 취소시 상태는 CANCEL");
		Assertions.assertEquals(10, book.getStockQuantity(), "주문이 취소된 상품은 그만큼 재고가 증가해야 한다.");

	}

	@Test
	public void 상품주문_재고수량초과() {
		Member member = createMember();
		Book book = createBook("시골 JPA", 10000, 10);

		int orderCount = 11;

		try {
			orderService.order(member.getId(), book.getId(), orderCount);
		} catch (NotEnoughStockException e) {
			return;
		}

		Assertions.assertThrows(NotEnoughStockException.class, () -> {
			orderService.order(member.getId(), book.getId(), orderCount);
		});

		// then
		fail("재고 수량 부족 예외가 발생 해야 한다.");
	}

	// 전체 조회
	@Test
	public void 전체_조회() {
		// given
		Member member = createMember();

		Book book1 = createBook("시골 JPA", 10000, 10);
		Book book2 = createBook("JPA", 10000, 10);

		int orderCount = 2;

		// when
		Long orderId1 = orderService.order(member.getId(), book1.getId(), orderCount);
		Long orderId2 = orderService.order(member.getId(), book2.getId(), orderCount);

		Order findOrder = orderRepository.findOne(orderId1);
		findOrder.cancel();

		// then
		OrderSearch orderSearch = new OrderSearch();
		OrderSearch orderSearch1 = new OrderSearch("회원1", OrderStatus.CANCEL);

		List<Order> ordersByString = orderRepository.findAllByString(orderSearch);
		List<Order> ordersByCriteria = orderRepository.findAllByCriteria(orderSearch);

		assertThat(ordersByString).isEqualTo(ordersByCriteria);

		List<Order> ordersByStringCondition = orderRepository.findAllByString(orderSearch1);
		List<Order> ordersByCriteriaCondition = orderRepository.findAllByCriteria(orderSearch1);

		assertThat(ordersByCriteriaCondition.size()).isEqualTo(1);
		assertThat(ordersByStringCondition.size()).isEqualTo(1);

	}

	private Book createBook(String name, int price, int stockQuantity) {
		Book book = new Book();
		book.setName(name);
		book.setPrice(price);
		book.setStockQuantity(stockQuantity);
		em.persist(book);
		return book;
	}

	private Member createMember() {
		Member member = new Member();
		member.setUsername("회원1");
		member.setAddress(new Address("서울", "경기", "123-123"));
		em.persist(member);
		return member;
	}

}