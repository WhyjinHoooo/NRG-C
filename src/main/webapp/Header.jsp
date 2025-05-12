<%@ page language="java" contentType="text/html; charset=UTF-8" isELIgnored="false"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>  
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="contextPath" value="${pageContext.request.contextPath}"/>
<link rel="stylesheet" href="${contextPath}/CSS/Nav.css?after">
<!-- <link href="https://maxcdn.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css" rel="stylesheet"> -->


<!DOCTYPE html>
<head>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.9.2/dist/umd/popper.min.js" integrity="sha384-IQsoLXl5PILFhosVNubq5LC7Qb9DXgDA9i+tQ8Zj3iwWAwPtgFTxbJ8NT4GN1R8p" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.0.2/dist/js/bootstrap.min.js" integrity="sha384-cVKIPhGWiC2Al4u+LWgxfKTRIcfu0JTxR+EQDz/bgldoEyl4H0zUF0QKbrJ0EcQF" crossorigin="anonymous"></script>
<script src="https://code.jquery.com/jquery-3.6.0.min.js" integrity="sha256-/xUj+3OJU5yExlq6GSYGSHk7tPXikynS7ogEvDej/m4=" crossorigin="anonymous"></script>
<script>
$(document).ready(function(){
  // 중첩된 드롭다운 메뉴를 위한 클릭 이벤트
  $('.dropdown-menu a.dropdown-toggle').on('click', function(e) {
    if (!$(this).next().hasClass('show')) {
      $(this).parents('.dropdown-menu').first().find('.show').removeClass("show");
    }
    var $subMenu = $(this).next(".dropdown-menu");
    $subMenu.toggleClass('show');

    $(this).parents('li.nav-item.dropdown.show').on('hidden.bs.dropdown', function(e) {
      $('.dropdown-submenu .show').removeClass("show");
    });

    return false;
  });
});
</script>
</head>
<body>
<%
	String User_Id = (String)session.getAttribute("id");
%>
<nav class="navbar navbar-expand-lg navbar-light" style="background-color: #002060;">
  <div class="container-fluid">
    <%-- <a class="navbar-brand Category" href="#" style="color: #ffffff;"><img src="${contextPath}/img/B_LoGo.png" alt=""></a> --%>
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNavDropdown" aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarNavDropdown">
      <ul class="navbar-nav">
        <!-- 기타 nav-item들 -->
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle Category" href="#" id="navbarDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false">MasterData</a>
          <ul class="dropdown-menu Sang" aria-labelledby="navbarDropdownMenuLink" >
            <li class="dropdown-submenu">
            	<a class="dropdown-item dropdown-toggle" href="#">Global</a>
            	<ul class="dropdown-menu">
	                <li><a class="dropdown-item" href="#">국가</a></li>
	                <li><a class="dropdown-item" href="#">통화</a></li>
	                <li><a class="dropdown-item" href="#">환율유형</a></li>
	                <li><a class="dropdown-item" href="#">단위</a></li>
	                <li><a class="dropdown-item" href="#">언어</a></li>
	                <li><a class="dropdown-item" href="#">우편번호</a></li>
	                <li><a class="dropdown-item" href="#">지역</a></li>
              </ul>
            </li>
            <li class="dropdown-submenu">
            	<a class="dropdown-item dropdown-toggle" href="#">Company</a>
            	<ul class="dropdown-menu">
                <li><a class="dropdown-item" href="${contextPath}/Company/Company_Regist.jsp">Company Code</a></li>
                <li><a class="dropdown-item" href="${contextPath}/Tax/Tax-Regist.jsp">Tax Area</a></li>
                <li><a class="dropdown-item" href="${contextPath}/Business/business-AGroup-Regist.jsp">BizArea Group</a></li>
                <li><a class="dropdown-item" href="${contextPath}/BusinessArea/BusinessArea_Regist.jsp">Biz Area</a></li>
                <li><a class="dropdown-item" href="${contextPath}/Coct/CCG-Regist.jsp">CostCenter Group</a></li>
                <li><a class="dropdown-item" href="${contextPath}/CostCenter/CC_regist.jsp">Cost Center</a></li>
                <li><a class="dropdown-item" href="${contextPath}/Plant/plant_regist.jsp">Plant</a></li>
                <li><a class="dropdown-item" href="#">Process</a></li>
                <li><a class="dropdown-item" href="#">업테코드</a></li>
                <li><a class="dropdown-item" href="#">업테코드</a></li>
              </ul>
            </li>
            <li class="dropdown-submenu">
            	<a class="dropdown-item dropdown-toggle" href="#">PS/SD/MM</a>
            	<ul class="dropdown-menu">
                <li><a class="dropdown-item" href="#">문류문서구분</a></li>
                <li><a class="dropdown-item" href="#">SalesGroup</a></li>
                <li><a class="dropdown-item" href="#">PurchasingGroup</a></li>
                <li><a class="dropdown-item" href="#">거래유형</a></li>
                <li><a class="dropdown-item" href="#">창고</a></li>
                <li><a class="dropdown-item" href="#">적재위치</a></li>
                <li><a class="dropdown-item" href="#">거래처구분</a></li>
                <li><a class="dropdown-item" href="#">거래처 구분코드</a></li>
                <li><a class="dropdown-item" href="#">거래처</a></li>
                <li><a class="dropdown-item" href="#">거래조건</a></li>
                <li><a class="dropdown-item" href="#">대금지급조건</a></li>
                <li><a class="dropdown-item" href="#">Customer 여신한도</a></li>
                <li><a class="dropdown-item" href="#">물대 지금보류원인</a></li>
                <li><a class="dropdown-item" href="#">재고유형</a></li>
                <li><a class="dropdown-item" href="#">Material 입출고유형</a></li>
                <li><a class="dropdown-item" href="#">Material Group</a></li>
                <li><a class="dropdown-item" href="${contextPath}/Material/Material_Regist.jsp">Material</a></li>
                <li><a class="dropdown-item" href="#">제품코드</a></li>
                <li><a class="dropdown-item" href="#">Material/Customer 납품 L/T</a></li>
                <li><a class="dropdown-item" href="#">HS Code</a></li>
                <li class="dropdown-submenu">
	              <a class="dropdown-item dropdown-toggle" href="#">MSDS 관리</a>
	              <ul class="dropdown-menu">
	                <li><a class="dropdown-item" href="#">Material별 구성성분 등록</a></li>
	                <li><a class="dropdown-item" href="#">P_Code 등록</a></li>
	                <li><a class="dropdown-item" href="#">H_Code 등록</a></li>
	                <li><a class="dropdown-item" href="#">유해화학물 그림문자 등록</a></li>
	                <li><a class="dropdown-item" href="#">유해화학물 판정 기준 물질 등록</a></li>
	              </ul>
            	</li>
              </ul>
            </li>
            <li class="dropdown-submenu">
            	<a class="dropdown-item dropdown-toggle" href="#">PP</a>
            	<ul class="dropdown-menu">
                <li><a class="dropdown-item" href="#">PP Order(Lot)분류코드</a></li>
                <li><a class="dropdown-item" href="#">Facility Type</a></li>
                <li><a class="dropdown-item" href="#">Facility Hierarchy</a></li>
                <li><a class="dropdown-item" href="#">생산라인(Line)</a></li>
                <li><a class="dropdown-item" href="#">BOM</a></li>
                <li><a class="dropdown-item" href="#">Process</a></li>
                <li><a class="dropdown-item" href="#">Routing</a></li>
                <li><a class="dropdown-item" href="#">Material/Process 제조 L/T</a></li>
                <li><a class="dropdown-item" href="#">Tray 생성</a></li>
                <li><a class="dropdown-item" href="#">Tray 생산 Lot Mapping</a></li>
                <li><a class="dropdown-item" href="#">Lot 바코드 Mapping</a></li>
                <li><a class="dropdown-item" href="#">PackingUnit Mapping</a></li>
              </ul>
            </li>
            <li class="dropdown-submenu">
            	<a class="dropdown-item dropdown-toggle" href="#">EM</a>
            	<ul class="dropdown-menu">
                <li><a class="dropdown-item" href="#">설비그룹</a></li>
                <li><a class="dropdown-item" href="#">설비BOMItem</a></li>
                <li><a class="dropdown-item" href="#">설비코드</a></li>
                <li><a class="dropdown-item" href="#">설비유실코드</a></li>
                <li><a class="dropdown-item" href="#">PM분류코드</a></li>
                <li><a class="dropdown-item" href="#">PM Item</a></li>
                <li><a class="dropdown-item" href="#">PM Inspection-item</a></li>
                <li><a class="dropdown-item" href="#">PM Inspec.Item-Measurement</a></li>
                <li><a class="dropdown-item" href="#">설비이상 원인코드</a></li>
                <li><a class="dropdown-item" href="#">설비이상 조치코드</a></li>
                <li><a class="dropdown-item" href="#">설비이상코드</a></li>
                <li><a class="dropdown-item" href="#">설비 운전 Parameter 등록</a></li>
                <li><a class="dropdown-item" href="#">설비 운전 Parameter Spec 등록</a></li>
                <li><a class="dropdown-item" href="#">설비/Material 운전 Param.Spec</a></li>
                <li><a class="dropdown-item" href="#">설비/자재 Mapping</a></li>
                <li><a class="dropdown-item" href="#">설비 운전 Parameter 등록</a></li>
                <li><a class="dropdown-item" href="#">설비 운전 Parameter 등록</a></li>
                <li><a class="dropdown-item" href="#">설비 운전 Parameter 등록</a></li>
              </ul>
            </li>
            <li class="dropdown-submenu">
            	<a class="dropdown-item dropdown-toggle" href="#">FI/CO</a>
            	<ul class="dropdown-menu">
                <li><a class="dropdown-item" href="#">계정그룹</a></li>
                <li><a class="dropdown-item" href="#">계정유형</a></li>
                <li><a class="dropdown-item" href="#">계정과목</a></li>
                <li><a class="dropdown-item" href="#">비용구분</a></li>
                <li><a class="dropdown-item" href="#">부서유형</a></li>
                <li><a class="dropdown-item" href="#">계정관리항목</a></li>
                <li><a class="dropdown-item" href="#">미결관리기준</a></li>
                <li><a class="dropdown-item" href="#">유형자산분류</a></li>
                <li><a class="dropdown-item" href="#">전표구분</a></li>
                <li><a class="dropdown-item" href="#">대차구분</a></li>
                <li><a class="dropdown-item" href="#">유형자산분류</a></li>
                <li><a class="dropdown-item" href="#">유형자산감가상각방법</a></li>
                <li><a class="dropdown-item" href="#">Tax구분</a></li>
                <li><a class="dropdown-item" href="#">Bank코드</a></li>
                <li><a class="dropdown-item" href="#">금융거래계좌</a></li>
                <li><a class="dropdown-item" href="#">신용카드</a></li>
                <li><a class="dropdown-item" href="#">환율</a></li>
                <li><a class="dropdown-item" href="#">Version</a></li>
                <li><a class="dropdown-item" href="#">금융거래계좌</a></li>
                <li><a class="dropdown-item" href="#">V.A.T율</a></li>
                <li><a class="dropdown-item" href="#">거래처별 계정과목 Mapping</a></li>
              </ul>
            </li>
            <li class="dropdown-submenu">
            	<a class="dropdown-item dropdown-toggle" href="#">HR</a>
            	<ul class="dropdown-menu">
                <li><a class="dropdown-item" href="#">직책</a></li>
                <li><a class="dropdown-item" href="#">직급</a></li>
                <li><a class="dropdown-item" href="#">직무</a></li>
                <li><a class="dropdown-item" href="#">EmployID</a></li>
                <li><a class="dropdown-item" href="#">교육과목분류코드</a></li>
                <li><a class="dropdown-item" href="#">급여항목분류</a></li>
                <li><a class="dropdown-item" href="#">성과평가분류코드</a></li>
                <li><a class="dropdown-item" href="#">근태분류코드</a></li>
                <li><a class="dropdown-item" href="#">근무유형분류코드</a></li>
                <li><a class="dropdown-item" href="#">교육과목</a></li>
                <li><a class="dropdown-item" href="#">상벌분류코드</a></li>
                <li><a class="dropdown-item" href="#">상벌코드</a></li>
                <li><a class="dropdown-item" href="#">승급분류코드</a></li>
                <li><a class="dropdown-item" href="#">파견코드</a></li>
                <li><a class="dropdown-item" href="#">급여공제구분</a></li>
                <li><a class="dropdown-item" href="#">소득공제구분코드</a></li>
                <li><a class="dropdown-item" href="#">세액공제구분코드</a></li>
              </ul>
            </li>            
          </ul>
        </li>
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle Category" href="#" id="navbarDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false">Purchasing</a>
          <ul class="dropdown-menu Sang" aria-labelledby="navbarDropdownMenuLink">
            <li class="dropdown-submenu">
            	<a class="dropdown-item dropdown-toggle" href="#">구매계획</a>
            	<ul class="dropdown-menu">
	                <li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">생상계획 기준 구매요청서 생성</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">생상계획 Data I/F</a></li>
		                <li><a class="dropdown-item" href="#">일자별 공정투입 자재별 소요량</a></li>
		                <li><a class="dropdown-item" href="#">일별 자재 계획방주량 관리</a></li>
		                <li><a class="dropdown-item" href="#">생산계획 기준 구매요청서 생성</a></li>
		                <li><a class="dropdown-item" href="#">유해화학물 판정 기준 물질 등록</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">현장 구매요청 관리</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">구매요청서 등록</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">Material별 재고계획 관리</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">구매 PO 잔량 연결</a></li>
		                <li><a class="dropdown-item" href="#">창고 장부재고 수량 연결</a></li>
		                <li><a class="dropdown-item" href="#">구매요청서 기준 입고 수량 연결</a></li>
		                <li><a class="dropdown-item" href="#">Material별 일일 재고 계획 생성</a></li>
		              </ul>
	            	</li>
              	</ul>
              	<a class="dropdown-item dropdown-toggle" href="#">발주관리</a>
            	<ul class="dropdown-menu">
	                <li class="dropdown-submenu">
	                <a class="dropdown-item dropdown-toggle" href="###">구매 요청</a>
	                <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="${contextPath}/Purchasing/Request.jsp">등록/수정/조회 </a></li>
		             </ul>
		             </li>
		              <li class="dropdown-submenu">
		             <a class="dropdown-item dropdown-toggle" href="#">구매 발주서</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">MPR구매 요청 발주서 전황</a></li>
		                <li><a class="dropdown-item" href="${contextPath}/Material_Order/OrderRegistform.jsp">긴급 자재 발주서 등록</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">구매 발주 품의</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">발주서 발행 품의상신</a></li>
		                <li><a class="dropdown-item" href="#">발주 품의 결과 조회</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">납기 관리</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">PO Item별 입고 예정일자 관리</a></li> 
		                <li><a class="dropdown-item" href="#">PO 잔령 GR Block처리</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">수입자재 Document 관리</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">선적 B/L 등록</a></li> 
		                <li><a class="dropdown-item" href="#">수입관세 납부증명서 등록</a></li>
		                <li><a class="dropdown-item" href="#">선급금 송금 내역 관리</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">매입부대비 관리</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">수입우대비 등록</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">IQC NG 물품 관리</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">IQC NG품 반품 의뢰</a></li>
		              </ul>
	            	</li>
              	</ul>
              	<a class="dropdown-item dropdown-toggle" href="#">매입마감</a>
            	<ul class="dropdown-menu">
	                <li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">입고마감 일정관리</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">입고 마감 일정 등록</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">내자 입고 마감</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">Vendor별 입고금액 연결</a></li>
		                <li><a class="dropdown-item" href="#">Vendor 마감 자료 Upload</a></li>
		                <li><a class="dropdown-item" href="#">Vendor별 입고 금액 확정</a></li>
		                <li><a class="dropdown-item" href="#">매입 세금계산서 Upload</a></li>
		                <li><a class="dropdown-item" href="#">Vendoe 입고금액 vs 매입세금계산서</a></li>
		                <li><a class="dropdown-item" href="#">내자 입고 마감 전표 생성</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">외장 입고 마감</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">외자 입고 형환 집계</a></li> 
		                <li><a class="dropdown-item" href="#">입고자재별 B/L, PO 집계</a></li>
		                <li><a class="dropdown-item" href="#">B/L별 매입부대비 집계</a></li>
		                <li><a class="dropdown-item" href="#">매입부대비 자재별 귀속</a></li>
		                <li><a class="dropdown-item" href="#">외자 입고마감 전표 생성</a></li>
		              </ul>
	            	</li>
              	</ul>
              	<a class="dropdown-item dropdown-toggle" href="#">Reporting</a>
            	<ul class="dropdown-menu">
	                <li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">구매계획현황 Rep</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">구매요청 현황 조회</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">발주형환 Rep</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">발주서 발행현황</a></li>
		                <li><a class="dropdown-item" href="#">발주서별 잔량 현황</a></li>
		                <li><a class="dropdown-item" href="#">이동 위치별 PO잔령 현황</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">일별 재고계획 Rep</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">일별 재고 전망</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">매입 현황 조회 Rep</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">월별 매입/입고 현황</a></li>
		                <li><a class="dropdown-item" href="#">월/분기/년도별 매입/입고 추세</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">관세환급 가능<br>수입면장 현황 Rep</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">소멸시효 도래 수입면장 현황</a></li>
		                <li><a class="dropdown-item" href="#">환급 불능 관세 현황</a></li>
		              </ul>
	            	</li>
              	</ul>
              	<a class="dropdown-item dropdown-toggle" href="#">Master Data</a>
            	<ul class="dropdown-menu">
	                <li><a class="dropdown-item" href="#">PurchasingGropu</a></li>
	                <li><a class="dropdown-item" href="#">Material/Vendor 조달 L/T</a></li>
	                <li><a class="dropdown-item" href="#">Material 안전재고 관리</a></li>
	                <li><a class="dropdown-item" href="#">단종 Material 관리</a></li>
	                <li><a class="dropdown-item" href="#">Mateiral별 거래가격 관리</a></li>
	                <li><a class="dropdown-item" href="#">외자 표준부대비율</a></li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">MSDS 관리</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">Material별 구성성분 등록</a></li>
		                <li><a class="dropdown-item" href="#">P_Code 등록</a></li>
		                <li><a class="dropdown-item" href="#">H_Code 등록</a></li>
		                <li><a class="dropdown-item" href="#">유해화학물 그림문자 등록</a></li>
		                <li><a class="dropdown-item" href="#">유해화학물 판정 기준 물질 등록</a></li>
		              </ul>
	            	</li>
              	</ul>
            </li>         
          </ul>
        </li>
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle Category" href="#" id="navbarDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false">Production</a>
          <!-- <ul class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
            <li class="dropdown-submenu">
            	<a class="dropdown-item dropdown-toggle" href="#">Global</a>
            	<ul class="dropdown-menu">
                	<li><a class="dropdown-item" href="#">국가(Nationality)</a></li>
              	</ul>
            </li>         
          </ul> -->
        </li>
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle Category" href="#" id="navbarDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false">Sales&Distribution</a>
          <!-- <ul class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
            <li class="dropdown-submenu">
            	<a class="dropdown-item dropdown-toggle" href="#">Global</a>
            	<ul class="dropdown-menu">
               		<li><a class="dropdown-item" href="#">국가(Nationality)</a></li>
              	</ul>
            </li>         
          </ul> -->
        </li>
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle Category" href="#" id="navbarDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false">Material Inventory</a>
          <ul class="dropdown-menu Sang" aria-labelledby="navbarDropdownMenuLink">
            <li class="dropdown-submenu">
            	<a class="dropdown-item dropdown-toggle" href="#">Material 입출고관리</a>
            	<ul class="dropdown-menu">
	                <li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">입하(납품) 등록</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">발주품 입하등록</a></li>
		                <li><a class="dropdown-item" href="#">입하품 선별분할등록</a></li>
		                <li><a class="dropdown-item" href="#">입하취소(IQC불량반품)처리</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">Material 입고처리</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="${contextPath}/Material_Input/MatInput.jsp">매입 입고처리</a></li>
		                <li><a class="dropdown-item" href="#">생산(반제품/제품) 입고처리</a></li>
		                <li><a class="dropdown-item" href="#">기타 입고처리</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">Material 출고처리</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="${contextPath}/Material_Output/MatOutput.jsp">생산투입 출고</a></li>
		                <li><a class="dropdown-item" href="#">생산외 사용출고</a></li>
		                <li><a class="dropdown-item" href="#">고객 매출(OnBoard)출고</a></li>
		                <li><a class="dropdown-item" href="#">Backlog Error 처리</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">창고간 이동(이체)처리</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">양품자재 이체</a></li>
		                <li><a class="dropdown-item" href="#">제상품 고객납품</a></li>
		                <li><a class="dropdown-item" href="#">품질검사 이체</a></li>
		                <li><a class="dropdown-item" href="#">불용/부실 이체</a></li>
		              </ul>
	            	</li>
              	</ul>
              	<a class="dropdown-item dropdown-toggle" href="#">재고 결산 관리</a>
            	<ul class="dropdown-menu">
	                <li><a class="dropdown-item" href="#">Material 공급요청서 등록</a></li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">재고실사</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">재고실사결과 등록</a></li>
		                <li><a class="dropdown-item" href="#">실사차이 원인 및 정산</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">수입부대비 정산</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">B/L별 수입부대비 집계</a></li>
		                <li><a class="dropdown-item" href="#">수입부대비 입고자재별 귀속(총평균)</a></li>
		                <li><a class="dropdown-item" href="#">재료비 수입부대비 계산(표준부대비)</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">수불표 생성</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">총평균 수불표 생성</a></li>
		                <li><a class="dropdown-item" href="#">총평균단수차 조정</a></li>
		                <li><a class="dropdown-item" href="#">이동평균 수불표 생성</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">재고자산 수불마감 일정관리</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">입고마감 일정 등록</a></li>
		                <li><a class="dropdown-item" href="#">출고마감 일정 등록</a></li>
		                <li><a class="dropdown-item" href="#">이동(이체)마감 일정 등록</a></li>
		              </ul>
	            	</li>
              	</ul>
              	<a class="dropdown-item dropdown-toggle" href="#">Reporting</a>
            	<ul class="dropdown-menu">
	                <li><a class="dropdown-item" href="#">발주품 입하 진행현황</a></li>
	                <li><a class="dropdown-item" href="#">입하품 입고 진행현황</a></li>
					<li><a class="dropdown-item" href="#">발주서 입고 진행현황</a></li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggleF" href="#">Material 재고 보유현황</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">상태별</a></li>
		                <li><a class="dropdown-item" href="#">창고 적재위치별</a></li>
		                <li><a class="dropdown-item" href="#">자재 Lot별 재고현황</a></li>
		              </ul>
	            	</li>
	            	<li><a class="dropdown-item" href="#">원자재 입출고 현황</a></li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">총평균 매입품 수불표</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">Company Level</a></li>
		                <li><a class="dropdown-item" href="#">Plant Level</a></li>
		              </ul>
	            	</li>
	            	<li><a class="dropdown-item" href="#">이동평균 매입품 수불표</a></li>
	            	<li><a class="dropdown-item" href="#">제상품 입출고 현황</a></li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">고객별 제상품 출고현황</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">납품기준</a></li>
		                <li><a class="dropdown-item" href="#">마감(OnBoard)기준</a></li>
		              </ul>
	            	</li>
	            	<li><a class="dropdown-item" href="#">총편균 제상품 수불표</a></li>
              	</ul>
            </li>      
          </ul>
        </li>
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle Category" href="#" id="navbarDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false">Equipment</a>
          <!-- <ul class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
            <li class="dropdown-submenu">
            	<a class="dropdown-item dropdown-toggle" href="#">Global</a>
            	<ul class="dropdown-menu">
                	<li><a class="dropdown-item" href="#">국가(Nationality)</a></li>
              	</ul>
            </li>         
          </ul> -->
        </li>
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle Category" href="#" id="navbarDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false">Quality Control</a>
          <ul class="dropdown-menu Sang" aria-labelledby="navbarDropdownMenuLink">
            <li class="dropdown-submenu">
            	<a class="dropdown-item dropdown-toggle" href="#">IQC</a>
            	<ul class="dropdown-menu">
	                <li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">입하현황</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">IQC 대상 SPL Label 발생</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">IQC 검사</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">검사항목별 측정값 등록</a></li>
		                <li><a class="dropdown-item" href="#">IQC 판정 등록</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">IQC 품질 현황 Report</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">수입검사 NG형환 조회</a></li>
		                <li><a class="dropdown-item" href="#">불량유형별 발생 현황</a></li>
		                <li><a class="dropdown-item" href="#">Vendor별 불량유형 현황</a></li>
		                <li><a class="dropdown-item" href="#">Vendor별 불량 발생 추세</a></li>
		              </ul>
	            	</li>
              	</ul>
              	<a class="dropdown-item dropdown-toggle" href="#">OQC</a>
            	<ul class="dropdown-menu">
	                <li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">OQC 검사 접수 현황</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">OQC SPL Label 발생</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">OQC 검사</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">검사항목별 측정값 등록</a></li>
		                <li><a class="dropdown-item" href="#">OQC 판정 등록</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">OQC 품질현황 Report</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">출하검사 NG현황 조회</a></li>
		                <li><a class="dropdown-item" href="#">불량유형별 발생 현황</a></li>
		                <li><a class="dropdown-item" href="#">품목별 불량유형 현황</a></li>
		                <li><a class="dropdown-item" href="#">기간별 불량 발생 추세</a></li>
		              </ul>
	            	</li>
              	</ul>
              	<a class="dropdown-item dropdown-toggle" href="#">공정품질</a>
            	<ul class="dropdown-menu">
	                <li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">공정 자주검사</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">검사항목별 측정값 등록</a></li>
		                <li><a class="dropdown-item" href="#">자주검사 판정 등록</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">Lot Holding 관리</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">Lot Holding 관리</a></li>
		                <li><a class="dropdown-item" href="#">Lot Holding 해제</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">공정별 품질현황 Report</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">공정불량 밣생 현황</a></li> 
		                <li><a class="dropdown-item" href="#">검사항목별 측정값 등록</a></li>
		                <li><a class="dropdown-item" href="#">공정별 발생 불량유형 현황</a></li>
		                <li><a class="dropdown-item" href="#">불량유형별 불량발생원인</a></li>
		                <li><a class="dropdown-item" href="#">품목별 불량유형 발생 현황</a></li>
		              </ul>
	            	</li>
              	</ul>
              	<a class="dropdown-item dropdown-toggle" href="#">고객 VOC</a>
            	<ul class="dropdown-menu">
	                <li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">고객 VOC 접수</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">고객 VOC 등록</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">VOC F/Up</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">VOC 발생 원인 등록</a></li>
		                <li><a class="dropdown-item" href="#">VOC 개선방안 등록</a></li>
		                <li><a class="dropdown-item" href="#">VOC 개선안 검증결과 등록</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">VOC 처리 결과</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">VOC 대응 결과 등록</a></li> 
		                <li><a class="dropdown-item" href="#">Claim 등록</a></li>
		                <li><a class="dropdown-item" href="#">RMA 접수</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">VOC 현황 Report</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">VOC접수 및 진행 현황</a></li> 
		                <li><a class="dropdown-item" href="#">고객별 VOC 발생현황</a></li>
		                <li><a class="dropdown-item" href="#">고객 불량 Costs 현황</a></li>
		              </ul>
	            	</li>
              	</ul>
              	<a class="dropdown-item dropdown-toggle" href="#">Vendor Audit</a>
            	<ul class="dropdown-menu">
	                <li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">거래처 Audity 계획 등록</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">Audit 항목 등록</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">Audit 실적 등록</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">Audit 항목별 측정값 등록</a></li>
		                <li><a class="dropdown-item" href="#">Audit 판정 등록</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">양상승인 관리</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">Site별 양산승인 품목 등록</a></li>
		                <li><a class="dropdown-item" href="#">품목별 양산승인 설비 등록</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">Vendor Audit 현황 Report</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">Vendor Audit 진행 현황</a></li>
		                <li><a class="dropdown-item" href="#">Vendor Site별 Audit 결과</a></li>
		                <li><a class="dropdown-item" href="#">Vendor별 Audit 검사내용</a></li>
		                <li><a class="dropdown-item" href="#">Vendor별 양산 승인 형환</a></li>
		              </ul>
	            	</li>
              	</ul>
              	<a class="dropdown-item dropdown-toggle" href="#">Master Data</a>
            	<ul class="dropdown-menu">
	                <li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">IQC 검사항목 관리</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">IQC 검사항목</a></li>
		                <li><a class="dropdown-item" href="#">검사항목뵬 측정 Parameter</a></li>
		                <li><a class="dropdown-item" href="#">Parameter별 Spec 정의</a></li>
		              </ul>
	            	</li>
	                <li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">OQC 검사항목 관리</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">OQC 검사항목</a></li>
		                <li><a class="dropdown-item" href="#">검사항목뵬 측정 Parameter</a></li>
		                <li><a class="dropdown-item" href="#">Parameter별 Spec 정의</a></li>
		              </ul>
	            	</li>
	                <li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">공정품질 검사항목 관리</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">공정품질 검사항목</a></li>
		                <li><a class="dropdown-item" href="#">검사항목별 측정 Parameter</a></li>
		                <li><a class="dropdown-item" href="#">Parameter별 Spec 정의</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">불량유형 관리</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">수입불량코드 등록</a></li>
		                <li><a class="dropdown-item" href="#">OQC불량코드 등록</a></li>
		                <li><a class="dropdown-item" href="#">공정불량코드 등록</a></li>
		                <li><a class="dropdown-item" href="#">불량유형그룹 등록</a></li>
		              </ul>
	            	</li>
              	</ul>
            </li>
          </ul>
        </li>
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle Category" href="#" id="navbarDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false">HRM</a>
          <!-- <ul class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
            <li class="dropdown-submenu">
            	<a class="dropdown-item dropdown-toggle" href="#">Global</a>
            	<ul class="dropdown-menu">
                	<li><a class="dropdown-item" href="#">국가(Nationality)</a></li>
              	</ul>
            </li>         
          </ul> -->
        </li>
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle Category" href="#" id="navbarDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false">Accounting</a>
          <ul class="dropdown-menu Sang" aria-labelledby="navbarDropdownMenuLink">
            <li class="dropdown-submenu">
            	<a class="dropdown-item dropdown-toggle" href="#">경비청구</a>
            	<ul class="dropdown-menu">
	                <li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">부서경비 청구</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">신용카드 경비 등록</a></li>
		                <li><a class="dropdown-item" href="#">세금계산서(계산서) 경비 등록</a></li>
		                <li><a class="dropdown-item" href="#">현급영수증 경비 등록</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">출장비 경비 청구</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">일일출장 경비 등록</a></li>
		                <li><a class="dropdown-item" href="#">국내출장 경비 등록</a></li>
		                <li><a class="dropdown-item" href="#">해외출장 경비 등록</a></li>
		              </ul>
	            	</li>
              	</ul>
              	<a class="dropdown-item dropdown-toggle" href="#">전표 생성 및 연결</a>
            	<ul class="dropdown-menu">
	                <li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">경비 전표 생성</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">부서경비 전표 연결</a></li>
		                <li><a class="dropdown-item" href="#">출장비 경비 전표 연결</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">대체전표 입력</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">급여 대체전표 생성</a></li>
		                <li><a class="dropdown-item" href="#">인건비성 복후비 생성</a></li>
		                <li><a class="dropdown-item" href="#">퇴직급여충당금 생성</a></li>
		                <li><a class="dropdown-item" href="#">상여금 대체전표 생성</a></li>
		                <li><a class="dropdown-item" href="#">충당금 대체전표 생성</a></li>
		                <li><a class="dropdown-item" href="#">기간 수익비용 전표 생성</a></li>
		                <li><a class="dropdown-item" href="#">유동성 대체전표 생성</a></li>
		                <li><a class="dropdown-item" href="#">미결정리전표 입력</a></li>
		                <li><a class="dropdown-item" href="#">외화 채권채무 평가</a></li>
		                <li><a class="dropdown-item" href="#">일반 대체 전표 입력</a></li>
		                <li><a class="dropdown-item" href="#">원가정리전표 입력</a></li>
		                <li><a class="dropdown-item" href="#">결산조정전표 입력</a></li>
		                <li><a class="dropdown-item" href="#">잉여금 처분전표 입력</a></li>
		              </ul>
	            	</li>
              	</ul>
              	<a class="dropdown-item dropdown-toggle" href="#">예금/채권/채무 관리</a>
            	<ul class="dropdown-menu">
	                <li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">입출금 전표 입력</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">예금</a></li>
		                <li><a class="dropdown-item" href="#">현금</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">매입 채무관리</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">채무 발생 전표입력</a></li>
		                <li><a class="dropdown-item" href="#">대금지불 대상 확정</a></li>
		                <li><a class="dropdown-item" href="#">채무 미결정리전표 입력</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">매출 채권관리</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">채권 발생 전표입력</a></li> 
		                <li><a class="dropdown-item" href="#">매출거래처 여신한도 등록</a></li>
		                <li><a class="dropdown-item" href="#">채권 미결정리전표 입력</a></li>
		              </ul>
	            	</li>
              	</ul>
              	<a class="dropdown-item dropdown-toggle" href="#">유형 / 무형지산</a>
            	<ul class="dropdown-menu">
	                <li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">유형(무형)자산 관리</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">건설중자산 등록</a></li>
		                <li><a class="dropdown-item" href="#">건설자금이자 계산</a></li>
		                <li><a class="dropdown-item" href="#">본자산 대체전표 생성</a></li>
		                <li><a class="dropdown-item" href="#">자본적지출 전표 입력</a></li>
		                <li><a class="dropdown-item" href="#">국고보조금 자산대체</a></li>
		                <li><a class="dropdown-item" href="#">감각상각비 계산</a></li>
		                <li><a class="dropdown-item" href="#">감각상각비 전표 생성</a></li>
		                <li><a class="dropdown-item" href="#">임차자산 등록</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">유형자산 처분전표 생성</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">매각</a></li>
		                <li><a class="dropdown-item" href="#">이동</a></li>
		                <li><a class="dropdown-item" href="#">폐기</a></li>
		                <li><a class="dropdown-item" href="#">대여</a></li>
		              </ul>
	            	</li>
	            	<li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">유형자산 유지보수 전표생성</a>
		              <ul class="dropdown-menu">
		                <li><a class="dropdown-item" href="#">수익적 지출전표 등록</a></li>
		              </ul>
	            	</li>
              	</ul>
              	<a class="dropdown-item dropdown-toggle" href="#">Reporting</a>
            	<ul class="dropdown-menu">
	                <li><a class="dropdown-item" href="#">합계잔액시산표</a></li>
	                <li><a class="dropdown-item" href="#">재무상태표</a></li>
	                <li><a class="dropdown-item" href="#">손익계산서</a></li>
	                <li><a class="dropdown-item" href="#">손익계산서 타계정명세</a></li>
	                <li><a class="dropdown-item" href="#">현금흐름표</a></li>
	                <li><a class="dropdown-item" href="#">이익잉여금처분계산서</a></li>
	                <li><a class="dropdown-item" href="#">자본변동표</a></li>
	                <li><a class="dropdown-item" href="#">계정별 보조원장</a></li>
	                <li><a class="dropdown-item" href="#">계정별 미결명세표</a></li>
	                <li><a class="dropdown-item" href="#">제조원가명세표</a></li>
	                <li><a class="dropdown-item" href="#">제조원가 타계정명세</a></li>
	                <li><a class="dropdown-item" href="#">전표조회</a></li>
	                <li><a class="dropdown-item" href="#">현금에금 시재표 생성/출력</a></li>
              	</ul>
            </li>
          </ul>
        </li>
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle Category" href="#" id="navbarDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false">Financing</a>
          <!-- <ul class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
            <li class="dropdown-submenu">
            	<a class="dropdown-item dropdown-toggle" href="#"></a>
            	<ul class="dropdown-menu">
                	<li><a class="dropdown-item" href="#">국가(Nationality)</a></li>
              	</ul>
            </li>         
          </ul> -->
        </li>
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle Category" href="#" id="navbarDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false">Costing</a>
          <!-- <ul class="dropdown-menu" aria-labelledby="navbarDropdownMenuLink">
            <li class="dropdown-submenu">
            	<a class="dropdown-item dropdown-toggle" href="#">Global</a>
            	<ul class="dropdown-menu">
                	<li><a class="dropdown-item" href="#">국가(Nationality)</a></li>
              	</ul>
            </li>         
          </ul> -->
        </li>
        
      </ul>
    </div>
  </div>
  <div class="LoginArea">
  	<%
  		if(User_Id != null){
  	%>
  		<a href="${contextPath}/Basic/Mypage.jsp" class="UsrInfoLink" id="UserBtn"><%=User_Id %></a>
  		<a href="${contextPath}/Basic/Logout.jsp" class="UsrInfoLink" id="LogOutBtn">LOGOUT</a>
  	<%
  		} else{
  	%>
  		<a href="${contextPath}/Basic/Login.jsp" id="UserBtn">LOGIN</a>
  	<%
  		}
  	%>
  </div>
</nav>
</body>
