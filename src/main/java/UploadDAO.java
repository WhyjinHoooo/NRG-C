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
	public String FileSave(String filedata) {
		connDB();
		pstmt = null;
		String YN = "No";
		try {
			System.out.println("filedata : " + filedata);
			String sql = "INSERT INTO DocTable VALUES(?,?)";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, filedata);
			pstmt.setString(2, filedata.substring(3, 9));
			pstmt.executeUpdate();
			YN = "Yes";
		}catch (SQLException e) {
			e.printStackTrace();
		}finally {
            if (pstmt != null) try { pstmt.close(); } catch (Exception e) {}
            if (conn != null) try { conn.close(); } catch (Exception e) {}
        }
		return YN;
	}
	public String insertMSData(List<String[]> csvData) {
        connDB();
        String YN = "No";
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO matstock VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(sql);
            System.out.println("길이 : " + csvData.size());
            for (int i = 0; i < csvData.size(); i++) {
                String[] DataList = csvData.get(i);
                for (int j = 0; j < DataList.length; j++) {
                	//System.out.println("DataList[" + j + "] : " + DataList[j].trim());
                    pstmt.setString(j + 1, DataList[j].trim());
                }
                pstmt.setString(18, "PUR" + DataList[1].trim() + ".txt");
                pstmt.setString(19, "PUR" + DataList[0].trim() + String.format("%04d", i + 1));
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
	public String insertMIData(List<String[]> csvData) {
        connDB();
        String YN = "No";
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO matinput VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(sql);
            System.out.println("길이 : " + csvData.size());
            for (int i = 0; i < csvData.size(); i++) {
                String[] DataList = csvData.get(i);
                for (int j = 0; j < DataList.length; j++) {
                	//System.out.println("DataList[" + j + "] : " + DataList[j].trim());
                    pstmt.setString(j + 1, DataList[j].trim());
                }
                pstmt.setString(17, "BFG" + DataList[1].trim() + ".txt");
                pstmt.setString(18, "BFG" + DataList[0].trim() + String.format("%04d", i + 1));
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
	public String batchintake(List<String[]> csvData) {
		connDB();
        String YN = "No";
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO matsplit VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(sql);
            System.out.println("길이 : " + csvData.size());
            for (int i = 0; i < csvData.size(); i++) {
                String[] DataList = csvData.get(i);
                for (int j = 0; j < DataList.length; j++) {
                	//System.out.println("DataList[" + j + "] : " + DataList[j].trim());
                    pstmt.setString(j + 1, DataList[j].trim());
                }
                pstmt.setString(15, "MGR" + DataList[1].trim() + ".txt");
                pstmt.setString(16, "MGR" + DataList[0].trim() + String.format("%04d", i + 1));
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
	
	public String SalesDelivery(List<String[]> csvData) {
		connDB();
        String YN = "No";
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO matdeli VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(sql);
            System.out.println("길이 : " + csvData.size());
            for (int i = 0; i < csvData.size(); i++) {
                String[] DataList = csvData.get(i);
                for (int j = 0; j < DataList.length; j++) {
//                	System.out.println("i : " + i);
//                	System.out.println("DataList[" + j + "] : " + DataList[j].trim());
                    pstmt.setString(j + 1, DataList[j].trim());
                }
                pstmt.setString(17, "SDG" + DataList[1].trim() + ".txt");
                pstmt.setString(18, "SDG" + DataList[0].trim() + String.format("%04d", i + 1));
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
	public String Joborder(List<String[]> csvData) {
		connDB();
        String YN = "No";
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO matdeli VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(sql);
            System.out.println("길이 : " + csvData.size());
            for (int i = 0; i < csvData.size(); i++) {
                String[] DataList = csvData.get(i);
                for (int j = 0; j < DataList.length; j++) {
                    pstmt.setString(j + 1, DataList[j].trim());
                }
                pstmt.setString(17, "SDG" + DataList[1].trim() + ".txt");
                pstmt.setString(18, "SDG" + DataList[0].trim() + String.format("%04d", i + 1));
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
