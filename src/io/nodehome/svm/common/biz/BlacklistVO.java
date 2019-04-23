package io.nodehome.svm.common.biz;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.nodehome.svm.common.util.DateUtil;

@SuppressWarnings("serial")
public class BlacklistVO implements Serializable {

	public static long BLACKLIST_CHECK_TIME = 10 * 60 * 1000;	// timestamp 10 minute
	private static List blacklist = new ArrayList();
	public static String NORMAL = "normal";		// 판정결과 정상
	public static String ABNORMAL = "abnormal";	// 판정결과 비정상
	
	/*
	 * blacklist host map에 등록
	 */
	public static boolean setCheckResult(String serviceId, String remoteHost, String targetHost, String validResult) {
		long nowTime = DateUtil.getTimeStamp();
		boolean processCheck = false;
		HashMap cmap = null;
		//System.out.println("remoteHost : "+remoteHost);
		//System.out.println("targetHost : "+targetHost);
		//System.out.println("validResult : "+validResult);
		
		if(blacklist.size()>0) {
			int checkIndex = -1;
			for(int i=0; i<blacklist.size(); i++) {
				HashMap map = (HashMap)blacklist.get(i);
				if(((String)map.get("serviceId")).equals(serviceId)) {
					cmap = map;
					checkIndex = i;
					break;
				}
			}
			if(cmap!=null) {	// 체크 리스트 존재하면
				long ctime = (long)cmap.get("startTime");
				long diffSec = nowTime - ctime;
				if(diffSec > BLACKLIST_CHECK_TIME) {	// 시간 경과 확인하고 초과 시 새로 등록
					blacklist.remove(checkIndex);
					cmap = null;
				} else {	// 체크 시간 이내
					if(!cmap.containsKey("blacklistHost")) {	// 미등록 상태 일때만 
						cmap.put("blacklistHost", new String[]{targetHost, validResult, remoteHost, Long.toString(nowTime)});
						blacklist.set(checkIndex, cmap);
					}
					processCheck = true;
				}
			}
		}

		if(!processCheck) {
			cmap = new HashMap();
			cmap.put("serviceId",serviceId);
			cmap.put("startTime",nowTime);
			cmap.put("blacklistHost", new String[]{targetHost, validResult, remoteHost, Long.toString(nowTime)});
			blacklist.add(cmap);
			processCheck = true;
		}

		//System.out.println("blacklist.size() : "+blacklist.size());
		
		return processCheck;
	}

	/*
	 * blacklist host map에 등록된 점검 결과를 확인한다.
	 */
	public static String getCheckResult(String serviceId, String targetHost, String validResult) {
		String result = "";
		long nowTime = DateUtil.getTimeStamp();
		HashMap cmap = null;
		//System.out.println("remoteHost : "+remoteHost);
		//System.out.println("targetHost : "+targetHost);
		//System.out.println("validResult : "+validResult);
		
		if(blacklist.size()>0) {
			for(int i=0; i<blacklist.size(); i++) {
				HashMap map = (HashMap)blacklist.get(i);
				if(((String)map.get("serviceId")).equals(serviceId)) {
					cmap = map;
					break;
				}
			}
			if(cmap!=null) {	// 체크 리스트 존재하면
				long ctime = (long)cmap.get("startTime");
				long diffSec = nowTime - ctime;
				if(diffSec < BLACKLIST_CHECK_TIME) {	// 제한 시간 이내일 경우만
					String[] res = (String[])cmap.get("blacklistHost");
					if(res!=null && res.length>2 && res[0].equals(targetHost))
						result = res[1];
				}
			}
		}

		if(result==null) result = "";
		/*
		 * return value
		 * 		"" - 등록된 결과가 없을때 또는 BLACKLIST_CHECK_TIME 제한시간을 초과 했을때
		 * 		"normal" - 정상 
		 * 		"abnormal" - 비정상 
		 */
		return result;
	}

}
