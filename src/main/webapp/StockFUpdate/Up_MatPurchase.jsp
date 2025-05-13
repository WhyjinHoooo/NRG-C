<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<link rel="stylesheet" href="${contextPath}/CSS/Common.css?after">
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<script>
document.addEventListener('DOMContentLoaded', function() {
    const thead = document.querySelector('.InfoTable-Header');
    const tbody = document.querySelector('.InfoTable-Body');
    
    tbody.addEventListener('scroll', function() {
        thead.scrollLeft = tbody.scrollLeft;
    });
});
function InitialTable(){
	$('.InfoTable-Body').empty();
	for (let i = 0; i < 1000; i++) {
        const row = $('<tr></tr>');
        for (let j = 0; j < 19; j++) {
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
    case "PlantSearch":
        window.open("${contextPath}/Pop/PlantSerach.jsp", "PopUp01", "width=" + popupWidth + ",height=" + popupHeight + ",left=" + position.x + ",top=" + position.y);
    break;
    case "FileSearch":
        window.open("${contextPath}/Pop/FileSearch.jsp", "PopUp02", "width=" + popupWidth + ",height=" + popupHeight + ",left=" + position.x + ",top=" + position.y);
    break;
    case "MonthSearch":
        window.open("${contextPath}/Pop/MonthSearch.jsp", "PopUp03", "width=" + popupWidth + ",height=" + popupHeight + ",left=" + position.x + ",top=" + position.y);
    break;
    case "UserSearch":
        window.open("${contextPath}/Pop/UserSearch.jsp", "PopUp04", "width=" + popupWidth + ",height=" + popupHeight + ",left=" + position.x + ",top=" + position.y);
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
	var CalcMonth = document.getElementById('CalcMonth');
	var RecentYear = new Date().getFullYear();
	var RecentMonth = new Date().getMonth() + 1;
	var EndYear = RecentYear - 30;
	for (let year = RecentYear, month = RecentMonth; year >= EndYear; ) {
	    var Option = document.createElement('option');
	    // 월이 1자리면 0을 붙여서 2자리로 만듦
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
	$('#UploadBtn').click(function() {
	    var fileInput = $('#textFile')[0];
	    if (!fileInput.files.length) {
	        alert('파일을 선택하세요!');
	        return;
	    }
	    var formData = new FormData();
	    formData.append("textFile", fileInput.files[0]);
	    $.ajax({
	        url: '../upload.do',
	        type: 'POST',
	        data: formData,
	        dataType: 'json',
	        processData: false, // [필수]
	        contentType: false, // [필수]
	        success: function(res) {
	        	console.log(res.result)
/* 	            if(res.result === "success") {
	                $('#result').html(
	                    '<b>파일명:</b> ' + res.fileName + '<br>' +
	                    '<b>행 개수:</b> ' + res.rowCount + '<br>' +
	                    '<b>DB 저장 결과:</b> ' + res.dbResult + '<br>'
	                );
	            } else {
	                $('#result').html('<span style="color:red;">오류: ' + res.message + '</span>');
	            } */
	        },
	        error: function(xhr) {
	            /* $('#result').html('<span style="color:red;">업로드 실패: ' + xhr.statusText + '</span>'); */
	        }
	    });
	});
})
</script>
<jsp:include page="../Header.jsp"></jsp:include>
<div class="StockArea">
	<div class="StockArea-Filter">
		<div class="Title">매입 실적 검색</div>
		<div class="InfoInput">
			<label>Company : </label> 
			<input type="text" class="ComCode" value="" readonly>
		</div>
		
		<div class="InfoInput">
			<label>Plant :  </label>
			<input type="text" class="PlantCode" onclick="InfoSearch('PlantSearch')" placeholder="SELECT" readonly>
		</div>
		
		<div class="InfoInput">
			<label>UPLODA DATA :  </label>
			<input type="text" class="UploadDataCode" onclick="InfoSearch('FileSearch')" placeholder="SELECT" readonly>
		</div>
		
		<div class="InfoInput">
			<label>결산월 :  </label>
			<select class="CalcMonth" id="CalcMonth"></select>
		</div>
		
		<div class="InfoInput">
			<label>등록일자 :  </label>
			<input type="date" class="RegistedDate">
		</div>
		
		<div class="InfoInput">
			<label>등록자 :  </label>
			<input type="text" class="RegisterId" onclick="InfoSearch('UserSearch')" placeholder="SELECT" readonly>
		</div>
		<button class="SearBtn">검색</button>	
	</div>
	<div class="StockArea-Result">
		<div class="StockArea-Data-Area">
			<div class="Title">매입 실적 현황</div>
			<table class="InfoTable">
					<thead class="InfoTable-Header">
					<tr>
						<th>순번</th><th>입출고유형</th><th>결산월</th><th>납품일</th><th>자재코드</th><th>품명</th><th>규격</th>
						<th>자재타입</th><th>입고출량</th><th>발주단가</th><th>입고금액</th><th>창고코드</th><th>창고</th>
						<th>발주번호</th><th>거래처코드</th><th>거래처명</th><th>거래처Lot번호</th><th>공장</th><th>기업</th>
					</tr>
				</thead>
				<tbody class="InfoTable-Body">
				</tbody>
			</table>
		</div>
		<div class="Btn-Area">
			<button class="DeterminBtn">발주전환</button>
			<button class="DelBtn">저장</button>
		</div>
		<div class="StockArea-Uploading">
			<div class="Title">매입 실적 입력</div>
			<div class="File-Uploading-Area">
				<div class="InfoInput">
					<label>검색 파일 :  </label>
					<input type="file" id="textFile" accept=".txt" required>
					<button id="UploadBtn">실행</button>
					<button id="CancelBtn">취소</button>
				</div>
			</div>
		</div>
	</div>
</div>
</body>
</html>