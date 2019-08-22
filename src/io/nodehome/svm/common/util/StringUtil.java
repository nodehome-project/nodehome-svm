package io.nodehome.svm.common.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

public class StringUtil extends StringUtils {

	public static String unescape(String src) {
		StringBuffer tmp = new StringBuffer();
		tmp.ensureCapacity(src.length());
		int lastPos = 0, pos = 0;
		char ch;
		while (lastPos < src.length()) {
			pos = src.indexOf("%", lastPos);
			if (pos == lastPos) {
				if (src.charAt(pos + 1) == 'u') {
					ch = (char) Integer.parseInt(src
							.substring(pos + 2, pos + 6), 16);
					tmp.append(ch);
					lastPos = pos + 6;
				} else {
					ch = (char) Integer.parseInt(src
							.substring(pos + 1, pos + 3), 16);
					tmp.append(ch);
					lastPos = pos + 3;
				}
			} else {
				if (pos == -1) {
					tmp.append(src.substring(lastPos));
					lastPos = src.length();
				} else {
					tmp.append(src.substring(lastPos, pos));
					lastPos = pos;
				}
			}
		}
		return tmp.toString();
	}
	/**
	 * Method to remove whitespace to the left of the string
	 */
	public static String ltrim(String str){
		int len = str.length();
		int idx = 0;

		while ((idx < len) && (str.charAt(idx) <= ' '))
		{
			idx++;
		}
		return str.substring(idx, len);
	}

	/**
	 * Method to remove whitespace to the right of the string
	 */
	public static String rtrim(String str){
		int len = str.length();

		while ((0 < len) && (str.charAt(len-1) <= ' '))
		{
			len--;
		}
		return str.substring(0, len);
	}

	/**
	 * It extracts and returns only the numeric part of the argument.
	 */
	public static String getDigit(String sValue) {
		if (sValue == null || "".equals(sValue)) {
			return "";
		}
		
		String digitStr = "0123456789";
		StringBuffer sb = new StringBuffer();
		
		for (int i = 0 ; i < sValue.length(); i++) {
			
			char cValue = sValue.charAt(i);			
			for (int j = 0 ; j < digitStr.length(); j++) {
				
				char digit = digitStr.charAt(j);				
				if (cValue == digit) {
					sb.append(cValue);
				}
			}
		}				
		return sb.toString();		
	}

	public static String changeMoney(String str) {
		DecimalFormat df = new DecimalFormat("###,###");

		return df.format(parseInt(str));
	}

	/**
	 * Remove comma from string
	 */
	public static String removeComma(String str) {
		String rtnValue = str;
		if ( isNull(str) ) {
			return "";
		}

		rtnValue = replace(rtnValue, ",", "");
		return rtnValue;
	}

	/**
	 * If str is null or "", "   " return true
	 */
	public static boolean isNull(String str) {
		return (str == null || (str.trim().length()) == 0 );
	}

	public static boolean isNull(Object obj) {
		String str = null;
		if( obj instanceof String ) {
			str = (String)obj;
		} else {
			return true;
		}

		return isNull(str);
	}

	/**
	 * not null
	 */
	public static boolean isNotNull(String str) {
		if( isNull(str) ){
			return false;

		} else {
			return true;
		}
	}

	public static boolean isNotNull(Object obj) {
		String str = null;
		if( obj instanceof String ) {
			str = (String)obj;
		} else {
			return false;
		}

		return isNotNull(str);
	}

	/**
	 * null check
	 */
	public static String nvl(String value) {
		return nvl(value, "");
	}

	/**
	 * null check
	 */
	public static String nvl(Object value) {
		Object rtnValue = value;
		if( rtnValue == null || !"java.lang.String".equals(rtnValue.getClass().getName())) {
			rtnValue = "";
		}

		return nvl((String)rtnValue, "");
	}

	/**
	 * null check
	 */
	public static String nvl(String value, String defaultValue) {
		if (isNull(value)) {
			return defaultValue;
		}

		return value.trim();
	}

	/**
	 * null check
	 */
	public static String nvl(Object value, String defaultValue) {
		String valueStr = nvl(value);
		if ( isNull(valueStr) ) {
			return defaultValue;
		}

		return valueStr.trim();
	}

	/**
	 * Method ksc2asc.
	 * 8859-1 to euc-kr
	 */
	public static String ksc2asc(String str) {
		String result = "";

		if (isNull(str)) {
			result = "";
		} else {
			try {
				result = new String( str.getBytes("euc-kr"), "8859_1" );
			} catch( Exception e ) {
				result = "";
			}
		}

		return result;
	}

	/**
	 * Method asc2ksc.
	 * euc-kr to 8859-1
	 */
	public static String asc2ksc(String str) {
		String result = "";

		if (isNull(str)) {
			result = "";
		} else {
			try {
				result = new String( str.getBytes("8859_1"), "euc-kr" );
			} catch( Exception e ) {
				result = "";
			}
		}

		return result;
	}

	/**
	 * Replace regular characters with double-byte characters
	 */
	public static String hexHex2Str(String strString) {
		String ori = "";
		
		try {
			byte[] result = strString.getBytes("Cp933");
			String temp = "";

			for ( int i = 1; i < result.length-1; i++ ) {
				temp = Integer.toHexString(result[i]).toUpperCase();
				ori = ori + temp.substring(temp.length()-2);
			}
		} catch(Exception _ex) {
			_ex.printStackTrace();
		}
		return ori;
	}
	
	/**
	 * Insert the method's description here.
	 */
	public static String hexStr2Hex(String hexaString) {
		int byteLength = hexaString.length() / 2;
		
		byte[] result = new byte[ byteLength ];

		for ( int i = 0; i < byteLength; i++ ) {

			String frag = hexaString.substring(i*2, i*2+2);
			result[i] = (byte)Integer.parseInt(frag, 16);

		}

		try {
			byte[] temp4 = new byte[ result.length + 2 ];
			System.arraycopy(result, 0, temp4, 1, result.length);
			
			temp4[0] = 0x0E;
			temp4[temp4.length-1] = 0x0F;

			String ori = new String( temp4, "Cp933");
			return getNcharToString(ori).trim();

		} catch(Exception _ex) {
			_ex.printStackTrace();
		}
		return null;
	}
	
	public static String getNcharToString(String Nchar) {
		
		if(Nchar==null || Nchar.length()==0)
			Nchar = "" ;
		else
			Nchar = Nchar.replace('　',' ');

		return Nchar;

	}
	
	/**
	 * Insert the method's description here.
	 */
	public static String hexStr2Str(String hexaString) {
		int byteLength = hexaString.length() / 2;
		
		byte[] result = new byte[ byteLength ];

		for ( int i = 0; i < byteLength; i++ ) {

			String frag = hexaString.substring(i*2, i*2+2);
			result[i] = (byte)Integer.parseInt(frag, 16);

		}

		try {
			byte[] temp4 = new byte[ result.length + 2 ];
			System.arraycopy(result, 0, temp4, 1, result.length);
			
			temp4[0] = 0x0E;
			temp4[temp4.length-1] = 0x0F;

			String ori = new String( temp4, "Cp933");

			//Debug
			return ori.trim();

		} catch(Exception _ex) {
			_ex.printStackTrace();
		}
		return null;
	}
	/**************************************************************************************/
	/*	parse method start	*/


	/**
	 * String to int
	 * @param value
	 * @return
	 */
	public static int parseInt(String value) {
		return parseInt(value, 0);
	}
	/**
	 * Object to int
	 */
	public static int parseInt(Object value) {
		String valueStr = nvl(value);
		return parseInt(valueStr, 0);
	}
	/**
	 * Object to int
	 */
	public static int parseInt(Object value, int defaultValue) {
		String valueStr = nvl(value);
		return parseInt(valueStr, defaultValue);
	}
	/**
	 * String to int
	 */
	public static int parseInt(String value, int defaultValue) {
		int returnValue = 0;

		if( isNull(value) ) {
			returnValue = defaultValue;
		} else if( !isNumeric(value) ) {
			returnValue = defaultValue;
		} else {
			returnValue = Integer.parseInt(value);
		}

		return returnValue;
	}

	/**
	 * String to long
	 */
	public static long parseLong(String value) {
		return parseLong(value, 0);
	}

	/**
	 * String to long
	 */
	public static long parseLong(String value, long defaultValue) {
		long returnValue = 0;

		if( isNull(value) ) {
			returnValue = defaultValue;
		} else if( !isNumeric(value) ) {
			returnValue = defaultValue;
		} else {
			returnValue = Long.parseLong(value);
		}

		return returnValue;
	}

	public static String stackTraceToString(Throwable e) {
		try {
			StringWriter sw = new StringWriter();
			PrintWriter pw = new PrintWriter(sw);
			e.printStackTrace(pw);
			return "------\r\n" + sw.toString() + "------\r\n";
	  }catch(Exception e2) {
		  return StringUtil.stackTraceToString2(e);
	  }
	}
	public static String stackTraceToString2(Throwable e) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		PrintStream p = new PrintStream(b);
		e.printStackTrace(p);
		p.close();
		String stackTrace = b.toString();
		try {
			b.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return stackTrace;
	}

	/**
	 * Remove the &#60;br&#62; tag from your html code
	 */
	public static String convertHtmlBr(String comment) {
		String rtnValue = "";
		if( isNull(comment) ) {
			return "";
		}

		rtnValue = replace(comment, "\r\n", "<br>");

		return rtnValue;
	}


	/**
	 * Converts a String array to a List.
	 */
	public static List changeList(String [] values) {
		List list = new ArrayList();

		if( values == null ) {
			return list;
		}
		for(int i=0,n=values.length; i<n; i++) {
			list.add(values[i]);
		}

		return list;
	}


	public static String[] toTokenArray(String str, String sep){

		String[] temp = null;

		try{
			StringTokenizer st = new StringTokenizer(str, sep);
			temp = new String[st.countTokens()];
			int index = 0;
			while(st.hasMoreTokens()){
				temp[index++] = st.nextToken();
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return temp;
	}

    /**
     * <br> get strings from token
     * @return hashtable.get (num)
     */
    static public Hashtable get_tokens (String p_str, String p_delim) throws Exception {
        Hashtable v_ht = new Hashtable ();

        StringTokenizer v_tokenizer = new StringTokenizer (p_str, p_delim);
        if (v_tokenizer == null)
            return v_ht;

        int v_cnt = 1;

        try {
            while (v_tokenizer.hasMoreTokens ())
                v_ht.put (String.valueOf(v_cnt++), v_tokenizer.nextToken ().trim ());
        } catch (Exception e) { e.printStackTrace(); throw new Exception (e); }

        return v_ht;
    }
    
    public static String strip(String str, String str1){

    	if(str == null || "".equals(str.trim())) return "";

	    String temp = str;
		int pos = -1;
		while((pos = temp.indexOf(str1, pos)) != -1) {
			String left = temp.substring(0, pos);
			String right = temp.substring(pos + 1, temp.length());
			temp = left + "" + right;
			pos += 1;
		}
		return temp;
    }

    /**
	 * Method ksc2asc.
	 */
	public static String ksc2utf8(String str) {
		String result = "";

		if (isNull(str)) {
			result = "";
		} else {
			try {
				result = new String( str.getBytes("euc-kr"), "utf-8" );
			} catch( Exception e ) {
				result = "";
			}
		}

		return result;
	}
	
    /**
	 * @return String
	 */
	public static String utf82ksc(String str) {
		String result = "";

		if (isNull(str)) {
			result = "";
		} else {
			try {
				result = new String( str.getBytes("utf-8"), "euc-kr" );
			} catch( Exception e ) {
				result = "";
			}
		}

		return result;
	}
	
    /**
	 * @return String
	 */
	public static String convertInconding(String str, String t1, String t2) {
		String result = "";

		if (isNull(str)) {
			result = "";
		} else {
			try {
				result = new String( str.getBytes(t1), t2 );
			} catch( Exception e ) {
				result = "";
			}
		}

		return result;
	}

	/**
	 * Converts ',', \ r \ n in string to HTML code.
	 */
	public static String changeQuotation(String str) {
		String rtnValue = str;
		rtnValue = nvl(rtnValue);
		rtnValue = replace(replace(replace(rtnValue, "'", "&#39;"), "\"", "&#34;"), "\r\n", "<br>");

		return rtnValue;
	}
	public static String changeQuotation(Object obj) {
		if( isStringInteger(obj) ) {
			return changeQuotation(String.valueOf(obj));
		}

		return "";
	}

	/**
	 * True if Object is a String or Integer
	 */
	public static boolean isStringInteger(Object obj) {

		boolean flag = false;

		if( obj instanceof String || obj instanceof Integer ) {
			flag = true;
		}

		return flag;
	}

	/**
	 * Get the percentage.
	 */
	public static String percentValue(int value, int total) {
		double val = Double.parseDouble(String.valueOf(value)) / Double.parseDouble(String.valueOf(total)) * 100;

		DecimalFormat df = new DecimalFormat("##0.0");
		return df.format(val);
	}




	/**
	 *  XSS(Cross Site Scripting)
	 */
	public static String replaceXSS(String sourceString){
		String rtnValue = null;
		if(sourceString!=null){
			rtnValue = sourceString;
			if(rtnValue.indexOf("<x-") == -1){
				rtnValue = rtnValue.replaceAll("< *(j|J)(a|A)(v|V)(a|A)(s|S)(c|C)(r|R)(i|I)(p|P)(t|T)", "<x-javascript");
				rtnValue = rtnValue.replaceAll("< *(v|V)(b|B)(s|S)(c|C)(r|R)(i|I)(p|P)(t|T)", "<x-vbscript");
				rtnValue = rtnValue.replaceAll("< *(s|S)(c|C)(r|R)(i|I)(p|P)(t|T)", "<x-script");
				rtnValue = rtnValue.replaceAll("< *(i|I)(f|F)(r|R)(a|A)(m|M)(e|E)", "<x-iframe");
				rtnValue = rtnValue.replaceAll("< *(f|F)(r|R)(a|A)(m|M)(e|E)", "<x-frame");
				rtnValue = rtnValue.replaceAll("(e|E)(x|X)(p|P)(r|R)(e|E)(s|S)(s|S)(i|I)(o|O)(n|N)", "x-expression");
				rtnValue = rtnValue.replaceAll("(a|A)(l|L)(e|E)(r|R)(t|T)", "x-alert");
				rtnValue = rtnValue.replaceAll(".(o|O)(p|P)(e|E)(n|N)", ".x-open");
				rtnValue = rtnValue.replaceAll("< *(m|M)(a|A)(r|R)(q|Q)(u|U)(e|E)(e|E)", "<x-marquee");
				rtnValue = rtnValue.replaceAll("&#", "&amp;#");
			}
		}

		return rtnValue;
    }


	/**
	 * Methods to change specific characters to HTML TAG format.
	 */
	public static String translate(String str){
		if ( str == null ) return null;

		StringBuffer buf = new StringBuffer();
		char[] c = str.toCharArray();
		int len = c.length;

		for ( int i=0; i < len; i++){
			if      ( c[i] == '&' ) buf.append("&amp;");
			else if ( c[i] == '<' ) buf.append("&lt;");
			else if ( c[i] == '>' ) buf.append("&gt;");
			else if ( c[i] == '"' ) buf.append("&quot;");	// (char)34
			else if ( c[i] == '\'') buf.append("&#039;");	// (char)39
			else buf.append(c[i]);
		}
		return buf.toString();
	}

   /**
    * A function that fills a string up to a specified length before or after a particular character    <BR>
    */
	public static String pad(String src, String pad, int totLen, int mode){
		String paddedString = "";

		if(src == null) return "";
		int srcLen = src.length();

		if((totLen<1)||(srcLen>=totLen)) return src;

		for(int i=0; i< (totLen-srcLen); i++){
			paddedString += pad;
		}

		if(mode == -1)
			paddedString += src;	// front padding
		else
	     	paddedString = src + paddedString; //back padding

		return paddedString;
	}

	/**
	 * It sends a given character (cPadder) to the left of strSource with a given length (iLength).
	 * ex) lpad("abc", 5, '^') ==> "^^abc"
	 *     lpad("abcdefghi", 5, '^') ==> "abcde"
	 *     lpad(null, 5, '^') ==> "^^^^^"
	 */
	public static String lpad(String strSource, int iLength, char cPadder){
		StringBuffer sbBuffer = null;

		if (!isEmpty(strSource)){
			int iByteSize = getByteSize(strSource);
			if (iByteSize > iLength){
				return strSource.substring(0, iLength);
			}else if (iByteSize == iLength){
				return strSource;
			}else{
				int iPadLength = iLength - iByteSize;
				sbBuffer = new StringBuffer();
				for (int j = 0; j < iPadLength; j++){
					sbBuffer.append(cPadder);
				}
				sbBuffer.append(strSource);
				return sbBuffer.toString();
			}
		}

		//int iPadLength = iLength;
		sbBuffer = new StringBuffer();
		for (int j = 0; j < iLength; j++){
			sbBuffer.append(cPadder);
		}
		return sbBuffer.toString();
	}

	/**
	 * Sends the given character (cPadder) to the right of strSource given the given length (iLength).
	 * ex) lpad("abc", 5, '^') ==> "abc^^"
	 *     lpad("abcdefghi", 5, '^') ==> "abcde"
	 *     lpad(null, 5, '^') ==> "^^^^^"
	 */
	public static String rpad(String strSource, int iLength, char cPadder){
		StringBuffer sbBuffer = null;
			if (!isEmpty(strSource)){
			int iByteSize = getByteSize(strSource);
			if (iByteSize > iLength){
				return strSource.substring(0, iLength);
			}else if (iByteSize == iLength){
				return strSource;
			}else{
				int iPadLength = iLength - iByteSize;
				sbBuffer = new StringBuffer(strSource);
				for (int j = 0; j < iPadLength; j++){
					sbBuffer.append(cPadder);
				}
				return sbBuffer.toString();
			}
		}
		sbBuffer = new StringBuffer();
		for (int j = 0; j < iLength; j++){
			sbBuffer.append(cPadder);
		}
		return sbBuffer.toString();
	}

	/**
	 *  Get byte size.
	 */
	public static int getByteSize(String str){
		if (str == null || str.length() == 0)
			return 0;
		byte[] byteArray = null;
			try{
			byteArray = str.getBytes("UTF-8");
		}catch (UnsupportedEncodingException ex){}
		if (byteArray == null) return 0;
		return byteArray.length;
	}
	 /**
     * Cut a long string
     */
    public static String strCut(String srcString, int nLength, boolean isNoTag, boolean isAddDot){
        String rtnVal = srcString;
        int oF = 0, oL = 0, rF = 0, rL = 0;
        int nLengthPrev = 0;

        if(isNoTag) {
            Pattern p = Pattern.compile("<(/?)([^<>]*)?>", Pattern.CASE_INSENSITIVE);
            rtnVal = p.matcher(rtnVal).replaceAll("");
        }
        rtnVal = rtnVal.replaceAll("&amp;", "&");
        rtnVal = rtnVal.replaceAll("(!/|\r|\n|&nbsp;)", "");
        try {
        	byte[] bytes = rtnVal.getBytes("UTF-8");
	        int j = 0;
	        if(nLengthPrev > 0) while(j < bytes.length) {
	        	if((bytes[j] & 0x80) != 0) {
	        		oF+=2; rF+=3; if(oF+2 > nLengthPrev) {break;} j+=3;
	            } else {if(oF+1 > nLengthPrev) {break;} ++oF; ++rF; ++j;}
	        }

	        j = rF;

	        while(j < bytes.length) {
	        	if((bytes[j] & 0x80) != 0) {
	        		if(oL+2 > nLength) {break;} oL+=2; rL+=3; j+=3;
	            } else {if(oL+1 > nLength) {break;} ++oL; ++rL; ++j;}
	        }

	        rtnVal = new String(bytes, rF, rL, "UTF-8");

	        if(isAddDot && rF+rL+3 <= bytes.length) {rtnVal+="...";}
        } catch(UnsupportedEncodingException e){
        	e.printStackTrace();
        	return srcString;
        }

	    return rtnVal;
    }


    /**
     * <br> string cut
     * @param p_str string
     * @param p_s start location
     * @param p_e end location
     */
    static public String get_string_from (String p_str, int p_s, int p_e) {
        try {
            return (p_str.length() > p_e) ? p_str.substring (p_s, p_e) + "..." : p_str;
        } catch (Exception e) { return p_str.length () > p_s ? p_str.substring (p_s, p_str.length ()) : ""; }
    }

    /**
     * <br> string cut from spec
     * @param p_data string
     * @param p_start start with string
     * @param p_end end with string
     */
    static public String cut_string_from (String p_data, String p_start, String p_end) {
        int v_pos = 0;
        String v_data = p_data;

        try {
            if ((v_pos = v_data.indexOf (p_start)) == -1) return v_data;

            String v_head = v_data.substring (0, v_pos);
            v_data = v_data.substring (v_pos, v_data.length ());
            return v_head
                   + v_data.substring (v_data.indexOf (p_end) + p_end.length (), v_data.length ());
        }

        catch (Exception e) { return p_data; }
    }

    /* <br> string replace
     * @param p_text full text
     * @param p_org
     * @param p_tar
     */
    static public String cvt_string_from (String p_text, String p_org, String p_tar)
    {
        int v_locate = 0, v_prev = -1;
        String v_tmp = "";

        if (p_text == null || "".equals (p_text))
            return "";

        while (v_locate != -1) {
            if ((v_locate = p_text.indexOf (p_org)) == -1) break;

            if (v_locate == v_prev)
                break;

            v_prev = v_locate;

            v_tmp = p_text.substring (0, v_locate);
            v_tmp += p_tar;
            v_tmp += p_text.substring (v_locate + p_org.length (), p_text.length ());

            p_text = v_tmp;
        };

        return p_text;
    }

    public static String mReplaceBRTag(String inStr) {
        int length = inStr.length();
        StringBuffer buffer = new StringBuffer();
        for(int i = 0; i < length; i++)
        {
            String tmp = inStr.substring(i, i + 1);
            if("\n".compareTo(tmp) == 0)
                buffer.append("<br>");
            if(" ".compareTo(tmp) == 0)
                buffer.append("&nbsp;");
            else
                buffer.append(tmp);
        }

        return buffer.toString();
    }

	
	/**
	 * Lists the contents of the array as comma.
	 * ex) [aaa][bbb][ccc] ->  'aaa','bbb','ccc'
	 */
	public static String convertSQLINString(String [] value) {
		StringBuffer sb = new StringBuffer();
		for (int i  = 0; i < value.length; i++) {
			sb.append(value[i]);
			if (i < value.length - 1) {
				sb.append(",");
			} 
		}
		return sb.toString();		
	}
    
    /**
     *  Returns the current time in the format yyyy-MM-dd hh: mm: ss.
     */
    public static Timestamp getCurrentTime() {
         SimpleDateFormat formatter = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss");
         Calendar cal = Calendar.getInstance();
         String today = formatter.format(cal.getTime());
         Timestamp ts = Timestamp.valueOf(today);
         
         return ts;
    }

    public static int[] RandomizeArray(int[] array){
		Random rgen = new Random();  // Random number generator			
 
		for (int i=0; i<array.length; i++) {
		    int randomPosition = rgen.nextInt(array.length);
		    int temp = array[i];
		    array[i] = array[randomPosition];
		    array[randomPosition] = temp;
		}
 
		return array;
	}
    public static String[] RandomizeArray(String[] array){
		Random rgen = new Random();  // Random number generator			
 
		for (int i=0; i<array.length; i++) {
		    int randomPosition = rgen.nextInt(array.length);
		    String temp = array[i];
		    array[i] = array[randomPosition];
		    array[randomPosition] = temp;
		}
 
		return array;
	}

    /**
     * XSS Prevention Processing.
     */
    public static String unscript(String data) {
        if (data == null || data.trim().equals("")) {
            return "";
        }
        
        String ret = data;
        
        ret = ret.replaceAll("<(S|s)(C|c)(R|r)(I|i)(P|p)(T|t)", "&lt;script");
        ret = ret.replaceAll("</(S|s)(C|c)(R|r)(I|i)(P|p)(T|t)", "&lt;/script");
        
        ret = ret.replaceAll("<(O|o)(B|b)(J|j)(E|e)(C|c)(T|t)", "&lt;object");
        ret = ret.replaceAll("</(O|o)(B|b)(J|j)(E|e)(C|c)(T|t)", "&lt;/object");
        
        ret = ret.replaceAll("<(A|a)(P|p)(P|p)(L|l)(E|e)(T|t)", "&lt;applet");
        ret = ret.replaceAll("</(A|a)(P|p)(P|p)(L|l)(E|e)(T|t)", "&lt;/applet");
        
        ret = ret.replaceAll("<(E|e)(M|m)(B|b)(E|e)(D|d)", "&lt;embed");
        ret = ret.replaceAll("</(E|e)(M|m)(B|b)(E|e)(D|d)", "&lt;embed");
        
        ret = ret.replaceAll("<(F|f)(O|o)(R|r)(M|m)", "&lt;form");
        ret = ret.replaceAll("</(F|f)(O|o)(R|r)(M|m)", "&lt;form");

        return ret;
    }
}