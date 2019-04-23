package io.nodehome.svm.common.biz;

import java.io.File;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import io.nodehome.cmm.service.GlobalProperties;
import io.nodehome.cmm.util.FileUtil;
import io.nodehome.svm.common.util.StringUtil;

@SuppressWarnings("serial")
public class NAHostVO implements Serializable {

	private static List naHosts = new ArrayList();

	public static void reloadNAHosts() {
		String FILE_SEPARATOR = System.getProperty("file.separator");
		String relativePathPrefix = GlobalProperties.class.getResource("").getPath().substring(0, GlobalProperties.class.getResource("").getPath().lastIndexOf("classes"));
		String hostPropertiesPath = relativePathPrefix + "hosts" + FILE_SEPARATOR + "";

    	String sroot = hostPropertiesPath.replaceAll("\\\\", "/")+"na_hosts.properties";

    	File file = new File(sroot);
    	if(file.exists()) {
        	String str = FileUtil.roadLocalFile(sroot);
        	String[] hosts = str.split("\n");
        	
    		if(str!=null && str.length()>0) {
    			if(hosts.length>0) {
    				for(int s=0; s<hosts.length; s++) {
        				String tUrl = StringUtil.nvl(hosts[s]).trim();
    					if(!tUrl.equals("")) {

    						String[] tmp = tUrl.replaceAll("https://", "").replaceAll("http://", "").split(":");
    						//System.out.println("tmp[0] : "+tmp[0]);
    						//System.out.println("tmp[1] : "+tmp[1]);
    						
    						// 포트가 열렸는지 확인할 IP 주소와 포트
    						boolean portCheck = availablePort(tmp[0],Integer.parseInt(tmp[1]));

    						if(portCheck) {
    							naHosts.add((String)tUrl);
    							//System.out.println("port available");
    						} else {
    							//System.out.println("port unavailable");
    						}
    							    
    					}
    				}
    				
    			}
    		}
    	}
		
	}
	
	public static boolean availablePort(String host, int port) {
		boolean result = false;

		try {
			(new Socket(host, port)).close();

			result = true;
		} catch (Exception e) {

		}
		return result;
	}


	public static List getNAHosts() {
		if(naHosts==null || naHosts.size()==0) reloadNAHosts();
		return naHosts;
	}
	
}
