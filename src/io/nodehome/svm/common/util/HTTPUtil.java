package io.nodehome.svm.common.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

public class HTTPUtil extends StringUtils {

	public static String urlGetConnection(String host) {
		BufferedReader in = null;
        String rtn = "";
		 
        try {
            URL obj = new URL(host);
            HttpURLConnection con = (HttpURLConnection)obj.openConnection();
            con.setRequestMethod("GET");
            in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
        
            String line;
            while((line = in.readLine()) != null) {
            	rtn += line;
            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(in != null) try { in.close(); } catch(Exception e) { e.printStackTrace(); }
        }

        return rtn;
	}
	
	public static String urlPostConnection(String host, String urlParameters) {
		String rtn = "";
		try {
			URL url = new URL(host);
	        byte[] postDataBytes = urlParameters.toString().getBytes("UTF-8");

	        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
	        conn.setRequestMethod("POST");
	        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
	        conn.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
	        conn.setDoOutput(true);
	        conn.getOutputStream().write(postDataBytes);

	        Reader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

	        for (int c; (c = in.read()) >= 0;)
	        	rtn += (char)c;
		} catch (UnsupportedEncodingException | MalformedURLException | ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return rtn;
	}
	
    public static void sendResponse(String str, HttpServletRequest request, HttpServletResponse response, String charSet) throws Exception {
		response.setContentType("text/html; charset="+charSet);
		response.setHeader("pragma",		"no-cache");
		response.setHeader("cache-control",	"no-cache");
		response.setHeader("expires",		"0");

		PrintWriter out = response.getWriter();
		
		out.println(str);
		out.flush();out.close();
	}
    
    public static void sendResponse(String str, HttpServletResponse response, String charSet) throws Exception {
		response.setContentType("text/html; charset="+charSet);
		response.setHeader("pragma",		"no-cache");
		response.setHeader("cache-control",	"no-cache");
		response.setHeader("expires",		"0");

		PrintWriter out = response.getWriter();

		out.println(str);
		out.flush();out.close();
	}

    public static String getMime(String args) {
     String sRtn = "application/octet-stream";
     String lower = args.toLowerCase();

     if("3dm".equals(lower)){sRtn="x-world/x-3dmf";}
     else if("3dmf".equals(lower)){sRtn="x-world/x-3dmf";}
     else if("a".equals(lower)){sRtn="application/octet-stream";}
     else if("aab".equals(lower)){sRtn="application/x-authorware-bin";}
     else if("aam".equals(lower)){sRtn="application/x-authorware-map";}
     else if("aas".equals(lower)){sRtn="application/x-authorware-seg";}
     else if("abc".equals(lower)){sRtn="text/vnd.abc";}
     else if("acgi".equals(lower)){sRtn="text/html";}
     else if("afl".equals(lower)){sRtn="video/animaflex";}
     else if("ai".equals(lower)){sRtn="application/postscript";}
     else if("aif".equals(lower)){sRtn="audio/aiff";}
     else if("aifc".equals(lower)){sRtn="audio/aiff";}
     else if("aiff".equals(lower)){sRtn="audio/x-aiff";}
     else if("aim".equals(lower)){sRtn="application/x-aim";}
     else if("aip".equals(lower)){sRtn="text/x-audiosoft-intra";}
     else if("ani".equals(lower)){sRtn="application/x-navi-animation";}
     else if("aos".equals(lower)){sRtn="application/x-nokia-9000-communicator-add-on-software";}
     else if("aps".equals(lower)){sRtn="application/mime";}
     else if("arc".equals(lower)){sRtn="application/octet-stream";}
     else if("arj".equals(lower)){sRtn="application/arj";}
     else if("art".equals(lower)){sRtn="image/x-jg";}
     else if("asf".equals(lower)){sRtn="video/x-ms-asf";}
     else if("asm".equals(lower)){sRtn="text/x-asm";}
     else if("asp".equals(lower)){sRtn="text/asp";}
     else if("asx".equals(lower)){sRtn="video/x-ms-asf";}
     else if("au".equals(lower)){sRtn="audio/x-au";}
     else if("avi".equals(lower)){sRtn="video/x-msvideo";}
     else if("avs".equals(lower)){sRtn="video/avs-video";}
     else if("bcpio".equals(lower)){sRtn="application/x-bcpio";}
     else if("bin".equals(lower)){sRtn="application/octet-stream";}
     else if("bm".equals(lower)){sRtn="image/bmp";}
     else if("bmp".equals(lower)){sRtn="image/bmp";}
     else if("boo".equals(lower)){sRtn="application/book";}
     else if("book".equals(lower)){sRtn="application/book";}
     else if("boz".equals(lower)){sRtn="application/x-bzip2";}
     else if("bsh".equals(lower)){sRtn="application/x-bsh";}
     else if("bz".equals(lower)){sRtn="application/x-bzip";}
     else if("bz2".equals(lower)){sRtn="application/x-bzip2";}
     else if("c".equals(lower)){sRtn="text/plain";}
     else if("c++".equals(lower)){sRtn="text/plain";}
     else if("cat".equals(lower)){sRtn="application/vnd.ms-pki.seccat";}
     else if("cc".equals(lower)){sRtn="text/plain";}
     else if("cc".equals(lower)){sRtn="text/x-c";}
     else if("ccad".equals(lower)){sRtn="application/clariscad";}
     else if("cco".equals(lower)){sRtn="application/x-cocoa";}
     else if("cdf".equals(lower)){sRtn="application/cdf";}
     else if("cer".equals(lower)){sRtn="application/pkix-cert";}
     else if("cha".equals(lower)){sRtn="application/x-chat";}
     else if("chat".equals(lower)){sRtn="application/x-chat";}
     else if("com".equals(lower)){sRtn="text/plain";}
     else if("conf".equals(lower)){sRtn="text/plain";}
     else if("cpio".equals(lower)){sRtn="application/x-cpio";}
     else if("cpp".equals(lower)){sRtn="text/x-c";}
     else if("cpt".equals(lower)){sRtn="application/mac-compactpro";}
     else if("crl".equals(lower)){sRtn="application/pkcs-crl";}
     else if("crt".equals(lower)){sRtn="application/pkix-cert";}
     else if("csh".equals(lower)){sRtn="application/x-csh";}
     else if("css".equals(lower)){sRtn="text/css";}
     else if("cxx".equals(lower)){sRtn="text/plain";}
     else if("dcr".equals(lower)){sRtn="application/x-director";}
     else if("deepv".equals(lower)){sRtn="application/x-deepv";}
     else if("def".equals(lower)){sRtn="text/plain";}
     else if("dif".equals(lower)){sRtn="video/x-dv";}
     else if("dir".equals(lower)){sRtn="application/x-director";}
     else if("dl".equals(lower)){sRtn="video/dl";}
     else if("doc".equals(lower)){sRtn="application/msword";}
     else if("dot".equals(lower)){sRtn="application/msword";}
     else if("dp".equals(lower)){sRtn="application/commonground";}
     else if("drw".equals(lower)){sRtn="application/drafting";}
     else if("dump".equals(lower)){sRtn="application/octet-stream";}
     else if("dv".equals(lower)){sRtn="video/x-dv";}
     else if("dvi".equals(lower)){sRtn="application/x-dvi";}
     else if("dwf".equals(lower)){sRtn="model/vnd.dwf";}
     else if("dwg".equals(lower)){sRtn="image/vnd.dwg";}
     else if("dwg".equals(lower)){sRtn="image/x-dwg";}
     else if("dxf".equals(lower)){sRtn="application/dxf";}
     else if("el".equals(lower)){sRtn="text/x-script.elisp";}
     else if("elc".equals(lower)){sRtn="application/x-elc";}
     else if("env".equals(lower)){sRtn="application/x-envoy";}
     else if("eps".equals(lower)){sRtn="application/postscript";}
     else if("es".equals(lower)){sRtn="application/x-esrehber";}
     else if("etx".equals(lower)){sRtn="text/x-setext";}
     else if("evy".equals(lower)){sRtn="application/envoy";}
     else if("exe".equals(lower)){sRtn="application/octet-stream";}
     else if("f".equals(lower)){sRtn="text/plain";}
     else if("f77".equals(lower)){sRtn="text/x-fortran";}
     else if("f90".equals(lower)){sRtn="text/plain";}
     else if("f90".equals(lower)){sRtn="text/x-fortran";}
     else if("fdf".equals(lower)){sRtn="application/vnd.fdf";}
     else if("fif".equals(lower)){sRtn="application/fractals";}
     else if("fif".equals(lower)){sRtn="image/fif";}
     else if("fli".equals(lower)){sRtn="video/fli";}
     else if("flo".equals(lower)){sRtn="image/florian";}
     else if("flx".equals(lower)){sRtn="text/vnd.fmi.flexstor";}
     else if("fmf".equals(lower)){sRtn="video/x-atomic3d-feature";}
     else if("for".equals(lower)){sRtn="text/plain";}
     else if("fpx".equals(lower)){sRtn="image/vnd.fpx";}
     else if("frl".equals(lower)){sRtn="application/freeloader";}
     else if("funk".equals(lower)){sRtn="audio/make";}
     else if("g".equals(lower)){sRtn="text/plain";}
     else if("g3".equals(lower)){sRtn="image/g3fax";}
     else if("gif".equals(lower)){sRtn="image/gif";}
     else if("gl".equals(lower)){sRtn="video/gl";}
     else if("gsd".equals(lower)){sRtn="audio/x-gsm";}
     else if("gsm".equals(lower)){sRtn="audio/x-gsm";}
     else if("gsp".equals(lower)){sRtn="application/x-gsp";}
     else if("gss".equals(lower)){sRtn="application/x-gss";}
     else if("gtar".equals(lower)){sRtn="application/x-gtar";}
     else if("gz".equals(lower)){sRtn="application/x-compressed";}
     else if("gzip".equals(lower)){sRtn="application/x-gzip";}
     else if("h".equals(lower)){sRtn="text/plain";}
     else if("hdf".equals(lower)){sRtn="application/x-hdf";}
     else if("help".equals(lower)){sRtn="application/x-helpfile";}
     else if("hgl".equals(lower)){sRtn="application/vnd.hp-hpgl";}
     else if("hh".equals(lower)){sRtn="text/plain";}
     else if("hlb".equals(lower)){sRtn="text/x-script";}
     else if("hlp".equals(lower)){sRtn="application/hlp";}
     else if("hpg".equals(lower)){sRtn="application/vnd.hp-hpgl";}
     else if("hpgl".equals(lower)){sRtn="application/vnd.hp-hpgl";}
     else if("hqx".equals(lower)){sRtn="application/binhex";}
     else if("hta".equals(lower)){sRtn="application/hta";}
     else if("htc".equals(lower)){sRtn="text/x-component";}
     else if("htm".equals(lower)){sRtn="text/html";}
     else if("html".equals(lower)){sRtn="text/html";}
     else if("htmls".equals(lower)){sRtn="text/html";}
     else if("htt".equals(lower)){sRtn="text/webviewhtml";}
     else if("htx".equals(lower)){sRtn="text/html";}
     else if("ice".equals(lower)){sRtn="x-conference/x-cooltalk";}
     else if("ico".equals(lower)){sRtn="image/x-icon";}
     else if("idc".equals(lower)){sRtn="text/plain";}
     else if("ief".equals(lower)){sRtn="image/ief";}
     else if("iefs".equals(lower)){sRtn="image/ief";}
     else if("iges".equals(lower)){sRtn="application/iges";}
     else if("iges".equals(lower)){sRtn="model/iges";}
     else if("igs".equals(lower)){sRtn="application/iges";}
     else if("ima".equals(lower)){sRtn="application/x-ima";}
     else if("imap".equals(lower)){sRtn="application/x-httpd-imap";}
     else if("inf".equals(lower)){sRtn="application/inf";}
     else if("ip".equals(lower)){sRtn="application/x-ip2";}
     else if("isu".equals(lower)){sRtn="video/x-isvideo";}
     else if("it".equals(lower)){sRtn="audio/it";}
     else if("iv".equals(lower)){sRtn="application/x-inventor";}
     else if("ivr".equals(lower)){sRtn="i-world/i-vrml";}
     else if("ivy".equals(lower)){sRtn="application/x-livescreen";}
     else if("jam".equals(lower)){sRtn="audio/x-jam";}
     else if("jav".equals(lower)){sRtn="text/plain";}
     else if("java".equals(lower)){sRtn="text/plain";}
     else if("jcm".equals(lower)){sRtn="application/x-java-commerce";}
     else if("jfif".equals(lower)){sRtn="image/jpeg";}
     else if("jfif-tbnl".equals(lower)){sRtn="image/jpeg";}
     else if("jpe".equals(lower)){sRtn="image/jpeg";}
     else if("jpeg".equals(lower)){sRtn="image/jpeg";}
     else if("jpg".equals(lower)){sRtn="image/jpeg";}
     else if("jps".equals(lower)){sRtn="image/x-jps";}
     else if("js".equals(lower)){sRtn="application/x-javascript";}
     else if("jut".equals(lower)){sRtn="image/jutvision";}
     else if("kar".equals(lower)){sRtn="audio/midi";}
     else if("ksh".equals(lower)){sRtn="application/x-ksh";}
     else if("ksh".equals(lower)){sRtn="text/x-script.ksh";}
     else if("la".equals(lower)){sRtn="audio/nspaudio";}
     else if("la".equals(lower)){sRtn="audio/x-nspaudio";}
     else if("lam".equals(lower)){sRtn="audio/x-liveaudio";}
     else if("latex".equals(lower)){sRtn="application/x-latex";}
     else if("lha".equals(lower)){sRtn="application/lha";}
     else if("lhx".equals(lower)){sRtn="application/octet-stream";}
     else if("list".equals(lower)){sRtn="text/plain";}
     else if("lma".equals(lower)){sRtn="audio/nspaudio";}
     else if("lma".equals(lower)){sRtn="audio/x-nspaudio";}
     else if("log".equals(lower)){sRtn="text/plain";}
     else if("lsp".equals(lower)){sRtn="application/x-lisp";}
     else if("lsp".equals(lower)){sRtn="text/x-script.lisp";}
     else if("lst".equals(lower)){sRtn="text/plain";}
     else if("lsx".equals(lower)){sRtn="text/x-la-asf";}
     else if("ltx".equals(lower)){sRtn="application/x-latex";}
     else if("lzh".equals(lower)){sRtn="application/octet-stream";}
     else if("m".equals(lower)){sRtn="text/plain";}
     else if("m".equals(lower)){sRtn="text/x-m";}
     else if("m1v".equals(lower)){sRtn="video/mpeg";}
     else if("m2a".equals(lower)){sRtn="audio/mpeg";}
     else if("m2v".equals(lower)){sRtn="video/mpeg";}
     else if("m3u".equals(lower)){sRtn="audio/x-mpequrl";}
     else if("man".equals(lower)){sRtn="application/x-troff-man";}
     else if("map".equals(lower)){sRtn="application/x-navimap";}
     else if("mar".equals(lower)){sRtn="text/plain";}
     else if("mbd".equals(lower)){sRtn="application/mbedlet";}
     else if("mc$".equals(lower)){sRtn="application/x-magic-cap-package-1.0";}
     else if("mcd".equals(lower)){sRtn="application/mcad";}
     else if("mcf".equals(lower)){sRtn="image/vasa";}
     else if("mcp".equals(lower)){sRtn="application/netmc";}
     else if("me".equals(lower)){sRtn="application/x-troff-me";}
     else if("mht".equals(lower)){sRtn="message/rfc822";}
     else if("mhtml".equals(lower)){sRtn="message/rfc822";}
     else if("mid".equals(lower)){sRtn="application/x-midi";}
     else if("midi".equals(lower)){sRtn="application/x-midi";}
     else if("mif".equals(lower)){sRtn="application/x-frame";}
     else if("mime".equals(lower)){sRtn="message/rfc822";}
     else if("mjf".equals(lower)){sRtn="audio/x-vnd.audioexplosion.mjuicemediafile";}
     else if("mjpg".equals(lower)){sRtn="video/x-motion-jpeg";}
     else if("mm".equals(lower)){sRtn="application/base64";}
     else if("mme".equals(lower)){sRtn="application/base64";}
     else if("mod".equals(lower)){sRtn="audio/mod";}
     else if("moov".equals(lower)){sRtn="video/quicktime";}
     else if("mov".equals(lower)){sRtn="video/quicktime";}
     else if("movie".equals(lower)){sRtn="video/x-sgi-movie";}
     else if("mp2".equals(lower)){sRtn="audio/mpeg";}
     else if("mp3".equals(lower)){sRtn="audio/mpeg3";}
     else if("mpa".equals(lower)){sRtn="audio/mpeg";}
     else if("mpc".equals(lower)){sRtn="application/x-project";}
     else if("mpe".equals(lower)){sRtn="video/mpeg";}
     else if("mpeg".equals(lower)){sRtn="video/mpeg";}
     else if("mpg".equals(lower)){sRtn="audio/mpeg";}
     else if("mpga".equals(lower)){sRtn="audio/mpeg";}
     else if("mpp".equals(lower)){sRtn="application/vnd.ms-project";}
     else if("mpt".equals(lower)){sRtn="application/x-project";}
     else if("mpv".equals(lower)){sRtn="application/x-project";}
     else if("mpx".equals(lower)){sRtn="application/x-project";}
     else if("mrc".equals(lower)){sRtn="application/marc";}
     else if("ms".equals(lower)){sRtn="application/x-troff-ms";}
     else if("mv".equals(lower)){sRtn="video/x-sgi-movie";}
     else if("my".equals(lower)){sRtn="audio/make";}
     else if("mzz".equals(lower)){sRtn="application/x-vnd.audioexplosion.mzz";}
     else if("nap".equals(lower)){sRtn="image/naplps";}
     else if("naplps".equals(lower)){sRtn="image/naplps";}
     else if("nc".equals(lower)){sRtn="application/x-netcdf";}
     else if("ncm".equals(lower)){sRtn="application/vnd.nokia.configuration-message";}
     else if("nif".equals(lower)){sRtn="image/x-niff";}
     else if("niff".equals(lower)){sRtn="image/x-niff";}
     else if("nix".equals(lower)){sRtn="application/x-mix-transfer";}
     else if("nsc".equals(lower)){sRtn="application/x-conference";}
     else if("nvd".equals(lower)){sRtn="application/x-navidoc";}
     else if("o".equals(lower)){sRtn="application/octet-stream";}
     else if("oda".equals(lower)){sRtn="application/oda";}
     else if("omc".equals(lower)){sRtn="application/x-omc";}
     else if("omcd".equals(lower)){sRtn="application/x-omcdatamaker";}
     else if("omcr".equals(lower)){sRtn="application/x-omcregerator";}
     else if("p".equals(lower)){sRtn="text/x-pascal";}
     else if("p10".equals(lower)){sRtn="application/pkcs10";}
     else if("p12".equals(lower)){sRtn="application/pkcs-12";}
     else if("p7a".equals(lower)){sRtn="application/x-pkcs7-signature";}
     else if("p7c".equals(lower)){sRtn="application/pkcs7-mime";}
     else if("p7m".equals(lower)){sRtn="application/pkcs7-mime";}
     else if("p7r".equals(lower)){sRtn="application/x-pkcs7-certreqresp";}
     else if("p7s".equals(lower)){sRtn="application/pkcs7-signature";}
     else if("part".equals(lower)){sRtn="application/pro_eng";}
     else if("pas".equals(lower)){sRtn="text/pascal";}
     else if("pbm".equals(lower)){sRtn="image/x-portable-bitmap";}
     else if("pcl".equals(lower)){sRtn="application/vnd.hp-pcl";}
     else if("pct".equals(lower)){sRtn="image/x-pict";}
     else if("pcx".equals(lower)){sRtn="image/x-pcx";}
     else if("pdb".equals(lower)){sRtn="chemical/x-pdb";}
     else if("pdf".equals(lower)){sRtn="application/pdf";}
     else if("pfunk".equals(lower)){sRtn="audio/make";}
     else if("pgm".equals(lower)){sRtn="image/x-portable-graymap";}
     else if("pgm".equals(lower)){sRtn="image/x-portable-greymap";}
     else if("pic".equals(lower)){sRtn="image/pict";}
     else if("pict".equals(lower)){sRtn="image/pict";}
     else if("pkg".equals(lower)){sRtn="application/x-newton-compatible-pkg";}
     else if("pko".equals(lower)){sRtn="application/vnd.ms-pki.pko";}
     else if("pl".equals(lower)){sRtn="text/plain";}
     else if("plx".equals(lower)){sRtn="application/x-pixclscript";}
     else if("pm".equals(lower)){sRtn="image/x-xpixmap";}
     else if("pm4".equals(lower)){sRtn="application/x-pagemaker";}
     else if("pm5".equals(lower)){sRtn="application/x-pagemaker";}
     else if("png".equals(lower)){sRtn="image/png";}
     else if("pnm".equals(lower)){sRtn="application/x-portable-anymap";}
     else if("pot".equals(lower)){sRtn="application/mspowerpoint";}
     else if("pov".equals(lower)){sRtn="model/x-pov";}
     else if("ppa".equals(lower)){sRtn="application/vnd.ms-powerpoint";}
     else if("ppm".equals(lower)){sRtn="image/x-portable-pixmap";}
     else if("pps".equals(lower)){sRtn="application/mspowerpoint";}
     else if("ppt".equals(lower)){sRtn="application/mspowerpoint";}
     else if("ppt".equals(lower)){sRtn="application/vnd.ms-powerpoint";}
     else if("ppz".equals(lower)){sRtn="application/mspowerpoint";}
     else if("pre".equals(lower)){sRtn="application/x-freelance";}
     else if("prt".equals(lower)){sRtn="application/pro_eng";}
     else if("ps".equals(lower)){sRtn="application/postscript";}
     else if("psd".equals(lower)){sRtn="application/octet-stream";}
     else if("pvu".equals(lower)){sRtn="paleovu/x-pv";}
     else if("pwz".equals(lower)){sRtn="application/vnd.ms-powerpoint";}
     else if("py".equals(lower)){sRtn="text/x-script.phyton";}
     else if("pyc".equals(lower)){sRtn="applicaiton/x-bytecode.python";}
     else if("qcp".equals(lower)){sRtn="audio/vnd.qcelp";}
     else if("qd3".equals(lower)){sRtn="x-world/x-3dmf";}
     else if("qd3d".equals(lower)){sRtn="x-world/x-3dmf";}
     else if("qif".equals(lower)){sRtn="image/x-quicktime";}
     else if("qt".equals(lower)){sRtn="video/quicktime";}
     else if("qtc".equals(lower)){sRtn="video/x-qtc";}
     else if("qti".equals(lower)){sRtn="image/x-quicktime";}
     else if("qtif".equals(lower)){sRtn="image/x-quicktime";}
     else if("ra".equals(lower)){sRtn="audio/x-pn-realaudio";}
     else if("ram".equals(lower)){sRtn="audio/x-pn-realaudio";}
     else if("ras".equals(lower)){sRtn="application/x-cmu-raster";}
     else if("rast".equals(lower)){sRtn="image/cmu-raster";}
     else if("rexx".equals(lower)){sRtn="text/x-script.rexx";}
     else if("rf".equals(lower)){sRtn="image/vnd.rn-realflash";}
     else if("rgb".equals(lower)){sRtn="image/x-rgb";}
     else if("rm".equals(lower)){sRtn="application/vnd.rn-realmedia";}
     else if("rmi".equals(lower)){sRtn="audio/mid";}
     else if("rmm".equals(lower)){sRtn="audio/x-pn-realaudio";}
     else if("rmp".equals(lower)){sRtn="audio/x-pn-realaudio";}
     else if("rng".equals(lower)){sRtn="application/ringing-tones";}
     else if("rnx".equals(lower)){sRtn="application/vnd.rn-realplayer";}
     else if("roff".equals(lower)){sRtn="application/x-troff";}
     else if("rp".equals(lower)){sRtn="image/vnd.rn-realpix";}
     else if("rpm".equals(lower)){sRtn="audio/x-pn-realaudio-plugin";}
     else if("rt".equals(lower)){sRtn="text/richtext";}
     else if("rtf".equals(lower)){sRtn="application/rtf";}
     else if("rtx".equals(lower)){sRtn="application/rtf";}
     else if("rv".equals(lower)){sRtn="video/vnd.rn-realvideo";}
     else if("s".equals(lower)){sRtn="text/x-asm";}
     else if("s3m".equals(lower)){sRtn="audio/s3m";}
     else if("saveme".equals(lower)){sRtn="application/octet-stream";}
     else if("sbk".equals(lower)){sRtn="application/x-tbook";}
     else if("scm".equals(lower)){sRtn="application/x-lotusscreencam";}
     else if("sdml".equals(lower)){sRtn="text/plain";}
     else if("sdp".equals(lower)){sRtn="application/sdp";}
     else if("sdr".equals(lower)){sRtn="application/sounder";}
     else if("sea".equals(lower)){sRtn="application/sea";}
     else if("set".equals(lower)){sRtn="application/set";}
     else if("sgm".equals(lower)){sRtn="text/sgml";}
     else if("sgml".equals(lower)){sRtn="text/sgml";}
     else if("sh".equals(lower)){sRtn="application/x-bsh";}
     else if("shar".equals(lower)){sRtn="application/x-bsh";}
     else if("shtml".equals(lower)){sRtn="text/html";}
     else if("sid".equals(lower)){sRtn="audio/x-psid";}
     else if("sit".equals(lower)){sRtn="application/x-sit";}
     else if("skd".equals(lower)){sRtn="application/x-koan";}
     else if("skm".equals(lower)){sRtn="application/x-koan";}
     else if("skp".equals(lower)){sRtn="application/x-koan";}
     else if("skt".equals(lower)){sRtn="application/x-koan";}
     else if("sl".equals(lower)){sRtn="application/x-seelogo";}
     else if("smi".equals(lower)){sRtn="application/smil";}
     else if("smil".equals(lower)){sRtn="application/smil";}
     else if("snd".equals(lower)){sRtn="audio/basic";}
     else if("sol".equals(lower)){sRtn="application/solids";}
     else if("spc".equals(lower)){sRtn="application/x-pkcs7-certificates";}
     else if("spl".equals(lower)){sRtn="application/futuresplash";}
     else if("spr".equals(lower)){sRtn="application/x-sprite";}
     else if("sprite".equals(lower)){sRtn="application/x-sprite";}
     else if("src".equals(lower)){sRtn="application/x-wais-source";}
     else if("ssi".equals(lower)){sRtn="text/x-server-parsed-html";}
     else if("ssm".equals(lower)){sRtn="application/streamingmedia";}
     else if("sst".equals(lower)){sRtn="application/vnd.ms-pki.certstore";}
     else if("step".equals(lower)){sRtn="application/step";}
     else if("stl".equals(lower)){sRtn="application/sla";}
     else if("stp".equals(lower)){sRtn="application/step";}
     else if("sv4cpio".equals(lower)){sRtn="application/x-sv4cpio";}
     else if("sv4crc".equals(lower)){sRtn="application/x-sv4crc";}
     else if("svf".equals(lower)){sRtn="image/vnd.dwg";}
     else if("svr".equals(lower)){sRtn="application/x-world";}
     else if("swf".equals(lower)){sRtn="application/x-shockwave-flash";}
     else if("t".equals(lower)){sRtn="application/x-troff";}
     else if("talk".equals(lower)){sRtn="text/x-speech";}
     else if("tar".equals(lower)){sRtn="application/x-tar";}
     else if("tbk".equals(lower)){sRtn="application/toolbook";}
     else if("tcl".equals(lower)){sRtn="application/x-tcl";}
     else if("tcsh".equals(lower)){sRtn="text/x-script.tcsh";}
     else if("tex".equals(lower)){sRtn="application/x-tex";}
     else if("texi".equals(lower)){sRtn="application/x-texinfo";}
     else if("texinfo".equals(lower)){sRtn="application/x-texinfo";}
     else if("text".equals(lower)){sRtn="application/plain";}
     else if("tgz".equals(lower)){sRtn="application/gnutar";}
     else if("tif".equals(lower)){sRtn="image/tiff";}
     else if("tiff".equals(lower)){sRtn="image/tiff";}
     else if("tr".equals(lower)){sRtn="application/x-troff";}
     else if("tsi".equals(lower)){sRtn="audio/tsp-audio";}
     else if("tsp".equals(lower)){sRtn="application/dsptype";}
     else if("tsv".equals(lower)){sRtn="text/tab-separated-values";}
     else if("turbot".equals(lower)){sRtn="image/florian";}
     else if("txt".equals(lower)){sRtn="application/octet-stream";}
     else if("uil".equals(lower)){sRtn="text/x-uil";}
     else if("uni".equals(lower)){sRtn="text/uri-list";}
     else if("unis".equals(lower)){sRtn="text/uri-list";}
     else if("unv".equals(lower)){sRtn="application/i-deas";}
     else if("uri".equals(lower)){sRtn="text/uri-list";}
     else if("uris".equals(lower)){sRtn="text/uri-list";}
     else if("ustar".equals(lower)){sRtn="application/x-ustar";}
     else if("uu".equals(lower)){sRtn="application/octet-stream";}
     else if("uue".equals(lower)){sRtn="text/x-uuencode";}
     else if("vcd".equals(lower)){sRtn="application/x-cdlink";}
     else if("vcs".equals(lower)){sRtn="text/x-vcalendar";}
     else if("vda".equals(lower)){sRtn="application/vda";}
     else if("vdo".equals(lower)){sRtn="video/vdo";}
     else if("vew".equals(lower)){sRtn="application/groupwise";}
     else if("viv".equals(lower)){sRtn="video/vivo";}
     else if("vivo".equals(lower)){sRtn="video/vivo";}
     else if("vmd".equals(lower)){sRtn="application/vocaltec-media-desc";}
     else if("vmf".equals(lower)){sRtn="application/vocaltec-media-file";}
     else if("voc".equals(lower)){sRtn="audio/voc";}
     else if("vos".equals(lower)){sRtn="video/vosaic";}
     else if("vox".equals(lower)){sRtn="audio/voxware";}
     else if("vqe".equals(lower)){sRtn="audio/x-twinvq-plugin";}
     else if("vqf".equals(lower)){sRtn="audio/x-twinvq";}
     else if("vql".equals(lower)){sRtn="audio/x-twinvq-plugin";}
     else if("vrml".equals(lower)){sRtn="application/x-vrml";}
     else if("vrt".equals(lower)){sRtn="x-world/x-vrt";}
     else if("vsd".equals(lower)){sRtn="application/x-visio";}
     else if("vst".equals(lower)){sRtn="application/x-visio";}
     else if("vsw".equals(lower)){sRtn="application/x-visio";}
     else if("w60".equals(lower)){sRtn="application/wordperfect6.0";}
     else if("w61".equals(lower)){sRtn="application/wordperfect6.1";}
     else if("w6w".equals(lower)){sRtn="application/msword";}
     else if("wav".equals(lower)){sRtn="audio/wav";}
     else if("wb1".equals(lower)){sRtn="application/x-qpro";}
     else if("wbmp".equals(lower)){sRtn="image/vnd.wap.wbmp";}
     else if("web".equals(lower)){sRtn="application/vnd.xara";}
     else if("wiz".equals(lower)){sRtn="application/msword";}
     else if("wk1".equals(lower)){sRtn="application/x-123";}
     else if("wmf".equals(lower)){sRtn="windows/metafile";}
     else if("wml".equals(lower)){sRtn="text/vnd.wap.wml";}
     else if("wmlc".equals(lower)){sRtn="application/vnd.wap.wmlc";}
     else if("wmls".equals(lower)){sRtn="text/vnd.wap.wmlscript";}
     else if("wmlsc".equals(lower)){sRtn="application/vnd.wap.wmlscriptc";}
     else if("word".equals(lower)){sRtn="application/msword";}
     else if("wp".equals(lower)){sRtn="application/wordperfect";}
     else if("wp5".equals(lower)){sRtn="application/wordperfect";}
     else if("wp6".equals(lower)){sRtn="application/wordperfect";}
     else if("wpd".equals(lower)){sRtn="application/wordperfect";}
     else if("wq1".equals(lower)){sRtn="application/x-lotus";}
     else if("wri".equals(lower)){sRtn="application/mswrite";}
     else if("wrl".equals(lower)){sRtn="application/x-world";}
     else if("wrz".equals(lower)){sRtn="model/vrml";}
     else if("wsc".equals(lower)){sRtn="text/scriplet";}
     else if("wsrc".equals(lower)){sRtn="application/x-wais-source";}
     else if("wtk".equals(lower)){sRtn="application/x-wintalk";}
     else if("xbm".equals(lower)){sRtn="image/x-xbitmap";}
     else if("xdr".equals(lower)){sRtn="video/x-amt-demorun";}
     else if("xgz".equals(lower)){sRtn="xgl/drawing";}
     else if("xif".equals(lower)){sRtn="image/vnd.xiff";}
     else if("xl".equals(lower)){sRtn="application/excel";}
     else if("xla".equals(lower)){sRtn="application/excel";}
     else if("xlb".equals(lower)){sRtn="application/excel";}
     else if("xlc".equals(lower)){sRtn="application/excel";}
     else if("xld".equals(lower)){sRtn="application/excel";}
     else if("xlk".equals(lower)){sRtn="application/excel";}
     else if("xll".equals(lower)){sRtn="application/excel";}
     else if("xlm".equals(lower)){sRtn="application/excel";}
     else if("xls".equals(lower)){sRtn="application/excel";}
     else if("xlt".equals(lower)){sRtn="application/excel";}
     else if("xlv".equals(lower)){sRtn="application/excel";}
     else if("xlw".equals(lower)){sRtn="application/excel";}
     else if("xm".equals(lower)){sRtn="audio/xm";}
     else if("xml".equals(lower)){sRtn="application/xml";}
     else if("xmz".equals(lower)){sRtn="xgl/movie";}
     else if("xpix".equals(lower)){sRtn="application/x-vnd.ls-xpix";}
     else if("xpm".equals(lower)){sRtn="image/x-xpixmap";}
     else if("xpm".equals(lower)){sRtn="image/xpm";}
     else if("x-png".equals(lower)){sRtn="image/png";}
     else if("xsr".equals(lower)){sRtn="video/x-amt-showrun";}
     else if("xwd".equals(lower)){sRtn="image/x-xwd";}
     else if("xyz".equals(lower)){sRtn="chemical/x-pdb";}
     else if("z".equals(lower)){sRtn="application/x-compress";}
     else if("zip".equals(lower)){sRtn="application/zip";}
     else if("zoo".equals(lower)){sRtn="application/octet-stream";}
     else if("zsh".equals(lower)){sRtn="text/x-script.zsh";}
     else{sRtn="application/octet-stream";}
     
     return sRtn;
    }


    
}