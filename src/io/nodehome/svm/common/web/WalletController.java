package io.nodehome.svm.common.web;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import io.nodehome.svm.common.util.StringUtil;

@Controller
public class WalletController {

	private static final Logger LOGGER = LoggerFactory.getLogger(WalletController.class);

	/*
	 * Coin transfer input form
	 */
	@RequestMapping("/svm/wallet/sendCoinForm")
	public String sendCoinForm(HttpServletRequest request, ModelMap model) throws UnknownHostException, IOException {
		return "svm/wallet/sendCoinForm";
	}
	
	/*
	 * Transaction history
	 */
	@RequestMapping("/svm/wallet/myTransHistory")
	public String transHistory(HttpServletRequest request, ModelMap model) throws UnknownHostException, IOException {
		return "svm/wallet/myTransHistory";
	}
	
}
