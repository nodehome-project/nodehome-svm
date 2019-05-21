package io.nodehome.svm.common.biz;

import java.io.Serializable;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.nodehome.cmm.service.GlobalProperties;
import io.nodehome.svm.common.CPWalletUtil;

@SuppressWarnings("serial")
public class ChainInfoVO implements Serializable {

	private static String ver = "";
	private static String network = "";

	public static void reloadChainInfo() {
		JSONObject joCoinInfo = null;
		joCoinInfo = CPWalletUtil.getValue(GlobalProperties.getProperty("project_serviceid"), ApiHelper.EC_CHAIN, "version", new String[] {"PID","10000"} );
		
		String strCoinValue = "";
		if(joCoinInfo!=null) {
			strCoinValue = String.valueOf(joCoinInfo.get("value"));	
		}

		if(strCoinValue!=null && strCoinValue.length()>0 && !strCoinValue.equals("{}")) {

			JSONParser jpTemp = new JSONParser();
			JSONObject joInfo;
			try {
				joInfo = (JSONObject)jpTemp.parse(strCoinValue);
				if(joInfo!=null) {
					ver = String.valueOf(joInfo.get("ver"));
					network = String.valueOf(joInfo.get("network"));
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	public static String getVersion() {
		if(ver.equals("")) reloadChainInfo();
		return ver;
	}
	
	public static String getNetwork() {
		if(network.equals("")) reloadChainInfo();
		return network;
	}
}
