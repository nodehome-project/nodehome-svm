package io.nodehome.svm.common.biz;

import java.io.Serializable;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import io.nodehome.cmm.service.GlobalProperties;
import io.nodehome.svm.common.CPWalletUtil;
import io.nodehome.svm.common.CoinUtil;

@SuppressWarnings("serial")
public class CoinListVO implements Serializable {

	private static List<HashMap> coinList = new ArrayList<HashMap>();
	private static HashMap coinInfo = new HashMap();	// Coin information
	private static HashMap coinPolicy = new HashMap();	// Coin operation policy

	public static HashMap getCoinInfo() {
		return coinInfo;
	}

	public static void setCoinInfo(HashMap coinInfo) {
		CoinListVO.coinInfo = coinInfo;
	}

	public static List<HashMap> getCoinList() {
		return (ArrayList<HashMap>)coinList;
	}

	public static void setCoinList(List<HashMap> clist) {
		coinList = clist;
	}

	public static HashMap getCoinListInfo(int coinNo) {
		HashMap rmap = null;
		for(int i=0; i<coinList.size(); i++) {
			rmap = coinList.get(i);
			if( Integer.parseInt(((String)rmap.get("cid")).replaceAll("COIN_",""))  == coinNo) {
				return rmap;
			}
		}
		return null;
	}
	public static String getCoinListCou(int coinNo) {
		HashMap rmap = null;
		for(int i=0; i<coinList.size(); i++) {
			rmap = coinList.get(i);
			if( Integer.parseInt(((String)rmap.get("cid")).replaceAll("COIN_",""))  == coinNo) {
				return (String)rmap.get("cou");
			}
		}
		return null;
	}

	public static String getCoinCou() {
		if(coinInfo==null || coinInfo.get("cou")==null) reloadCoinInfo();
		return CoinUtil.DISPLAY_COIN_WORD + (String)coinInfo.get("cou");
	}
	public static String getMaxRemittanceAmount() {
		if(coinInfo==null || coinInfo.get("mxt")==null) reloadCoinInfo();
		return Long.toString((long)coinInfo.get("mxt"));
	}
	public static String getMinRemittanceAmount() {
		if(coinInfo==null || coinInfo.get("mit")==null) reloadCoinInfo();
		return Long.toString((long)coinInfo.get("mit"));
	}
	
	// reload coin list
	public static void reloadCoinInfo() {
		JSONObject joCoinInfo = null;
		joCoinInfo = CPWalletUtil.getValue(GlobalProperties.getProperty("project_serviceid"), ApiHelper.EC_CHAIN, "getCoinInfo", new String[] {"PID","10000"} );
		
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
				}
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}
	
	// reload coin list
	public static void reloadCoinPolicy() {
		JSONObject joCoinInfo = null;
		joCoinInfo = CPWalletUtil.getValue(GlobalProperties.getProperty("project_serviceid"), ApiHelper.EC_CHAIN, "getCoinPolicy", new String[] {"PID","10000"} );
		
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
				}
			    
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
	}

	// Wallet registration fee
	public static String getWalletRegFee() {
		if(coinPolicy==null || coinPolicy.get("FeeRegisterWallet")==null) reloadCoinPolicy();  
		if(coinPolicy==null || coinPolicy.get("FeeRegisterWallet")==null) return "";   
		DecimalFormat df = new DecimalFormat("0");
		double mv = Double.parseDouble((BigInteger.valueOf((long)coinPolicy.get("FeeRegisterWallet"))).toString()) * (Double.parseDouble((String.valueOf(coinPolicy.get("ChipPrice")))));
		String rv = df.format(mv);
		return rv;
	}

	// Service Registration Fee
	public static String getServiceRegFee() {
		if(coinPolicy==null || coinPolicy.get("FeeRegisterService")==null) reloadCoinPolicy();  
		DecimalFormat df = new DecimalFormat("0");   
		double mv = Double.parseDouble((BigInteger.valueOf((long)coinPolicy.get("FeeRegisterService"))).toString()) * (Double.parseDouble((String.valueOf(coinPolicy.get("ChipPrice")))));
		String rv = df.format(mv);
		return rv;
	}

	public static String getCreateTokenFee() {
		if(coinPolicy==null || coinPolicy.get("FeeCreateToken")==null) reloadCoinPolicy();   
		DecimalFormat df = new DecimalFormat("0");     
		double mv = Double.parseDouble((BigInteger.valueOf((long)coinPolicy.get("FeeCreateToken"))).toString()) * (Double.parseDouble((String.valueOf(coinPolicy.get("ChipPrice")))));
		String rv = df.format(mv);
		return rv;
	}

	public static String getFeeMin() {
		if(coinPolicy==null || coinPolicy.get("FeeMin")==null) reloadCoinPolicy();   
		DecimalFormat df = new DecimalFormat("0");
		double mv = Double.parseDouble((BigInteger.valueOf((long)coinPolicy.get("FeeMin"))).toString()) * (Double.parseDouble((String.valueOf(coinPolicy.get("ChipPrice")))));
		String rv = df.format(mv);
		return rv;
	}

	public static HashMap getCoinPolicy() {
		return coinPolicy;
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
