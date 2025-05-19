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
		        String sql = "SELECT * FROM plantset WHERE comcode = ?";
		        
		        pstmt = conn.prepareStatement(sql);
		        pstmt.setString(1, Comcode);
		        rs = pstmt.executeQuery();
		        
		        if(!rs.next()){
		    %>
		    	<tr>
		    		<td colspan="2"><a href="javascript:void(0)" onClick="window.close();">기업과 연동된 공장은 없습니다.</a></td>
				</tr>
		    		
		    <%
		        } else{
		        	do{
			%>
		         <tr>
					<td>
						<a href="javascript:void(0)" onClick="
							window.opener.document.querySelector('.PlantCode').value='<%=rs.getString("plant")%>';
							window.opener.document.querySelector('.PlantCode').dispatchEvent(new Event('change'));
						window.close();"><%=rs.getString("plant") %>
						</a>
					</td>
					<td><%=rs.getString("plantDesc") %></td>
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
