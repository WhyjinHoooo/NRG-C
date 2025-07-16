<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>원자재 결산 수불표</title>
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
function exportExcel() {
	  const originalTable = document.querySelector('.InfoTable');

	  // 1. thead 복제 및 헤더 분리
	  const thead = originalTable.querySelector('thead').cloneNode(true);
	  const tr = thead.querySelector('tr');
	  // 새 헤더 배열
	  const newThs = [];
	  // 기존 th들을 순회하며 헤더 분리
	  tr.querySelectorAll('th').forEach(th => {
	    const divs = th.querySelectorAll('div');
	    if (divs.length === 2) {
	      // 복합 헤더인 경우: 예) 기초 -> 기초수량, 기초금액
	      const mainTitle = divs[0].innerText.trim();
	      const spans = divs[1].querySelectorAll('span');
	      spans.forEach(span => {
	        const newTh = document.createElement('th');
	        newTh.innerText = mainTitle + span.innerText.trim();
	        newThs.push(newTh);
	      });
	    } else {
	      // 단일 헤더인 경우
	      const newTh = document.createElement('th');
	      newTh.innerText = th.innerText.trim();
	      newThs.push(newTh);
	    }
	  });
	  // 기존 tr 내용 삭제 후 새 th들 추가
	  tr.innerHTML = '';
	  newThs.forEach(th => tr.appendChild(th));
	  // 2. tbody 복제
	  const tbody = originalTable.querySelector('tbody').cloneNode(true);
	  // 3. 임시 테이블 생성 후 thead, tbody 붙이기
	  const tempTable = document.createElement('table');
	  tempTable.appendChild(thead);
	  tempTable.appendChild(tbody);
	  // 4. SheetJS를 이용해 엑셀 파일 생성 및 다운로드
	  const wb = XLSX.utils.table_to_book(tempTable, { sheet: "Sheet1" });
	  XLSX.writeFile(wb, 'exported_table.xlsx');
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
function InitialTable(){
	$('.InfoTable-Body').empty();
	for (let i = 0; i < 50; i++) {
		const row = $('<tr></tr>');
		for (let j = 0; j < 19; j++) {
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
	var ComData = null;
	var MonthData = null;
	var CalcMonth = document.getElementById('IQDate');
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
	$('.CLBtn').click(function(){
		ComData = $('.ComCode').val();
		MonthData = $('.IQDate').val();
		$.ajax({
			url : '${contextPath}/CostCalc/RawmPriceCalc.do',
			type : 'POST',
			data : {ComCode : ComData, IQData : MonthData},
			dataType: 'json',
			async: false,
			success: function(data){
				console.log(data.result);
				if(data.result === "success"){
					alert('결산이 완료되었습니다.');
					if (!MonthData || MonthData.trim() === '') {
					    // 필요하다면 사용자에게 안내 메시지도 추가 가능
					    alert('월 데이터를 입력하세요!');
					    return false;
					}else{
						$('.InfoTable-Body').empty();
						$.ajax({
							url : '${contextPath}/CostCalc/RawmDataLoading.do',
							type : 'POST',
							data : JSON.stringify({ComCode : ComData, IQData : MonthData}),
							contentType: 'application/json; charset=utf-8',
							dataType: 'json',
							async: false,
							success: function(data){
								console.log('data.result : ' + data.result);
								if(data.result === "success"){
									if(data.List.length > 0) {
										for(var i = 0; i < data.List.length; i++){
											var row = '<tr>' +
											'<td>' +  ComData + '</td>' + 
					    			        '<td>' + (data.List[i].matcode || 'N/A') + '</td>' +
					    			        '<td>' + (data.List[i].matdesc || 'N/A') + '</td>' +
					    			        '<td>' + (data.List[i].mattype || 'N/A') + '</td>' +
											'<td>' + (data.List[i].spec || 'N/A') + '</td>' + 
						    			    '<td>' + (data.List[i].beginStocqty || 'N/A') + '</td>' +
						    			    '<td>' + (data.List[i].BsAmt || 'N/A') + '</td>' + 
						    			    '<td>' + (data.List[i].GrTransacQty || 'N/A') + '</td>' +
						    			    '<td>' + (data.List[i].GrPurAmt || 'N/A') + '</td>' +
					    			        '<td>' + (data.List[i].GrSubAmt ?? 'N/A') + '</td>' + 
					    			        '<td>' + (data.List[i].GrSumAmt ?? 'N/A') + '</td>' + 
					    			        '<td>' + (data.List[i].GrTransferQty ?? 'N/A') + '</td>' + 
					    			        '<td>' + (data.List[i].GiTransferQty ?? 'N/A') + '</td>' + 
					    			        '<td>' + (data.List[i].GiTransacQty ?? 'N/A') + '</td>' + 
					    			        '<td>' + (data.List[i].GiAmt ?? 'N/A') + '</td>' + 
					    			        '<td>' + (data.List[i].EndStocQty ?? 'N/A') + '</td>' + 
					    			        '<td>' + (data.List[i].EsAmt ?? 'N/A') + '</td>' + 
					    			        '<td>' + (data.List[i].UnitPrice ?? 'N/A') + '</td>' +
					    			        '<td>' + (data.List[i].ErrorOX ?? 'N/A') + '</td>' +
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
				}else{
					alert(data.message);
				}
			},
			error: function(jqXHR, textStatus, errorThrown){
				alert('오류 발생: ' + textStatus + ', ' + errorThrown);
	    	}
    	});
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
<button class="ExcelSaveBtn" onclick="exportExcel()">저장</button>
<div class="Hall">
	<div class="MainHall">
		<div class="Title">검색 항목</div>
		<div class="MainHallArray">
			<div class="Main-Colume">
				<label>회사 : </label>
				<input type="text" class="ComCode" name="ComCode" onclick="InfoSearch('ComSearch')" readonly>
			</div>
			<div class="Main-Colume">
				<label>재고유형 : </label>
				<input type="text" class="MatType" name="MatType" value="RAWM" readonly>
			</div>
			<div class="Main-Colume">
				<label>❗결산월 : </label>
				<select type="date" class="IQDate" name="IQDate" id="IQDate"></select>
			</div>
		</div>
		
		<div class="BtnArea">
			<button class="CLBtn">결산&조회</button>
		</div>
	</div>
	
	<div class="SubHall">
		<div class="Title">원자재 결산 수불표</div>
		<table class="InfoTable">
			<thead class="InfoTable-Header">
				<tr>
					<th>회사</th><th>품번</th><th>품명</th><th>재고유형</th><th>규격</th>
					<th>
						<div>기초</div>
				        <div>
				          <span>수량</span>
				          <span>금액</span>
				        </div>
					</th>
					<th>
						<div>입고</div>
				        <div>
				          <span>수량</span>
				          <span>금액</span>
				          <span>부대비</span>
				          <span>금액계</span>
				        </div>
					</th>
					<th>이체입고</th><th>이체출고</th>
					<th>
						<div>출고</div>
				        <div>
				          <span>수량</span>
				          <span>금액</span>
				        </div>
					</th>
					<th>
						<div>기말재고</div>
				        <div>
				          <span>수량</span>
				          <span>금액</span>
				        </div>
					</th>
					<th>재고단가</th>
					<th>오류유무</th>
				</tr>
 			</thead>
			<tbody class="InfoTable-Body">
			</tbody>
		</table>
	</div>
</div>
</body>
</html>