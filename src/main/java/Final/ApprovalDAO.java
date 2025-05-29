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
	    				count = CountRs.getInt("length") + 1;
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

	public String forBFGdata(JSONObject jsonObj) {
		connDB();
	    String[] keyOrder = {"ComCode", "PlantCode", "UploadDataCode", "CalcMonth"};
	    String[] DataList = new String[keyOrder.length];
	    String result = null;
	    int length = 0;
	    int count = 0;
	    String YN = "No";
	    for (int i = 0; i < keyOrder.length; i++) {
	        DataList[i] = jsonObj.has(keyOrder[i]) ? jsonObj.get(keyOrder[i]).toString() : "";
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
	    				count = CountRs.getInt("length") + 1;
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

	    		CountSql = "SELECT COUNT(*) as length FROM matinput WHERE LEFT (document, 9) = ?";
	    		CountPstmt = conn.prepareStatement(CountSql);
	    		CountPstmt.setString(1, DataList[2] + DataList[3]);
	    		CountRs = CountPstmt.executeQuery();
	    		if(CountRs.next()) {
	    			length = CountRs.getInt("length");
	    		}
	    		String InsertsqlL =
	    			    "INSERT INTO InvenLogl (docnum, seq, movetype, closingmon, transactiondate, matcode, matdesc, spec, lotnum, mattype, quantity, " +
	    			    "storcode, stordesc, process, processDesc, workordnum, DeleteYN, plant, comcode, keyvalue) " +
	    			    "SELECT ?, ROW_NUMBER() OVER (ORDER BY (SELECT 1)), matinput.type, ?, matinput.delivery, matinput.itemno, matinput.item, matinput.spec, matinput.lot, matinput.stocktype, " +
	    			    "matinput.amount, matinput.whcode, matinput.warehouse, matinput.process, matinput.processdes, matinput.pono, ?, " +
	    			    "matinput.plant, matinput.company, CONCAT(?, LPAD(ROW_NUMBER() OVER (ORDER BY (SELECT 1)),4,'0')) " +
	    			    "FROM matinput " +
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

	public String forMGRdata(JSONObject jsonObj) {
		connDB();
	    String[] keyOrder = {"ComCode", "PlantCode", "UploadDataCode", "CalcMonth"};
	    String[] DataList = new String[keyOrder.length];
	    String result = null;
	    int length = 0;
	    int count = 0;
	    String YN = "No";
	    for (int i = 0; i < keyOrder.length; i++) {
	        DataList[i] = jsonObj.has(keyOrder[i]) ? jsonObj.get(keyOrder[i]).toString() : "";
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
	    				count = CountRs.getInt("length") + 1;
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

	    		CountSql = "SELECT COUNT(*) as length FROM matsplit WHERE LEFT (document, 9) = ?";
	    		CountPstmt = conn.prepareStatement(CountSql);
	    		CountPstmt.setString(1, DataList[2] + DataList[3]);
	    		CountRs = CountPstmt.executeQuery();
	    		if(CountRs.next()) {
	    			length = CountRs.getInt("length");
	    		}
	    		String InsertsqlL =
	    			    "INSERT INTO InvenLogl (docnum, seq, movetype, closingmon, transactiondate, matcode, matdesc, spec, lotnum, mattype, quantity, " +
	    			    "storcode, stordesc, workordnum, DeleteYN, plant, comcode, keyvalue) " +
	    			    "SELECT ?, ROW_NUMBER() OVER (ORDER BY (SELECT 1)), matsplit.type, ?, matsplit.delivery, matsplit.itemno, matsplit.item, matsplit.spec, matsplit.lot, matsplit.stocktype, " +
	    			    "matsplit.weight, matsplit.whcode, matsplit.warehouse, matsplit.pono, ?, " +
	    			    "matsplit.plant, matsplit.company, CONCAT(?, LPAD(ROW_NUMBER() OVER (ORDER BY (SELECT 1)),4,'0')) " +
	    			    "FROM matsplit " +
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

	public String forSDGdata(JSONObject jsonObj) {
		connDB();
	    String[] keyOrder = {"ComCode", "PlantCode", "UploadDataCode", "CalcMonth"};
	    String[] DataList = new String[keyOrder.length];
	    String result = null;
	    int length = 0;
	    int count = 0;
	    String YN = "No";
	    for (int i = 0; i < keyOrder.length; i++) {
	        DataList[i] = jsonObj.has(keyOrder[i]) ? jsonObj.get(keyOrder[i]).toString() : "";
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
	    				count = CountRs.getInt("length") + 1;
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

	    		CountSql = "SELECT COUNT(*) as length FROM matdeli WHERE LEFT (document, 9) = ?";
	    		CountPstmt = conn.prepareStatement(CountSql);
	    		CountPstmt.setString(1, DataList[2] + DataList[3]);
	    		CountRs = CountPstmt.executeQuery();
	    		if(CountRs.next()) {
	    			length = CountRs.getInt("length");
	    		}
	    		String InsertsqlL =
	    			    "INSERT INTO InvenLogl (docnum, seq, movetype, closingmon, transactiondate, matcode, matdesc, spec, lotnum, mattype, quantity, " +
	    			    "storcode, stordesc, salesordnum, vendcode, vendDesc, DeleteYN, plant, comcode, keyvalue) " +
	    			    "SELECT ?, ROW_NUMBER() OVER (ORDER BY (SELECT 1)), matdeli.type, ?, matdeli.delivery, matdeli.itemno, matdeli.item, matdeli.spec, matdeli.lot, matdeli.stocktype, " +
	    			    "matdeli.weight, matdeli.whcode, matdeli.warehouse, matdeli.pono, matdeli.vencode, matdeli.vender, ?, " +
	    			    "matdeli.plant, matdeli.company, CONCAT(?, LPAD(ROW_NUMBER() OVER (ORDER BY (SELECT 1)),4,'0')) " +
	    			    "FROM matdeli " +
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

	public void sumProcess() {
		connDB();
		String EmptySql = "SELECT COUNT(*) as length FROM sumtable";
		try {
			pstmt = conn.prepareStatement(EmptySql);
			ResultSet EmpRs = pstmt.executeQuery();
			int TableDataCount = EmpRs.getInt("length");
			if(TableDataCount == 0) {
				String DataSearchSql = "SELECT * FROM InvenLogl";
				PreparedStatement DataSearchPstmt = conn.prepareStatement(DataSearchSql);
				ResultSet DataSearchRs = DataSearchPstmt.executeQuery();
				
			}else {
				
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	
}
