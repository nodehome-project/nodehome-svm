package io.nodehome.svm.platform.service;

import java.io.Serializable;
import java.util.List;

public class WalletVO implements Serializable {
	
	List<String> args = null;
	String npid = "";
	String wid = "";
	String nonce = "";
	
	public List<String> getArgs() {
		return args;
	}
	public void setArgs(List<String> args) {
		this.args = args;
	}
	public String getNpid() {
		return npid;
	}
	public void setNpid(String npid) {
		this.npid = npid;
	}
	public String getWid() {
		return wid;
	}
	public void setWid(String wid) {
		this.wid = wid;
	}
	public String getNonce() {
		return nonce;
	}
	public void setNonce(String nonce) {
		this.nonce = nonce;
	}
	
		
}
