package io.nodehome.svm.common.web;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.LocaleResolver;

@Controller
public class ComIndexController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ComIndexController.class);
	private LocaleResolver localeResolver;

	@RequestMapping("/index")
	public String mypage(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws UnknownHostException, IOException {
		System.out.println("***************** wid : " +request.getParameter("wid"));
		return "/index";
	}
}
