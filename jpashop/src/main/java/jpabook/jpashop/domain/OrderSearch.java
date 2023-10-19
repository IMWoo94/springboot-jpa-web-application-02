package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderSearch {

	private String memberName; //회원 이름
	private OrderStatus orderStatus;//주문 상태[ORDER, CANCEL]

	public OrderSearch() {
	}

	public OrderSearch(String memberName, OrderStatus orderStatus) {
		this.memberName = memberName;
		this.orderStatus = orderStatus;
	}
}
