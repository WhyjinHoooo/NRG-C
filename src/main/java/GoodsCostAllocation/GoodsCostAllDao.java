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
	    			+ "SUM(FertMatCost) as SumOfFMC, SUM(FertManufCost) as SumOfFMfC "
	    			+ "FROM processcosttable WHERE ComCode = ? AND PlantCode = ? AND ClosingMon = ? GROUP BY WorkOrd ORDER BY CostingLev ASC";
	    	pstmt = conn.prepareStatement(SelectSql);
	    	pstmt.setString(1, DataList[0].trim());
	    	pstmt.setString(2, DataList[1].trim());
	    	pstmt.setString(3, DataList[2].trim());
	    	ResultSet SelectRs = pstmt.executeQuery();
	    	while(SelectRs.next()) {
	    		double SumOfFMC = SelectRs.getDouble("SumOfFMC");
	    		double SumOfFMfC = SelectRs.getDouble("SumOfFMfC");
	    		String WorkOrd = SelectRs.getString("WorkOrd");
	    		
	    		String LineGrSearchSql = "SELECT * FROM InvenLogl WHERE workordnum = ? AND LEFT(movetype, 2) = ?";
	    		PreparedStatement LineGrSearchPstmt = conn.prepareStatement(LineGrSearchSql);
	    		LineGrSearchPstmt.setString(1, WorkOrd);
	    		LineGrSearchPstmt.setString(2, "GR");
	    		ResultSet LineGrSearchRs = LineGrSearchPstmt.executeQuery();
	    		double SumOfAmount = 0.0;
	    		while(LineGrSearchRs.next()) {
	    			SumOfAmount += LineGrSearchRs.getDouble("quantity");
	    		}
	    		String CP_LineGrSearchSql = "SELECT * FROM InvenLogl WHERE workordnum = ? AND LEFT(movetype, 2) = ?";
	    		PreparedStatement CP_LineGrSearchPstmt = conn.prepareStatement(CP_LineGrSearchSql);
	    		CP_LineGrSearchPstmt.setString(1, WorkOrd);
	    		CP_LineGrSearchPstmt.setString(2, "GR");
	    		ResultSet CP_LineGrSearchRs = CP_LineGrSearchPstmt.executeQuery();
	    		while(CP_LineGrSearchRs.next()) {
	    			String keyValue = CP_LineGrSearchRs.getString("keyvalue");
	    			double Qty = CP_LineGrSearchRs.getDouble("quantity");
	    			int EachAmount = (int)Math.round(Qty * SumOfFMC / SumOfAmount);
	    			int EachAmtOhc = (int)Math.round(Qty * SumOfFMfC / SumOfAmount);
	    			
	    			String LineUpdateSql = "UPDATE InvenLogl SET amount = ?, amtOhC = ? WHERE keyvalue = ?";
	    			PreparedStatement LineUpPstmt = conn.prepareStatement(LineUpdateSql);
	    			LineUpPstmt.setInt(1, EachAmount);
	    			LineUpPstmt.setInt(2, EachAmtOhc);
	    			LineUpPstmt.setString(3, keyValue);
	    			LineUpPstmt.executeUpdate();
	    		}
	    		// 1. InvenLogl에서 업데이트한 amout와 amtOhC의 합을 SumOfFMC랑 SumOfFMfC의 값을 비교
	    		// 2. 비교 후 틀리면 조치 아니면 나둠
	    		// 3. 조치 후, CostingAmtTab(신규 생성 예정)에 회사 레벨에 해당하는 sumtable의 값들을 CostingAmtTab의 기초수량, 입고수량, 출고수량, 기말수량에 INSERT
	    		// 4. InvenLogl에서 재료의 입고재료비, 입고경비를 입력한 후, 재료비랑 경비의 단가를 계산한 후, 기말재료비 계산
	    		// 5. 계산된 기말재료비, 입고재료비, 기초재료비를 통해 출고재료비 계산
	    		// 6. 해당 재료의 재료비단가와 경비단가를 입고에 해당한는 수량의 amount와 amtohC에 곱한 후 합을 CostingAmtTab에서 계산한 값과 비교
	    		// 7. 비교 후 틀리면 조치 아니면 나둠
	    	}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return result;
	}
	
	
}
