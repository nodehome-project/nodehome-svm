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
		String returnStr = runCommandExec(pcwalletPath+ System.getProperty("file.separator") + "sigtool -cmd=newKey -netID=0xEF");
		return returnStr;
	}
	
	// Verify Signature
	public String getVerifySignature(String inputString, String pubk, String sig) throws Exception {
		// Example : /sigtool -cmd=verify -netID=0xEF -input=\"fimusic\" -pubKey=cMgcR49dvEfUrrp6gPSJFbe394SB2JNK2a9q4izbNj29jq8B5RLP -signature=381yXYhuSvV13BQnBHFsSYRbMGgKpFZrWNUi7jMx38h9c3scVGzFnN4oMnPknL689465GRNc4X1MPde7anG3KCUM2v47dWZ4
		String returnStr = runCommandExec(pcwalletPath+ System.getProperty("file.separator") + "sigtool -cmd=verify -netID=0xEF -input=\""+inputString+"\" -pubKey="+pubk+" -signature="+sig+"");
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
		String returnStr = runCommandExec(pcwalletPath+ System.getProperty("file.separator") + "sigtool -cmd=import -netID=0xEF -mnemonicWords='"+mnemonicWords+"'");
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
