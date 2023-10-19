package jpabook.jpashop.web;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.util.PatternMatchUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
// @Component
public class LoginFilter implements Filter {

	private static final String[] whiteList = {"/", "/members/*", "/css/**"};

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws
		IOException,
		ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest)request;
		HttpServletResponse httpResponse = (HttpServletResponse)response;
		String requestURI = httpRequest.getRequestURI();
		log.info("requsetURI = {}", requestURI);
		try {
			if (isLoginCheckPath(requestURI)) {
				httpResponse.sendRedirect("/?redirectURL=" + requestURI);
				return;
			}
			log.info("loginFilter doFilter method doFilter before");
			chain.doFilter(request, response);
			log.info("loginFilter doFilter method doFilter after");
		} catch (Exception e) {
			throw e;
		} finally {
			log.info("인증 체크 필터 종료 {}", requestURI);
		}

	}

	/**
	 * 화이트 리스트의 경우 인증 체크 X
	 */
	private boolean isLoginCheckPath(String requestURI) {
		return !PatternMatchUtils.simpleMatch(whiteList, requestURI);
	}
}
