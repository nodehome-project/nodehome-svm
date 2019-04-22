package io.nodehome.cmm.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class LoginManager implements HttpSessionBindingListener {
	private static LoginManager loginManager = null;
	
	private static List<HashMap> loginUsers = new ArrayList<HashMap>();
	
	private LoginManager() {
		super();
	}
	
	public static synchronized LoginManager getInstance() {
		if(loginManager==null) {
			loginManager = new LoginManager();
		}
		return loginManager;
	}
	
	// Login Check
	public boolean isValid(HttpSession session, String userId, String userPw) {
		setSession(session, userId);
		return true;
	}
	
	// Check if you are logged in
	// True if logged in, false if not logged in
	public boolean isLogin(String sessionId) {
		boolean isLogin = false;
		
		for(int i=0; i<loginUsers.size(); i++) {
			HashMap map = loginUsers.get(i);
			if(((String)map.get("user_id")).equals(sessionId)) {
				isLogin = true;					
			}
		}
		return isLogin;
	}
	
	// Store id in hash table
	public void setSession(HttpSession session, String userId) {
		HashMap lm = new HashMap<String,String>();
		lm.put("user_id", userId);
		lm.put("session_id", session.getId());
		loginUsers.add(lm);
		session.setMaxInactiveInterval(20);
		session.setAttribute("user_id", userId);
		session.setAttribute("login", this.getInstance());

		System.out.println("--------------------------------------");
		for(int i=0; i<loginUsers.size(); i++) {
			HashMap map = loginUsers.get(i);
			System.out.println("key : " + map.get("user_id") +" , "+map.get("session_id"));
		}
		System.out.println("--------------------------------------");
	}
	
	// When the session is established
	public void valueBound(HttpSessionBindingEvent event) {
		;
	}
	
	// When session is disconnected
	public void valueUnbound(HttpSessionBindingEvent event) {
		List<HashMap> loginUsers2 = new ArrayList<HashMap>();
		for(int i=0; i<loginUsers.size(); i++) {
			HashMap map = loginUsers.get(i);
			if(!((String)map.get("session_id")).equals(event.getSession().getId())) {
				loginUsers2.add(map);
			} else {
			}
		}
		loginUsers = loginUsers2;
	}
	
	// Identify the currently logged in ID by session ID
	//public String getUserId(String sessionId) {
	//	return (String)loginUsers.get(sessionId);
	//}
	
	// Current Users
	public int getUserCount() {
		return loginUsers.size();
	}
}
