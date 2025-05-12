<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<link rel="stylesheet" href="${contextPath}/CSS/Common.css?after">
<script src="http://code.jquery.com/jquery-latest.js"></script>
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
$(document).ready(function(){
	InitialTable()
})
</script>
<%-- <jsp:include page="../Header.jsp"></jsp:include> --%>
<div class="StockArea">
	<div class="StockArea-Filter">
		<div class="Title">매입 실적 검색</div>
		<div class="InfoInput">
			<label>Company : </label> 
			<input type="text">
		</div>
		
		<div class="InfoInput">
			<label>Plant :  </label>
			<input type="text">
		</div>
		
		<div class="InfoInput">
			<label>Vendor :  </label>
			<input type="text">
		</div>
		
		<div class="InfoInput">
			<label>구매그룹 :  </label>
			<input type="text">
		</div>
		
		<div class="InfoInput">
			<label>동일품목 합산 :  </label>
			<input type="text">
		</div>
		
		<div class="InfoInput">
			<label>Material :  </label>
			<input type="text">
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