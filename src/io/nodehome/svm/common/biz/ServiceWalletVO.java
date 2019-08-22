package io.nodehome.svm.common.biz;

import java.io.File;
import java.io.Serializable;

import io.nodehome.cmm.service.GlobalProperties;
import io.nodehome.cmm.util.FileUtil;

@SuppressWarnings("serial")
public class ServiceWalletVO implements Serializable {

	private static String walletInfo = "";

	public static void reloadWalletInfo() {
		String FILE_SEPARATOR = System.getProperty("file.separator");
		String relativePathPrefix = GlobalProperties.class.getResource("").getPath().substring(0, GlobalProperties.class.getResource("").getPath().lastIndexOf("classes"));
		String hostPropertiesPath = relativePathPrefix + "hosts" + FILE_SEPARATOR + "";
		String value = "";

    	String sroot = hostPropertiesPath.replaceAll("\\\\", "/")+"wallet_"+GlobalProperties.getProperty("project_serviceID")+".properties";

    	File file = new File(sroot);
    	if(file.exists()) {
        	String str = FileUtil.roadLocalFile(sroot);
        	walletInfo = str.trim().replaceAll(" ","");
    	}
		
	}
	
	public static String getWalletId() {
		return walletInfo;
	}
	
}
