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
		        String Comcode = request.getParameter("Comcode");
		        PreparedStatement pstmt = null;
		        ResultSet rs = null;
		        String sql = "SELECT * FROM filecodeset WHERE comcode = ?";
		        
		        pstmt = conn.prepareStatement(sql);
		        pstmt.setString(1, Comcode);
		        rs = pstmt.executeQuery();
		        
		        if(!rs.next()){
			%>
				<tr>
					<td></td>
				</tr>
			<%
				} else{
					do{
		    %>
		         <tr>
					<td><a href="javascript:void(0)" onClick="
							window.opener.document.querySelector('.UploadDataCode').value='<%=rs.getString("FileDivCode")%>';
							window.opener.document.querySelector('.UploadDataCode').dispatchEvent(new Event('change'));
							window.close();"><%=rs.getString("FileDivCode") %>             		
						</a>
					</td>
					<td><%=rs.getString("filedesc") %></td>
				</tr>
		    <%  
					} while(rs.next());
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
