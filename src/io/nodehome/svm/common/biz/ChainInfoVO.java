package io.nodehome.svm.common.biz;

import java.io.Serializable;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.nodehome.cmm.service.GlobalProperties;
import io.nodehome.svm.common.CPWalletUtil;

@SuppressWarnings("serial")
public class ChainInfoVO implements Serializable {

	private static HashMap chainInfo = new HashMap();

	public static String[] reloadChainInfo(String chainID) {
		JSONObject joCoinInfo = null;
		joCoinInfo = CPWalletUtil.getValue(GlobalProperties.getProperty("project_serviceID"), ApiHelper.EC_CHAIN, "version", new String[] {"PID","10000"}, chainID );
		String[] rtnValue = {"",""};
		
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
					String ver = String.valueOf(joInfo.get("ver"));
					String network = String.valueOf(joInfo.get("network"));
					chainInfo.put(chainID+"VER", ver);
					chainInfo.put(chainID+"NET", network);
					rtnValue[0] = ver;
					rtnValue[1] = network;
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return rtnValue;
	}

	public static String getVersion(String chainID) {
		String ver = (String)chainInfo.get(chainID+"VER");
		if(ver==null || ver.equals("")) ver = (reloadChainInfo(chainID))[0];
		return ver;
	}
	
	public static String getNetwork(String chainID) {
		String network = (String)chainInfo.get(chainID+"NET");
		if(network==null || network.equals("")) network = (reloadChainInfo(chainID))[1];
		return network;
	}
}
