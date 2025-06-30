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
<title>작업지시서 현황</title>
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
	var HeaderLength = $('thead.InfoTable-Header th').length;
	console.log('HeaderLength : ' + HeaderLength);
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
    case "FileSearch":
        window.open("${contextPath}/Pop/fileSearch.jsp?Comcode=" + ComCode, "PopUp03", "width=" + popupWidth + ",height=" + popupHeight + ",left=" + position.x + ",top=" + position.y);
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
	$('#UploadBtn').click(function(e) {
		e.preventDefault();
	    var fileInput = $('#textFile')[0];
	    if (!fileInput.files.length) {
	        alert('파일을 선택하세요!');
	        return;
	    }
	    var formData = new FormData();
	    formData.append("textFile", fileInput.files[0]);
	    $.ajax({
	        url: '${contextPath}/upload.do',
	        type: 'POST',
	        data: formData,
	        dataType: 'json',
	        processData: false, // [필수]
	        contentType: false, // [필수]
	        success: function(res) {
	        	console.log(res.result)
	        },
	        error: function(xhr) {
	        }
	    });
	});
	$('#CancelBtn').click(function(e){
		e.preventDefault();
		$('#textFile').val('');
	})
	$('.DelBtn').click(function(){
		var Textfile = $('.UploadDataCode').val() + $('.CalcMonth').val() + ".txt";
		console.log('Textfile : ' + Textfile);
		$.ajax({
			url : '${contextPath}/AjaxSet/FileDelete.jsp',
			type : 'POST',
			data :  {file : Textfile},
			success : function(data){
				console.log(data.trim());
				if(data.trim() === "Done"){
					alert("해당 파일을 모두 삭제했습니다.");
					InitialTable();
				}else{
					alert("해당 파일은 존재하지 않습니다");
				}
			},
			error: function(jqXHR, textStatus, errorThrown){
				alert('오류 발생: ' + textStatus + ', ' + errorThrown);
	    	}
    	});
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
				url : '${contextPath}/InfoLoading/Joborder.do',
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
			            	        '<td>' + (i + 1).toString().padStart(3, '0') + '</td>' +
			            	        '<td>' + (data.List[i].pono ?? 'N/A') + '</td>' +
			            	        '<td>' + (data.List[i].MOType ?? 'N/A') + '</td>' +
			            	        '<td>' + (data.List[i].lotNum ?? 'N/A') + '</td>' +
			            	        '<td>' + (data.List[i].delidiv ?? 'N/A') + '</td>' +
			            	        '<td>' + (data.List[i].facLind ?? 'N/A') + '</td>' +
			            	        '<td>' + (data.List[i].facility ?? 'N/A') + '</td>' +
			            	        '<td>' + (data.List[i].itemno ?? 'N/A') + '</td>' +
			            	        '<td>' + (data.List[i].item ?? 'N/A') + '</td>' +
			            	        '<td>' + (data.List[i].makedate ?? 'N/A') + '</td>' +
			            	        '<td>' + (data.List[i].prokg ?? 'N/A') + '</td>' +
			            	        '<td>' + (data.List[i].prolt ?? 'N/A') + '</td>' +
			            	        '<td>' + (data.List[i].Runtime ?? '00:00') + '</td>' +
			            	        '<td>' + (data.List[i].InputTime ?? '00:00') + '</td>' +
			            	        '<td>' + (data.List[i].MixTime ?? '00:00') + '</td>' +
			            	        '<td>' + (data.List[i].TestTime ?? '00:00') + '</td>' +
			            	        '<td>' + (data.List[i].plant ?? 'N/A') + '</td>' +
			            	        '<td>' + (data.List[i].company ?? 'N/A') + '</td>' +
			            	        '</tr>';
			            	    $('.InfoTable-Body').append(row);
			            	}
			            }
			        } else {
			            alert("실패: 검색 항목을 다시 선택해주세요.");
			            InitialTable();
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
<div class="StockArea">
	<div class="StockArea-Filter">
		<div class="Title">조회 조건 입력</div>
		<div class="InfoInput">
			<label>Company : </label> 
			<input type="text" class="ComCode FilterOP" name="ComCode" onclick="InfoSearch('ComSearch')" readonly>
		</div>
		
		<div class="InfoInput">
			<label>Plant :  </label>
			<input type="text" class="PlantCode FilterOP" name="PlantCode" onclick="InfoSearch('PlantSearch')" placeholder="SELECT" readonly>
		</div>
		
		<div class="InfoInput">
			<label>UPLODA DATA :  </label>
			<input type="text" class="UploadDataCode FilterOP" name="UploadDataCode" value="PWC" readonly>
		</div>
		
		<div class="InfoInput">
			<label>결산월 :  </label>
			<select class="CalcMonth FilterOP" id="CalcMonth" name="CalcMonth" value="202504"></select>
		</div>
		
		<div class="InfoInput">
			<label>등록일자 :  </label>
			<input type="date" class="RegistedDate" >
		</div>
		
		<div class="InfoInput">
			<label>등록자 :  </label>
			<input type="text" class="RegisterId" onclick="InfoSearch('UserSearch')" placeholder="SELECT" readonly>
		</div>
		<button class="SearBtn">검색</button>	
	</div>
	<div class="StockArea-Result">
		<div class="StockArea-Data-Area">
			<div class="Title">공정 작업실적 발행 현황</div>
			<table class="InfoTable">
					<thead class="InfoTable-Header">
					<tr>
						<th>회사</th><th>공장</th><th>결산월</th><th>작업지시번호</th><th>작지유형</th><th>제조Lot</th><th>제조품번</th>
						<th>제조품명</th><th>원가차수</th><th>작업순서</th><th>공정코드</th><th>공정명</th><th>수불구분</th><th>투입중량</th>
						<th>제품중량</th><th>기말제고중량</th><th>교반시간</th><th>소분시작월</th><th>소분완료월</th>
					</tr>
				</thead>
				<tbody class="InfoTable-Body">
				</tbody>
			</table>
		</div>
		<div class="Btn-Area">
			<button class="OkBtn">확정</button>
			<button class="DelBtn">삭제</button>
		</div>
		<div class="StockArea-Uploading">
			<div class="Title">공정 작업실적 업로딩</div>
			<div class="File-Uploading-Area">
				<div class="InfoInput">
					<label>검색 파일 :  </label>
					<input type="file" id="textFile" accept=".txt" required>
					<div id="ErrorMess">(File명 : "PWCyyyymm.txt")</div>
					<button id="UploadBtn">실행</button>
					<button id="CancelBtn">취소</button>
				</div>
			</div>
		</div>
	</div>
</div>
</body>
</html>