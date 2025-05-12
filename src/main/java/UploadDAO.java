import java.util.Arrays;
import java.util.List;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class UploadDAO {
	private Connection conn;
	private PreparedStatement pstmt;
	private DataSource dataFactory;
	
	private void connDB() {
        try {
            Context ctx = new InitialContext();
            Context envContext = (Context) ctx.lookup("java:/comp/env");
            dataFactory = (DataSource) envContext.lookup("jdbc/mysql");
            conn = dataFactory.getConnection();
            System.out.println("DB 접속 성공");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public String insertCsvData(List<String[]> csvData) {
        connDB();
        String YN = "No";
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO testtable VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(sql);
            System.out.println("길이 : " + csvData.size());
            for (int i = 0; i < csvData.size(); i++) {
                String[] DataList = csvData.get(i);
                pstmt.setString(1, "PUR" + DataList[0].trim() + String.format("%04d", i + 1));
                for (int j = 0; j < DataList.length; j++) {
                    pstmt.setString(j + 2, DataList[j].trim());
                }
                pstmt.addBatch();
            }
            pstmt.executeBatch();  // 일괄 실행
            YN = "Yes";
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (Exception e) {}
            if (conn != null) try { conn.close(); } catch (Exception e) {}
        }
        return YN;
    }
}
