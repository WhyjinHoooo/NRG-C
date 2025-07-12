package GoodsCostAllocation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;

public class GoodsCostAllDao {
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
	public String DataLoading(JSONObject jsonObj) {
		// TODO Auto-generated method stub
		connDB();
		String[] keyOrder = {"ComCode", "PlantCode", "CalcMonth"};
		String[] DataList = new String[keyOrder.length];
		String result = null;
		for (int i = 0; i < keyOrder.length; i++) {
	        DataList[i] = jsonObj.has(keyOrder[i]) ? jsonObj.get(keyOrder[i]).toString() : "";
	        System.out.println(DataList[i]);
	    }
		ResultSet rs = null;
	    JSONArray jsonArray = new JSONArray();
	    try {
	    	String SelectSql = "SELECT ClosingMon, ComCode, PlantCode, WorkType, CostingLev, WorkOrd, "
	    			+ "SUM(WipMatCost) as SumOfWMC, SUM(WipMnaufCost) as SumOfWMfC, SUM(FertMatCost) as SumOfFMC, SUM(FertManufCost) as SumOfFMfC "
	    			+ "FROM processcosttable WHERE ComCode = ? AND PlantCode = ? AND ClosingMon = ? GROUP BY WorkOrd ORDER BY CostingLev ASC";
	    	pstmt = conn.prepareStatement(SelectSql);
	    	pstmt.setString(1, DataList[0].trim());
	    	pstmt.setString(2, DataList[1].trim());
	    	pstmt.setString(3, DataList[2].trim());
	    	rs = pstmt.executeQuery();
	    	while(rs.next()) {
	    		JSONObject jsonObject = new JSONObject();
	    		jsonObject.put("ClosingMon", rs.getString("ClosingMon"));
		    	jsonObject.put("ComCode", rs.getString("ComCode"));
		    	jsonObject.put("PlantCode", rs.getString("PlantCode"));
		    	jsonObject.put("WorkType", rs.getString("WorkType"));
		    	jsonObject.put("CostingLev", rs.getString("CostingLev"));
		    	jsonObject.put("WorkOrd", rs.getDouble("WorkOrd"));
		    	jsonObject.put("SumOfWMC", rs.getString("SumOfWMC"));
		    	jsonObject.put("SumOfWMfC", rs.getString("SumOfWMfC"));
		    	jsonObject.put("SumOfFMC", rs.getString("SumOfFMC"));
		    	jsonObject.put("SumOfFMfC", rs.getString("SumOfFMfC"));
			    jsonArray.put(jsonObject);
	    	}
		    if (jsonArray.length() == 0) {
		        result = null;
		    } else {
		        result = jsonArray.toString();
		    }
	    }catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}
	public String GoodsCostCalc(JSONObject jsonObj) {
		// TODO Auto-generated method stub
		connDB();
		String[] keyOrder = {"ComCode", "PlantCode", "CalcMonth"};
		String[] DataList = new String[keyOrder.length];
		String result = null;
		for (int i = 0; i < keyOrder.length; i++) {
	        DataList[i] = jsonObj.has(keyOrder[i]) ? jsonObj.get(keyOrder[i]).toString() : "";
	        System.out.println(DataList[i]);
	    }
		ResultSet rs = null;
	    JSONArray jsonArray = new JSONArray();
		try {
			String SelectSql = "SELECT ClosingMon, ComCode, PlantCode, WorkType, CostingLev, WorkOrd, "
	    			+ "SUM(WipMatCost) as SumOfWMC, SUM(WipMnaufCost) as SumOfWMfC, SUM(FertMatCost) as SumOfFMC, SUM(FertManufCost) as SumOfFMfC "
	    			+ "FROM processcosttable WHERE ComCode = ? AND PlantCode = ? AND ClosingMon = ? GROUP BY WorkOrd ORDER BY CostingLev ASC";
	    	pstmt = conn.prepareStatement(SelectSql);
	    	pstmt.setString(1, DataList[0].trim());
	    	pstmt.setString(2, DataList[1].trim());
	    	pstmt.setString(3, DataList[2].trim());
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return result;
	}
	
	
}
