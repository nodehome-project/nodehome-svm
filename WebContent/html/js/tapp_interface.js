var AWI_ENABLE = false;
var AWI_DEVICE = 'ios';

//-----------------------------------------------------
// iphone interface 
// IPHONE callback response function control
var  _timeIOSCallbackWaitEnd = Date.now();  // Response timeout time
var  _boolNeedIOSCallbackWait = false;       // Need to wait
var  _strIOSCallbackValue = "";                  // Returned string

// Calling ajax url with JSON request value
function WSI_callJsonAPI(pUrl, psQuery) {
	var retData = null;
	$.ajax({
	    url: pUrl, 
	    type: 'POST',
	    data: JSON.stringify(psQuery),
	    dataType: 'json', 
	    async:false,
		contentType:"application/json;charset=UTF-8",
	    success: function(data) { 
	    	retData = data;
	    },
	    error:function(data,status,er) { 
	    	//alert("error: "+data.responseText+" status: "+status+" er:"+er);
	    }
	});
	return retData;
}

// Calling other domain json object request, response
/*
 * Example
		var sQuery = {"requestUrl" : "<%=GlobalProperties.getProperty("music_site_url")%>/api/fimusic/music_list","listId":"1","pageSize":"10","pageIndex":pageIndex};
		var data = WSI_callCorsJsonAPI(sQuery);
 */
function WSI_callCorsJsonAPI(psQuery) {
	var retData = null;
	$.ajax({
	    url: "/svm/common/callCorsUrl", 
	    type: 'POST',
	    data: JSON.stringify(psQuery),
	    dataType: 'json',
	    async:false,
		contentType:"application/json;charset=UTF-8",
	    success: function(data) { 
			if(data['result'] != "FAIL") {
				retData = data;
			} else {
				alert('error');
				return false;
			}
	    },
	    error:function(data,status,er) { 
	    	// alert("error: "+data.responseText+" status: "+status+" er:"+er);
	    }
	});
	return retData;
}

//Information setting
function AWI_setConfig(strName,strValue) {
	if(!AWI_ENABLE) return;
	var joCmd = null;
	var params = new Object();
	params['cmd'] = "setConfig";
	params['key'] = strName;
	params['value'] = strValue;
	joCmd = {func:params};
	if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	} else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	} else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	}
	var joReturn = JSON.parse(sReturn);
	return joReturn['result'];
}

//And inquires the setting information value set in the phone.
function AWI_getConfig(strName) {
	var sReturn = "{ \"result\":\"FAIL\", \"value\":\"\" }"
	if(!AWI_ENABLE) return "";
	var joCmd = null;
	var params = new Object();
	params['cmd'] = "getConfig";
	params['key'] = strName;
	joCmd = {func:params};
	if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	} else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	} else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	}
	var joReturn = JSON.parse(sReturn);
	return joReturn['value'];
}

//Information setting
function AWI_setAccountConfig(strName,strValue) {
	if(!AWI_ENABLE) return;
	var joCmd = null;
	var params = new Object();
	params['cmd'] = "setAccountConfig";
	params['key'] = strName;
	params['value'] = strValue;
	joCmd = {func:params};
	if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	} else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	} else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	}
	var joReturn = JSON.parse(sReturn);
	return joReturn['result'];
}

//And inquires the setting information value set in the phone.
function AWI_getAccountConfig(strName) {
	var sReturn = "{ \"result\":\"FAIL\", \"value\":\"\" }"
	if(!AWI_ENABLE) return "";
	var joCmd = null;
	var params = new Object();
	params['cmd'] = "getAccountConfig";
	params['key'] = strName;
	joCmd = {func:params};
	if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	} else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	} else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	}
	var joReturn = JSON.parse(sReturn);
	return joReturn['value'];
}

//Verify the password set on the phone.
function AWI_checkPassword(strPW) {
	var joReturn = null;
	if(!AWI_ENABLE) return "";
	var joCmd = null;
	var params = new Object();
	params['cmd'] = "checkPassword";
	params['password'] = strPW;
	joCmd = {func:params};
	if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	} else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	} else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	}
	joReturn = JSON.parse(sReturn);
	return joReturn['result'];
}

// Save your new password to phone
function AWI_setPassword(strPW) {
	var sReturn = "{ \"result\":\"FAIL\", \"value\":\"\" }"
	if(!AWI_ENABLE) return "";
	var joCmd = null;
	var params = new Object();
	params['cmd'] = "setPassword";
	params['password'] = strPW;
	joCmd = {func:params};
	if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	} else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	} else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	}
	var joReturn = JSON.parse(sReturn); // { "result":"OK", "value":true }
	return joReturn['result'];
}

// Request to check data in terminal node.
function AWI_isSetPassword() {
	if(!AWI_ENABLE) return;
	var joCmd = null;
	var params = new Object();
	params['cmd'] = "isSetPassword";
	joCmd = {func:params};
	if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	} else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	} else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	}
	var joReturn = JSON.parse(sReturn);
	return joReturn['result'];
}

//Create a new account
//sOwner: owner name, same as account name, later account name can be changed
//By default, 100 coins are created.
function AWI_newWallet() {
	var sReturn = "{ \"result\":\"FAIL\" }"
	if(!AWI_ENABLE) return;
    var joCmd = null;
    var params = new Object();
    params['cmd'] = "newWallet";
    joCmd = {func:params};
	if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	} else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	} else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	}
	return sReturn;
}

// Request to delete wallet in terminal node. Call backup wallet activtiy to save paper wallet if need it.
function AWI_deleteWallet(walletID) {
	var sReturn = "{ \"result\":\"FAIL\" }"
	if(!AWI_ENABLE) return;
    var joCmd = null;
    var params = new Object();
    params['cmd'] = "deleteWallet";
    params['walletID'] = walletID;
    joCmd = {func:params};
	if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	} else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	} else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	}
	return sReturn;
}

//Save account information on mobile phone
function AWI_notiAccount(aid, wid, cid, pname) {
	var sReturn = "{ \"result\":\"FAIL\" }"
	if(!AWI_ENABLE) return;
    var joCmd = null;
    var params = new Object();
    params['result'] = "OK";
    params['cmd'] = "notiAccount";
    params['aid'] = aid;
    params['wid'] = wid;
    params['cid'] = cid;
    params['wname'] = pname;
    joCmd = {func:params};
	if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	} else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	} else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	}
	return sReturn;
}

// Create Signature
function AWI_getSignature(walletID, pArgs, queryType, funcName) {
	var sReturn = "{ \"result\":\"FAIL\" , \"value\":{} }"

	var joCmd = null;
	var params = new Object();
	params['cmd'] = "getSignature";
	params['walletID'] = walletID;
	params['args'] = pArgs;
	params['query_type'] = queryType;
	params['func_name'] = funcName;
	joCmd = {func:params};
	 
	if(!AWI_ENABLE) return;
	if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	} else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	} else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	}
	 var joReturn = JSON.parse(sReturn);
	return joReturn;
}

//Create Signature
function AWI_getWalletList() {
	var sReturn = "{ \"result\":\"FAIL\" , \"value\":{} }"

	if(!AWI_ENABLE) return;
	 var joCmd = null;
	 var params = new Object();
	 params['cmd'] = "getWalletList";
	 joCmd = {func:params};
		if(AWI_DEVICE == 'ios') {
			sReturn =  prompt(JSON.stringify(joCmd));	
		} else if(AWI_DEVICE == 'android') {
			sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
		} else { // windows
			sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
		}
	return sReturn;
}

// Select the service list and save the settings to the terminal.
function AWI_setServiceList(serviceIDs) {
	var sReturn = "{ \"result\":\"FAIL\" , \"value\":{} }"

	 var jsonObj = $.parseJSON('[' + serviceIDs + ']');
	 var joCmd = null;
	 var params = new Object();
	 params['cmd'] = "setServiceList";
	 params['list'] = jsonObj;
	 joCmd = {func:params};
	 
	if(!AWI_ENABLE) return;
	if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	} else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	} else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	}
	 var joReturn = JSON.parse(sReturn);
	return joReturn;
}

function AWI_runTransaction(transType, jaArgs, callbackFunc, displayTitle, displayDesc, displayParam, walletID, fee) {
	var sReturn = "{ \"result\":\"FAIL\" , \"value\":{} }"
	if(!AWI_ENABLE) return;
	var joCmd = null;
	var params =  new Object();
	params['cmd'] = "runTransaction";
	params['transType'] = transType;
	params['callbackFunc'] = callbackFunc;
	params['displayTitle'] = displayTitle;
	params['displayDesc'] = displayDesc;
	params['displayArgs'] = displayParam;
	params['walletID'] = walletID;
	params['displayFee'] = fee;
	params['args'] = jaArgs;
	joCmd = {func:params};
	if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	} else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	} else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	}
	return sReturn;
}

// [step 1] Terminal Send Coin Confirm
function AWI_transferWebTransactionConfirm(transType, jaArgs, walletID, callbackFunc) {
	var sReturn = "{ \"result\":\"FAIL\" , \"value\":{} }"
	if(!AWI_ENABLE) return;
	var joCmd = null;
	var params =  new Object();
	params['cmd'] = "transferWebTransactionConfirm";
	params['transType'] = transType;
	params['walletID'] = walletID;
	params['args'] = jaArgs;
	params['callbackFunc'] = callbackFunc;
	joCmd = {func:params};
	if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	} else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	} else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	}
}

// [step 2] Terminal Send Coin Process
function AWI_transferWebTransaction(transType, jaArgs, walletID, callbackFunc) {
	var sReturn = "{ \"result\":\"FAIL\" , \"value\":{} }"
	if(!AWI_ENABLE) return;
	var joCmd = null;
	var params =  new Object();
	params['cmd'] = "transferWebTransaction";
	params['transType'] = transType;
	params['walletID'] = walletID;
	params['args'] = jaArgs;
	params['callbackFunc'] = callbackFunc;
	joCmd = {func:params};
	if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	} else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	} else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	}
}

//backup
function AWI_setBackup(walletID) {
	var sReturn = "{ \"result\":\"FAIL\" }"
	if(!AWI_ENABLE) return;
	var joCmd = null;
	var params = new Object();
	params['cmd'] = "backup";
	params['walletID'] = walletID;
	joCmd = {func:params};
	if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	} else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	} else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	}
	return sReturn;
}

//restore
function AWI_setRestore(walletID) {
	var sReturn = "{ \"result\":\"FAIL\" }"
	if(!AWI_ENABLE) return;
	var joCmd = null;
	var params = new Object();
	params['cmd'] = "restore";
	joCmd = {func:params};
	if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	} else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	} else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	}
	return sReturn;
}

//showQRCode
function AWI_showQRCode(walletID, walletName) {
	var sReturn = "{ \"result\":\"FAIL\" }"
	if(!AWI_ENABLE) return;
	var joCmd = null;
	var params = new Object();
	params['cmd'] = "showQRCode";
	params['walletID'] = walletID;
	params['walletName'] = walletName;
	joCmd = {func:params};
	if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	} else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	} else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	}
	return sReturn;
}

/* Request to encrypt text via wallet id.
 * 
 * Example - 
		var aaa = "테스트지갑";
		var aaa2 = AWI_getEncryptedText(j_curWID, aaa);
		gg = JSON.parse(aaa2);
		alert(gg.value);
		bhbh = gg.value;
		var kjkji = AWI_getDecryptedText(j_curWID,bhbh);
		gg = JSON.parse(kjkji);
		alert(gg.value);
 */
function AWI_getEncryptedText(walletID, plainText) {
	var sReturn = "{ \"result\":\"FAIL\" }"
	if(!AWI_ENABLE) return;
	var joCmd = null;
	var params = new Object();
	params['cmd'] = "getEncryptedText";
	params['walletID'] = walletID;
	params['plainText'] = plainText;
	joCmd = {func:params};
	if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	} else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	} else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	}
	return sReturn;
}

// Request to decrypt text via wallet id.
function AWI_getDecryptedText(walletID, encryptedText) {
	var sReturn = "{ \"result\":\"FAIL\" }"
	if(!AWI_ENABLE) return;
	var joCmd = null;
	var params = new Object();
	params['cmd'] = "getDecryptedText";
	params['walletID'] = walletID;
	params['encryptedText'] = encryptedText;
	joCmd = {func:params};
	if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	} else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	} else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	}
	return sReturn;
}

function AWI_isCheckedPassword() {
	var sReturn = "{ \"result\":\"FAIL\", \"value\":false }"
	if(!AWI_ENABLE) return false;

	var joCmd = null;
	var params = new Object();
	params['cmd'] = "isCheckedPassword";
	joCmd = {func:params};
	if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	} else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	} else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	}
	var joReturn = JSON.parse(sReturn); // { "result":"OK", "value":true }
	return joReturn['result'];
}

function AWI_isAbleFingerprint() {
	var sReturn = "{ \"result\":\"FAIL\" , \"value\":{} }"

	if(!AWI_ENABLE) return;
	 var joCmd = null;
	 var params = new Object();
	 params['cmd'] = "isAbleFingerprint";
	 joCmd = {func:params};
		if(AWI_DEVICE == 'ios') {
			sReturn =  prompt(JSON.stringify(joCmd));	
		} else if(AWI_DEVICE == 'android') {
			sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
		} else { // windows
			sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
		}
	return sReturn;
}
function AWI_showFingerprint() {
	var sReturn = "{ \"result\":\"FAIL\" , \"value\":{} }"

	if(!AWI_ENABLE) return;
	 var joCmd = null;
	 var params = new Object();
	 params['cmd'] = "showFingerprint";
	 joCmd = {func:params};
		if(AWI_DEVICE == 'ios') {
			sReturn =  prompt(JSON.stringify(joCmd));	
		} else if(AWI_DEVICE == 'android') {
			sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
		} else { // windows
			sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
		}
	return sReturn;
}
function AWI_setAppTitle(title) {
	var sReturn = "{ \"result\":\"FAIL\" , \"value\":{} }"

	if(!AWI_ENABLE) return;
	 var joCmd = null;
	 var params = new Object();
	 params['cmd'] = "setAppTitle";
	 params['title'] = title;
	 joCmd = {func:params};
		if(AWI_DEVICE == 'ios') {
			sReturn =  prompt(JSON.stringify(joCmd));	
		} else if(AWI_DEVICE == 'android') {
			sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
		} else { // windows
			sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
		}
}
function AWI_setAppTitleColor(color1, color2, orientation) {
	var sReturn = "{ \"result\":\"FAIL\" , \"value\":{} }"

	if(!AWI_ENABLE) return;
	 var joCmd = null;
	 var params = new Object();
	 params['cmd'] = "setAppTitleColor";
	 params['startColor'] = color1;
	 params['endColor'] = color2;
	 params['orientation'] = orientation;
	 joCmd = {func:params};
		if(AWI_DEVICE == 'ios') {
			sReturn =  prompt(JSON.stringify(joCmd));	
		} else if(AWI_DEVICE == 'android') {
			sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
		} else { // windows
			sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
		}
}

function AWI_logout() {
	if(!AWI_ENABLE) return;
	 var joCmd = null;
	 var params = new Object();
	 params['cmd'] = "logout";
	 joCmd = {func:params};
	 if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	 } else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	 } else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	 }
}

function AWI_callUrl(host) {
	if(!AWI_ENABLE) return;
	 var joCmd = null;
	 var params = new Object();
	 params['cmd'] = "callUrl";
	 params['host'] = host;
	 joCmd = {func:params};
	 if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	 } else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	 } else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	 }
}

function AWI_openAppByHost(host) {
	if(!AWI_ENABLE) return;
	 var joCmd = null;
	 var params = new Object();
	 params['cmd'] = "openAppByHost";
	 params['host'] = host;
	 joCmd = {func:params};
	 if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	 } else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	 } else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	 }
}

function AWI_showSettingView() {
	if(!AWI_ENABLE) return;
	 var joCmd = null;
	 var params = new Object();
	 params['cmd'] = "showSettingView";
	 joCmd = {func:params};
	 if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	 } else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	 } else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	 }
}

function AWI_openServiceApp(serviceID) {
	if(!AWI_ENABLE) return;
	 var joCmd = null;
	 var params = new Object();
	 params['cmd'] = "openServiceApp";
	 params['serviceID'] = serviceID;
	 joCmd = {func:params};
	 if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	 } else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	 } else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	 }
}

function AWI_closeServiceApp() {
	if(!AWI_ENABLE) return;
	 var joCmd = null;
	 var params = new Object();
	 params['cmd'] = "closeServiceApp";
	 params['isLauncher'] = "N";
	 joCmd = {func:params};
	 if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	 } else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	 } else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	 }
}

function AWI_setTerminatePath(path) {
	if(!AWI_ENABLE) return;
	 var joCmd = null;
	 var params = new Object();
	 params['cmd'] = "setTerminatePath";
	 params['path'] = path;
	 joCmd = {func:params};
	 if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	 } else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	 } else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	 }
}

function AWI_getAPIVersion() {
	var sReturn = "{ \"result\":\"FAIL\" }"
	if(!AWI_ENABLE) return;
	 var joCmd = null;
	 var params = new Object();
	 params['cmd'] = "getAPIVersion";
	 joCmd = {func:params};
	 if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	 } else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	 } else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	 }
	 return sReturn;
}

function AWI_getSeedHostInfo() {
	var sReturn = "{ \"result\":\"FAIL\" }"

	if(!AWI_ENABLE) return;
	var joCmd = null;
	var params = new Object();
	params['cmd'] = "getSeedHostInfo";
	joCmd = {func:params};
	if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	} else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	} else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	}
	var joReturn = JSON.parse(sReturn); // { "result":"OK", "value":true }
	return joReturn;
}

function AWI_goHubPage() {
	var sReturn = "{ \"result\":\"FAIL\" }"

	if(!AWI_ENABLE) return;
	 var joCmd = null;
	 var params = new Object();
	 params['cmd'] = "goHubPage";
	 joCmd = {func:params};
		if(AWI_DEVICE == 'ios') {
			sReturn =  prompt(JSON.stringify(joCmd));	
		} else if(AWI_DEVICE == 'android') {
			sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
		} else { // windows
			sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
		}
	return sReturn;
}

function AWI_getDevModeState() {
	var sReturn = "{ \"result\":\"FAIL\" }"

	if(!AWI_ENABLE) return;
	 var joCmd = null;
	 var params = new Object();
	 params['cmd'] = "getDevModeState";
	 joCmd = {func:params};
	 if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	 } else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	 } else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	 }
	var joReturn = JSON.parse(sReturn); // { "result":"OK", "value":true }
	return joReturn;
}