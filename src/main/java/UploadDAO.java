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
            String sql = "INSERT INTO matstock VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(sql);
            System.out.println("길이 : " + csvData.size());
            for (int i = 0; i < csvData.size(); i++) {
                String[] DataList = csvData.get(i);
                for (int j = 0; j < DataList.length; j++) {
                    pstmt.setString(j + 1, DataList[j].trim());
                }
                pstmt.setString(20, "PUR" + DataList[1].trim() + ".txt");
                pstmt.setString(21, "PUR" + DataList[1].trim() + String.format("%04d", i + 1));
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
                pstmt.setString(18, "BFG" + DataList[1].trim() + String.format("%04d", i + 1));
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
                pstmt.setString(16, "MGR" + DataList[1].trim() + String.format("%04d", i + 1));
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
	public String Joborder(List<String[]> csvData, String file) {
		connDB();
        String YN = "No";
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO matorderlist VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            pstmt = conn.prepareStatement(sql);
            System.out.println("길이 : " + csvData.size());
            for (int i = 0; i < csvData.size(); i++) {
                String[] DataList = csvData.get(i);
                pstmt.setString(1,  DataList[0].trim());  
                pstmt.setString(2,  DataList[2].substring(0, 1).trim());
                pstmt.setString(3,  DataList[9].trim());
                pstmt.setString(4,  DataList[2].trim());
                pstmt.setString(5,  DataList[3].trim());
                pstmt.setString(6,  DataList[4].trim());
                pstmt.setString(7,  DataList[5].trim());
                pstmt.setString(8,  DataList[6].trim());
                pstmt.setString(9,  DataList[1].trim());
                pstmt.setString(10,  String.format("%.3f", DataList[7].trim()));
                pstmt.setString(11,  String.format("%.3f", DataList[8].trim()));
                for(int j = 12; j < 16 ; j++) {
                	pstmt.setString(j, null);
                }
                pstmt.setString(16,  DataList[10].trim());
                pstmt.setString(17,  DataList[11].trim());
                
                pstmt.setString(18, file);
                pstmt.setString(19, file.substring(0, 9) + String.format("%04d", i + 1));
                pstmt.addBatch();
            }
            pstmt.executeBatch();  // 일괄 실행
            System.out.println("file : " + file);
            YN = "Yes";
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (pstmt != null) try { pstmt.close(); } catch (Exception e) {}
            if (conn != null) try { conn.close(); } catch (Exception e) {}
        }
        return YN;
	}
	public String ProResult(List<String[]> csvData, String fileName) {
		connDB();
        String YN = "No";
        PreparedStatement pstmt = null;
        try {
            String sql = "INSERT INTO matseqlist VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
            pstmt = conn.prepareStatement(sql);
            System.out.println("길이 : " + csvData.size());
            for (int i = 0; i < csvData.size(); i++) {
                String[] DataList = csvData.get(i);
                for (int j = 0; j < DataList.length; j++) {
                    pstmt.setString(j + 1, DataList[j].trim());
                }
                pstmt.setString(13, fileName);
                pstmt.setString(14, fileName.substring(0, 9) + String.format("%04d", i + 1));
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
	
	public String DeletProcess(String fileName) {
		connDB();
		pstmt = null;
		String notice = "No Delete";
		try {
			System.out.println("filedata : " + fileName);
			String sql = "DELETE FROM DocTable WHERE InfoFile = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, fileName);
			pstmt.executeUpdate();
			notice = "Yes Delete";
		}catch (SQLException e) {
			e.printStackTrace();
		}finally {
            if (pstmt != null) try { pstmt.close(); } catch (Exception e) {}
            if (conn != null) try { conn.close(); } catch (Exception e) {}
        }
		return notice;
	}
}
