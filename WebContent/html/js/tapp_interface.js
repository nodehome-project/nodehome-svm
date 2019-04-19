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
	        alert("error: "+data.responseText+" status: "+status+" er:"+er);
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
	        alert("error: "+data.responseText+" status: "+status+" er:"+er);
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
function AWI_getSignature(walletId, pArgs, queryType, funcName) {
	var sReturn = "{ \"result\":\"FAIL\" , \"value\":{} }"

	var joCmd = null;
	var params = new Object();
	params['cmd'] = "getSignature";
	params['walletId'] = walletId;
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

function AWI_runTransaction(transType, jaArgs, callbackFunc, displayTitle, displayDesc, displayParam, walletId) {
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
	params['walletId'] = walletId;
	params['args'] = jaArgs;
	joCmd = {func:params};
	if(AWI_DEVICE == 'ios') {
		sReturn =  prompt(JSON.stringify(joCmd));	
	} else if(AWI_DEVICE == 'android') {
		sReturn =  window.AWI.callAppFunc(JSON.stringify(joCmd));	
	} else { // windows
		sReturn =  window.external.CallAppFunc(JSON.stringify(joCmd));	
	}
}

//showQRCode
function AWI_showQRCode(walletId, walletName) {
	var sReturn = "{ \"result\":\"FAIL\" }"
	if(!AWI_ENABLE) return;
	var joCmd = null;
	var params = new Object();
	params['cmd'] = "showQRCode";
	params['walletId'] = walletId;
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
function AWI_getEncryptedText(walletId, plainText) {
	var sReturn = "{ \"result\":\"FAIL\" }"
	if(!AWI_ENABLE) return;
	var joCmd = null;
	var params = new Object();
	params['cmd'] = "getEncryptedText";
	params['walletId'] = walletId;
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
function AWI_getDecryptedText(walletId, encryptedText) {
	var sReturn = "{ \"result\":\"FAIL\" }"
	if(!AWI_ENABLE) return;
	var joCmd = null;
	var params = new Object();
	params['cmd'] = "getDecryptedText";
	params['walletId'] = walletId;
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

function AWI_getNetID() {
	if(!AWI_ENABLE) return;
	 var joCmd = null;
	 var params = new Object();
	 params['cmd'] = "getNetID";
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
