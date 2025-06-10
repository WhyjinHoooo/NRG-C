package FileDown;

import java.io.IOException;
import java.sql.*;

import javax.naming.*;
import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.sql.DataSource;

/**
 * Servlet implementation class MatDataDown
 */
@WebServlet("/MatDataDown")
public class MatDataDown extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public MatDataDown() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doHandle(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doHandle(request, response);
	}

	private void doHandle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Connection conn = null;
	    PreparedStatement pstmt = null;
	    DataSource dataFactory = null;
	    ResultSet rs = null;
	    try {
	        Context ctx = new InitialContext();
	        Context envContext = (Context) ctx.lookup("java:/comp/env");
	        dataFactory = (DataSource) envContext.lookup("jdbc/mysql"); // 이름 확인 필수!
	        conn = dataFactory.getConnection();

	        String Sql = "SELECT * FROM sumtable";
	        pstmt = conn.prepareStatement(Sql);
	        rs = pstmt.executeQuery();

	        StringBuilder csv = new StringBuilder();
	        ResultSetMetaData meta = rs.getMetaData();
	        int colCount = meta.getColumnCount();
	        for (int i = 1; i <= colCount; i++) {
	            csv.append(meta.getColumnName(i));
	            if (i < colCount) csv.append(",");
	        }
	        csv.append("\n");

	        while (rs.next()) {
	            for (int i = 1; i <= colCount; i++) {
	                String value = rs.getString(i);
	                // CSV 특수문자 처리 (예: 콤마, 따옴표 등)
	                if (value != null && (value.contains(",") || value.contains("\"") || value.contains("\n"))) {
	                    value = "\"" + value.replace("\"", "\"\"") + "\"";
	                }
	                csv.append(value != null ? value : "");
	                if (i < colCount) csv.append(",");
	            }
	            csv.append("\n");
	        }

		 response.setContentType("text/csv; charset=UTF-8");
		 response.setHeader("Content-Disposition", "attachment; filename=\"창고수불데이터.csv\"");
		 response.setCharacterEncoding("UTF-8");
		 response.getWriter().write(csv.toString());
		 response.getWriter().flush();

		} catch (Exception e) {
	        e.printStackTrace();
	        response.reset(); // 혹시 이미 헤더를 보냈다면 리셋
	        response.setContentType("text/plain; charset=UTF-8");
	        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        response.getWriter().write("CSV 생성 실패: " + e.getMessage());
	    } finally {
	        if (rs != null) try { rs.close(); } catch (SQLException e) {}
	        if (pstmt != null) try { pstmt.close(); } catch (SQLException e) {}
	        if (conn != null) try { conn.close(); } catch (SQLException e) {}
	    }
	}
}
