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
			int TableDataCount = 0;
			if(EmpRs.next()) {
				TableDataCount = EmpRs.getInt("length");
			}
			String DataSearchSql = null;
			PreparedStatement DataSearchPstmt = null;
			ResultSet DataSearchRs = null;
			System.out.println("TableDataCount : " + TableDataCount);
			if(TableDataCount == 0) {
				DataSearchSql = "SELECT * FROM InvenLogl";
				DataSearchPstmt = conn.prepareStatement(DataSearchSql);
				DataSearchRs = DataSearchPstmt.executeQuery();
				boolean Isfirst = true;				
				while (DataSearchRs.next()) {				
					if (Isfirst) {
					    String insertSql = "INSERT INTO sumtable VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					    PreparedStatement insertPstmt = conn.prepareStatement(insertSql);
					    insertPstmt.setString(1, DataSearchRs.getString("closingmon"));
					    insertPstmt.setString(2, DataSearchRs.getString("comcode"));
					    insertPstmt.setString(3, DataSearchRs.getString("plant"));
					    insertPstmt.setString(4, DataSearchRs.getString("storcode"));
					    insertPstmt.setString(5, DataSearchRs.getString("lotnum"));
					    insertPstmt.setString(6, DataSearchRs.getString("matcode"));
					    insertPstmt.setString(7, DataSearchRs.getString("matdesc"));
					    insertPstmt.setString(8, DataSearchRs.getString("mattype"));
					    insertPstmt.setString(9, DataSearchRs.getString("spec"));
					    insertPstmt.setString(10, "0");

					    String MVType = DataSearchRs.getString("movetype").substring(0, 2);
					    if (MVType.equals("GR")) {
					        insertPstmt.setDouble(11, DataSearchRs.getDouble("quantity"));
						    insertPstmt.setString(12, "0");
						    insertPstmt.setString(13, "0");
					        insertPstmt.setDouble(14, 0);
						    insertPstmt.setDouble(15, DataSearchRs.getDouble("quantity"));
					    } else {
					    	insertPstmt.setDouble(11, 0);
						    insertPstmt.setString(12, "0");
						    insertPstmt.setString(13, "0");
					        insertPstmt.setDouble(14, DataSearchRs.getDouble("quantity"));
					        insertPstmt.setDouble(15, -DataSearchRs.getDouble("quantity"));
					    }
					    insertPstmt.executeUpdate();
					    Isfirst = false;
					}else {
						String scanSql = "SELECT GrTransacQty, GiTransacQty FROM sumtable WHERE comcode = ? AND plant = ? AND warehouse = ? AND lotnum = ?";
					    PreparedStatement scanPstmt = conn.prepareStatement(scanSql);
					    scanPstmt.setString(1, DataSearchRs.getString("comcode"));
					    scanPstmt.setString(2, DataSearchRs.getString("plant"));
					    scanPstmt.setString(3, DataSearchRs.getString("storcode")); // warehouse
					    scanPstmt.setString(4, DataSearchRs.getString("lotnum"));
					    ResultSet scanRs = scanPstmt.executeQuery();

					    String MVType = DataSearchRs.getString("movetype").substring(0, 2);
					    double quantity = DataSearchRs.getDouble("quantity");

					    if (scanRs.next()) {
					        // 2-2. 기존 데이터가 있으면 UPDATE
					        double GRQty = scanRs.getDouble("GrTransacQty");
					        double GIQty = scanRs.getDouble("GiTransacQty");

					        if (MVType.equals("GR")) {
					        	GRQty += quantity;
					        } else if (MVType.equals("GI")) {
					        	GIQty += quantity;
					        }
					        double TotalQty = GRQty - GIQty; // 15열 계산

					        String updateSql = "UPDATE sumtable SET GrTransacQty = ?, GiTransacQty = ?, EndStocQty = ? WHERE comcode = ? AND plant = ? AND warehouse = ? AND lotnum = ?";
					        PreparedStatement updatePstmt = conn.prepareStatement(updateSql);
					        updatePstmt.setDouble(1, GRQty);
					        updatePstmt.setDouble(2, GIQty);
					        updatePstmt.setDouble(3, TotalQty);
					        updatePstmt.setString(4, DataSearchRs.getString("comcode"));
					        updatePstmt.setString(5, DataSearchRs.getString("plant"));
					        updatePstmt.setString(6, DataSearchRs.getString("storcode"));
					        updatePstmt.setString(7, DataSearchRs.getString("lotnum"));
					        updatePstmt.executeUpdate();
					    } else {
					        // 2-3. 기존 데이터가 없으면 INSERT
					        String insertSql = "INSERT INTO sumtable VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					        PreparedStatement insertPstmt = conn.prepareStatement(insertSql);
					        insertPstmt.setString(1, DataSearchRs.getString("closingmon"));
						    insertPstmt.setString(2, DataSearchRs.getString("comcode"));
						    insertPstmt.setString(3, DataSearchRs.getString("plant"));
						    insertPstmt.setString(4, DataSearchRs.getString("storcode"));
						    insertPstmt.setString(5, DataSearchRs.getString("lotnum"));
						    insertPstmt.setString(6, DataSearchRs.getString("matcode"));
						    insertPstmt.setString(7, DataSearchRs.getString("matdesc"));
						    insertPstmt.setString(8, DataSearchRs.getString("mattype"));
						    insertPstmt.setString(9, DataSearchRs.getString("spec"));
						    insertPstmt.setString(10, "0");
						    
					        if (MVType.equals("GR")) {
					            insertPstmt.setDouble(11, quantity);
						        insertPstmt.setString(12, "0");
							    insertPstmt.setString(13, "0");
					            insertPstmt.setDouble(14, 0);
					            insertPstmt.setDouble(15, quantity);
					        } else {
					            insertPstmt.setDouble(11, 0);
						        insertPstmt.setString(12, "0");
							    insertPstmt.setString(13, "0");
					            insertPstmt.setDouble(14, quantity);
					            insertPstmt.setDouble(15, -quantity);
					        }
					        insertPstmt.executeUpdate();
					    }
					    scanRs.close();
					}
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	
}
