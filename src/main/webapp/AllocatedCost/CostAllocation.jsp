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
<title>배부원가 등록/조회</title>
</head>
<body>
<script>
function onlyNumberWithComma(obj) {
	let number = obj.value.replace(/[^0-9\-]/g, "");
    number = number.replace(/(?!^)-/g, "");

    if(number === "-") {
        obj.value = "-";
        return;
    }

    if(number) {
        obj.value = Number(number).toLocaleString();
    } else {
        obj.value = "";
    }
}
function InitialTable(){
	$('.InfoTable-Body').empty();
	var HeaderLength = $('thead.InfoTable-Header th').length;
	for (let i = 0; i < 1000; i++) {
        const row = $('<tr></tr>');
        for (let j = 0; j < HeaderLength; j++) {
            row.append('<td></td>');
        }
        $('.InfoTable-Body').append(row);
    }
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
function DateSetting(){
	var CurrentDate = new Date();
	var today = CurrentDate.getFullYear() + '-' + ('0' + (CurrentDate.getMonth() + 1)).slice(-2) + '-' + ('0' + CurrentDate.getDate()).slice(-2);
	$('.RegistedDate').attr('max', today);
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
	
	$('.AIDMonth').val($('.CalcMonth').val());
	
	$('.CalcMonth').change(function(){
		var value = $(this).val();
		$('.AIDMonth').val(value);
	})
	
	$('.DelBtn').click(function(){
		$('.AID').not('.AIDMonth').val('');
	})
	
	$('.SearBtn').click(function(){
		$('.InfoTable-Body').empty();
		$('.FilterOP').each(function(){
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
				url : '${contextPath}/AcCalc/CostAlloLoad.do',
				type : 'POST',
				data :  JSON.stringify(FilterList),
				contentType: 'application/json; charset=utf-8',
				dataType: 'json',
				async: false,
				success : function(data){
					console.log(data.result);
					if(data.result === "success") {
			            if(data.List.length > 0) {
			            	for(var i = 0; i < data.List.length; i++){
			            	    var row = '<tr>' +
			            	        '<td>' + (data.List[i].ClosingDate ?? 'N/A') + '</td>' +
			            	        '<td>' + (data.List[i].OP10.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",") ?? 'N/A') + '</td>' +
			            	        '<td>' + (data.List[i].OP20.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",") ?? 'N/A') + '</td>' +
			            	        '<td>' + (data.List[i].OP30.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",") ?? 'N/A') + '</td>' +
			            	        '<td>' + (data.List[i].OP40.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",") ?? 'N/A') + '</td>' +
			            	        '<td>' + (data.List[i].OP50.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",") ?? 'N/A') + '</td>' +
			            	        '</tr>';
			            	    $('.InfoTable-Body').append(row);
			            	}
			            }
			        } else {
			            alert("실패: 검색 항목을 다시 선택해주세요.");
			        }
				},
				error: function(jqXHR, textStatus, errorThrown){
					alert('오류 발생: ' + textStatus + ', ' + errorThrown);
		    	}
	    	});
		}
	});
	$('.OkBtn').click(function(){
		$('.AID').each(function(){
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
			alert('필터 값이 비어 있고, 테이블도 비어 있습니다!');
			return false;
		}else{
			$.ajax({
				url : '${contextPath}/AcCalc/CalcCostAllocation.do',
				type : 'POST',
				data :  JSON.stringify(FilterList),
				contentType: 'application/json; charset=utf-8',
				dataType: 'json',
				async: false,
				success : function(data){
					console.log(data.result);
					if(data.result.trim() === 'success'){
						location.reload();
					}else{
						alert('bad');
					}
				},
				error: function(jqXHR, textStatus, errorThrown){
					alert('오류 발생: ' + textStatus + ', ' + errorThrown);
		    	}
	    	});
		}
	});
})
</script>
<jsp:include page="../Header.jsp"></jsp:include>
<div class="ACostArea_Head">
	<div class="ACostArea-Filter">
		<div class="Title">조회 조건 입력</div>
		<div class="InfoInput">
			<label>Company : </label> 
			<input type="text" class="ComCode FilterOP AID" name="ComCode" onclick="InfoSearch('ComSearch')" readonly>
		</div>
		
		<div class="InfoInput">
			<label>Plant :  </label>
			<input type="text" class="PlantCode FilterOP AID" name="PlantCode" onclick="InfoSearch('PlantSearch')" placeholder="SELECT" readonly>
		</div>
		
		<div class="InfoInput">
			<label>결산월 :  </label>
			<select class="CalcMonth FilterOP" id="CalcMonth" name="CalcMonth" value="202504"></select>
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
		<div class="ACostArea-Uploading">
			<div class="Title">배부 귀속 원가</div>
			<table class="ACInputTable">
				<thead>
					<tr>
						<th>결산월</th><th>교반 가공비</th><th>검가 가공비</th><th>소분 가공비</th><th>공통 재료비</th><th>상품매입금액</th>
					</tr>
				</thead>
				<tbody>
					<tr>
						<td><input class="AID AIDMonth" type="text" name="AIDMonth" readonly></td>
						<td><input class="AID OP10" type="text" name="OP10" oninput="onlyNumberWithComma(this)"></td>
						<td><input class="AID OP20" type="text" name="OP20" oninput="onlyNumberWithComma(this)"></td>
						<td><input class="AID OP30" type="text" name="OP30" oninput="onlyNumberWithComma(this)"></td>
						<td><input class="AID OP40" type="text" name="OP40" oninput="onlyNumberWithComma(this)"></td>
						<td><input class="AID OP50" type="text" name="OP50" oninput="onlyNumberWithComma(this)"></td>
					</tr>
				</tbody>
			</table>
		</div>
		<div class="Btn-Area">
			<button class="OkBtn">등록</button>
			<button class="DelBtn">초기화</button>
		</div>
		<div class="ACostArea-Data-Area">
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