package StockDataLoading;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;

public class SavedDataLoadingDAO {
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
	
	public String StdCompanyLv(JSONObject jsonObj) {
		connDB();
		String[] keyOrder = {"FromDate", "EndDate", "MatData", "ComData"};
		String[] DataList = new String[keyOrder.length];
		String result = null;
		for (int i = 0; i < keyOrder.length; i++) {
	        DataList[i] = jsonObj.has(keyOrder[i]) ? jsonObj.get(keyOrder[i]).toString() : "";
	    }
	    ResultSet rs = null;
	    JSONArray jsonArray = new JSONArray();
	    try {
	    	String sql = 
	    		    "SELECT comcode, matcode, matdesc, spec, " +
	    		    "SUM(beginStocqty) AS beginStocqty_sum, " +
	    		    "SUM(GrTransacQty) AS GrTransacQty_sum, " +
	    		    "SUM(GrTransferQty) AS GrTransferQty_sum, " +
	    		    "SUM(GiTransferQty) AS GiTransferQty_sum, " +
	    		    "SUM(GiTransacQty) AS GiTransacQty_sum, " +
	    		    "SUM(EndStocQty) AS EndStocQty_sum " +
	    		    "FROM sumtable " +
	    		    "WHERE comcode = ?  AND closingMon >= ? AND closingMon <= ? " +
	    		    "GROUP BY comcode, matcode, matdesc, spec";
	    	pstmt = conn.prepareStatement(sql);
	    	pstmt.setString(1, DataList[3].trim());
	    	pstmt.setString(2, DataList[0].substring(0, 7).replace("-", "").trim());
	    	pstmt.setString(3, DataList[1].substring(0, 7).replace("-", "").trim());
	    	rs = pstmt.executeQuery();
	    	while(rs.next()) {
	    		JSONObject jsonObject = new JSONObject();
	    		jsonObject.put("comcode", rs.getString("comcode"));
		    	jsonObject.put("matcode", rs.getString("matcode"));
		    	jsonObject.put("matdesc", rs.getString("matdesc"));
		    	jsonObject.put("spec", rs.getString("spec"));
		    	jsonObject.put("beginStocqty_sum", String.format("%.3f", rs.getDouble("beginStocqty_sum")));
			    jsonObject.put("GrTransacQty_sum", String.format("%.3f", rs.getDouble("GrTransacQty_sum")));
			    jsonObject.put("GrTransferQty_sum", String.format("%.3f", rs.getDouble("GrTransferQty_sum")));
			    jsonObject.put("GiTransferQty_sum", String.format("%.3f", rs.getDouble("GiTransferQty_sum")));
			    jsonObject.put("GiTransacQty_sum", String.format("%.3f", rs.getDouble("GiTransacQty_sum")));
			    jsonObject.put("EndStocQty_sum", String.format("%.3f", rs.getDouble("EndStocQty_sum")));
			    jsonArray.put(jsonObject);
	    	}
		    if (jsonArray.length() == 0) {
		        result = null;
		    } else {
		        result = jsonArray.toString();
		    }
	    }catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}

	public String StdPlaWarLv(JSONObject jsonObj) {
		connDB();
		String[] keyOrder = {"value", "FromDate", "EndDate", "MatData", "ComData", "PlantData", "SLoData"};
		String[] DataList = new String[keyOrder.length];
		String result = null;
		for (int i = 0; i < keyOrder.length; i++) {
	        DataList[i] = jsonObj.has(keyOrder[i]) ? jsonObj.get(keyOrder[i]).toString() : "";
	    }
	    ResultSet rs = null;
	    JSONArray jsonArray = new JSONArray();
	    try {
	    	String sql = null;
	    	switch(DataList[0]) {
	    	case "2":
	    		sql = "SELECT comcode, plant, matcode, matdesc, spec, " +
	   	    		    "SUM(beginStocqty) AS beginStocqty_sum, " +
	   	    		    "SUM(GrTransacQty) AS GrTransacQty_sum, " +
	   	    		    "SUM(GrTransferQty) AS GrTransferQty_sum, " +
	   	    		    "SUM(GiTransferQty) AS GiTransferQty_sum, " +
	   	    		    "SUM(GiTransacQty) AS GiTransacQty_sum, " +
	   	    		    "SUM(EndStocQty) AS EndStocQty_sum " +
	   	    		    "FROM sumtable " +
	   	    		    "WHERE comcode = ? AND plant = ? AND closingMon >= ? AND closingMon <= ? " +
	   	    		    "GROUP BY comcode, plant, matcode, matdesc, spec";
	    		pstmt = conn.prepareStatement(sql);
		    	pstmt.setString(1, DataList[4].trim());
		    	pstmt.setString(2, DataList[5].trim());
		    	pstmt.setString(3, DataList[1].substring(0, 7).replace("-", "").trim());
		    	pstmt.setString(4, DataList[2].substring(0, 7).replace("-", "").trim());
		    	rs = pstmt.executeQuery();
		    	while(rs.next()) {
		    		JSONObject jsonObject = new JSONObject();
		    		jsonObject.put("comcode", rs.getString("comcode"));
		    		jsonObject.put("plant", rs.getString("plant"));
			    	jsonObject.put("matcode", rs.getString("matcode"));
			    	jsonObject.put("matdesc", rs.getString("matdesc"));
			    	jsonObject.put("spec", rs.getString("spec"));
			    	jsonObject.put("beginStocqty_sum", String.format("%.3f", rs.getDouble("beginStocqty_sum")));
				    jsonObject.put("GrTransacQty_sum", String.format("%.3f", rs.getDouble("GrTransacQty_sum")));
				    jsonObject.put("GrTransferQty_sum", String.format("%.3f", rs.getDouble("GrTransferQty_sum")));
				    jsonObject.put("GiTransferQty_sum", String.format("%.3f", rs.getDouble("GiTransferQty_sum")));
				    jsonObject.put("GiTransacQty_sum", String.format("%.3f", rs.getDouble("GiTransacQty_sum")));
				    jsonObject.put("EndStocQty_sum", String.format("%.3f", rs.getDouble("EndStocQty_sum")));
				    jsonArray.put(jsonObject);
		    	}
		    	break;
	    	case "3":
	    		   sql = "SELECT comcode, plant, warehouse, matcode, matdesc, spec, " +
	   	    		    "SUM(beginStocqty) AS beginStocqty_sum, " +
	   	    		    "SUM(GrTransacQty) AS GrTransacQty_sum, " +
	   	    		    "SUM(GrTransferQty) AS GrTransferQty_sum, " +
	   	    		    "SUM(GiTransferQty) AS GiTransferQty_sum, " +
	   	    		    "SUM(GiTransacQty) AS GiTransacQty_sum, " +
	   	    		    "SUM(EndStocQty) AS EndStocQty_sum " +
	   	    		    "FROM sumtable " +
	   	    		    "WHERE comcode = ? AND plant = ? AND warehouse = ? AND closingMon >= ? AND closingMon <= ? " +
	   	    		    "GROUP BY comcode, plant, warehouse, matcode, matdesc, spec";
	    		   pstmt = conn.prepareStatement(sql);
	    		   pstmt.setString(1, DataList[4].trim());
	    		   pstmt.setString(2, DataList[5].trim());
	    		   //pstmt.setString(3, DataList[6].trim());
	    		   pstmt.setString(3, "AP00-000");
	    		   pstmt.setString(4, DataList[1].substring(0, 7).replace("-", "").trim());
	    		   pstmt.setString(5, DataList[2].substring(0, 7).replace("-", "").trim());
	   	    	rs = pstmt.executeQuery();
		    	while(rs.next()) {
		    		JSONObject jsonObject = new JSONObject();
		    		jsonObject.put("comcode", rs.getString("comcode"));
		    		jsonObject.put("plant", rs.getString("plant"));
		    		jsonObject.put("warehouse", rs.getString("warehouse"));
			    	jsonObject.put("matcode", rs.getString("matcode"));
			    	jsonObject.put("matdesc", rs.getString("matdesc"));
			    	jsonObject.put("spec", rs.getString("spec"));
			    	jsonObject.put("beginStocqty_sum", String.format("%.3f", rs.getDouble("beginStocqty_sum")));
				    jsonObject.put("GrTransacQty_sum", String.format("%.3f", rs.getDouble("GrTransacQty_sum")));
				    jsonObject.put("GrTransferQty_sum", String.format("%.3f", rs.getDouble("GrTransferQty_sum")));
				    jsonObject.put("GiTransferQty_sum", String.format("%.3f", rs.getDouble("GiTransferQty_sum")));
				    jsonObject.put("GiTransacQty_sum", String.format("%.3f", rs.getDouble("GiTransacQty_sum")));
				    jsonObject.put("EndStocQty_sum", String.format("%.3f", rs.getDouble("EndStocQty_sum")));
				    jsonArray.put(jsonObject);
		    	}
	    		break;
	    	}
		    if (jsonArray.length() == 0) {
		        result = null;
		    } else {
		        result = jsonArray.toString();
		    }
	    }catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
