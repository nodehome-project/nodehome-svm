package io.nodehome.svm.common.biz;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.nodehome.cmm.service.GlobalProperties;
import io.nodehome.svm.common.CPWalletUtil;
import io.nodehome.svm.common.CoinUtil;

@SuppressWarnings("serial")
public class CoinListVO implements Serializable {

	//private static List<HashMap> coinList = new ArrayList<HashMap>();
	private static HashMap<Object,HashMap> coinInfoList = new HashMap<Object,HashMap>();	// Coin information
	private static HashMap<Object,HashMap> coinPolicyList = new HashMap<Object,HashMap>();	// Coin operation policy

//	public static List<HashMap> getCoinList() {
//		return (ArrayList<HashMap>)coinList;
//	}
//
//	public static void setCoinList(List<HashMap> clist) {
//		coinList = clist;
//	}
//
//	public static HashMap getCoinListInfo(int coinNo) {
//		HashMap rmap = null;
//		for(int i=0; i<coinList.size(); i++) {
//			rmap = coinList.get(i);
//			if( Integer.parseInt(((String)rmap.get("cid")).replaceAll("COIN_",""))  == coinNo) {
//				return rmap;
//			}
//		}
//		return null;
//	}
//	public static String getCoinListCou(int coinNo) {
//		HashMap rmap = null;
//		for(int i=0; i<coinList.size(); i++) {
//			rmap = coinList.get(i);
//			if( Integer.parseInt(((String)rmap.get("cid")).replaceAll("COIN_",""))  == coinNo) {
//				return (String)rmap.get("cou");
//			}
//		}
//		return null;
//	}

	public static HashMap getCoinInfo(String chainID) {
		HashMap coinInfo = (HashMap)coinInfoList.get(chainID);
		if(coinInfo==null || coinInfo.get("cou")==null) coinInfo = reloadCoinInfo(chainID);
		return coinInfo;
	}

	public static void setCoinInfo(HashMap coinInfo, String chainID) {
		coinInfoList.put(chainID, coinInfo);
	}
	
	public static String getCoinCou(String chainID) {
		HashMap coinInfo = (HashMap)coinInfoList.get(chainID);
		if(coinInfo==null || coinInfo.get("cou")==null) coinInfo = reloadCoinInfo(chainID);
		return CoinUtil.DISPLAY_COIN_WORD + (String)coinInfo.get("cou");
	}
	public static String getMaxRemittanceAmount(String chainID) {
		HashMap coinInfo = (HashMap)coinInfoList.get(chainID);
		if(coinInfo==null || coinInfo.get("mxt")==null) coinInfo = reloadCoinInfo(chainID);
		return Long.toString((long)coinInfo.get("mxt"));
	}
	public static String getMinRemittanceAmount(String chainID) {
		HashMap coinInfo = (HashMap)coinInfoList.get(chainID);
		if(coinInfo==null || coinInfo.get("mnt")==null) coinInfo = reloadCoinInfo(chainID);
		return Long.toString((long)coinInfo.get("mnt"));
	}
	
	// reload coin list
	public static HashMap reloadCoinInfo(String chainID) {
		JSONObject joCoinInfo = null;
		HashMap coinInfo = new HashMap();
		joCoinInfo = CPWalletUtil.getValue(GlobalProperties.getProperty("project_serviceID"), ApiHelper.EC_CHAIN, "getCoinInfo", new String[] {"PID","10000"}, chainID );

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
					coinInfo.put("nm", joInfo.get("nm"));
					coinInfo.put("cou", joInfo.get("cou"));
					coinInfo.put("mxt", joInfo.get("mxt"));
					coinInfo.put("mit", joInfo.get("mit"));
					coinInfo.put("mnt", joInfo.get("mnt"));
					try {
						coinInfoList.put(chainID, coinInfo);
					} catch(Exception e) {
						e.printStackTrace();
					}
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return coinInfo;
	}
	
	// reload coin list
	public static HashMap reloadCoinPolicy(String chainID) {
		HashMap coinPolicy = new HashMap();
		JSONObject joCoinInfo = null;
		joCoinInfo = CPWalletUtil.getValue(GlobalProperties.getProperty("project_serviceID"), ApiHelper.EC_CHAIN, "getCoinPolicy", new String[] {"PID","10000"}, chainID );
		
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
					coinPolicy.put("FeeWid", joInfo.get("fee_wid"));
					coinPolicy.put("ChipPrice", joInfo.get("chip_price"));		// Coin exchange rate
					coinPolicy.put("FeeMin", joInfo.get("fee_min"));			// Minimum fee
					coinPolicy.put("FeeMax", joInfo.get("fee_max"));			// Maximum fee
					coinPolicy.put("FeeRate", joInfo.get("fee_rate"));			// Commission rate (%)
					coinPolicy.put("ChipRegData", joInfo.get("chip_regdata"));
					
					JSONObject joInfo2 = (JSONObject)joInfo.get("fixed_fees");
					coinPolicy.put("FeeRegisterService", joInfo2.get("registerService"));	// Service Registration Fee
					coinPolicy.put("FeeRegisterWallet", joInfo2.get("registerWallet"));	// Wallet registration fee
					coinPolicy.put("FeeCreateToken", joInfo2.get("createToken"));
					coinPolicyList.put(chainID, coinPolicy);
				}
			    
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return coinPolicy;
	}

	public static HashMap getPolicyInfo(String chainID) {
		HashMap coinPolicy = (HashMap)coinPolicyList.get(chainID);
		if(coinPolicy==null) coinPolicy = reloadCoinPolicy(chainID);
		return coinPolicy;
	}
	
	// Wallet registration fee
	public static String getWalletRegFee(String chainID) {
		HashMap coinPolicy = (HashMap)coinPolicyList.get(chainID);
		if(coinPolicy==null || coinPolicy.get("FeeRegisterWallet")==null) coinPolicy = reloadCoinPolicy(chainID);  
		if(coinPolicy==null || coinPolicy.get("FeeRegisterWallet")==null) return "";   
		DecimalFormat df = new DecimalFormat("0");
		double mv = Double.parseDouble((BigInteger.valueOf((long)coinPolicy.get("FeeRegisterWallet"))).toString()) * (Double.parseDouble((String.valueOf(coinPolicy.get("ChipPrice")))));
		String rv = df.format(mv);
		return rv;
	}

	// Service Registration Fee
	public static String getServiceRegFee(String chainID) {
		HashMap coinPolicy = (HashMap)coinPolicyList.get(chainID);
		if(coinPolicy==null || coinPolicy.get("FeeRegisterService")==null) coinPolicy = reloadCoinPolicy(chainID);  
		DecimalFormat df = new DecimalFormat("0");   
		double mv = Double.parseDouble((BigInteger.valueOf((long)coinPolicy.get("FeeRegisterService"))).toString()) * (Double.parseDouble((String.valueOf(coinPolicy.get("ChipPrice")))));
		String rv = df.format(mv);
		return rv;
	}

	public static String getCreateTokenFee(String chainID) {
		HashMap coinPolicy = (HashMap)coinPolicyList.get(chainID);
		if(coinPolicy==null || coinPolicy.get("FeeCreateToken")==null) coinPolicy = reloadCoinPolicy(chainID);
		String rv = "";
		if(coinPolicy!=null && coinPolicy.get("FeeCreateToken")!=null) {
			DecimalFormat df = new DecimalFormat("0");     
			double mv = Double.parseDouble((BigInteger.valueOf((long)coinPolicy.get("FeeCreateToken"))).toString()) * (Double.parseDouble((String.valueOf(coinPolicy.get("ChipPrice")))));
			rv = df.format(mv);	
		}
		return rv;
	}

	public static String getFeeMin(String chainID) {
		HashMap coinPolicy = (HashMap)coinPolicyList.get(chainID);
		if(coinPolicy==null || coinPolicy.get("FeeMin")==null) coinPolicy = reloadCoinPolicy(chainID);  
		String rv = "";
		if(coinPolicy!=null && coinPolicy.get("FeeMin")!=null) {
			DecimalFormat df = new DecimalFormat("0");
			double mv = Double.parseDouble((BigInteger.valueOf((long)coinPolicy.get("FeeMin"))).toString()) * (Double.parseDouble((String.valueOf(coinPolicy.get("ChipPrice")))));
			rv = df.format(mv);
		}
		return rv;
	}
	
/*
	public static String getCoinListCou(int coinNo) {
		HashMap rmap = null;
		for(int i=0; i<coinList.size(); i++) {
			rmap = coinList.get(i);
			if( Integer.parseInt(((String)rmap.get("cid")).replaceAll("COIN_",""))  == coinNo) {
				return (String)rmap.get("cou");
			}
		}
		return null;
	}*/
}
