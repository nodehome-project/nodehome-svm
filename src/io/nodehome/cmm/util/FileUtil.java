package io.nodehome.cmm.util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.PixelGrabber;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.ImageOutputStream;
import javax.swing.ImageIcon;

import io.nodehome.cmm.service.GlobalProperties;
import io.nodehome.svm.common.util.StringUtil;

public class FileUtil {

    public static void makeDir(String savedir) throws Exception{
    	java.io.File dir=new java.io.File(savedir);
    	if(!dir.exists()){	dir.mkdirs();	}
    }

    public static void copyFileAbs(java.io.File src, java.io.File dst) throws java.io.IOException {    	
    	java.io.InputStream in = new java.io.FileInputStream(src);
    	java.io.OutputStream out = new java.io.FileOutputStream(dst);
    	
    	byte[] buf = new byte[1024];
    	int len;
    	while ((len = in.read(buf)) > 0) {
    		out.write(buf, 0, len);
    	}
    	in.close();
    	out.close();
    }

	/**
	 * Import HTML content from the web
	 */
	public static String roadWebHtmlFile(String url) {
		String readHtml = "";
		URL htmlUrl;
		try {
			htmlUrl = new URL(url);
			BufferedReader in = new BufferedReader(new InputStreamReader(htmlUrl.openStream(), "UTF-8"));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				readHtml += inputLine;
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return readHtml;
	}

	/**
	 * Read Local Path file
	 */
	public static String roadLocalFile(String filepath) {
		String readFile = "";
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(filepath), "UTF-8"));
			String s;
			while ((s = in.readLine()) != null) {
				readFile += s+"\n";
			}
			in.close();
		} catch (IOException e) {
			System.err.println(e); // error message output
			System.exit(1);
		}

		return readFile;
	}
	
	public static String read (File p_fd) {
        int nCode = 0;
        StringBuffer szRetText = new StringBuffer(8192);

        try {
            if (p_fd.exists() == true) {
                FileInputStream v_fis = new FileInputStream (p_fd);

                while (nCode != -1) {
                    nCode = v_fis.read();

                    if (nCode != -1)
                        szRetText.append ((char) nCode);
                };
                v_fis.close();
            }
        }
        catch (Exception e) { e.printStackTrace(); }

        return StringUtil.asc2ksc(szRetText.toString ());
    }

    /**
     * If there is an attachment, delete it.
     */
    public static void fileDelete(String fileName, String fileDir) {
    	if(fileName != null) {
            File f = new File(fileName);
            if(f.delete()) {
                System.out.println("\uC0AD\uC81C \uB418\uC5C8\uC2B5\uB2C8\uB2E4.!!");
            } else {
                System.out.println("\uC0AD\uC81C \uB418\uC9C0 \uC54A\uC558\uC2B5\uB2C8\uB2E4.!!");
            }	
    	}    
        
    	if(fileDir != null) {
            File fDir = new File(fileDir);
            if(fDir.exists()) {
                System.out.println("\uB514\uB809\uD1A0\uB9AC \uC874\uC7AC!!");
                if(fDir.delete())
                    System.out.println("\uC0AD\uC81C \uB418\uC5C8\uC2B5\uB2C8\uB2E4.!!");
                else
                    System.out.println("\uC0AD\uC81C \uB418\uC9C0 \uC54A\uC558\uC2B5\uB2C8\uB2E4.!!");
            }	
    	}
    }
    
	/**
	 * Returns the file name excluding the extension of the file.
	 */
	public static String getFileNameWithoutExtension(String fileName){
		String fileNameWihtoutExtension = null;
		if(fileName==null || fileName.trim().equals("")){
			fileNameWihtoutExtension = null;
		}else{
			int startFileNameIndex = fileName.lastIndexOf("/")==-1?fileName.lastIndexOf("\\") + 1 : fileName.lastIndexOf("/") + 1;
			fileNameWihtoutExtension = fileName.substring(startFileNameIndex, fileName.lastIndexOf("."));
		}

		return fileNameWihtoutExtension;
	}

	/**
	 * Returns the extension of the file.
	 */
	public static String getExtension(String fileName){
		String extension = null;
		if(fileName==null || fileName.trim().equals("")){
			extension = null;
		}else{
			String temps[] = fileName.split("\\.");
			extension = temps[temps.length-1];
		}

		return extension;
	}

	/**
	 * parse full path name
	 * @return String[0] : path
	 *         String[1] : fileName
	 *         String[2] : extension
	 */
	public static String[] getFileInformation(String fileFullPathName){
		if(fileFullPathName==null || fileFullPathName.trim().equals("")){
			return null;
		}

		File f = new File(fileFullPathName);
		String fileName = f.getName();
		String path = f.getPath();
		String extension = null;
		if(fileName==null || fileName.trim().equals("")){
			extension = null;
		}else{
			String temps[] = fileName.split("\\.");
			extension = temps[temps.length-1];
		}

		return new String[]{path, fileName, extension};
	}

	public static String appendChild(String pDirectory, String pChild){
		String directory = pDirectory;
		String child = pChild;

		if(directory==null || directory.trim().equals("")) return child;
		if(child==null || child.trim().equals("")) return directory;

		directory = directory.trim();
		child = child.trim();

		int separatorCount = 0;
		if(directory.endsWith(File.separator)) separatorCount++;
		if(child.startsWith(File.separator)) separatorCount++;

		if(separatorCount==0) return directory + File.separator + child;
		else if(separatorCount==1) return directory + child;

		child = child.substring(File.separator.length());
		return directory + child;
	}

	/**
	 * To prevent file name duplication, convert the file name to "filename_yyyyMMddhhmmss" format.
	 * @param pPrefix 	File name without extension
	 */
	public static String getUniqueName(String pPrefix){
		String prefix = pPrefix;
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmss");
		String dateTime = formatter.format(new Date());
		String currentTimeMillis = ""+System.currentTimeMillis();
		String milliSecond = currentTimeMillis.substring(currentTimeMillis.length()-3);

		if(prefix==null) prefix = "";
		String uniqueName = prefix.trim() + "_" + dateTime + milliSecond;

		return uniqueName;
	}

	/**
	 * To prevent file name duplication, convert the file name to "filename_yyyyMMddhhmmss" format.
	 * @param pPrefix 	File name without extension
	 */
	public static String getUniqueFileName(int idx){
		SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddhhmmss");
		String dateTime = formatter.format(new Date());


		String uniqueName = dateTime + generateKey(idx+1);

		return uniqueName;
	}

	public static String generateKey(int seq){
		DecimalFormat decimalFormat = new DecimalFormat("000");
		String currentTimeMillis = ""+System.currentTimeMillis();
		String milliSecond = currentTimeMillis.substring(currentTimeMillis.length()-3);
		return milliSecond + decimalFormat.format(seq);
	}


	/**
	 * Write a string to the file using the system's default encoding.
	 */
	public static void writeFile(String filePath, boolean append, String content) throws Exception{
		BufferedWriter writer = null;
		try{
			FileWriter fWriter = new FileWriter(filePath, append);
			writer = new BufferedWriter(fWriter);

			writer.write(content);
		}catch(IOException ioe){
			throw new Exception("There was an error writing the given contents to the file.", ioe);
		}finally{
			try{
				writer.flush();
			}catch(Exception ignore){}
			try{
				writer.close();
			}catch(Exception ignore){}
		}
	}

	/**
	 * replace file extension<br>
	 * <br>
	 * replaceFileExtension("C:\\a\\b\\c\\mytext.bmp", "png") ==> "C:\\a\\b\\c\\mytext.png"<br>
	 * replaceFileExtension("C:\\a\\b\\c\\mytext", "png") ==> "C:\\a\\b\\c\\mytext.png"<br>
	 * replaceFileExtension("C:\\a\\b\\c\\mytext.", "png") ==> "C:\\a\\b\\c\\mytext.png"<br>
	 * @param fileName
	 * @param newExtension
	 */
	public static String replaceFileExtension(String pFileName, String newExtension){
		String fileName = pFileName;
		if(fileName==null || fileName.trim().equals(""))
			return fileName;

		fileName = fileName.trim();
		int sepPos = fileName.indexOf('.');
		if(sepPos<0)
			return fileName + "." + newExtension;

		return fileName.substring(0, sepPos) + "." + newExtension;
	}

	/**
	 * Append the suffix given before the filename extension.
	 *
	 * addSuffixFilename("C:\\a\\b\\c\\mytext.bmp", "_small")) ==> "C:\\a\\b\\c\\mytext_small.bmp"
	 * addSuffixFilename("C:\\a\\b\\c\\mytext", "_small")); ==> "C:\\a\\b\\c\\mytext_small"
	 * addSuffixFilename("C:\\a\\b\\c\\mytext.", "_small")); ==> "C:\\a\\b\\c\\mytext_small."
	 */
	public static String addSuffixFilename(String pFileName, String suffix){
		String fileName = pFileName;
		if(fileName==null || fileName.trim().equals(""))
			return fileName;

		if(suffix==null || suffix.equals("")){
			return fileName;
		}

		fileName = fileName.trim();
		int sepPos = fileName.indexOf('.');
		if(sepPos<0)
			return fileName + suffix;

		return fileName.substring(0, sepPos) + suffix + fileName.substring(sepPos);
	}



	/**
	 * Writes a string to the file using the specified Encoding.
	 */
	public static void writeFile(String filePath, String encoding, boolean append, String content) throws Exception{
		FileOutputStream fos = null;
//		OutputStreamWriter oStream = null;

		try{
//			fos = new FileOutputStream(filePath, append);
//			oStream = new OutputStreamWriter(fos, encoding);
//			oStream.write(content);
//			oStream.flush();
//			oStream.close();
			
			BufferedWriter output = null;

			File file = new File(filePath);
			file.setReadable(true, false);
			file.setExecutable(true, false);
			file.setWritable(true, false);
            output = new BufferedWriter(new FileWriter(file));
            output.write(content);
            output.close();
		}catch(UnsupportedEncodingException uee){
			throw new Exception("Unable to encode", uee);
		}catch(IOException ioe){
			throw new Exception("An error has occurred", ioe);
		}finally{
			if(fos != null){
				fos.close();
			}
		}
	}

	public static boolean isImageFile(String fileName){
		String extension = getExtension(fileName);
		boolean isImageFile = false;

		if("GIF".equalsIgnoreCase(extension)){
			isImageFile = true;
		}else if("JPG".equalsIgnoreCase(extension)){
			isImageFile = true; // Exif Formats (JPG)
		}else if("BMP".equalsIgnoreCase(extension)){
			isImageFile = true; // BMP Formats (BMP)
		}else if("JPEG".equalsIgnoreCase(extension)){
			isImageFile = true; // Exif Formats (JPEG)
		}else if("TIFF".equalsIgnoreCase(extension)){
			isImageFile = true; // Exif Formats (TIFF)
		}else if("TIF".equalsIgnoreCase(extension)){
			isImageFile = true; // Exif Formats (TIF)
		}else if("PNG".equalsIgnoreCase(extension)){
			isImageFile = true; // Portable Network Graphics Format (PNG)
		}else if("PSD".equalsIgnoreCase(extension)){
			isImageFile = true; // PhotoShop 3.0 Format (PSD)
		}else if("AI".equalsIgnoreCase(extension)){
			isImageFile = true; // Illusterator Format (AI)
		}else if("PCX".equalsIgnoreCase(extension)){
			isImageFile = true; // PCX Formats (PCX)
		}else if("DCX".equalsIgnoreCase(extension)){
			isImageFile = true; // PCX Formats (DCX)
		}else if("AFP".equalsIgnoreCase(extension)){
			isImageFile = true; // AFP Format (AFP)
		}else if("CGM".equalsIgnoreCase(extension)){
			isImageFile = true; // Computer Graphics Metafile (CGM)
		}else if("CMX".equalsIgnoreCase(extension)){
			isImageFile = true; // Corel Presentation Exchange Format (CMX)
		}else if("DGN".equalsIgnoreCase(extension)){
			isImageFile = true; // DGN (DGN)
		}else if("DIC".equalsIgnoreCase(extension)){
			isImageFile = true; // DICOM Format (DIC)
		}else if("CUT".equalsIgnoreCase(extension)){
			isImageFile = true; // Dr. Halo (CUT)
		}else if("DRW".equalsIgnoreCase(extension)){
			isImageFile = true; // DRaWing (DRW)
		}else if("DXF".equalsIgnoreCase(extension)){
			isImageFile = true; // Drawing Interchange Format (DXF)
		}else if("DWF".equalsIgnoreCase(extension)){
			isImageFile = true; // DWF Format (DWF)
		}else if("DWG".equalsIgnoreCase(extension)){
			isImageFile = true; // DWG Format (DWG)
		}else if("EPS".equalsIgnoreCase(extension)){
			isImageFile = true; // Encapsulated PostScript (EPS)
		}else if("SHP".equalsIgnoreCase(extension)){
			isImageFile = true; // ESRI Shape Format (SHP)
		}else if("FLC".equalsIgnoreCase(extension)){
			isImageFile = true; // Flic Animation (FLC)
		}else if("ICO".equalsIgnoreCase(extension)){
			isImageFile = true; // Icons and Cursors (ICO)
		}else if("CUR".equalsIgnoreCase(extension)){
			isImageFile = true; // Icons and Cursors (CUR)
		}else if("IFF".equalsIgnoreCase(extension)){
			isImageFile = true; // Interchange File Formats (IFF)
		}else if("JBG".equalsIgnoreCase(extension)){
			isImageFile = true; // JBIG Format (JBG)
		}else if("CMP".equalsIgnoreCase(extension)){
			isImageFile = true; // JPEG and LEAD Compressed (CMP)
		}else if("FPX".equalsIgnoreCase(extension)){
			isImageFile = true; // Kodak Formats (FPX)
		}else if("PCD".equalsIgnoreCase(extension)){
			isImageFile = true; // Kodak Formats (PCD)
		}else if("PCT".equalsIgnoreCase(extension)){
			isImageFile = true; // Macintosh Pict Format (PCT)
		}else if("CLP".equalsIgnoreCase(extension)){
			isImageFile = true; // Microsoft Windows Clipboard (CLP)
		}else if("PLT".equalsIgnoreCase(extension)){
			isImageFile = true; // PLT (PLT)
		}else if("PBM".equalsIgnoreCase(extension)){
			isImageFile = true; // Portable Bitmap Utilities (PBM)
		}else if("PS".equalsIgnoreCase(extension)){
			isImageFile = true; // PostScript Document Format (PS)
		}else if("PCL".equalsIgnoreCase(extension)){
			isImageFile = true; // Printer Command Language Format (PCL)
		}else if("PTOCA".equalsIgnoreCase(extension)){
			isImageFile = true; // PTOCA Format (PTOCA)
		}else if("SVG".equalsIgnoreCase(extension)){
			isImageFile = true; // Scalable Vector Graphics Format (SVG)
		}else if("SCT".equalsIgnoreCase(extension)){
			isImageFile = true; // Scitex Continuous Tone Format (SCT)
		}else if("SGI".equalsIgnoreCase(extension)){
			isImageFile = true; // Silicon Graphics Image Format (SGI)
		}else if("SMP".equalsIgnoreCase(extension)){
			isImageFile = true; // SMP Format (SMP)
		}else if("RAS".equalsIgnoreCase(extension)){
			isImageFile = true; // SUN Raster Format (RAS)
		}else if("TGA".equalsIgnoreCase(extension)){
			isImageFile = true; // Truevision TARGA Format (TGA)
		}else if("XPM".equalsIgnoreCase(extension)){
			isImageFile = true; // XPicMap (XPM)
		}else if("XWD".equalsIgnoreCase(extension)){
			isImageFile = true; // X WindowDump (XWD)
		}else if("ANI".equalsIgnoreCase(extension)){
			isImageFile = true; // Windows Animated Cursor (ANI)
		}else if("AVI".equalsIgnoreCase(extension)){
			isImageFile = true; // Windows AVI Format (AVI)
		}else if("WMF".equalsIgnoreCase(extension)){
			isImageFile = true; // Windows Metafile Formats (WMF)
		}else if("EMF".equalsIgnoreCase(extension)){
			isImageFile = true; // Windows Metafile Formats (EMF)
		}else if("WPG".equalsIgnoreCase(extension)){
			isImageFile = true; // WordPerfect Format (WPG)
		}else if("CMP".equalsIgnoreCase(extension)){
			isImageFile = true; // LEAD 1-Bit Format (CMP)
		}else if("XBM".equalsIgnoreCase(extension)){
			isImageFile = true; // XBitMap Format (XBM)
		}else if("ITG".equalsIgnoreCase(extension)){
			isImageFile = true; // Intergraph Format (ITG)
		}else if("MAC".equalsIgnoreCase(extension)){
			isImageFile = true; // Miscellaneous 1-Bit Formats (MAC)
		}else if("IMG".equalsIgnoreCase(extension)){
			isImageFile = true; // Miscellaneous 1-Bit Formats (IMG)
		}else if("MSP".equalsIgnoreCase(extension)){
			isImageFile = true; // Miscellaneous 1-Bit Formats (MSP)
		}else if("MODCA".equalsIgnoreCase(extension)){
			isImageFile = true; // Image Object Content Architecture Format (MODCA)
		}else if("IOCA".equalsIgnoreCase(extension)){
			isImageFile = true; // Image Object Content Architecture Format (IOCA)
		}else{
			isImageFile = false;
		}

		return isImageFile;
	}

	public static File[] selectFileList(String filePath) {
		if(filePath == null || filePath.equals("")) return null; 
        File dir = new File(filePath); 
        File[] list = dir.listFiles(); 
        boolean[] isDir = new boolean[list.length]; 
        
        for(int i=0; i < list.length;i++) { 
        	File fileName = list[i]; 
              isDir[i] = fileName.isDirectory();
              // System.out.println(isDir[i]); 
        } 
        return list;
	}

	public static void saveImage(String imageUrl, File saveFile) {                     
		URL url = null;                                                                                                         
		BufferedImage bi = null;                                                                                            
		
		String fileFormat = imageUrl.substring(imageUrl.lastIndexOf(".")+1);
		
		try {                                                                                                                        
			url = new URL(imageUrl); // Image URL to download                                          
			bi = ImageIO.read(url);                                                                             
			ImageIO.write(bi, fileFormat, saveFile); // File type to save, file name to save        

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}                                                                                                                             
	}                                                                                                                                     

	/**
	 * Transferring files to a file server
	 * @throws IOException 
	 * @throws UnknownHostException 
	 */
	public static String sendFileServer(String imageUrl, String degree, String folderPath, int imgSize) throws UnknownHostException, IOException {
        String[] depth = imageUrl.split("/");
        String imgFileName = depth[depth.length - 1];
        imgFileName = imgFileName.replaceAll(";","");
        String fileFullPathName = GlobalProperties.getProperty("Globals.fileTempPath")+File.separator+imgFileName;
        
        // Copy to web server
		File file = new File(fileFullPathName);
		saveImage(imageUrl, file);
		
		// Rotate image
		if(!degree.equals("0") && !degree.equals("")) {
			rotateImage(fileFullPathName, degree);
		}
		
		if(imgSize!=0) {
			try {
				imgFileName = imageResize(GlobalProperties.getProperty("Globals.fileTempPath")+File.separator, imgFileName, "s" + imgFileName, imgSize);
			} catch (Exception e1) {
				e1.printStackTrace();
			}	
		}
		
		file = new File(GlobalProperties.getProperty("Globals.fileTempPath")+File.separator+imgFileName);
       // 2. Stream preparation
       FileInputStream fis;
		try {
			fis = new FileInputStream(file);

	        BufferedInputStream bis = new BufferedInputStream(fis);
	        // Preparing the Socket
	        Socket socket = new Socket(GlobalProperties.getProperty("file.server.domain"), Integer.parseInt(GlobalProperties.getProperty("file.server.port")));
	        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());// You can write your own string bytes.
	        // File name transfer
	        dos.writeUTF(folderPath+"/|"+imgFileName);
	        dos.flush();
	        // 3. Read -> Transfer
	        byte[] arr = new byte[1024];
	        while (bis.read(arr) != -1) {
	            dos.write(arr); // Transfer
	 
	        }
	        // 4. flush()
	        dos.flush();
	        fis.close();
	        bis.close();
	        dos.close();
	        socket.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			file.delete();
		}

		return "http://"+GlobalProperties.getProperty("file.server.domain")+"/data/"+folderPath+"/"+imgFileName;
	}

    public static String imageResize(String ppath, String psoruceName, String ptargetName, int resize_image_width) throws Exception { 
    	return  imageResizeOpt(ppath,psoruceName,ptargetName,resize_image_width,true);
    }
    
    public static String imageResizeOpt(String ppath, String psoruceName, String ptargetName, int resize_image_width,boolean bDelete) throws Exception { 
    	String soruce = ppath + psoruceName; 
    	String target = ppath + ptargetName;
        Image imgSource = new ImageIcon(soruce).getImage(); 
        String returnStringName = psoruceName;

        int oldW = imgSource.getWidth(null); 
        int oldH = imgSource.getHeight(null); 
        
        if(oldW > resize_image_width) {
            int newW = resize_image_width;
            int newH = (resize_image_width * oldH) / oldW; 

            Image imgTarget = imgSource.getScaledInstance(newW, newH, Image.SCALE_SMOOTH); 

            int pixels[] = new int[newW * newH]; 

            PixelGrabber pg = new PixelGrabber(imgTarget, 0, 0, newW, newH, pixels, 0, newW); 
            pg.grabPixels();

            BufferedImage bi = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_RGB); 
            bi.setRGB(0, 0, newW, newH, pixels, 0, newW); 

            int i = psoruceName.lastIndexOf('.');
            String fType = "png";
            if (i > 0) {
            	fType = psoruceName.substring(i+1);
            }
            FileOutputStream fos = new FileOutputStream(target); 
            ImageWriter imageWriter = (ImageWriter)ImageIO.getImageWritersBySuffix(fType).next();
            ImageOutputStream ios = ImageIO.createImageOutputStream(fos);
            imageWriter.setOutput(ios);
            IIOMetadata imageMetaData = imageWriter.getDefaultImageMetadata(new ImageTypeSpecifier(bi), null);
            imageWriter.write(imageMetaData, new IIOImage(bi, null, null), null);

            fos.close(); 
            returnStringName = ptargetName;
            if(bDelete)
            {
	            File f = new File(soruce);
	            if(f.exists())
	            	f.delete();
	            f = null;
            }
        } else {
			File f1 = new File(ppath + psoruceName);
			File f2 = new File(ppath + ptargetName);
            returnStringName = ptargetName;
			FileUtil.copyFileAbs(f1, f2);
            if(bDelete)
            {
	            if(f1.exists())
	            	f1.delete();
            }
            f1 = null;
        }
        return returnStringName;
    }
    // Image rotate
	public static String rotateImage(String filePath, String angle) {
        String[] depth = filePath.split("/"); 
        String imgFileName = depth[depth.length - 1];
		String fileFormat = imgFileName.substring(imgFileName.lastIndexOf(".")+1);
        
		BufferedImage img = null;
		BufferedImage img2 = null;
		try {
		    img = ImageIO.read(new File(filePath));
			if(angle.equals("180")) {
			    img2 = rotate180(img);
			} else if(angle.equals("90")) {
			    img2 = rotate90ToRight(img);
			} else {
			    img2 = rotate90ToLeft(img);
			}
		    File outputfile = new File(filePath);
		    ImageIO.write(img2, fileFormat, outputfile);
		} catch (IOException e) {
		}
		
		return "";
	}
	
	public static BufferedImage rotate180( BufferedImage inputImage ) {
			int width = inputImage.getWidth(); //the Width of the original image
			int height = inputImage.getHeight();//the Height of the original image
			BufferedImage returnImage = new BufferedImage( width, height, inputImage.getType()  );

			for( int x = 0; x < width; x++ ) {
				for( int y = 0; y < height; y++ ) {
					returnImage.setRGB( width - x - 1, height - y - 1, inputImage.getRGB( x, y  )  );
				}
			}
			return returnImage;
	}
	
	public static BufferedImage rotate90ToRight( BufferedImage inputImage ){
		int width = inputImage.getWidth();
		int height = inputImage.getHeight();
		BufferedImage returnImage = new BufferedImage( height, width , inputImage.getType()  );

		for( int x = 0; x < width; x++ ) {
			for( int y = 0; y < height; y++ ) {
				returnImage.setRGB( height - y -1, x, inputImage.getRGB( x, y  )  );
			}
		}
		return returnImage;
	}

	public static BufferedImage rotate90ToLeft( BufferedImage inputImage ){
			int width = inputImage.getWidth();
			int height = inputImage.getHeight();
			BufferedImage returnImage = new BufferedImage( height, width , inputImage.getType()  );
			for( int x = 0; x < width; x++ ) {
				for( int y = 0; y < height; y++ ) {
					returnImage.setRGB(y, (width - x) - 1, inputImage.getRGB( x, y  )  );
				}
			}
			return returnImage;
		}
}
