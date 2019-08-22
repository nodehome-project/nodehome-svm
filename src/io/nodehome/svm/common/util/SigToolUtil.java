package io.nodehome.svm.common.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

import io.nodehome.svm.common.biz.ChainInfoVO;

public class SigToolUtil {

	private String pcwalletPath = "";

	public SigToolUtil(String paramPcwalletPath) {
		this.pcwalletPath = paramPcwalletPath;
	}

	// Generate Key
	public String getGenerateKey(String chainID) throws Exception {
		String project_net = ChainInfoVO.getNetwork(chainID);
//		if(project_net.equals("dev")) netID = "0xDE";
//		if(project_net.equals("biz")) netID = "0x80";
		String netType = project_net;	// test, biz, dev
		String returnStr = runCommandExec(pcwalletPath+ System.getProperty("file.separator") + "sigtool -cmd=newKey -netType="+netType+"");
		return returnStr;
	}
	
	// Verify Signature
	public String getVerifySignature(String inputString, String pubk, String sig, String chainID) throws Exception {
		String project_net = ChainInfoVO.getNetwork(chainID);
		String netType = project_net;
		System.out.println("sigtool -cmd=verify -netType="+netType+" -input=\""+inputString+"\" -pubKey="+pubk+" -signature="+sig+"");
		String returnStr = runCommandExec(pcwalletPath+ System.getProperty("file.separator") + "sigtool -cmd=verify -netType="+netType+" -input=\""+inputString+"\" -pubKey="+pubk+" -signature="+sig+"");
		return returnStr;
	}

	// Generate Signature
	public String getGenerateSignature(String inputString, String prik, String chainID) throws Exception {
		/*
		 * const (	network_bizNet  = byte(0x80)	network_testNet  = byte(0xEF)	network_devNet = byte(0xDE))
		 */
		// Example : /sigtool -cmd=signature -netID=0xEF -input="Some Text" -privKey=cSTzWvT8NziMDitNdnqj7NiKuBH1D3VDCnHHpiQongPfyfpnwpxz
		String project_net = ChainInfoVO.getNetwork(chainID);
		String netType = project_net;
		System.out.println(pcwalletPath+ System.getProperty("file.separator") + "sigtool -cmd=signature -netType="+netType+" -input=\""+inputString+"\" -privKey="+prik+"");
		String returnStr = runCommandExec(pcwalletPath+ System.getProperty("file.separator") + "sigtool -cmd=signature -netType="+netType+" -input=\""+inputString+"\" -privKey="+prik+"");
		return returnStr;
	}

	// Recover Key
	public String getRecoverKey(String mnemonicWords, String chainID) throws Exception {
		String project_net = ChainInfoVO.getNetwork(chainID);
		String netType = project_net;
		String returnStr = runCommandExec(pcwalletPath+ System.getProperty("file.separator") + "sigtool -cmd=import -netType="+netType+" -mnemonicWords='"+mnemonicWords+"'");
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
