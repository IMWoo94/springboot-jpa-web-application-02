package jpabook.jpashop;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelloController {

	@GetMapping("hello")
	public String hello(Model model) {
		model.addAttribute("data", "hello!!");
		/*
		일반 Controller 에서 String 으로 return
		하면 Dispatcher Servlet 에서 viewResolve 에 던져서 관련 view 를 찾아 옵니다.
		 */
		return "hello";
	}

}
