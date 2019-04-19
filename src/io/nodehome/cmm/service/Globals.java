package io.nodehome.cmm.service;

public class Globals {
	//OS type
    public static final String OS_TYPE = GlobalProperties.getProperty("Globals.OsType");
    //ShellFile Path
    public static final String SHELL_FILE_PATH = GlobalProperties.getPathProperty("Globals.ShellFilePath");

    //Original filename
	public static final String ORIGIN_FILE_NM = "originalFileName";
	//File extensions
	public static final String FILE_EXT = "fileExtension";
	//File size
	public static final String FILE_SIZE = "fileSize";
	//Uploaded file name
	public static final String UPLOAD_FILE_NM = "uploadFileName";
	//File path
	public static final String FILE_PATH = "filePath";



}
