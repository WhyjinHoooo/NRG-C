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
    <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navbarNavDropdown" aria-controls="navbarNavDropdown" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>
    <div class="collapse navbar-collapse" id="navbarNavDropdown">
      <ul class="navbar-nav">
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle Category" href="#" id="navbarDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false">MASTER</a>
          <ul class="dropdown-menu Sang" aria-labelledby="navbarDropdownMenuLink">
            <li class="dropdown-submenu">
            	<a class="dropdown-item dropdown-toggle" href="#">품목코드</a>
              	<a class="dropdown-item dropdown-toggle" href="#">거래처 코드</a>
              	<a class="dropdown-item dropdown-toggle" href="#">단가관리</a>
            	<ul class="dropdown-menu">
	                <li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">구매단가</a>
		              <a class="dropdown-item dropdown-toggle" href="#">판매단가</a>
		              <a class="dropdown-item dropdown-toggle" href="#">환울등록</a>
	            	</li>
              	</ul>
              	<a class="dropdown-item dropdown-toggle" href="#">조직코드</a>
            	<ul class="dropdown-menu">
	                <li class="dropdown-submenu">
		              <a class="dropdown-item dropdown-toggle" href="#">공정코드</a>
		              <a class="dropdown-item dropdown-toggle" href="#">창고코드</a>
		              <a class="dropdown-item dropdown-toggle" href="#">창고입출고코드</a>
		              <a class="dropdown-item dropdown-toggle" href="#">원가집게조직</a>
	            	</li>
              	</ul>
              	<a class="dropdown-item dropdown-toggle" href="#">사용자 관리</a>
            </li>         
          </ul>
        </li>
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle Category" href="#" id="navbarDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false">수불 UPDATE</a>
          <ul class="dropdown-menu Sang" aria-labelledby="navbarDropdownMenuLink">
            <li class="dropdown-submenu">
            	<a class="dropdown-item dropdown-toggle" href="#">EXCEL 로딩</a>
            	<ul class="dropdown-menu">
                	<li><a class="dropdown-item" href="${contextPath}/StockFUpdate/Up_MatPurchase.jsp">자재매입 실적</a></li>
                	<li><a class="dropdown-item" href="${contextPath}/StockFUpdate/Up_Feedstock.jsp">생산투입 실적</a></li>
                	<li><a class="dropdown-item" href="${contextPath}/StockFUpdate/Up_Subpackreceipt.jsp">생산입고 실적</a></li>
                	<li><a class="dropdown-item" href="${contextPath}/StockFUpdate/Up_ProductShipment.jsp">제상품 판매실적</a></li>
                	<li><a class="dropdown-item" href="${contextPath}/StockFUpdate/Up_ProResult.jsp">공정별 작업실적</a></li>
                	<li><a class="dropdown-item" href="${contextPath}/StockFUpdate/Up_WorkOrder.jsp">작지 ID생성실적</a></li>
              	</ul>
              	<a class="dropdown-item dropdown-toggle" href="#">수불 생성</a>
            	<ul class="dropdown-menu">
                	<li><a class="dropdown-item" href="#">자재수불 생성</a></li>
                	<li><a class="dropdown-item" href="#">공정수불 생성</a></li>
                	<li><a class="dropdown-item" href="#">반제품 수불 생성</a></li>
                	<li><a class="dropdown-item" href="#">상품 수불 생성</a></li>
                	<li><a class="dropdown-item" href="#">제품 수불 생성</a></li>
              	</ul>
              	<a class="dropdown-item dropdown-toggle" href="#">수불 등록/수정</a>
            	<ul class="dropdown-menu">
                	<li><a class="dropdown-item" href="#">타계정출고 등록</a></li>
                	<li><a class="dropdown-item" href="#">로딩자료 수정</a></li>
              	</ul>
              	<a class="dropdown-item dropdown-toggle" href="#">수불 조회/확정</a>
            	<ul class="dropdown-menu">
                	<li><a class="dropdown-item" href="${contextPath}/StockMScreen/MatInven.jsp">수불 조회/확정</a></li>
                	<li><a class="dropdown-item" href="#">로딩자료 수정</a></li>
              	</ul>
            </li>         
          </ul>
        </li>
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle Category" href="#" id="navbarDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false">원재료 원가계산</a>
          <ul class="dropdown-menu Sang" aria-labelledby="navbarDropdownMenuLink">
            <li class="dropdown-submenu">
            	<a class="dropdown-item dropdown-toggle" href="#">원자재</a>
            	<ul class="dropdown-menu">
               		<li><a class="dropdown-item" href="#">기초 금액 연결</a></li>
               		<li><a class="dropdown-item" href="#">매입 생성연결</a></li>
               		<li><a class="dropdown-item" href="#">부대비 정산등록</a></li>
               		<li><a class="dropdown-item" href="#">재고 금액 생성</a></li>
              	</ul>
              	<a class="dropdown-item dropdown-toggle" href="#">자재수불 보회</a>
            	<ul class="dropdown-menu">
               		<li><a class="dropdown-item" href="#">수량 기준 수불표</a></li>
               		<li><a class="dropdown-item" href="#">수량+금액 기준 수불표</a></li>
               		<li><a class="dropdown-item" href="#">유형별 자재 입출현황</a></li>
               		<li><a class="dropdown-item" href="#">거래처/자재별 매입현황</a></li>
               		<li><a class="dropdown-item" href="#">기간 자재별 재고변동</a></li>
               		<li><a class="dropdown-item" href="#">기간 투입재료비 추세</a></li>
              	</ul>
            </li>         
          </ul>
        </li>
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle Category" href="#" id="navbarDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false">공정원가 관리</a>
          <ul class="dropdown-menu Sang" aria-labelledby="navbarDropdownMenuLink">
            <li class="dropdown-submenu">
            	<a class="dropdown-item dropdown-toggle" href="#">공정 우너가 배분기준 등록/수정</a>
              	<a class="dropdown-item dropdown-toggle" href="#">배부적수 관리</a>
            	<ul class="dropdown-menu">
	                <li><a class="dropdown-item" href="#">공정별 적수 생성</a></li>
	                <li><a class="dropdown-item" href="#">배부적수 수정/등록</a></li>
              	</ul>
              	<a class="dropdown-item dropdown-toggle" href="#">공정원가등록</a>
            	<ul class="dropdown-menu">
	                <li><a class="dropdown-item" href="#">공정별 가공비 생성</a></li>
	                <li><a class="dropdown-item" href="#">공정별 공통재료비 등록</a></li>
              	</ul>
              	<a class="dropdown-item dropdown-toggle" href="#">공정원가계산</a>
            	<ul class="dropdown-menu">
	                <li><a class="dropdown-item" href="#">기초재공 원가연결</a></li>
	                <li><a class="dropdown-item" href="#">iD별 직접재료비 집계</a></li>
	                <li><a class="dropdown-item" href="#">iD별 공통재료비 귀속</a></li>
	                <li><a class="dropdown-item" href="#">iD별 공정가공비 배부</a></li>
	                <li><a class="dropdown-item" href="#">발생원가 귀속 확정</a></li>
              	</ul>
            </li>      
          </ul>
        </li>
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle Category" href="#" id="navbarDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false">매출원가 계산</a>
          <ul class="dropdown-menu Sang" aria-labelledby="navbarDropdownMenuLink">
            <li class="dropdown-submenu">
            	<a class="dropdown-item dropdown-toggle" href="#">제품 매출원가 계산</a>
            	<ul class="dropdown-menu">
	                <li><a class="dropdown-item" href="#">기초제품 재고 연결</a></li>
	                <li><a class="dropdown-item" href="#">iD별 제품원가 소분귀속</a></li>
	                <li><a class="dropdown-item" href="#">소분품번 생산원가 집계</a></li>
	                <li><a class="dropdown-item" href="#">1차 제품 재고금액 생성</a></li>
	                <li><a class="dropdown-item" href="#">공정투입 Assy원가 귀속</a></li>
	                <li><a class="dropdown-item" href="#">제품 매울원가 조회/확정</a></li>
              	</ul>
              	<a class="dropdown-item dropdown-toggle" href="#">상품매출원가 계산</a>
            	<ul class="dropdown-menu">
	                <li><a class="dropdown-item" href="#">기초상품 재고 연결</a></li>
	                <li><a class="dropdown-item" href="#">당일 성품매입금액 연결</a></li>
	                <li><a class="dropdown-item" href="#">상품 매충원가 계산</a></li>
	                <li><a class="dropdown-item" href="#">상품 매출원가 조회/확정</a></li>
              	</ul>
              	<a class="dropdown-item dropdown-toggle" href="#">제품/상품 원가수불 관리</a>
            	<ul class="dropdown-menu">
	                <li><a class="dropdown-item" href="#">매출원가 수불표 조회</a></li>
	                <li><a class="dropdown-item" href="#">거러채별 매출손익 현황</a></li>
	                <li><a class="dropdown-item" href="#">거래처별 매충손익 추세</a></li>
	                <li><a class="dropdown-item" href="#">거래처별 매출 차이분석</a></li>
              	</ul>
            </li>      
          </ul>
        </li>
        <li class="nav-item dropdown">
          <a class="nav-link dropdown-toggle Category" href="#" id="navbarDropdownMenuLink" role="button" data-bs-toggle="dropdown" aria-expanded="false">발생원가 집계/배부</a>
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
