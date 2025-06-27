<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>입출고 유형별 발생현황</title>
<link rel="stylesheet" href="../CSS/CalcPrice.css?after">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.18.5/xlsx.full.min.js"></script>
<script src="http://code.jquery.com/jquery-latest.js"></script>
<script>
var TotalCount = [];
document.addEventListener('DOMContentLoaded', function() {
    const tbody = document.querySelector('.InfoTable-Body');
    const thead = document.querySelector('.InfoTable-Header');

    tbody.addEventListener('scroll', function() {
        thead.scrollLeft = tbody.scrollLeft; // thead의 스크롤 위치를 직접 설정
    });
});
function PopupPosition(popupWidth, popupHeight) {
    var dualScreenLeft = window.screenLeft !== undefined ? window.screenLeft : window.screenX;
    var dualScreenTop = window.screenTop !== undefined ? window.screenTop : window.screenY;
    
    var width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
    var height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;
    var xPos, yPos;
    if (width == 2560 && height == 1440) {
        xPos = (2560 / 2) - (popupWidth / 2);
        yPos = (1440 / 2) - (popupHeight / 2);
    } else if (width == 1920 && height == 1080) {
        xPos = (1920 / 2) - (popupWidth / 2);
        yPos = (1080 / 2) - (popupHeight / 2);
    } else {
        var monitorWidth = 2560;
        var monitorHeight = 1440;
        xPos = (monitorWidth / 2) - (popupWidth / 2) + dualScreenLeft;
        yPos = (monitorHeight / 2) - (popupHeight / 2) + dualScreenTop;
    }
    return { x: xPos, y: yPos };
}
function InfoSearch(field){
    event.preventDefault();
    var popupWidth = 500;
    var popupHeight = 600;
    
    var ComCodeData = $('.ComCode').val();
    var MatTypeData = $('.MatType').val();
    var PlantData = $('.Plant').val();
    var MVData = $('.MVtype').val();
    var FromData = $('.FromDate').val();
    var EndData = $('.EndDate').val();
    
    var position = PopupPosition(popupWidth, popupHeight);
    
    var MoveType = event.target.name;
    
    switch(field){
    case "ComSearch":
        window.open("${contextPath}/Pop/ComSerach.jsp", "PopUp01", "width=" + popupWidth + ",height=" + popupHeight + ",left=" + position.x + ",top=" + position.y);
    break;
    case "PlantSearch":
        window.open("${contextPath}/Pop/PlantSearch.jsp?Comcode=" + ComCodeData, "PopUp02", "width=" + popupWidth + ",height=" + popupHeight + ",left=" + position.x + ",top=" + position.y);
    break;
    case "MatSearch":
        window.open("${contextPath}/Pop/MatSearch.jsp?Comcode=" + ComCodeData, "PopUp03", "width=" + popupWidth + ",height=" + popupHeight + ",left=" + position.x + ",top=" + position.y);
    break;
    case "MVSearch":
        window.open("${contextPath}/Pop/MVSearch.jsp?Comcode=" + ComCodeData, "PopUp04", "width=" + popupWidth + ",height=" + popupHeight + ",left=" + position.x + ",top=" + position.y);
    break;
    }
}
function InitialTable(){
	$('.InfoTable-Body').empty();
	for (let i = 0; i < 50; i++) {
		const row = $('<tr></tr>');
		for (let j = 0; j < 16; j++) {
			row.append('<td></td>');
		}
	$('.InfoTable-Body').append(row);
	}
}
function DateSetting(){
	var CurrentDate = new Date();
	var today = CurrentDate.getFullYear() + '-' + ('0' + (CurrentDate.getMonth() + 1)).slice(-2) + '-01';
	$('.FromDate').val(today);
}
$(document).ready(function() {
	DateSetting();
	InitialTable();
	var ComData = null; // 회사
	var MTypeData = null; // 재교유형
	var PlantData = null; // 공장
	var MatData = null; // 자재코드
	var MVData = null; // 입출고구분
	var FromData = null // 거래일자
	var EndData = null; // 거래일자
	var KeyInfoList = {};
	
    $('.SearBtn').click(function(){
    	$('.KeyOption').each(function(){
		    var name = $(this).attr('name');
		    var value = $(this).val();
		    console.log(name + ' : ' + value);
		    KeyInfoList[name] = value;
		});
    	var pass = true;
		$.each(KeyInfoList,function(key, value){
			if(key === 'MatCode'){
				return true;
			}
			if (value == null || value === '') {
    	        pass = false;
    	        return false;
    	    }
		})
		if (!pass) {
			alert('모든 필수 항목을 모두 입력해주세요.');
		}else{
			$('.InfoTable-Body').empty();
			$.ajax({
				url : '${contextPath}/CostCalc/RawmLineDataLoading.do',
				type : 'POST',
				data : JSON.stringify(KeyInfoList),
				contentType: 'application/json; charset=utf-8',
				dataType: 'json',
				async: false,
				success: function(data){
					console.log(data.result);
					if(data.result === "success"){
						if(data.List.length > 0) {
							for(var i = 0; i < data.List.length; i++){
								var row = '<tr>' +
								'<td>' + (data.List[i].docnum || 'N/A') + '</td>' + 
		    			        '<td>' + (data.List[i].transactiondate || 'N/A') + '</td>' +
		    			        '<td>' + (data.List[i].matcode || 'N/A') + '</td>' +
		    			        '<td>' + (data.List[i].matdesc || 'N/A') + '</td>' +
								'<td>' + (data.List[i].movetype || 'N/A') + '</td>' + 
			    			    '<td>' + (data.List[i].mattype || 'N/A') + '</td>' +
			    			    '<td>' + (data.List[i].lotnum || 'N/A') + '</td>' + 
			    			    '<td>' + (data.List[i].quantity || 'N/A') + '</td>' +
			    			    '<td>' + (data.List[i].workordnum || 'N/A') + '</td>' +
		    			        '<td>' + (data.List[i].procuordnum ?? 'N/A') + '</td>' + 
		    			        '<td>' + (data.List[i].salesordnum ?? 'N/A') + '</td>' + 
		    			        '<td>' + (data.List[i].vendcode ?? 'N/A') + '</td>' + 
		    			        '<td>' + (data.List[i].vendDesc ?? 'N/A') + '</td>' + 
		    			        '<td>' + (data.List[i].storcode ?? 'N/A') + '</td>' + 
		    			        '<td>' + (data.List[i].stordesc ?? 'N/A') + '</td>' + 
		    			        '<td>' + (data.List[i].plant ?? 'N/A') + '</td>' + 
			    			    '</tr>';
	     			        $('.InfoTable-Body').append(row);
							}
						}
					}
				},
				error: function(jqXHR, textStatus, errorThrown){
					alert('오류 발생: ' + textStatus + ', ' + errorThrown);
		    	}
	    	});
		}
    })
    
});
</script>
<%
String UserId = (String)session.getAttribute("id");
String userComCode = (String)session.getAttribute("depart");
String UserIdNumber = (String)session.getAttribute("UserIdNumber");
%>
</head>
<body>
<jsp:include page="../Header.jsp"></jsp:include>
<div class="Hall">
	<div class="MainHall">
		<div class="Title">검색 항목</div>
		<div class="MainHallArray">
			<div class="Main-Colume">
				<label>회사 : </label>
				<input type="text" class="ComCode KeyOption" name="ComCode" onclick="InfoSearch('ComSearch')" readonly>
			</div>
			<div class="Main-Colume">
				<label>재고유형 : </label>
				<input type="text" class="MatType KeyOption" name="MatType" value="RAWM" readonly>
			</div>
			<div class="Main-Colume">
				<label>❗공장 : </label>
				<input type="text" class="PlantCode KeyOption" name="PlantCode" id="Plant" onclick="InfoSearch('PlantSearch')" readonly>
			</div>
			<div class="Main-Colume">
				<label>자재 : </label>
				<input type="text" class="MatCode KeyOption" name="MatCode" id="MatCode" readonly>
			</div>
			<div class="Main-Colume">
				<label>입출고구분 : </label>
				<input type="text" class="MVtype KeyOption" name="MVtype" readonly value="GR10">
			</div>
			<div class="Main-Colume">
				<label>❗거래일자(From) : </label>
				<input type="date" class="FromDate KeyOption" name="FromDate">
			</div>
			
			<div class="Main-Colume">
				<label>❗거래일자(End) : </label>
				<input type="date" class="EndDate KeyOption" name="EndDate">
			</div>
		</div>
		
		<div class="BtnArea">
			<button class="CLBtn">결산</button>
			<button class="SearBtn">조회</button>
		</div>
	</div>
	
	<div class="SubHall">
		<div class="Title">입출고 유형별 발생현황</div>
		<table class="InfoTable">
			<thead class="InfoTable-Header MTCase">
				<tr>
					<th>번호</th><th>입출고일자</th><th>품목번호</th><th>품목명</th><th>입출고구분</th>
					<th>재고유형</th><th>Lot번호</th><th>수량</th><th>작지번호</th><th>발주번호</th><th>수주번호</th>
					<th>거래처</th><th>거래처명</th><th>창고</th><th>창고명</th><th>공장</th>
				</tr>
 			</thead>
			<tbody class="InfoTable-Body MTCaseBody">
			</tbody>
		</table>
	</div>
</div>
</body>
</html>