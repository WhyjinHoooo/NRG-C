package Final;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;

public class ApprovalDAO {
	private Connection conn;
	private PreparedStatement pstmt;
	private DataSource dataFactory;
	private String sql;
	
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

	public String forPURdata(JSONObject jsonObj) {
		connDB();
	    String[] keyOrder = {"ComCode", "PlantCode", "UploadDataCode", "CalcMonth"};
	    String[] DataList = new String[keyOrder.length];
	    String result = null;
	    int length = 0;
	    int count = 0;
	    String YN = "No";
	    for (int i = 0; i < keyOrder.length; i++) {
	        DataList[i] = jsonObj.has(keyOrder[i]) ? jsonObj.get(keyOrder[i]).toString() : "";
	        System.out.println("창고 수불 관리 테이블 DataList[" + i + "] : " + DataList[i]);
	    }
	    ResultSet rs = null;
	    try {
	    	sql = "SELECT * FROM DocTable WHERE InfoFile = ?";
	    	pstmt = conn.prepareStatement(sql);
	    	pstmt.setString(1, DataList[2] + DataList[3] + ".txt");
	    	rs = pstmt.executeQuery();
	    	if(rs.next()) {
	    		String CountSql = "SELECT COUNT(*) as length FROM invenlogh";
	    		PreparedStatement CountPstmt = conn.prepareStatement(CountSql);
	    		ResultSet CountRs = CountPstmt.executeQuery();
	    		if(CountRs.next()) {
	    			if(CountRs.getInt("length") == 0) {
	    				count = 1;
	    			}else {
	    				count = CountRs.getInt("length");
	    			}
	    		}
	    		String InsertsqlH = "INSERT INTO invenlogh VALUES(?,?,?,?,?,?,?,?,?)";
	    		PreparedStatement InsertPstmtH = conn.prepareStatement(InsertsqlH);
	    		InsertPstmtH.setString(1, DataList[2] + DataList[3] + String.format("%04d", count));
	    		InsertPstmtH.setString(2, DataList[3]);
	    		InsertPstmtH.setInt(3, count);
	    		InsertPstmtH.setString(4, DataList[2]);
	    		InsertPstmtH.setString(5, null);
	    		InsertPstmtH.setString(6, null);
	    		InsertPstmtH.setString(7, "N");
	    		InsertPstmtH.setString(8, DataList[1]);
	    		InsertPstmtH.setString(9, DataList[0]);
	    		InsertPstmtH.executeUpdate();

	    		CountSql = "SELECT COUNT(*) as length FROM matstock WHERE LEFT (document, 9) = ?";
	    		CountPstmt = conn.prepareStatement(CountSql);
	    		CountPstmt.setString(1, DataList[2] + DataList[3]);
	    		CountRs = CountPstmt.executeQuery();
	    		if(CountRs.next()) {
	    			length = CountRs.getInt("length");
	    		}
	    		System.out.println("length : " + length);
	    		String InsertsqlL =
	    			    "INSERT INTO InvenLogl (docnum, seq, movetype, closingmon, transactiondate, matcode, matdesc, spec, lotnum, mattype, quantity, amount, " +
	    			    "storcode, stordesc, procuordnum, vendcode, vendDesc, DeleteYN, plant, comcode, keyvalue) " +
	    			    "SELECT ?, ROW_NUMBER() OVER (ORDER BY (SELECT 1)), matstock.type, ?, matstock.delivery, matstock.itemno, matstock.item, matstock.spec, matstock.lot, matstock.stocktype, " +
	    			    "matstock.weight, matstock.amount, matstock.whcode, matstock.warehouse, matstock.pono, matstock.vendor, matstock.vendorname, ?, " +
	    			    "matstock.plant, matstock.company, CONCAT(?, LPAD(ROW_NUMBER() OVER (ORDER BY (SELECT 1)),4,'0')) " +
	    			    "FROM matstock " +
	    			    "WHERE document = ?";
	    		PreparedStatement InsertPstmtL = conn.prepareStatement(InsertsqlL);
	    		InsertPstmtL.setString(1, DataList[2] + DataList[3] + String.format("%04d", count));
	    		InsertPstmtL.setString(2, DataList[3]);
	    		InsertPstmtL.setString(3, "N");
	    		InsertPstmtL.setString(4, DataList[2] + DataList[3] + String.format("%04d", count));
	    		InsertPstmtL.setString(5, DataList[2] + DataList[3] + ".txt");
	    		InsertPstmtL.executeUpdate();
	    		YN = "Yes";
	    	}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return YN;
	}
	
	
}
