<%@page import="java.time.format.DateTimeFormatter"%>
<%@page import="java.time.LocalDateTime"%>
<%@page import="java.sql.SQLException"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ include file="../../mydbcon.jsp" %>

<%
	String TextFile = request.getParameter("file");
	PreparedStatement txt_pstmt = null;
	ResultSet txt_rs = null;
	PreparedStatement Del_pstmt01 = null;    
	PreparedStatement Del_pstmt02 = null;
	try {
	    String pass = null;
	    String SearTxtSql = "SELECT * FROM DocTable WHERE InfoFile = ?";
	    txt_pstmt = conn.prepareStatement(SearTxtSql);
	    txt_pstmt.setString(1, TextFile);
	    txt_rs = txt_pstmt.executeQuery();
	    if(txt_rs.next()){
	        String FileDelSql ="DELETE FROM matstock WHERE document = ?";
	        Del_pstmt01 = conn.prepareStatement(FileDelSql);
	        Del_pstmt01.setString(1, TextFile);
	        Del_pstmt01.executeUpdate();
	        pass = "Done";
	    } else {
	        pass = "Nope";
	    }
	    if("Done".equals(pass)){
	        String DataDelSql = "DELETE FROM DocTable WHERE InfoFile = ?";
	        Del_pstmt02 = conn.prepareStatement(DataDelSql);
	        Del_pstmt02.setString(1, TextFile);
	        Del_pstmt02.executeUpdate();
	    }
	    out.print(pass);
	} catch(Exception e){
	    e.printStackTrace();
	} finally {
	    if(txt_rs != null) try { txt_rs.close(); } catch(Exception e) {}
	    if(txt_pstmt != null) try { txt_pstmt.close(); } catch(Exception e) {}
	    if(Del_pstmt01 != null) try { Del_pstmt01.close(); } catch(Exception e) {}
	    if(Del_pstmt02 != null) try { Del_pstmt02.close(); } catch(Exception e) {}
	}
%>
