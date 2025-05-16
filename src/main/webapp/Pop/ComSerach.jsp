<%@page import="java.sql.SQLException"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<%@ include file="../../mydbcon.jsp" %>
<link rel="stylesheet" href="../CSS/PopUp.css?after">
<title>Insert title here</title>
</head>
<body>
<h1>검색</h1>
<hr>
    <center>
		<div class="PopUpArea">
			<table class="">
				<thead>
			        <tr>
			            <th>코드</th><th>설명</th>
			        </tr>
			        </thead>
			        <tbody>
		    <%
		        try{
		        PreparedStatement pstmt = null;
		        ResultSet rs = null;
		        String sql = "SELECT * FROM comcodeset";		        
		        pstmt = conn.prepareStatement(sql);
		        rs = pstmt.executeQuery();
		        while(rs.next()){
		    %>
		         <tr>
					<td>
						<a href="javascript:void(0)" onClick="
							window.opener.document.querySelector('.ComCode').value='<%=rs.getString("comcode")%>';
							window.opener.document.querySelector('.ComCode').dispatchEvent(new Event('change'));
							window.close();"><%=rs.getString("comcode") %>
						</a>
					</td>
					<td><%=rs.getString("comDesc") %></td>
				</tr>
		    <%  
		        }
				}catch(SQLException e){
		            e.printStackTrace();
		        }
		    %>
		    	</tbody>
		    </table>    
		</div>    
    </center>
</body>
</html>
