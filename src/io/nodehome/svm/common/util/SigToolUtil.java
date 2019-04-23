package io.nodehome.svm.common.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

import io.nodehome.cmm.service.GlobalProperties;

public class SigToolUtil {

	private String pcwalletPath = "";

	public SigToolUtil(String paramPcwalletPath) {
		this.pcwalletPath = paramPcwalletPath;
	}

	// Generate Key
	public String getGenerateKey() throws Exception {
		String netID = "0xEF";
		String project_net = GlobalProperties.getProperty("project_net");
		if(project_net.equals("DEBUGNET")) netID = "0xDE";
		if(project_net.equals("MAINNET")) netID = "0x80";
		String returnStr = runCommandExec(pcwalletPath+ System.getProperty("file.separator") + "sigtool -cmd=newKey -netID="+netID+"");
		return returnStr;
	}
	
	// Verify Signature
	public String getVerifySignature(String inputString, String pubk, String sig) throws Exception {
		String project_net = GlobalProperties.getProperty("project_net");
		String netID = "0xEF";
		if(project_net.equals("DEBUGNET")) netID = "0xDE";
		if(project_net.equals("MAINNET")) netID = "0x80";
		String returnStr = runCommandExec(pcwalletPath+ System.getProperty("file.separator") + "sigtool -cmd=verify -netID="+netID+" -input=\""+inputString+"\" -pubKey="+pubk+" -signature="+sig+"");
		return returnStr;
	}

	// Generate Signature
	public String getGenerateSignature(String inputString, String prik) throws Exception {
		/*
		 * const (	network_mainNet  = byte(0x80)	network_testNet  = byte(0xEF)	network_debugNet = byte(0xDE))
		 */
		// Example : /sigtool -cmd=signature -netID=0xEF -input="Some Text" -privKey=cSTzWvT8NziMDitNdnqj7NiKuBH1D3VDCnHHpiQongPfyfpnwpxz
		String netID = "0xEF";
		String project_net = GlobalProperties.getProperty("project_net");
		if(project_net.equals("DEBUGNET")) netID = "0xDE";
		if(project_net.equals("MAINNET")) netID = "0x80";
		System.out.println(pcwalletPath+ System.getProperty("file.separator") + "sigtool -cmd=signature -netID="+netID+" -input=\""+inputString+"\" -privKey="+prik+"");
		String returnStr = runCommandExec(pcwalletPath+ System.getProperty("file.separator") + "sigtool -cmd=signature -netID="+netID+" -input=\""+inputString+"\" -privKey="+prik+"");
		return returnStr;
	}

	// Recover Key
	public String getRecoverKey(String mnemonicWords) throws Exception {
		String netID = "0xEF";
		String project_net = GlobalProperties.getProperty("project_net");
		if(project_net.equals("DEBUGNET")) netID = "0xDE";
		if(project_net.equals("MAINNET")) netID = "0x80";
		String returnStr = runCommandExec(pcwalletPath+ System.getProperty("file.separator") + "sigtool -cmd=import -netID="+netID+" -mnemonicWords='"+mnemonicWords+"'");
		return returnStr;
	}
	
	private String runCommandExec(String cmd) {
		String returnStr = "";
		try {
			Process process;
			String osName = System.getProperty("os.name");
			if (osName.toLowerCase().startsWith("window")) {
				process = Runtime.getRuntime().exec(cmd);
			} else {
				String[] cmd2 = {"/bin/sh", "-c", cmd };
				process = Runtime.getRuntime().exec(cmd2);
			}

			BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
			Scanner scanner = new Scanner(br);
			scanner.useDelimiter(System.getProperty("line.separator"));
			while (scanner.hasNext()) {
				returnStr += scanner.next();
				//System.out.println(scanner.next());
			}
			scanner.close();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return returnStr;
	}

}
