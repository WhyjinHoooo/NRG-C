<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>수불관리</title>
<link rel="stylesheet" href="../CSS/Inven.css?after">
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
/*     case "FileSearch":
        window.open("${contextPath}/Pop/fileSearch.jsp?Comcode=" + ComCode, "PopUp03", "width=" + popupWidth + ",height=" + popupHeight + ",left=" + position.x + ",top=" + position.y);
    break; */
    }
}
const cssMap = {
	1: '../CSS/Inv_ComLev.css?after',
	2: '../CSS/Inv_PlaLev.css?after',
	3: '../CSS/Inv_SlocLev.css?after',
	4: '../CSS/Inv_LotLev.css?after',
	default: '../CSS/Inven.css?after'
};	
function applyCSS(condition) {
	console.log('applyCSS condition : ' + condition);
	const linkTbody = document.createElement('link');
    linkTbody.rel = 'stylesheet';
    linkTbody.href = cssMap[condition] || cssMap['default'];

    const oldLinkTbody = document.querySelector('link[data-target="tbody"]');
    if (oldLinkTbody) oldLinkTbody.remove();

    linkTbody.setAttribute('data-target', 'tbody');
    document.head.appendChild(linkTbody);

    const linkFooter = document.createElement('link');
    linkFooter.rel = 'stylesheet';
    linkFooter.href = cssMap[condition] || cssMap['default'];

    const oldLinkFooter = document.querySelector('link[data-target="footer"]');
    if (oldLinkFooter) oldLinkFooter.remove();

    linkFooter.setAttribute('data-target', 'footer');
    document.head.appendChild(linkFooter);
}
function TestFunction(value) {
    let Turl;
    if (value === 'Company') {
        Turl = 'SubHall/ComLevelTable.jsp';
    } else if (value === 'Plant') {
        Turl = 'SubHall/PlantLevelTable.jsp';
    } else if (value === 'Slocation') {
        Turl = 'SubHall/SlocLevelTable.jsp';
    }else  if(value === 'Lot'){
    	Turl = 'SubHall/LotLevelTable.jsp';
    }
    if(Turl){
    	$('.InfoTable-Header').load(Turl);
    }
}
function InitialTable(count){
	$('.InfoTable-Body').empty();
	for (let i = 0; i < 50; i++) {
		const row = $('<tr></tr>');
		for (let j = 0; j < count; j++) {
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
function updateClock() {
    const now = new Date();
    const year = now.getFullYear();
    const month = (now.getMonth() + 1).toString().padStart(2, '0');
    const day = now.getDate().toString().padStart(2, '0');
    const hours = now.getHours();
    const minutes = now.getMinutes().toString().padStart(2, '0');
    const seconds = now.getSeconds().toString().padStart(2, '0');
    
    const dateString = `${'${year}'}-${'${month}'}-${'${day}'}`;
    const timeString = `${'${hours}'}:${'${minutes}'}:${'${seconds}'}`;
    const fullString = `${'${dateString}'} ${'${timeString}'}`;
    document.getElementById('clock').textContent = fullString;
}
$(document).ready(function() {
	let TimeSetting = setInterval(updateClock, 1000);
	//updateClock();
	DateSetting();
	$('.FromDate, .EndDate').change(function() {
		var BeforeDate = null;
		var value = $(this).val();
		if($(this).hasClass('FromDate')){
			BeforeDate = new Date(value);
			var initDate = BeforeDate.getFullYear() + '-' + ('0' + (BeforeDate.getMonth() + 1)).slice(-2) + '-01';
			console.log('initDate : ' + initDate);
			$(this).val(initDate);
		}else{
			BeforeDate = new Date(value);
			var FinalDate = BeforeDate.getFullYear() + '-' + ('0' + (BeforeDate.getMonth() + 1)).slice(-2) + '-' +new Date(BeforeDate.getFullYear(), (BeforeDate.getMonth() + 1), 0).getDate();
			console.log('FinalDate : ' + FinalDate);
			$(this).val(FinalDate);
		}
		
	});
	TestFunction('Company');
	InitialTable(18);
	$('.ResBtn').click(function(){
		TimeSetting = setInterval(updateClock, 1000);
		$('.SearOp').each(function(){
           $(this).val('');
           $(this).attr('placeholder', 'SELECT');
        });
	})
	$('.Main-Colume > button').click(function(){
		$(this).closest('div').find('input').val('');
		$(this).closest('div').find('input').attr('placeholder', 'SELECT');
	})
	
	var condition = 1;
	applyCSS(condition);
    var value = '1';
    var count = null;
    $('.CateBtn').click(function() {
    	value = $(this).val();
    	switch(value){
    	case '1':
    		$("footer").show();
    		$('.LvP, .LvS, .LvL').prop('hidden',true);
    		count = 18;
    		condition = 1;
    		break;
    	case '2':
    		$("footer").show();
    		$('.LvP').prop('hidden',false);
    		$('.LvS, .LvL').prop('hidden',true);
    		count = 19;
    		condition = 2;
    		break;
    	case '3':
    		$("footer").show();
    		$('.LvP, .LvS').prop('hidden',false);
    		$('.LvL').prop('hidden',true);
    		count = 20;
    		condition = 3;
    		break;
    	case '4':
    		$("footer").hide();
    		$('.LvP, .LvL, .LvS').prop('hidden',false);
    		count = 21;
    		condition = 4;
    		var UserId = $('.UserId').val();
    		break;
    	}
		InitialTable(count);
		switch(value){
    	case '4':
    		applyCSS(condition);
    		break;
    	case '3':
    		applyCSS(condition);
    		break;
    	case '2':
    		applyCSS(condition);
    		break;
    	case '1':
    		applyCSS(condition);
    		break;
    	}
	});
    
    $('.SearBtn').click(function(){
	    $('.InfoTable-Body').empty();
    	var InQ = 0, InA = 0, PI = 0, PA = 0, MO = 0, MA = 0, TI = 0, TA = 0, IQ = 0, IA = 0;
    	
    	var FromDate = $('.FromDate').val();
    	var EndDate = $('.EndDate').val();
    	var MatData = $('.MatCode').val();
    	var ComData = $('.ComCode').val();
    	var IntFromDate = new Date(FromDate).getTime();
    	var IntEndDate = new Date(EndDate).getTime();
    	var PlantData = $('.PlantCode').val();
    	var SLoData = $('.SLocCode').val();
    	var MatType = $('.MatType').val()
    	var LotNum = $('.LotNum').val();
    	if (isNaN(IntEndDate)) {
    	    alert('유효하지 않은 날짜 형식입니다.');
    	    return false;
    	}
    	if (IntFromDate > IntEndDate) {
    	    alert('조회기간을 다시 입력하세요.');
    	    return false;
    	}
    	var List = {
    		'FromDate' : FromDate,
    		'EndDate' : EndDate,
    		'MatData' : MatData,
    		'ComData' : ComData
    	}
    	console.log(List);
    	switch(value){
    	case '1':
        	$.ajax({
    			url : '${contextPath}/SavedDataLoading/C_LoadData.do',
    			type : 'POST',
    			data :  JSON.stringify(List),
    			contentType: 'application/json; charset=utf-8',
    			dataType: 'json',
    			async: false,
    			success: function(data){
    			   if(data.result === "success"){
    				   if(data.List.length > 0) {
							for(var i = 0; i < data.List.length; i++){
    						   var row = '<tr>' +
    	    			        '<td>' + (i + 1).toString().padStart(2,'0') + '</td>' + 
    	    			        '<td>' + (data.List[i].comcode || 'N/A') + '</td>' + 
    	    			        '<td>' + (data.List[i].matcode || 'N/A') + '</td>' + 
    	    			        '<td>' + (data.List[i].matdesc || 'N/A') + '</td>' +
    	    			        '<td>' + (data.List[i].mattype || 'N/A') + '</td>' + 
    	    			        '<td>' + (data.List[i].spec || 'N/A') + '</td>' + 
    	    			        '<td>' + (data.List[i].beginStocqty_sum ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].Initial_Amt ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].GrTransacQty_sum ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].Purchase_Amt ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].GrTransferQty_sum ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].Material_Amt ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].GiTransferQty_sum ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].Transfer_Amt ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].GiTransferQty_sum ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].Inventory_Amt ?? 0) + '</td>' +
    	    			        '<td>' + (data.List[i].EndStocQty_sum ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].Inventory_Amt ?? 0) + '</td>' + 
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
    		break;
    	case '2':
    	case '3':
    		if (value === '2' && (!PlantData || PlantData.trim() === '')) {
    		    return false;
    		}else if(value === '3' && (!PlantData || PlantData.trim() === '') && (!SLoData || SLoData.trim() === '')){
    			return false;
    		}
    		List['value'] = value; 
    		List['PlantData'] = PlantData;
    		List['SLoData'] = SLoData;
    		$.ajax({
    			url : '${contextPath}/SavedDataLoading/PS_LoadData.do',
    			type : 'POST',
    			data :  JSON.stringify(List),
    			contentType: 'application/json; charset=utf-8',
    			dataType: 'json',
    			async: false,
    			success: function(data){
    				if(data.result === "success"){
    					if(data.List.length > 0) {
    						for(var i = 0; i < data.List.length; i++){
    							var row = '<tr>' +
    							'<td>' + (i + 1).toString().padStart(2,'0') + '</td>' + 
    	    			        '<td>' + (data.List[i].comcode || 'N/A') + '</td>';
    							if(value === '2'){
    	    			        	row += '<td>' + (data.List[i].plant || 'N/A') + '</td>'; 
    	    			        }else{
    	    			        	row += '<td>' + (data.List[i].plant || 'N/A') + '</td>' +
    	    			        		   '<td>' + (data.List[i].warehouse || 'N/A') + '</td>';
    	    			        }
    							row += 
    							'<td>' + (data.List[i].matcode || 'N/A') + '</td>' + 
 		    			       	'<td>' + (data.List[i].matdesc || 'N/A') + '</td>' +
 		    			       	'<td>' + (data.List[i].mattype || 'N/A') + '</td>' + 
 		    			       	'<td>' + (data.List[i].spec || 'N/A') + '</td>' +
    	    			        '<td>' + (data.List[i].beginStocqty_sum ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].Initial_Amt ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].GrTransacQty_sum ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].Purchase_Amt ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].GrTransferQty_sum ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].Material_Amt ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].GiTransferQty_sum ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].Transfer_Amt ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].GiTransferQty_sum ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].Inventory_Amt ?? 0) + '</td>' +
    	    			        '<td>' + (data.List[i].EndStocQty_sum ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].Inventory_Amt ?? 0) + '</td>' + 
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
    		break;
    	case '4':
    		List['PlantData'] = PlantData;
    		List['SLoData'] = SLoData;
    		List['MatType'] = MatType;
    		List['LotNum'] = LotNum;
    		$.ajax({
    			url : '${contextPath}/SavedDataLoading/Lot_LoadData.do',
    			type : 'POST',
    			data :  JSON.stringify(List),
    			contentType: 'application/json; charset=utf-8',
    			dataType: 'json',
    			async: false,
    			success: function(data){
    				if(data.result === "success"){
    					if(data.List.length > 0) {
    						for(var i = 0; i < data.List.length; i++){
    							var row = '<tr>' +
    							'<td>' + (i + 1).toString().padStart(2,'0') + '</td>' + 
    	    			        '<td>' + (data.List[i].comcode || 'N/A') + '</td>' +
    	    			        '<td>' + (data.List[i].plant || 'N/A') + '</td>' +
    	    			        '<td>' + (data.List[i].warehouse || 'N/A') + '</td>' +
    							'<td>' + (data.List[i].lotnum || 'N/A') + '</td>' + 
 		    			       	'<td>' + (data.List[i].matcode || 'N/A') + '</td>' +
 		    			       	'<td>' + (data.List[i].matdesc || 'N/A') + '</td>' + 
 		    			       	'<td>' + (data.List[i].mattype || 'N/A') + '</td>' +
 		    			       	'<td>' + (data.List[i].spec || 'N/A') + '</td>' +
    	    			        '<td>' + (data.List[i].beginStocqty_sum ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].Initial_Amt ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].GrTransacQty_sum ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].Purchase_Amt ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].GrTransferQty_sum ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].Material_Amt ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].GiTransferQty_sum ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].Transfer_Amt ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].GiTransferQty_sum ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].Inventory_Amt ?? 0) + '</td>' +
    	    			        '<td>' + (data.List[i].EndStocQty_sum ?? 0) + '</td>' + 
    	    			        '<td>' + (data.List[i].Inventory_Amt ?? 0) + '</td>' + 
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
    		break;
    	}
    	applyCSS(condition);
    	clearInterval(TimeSetting);
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
		<div class="Category">
			<input class="UserId" value="<%=UserIdNumber%>" hidden>
			<button class="CateBtn" onclick="TestFunction('Company')" value="1">Com.Lv</button> <!-- 1 -->
			<button class="CateBtn" onclick="TestFunction('Plant')" value="2">Pla.Lv</button> <!-- 2 -->
			<button class="CateBtn" onclick="TestFunction('Slocation')" value="3">SLo.lv</button> <!-- 3 -->
			<button class="CateBtn" onclick="TestFunction('Lot')" value="4">Lot.Lv</button> <!-- 4 -->
		</div>
		<div class="MainHallArray">
			<div class="Main-Colume">
				<label>❗Company : </label>
				<input type="text" class="ComCode FilterOP" name="ComCode" onclick="InfoSearch('ComSearch')" readonly>
			</div>
			<div class="Main-Colume">
				<label>❗조회기간(From) : </label>
				<input type="date" class="FromDate" name="FromDate">
			</div>
			<div class="Main-Colume">
				<label>❗조회기간(End) : </label>
				<input type="date" class="EndDate" name="EndDate">
			</div>
			<div class="Main-Colume LvP" hidden>
				<label>❗Plant : </label>
				<input type="text" class="PlantCode FilterOP" name="PlantCode" onclick="InfoSearch('PlantSearch')" placeholder="SELECT" readonly>
				<button>Del</button>
			</div>
			<div class="Main-Colume LvS" hidden>
				<label>❗창고 : </label>
				<input type="text" class="SLocCode SearOp" name="SLocCode" onclick="InfoSearch('SLoSearch')" placeholder="SELECT" readonly>
				<button>Del</button>
			</div>
 			<div class="Main-Colume LvL" hidden>
				<label>❗재고유형 : </label>
				<input type="text" class="MatType SearOp" name="MatType" onclick="InfoSearch('TypeSearch')" readonly placeholder="SELECT">
				<button>Del</button>
			</div>
			<div class="Main-Colume LvL" hidden>
				<label>❗조달로트번호 : </label>
				<input type="text" class="LotNum SearOp" name="MovCode-In" onclick="InfoSearch('MovSearch')" readonly placeholder="SELECT">
				<button>Del</button>
			</div>
			<div class="Main-Colume">
				<label>Mateiral : </label>
				<input type="text" class="MatCode SearOp" name="MatCode" onclick="InfoSearch('MatSearch')" placeholder="SELECT" readonly>
				<button>Del</button>
			</div>
		</div>
		
		<div class="BtnArea">
			<button class="SearBtn">Search</button>
			<button class="ResBtn">Reset</button>
			<button class="SummBtn">Sum</button>
			<div class="TimeArea" id="clock">Loading...</div>
		</div>
		
	</div>
	
	<div class="SubHall">
		<div class="Title">재고 수불 현황</div>
		<table class="InfoTable">
			<thead class="InfoTable-Header">
 			</thead>
			<tbody class="InfoTable-Body">
			</tbody>
		</table>
	</div>
</div>
</body>
</html>