<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<%
    request.setCharacterEncoding("UTF-8");
    response.setCharacterEncoding("UTF-8");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>처리 결과 - ${fileName}</title>
<style>
    table { border-collapse: collapse; width: 80%; margin: 20px auto; }
    th, td { padding: 10px; border: 1px solid #ddd; text-align: left; }
    th { background-color: #f5f5f5; }
    .file-info { 
        text-align: center; 
        padding: 20px;
        font-size: 1.2em;
        color: #2c3e50;
    }
</style>
</head>
<body>

<div class="file-info">
    📁 업로드 파일명: <strong>${fileName}</strong>
</div>
<c:choose>
    <c:when test="${empty csvData}">
        <p class="file-info">⚠️ 변환된 데이터가 없습니다</p>
    </c:when>
    <c:otherwise>
        <table>
            <caption>변환 결과 (${fn:length(csvData)}행)</caption>
            <c:forEach items="${csvData}" var="row" varStatus="status">
                <tr>
                    <c:forEach items="${row}" var="cell">
                        <td>${cell}</td>
                    </c:forEach>
                </tr>
            </c:forEach>
        </table>
    </c:otherwise>
</c:choose>

</body>
</html>
