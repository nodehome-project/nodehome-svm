package io.nodehome.cmm.context;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nodehome.cmm.service.GlobalProperties;

public class WebServletContextListener implements ServletContextListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebServletContextListener.class);
    
    public WebServletContextListener(){
    	setProfileSetting();
    }

    public void contextInitialized(ServletContextEvent event){
    	if(System.getProperty("spring.profiles.active") == null){
    		setProfileSetting();
    	}
    }

    public void contextDestroyed(ServletContextEvent event) {
    	System.setProperty("spring.profiles.active", "");
    } 
    
    public void setProfileSetting(){
        try {
            LOGGER.debug("===========================Start ServletContextLoad START ===========");
            System.setProperty("spring.profiles.active", GlobalProperties.getProperty("Globals.DbType")+","+GlobalProperties.getProperty("Globals.Auth"));
            LOGGER.debug("Setting spring.profiles.active>"+System.getProperty("spring.profiles.active"));
            LOGGER.debug("===========================END   ServletContextLoad END ===========");
        } catch(IllegalArgumentException e) {
    		LOGGER.error("[IllegalArgumentException] Try/Catch...usingParameters Runing : "+ e.getMessage());
        } catch (Exception e) {
        	LOGGER.error("[" + e.getClass() +"] search fail : " + e.getMessage());
        }
    }
}
