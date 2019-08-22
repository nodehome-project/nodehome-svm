package io.nodehome.svm.common.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.StringTokenizer;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
public class EtcUtils {
	
	public static int findChar(StringBuffer strbBuf, char chFind, boolean bIgnoreCase, int nStartPos)
	{
		int nEffectChars = strbBuf.length();
		int nIdx;
		if (nStartPos < 0) nStartPos = 0;
		if (bIgnoreCase == false)
		{
			for (nIdx = nStartPos; nIdx < nEffectChars; nIdx++)
			{
				if (strbBuf.charAt(nIdx) == chFind)
				{
					return nIdx;
				}
			}
		}
		else
		{
			chFind = Character.toUpperCase(chFind);
			for (nIdx = nStartPos; nIdx < nEffectChars; nIdx++)
			{
				if (Character.toUpperCase(strbBuf.charAt(nIdx)) == chFind)
				{
						return nIdx;
				}
			}

		}
		return -1;
	}
	
	public static int findStr(StringBuffer strbBuf, String strFind, boolean bIgnoreCase, int nStartPos)
	{
		if (strFind == null || strFind.length() == 0) return -1;
		int nEffectChars = strbBuf.length();
		int nIdx, nFindIdx, nFindLen;
		boolean bFound = false;
		char chFirst;
		if (nStartPos < 0) nStartPos = 0;
		chFirst = strFind.charAt(0);
		nFindLen = strFind.length();
		if (bIgnoreCase == false)
		{
			for (nIdx = nStartPos; nIdx < nEffectChars; nIdx++)
			{
				if (strbBuf.charAt(nIdx) == chFirst)
				{
					if (nIdx + nFindLen < nEffectChars)
					{
						bFound = true;
						for (nFindIdx = 1; nFindIdx < nFindLen; nFindIdx++)
						{
							if (strbBuf.charAt(nIdx + nFindIdx) != strFind.charAt(nFindIdx))
							{
								bFound = false;
								break;
							}
						}
						if (bFound) return nIdx;
					}
				}
			}
		}
		else
		{
			chFirst = Character.toUpperCase(chFirst);
			String strUpperFind = strFind.toUpperCase();
			for (nIdx = nStartPos; nIdx < nEffectChars; nIdx++)
			{
				if (Character.toUpperCase(strbBuf.charAt(nIdx)) == chFirst)
				{
					if (nIdx + nFindLen <= nEffectChars)
					{
						bFound = true;
						for (nFindIdx = 1; nFindIdx < nFindLen; nFindIdx++)
						{
							if (Character.toUpperCase(strbBuf.charAt(nIdx + nFindIdx)) != strUpperFind.charAt(nFindIdx))
							{
								bFound = false;
								break;
							}
						}
						if (bFound) return nIdx;
					}
				}
			}

		}
		return -1;
	}

	public static StringBuffer replaceTokenAll(StringBuffer strbBuf, String strOldToken, String strNewToken,
			boolean bIgnoreCase, int nStartPos)
	{
		int nOldTokenLen = strOldToken.length();
		int nNewTokenLen = strNewToken.length();

		while ((nStartPos = findStr(strbBuf, strOldToken, bIgnoreCase, nStartPos)) != -1)
		{
			strbBuf = strbBuf.replace(nStartPos, nStartPos + nOldTokenLen, strNewToken);
			nStartPos += nNewTokenLen;
		}
		return strbBuf;
	}
	
	public static java.util.Date getJavaDate(ResultSet rs,String strDateFieldName) throws SQLException
	{
		String strDate;
		strDate = rs.getString(strDateFieldName);
		return getJavaDate(strDate);
	}
	
	@SuppressWarnings("deprecation")
	public static java.util.Date getJavaDate(String strDate)
	{
		if(strDate == null)
			return null;
		String[] astrField = splitString(strDate,"-:/. ",true,false);
		if(astrField.length < 3)
			return null;
		
		int nY,nM,nD;
		nY = Integer.parseInt(astrField[0]) - 1900;
		nM = Integer.parseInt(astrField[1]) - 1;
		nD = Integer.parseInt(astrField[2]);

		int nH,nMin,nS;
		nH = 0; 
		nMin = 0;
		nS = 0;
		if(astrField.length >= 6)
		{
			nH = Integer.parseInt(astrField[3]);
			nMin = Integer.parseInt(astrField[4]);
			nS = Integer.parseInt(astrField[5]);
		}
		return new java.util.Date(nY,nM,nD,nH,nMin,nS);
	}
	
	@SuppressWarnings("deprecation")
	public static java.util.Date getJavaDate02(String strDate)
	{
		if(strDate == null)
			return null;
		
		int nY = 0;
		int nM = 0;
		int nD = 1;
		if(strDate.length() >= 4) 
			nY = Integer.parseInt(strDate.substring(0, 4)) - 1900;
		if(strDate.length() >= 6) 
			nM = Integer.parseInt(strDate.substring(4, 6)) - 1;
		if(strDate.length() >= 8)
			nD = Integer.parseInt(strDate.substring(6, 8));

		int nH,nMin,nS;
		nH = 0; 
		nMin = 0;
		nS = 0;
		if(strDate.length() >= 10)
			nH = Integer.parseInt(strDate.substring(8, 10));
		if(strDate.length() >= 12)
			nMin = Integer.parseInt(strDate.substring(10, 12));
		if(strDate.length() >= 14)
			nS = Integer.parseInt(strDate.substring(12, 14));

		return new java.util.Date(nY,nM,nD,nH,nMin,nS);
	}
	
	public static String getStrDate(java.util.Date date,String strFormat)
	{
		if(date == null)
			return null;
		SimpleDateFormat sf = new SimpleDateFormat(strFormat);
		return sf.format(date);
	}

	public static String getStrDate(java.util.Date date,String strFormat,String strDefault)
	{
		if(date == null)
			return strDefault;
		SimpleDateFormat sf = new SimpleDateFormat(strFormat);
		return sf.format(date);
	}

	public static String getSqlDate(java.util.Date date,String strDBType)
	{
		if(date == null)
			return "null";
		String strDate = EtcUtils.getStrDate(date,"yyyy-MM-dd HH:mm:ss");
		String strSqlDate = "";
		if(strDBType.equals("oracle"))
			strSqlDate = "to_date('" + strDate + "','yyyy-MM-dd HH24:mi:ss')";
		else if(strDBType.equals("mssql"))
			strSqlDate = "convert(DATETIME,'" + strDate + "')";
		else
			strSqlDate = "'" + strDate + "'";
		return strSqlDate;
	}
	
	public static java.util.Date addDateDay(java.util.Date date,int nDay,boolean bClearHHmmss)
	{
		if(date == null)
			return null;
		Calendar calTest = Calendar.getInstance();
		calTest.setTime(date);
		calTest.add(Calendar.DATE, nDay);
		if(bClearHHmmss)
		{
			calTest.set(Calendar.HOUR,0);
			calTest.set(Calendar.MINUTE,0);
			calTest.set(Calendar.SECOND,0);
		}
		return calTest.getTime();
	}

	public static java.util.Date addDateMonth(java.util.Date date,int nMonth,boolean bClearHHmmss)
	{
		if(date == null)
			return null;
		Calendar calTest = Calendar.getInstance();
		calTest.setTime(date);
		calTest.add(Calendar.MONTH, nMonth);
		if(bClearHHmmss)
		{
			calTest.set(Calendar.HOUR,0);
			calTest.set(Calendar.MINUTE,0);
			calTest.set(Calendar.SECOND,0);
		}
		return calTest.getTime();
	}	

	public static String CheckS(String strData,int nMaxLen,String strNull)
	{
		if(strData == null)
			return strNull;
		strData = strData.trim();
		if(strData.length() > nMaxLen)
			strData =  strData.substring(0,nMaxLen);
		return strData;
	}

	public static String NullH(String strText)
	{
		if(strText == null)
			return "";

		boolean bFound = false;
		if(strText.indexOf('&') >= 0 || strText.indexOf('<')  >= 0 
				|| strText.indexOf('>')  >= 0 || strText.indexOf("'")  >= 0 || strText.indexOf("\"")  >= 0 )
		{
			bFound = true;
		}
		if(!bFound)
			return strText;

	    StringBuffer strbText = new StringBuffer();
	    strbText.append(strText);
	    EtcUtils.replaceTokenAll(strbText,"&","&amp;",false,0);
	    EtcUtils.replaceTokenAll(strbText,">","&gt;",false,0);
	    EtcUtils.replaceTokenAll(strbText,"<","&lt;",false,0);
	    EtcUtils.replaceTokenAll(strbText,"'","&apos;",false,0);
	    EtcUtils.replaceTokenAll(strbText,"\"","&quot;",false,0);
		return strbText.toString();
	}

	public static String[] fromJSONArrayString(JSONArray jaItem)
	{
		String[]  aryItem = new String[jaItem.size()];
		for(int i = 0; i  < jaItem.size(); i ++)
		{
			aryItem[i] = (String)jaItem.get(i);
		}
		return aryItem;
	}
	
	public static boolean  checkJSONFieldEmpty(JSONObject  joChk,String[] aryNames)
	{
		String strTemp = null;
		for(String strName : aryNames)
		{
			strTemp = (String)joChk.get(strName);
			if(strTemp.isEmpty())
				return false;
		}
		return true;
	}

	public static int parserInt(String strInt,int nDefault)
	{
		if(strInt == null)
			return nDefault;
		
		strInt = strInt.trim();
		int nStart = 0;
		int nEnd = -1;
		int nBase = 10;
		
		if(strInt.startsWith("0x") || strInt.startsWith("0X"))
		{
			nStart = 2;
			nBase = 16;
		}

		for(int i = nStart; i < strInt.length(); i++)
		{
			if(!Character.isDigit(strInt.charAt(i)))
				break;
			nEnd = i;
		}
		
		if(nEnd < nStart)
			return nDefault;
		try
		{
			return Integer.parseInt(strInt.substring(nStart, nEnd+1),nBase);
		}
		catch(NumberFormatException e)
		{
			;
		}
		return nDefault;
	}
	
	static public String getFilepath(String strOrgFile)
	{
		int nDotPos = strOrgFile.lastIndexOf('.');
		int nSepPos = strOrgFile.lastIndexOf(File.separatorChar);
        if(nSepPos < 0)
            return "";
		if(nDotPos < 0 || nSepPos > nDotPos)
		{
			return strOrgFile;
		}
		return strOrgFile.substring(0,nSepPos);
	}
	static public String getFilename(String strOrgFile)
	{
		int nSepPos = strOrgFile.lastIndexOf(File.separatorChar);
		if(nSepPos >= 0)
		{
				return strOrgFile.substring(nSepPos+1,strOrgFile.length());
		}
		return strOrgFile;
	}
	static public String getFilenameOnly(String strOrgFile)
	{
		int nDotPos = strOrgFile.lastIndexOf('.');	
		int nSepPos = strOrgFile.lastIndexOf(File.separatorChar);
		if(nDotPos < 0 || nSepPos > nDotPos)
		{
			return strOrgFile.substring(nSepPos+1,strOrgFile.length());
		}
		else
		{
			return strOrgFile.substring(nSepPos+1,nDotPos);
		}
	}
	static public String getFileExt(String strOrgFile)
	{
		int nDotPos = strOrgFile.lastIndexOf('.');
		int nSepPos = strOrgFile.lastIndexOf(File.separatorChar);
		if(nDotPos < 0 || nSepPos > nDotPos)
		{
			return "";
		}
		return strOrgFile.substring(nDotPos+1,strOrgFile.length());
	}	
	
	static public String getUniqFilename(String strOrgFile)
	{
		File fileContent = new File(strOrgFile);
		if(!fileContent.exists())
			return strOrgFile;

		String strNewPureFilename = null;
		int i = 1;
		String strPurePath = 	getFilepath(strOrgFile);
		String strPureFilename = 	getFilenameOnly(strOrgFile);	
		String strFileExt = 	getFileExt(strOrgFile);	

		strNewPureFilename = String.format("%s(%d)", strPureFilename,i++);	
		String strNewFilename = 	strPurePath + File.separator + strNewPureFilename + "." + strFileExt;
		fileContent = new File(strNewFilename);
		while(fileContent.exists())
		{
			strNewPureFilename = String.format("%s(%d)", strPureFilename,i++);	
			strNewFilename = 	strPurePath + File.separator + strNewPureFilename + "." + strFileExt;
			fileContent = new File(strNewFilename);
		}
		return strNewFilename;
	}
	public static byte[] readFile(String strFilename)
	{
		File filePath = new File(strFilename);
		FileInputStream inputStream;
		byte[] bytRet = null;
		try
		{
			inputStream = new FileInputStream(filePath);
			bytRet =  readFile(inputStream);
			inputStream.close();
		}
		catch (FileNotFoundException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bytRet;
	}
	
	public static byte[] readFile(InputStream isFile)
	{
		ByteArrayOutputStream bytearray = new ByteArrayOutputStream();
		try
		{
			byte[] body = new byte[1024];
			int i = 0;
			while ((i = isFile.read(body)) > 0)
			{
				bytearray.write(body, 0, i);
			}
			bytearray.flush();

		}
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		byte[] rtn = bytearray.toByteArray();

		if (rtn.length > 3)
		{
			if ((byte) rtn[0] == (byte) 0xef && (byte) rtn[1] == (byte) 0xbb && (byte) rtn[2] == (byte) 0xbf)
			{
				bytearray.reset();
				// utf-8
				bytearray.write(rtn, 3, rtn.length - 3);
			}
		}
		return bytearray.toByteArray();
	}
	
	 static public void sendResponseData( HttpServletResponse response , byte[] imgContentsArray ) 
	 {       
	     ServletOutputStream  svrOut = null ;   BufferedOutputStream outStream = null ;
	      try
	      {                  
	          svrOut = response.getOutputStream(); 
	          outStream =  new BufferedOutputStream( svrOut );                   
	          outStream.write(  imgContentsArray, 0, imgContentsArray.length );     
	          outStream.flush();                      
	      } 
	      catch( Exception writeException ) 
	      {
	          writeException.printStackTrace();
	      }
	      finally 
	      {
	           try 
	           {            
	              if ( outStream != null ) 
	             	 outStream.close(); 
	           } 
	           catch( Exception closeException ) 
	           {
	             closeException.printStackTrace();
	           }   
	        }
	   }
	static public String getYesNo(boolean bAns)
	{
		return bAns?"Yes":"No";
	}
	static public boolean getBool(String strAns)
	{
		if(strAns == null)
			return false;
		if(strAns.equalsIgnoreCase("true"))
			return true;
		if(strAns.equalsIgnoreCase("false"))
			return false;
		
		if(strAns.equalsIgnoreCase("Yes"))
			return true;
		if(strAns.equalsIgnoreCase("No"))
			return false;
		
		return false;
	}
	public static String[] splitString(String strMsg, String strDelim, boolean btrimToken, boolean bIncludeDelim)
	{
		StringTokenizer tokMsg = new StringTokenizer(strMsg.trim(), strDelim, true);
		ArrayList<String> aStr = new ArrayList<String>();
		String strTok;
		boolean bPreTokIsDelim = true;
		while (tokMsg.hasMoreTokens())
		{
			strTok = tokMsg.nextToken();
			if (strDelim.indexOf(strTok) >= 0)
			{
				if (bPreTokIsDelim) aStr.add("");
				if (bIncludeDelim) aStr.add(strTok);
				bPreTokIsDelim = true;
			}
			else
			{
				if (btrimToken) aStr.add(strTok.trim());
				else aStr.add(strTok);
				bPreTokIsDelim = false;
			}
		}
		if (bPreTokIsDelim) aStr.add("");
		String[] aTokens = new String[aStr.size()];
		aTokens = aStr.toArray(aTokens);
		return aTokens;
	}
	
	public static byte[] requestPost(String strUrl,String strContent,String strContentType)
	{
		// -------------------
		byte[] bytReturn = null;
		HttpClient client = new HttpClient();
		PostMethod method = new PostMethod(strUrl);
		if(strContentType != null)
		{
			if(!strContentType.isEmpty())
				method.setRequestHeader("Content-type", strContentType);
		}
		
		method.setRequestBody(strContent);

		try
		{
			System.out.println(strContent);
			int statusCode = client.executeMethod(method);

			if (statusCode == HttpStatus.SC_OK)
			{
				bytReturn = method.getResponseBody();
			}
			else
			{
				System.out.println("(IPHONE) Push FAIL(" + statusCode + ")");
			}
		}
		catch (HttpException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		finally
		{
			if (method != null)
				method.releaseConnection();
		}
		return bytReturn;
	}

}
