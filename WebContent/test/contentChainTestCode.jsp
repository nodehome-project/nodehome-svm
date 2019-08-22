<%@ page language="java" contentType="text/html; charset=utf-8"  pageEncoding="utf-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="org.json.simple.JSONArray"%>
<%@ page import="org.json.simple.JSONObject"%>
<%@ page import="org.json.simple.parser.JSONParser"%>
<%@ page import="org.json.simple.parser.ParseException"%>
<%@ page import="io.nodehome.svm.common.CPWalletUtil"%>
<%@ page import="io.nodehome.svm.common.biz.CoinListVO" %>
<%@ page import="io.nodehome.svm.common.util.EtcUtils"%>
<%@ page import="io.nodehome.svm.common.util.StringUtil"%>
<%@ page import="io.nodehome.svm.common.util.DateUtil"%>
<%@ page import="io.nodehome.cmm.FouriMessageSource"%>
<%@ page import="io.nodehome.cmm.service.GlobalProperties"%>
<%@ page import="io.nodehome.svm.common.biz.ApiHelper"%>
<%@ page import="io.nodehome.svm.common.util.KeyManager"%>
<%@ page import="java.text.*" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.TimeZone" %>
<%@ page import="java.text.SimpleDateFormat" %>

<%@ include file="/inc/alertMessage.jsp" %>
<%
request.setCharacterEncoding("utf-8");
response.setContentType("text/html; charset=utf-8");

String strAWID = request.getParameter("wid");
if (strAWID == null) strAWID = "";
%>
<!DOCTYPE html>
<html>
	<head>
    <meta http-equiv="content-type" content="text/html;charset=utf-8" />
    <title></title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0; maximum-scale=1.0; minimum-scale=1.0; user-scalable=no;"/>
    
    <link href="https://fonts.googleapis.com/css?family=Baloo+Tammudu" rel="stylesheet">
  	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css">
    <link rel="stylesheet" href="/css/style.css"/>
    
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.2.1/jquery.min.js"></script>
    <script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js"></script>
    <script src="/js/loader.js"></script>
    <script src="/js/tapp_interface.js"></script>

	<style>
	#mplay {
	    position: fixed;
	    width: 320px;
	    height: 170px;
	    margin: -150px 0 0 -75px;
	    top: 50%;
	    left: 50%;
	    background:#fff;
	    border:1px solid #ccc;
	    border-radius: 20px;
	    display:none;
	}
	</style>

	<script>
	var pageIndex = 1; 
	var totalPage = 0; 
	var totalCount = 0; 
	var listCount = 0; 
	
	//$.ajaxSetup({ async:false }); // AJAX calls in order
	
	// Script to run as soon as loaded from the web
	$(function() {
	});

	var myMusicCount = 0;
	var myMusicList;
	
	// Function to call as soon as it is loaded from the App
	function AWI_OnLoadFromApp(dtype) {
		// Activate AWI_XXX method
		AWI_ENABLE = true;
		AWI_DEVICE = dtype;
		 
		j_curWID = AWI_getAccountConfig("CUR_WID");
		j_curWNM = AWI_getAccountConfig(j_curWID);

		// 내 구매 List 시작
		// ************ SVM API
		var sArgs = ["<%=KeyManager.PID%>","<%=KeyManager.VERSION%>" ,"nodehome","[\"fimusic\",\""+j_curWID+"\"]","0","9999999","1","","",""];
		sQuery = {"npid":sNpid, "order" : "desc", "parameterArgs" : sArgs};
		retData = WSI_callJsonAPI("/svm/content/getSubDataList", sQuery);
		if (retData['result'] == "OK") {
			myMusicList = retData['list'];
			myMusicCount = parseInt(retData['totalCount']);
        } else {
        	alert("error");
        	return;
        }
		// 내 구매 List 끝
		
		
		
		// Music List (음원 사이트 API에 음원 목록 요청)
		var sQuery = {"requestUrl" : "<%=GlobalProperties.getProperty("music_site_url")%>/api/fimusic/music_list","listId":"1","pageSize":"10","pageIndex":pageIndex};
		var data = WSI_callCorsJsonAPI(sQuery);
		 
		if(data['result'] != "FAIL") {
			var slist = data['list'];
			totalCount = data['totalCount'];
			listCount = data['listCount'];
			totalPage = Math.floor(totalCount / 10);
			if((totalCount % 10) > 0) totalPage++;	// 목록의 전체 페이지 수

			displayMusicTable(slist);
		} else {
			alert('error');
			return false;
		}
	}

	// 음악 리스트 페이징
	function submitList() {
		pageIndex++;
		var sQuery = {"requestUrl" : "<%=GlobalProperties.getProperty("music_site_url")%>/api/fimusic/music_list","listId":"1","pageSize":"10","pageIndex":pageIndex};
		var data = WSI_callCorsJsonAPI(sQuery);
		 
		if(data['result'] != "FAIL") {
			var slist = data['list'];
			displayMusicTable(slist);
		} else {
			alert('error');
			return false;
		}
	}
	
	function displayMusicTable(slist) {
    	var sHTML="";
    	for(var i=0; i < slist.length ; i ++) {
    		sHTML += '<tr>';
    		sHTML += '	<td style="padding:5px 5px 5px 5px;">';
    		sHTML += '		<img src="<%=GlobalProperties.getProperty("music_site_url")%>'+slist[i]['imageUrl']+'" style="max-width:50px;max-height:50px;" class="img-thumbnail"/>';
    		sHTML += '	</td>';
    		sHTML += '	<td style="padding:0px 0px 0px 10px;min-width:40px;font-weight:bold;">';
			sHTML += 		((pageIndex-1)*10)+(i+1);
			sHTML += '	</td>';
			sHTML += '	<td style="word-break:break-all;text-align:left;">';
			sHTML += '		'+slist[i]['songTitle']+'<br/>';
			sHTML += '		<span style="color:#9933ff;">'+slist[i]['artist']+'</span>';
			sHTML += '	</td>';
			sHTML += '	<td style="padding-right:10px;line-height:50px;width:70px;">';
    		sHTML += '		<a href="javascript:openMusic(\''+slist[i]['songTitle']+'\',\'<%=GlobalProperties.getProperty("music_site_url")%>'+slist[i]['simplePlayUrl']+'\')"><img src="/images/music_play.png"/></a>';

    		// 구매 이력 체크
    		var chk = true;
    		if(myMusicCount>0) {
    			for(var m = 0; m < myMusicList.length ; m++) {
    				if(myMusicList[m]['atchId'] == slist[i]['atchId'] && myMusicList[m]['atchSn'] == slist[i]['atchSn']) chk=false;
    			}	
    		}
    		if(chk)
				sHTML += '  	<a href="javascript:addData(\'buy_btn'+((pageIndex-1)*10)+(i+1)+'\',\''+slist[i]['listId']+'\',\''+slist[i]['songTitle']+'\',\''+slist[i]['atchId']+'\',\''+slist[i]['atchSn']+'\',\''+slist[i]['artist']+'\',\''+slist[i]['imageUrl']+'\',\''+slist[i]['simplePlayUrl']+'\',\''+slist[i]['playUrl']+'\')"><img src="/images/down.png"/></a>';
			
    		sHTML += '	</td>';
    		sHTML += '</tr>';
    		sHTML += '<tr><td colspan="4" style="height:1px;margin:5px;background-color:#ddd;"/></td></tr>';
    	}
    	$('#div_music_box_table > tbody:last-child').append(sHTML);
    	
    	if(totalPage <= pageIndex) $('#list_more').css('display','none');
	}

	/* 
	체인에 저장할 데이타의 구조 선언

	Key : ["mylist","", "124854123154565"]
		  ["function name", "구매자 wallet id", "record key(Timestamp)]"]
		
	value : 
		{"listId":1
		,"songTitle":"녹는 중 [Feat. Verbal Jint]"
		,"atchId":24
		,"atchSn":"1"
		,"artist":"다비치"
		,"imageUrl":"/common/file/file_down_mini.do?atchFileId=24&fileSn=0"
		,"simplePlayUrl":"/common/file/music_simple_play.do?atchFileId=24&fileSn=1"
		,"playUrl":"/api/music_play.do?atchFileId=24&fileSn=1"}
	*/

	var commit_id = "";
	var fee = "";
	var sNonce = "";
	var sNpid = "";
	
	// 음악 구매
	function addData(btn_id, listId, songTitle, atchId, atchSn, artist, imageUrl, simplePlayUrl, playUrl) {
		$('#loading').css('display','block');
		
		// 동기방식 상태라 setTimeout() 로딩바 출력 시간 지정.
		setTimeout(function () {
	
				// ************ step1 : get Nonce / SVM API
				var sQuery = {"pid":"<%=KeyManager.PID%>", "ver":"<%=KeyManager.VERSION%>", "nType":"invoke", "cType":"<%=ApiHelper.EC_CHAIN%>"};
				var retData = WSI_callJsonAPI("/svm/common/getNonce", sQuery);
				if(retData['result'] == "OK") {
					sNonce = retData['nonce'];
					sNpid = retData['npid'];
					
				} else {
					$('#loading').css('display','none');
					return false;
				}
	
				var key3 = new Date().getTime();	// 등록하는 Record의 접근 고유 index key 로 사용된다.
				// ************ step2 : get Signature / S-T API
				sQuery = ["<%=KeyManager.PID%>","<%=KeyManager.VERSION%>"
				          ,"nodehome"
				          ,"[\"fimusic\",\""+j_curWID+"\",\""+key3+"\"]"
				          ,"{\"listId\":\""+listId+"\", \"songTitle\":\""+songTitle+"\", \"atchId\":\""+atchId+"\", \"atchSn\":\""+atchSn+"\", \"artist\":\""+artist+"\", \"imageUrl\":\""+imageUrl+"\", \"simplePlayUrl\":\""+simplePlayUrl+"\", \"playUrl\":\""+playUrl+"\"}"
				          ,sNonce];
				var sigRes = AWI_getSignature(j_curWID, sQuery, "invoke","reserveSetData");
				//alert(sigRes['signature_key']);
				
				// PC Wallet 에서 getSignature 할때
				sQuery = "<%=KeyManager.VERSION%>nodehome[\"fimusic\",\""+j_curWID+"\",\""+key3+"\"]{\"listId\":\""+listId+"\", \"songTitle\":\""+songTitle+"\", \"atchId\":\""+atchId+"\", \"atchSn\":\""+atchSn+"\", \"artist\":\""+artist+"\", \"imageUrl\":\""+imageUrl+"\", \"simplePlayUrl\":\""+simplePlayUrl+"\", \"playUrl\":\""+playUrl+"\"}"+sNonce+"";
				var sigRes = AWI_getSignature(j_curWID, sQuery, "invoke","reserveSetData");
				//alert(sigRes['signature_key']);
	
				if(sigRes['result']=="OK") {
					sSig = sigRes['signature_key'];	
					
					// ************ step3 : uploadData / SVM API
					sQuery = {"npid":sNpid, "parameterArgs" : ["<%=KeyManager.PID%>","<%=KeyManager.VERSION%>"
					                  ,"nodehome"
		          			          ,"[\"fimusic\",\""+j_curWID+"\",\""+key3+"\"]"
		         			          ,"{\"listId\":\""+listId+"\", \"songTitle\":\""+songTitle+"\", \"atchId\":\""+atchId+"\", \"atchSn\":\""+atchSn+"\", \"artist\":\""+artist+"\", \"imageUrl\":\""+imageUrl+"\", \"simplePlayUrl\":\""+simplePlayUrl+"\", \"playUrl\":\""+playUrl+"\"}"
		         			          ,sNonce,sSig,j_curWID]};
					retData = WSI_callJsonAPI("/svm/content/reserveSetData", sQuery);
					sNonce = "";
					if(retData['result'] == "OK") {
						fee = retData['fee'];
						commit_id = retData['commit_id'];
						sNpid = retData['npid'];	// 취소할때 npid 지정
					} else {
						alert('error');
						commit_id = "";
						$('#loading').css('display','none');
						return false;
					}
				}
				
				if(commit_id!="" && fee!="") {
					if(confirm('수수료는 '+fee+' <%=CoinListVO.getCoinCou()%> 입니다. 구매 하시겠습니까?')) {
						actionCommit();
						$('#loading').css('display','none');
						$('#'+btn_id).css('display','none');
						return true;
					} else {
						sQuery = {"npid":sNpid, "parameterArgs" : ["<%=KeyManager.PID%>","<%=KeyManager.VERSION%>", commit_id]};
						WSI_callJsonAPI("/svm/content/removeUploadData", sQuery);
						$('#loading').css('display','none');
						return false;
					}
				}
		}, 10);
		
	}
	
	// 수수료송금 & data commit
	function actionCommit() {
		
		// ************ step1 : get Nonce / SVM API
		var sQuery = {"pid":"<%=KeyManager.PID%>", "ver":"<%=KeyManager.VERSION%>", "nType":"query"};
		var retData = WSI_callJsonAPI("/svm/common/getNonce", sQuery);
		if(retData['result'] == "OK") {
			sNonce = retData['nonce'];
		} else {
			return false;
		}

		// ************ step2 : get Signature / S-T API
		sQuery = ["<%=KeyManager.PID%>","<%=KeyManager.VERSION%>","1",sNonce];
		var sigRes = AWI_getSignature(j_curWID, sQuery,"query","getNTransHistory");
		if(sigRes['result']=="OK") {
			sSig = sigRes['signature_key'];	
			// ************ step3 : get TransHistory / SVM API
			sQuery = {"parameterArgs" : ["<%=KeyManager.PID%>","<%=KeyManager.VERSION%>","1",sNonce,sSig,j_curWID]};
			retData = WSI_callJsonAPI("/svm/common/getNTransHistory", sQuery);	// 송금용 nonce 생성
			sNonce = "";
			if(retData['result'] == "OK") {
				sNonce = retData['nonce'];
			} else {
				return '';
			}
		} else {
			sNonce = "";
		}
		
		var iNonce = sNonce;

		if(iNonce=="") {
			alert('error');
			return false;
		}
		
		var commitMemo = "buy music list";
		
		// ************ step3 : get Signature / S-T API
		sQuery = ["<%=KeyManager.PID%>","<%=KeyManager.VERSION%>",fee,commit_id,commitMemo,iNonce];
		var sigRes = AWI_getSignature(j_curWID, sQuery,"invoke","payFeeForReserve");

		if(sigRes['result']=="OK") {
			sSig = sigRes['signature_key'];	
			
			// ************ step4 : SVM API
			var sArgs = ["<%=KeyManager.PID%>","<%=KeyManager.VERSION%>",fee,commit_id,commitMemo,iNonce,sSig,j_curWID];
			sQuery = {"npid":"", "parameterArgs" : sArgs};
			retData = WSI_callJsonAPI("/svm/content/payFeeForReserve", sQuery);
			if (retData['ec'] == "0") {
				alert("success");
            	location.reload();
            } else {
            	alertMassage("<spring:message code="gtoken.msg.writeError" />("+joRes['strValue']+")");
            	return;
            }
		}
	}
	</script>
	
</head>
<body>

  	<div id="loading" class="loading" style="display:none;"><img id="loading_img" alt="loading" src="/images/viewLoading.gif" /></div>

	<div class="container">
		<p style="height:50px;">&nbsp;</p>
	    <span style="font-size:24px; font-weight:bold;">음악 차트</span>
	    <div style="width:100%; height:8px; text-align:center;"></div>
        
		<form name="frm" id="frm" method="post" onsubmit="return false;">
			<input type="hidden" name="nonce" id="nonce" />
	        <input type="hidden" name="npid" id="npid" />
	 	    
			<table style="width:100%;" cellpadding="0" cellspacing="0" border="0" id="div_music_box_table">
			<tbody></tbody>
		    </table>
		    
			<p style="width:100%;text-align:center;margin-top:10px;" id="list_more">
				<button type="button" style="margin:0 auto;width:90%;" class="btn btn-outline-success" onclick="submitList()">More ...</button>
			</p>
		</form>
				
	</div>
	
	
	<!-- 미리듣기 -->
	<div id="mplay">
		<p style="width:90%;padding:20px 0 10px 40px;font-size:12pt;font-weight:600;" id="stitle">미리듣기</p>
		<hr style="height:1px solid #aaaaaa;clear:both;margin:5px;"/>
		<div style="text-align:center;padding-top:10px;vertical-align:middle;">
		    <script>
				if (navigator.userAgent.match(/iPhone|Mobile|UP.Browser|Android|BlackBerry|Windows CE|Nokia|webOS|Opera Mini|SonyEricsson|opera mobi|Windows Phone|IEMobile|POLARIS/) != null) {
				    document.write("<audio controls='controls' id='mplayer' src='' controlsList=\"nodownload\"></audio>");
				} else {
					document.write("<audio controls='controls' id='mplayer' autoplay style='width:80%;height:50px;' source src='' type='audio/mp3'></audio>");
				}
		    </script>
			<br/>30초 미리듣기 입니다.
		</div>
		<div style="position:absolute;right:10px;top:10px;"><a href="javascript:closeMpop()"><img src="/images/x_btn.gif"/></a></div>
	</div>
	<script>
	audio = $("<audio>").attr("id", "mplayer").attr("preload", "auto");
	function openMusic(title, pUrl) {
		var obj = $('#mplay');
        var left = ( $(document).scrollLeft() + ($(window).width() - (obj.width()/2)) / 2 );
        //var top = ( $(document).scrollTop() + ($(window).height() - (obj.height()/2)) / 2 );
		obj.css({display:'block', left: left});

		$('#stitle').html(title);
		$("#mplayer").attr("src", pUrl);
		var audio = document.getElementById('mplayer');
		audio.play();
	}
	function closeMpop() {
		var audio = document.getElementById('mplayer');
		audio.pause();
		$('#mplay').css('display','none');
	}
	
	$(window).unload( function () {
		var audio = document.getElementById('mplayer');
		audio.stop();
	} );


	</script>
	
	
	
</body>
</html>