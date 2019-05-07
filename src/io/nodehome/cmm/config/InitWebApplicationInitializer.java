package io.nodehome.cmm.config;

import java.io.IOException;

import javax.servlet.FilterRegistration;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import io.nodehome.cmm.service.GlobalProperties;
import io.nodehome.svm.common.biz.ApiHelper;
import io.nodehome.svm.common.biz.CoinListVO;
import io.nodehome.svm.common.biz.NAHostVO;
import io.nodehome.svm.common.biz.ServiceWalletVO;
import io.nodehome.svm.common.util.KeyManager;
import net.fouri.libs.bitutil.model.NetConfig;
import net.fouri.libs.bitutil.model.NetConfig.NetworkType;

public class InitWebApplicationInitializer implements WebApplicationInitializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(InitWebApplicationInitializer.class);
	
	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		LOGGER.debug("WebApplicationInitializer START-============================================");
		
		//-------------------------------------------------------------
		// ServletContextListener Condif
		//-------------------------------------------------------------
		servletContext.addListener(new io.nodehome.cmm.context.WebServletContextListener());
				
		//-------------------------------------------------------------
		// Spring CharacterEncodingFilter setup
		//-------------------------------------------------------------
		FilterRegistration.Dynamic characterEncoding = servletContext.addFilter("encodingFilter", new org.springframework.web.filter.CharacterEncodingFilter());
		characterEncoding.setInitParameter("encoding", "UTF-8");
		characterEncoding.setInitParameter("forceEncoding", "true");
		characterEncoding.addMappingForUrlPatterns(null, false, "/*");
		//characterEncoding.addMappingForUrlPatterns(EnumSet.of(DispatcherType.REQUEST), true, "*.do");
		
		//-------------------------------------------------------------
		// Spring ServletContextListener setup
		//-------------------------------------------------------------
		XmlWebApplicationContext rootContext = new XmlWebApplicationContext();
		rootContext.setConfigLocations(new String[] { "classpath*:resources/spring/com/**/context-*.xml" });
		rootContext.refresh();
		rootContext.start();
		
		servletContext.addListener(new ContextLoaderListener(rootContext));
		
		//-------------------------------------------------------------
		// Spring ServletContextListener setup
		//-------------------------------------------------------------
		XmlWebApplicationContext xmlWebApplicationContext = new XmlWebApplicationContext();
		xmlWebApplicationContext.setConfigLocation("/WEB-INF/config/fouri/springmvc/fouri-com-*.xml");
		ServletRegistration.Dynamic dispatcher = servletContext.addServlet("dispatcher", new DispatcherServlet(xmlWebApplicationContext));
		dispatcher.setLoadOnStartup(1);
		dispatcher.addMapping("/");
		dispatcher.addMapping("*.do");
		
		//-------------------------------------------------------------
		// Spring RequestContextListener setup
		//-------------------------------------------------------------
		servletContext.addListener(new org.springframework.web.context.request.RequestContextListener());
		
		LOGGER.debug("WebApplicationInitializer END-============================================");

		//----------------------------------
		// set default net type
		String strValue = GlobalProperties.getProjectNet();
		NetworkType emType = NetworkType.TESTNET; 
		if(strValue.equalsIgnoreCase("MAINNET"))
			emType = NetworkType.MAINNET;
		else if(strValue.equals("DEBUGNET"))
			emType = NetworkType.DEBUGNET;
		NetConfig.setDefaultNet(emType);

		String tempHosts = "";
		try {
			tempHosts = ApiHelper.postJSON("http://127.0.0.1:"+GlobalProperties.getProperty("nodem_port")+"/selfcheck.bin?p="+GlobalProperties.getProperty("nodem_port")+"&d="+GlobalProperties.getProperty("Globals.serviceHost"), "{}");
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		System.out.println("selfcheck.bin : "+tempHosts);

		//----------------------------------
		KeyManager.reloadManagerKey();
		CoinListVO.reloadCoinInfo();	// load coin info
		CoinListVO.reloadCoinPolicy();	// load coin policy
		ServiceWalletVO.reloadWalletInfo();	// load service node wallet info
		NAHostVO.reloadNAHosts();	// load NA host list
		//----------------------------------
		
	}
	
}
