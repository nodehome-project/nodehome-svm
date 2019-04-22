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
	 * Coin Transmission Confirm Page
	 */
	@RequestMapping("/svm/wallet/sendCoinConfirm")
	public String sendCoinConfirm(@RequestBody String map, ModelMap model) throws UnknownHostException, IOException {
		String requestValue = StringUtil.nvl(URLDecoder.decode(map,"utf-8"));
		if((requestValue.substring(requestValue.length()-1)).equals("=")) requestValue = requestValue.substring(0, requestValue.length()-1);
		model.addAttribute("requestValue" ,requestValue);
		return "svm/wallet/sendCoinConfirm";
	}
	
	/*
	 * Coin Transmission Complete Page
	 */
	@RequestMapping("/svm/wallet/sendCoinComplete")
	public String sendCoinComplete(@RequestBody String map, ModelMap model) throws UnknownHostException, IOException {
		String requestValue = StringUtil.nvl(URLDecoder.decode(map,"utf-8"));
		if((requestValue.substring(requestValue.length()-1)).equals("=")) requestValue = requestValue.substring(0, requestValue.length()-1);
		model.addAttribute("requestValue" ,requestValue);
		return "svm/wallet/sendCoinComplete";
	}

	/*
	 * Create Wallet Entry Form
	 */
	@RequestMapping("/svm/wallet/createWalletForm")
	public String createWalletForm(HttpServletRequest request, ModelMap model) throws UnknownHostException, IOException {
		return "svm/wallet/createWalletForm";
	}
	
	/*
	 * My Wallet List
	 */
	@RequestMapping("/svm/wallet/myWalletList")
	public String myWalletList(HttpServletRequest request, ModelMap model) throws UnknownHostException, IOException {
		return "svm/wallet/myWalletList";
	}
	
	/*
	 * Transaction history
	 */
	@RequestMapping("/svm/wallet/myTransHistory")
	public String transHistory(HttpServletRequest request, ModelMap model) throws UnknownHostException, IOException {
		return "svm/wallet/myTransHistory";
	}
	
	/*
	 * Create Wallet Restore Name Input Form
	 */
	@RequestMapping("/svm/wallet/createWalletRestore")
	public String createWalletRestore(HttpServletRequest request, ModelMap model) throws UnknownHostException, IOException {
		return "svm/wallet/createWalletRestore";
	}

	/*
	 * Move regist wallet form page
	 */
	@RequestMapping("/svm/wallet/registWalletForm")
	public String registWalletForm(HttpServletRequest request, ModelMap model) throws UnknownHostException, IOException {
		return "svm/wallet/registWalletForm";
	}

	@RequestMapping("/svm/wallet/sendTokenForm")
	public String sendTokenForm(HttpServletRequest request, ModelMap model) throws UnknownHostException, IOException {
		return "svm/wallet/sendTokenForm";
	}

	@RequestMapping("/svm/wallet/createTokenForm")
	public String createTokenForm(HttpServletRequest request, ModelMap model) throws UnknownHostException, IOException {
		return "svm/wallet/createTokenForm";
	}

	@RequestMapping("/svm/wallet/mintTokenForm")
	public String mintTokenForm(HttpServletRequest request, ModelMap model) throws UnknownHostException, IOException {
		return "svm/wallet/mintTokenForm";
	}
	
	@RequestMapping("/svm/wallet/myTokenHistory")
	public String myTokenHistory(HttpServletRequest request, ModelMap model) throws UnknownHostException, IOException {
		return "svm/wallet/myTokenHistory";
	}
}
