package io.nodehome.svm.common.web;

import java.io.IOException;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.LocaleResolver;

import io.nodehome.cmm.service.GlobalProperties;
import io.nodehome.libs.bitutil.crypto.InMemoryPrivateKey;
import io.nodehome.libs.bitutil.model.NetConfig;
import io.nodehome.svm.common.biz.ApiHelper;
import io.nodehome.svm.common.util.StringUtil;

@Controller
public class ComIndexController {

	private static final Logger LOGGER = LoggerFactory.getLogger(ComIndexController.class);
	private LocaleResolver localeResolver;

	@RequestMapping("/index")
	public String mypage(HttpServletRequest request, HttpServletResponse response, ModelMap model) throws UnknownHostException, IOException {
		//System.out.println("***************** wid : " +request.getParameter("wid"));
		return "/index";
	}
}
