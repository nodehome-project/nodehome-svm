package io.nodehome.cmm.util;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MybaitsUtil {
	
	private static final Logger logger = LoggerFactory.getLogger(MybaitsUtil.class); 
	
    /**
	 * Check whether it is empty.
	 * @param o Object
	 * @return boolean
	 * @exception IllegalArgumentException
	 */
	public static boolean isEmpty(Object o) throws IllegalArgumentException {
		try {
			if(o == null) return true;
		
			if(o instanceof String) {
				if(((String)o).length() == 0){
					return true;
				}
			} else if(o instanceof Collection) {
				if(((Collection)o).isEmpty()){
				return true;
				}
			} else if(o.getClass().isArray()) {
				if(Array.getLength(o) == 0){
				return true;
				}
			} else if(o instanceof Map) {
				if(((Map)o).isEmpty()){
				return true;
				}
			}else {
				return false;
			}
		
			return false;
		} catch(IllegalArgumentException e) {
			logger.error("[IllegalArgumentException] Try/Catch...usingParameters Runing : "+ e.getMessage());
		} catch(Exception e) {
			logger.error("["+e.getClass()+"] Try/Catch...Exception : " + e.getMessage());
		}
		return false;
	}

    /**
	 * Not Check whether it is empty or not.
	 * @param o Object
	 * @return boolean
	 * @exception IllegalArgumentException
	 */
	public static boolean isNotEmpty(Object o) {
		return !isEmpty(o);
	}

    /**
	 * Check whether it is Equal.
	 * @param obj Object, obj Object
	 * @return boolean
	 */
	
    public static boolean isEquals(Object obj, Object obj2){
    	if(isEmpty(obj)) return false;

    	if(obj instanceof String && obj2 instanceof String) {
    		if( (String.valueOf(obj)).equals( String.valueOf(obj2) )){
				return true;
			}
    	}else if(obj instanceof String && obj2 instanceof Integer) {
    		if( (String.valueOf(obj)).equals( String.valueOf((Integer)obj2) )){
				return true;
			}
    		
    	}else if(obj instanceof Integer && obj2 instanceof String) {
    		if( (String.valueOf(obj2)).equals( String.valueOf((Integer)obj) )){
				return true;
			}
		} else if(obj instanceof Integer && obj instanceof Integer) {
    		if((Integer)obj == (Integer)obj2){
				return true;
			}
		}
    	
        return false;	
    }
    
    /**
	 * Checks whether the String is equal.
	 * @param obj Object, obj Object
	 * @return boolean
	 */
    public static boolean isEqualsStr(Object obj, String s){
    	if(isEmpty(obj)) return false;
    	
    	if(s.equals(String.valueOf(obj))){
    		 return true;
    	}
        return false;
    }
    
}
