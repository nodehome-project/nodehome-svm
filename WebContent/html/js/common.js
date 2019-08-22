
function unixTimeToDateTime(unixtime) {
	var d = new Date(unixtime *1000);
	var datetime = d.getFullYear()+"-"+("0"+(d.getMonth()+1)).slice(-2)+"-"+("0"+d.getDate()).slice(-2)+" "
	  +("0"+d.getHours()).slice(-2)+":"+("0"+d.getMinutes()).slice(-2)+":"+("0"+d.getSeconds()).slice(-2);
	return datetime;
}

function getTimestampFromDatetime(dateString){
 	// dateString (YYYY-MM-DD HH24:MI)
 	var dateParts = dateString.split(' '), timeParts = dateParts[1].split(':'), date;
  		dateParts = dateParts[0].split('-');
  		date = new Date(dateParts[0],parseInt(dateParts[1],10) -1,dateParts[2], timeParts[0],timeParts[1]);
 	return date.getTime();  
}

// input element : y Input clear default
// onFocus()Connect to the event and delete it if it is the default.
// <input onFocus='clearText(this);' >
function clearText(y)
{ 
	if (y.defaultValue==y.value) 
		y.value = ""; 
} 

//input element : Make sure y value is normal
function checkText(y)
{ 
  var v = y.value;
  v = v.trim();
  if (y.defaultValue==v) 
		return false; 
	if (v == "")
	  return false;
	return true;
}

//Check if string value is normal
function checkValue(str)
{ 
  if(str == null)
	  return false;
  var v = str.trim();
	if (v == "")
	  return false;
	return true;
}

function checkMaxLength(obj)
{
	 var maxLength = parseInt(obj.getAttribute('maxlength'));
	 if(obj.value.length > maxLength)
    {
		 alert("The number of characters is limited to "+ maxLength +" characters.");
		 obj.value = obj.value.substring(0,maxLength);
    }
}

// String encoding to avoid conflict with html
function htmlEscape(str)
{
    return str
        .replace(/&/g, '&amp;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;');
}

// Restore html encoding characters
function htmlUnescape(str)
{
    return str
        .replace(/&quot;/g, '"')
        .replace(/&#39;/g, "'")
        .replace(/&lt;/g, '<')
        .replace(/&gt;/g, '>')
        .replace(/&amp;/g, '&');
}

// Check your browser type
function getBrower()
{
	var agent = navigator.userAgent.toLowerCase();
	var appName =  navigator.appName.toLowerCase();
	if (agent.indexOf("chrome") != -1) {
		return "chrome";
	}
	else if (agent.indexOf("safari") != -1) {
		return "safari";
	}
	else if (agent.indexOf("firefox") != -1) {
		return "firefox";
	}
	else if ( appName.indexOf("explorer") != -1)
	{
		return "msie";
	}
	else if ( appName == 'netscape' && agent.search('trident') != -1) 
	{
		return "msie";
	}
	return "unknown";
}

// Server Log
function serverLog(log,urlLogger,fnResult)
{
	var sData = 'log=' + encodeURI(log);
	$.ajax({
	     type:"POST",  
	     url: urlLogger,
	     data: sData,
	     dataType: "text",
	     async: false,
	     success:function(data,status,xhr)
	     {
	    	if(fnResult != null)
	    	{
		     	if(status == 'success')
		     	{
		     		fnResult("[success] status:"+status); 
		     	}
		     	else
		     	{
		     		fnResult("[fail] status:"+status+" / "+"message:"+xhr.responseText); 
		     	}
	    	}
	     },   
	     error:function(request,status,error)
	     { 
			
	    	 if(fnResult != null)
	    	 {
	    		//$('#id_status').text("[error] status:"+status+" / "+"message:"+request.responseText+" / "+"error:"+error); 
		    	 fnResult("[error] status:"+status+" / error:"+error); 	 
	    	 }
	     } 
	});
}

function gfnAddComma(obj) {
	var str = "" + obj.toString().replace(/,/gi,''); // 콤마 제거
	var regx = new RegExp(/(-?\d+)(\d{3})/);
	var bExists = str.indexOf(".",0);
	var strArr = str.split('.');
	while(regx.test(strArr[0])){
		strArr[0] = strArr[0].replace(regx,"$1,$2");
	}
	if (bExists > -1)
		obj = strArr[0] + "." + strArr[1];
	else
		obj = strArr[0];
	return obj;
}

String.prototype.trim = function(){
	return this.replace(/^\s+|\s+$/g, "");
}

String.prototype.replaceAll = function(src, repl){
	 var str = this;
	 if(src == repl){return str;}
	 while(str.indexOf(src) != -1) {
	 	str = str.replace(src, repl);
	 }
	 return str;
}

// * is big => right : -1  = : 0   left : 1
versionCompare = function(left, right) {                                                                            
    if (typeof left + typeof right != 'stringstring')                                                                   
        return false;                                                                                                          
                                                                                                                                   
    var a = left.split('.')                                                                                                     
    ,   b = right.split('.')                                                                                                    
    ,   i = 0, len = Math.max(a.length, b.length);                                                                
                                                                                                                                   
    for (; i < len; i++) {                                                                                                     
        if ((a[i] && !b[i] && parseInt(a[i]) > 0) || (parseInt(a[i]) > parseInt(b[i]))) {                      
            return 1;                                                                                                           
        } else if ((b[i] && !a[i] && parseInt(b[i]) > 0) || (parseInt(a[i]) < parseInt(b[i]))) {             
            return -1;                                                                                                          
        }                                                                                                                          
    }                                                                                                                              
                                                                                                                                   
    return 0;                                                                                                                   
}