<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@page import="java.sql.DriverManager"%>
<%@page import="java.sql.Connection"%>
<%@page import="java.sql.PreparedStatement"%>
<%@page import="java.sql.ResultSet"%>
<%@page import="java.io.PrintWriter"%>


<%
	Connection conn = null;

	if(true)
	{
		String url = "jdbc:mysql://localhost:3306/project?useSSL=false"; //Database 이름은 campusdb 
		String id = "root";                     //MySQL에 접속을 위한 계정의 ID
		String pwd = "jinsang1027#";            //MySQL에 접속을 위한 계정의 암호
		Class.forName("com.mysql.jdbc.Driver");
		conn = DriverManager.getConnection(url, id, pwd);

		//out.println("<h1>MySQL DB 연결 성공</h1>");
	}else{
		
		
		Class.forName("oracle.jdbc.OracleDriver");
		conn = DriverManager.getConnection(
				"jdbc:oracle:thin:@localhost:1521/xe",  //Table Space 명은 xe
				"camuser", //Oracle에 접속을 위한 계정의 ID
				"tiger" //Oracle에 접속을 위한 계정의 암호
				);
		
		out.println("Oracle Connected");
	}
	
%>
