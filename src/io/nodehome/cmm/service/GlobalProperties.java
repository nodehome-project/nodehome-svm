package io.nodehome.cmm.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.nodehome.cmm.util.ResourceCloseHelper;

public class GlobalProperties {

	private static final Logger LOGGER = LoggerFactory.getLogger(GlobalProperties.class);

	//File separator
	final static  String FILE_SEPARATOR = System.getProperty("file.separator");
	public static final String RELATIVE_PATH_PREFIX = GlobalProperties.class.getResource("").getPath().substring(0, GlobalProperties.class.getResource("").getPath().lastIndexOf("io"));
	public static final String GLOBALS_PROPERTIES_FILE = RELATIVE_PATH_PREFIX + "resources/props" + FILE_SEPARATOR + "globals.properties";

	public static String getProjectNet() {
		return getProperty2("project_net", "");
	}
	public static String getProjectApiHost() {
		return getProperty2("project_api_path", "");
	}
	
	/**
	 * Get the set value.
	 * 
	 * @param strKey
	 * @param strDefault
	 * @return strValue
	 */
	public static String getPropertyString(String strKey, String strDefault) {
		String rv = GlobalProperties.getProperty(strKey);
		if(rv==null || rv.equals(""))
			return strDefault;
		else 
			return rv;
	}

	/**
	 * 
	 * @param strKey
	 * @param strValue
	 * @return
	 */
	public static void setPropertyString(String strKey, String strValue) {
		Properties props = new Properties();
		props.setProperty(strKey, strValue);
	}

	public static void setPropertyInt(String strKey, int nValue) {
		Properties props = new Properties();
		props.setProperty(strKey, Integer.toString(nValue));
	}

	public static boolean getPropertyBool(String strKey, boolean bDefault) {
		String strValue = getPropertyString(strKey, bDefault ? "true" : "false");
		return strValue.equalsIgnoreCase("true");
	}

	public static void setPropertyBool(String strKey, boolean bValue) {
		Properties props = new Properties();		
		props.setProperty(strKey, bValue ? "true" : "false");
	}
	
	/**
	 * Returns the relative path property value as an absolute path
	 * @param keyName String
	 * @return String
	 */
	public static String getPathProperty(String keyName) {
		String value = "";
		
		LOGGER.debug("getPathProperty : {} = {}", GLOBALS_PROPERTIES_FILE, keyName);
		
		FileInputStream fis = null;
		try {
			Properties props = new Properties();
			
			fis = new FileInputStream(filePathBlackList(GLOBALS_PROPERTIES_FILE));
			props.load(new BufferedInputStream(fis));
			
			value = props.getProperty(keyName).trim();
			value = RELATIVE_PATH_PREFIX + "resources/props" + System.getProperty("file.separator") + value;
		} catch (FileNotFoundException fne) {
			LOGGER.debug("Property file not found.", fne);
			throw new RuntimeException("Property file not found", fne);
		} catch (IOException ioe) {
			LOGGER.debug("Property file IO exception", ioe);
			throw new RuntimeException("Property file IO exception", ioe);
		} finally {
			ResourceCloseHelper.close(fis);
		}
		
		return value;
	}

	/**
	 * Returns the property value
	 * @param keyName String
	 * @return String
	 */
	public static String getProperty(String keyName) {
		String value = "";
		
		LOGGER.debug("getProperty : {} = {}", GLOBALS_PROPERTIES_FILE, keyName);
		
		FileInputStream fis = null;
		try {
			Properties props = new Properties();
			
			fis = new FileInputStream(filePathBlackList(GLOBALS_PROPERTIES_FILE));
			
			props.load(new BufferedInputStream(fis));
			if (props.getProperty(keyName) == null) {
				return "";
			}
			value = props.getProperty(keyName).trim();
		} catch (FileNotFoundException fne) {
			LOGGER.debug("Property file not found.", fne);
			throw new RuntimeException("Property file not found", fne);
		} catch (IOException ioe) {
			LOGGER.debug("Property file IO exception", ioe);
			throw new RuntimeException("Property file IO exception", ioe);
		} finally {
			ResourceCloseHelper.close(fis);
		}
		
		return value;
	}
	public static String getProperty2(String keyName, String defaultValue) {
		String value = "";
		
		LOGGER.debug("getProperty : {} = {}", GLOBALS_PROPERTIES_FILE, keyName);
		
		FileInputStream fis = null;
		try {
			Properties props = new Properties();
			fis = new FileInputStream(filePathBlackList(GLOBALS_PROPERTIES_FILE));
			
			props.load(new BufferedInputStream(fis));
			if (props.getProperty(keyName) == null) {
				return defaultValue;
			}
			value = props.getProperty(keyName).trim();
		} catch (FileNotFoundException fne) {
			LOGGER.debug("Property file not found.", fne);
			throw new RuntimeException("Property file not found", fne);
		} catch (IOException ioe) {
			LOGGER.debug("Property file IO exception", ioe);
			throw new RuntimeException("Property file IO exception", ioe);
		} finally {
			ResourceCloseHelper.close(fis);
		}
		
		return value;
	}

	/**
	 * Returns the property value
	 * @param fileName String
	 * @param key String
	 * @return String
	 */
	public static String getPathProperty(String fileName, String key) {
		FileInputStream fis = null;
		try {
			Properties props = new Properties();
			
			fis = new FileInputStream(filePathBlackList(fileName));
			props.load(new BufferedInputStream(fis));
			fis.close();

			String value = props.getProperty(key);
			value = RELATIVE_PATH_PREFIX + "resources/props" + System.getProperty("file.separator") + value;
			
			return value;
		} catch (FileNotFoundException fne) {
			LOGGER.debug("Property file not found.", fne);
			throw new RuntimeException("Property file not found", fne);
		} catch (IOException ioe) {
			LOGGER.debug("Property file IO exception", ioe);
			throw new RuntimeException("Property file IO exception", ioe);
		} finally {
			ResourceCloseHelper.close(fis);
		}
	}

	public static String getProperty(String fileName, String key) {
		FileInputStream fis = null;
		try {
			Properties props = new Properties();
			
			fis = new FileInputStream(filePathBlackList(fileName));
			props.load(new BufferedInputStream(fis));
			fis.close();

			String value = props.getProperty(key);
			
			return value;
		} catch (FileNotFoundException fne) {
			LOGGER.debug("Property file not found.", fne);
			throw new RuntimeException("Property file not found", fne);
		} catch (IOException ioe) {
			LOGGER.debug("Property file IO exception", ioe);
			throw new RuntimeException("Property file IO exception", ioe);
		} finally {
			ResourceCloseHelper.close(fis);
		}
	}

	/**
	 * Parses the contents of a given profile (key-value) and returns a struct array of the type.
	 * @param property String
	 * @return ArrayList
	 */
	public static ArrayList<Map<String, String>> loadPropertyFile(String property) {
		ArrayList<Map<String, String>> keyList = new ArrayList<Map<String, String>>();

		String src = property.replace('\\', File.separatorChar).replace('/', File.separatorChar);
		FileInputStream fis = null;
		try {

			File srcFile = new File(filePathBlackList(src));
			if (srcFile.exists()) {

				Properties props = new Properties();
				fis = new FileInputStream(src);
				props.load(new BufferedInputStream(fis));
				fis.close();

				Enumeration<?> plist = props.propertyNames();
				if (plist != null) {
					while (plist.hasMoreElements()) {
						Map<String, String> map = new HashMap<String, String>();
						String key = (String) plist.nextElement();
						map.put(key, props.getProperty(key));
						keyList.add(map);
					}
				}
			}
		} catch (IOException ex) {
			LOGGER.debug("IO Exception", ex);
			throw new RuntimeException(ex);
		} finally {
			ResourceCloseHelper.close(fis);
		}

		return keyList;
	}

	private static String filePathBlackList(String value) {
		String returnValue = value;
		if (returnValue == null || returnValue.trim().equals("")) {
			return "";
		}

		returnValue = returnValue.replaceAll("\\.\\./", ""); // ../
		returnValue = returnValue.replaceAll("\\.\\.\\\\", ""); // ..\

		return returnValue;
	}
}
