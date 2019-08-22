package io.nodehome.cmm.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Enumeration;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SessionCookieUtil {

    /**
     * Ability to generate session information with key value given in HttpSession
     * 
     * @param request
     * @param keyStr - Session key
     * @param valStr - the session value
     * @throws Exception
     */
    public static void setSessionAttribute(HttpServletRequest request, String keyStr, String valStr) throws Exception {

		HttpSession session = request.getSession();
		session.setAttribute(keyStr, valStr);
    }

    /**
     * Ability to create a session object with the given key value in HttpSession
     * 
     * @param request
     * @param keyStr - Session key
     * @param valStr - the session value
     * @throws Exception
     */
    public static void setSessionAttribute(HttpServletRequest request, String keyStr, Object obj) throws Exception {

		HttpSession session = request.getSession();
		session.setAttribute(keyStr, obj);
    }

    /**
     * Get session value corresponding to given key value in HttpSession
     * 
     * @param request
     * @param keyStr - Session key
     * @return
     * @throws Exception
     */
    public static Object getSessionAttribute(HttpServletRequest request, String keyStr) throws Exception {

		HttpSession session = request.getSession();
		return session.getAttribute(keyStr);
    }

    /**
     * Ability to call all values in the HttpSession object
     * 
     * @param request
     * @return
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public static String getSessionValuesString(HttpServletRequest request) throws Exception {
		HttpSession session = request.getSession();
		String returnVal = "";
	
		Enumeration e = session.getAttributeNames();
		while (e.hasMoreElements()) {
		    String sessionKey = (String)e.nextElement();
		    returnVal = returnVal + "[" + sessionKey + " : " + session.getAttribute(sessionKey) + "]";
		}
	
		return returnVal;
    }

    /**
     * Ability to delete a session in an HttpSession with a given key value
     * 
     * @param request
     * @param keyStr - Session key
     * @throws Exception
     */
    public static void removeSessionAttribute(HttpServletRequest request, String keyStr) throws Exception {
	
		HttpSession session = request.getSession();
		session.removeAttribute(keyStr);
    }

    /**
     * Creation of cookies - Set the cookies to be retained for the number of minutes received.
     * Set cookie validity time to 5 minutes => (cookie.setMaxAge (60 * 5)
     * Set the effective time of the cookie to 10 days => (cookie.setMaxAge (60 * 60 * 24 * 10)
     * 
     * @param response - Response
     * @param cookieNm - Cookie name
     * @param cookieValue - the cookie value
     * @param minute - Time to last (minutes)
     * @return
     * @exception
     * @see
     */
    public static void setCookie(HttpServletResponse response, String cookieNm, String cookieVal, int minute) throws UnsupportedEncodingException {
		String cookieValue = URLEncoder.encode(cookieVal, "utf-8");
		Cookie cookie = new Cookie(cookieNm, cookieValue);
		cookie.setMaxAge(60 * minute);
		response.addCookie(cookie);
    }

    /**
     * Cookie Generation - If you do not set a cookie expiration time, the lifetime of the cookie is
     * 
     * @param response - Response
     * @param cookieNm - Cookie name
     * @param cookieValue - the cookie value
     * @return
     * @exception
     * @see
     */

    public static void setCookie(HttpServletResponse response, String cookieNm, String cookieVal) throws UnsupportedEncodingException {
		String cookieValue = URLEncoder.encode(cookieVal, "utf-8");
		Cookie cookie = new Cookie(cookieNm, cookieValue);
		response.addCookie(cookie);
    }

    /**
     * Use cookie value - Reads the cookie value.
     * 
     * @param request - Request
     * @param name - the cookie name
     * @return Cookie value
     * @exception
     * @see
     */
    public static String getCookie(HttpServletRequest request, String cookieNm) throws Exception {
		Cookie[] cookies = request.getCookies();
	
		if (cookies == null)
		    return "";
	
		String cookieValue = null;
	
		for (int i = 0; i < cookies.length; i++) {
		    if (cookieNm.equals(cookies[i].getName())) {
			cookieValue = URLDecoder.decode(cookies[i].getValue(), "utf-8");
	
			break;
	
		    }
		}
	
		return cookieValue;
    }

    /**
     * Clear cookie value - cookie.setMaxAge(0) - The same effect as clearing cookies by setting the effective time of the cookie to 0
     * 
     * @param request - Request
     * @param name - the cookie name
     * @return Cookie value
     * @exception
     * @see
     */
    public static void setCookie(HttpServletResponse response, String cookieNm) throws UnsupportedEncodingException {
		Cookie cookie = new Cookie(cookieNm, null);
		cookie.setMaxAge(0);
		response.addCookie(cookie);
    }
}