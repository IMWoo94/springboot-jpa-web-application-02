package jpabook.jpashop.web;

import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

// @Configuration
public class WebConfig extends WebMvcConfigurationSupport {
	@Override
	protected void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(new LoingInterceptor()).order(2)
			.addPathPatterns("/**")
			.excludePathPatterns(
				"/", "/members/add", "/login", "/logout",
				"/css/**", "/*.ico", "/error"
			);
	}
}
