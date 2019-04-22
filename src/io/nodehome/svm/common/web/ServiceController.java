package io.nodehome.svm.common.web;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ServiceController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ServiceController.class);

	/*
	 * Add new service form
	 */
	@RequestMapping("/svm/service/addServiceForm")
	public String addServiceForm(HttpServletRequest request, ModelMap model) throws UnknownHostException, IOException {
		return "svm/service/addServiceForm";
	}
	
}
