<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<link rel="stylesheet" href="${contextPath}/CSS/Common2.css?after">
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>공정제품원가배부</title>
</head>
<body>
<script>
function InitialTable(){
	$('.ACInputTable-Body').empty();
	var HeaderLength = $('thead.ACInputTable-Header th').length;
	for (let i = 0; i < 1000; i++) {
        const row = $('<tr></tr>');
        for (let j = 0; j < HeaderLength; j++) {
            row.append('<td></td>');
        }
        $('.ACInputTable-Body').append(row);
    }
}
function DateSetting(){
	var CurrentDate = new Date();
	var today = CurrentDate.getFullYear() + '-' + ('0' + (CurrentDate.getMonth() + 1)).slice(-2) + '-' + ('0' + CurrentDate.getDate()).slice(-2);
	$('.RegistedDate').attr('max', today);
}
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
    
    var ComCode = $('.ComCode').val();
    var position = PopupPosition(popupWidth, popupHeight);
    
    var MoveType = event.target.name;
    
    switch(field){
    case "ComSearch":
        window.open("${contextPath}/Pop/ComSerach.jsp", "PopUp01", "width=" + popupWidth + ",height=" + popupHeight + ",left=" + position.x + ",top=" + position.y);
    break;
    case "PlantSearch":
        window.open("${contextPath}/Pop/PlantSearch.jsp?Comcode=" + ComCode, "PopUp02", "width=" + popupWidth + ",height=" + popupHeight + ",left=" + position.x + ",top=" + position.y);
    break;
    }
}
$(document).ready(function(){
	InitialTable();
	DateSetting();
	var FilterList = {};
	var CalcMonth = document.getElementById('CalcMonth');
	var RecentYear = new Date().getFullYear();
	var RecentMonth = new Date().getMonth() + 1;
	var EndYear = RecentYear - 30;
	for (let year = RecentYear, month = RecentMonth; year >= EndYear; ) {
	    var Option = document.createElement('option');
	    var monthStr = month < 10 ? '0' + month : ''+ month;
	    Option.value = year + monthStr;
	    Option.textContent = year + monthStr;
	    CalcMonth.appendChild(Option);
	    month--;
	    if (month === 0) {
	        month = 12;
	        year--;
	    }
	}
	$('.SearBtn').click(function(){
		$('.ACInputTable-Body').empty();
		$('.SearOp').each(function(){
		    var name = $(this).attr('name');
		    var value = $(this).val();
		    FilterList[name] = value;
		});
		var pass = true;
		$.each(FilterList,function(key, value){
			if (value == null || value === '') {
    	        pass = false;
    	        return false;
    	    }
		})
		if(!pass){
			alert('모든 필수 항목을 모두 입력해주세요.');
		}else{
			$.ajax({
				url : '${contextPath}/ProGoodsCostAlloSet/GoodsCostLoad.do',
				type : 'POST',
				data :  JSON.stringify(FilterList),
				contentType: 'application/json; charset=utf-8',
				dataType: 'json',
				async: false,
				success : function(data){
					console.log(data.result);
					if(data.result === "success") {
			            if(data.List.length > 0) {
			            	console.log(data.List.length);
			            	for(var i = 0; i < data.List.length; i++){
			            	    var row = '<tr>' +
			            	        '<td>' + (data.List[i].ClosingMon ?? 'N/A') + '</td>' +
			            	        '<td>' + (data.List[i].ComCode ?? 'N/A') + '</td>' +
			            	        '<td>' + (data.List[i].PlantCode ?? 'N/A') + '</td>' +
			            	        '<td>' + (data.List[i].WorkType ?? 'N/A') + '</td>' +
			            	        '<td>' + (data.List[i].CostingLev ?? 'N/A') + '</td>' +
			            	        '<td>' + (data.List[i].WorkOrd ?? 'N/A') + '</td>' +
			            	        '<td>' + (data.List[i].SumOfWMC ?? 'N/A') + '</td>' +
			            	        '<td>' + (data.List[i].SumOfWMfC ?? 'N/A') + '</td>' +
			            	        '<td>' + (data.List[i].SumOfFMC ?? 'N/A') + '</td>' +
			            	        '<td>' + (data.List[i].SumOfFMfC ?? 'N/A') + '</td>' +
			            	        '</tr>';
			            	    $('.ACInputTable-Body').append(row);
			            	}
			            }
			        } else {
			            alert("실패: 검색 항목을 다시 선택해주세요.");
			        }
				},
				error: function(jqXHR, textStatus, errorThrown){
					alert('조회 오류 발생: ' + textStatus + ', ' + errorThrown);
		    	}
	    	});
		}
	})
	$('.OkBtn').click(function(){
		$('.SearOp').each(function(){
		    var name = $(this).attr('name');
		    var value = $(this).val();
		    FilterList[name] = value;
		});
		var pass = true;
		$.each(FilterList,function(key, value){
			if (value == null || value === '') {
    	        pass = false;
    	        return false;
    	    }
		})
		if(!pass){
			alert('모든 필수 항목을 모두 입력해주세요.');
		}else{
			$.ajax({
				url : '${contextPath}/ProGoodsCostAlloSet/GoodsCostCalc.do',
				type : 'POST',
				data :  JSON.stringify(FilterList),
				contentType: 'application/json; charset=utf-8',
				dataType: 'json',
				async: false,
				success : function(data){
					console.log(data.result);
					alert('분배가 정상적으로 진행됐습니다.');
				},
				error: function(jqXHR, textStatus, errorThrown){
					alert('분배 오류 발생: ' + textStatus + ', ' + errorThrown);
		    	}
	    	});
		}
	})
	$('.QueryBtn').click(function(){
		alert('asd1');
	})
})
</script>
<jsp:include page="../Header.jsp"></jsp:include>
<div class="ACostArea_Head">
	<div class="ACostArea-Filter">
		<div class="Title">조회 조건 입력</div>
		<div class="InfoInput">
			<label>Company : </label> 
			<input type="text" class="ComCode SearOp" name="ComCode" onclick="InfoSearch('ComSearch')" readonly>
		</div>
		
		<div class="InfoInput">
			<label>Plant :  </label>
			<input type="text" class="PlantCode SearOp" name="PlantCode" onclick="InfoSearch('PlantSearch')" placeholder="SELECT" readonly>
		</div>
		
		<div class="InfoInput">
			<label>결산월 :  </label>
			<select class="CalcMonth SearOp" id="CalcMonth" name="CalcMonth" value="202504"></select>
		</div>
		
		<div class="InfoInput">
			<label>등록일자 :  </label>
			<input type="date" class="RegistedDate" name="RegistedDate">
		</div>
		
		<div class="InfoInput">
			<label>등록자 :  </label>
			<input type="text" class="RegisterId" onclick="InfoSearch('UserSearch')" placeholder="SELECT" readonly>
		</div>
		<button class="SearBtn">검색</button>	
	</div>
	<div class="ACostArea_Tail">
		<div class="GoodSCoatSum-State">
			<div class="Title">배부 귀속 원가</div>
			<table class="ACInputTable">
				<thead class="ACInputTable-Header">
					<tr>
						<th>결산월</th><th>기업코드</th><th>공장코드</th><th>작지유형</th><th>차수</th><th>작지번호</th>
						<th>재공재료비</th><th>재공가공비</th><th>제품재료비</th><th>제품가공비</th>
					</tr>
				</thead>
				<tbody class="ACInputTable-Body">
				</tbody>
			</table>
		</div>
		<div class="Btn-Area">
			<button class="OkBtn">분배</button>
			<button class="QueryBtn">조회</button>
			<button class="DelBtn">초기화</button>
		</div>
		<div class="CostingAmtTable">
			<div class="Title">배부원가 등록 현황</div>
			<table class="InfoTable">
				<thead class="InfoTable-Header">
					<tr>
						<th>결산월</th><th>교반 가공비</th><th>검가 가공비</th><th>소분 가공비</th><th>공통 재료비</th><th>상품매입금액</th>
					</tr>
				</thead>
				<tbody class="InfoTable-Body">
				</tbody>
			</table>
		</div>
	</div>
</div>
</body>
</html>