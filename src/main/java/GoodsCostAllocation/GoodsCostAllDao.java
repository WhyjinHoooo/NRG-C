package GoodsCostAllocation;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mysql.cj.x.protobuf.MysqlxPrepare.Prepare;

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
	    		int SumOfFMC = SelectRs.getInt("SumOfFMC");
	    		int SumOfFMfC = SelectRs.getInt("SumOfFMfC"); // 200310
	    		String WorkOrd = SelectRs.getString("WorkOrd");
	    		
	    		String LineGrSearchSql = "SELECT * FROM invenlogl_backup WHERE workordnum = ? AND LEFT(movetype, 2) = ?";
	    		PreparedStatement LineGrSearchPstmt = conn.prepareStatement(LineGrSearchSql);
	    		LineGrSearchPstmt.setString(1, WorkOrd);
	    		LineGrSearchPstmt.setString(2, "GR");
	    		ResultSet LineGrSearchRs = LineGrSearchPstmt.executeQuery();
	    		double SumOfAmount = 0.0;
	    		while(LineGrSearchRs.next()) {
	    			SumOfAmount += LineGrSearchRs.getDouble("quantity");
	    		}
	    		String CP_LineGrSearchSql = "SELECT * FROM invenlogl_backup WHERE workordnum = ? AND LEFT(movetype, 2) = ?";
	    		PreparedStatement CP_LineGrSearchPstmt = conn.prepareStatement(CP_LineGrSearchSql);
	    		CP_LineGrSearchPstmt.setString(1, WorkOrd);
	    		CP_LineGrSearchPstmt.setString(2, "GR");
	    		ResultSet CP_LineGrSearchRs = CP_LineGrSearchPstmt.executeQuery();
	    		while(CP_LineGrSearchRs.next()) {
	    			String keyValue = CP_LineGrSearchRs.getString("keyvalue");
	    			double Qty = CP_LineGrSearchRs.getDouble("quantity");
	    			int EachAmount = (int)Math.round(Qty * SumOfFMC / SumOfAmount);
	    			int EachAmtOhc = (int)Math.round(Qty * SumOfFMfC / SumOfAmount);
	    			
	    			String LineUpdateSql = "UPDATE invenlogl_backup SET amount = ?, amtOhC = ? WHERE keyvalue = ?";
	    			PreparedStatement LineUpPstmt = conn.prepareStatement(LineUpdateSql);
	    			LineUpPstmt.setInt(1, EachAmount);
	    			LineUpPstmt.setInt(2, EachAmtOhc);
	    			LineUpPstmt.setString(3, keyValue);
	    			LineUpPstmt.executeUpdate();
	    		}
	    		String LineGrSumChkSql = "SELECT SUM(amount) as SumAmount, sum(amtOhC) as SumAmtOhc FROM invenlogl_backup WHERE workordnum = ? AND LEFT(movetype, 2) = ?";
	    		PreparedStatement LineGrSumChkPstmt = conn.prepareStatement(LineGrSumChkSql);
	    		LineGrSumChkPstmt.setString(1, WorkOrd);
	    		LineGrSumChkPstmt.setString(2, "GR");
	    		ResultSet LineGrSumChkRs = LineGrSumChkPstmt.executeQuery();
	    		if(LineGrSumChkRs.next()) {
	    			int SumAmount = LineGrSumChkRs.getInt("SumAmount");
	    			int SumAmtOhc = LineGrSumChkRs.getInt("SumAmtOhc"); // invenlogl_backup에서 온 값 200312
	    			
	    			int SumAmtGap = SumOfFMC - SumAmount;
	    			int SumAmtOhcGap = SumOfFMfC - SumAmtOhc;
	    			
	    			String LineItemFindSql = "SELECT * FROM invenlogl_backup WHERE workordnum = ? AND LEFT(movetype, 2) = ?";
	    			PreparedStatement LineItemPstmt = conn.prepareStatement(LineItemFindSql);
	    			LineItemPstmt.setString(1, WorkOrd);
	    			LineItemPstmt.setString(2, "GR");
	    			ResultSet LineItemRs = LineItemPstmt.executeQuery();
	    			if(LineItemRs.next()) {
	    				String KeyValue = LineItemRs.getString("keyvalue");
	    				int ItemAmt = LineItemRs.getInt("amount");
	    				int ItemAmtOhC = LineItemRs.getInt("amtOhC");
	    				
	    				ItemAmt += SumAmtGap;
	    				ItemAmtOhC += SumAmtOhcGap;
	    				
	    				String LineItemUpSql = "UPDATE invenlogl_backup SET amount = ?, amtOhC = ? WHERE keyvalue = ?";
	    				PreparedStatement LineItemUpPstmt = conn.prepareStatement(LineItemUpSql);
	    				LineItemUpPstmt.setInt(1, ItemAmt);
	    				LineItemUpPstmt.setInt(2, ItemAmtOhC);
	    				LineItemUpPstmt.setString(3, KeyValue);
	    				LineItemUpPstmt.executeUpdate();
	    			}
	    		}
	    	}
	    	// 1 ~ 4번 과정
	    	System.out.println("끝");
    		String PrimCostingGrSql = "INSERT INTO productcost (closingmon, comcode, plant, matcode, matdesc, spec, matType, GR_Qty, GR_MatC, GR_ExpC, KeyVal) "
    				+ "SELECT closingmon, comcode, plant, matcode, matdesc, spec, mattype, "
    				+ "SUM(quantity) as S_Qua, SUM(amount) as S_Amt, SUM(amtOhC) as S_AmtOhC, "
    				+ "CONCAT(invenlogl_backup.closingmon, invenlogl_backup.matcode)"
    				+ "FROM invenlogl_backup WHERE LEFT(movetype, 2) = ? AND closingmon = ? GROUP BY matcode";
    		PreparedStatement PrimCostingGrPstmt = conn.prepareStatement(PrimCostingGrSql);
    		PrimCostingGrPstmt.setString(1, "GR");
    		PrimCostingGrPstmt.setString(2, DataList[2].trim());
    		PrimCostingGrPstmt.executeUpdate();
    		
    		String PastPresentSumSql_01 = "SELECT * FROM productcost WHERE closingmon = ?";
    		PreparedStatement PastPresentPstmt_01 = conn.prepareStatement(PastPresentSumSql_01);
    		String PastClosingDate = null;
    		int PresentClosingDate_Y = Integer.parseInt(DataList[2].trim().substring(0, 4));
    		int PresentClosingDate_M = Integer.parseInt(DataList[2].trim().substring(4));
    		if(PresentClosingDate_M == 1) {
    			PresentClosingDate_M = 12;
    			PresentClosingDate_Y -= 1;
    		}else {
    			PresentClosingDate_M -= 1;
    		}
			PastClosingDate = PresentClosingDate_Y + String.format("%02d", PresentClosingDate_M);
			PastPresentPstmt_01.setString(1, PastClosingDate);
			ResultSet PastRs = PastPresentPstmt_01.executeQuery();
			while(PastRs.next()) {
				String ItemCode = PastRs.getString("matcode");
				double P_ES_Qty = PastRs.getDouble("ES_Qty");
				int P_ES_MatC = PastRs.getInt("ES_MatC");
				int P_ES_LabC = PastRs.getInt("ES_LabC");
				int P_ES_ExpC = PastRs.getInt("ES_ExpC");
				String PastPresentSumSql_02 = "SELECT * FROM productcost WHERE closingmon = ? AND matcode = ?";
				PreparedStatement PastPresentPstmt_02 = conn.prepareStatement(PastPresentSumSql_02);
				PastPresentPstmt_02.setString(1, DataList[2].trim());
				PastPresentPstmt_02.setString(2, ItemCode);
				ResultSet PresentRs = PastPresentPstmt_02.executeQuery();
				String PresentUpSql = null;
				PreparedStatement PresentUpPstmt = null;
				if(PresentRs.next()) {
					PresentUpSql = "UPDATE productcost SET BS_Qty = ?, BS_MatC = ?, BS_LabC = ?, BS_ExpC = ? WHERE closingmon = ? AND matcode = ?";
					PresentUpPstmt = conn.prepareStatement(PresentUpSql);
					PresentUpPstmt.setDouble(1, P_ES_Qty);
					PresentUpPstmt.setInt(2, P_ES_MatC);
					PresentUpPstmt.setInt(3, P_ES_LabC);
					PresentUpPstmt.setInt(4, P_ES_ExpC);
					PresentUpPstmt.setString(5, DataList[2].trim());
					PresentUpPstmt.setString(6, ItemCode);
					PresentUpPstmt.executeUpdate();
				}else {
					PresentUpSql = "INSERT INTO productcost (closingmon, comcode, plant, matcode, matdesc, spec, matType, BS_Qty, BS_MatC, BS_LabC, BS_ExpC, "
							+ "ES_Qty, ES_MatC, ES_LabC, ES_ExpC, KeyVal) "
							+ "SELECT ?, comcode, plant, matcode, matdesc, spec, matType, ES_Qty, ES_MatC, ES_LabC, ES_ExpC, "
							+ "ES_Qty, ES_MatC, ES_LabC, ES_ExpC, CONCAT(?, matcode) "
							+ "FROM productcost WHERE closingmon = ? AND matcode = ?";
					PresentUpPstmt = conn.prepareStatement(PresentUpSql);
					PresentUpPstmt.setString(1, DataList[2].trim());
					PresentUpPstmt.setString(2, DataList[2].trim());
					PresentUpPstmt.setString(3, PastClosingDate);
					PresentUpPstmt.setString(4, ItemCode);
					PresentUpPstmt.executeUpdate();
				}
			}

    		String PrimCostingItemSearchSql = "SELECT * FROM productcost WHERE closingmon = ?";
    		PreparedStatement PrimCostingItemSearchPstmt = conn.prepareStatement(PrimCostingItemSearchSql);
    		PrimCostingItemSearchPstmt.setString(1, DataList[2].trim());
    		ResultSet PrimCostingItemSearchRs = PrimCostingItemSearchPstmt.executeQuery();
    		while(PrimCostingItemSearchRs.next()) {
    			String ItemCode = PrimCostingItemSearchRs.getString("matcode");
    			String KetValue = PrimCostingItemSearchRs.getString("KeyVal");

    			String LinePrimCosItemSearchSql = "SELECT closingmon, comcode, plant, matcode, matdesc, spec, mattype, "
    					+ "SUM(quantity) as S_Qua, SUM(amount) as S_Amt, SUM(amtOhC) as S_AmtOhC FROM invenlogl_backup "
    					+ "WHERE LEFT(movetype, 2) = ? AND matcode = ? GROUP BY matcode";
    			PreparedStatement LinePrimCosItemSearchPstmt = conn.prepareStatement(LinePrimCosItemSearchSql);
    			LinePrimCosItemSearchPstmt.setString(1, "GI");
    			LinePrimCosItemSearchPstmt.setString(2, ItemCode);
    			ResultSet LinePrimCosItemSearchRs = LinePrimCosItemSearchPstmt.executeQuery();
    			if(LinePrimCosItemSearchRs.next()) {
    				double S_Qua = LinePrimCosItemSearchRs.getDouble("S_Qua");
    				double S_Amt = LinePrimCosItemSearchRs.getDouble("S_Amt");
    				double S_AmtOhC = LinePrimCosItemSearchRs.getDouble("S_AmtOhC");
    				String PrimCostingGiUpSql = "UPDATE productcost SET Gi_Qty = ?, Gi_MatC = ?, Gi_ExpC = ? WHERE KeyVal = ?";
    				PreparedStatement PrimCostingGiUpPstmt = conn.prepareStatement(PrimCostingGiUpSql);
    				PrimCostingGiUpPstmt.setDouble(1, S_Qua);
    				PrimCostingGiUpPstmt.setDouble(2, S_Amt);
    				PrimCostingGiUpPstmt.setDouble(3, S_AmtOhC);
    				PrimCostingGiUpPstmt.setString(4, KetValue);
    				PrimCostingGiUpPstmt.executeUpdate();
    			}
    		}
    		String PrimCostingItemSearchSql_v2 = "SELECT * FROM productcost WHERE closingmon = ? AND matType != ?";
    		PreparedStatement PrimCostingItemSearchPstmt_v2 = conn.prepareStatement(PrimCostingItemSearchSql_v2);
    		PrimCostingItemSearchPstmt_v2.setString(1, DataList[2].trim());
    		PrimCostingItemSearchPstmt_v2.setString(2, "RAWM");
    		ResultSet PrimCostingItemSearchRs_v2 = PrimCostingItemSearchPstmt_v2.executeQuery();
    		while(PrimCostingItemSearchRs_v2.next()) {
    			String ItemCode = PrimCostingItemSearchRs_v2.getString("matcode");
    			String KetValue = PrimCostingItemSearchRs_v2.getString("KeyVal");
    			
    			double MatUnitP = 0.0;
    			double ExpUnitP = 0.0;
    			
    			double BS_Qty = PrimCostingItemSearchRs_v2.getInt("BS_Qty");
    			int BS_MatC = PrimCostingItemSearchRs_v2.getInt("BS_MatC");
    			int BS_ExpC = PrimCostingItemSearchRs_v2.getInt("BS_ExpC");

    			double GR_Qty = PrimCostingItemSearchRs_v2.getInt("GR_Qty");
    			int GR_MatC = PrimCostingItemSearchRs_v2.getInt("GR_MatC");
    			int GR_ExpC = PrimCostingItemSearchRs_v2.getInt("GR_ExpC");
    			
    			double Gi_Qty = PrimCostingItemSearchRs_v2.getInt("Gi_Qty");
    			int Gi_MatC = PrimCostingItemSearchRs_v2.getInt("Gi_MatC");	
    			int Gi_ExpC = PrimCostingItemSearchRs_v2.getInt("Gi_ExpC");

    			double ES_Qty = PrimCostingItemSearchRs_v2.getInt("ES_Qty");
    			int ES_MatC = PrimCostingItemSearchRs_v2.getInt("ES_MatC");
    			int ES_ExpC = PrimCostingItemSearchRs_v2.getInt("ES_ExpC");

    			MatUnitP = (BS_MatC + GR_MatC) / (BS_Qty + GR_Qty);
    			ExpUnitP  = (BS_ExpC + GR_ExpC) / (BS_Qty + GR_Qty);
    			
    			ES_Qty = BS_Qty + GR_Qty - Gi_Qty;
    			ES_MatC = (int)Math.round(ES_Qty * MatUnitP);
    			ES_ExpC = (int)Math.round(ES_Qty * ExpUnitP);
    			
    			Gi_MatC = BS_MatC + GR_MatC - ES_MatC;
    			Gi_ExpC = BS_ExpC + GR_ExpC - ES_ExpC;

    			String LineItemTotalUpdateSql = "UPDATE productcost SET Gi_MatC = ?, Gi_ExpC = ?, ES_Qty = ?, ES_MatC = ?, ES_ExpC = ? "
    					+ "WHERE KeyVal = ?";
    			PreparedStatement LineItemTotalUpdatePstmt = conn.prepareStatement(LineItemTotalUpdateSql);
    			LineItemTotalUpdatePstmt.setInt(1, Gi_MatC);
    			LineItemTotalUpdatePstmt.setInt(2, Gi_ExpC);
    			LineItemTotalUpdatePstmt.setDouble(3, ES_Qty);
    			LineItemTotalUpdatePstmt.setInt(4, ES_MatC);
    			LineItemTotalUpdatePstmt.setInt(5, ES_ExpC);
    			LineItemTotalUpdatePstmt.setString(6, KetValue);
    			LineItemTotalUpdatePstmt.executeUpdate();
    		}
    		System.out.println("끝끝");
    		String PrimCostingItemSearchSql_v3 = "SELECT * FROM productcost WHERE closingmon = ? AND matType != ?";
    		PreparedStatement PrimCostingItemSearchPstmt_v3 = conn.prepareStatement(PrimCostingItemSearchSql_v3);
    		PrimCostingItemSearchPstmt_v3.setString(1, DataList[2].trim());
    		PrimCostingItemSearchPstmt_v3.setString(2, "RAWM");
    		ResultSet PrimCostingItemSearchRs_v3 = PrimCostingItemSearchPstmt_v3.executeQuery();
    		while(PrimCostingItemSearchRs_v3.next()) {
    			String ItemCode = PrimCostingItemSearchRs_v3.getString("matcode");
    			int Gi_MatC = PrimCostingItemSearchRs_v3.getInt("Gi_MatC");
    			int Gi_ExpC = PrimCostingItemSearchRs_v3.getInt("Gi_ExpC");
    			
    			double BS_Qty = PrimCostingItemSearchRs_v3.getInt("BS_Qty");
    			int BS_MatC = PrimCostingItemSearchRs_v3.getInt("BS_MatC");
    			int BS_ExpC = PrimCostingItemSearchRs_v3.getInt("BS_ExpC");

    			double GR_Qty = PrimCostingItemSearchRs_v3.getInt("GR_Qty");
    			int GR_MatC = PrimCostingItemSearchRs_v3.getInt("GR_MatC");
    			int GR_ExpC = PrimCostingItemSearchRs_v3.getInt("GR_ExpC");
    			
    			double MatUnitP = 0.0;
    			double ExpUnitP = 0.0;
    			MatUnitP = (BS_MatC + GR_MatC) / (BS_Qty + GR_Qty);
    			ExpUnitP  = (BS_ExpC + GR_ExpC) / (BS_Qty + GR_Qty);

    			int SumOfAmtAboutGi = 0;
    			int SumOfAmtOhCAboutGi = 0;
    			
    			String LinePrimCosItemSearchSql = 
						"SELECT * FROM invenlogl_backup "
					+ 	"WHERE LEFT(movetype, 2) = ? AND matcode = ? AND closingmon = ? AND mattype != ?";
    			PreparedStatement LinePrimCosItemSearchPstmt = conn.prepareStatement(LinePrimCosItemSearchSql);
    			LinePrimCosItemSearchPstmt.setString(1, "GI");
    			LinePrimCosItemSearchPstmt.setString(2, ItemCode);
    			LinePrimCosItemSearchPstmt.setString(3, DataList[2].trim());
    			LinePrimCosItemSearchPstmt.setString(4, "RAWM");
    			ResultSet LinePrimCosItemSearchRs = LinePrimCosItemSearchPstmt.executeQuery();
    			while(LinePrimCosItemSearchRs.next()) {
    				String KeyVal = LinePrimCosItemSearchRs.getString("keyvalue"); 
    				double Qty = LinePrimCosItemSearchRs.getDouble("quantity");
    				int amt = (int)Math.round(Qty * MatUnitP);
    				int amtOhC = (int)Math.round(Qty * ExpUnitP);
    				String FFGDItemAmtUpSql = "UPDATE invenlogl_backup SET amount = ?, amtOhC = ? WHERE keyvalue = ?";
    				PreparedStatement FFGDItemAmtUpPstmt = conn.prepareStatement(FFGDItemAmtUpSql);
    				FFGDItemAmtUpPstmt.setBigDecimal(1, new BigDecimal(amt));
    				FFGDItemAmtUpPstmt.setBigDecimal(2, new BigDecimal(amtOhC));
    				FFGDItemAmtUpPstmt.setString(3, KeyVal);
    				FFGDItemAmtUpPstmt.executeUpdate();
    			}
    			
    			String LineGrSumChkSql = 
						"SELECT SUM(amount) as SumAmount, sum(amtOhC) as SumAmtOhc "
					+ 	"FROM invenlogl_backup WHERE "
					+ 	"LEFT(movetype, 2) = ? AND matcode = ? AND closingmon = ? AND mattype != ?";
	    		PreparedStatement LineGrSumChkPstmt = conn.prepareStatement(LineGrSumChkSql);
	    		LineGrSumChkPstmt.setString(1, "GI");
	    		LineGrSumChkPstmt.setString(2, ItemCode);
	    		LineGrSumChkPstmt.setString(3, DataList[2].trim());
	    		LineGrSumChkPstmt.setString(4, "RAWM");
	    		ResultSet LineGrSumChkRs = LineGrSumChkPstmt.executeQuery();
	    		if(LineGrSumChkRs.next()) {
	    			int SumAmount = LineGrSumChkRs.getInt("SumAmount");
	    			int SumAmtOhc = LineGrSumChkRs.getInt("SumAmtOhc");
	    			
	    			int SumAmtGap = SumAmount - Gi_MatC; 
	    			int SumAmtOhcGap = SumAmtOhc - Gi_ExpC;
	    			
	    			String LineItemFindSql = "SELECT * FROM invenlogl_backup WHERE LEFT(movetype, 2) = ? AND matcode = ? AND closingmon = ? AND mattype != ?";
	    			PreparedStatement LineItemPstmt = conn.prepareStatement(LineItemFindSql);
	    			LineItemPstmt.setString(1, "GI");
	    			LineItemPstmt.setString(2, ItemCode);
	    			LineItemPstmt.setString(3, DataList[2].trim());
	    			LineItemPstmt.setString(4, "RAWM");
	    			ResultSet LineItemRs = LineItemPstmt.executeQuery();
	    			if(LineItemRs.next()) {
	    				String KeyValue = LineItemRs.getString("keyvalue");
	    				int ItemAmt = LineItemRs.getInt("amount");
	    				int ItemAmtOhC = LineItemRs.getInt("amtOhC");
	    				
	    				String MatType = LineItemRs.getString("mattype");
	    				String WorkOrdNum = LineItemRs.getString("workordnum");
	    				String MatProcess = LineItemRs.getString("process");
	    				
	    				ItemAmt -= SumAmtGap;
	    				ItemAmtOhC -= SumAmtOhcGap; 
	    				
	    				String LineItemUpSql = "UPDATE invenlogl_backup SET amount = ?, amtOhC = ? WHERE keyvalue = ?";
	    				PreparedStatement LineItemUpPstmt = conn.prepareStatement(LineItemUpSql);
	    				LineItemUpPstmt.setBigDecimal(1, new BigDecimal(ItemAmt));
	    				LineItemUpPstmt.setBigDecimal(2, new BigDecimal(ItemAmtOhC));
	    				LineItemUpPstmt.setString(3, KeyValue);
	    				LineItemUpPstmt.executeUpdate();
	    			}
	    		}
    		}
    		
    		String PrimCostingItemSearchSql_v4 = "SELECT * FROM productcost WHERE closingmon = ? AND matType NOT IN (?, ?)";
    		PreparedStatement PrimCostingItemSearchPstmt_v4 = conn.prepareStatement(PrimCostingItemSearchSql_v4);
    		PrimCostingItemSearchPstmt_v4.setString(1, DataList[2].trim());
    		PrimCostingItemSearchPstmt_v4.setString(2, "RAWM");
    		PrimCostingItemSearchPstmt_v4.setString(3, "TMGD");
    		ResultSet PrimCostingItemSearchRs_v4 = PrimCostingItemSearchPstmt_v4.executeQuery();
    		while(PrimCostingItemSearchRs_v4.next()){
    			String MatCode = PrimCostingItemSearchRs_v4.getString("matcode");
    			String GetItemsSql = "SELECT workordnum, process, SUM(amount) AS sum_amount, SUM(amtOhC) AS sum_amtOhC "
    					+ " FROM invenlogl_backup WHERE LEFT(movetype, 2) = ? AND matcode = ? AND closingmon = ? AND mattype NOT IN (?, ?) GROUP BY workordnum ORDER BY workordnum";
    			PreparedStatement GetItemsPstmt = conn.prepareStatement(GetItemsSql);
    			GetItemsPstmt.setString(1, "GI");
    			GetItemsPstmt.setString(2, MatCode);
    			GetItemsPstmt.setString(3, DataList[2].trim());
    			GetItemsPstmt.setString(4, "RAWM");
    			GetItemsPstmt.setString(5, "TMGD");
    			ResultSet GetItemsRs = GetItemsPstmt.executeQuery();
    			while(GetItemsRs.next()){
    				String ProCode = GetItemsRs.getString("process");
    				String WorkOrder = GetItemsRs.getString("workordnum");
    				int ItemAmt = GetItemsRs.getInt("sum_amount");
    				int ItemAmtOhC = GetItemsRs.getInt("sum_amtOhC");
    				
    				String Process_Cost_Table_Renew_Sql = "SELECT * FROM processcosttable_copy WHERE ProcessCode = ? AND WorkOrd = ?";
    				PreparedStatement Process_Cost_Table_Renew_Pstmt = conn.prepareStatement(Process_Cost_Table_Renew_Sql);
    				Process_Cost_Table_Renew_Pstmt.setString(1, ProCode);
    				Process_Cost_Table_Renew_Pstmt.setString(2, WorkOrder);
    				ResultSet Process_Cost_Table_Renew_Rs = Process_Cost_Table_Renew_Pstmt.executeQuery();
    				if(Process_Cost_Table_Renew_Rs.next()) {
    					double RawMatCost = Process_Cost_Table_Renew_Rs.getDouble("RawMatCost");
    					double OthMatCost = Process_Cost_Table_Renew_Rs.getDouble("OthMatCost"); 
    			
    					double ManufCost = Process_Cost_Table_Renew_Rs.getDouble("ManufCost");
    					
    					double MatCostSum = 0;
    					double ManufCostSum = 0;
    					
    					MatCostSum = RawMatCost + ItemAmt + OthMatCost;
    					ManufCostSum = ManufCost + ItemAmtOhC;
    					
    					String Process_Cost_Table_Update_Sql = "UPDATE processcosttable_copy SET HalbMatCost = ?, MatCostSum = ?, HalbManufCost = ?, ManufCostSum = ?, "
    							+ "FertMatCost = ?, FertManufCost = ? WHERE WorkOrd = ? AND ProcessCode = ?";
    					PreparedStatement Process_Cost_Table_Update_Pstmt = conn.prepareStatement(Process_Cost_Table_Update_Sql);
    					Process_Cost_Table_Update_Pstmt.setDouble(1, ItemAmt);
    					Process_Cost_Table_Update_Pstmt.setDouble(2, MatCostSum);
    					Process_Cost_Table_Update_Pstmt.setDouble(3, ItemAmtOhC);
    					Process_Cost_Table_Update_Pstmt.setDouble(4, ManufCostSum);
    					Process_Cost_Table_Update_Pstmt.setDouble(5, MatCostSum);
    					Process_Cost_Table_Update_Pstmt.setDouble(6, ManufCostSum);
    					Process_Cost_Table_Update_Pstmt.setString(7, WorkOrder);
    					Process_Cost_Table_Update_Pstmt.setString(8, ProCode);
    					Process_Cost_Table_Update_Pstmt.executeUpdate();
    				}
    				String HalbSql = "SELECT SUM(MatCostSum) as MatCostSum, SUM(ManufCostSum) as ManufCostSum FROM processcosttable_copy WHERE WorkOrd = ?";
    				PreparedStatement HalbPstmt = conn.prepareStatement(HalbSql);
    				HalbPstmt.setString(1, WorkOrder);
    				ResultSet HalbRs = HalbPstmt.executeQuery();
    				if(HalbRs.next()) {
    					double HalbMatAddCost = HalbRs.getDouble("MatCostSum");
    					double HalbManufAddCost = HalbRs.getDouble("ManufCostSum");
    					String LineLv2PriceSearchSql = "SELECT COUNT(*) as ItemCount, SUM(quantity) as QtySum FROM invenlogl_backup WHERE workordnum = ? AND LEFT(movetype, 2) = ? AND mattype NOT IN ('RAWM', 'TMGD')";
    					PreparedStatement Lv2PricePstmt = conn.prepareStatement(LineLv2PriceSearchSql);
    					Lv2PricePstmt.setString(1, WorkOrder);
    					Lv2PricePstmt.setString(2, "GR");
    					ResultSet Lv2Rs = Lv2PricePstmt.executeQuery();
    					if(Lv2Rs.next()) {
    						int ItemCount = Lv2Rs.getInt("ItemCount");
    						int QtySum = (int)Math.round(Lv2Rs.getDouble("QtySum"));
    						String LineLv2PriceEditSql = null;
    						PreparedStatement LineLv2PriceEditPstmt = null; 
    						if(ItemCount > 0) {
    							switch(ItemCount) {
    							case 1:
//    	    			이 과정에서 RAWM , TMFD는 대상이 아니다.
    								System.out.println("1개-WorkOrdNum : " + WorkOrder);
    								LineLv2PriceEditSql = "UPDATE invenlogl_backup SET amount = ?, amtOhC = ? WHERE workordnum = ? AND LEFT(movetype, 2) = ? AND mattype NOT IN ('RAWM', 'TMGD')";
    								LineLv2PriceEditPstmt = conn.prepareStatement(LineLv2PriceEditSql);
    								LineLv2PriceEditPstmt.setDouble(1, HalbMatAddCost);
    								LineLv2PriceEditPstmt.setDouble(2, HalbManufAddCost);
    								LineLv2PriceEditPstmt.setString(3, WorkOrder);
    								LineLv2PriceEditPstmt.setString(4, "GR");
    								LineLv2PriceEditPstmt.executeUpdate();
    								break;
    							default:
    								System.out.println("1개이상-WorkOrdNum : " + WorkOrder);
    								String QtySql = "SELECT * FROM invenlogl_backup WHERE workordnum = ? AND LEFT(movetype, 2) = ? AND mattype NOT IN ('RAWM', 'TMGD')";
    								PreparedStatement QtyPstmt = conn.prepareStatement(QtySql);
    								QtyPstmt.setString(1, WorkOrder);
    								QtyPstmt.setString(2, "GR");
    								ResultSet QtyRs = QtyPstmt.executeQuery();
    								while(QtyRs.next()) {
    									int Qty = (int)Math.round(QtyRs.getDouble("quantity"));
    									String KeyData = QtyRs.getString("keyvalue");
    									
    									LineLv2PriceEditSql = "UPDATE invenlogl_backup SET amount = ?, amtOhC = ? WHERE workordnum = ? AND LEFT(movetype, 2) = ? AND keyvalue = ? AND mattype NOT IN ('RAWM', 'TMGD')";
    									LineLv2PriceEditPstmt = conn.prepareStatement(LineLv2PriceEditSql);
    	    							LineLv2PriceEditPstmt.setDouble(1, Math.round(HalbMatAddCost * Qty / QtySum));
    	    							LineLv2PriceEditPstmt.setDouble(2, Math.round(HalbManufAddCost * Qty / QtySum));
    	    							LineLv2PriceEditPstmt.setString(3, WorkOrder);
    	    							LineLv2PriceEditPstmt.setString(4, "GR");
    	    							LineLv2PriceEditPstmt.setString(5, KeyData);
    	    							LineLv2PriceEditPstmt.executeUpdate();
    								}
    								String ForModySql = "SELECT SUM(amount) as SumAmount, SUM(amtOhC) as SumAmtOhc FROM invenlogl_backup WHERE workordnum = ? AND LEFT(movetype, 2) = 'GR' AND mattype NOT IN ('RAWM', 'TMGD')";
    								PreparedStatement ForModyPstmt = conn.prepareStatement(ForModySql);
    								ForModyPstmt.setString(1, WorkOrder);
    								ResultSet ForModyRs = ForModyPstmt.executeQuery();
    								if(ForModyRs.next()) {
    									int SumAmount = ForModyRs.getInt("SumAmount");
    									int SumAmtOhc = ForModyRs.getInt("SumAmtOhc");
    									
    									int AmtGap = 0;
    									int OhcGap = 0;
    									
    									AmtGap = (int) (SumAmount - HalbMatAddCost);
    									OhcGap = (int) (SumAmtOhc - HalbManufAddCost);
    									
    									String SearModiSql = "SELECT * FROM invenlogl_backup WHERE workordnum = ? AND LEFT(movetype, 2) = 'GR' AND mattype NOT IN ('RAWM', 'TMGD')";
    									PreparedStatement SearModiPstmt = conn.prepareStatement(SearModiSql);
    									SearModiPstmt.setString(1, WorkOrder);
    									ResultSet SearModiRs = SearModiPstmt.executeQuery();
    									if(SearModiRs.next()) {
    										int Amt = SearModiRs.getInt("amount");
    										int AmtOhc = SearModiRs.getInt("amtOhC");
    										String KeyValue = SearModiRs.getString("keyvalue");
    										
    										String ModiSql = "UPDATE invenlogl_backup SET amount = ?, amtOhC = ? WHERE keyvalue = ?";
    										PreparedStatement ModiPstmt = conn.prepareStatement(ModiSql);
    										ModiPstmt.setInt(1, Amt - AmtGap);
    										ModiPstmt.setInt(2, AmtOhc - OhcGap);
    										ModiPstmt.setString(3, KeyValue);
    										ModiPstmt.executeUpdate();
    									}
    								}
    								break;
    							}
    						}
    					}
    				}
    			}
    		}
//			String Process_Cost_Table_Renew_Sql = "SELECT * FROM processcosttable_copy WHERE ProcessCode = ? AND WorkOrd = ?";
//			PreparedStatement Process_Cost_Table_Renew_Pstmt = conn.prepareStatement(Process_Cost_Table_Renew_Sql);
//			Process_Cost_Table_Renew_Pstmt.setString(1, MatProcess);
//			Process_Cost_Table_Renew_Pstmt.setString(2, WorkOrdNum);
//			ResultSet Process_Cost_Table_Renew_Rs = Process_Cost_Table_Renew_Pstmt.executeQuery();
//			if(Process_Cost_Table_Renew_Rs.next()) {
//				double RawMatCost = Process_Cost_Table_Renew_Rs.getDouble("RawMatCost");
//				double OthMatCost = Process_Cost_Table_Renew_Rs.getDouble("OthMatCost"); 
		
//				double ManufCost = Process_Cost_Table_Renew_Rs.getDouble("ManufCost");
//				
//				double MatCostSum = 0;
//				double ManufCostSum = 0;
//				
//				MatCostSum = RawMatCost + ItemAmt + OthMatCost;
//				ManufCostSum = ManufCost + ItemAmtOhC;
//				
//				String Process_Cost_Table_Update_Sql = "UPDATE processcosttable_copy SET HalbMatCost = ?, MatCostSum = ?, HalbManufCost = ?, ManufCostSum = ?, "
//						+ "FertMatCost = ?, FertManufCost = ? WHERE WorkOrd = ? AND ProcessCode = ?";
//				PreparedStatement Process_Cost_Table_Update_Pstmt = conn.prepareStatement(Process_Cost_Table_Update_Sql);
//				Process_Cost_Table_Update_Pstmt.setDouble(1, ItemAmt);
//				Process_Cost_Table_Update_Pstmt.setDouble(2, MatCostSum);
//				Process_Cost_Table_Update_Pstmt.setDouble(3, ItemAmtOhC);
//				Process_Cost_Table_Update_Pstmt.setDouble(4, ManufCostSum);
//				Process_Cost_Table_Update_Pstmt.setDouble(5, MatCostSum);
//				Process_Cost_Table_Update_Pstmt.setDouble(6, ManufCostSum);
//				Process_Cost_Table_Update_Pstmt.setString(7, WorkOrdNum);
//				Process_Cost_Table_Update_Pstmt.setString(8, MatProcess);
//				Process_Cost_Table_Update_Pstmt.executeUpdate();
//			}
//			String HalbSql = "SELECT SUM(MatCostSum) as MatCostSum, SUM(ManufCostSum) as ManufCostSum FROM processcosttable_copy WHERE WorkOrd = ?";
//			PreparedStatement HalbPstmt = conn.prepareStatement(HalbSql);
//			HalbPstmt.setString(1, WorkOrdNum);
//			ResultSet HalbRs = HalbPstmt.executeQuery();
//			if(HalbRs.next()) {
//				double HalbMatAddCost = HalbRs.getDouble("MatCostSum");
//				double HalbManufAddCost = HalbRs.getDouble("ManufCostSum");
//				String LineLv2PriceSearchSql = "SELECT COUNT(*) as ItemCount, SUM(quantity) as QtySum FROM invenlogl_backup WHERE workordnum = ? AND LEFT(movetype, 2) = ?";
//				PreparedStatement Lv2PricePstmt = conn.prepareStatement(LineLv2PriceSearchSql);
//				Lv2PricePstmt.setString(1, WorkOrdNum);
//				Lv2PricePstmt.setString(2, "GR");
//				ResultSet Lv2Rs = Lv2PricePstmt.executeQuery();
//				if(Lv2Rs.next()) {
//					int ItemCount = Lv2Rs.getInt("ItemCount");
//					int QtySum = (int)Math.round(Lv2Rs.getDouble("QtySum"));
//					String LineLv2PriceEditSql = null;
//					PreparedStatement LineLv2PriceEditPstmt = null; 
//					if(ItemCount > 0) {
//						switch(ItemCount) {
//						case 1:
//    			이 과정에서 RAWM , TMFD는 대상이 아니다.
//							System.out.println("1개-WorkOrdNum : " + WorkOrdNum);
//							LineLv2PriceEditSql = "UPDATE invenlogl_backup SET amount = ?, amtOhC = ? WHERE workordnum = ? AND LEFT(movetype, 2) = ?";
//							LineLv2PriceEditPstmt = conn.prepareStatement(LineLv2PriceEditSql);
//							LineLv2PriceEditPstmt.setDouble(1, HalbMatAddCost);
//							LineLv2PriceEditPstmt.setDouble(2, HalbManufAddCost);
//							LineLv2PriceEditPstmt.setString(3, WorkOrdNum);
//							LineLv2PriceEditPstmt.setString(4, "GR");
//							LineLv2PriceEditPstmt.executeUpdate();
//							break;
//						default:
//							System.out.println("1개이상-WorkOrdNum : " + WorkOrdNum);
//							String QtySql = "SELECT * FROM invenlogl_backup WHERE workordnum = ? AND LEFT(movetype, 2) = ?";
//							PreparedStatement QtyPstmt = conn.prepareStatement(QtySql);
//							QtyPstmt.setString(1, WorkOrdNum);
//							QtyPstmt.setString(2, "GR");
//							ResultSet QtyRs = QtyPstmt.executeQuery();
//							while(QtyRs.next()) {
//								int Qty = (int)Math.round(QtyRs.getDouble("quantity"));
//								String KeyData = QtyRs.getString("keyvalue");
//								
//								LineLv2PriceEditSql = "UPDATE invenlogl_backup SET amount = ?, amtOhC = ? WHERE workordnum = ? AND LEFT(movetype, 2) = ? AND keyvalue = ?";
//								LineLv2PriceEditPstmt = conn.prepareStatement(LineLv2PriceEditSql);
//    							LineLv2PriceEditPstmt.setDouble(1, Math.round(HalbMatAddCost * Qty / QtySum));
//    							LineLv2PriceEditPstmt.setDouble(2, Math.round(HalbManufAddCost * Qty / QtySum));
//    							LineLv2PriceEditPstmt.setString(3, WorkOrdNum);
//    							LineLv2PriceEditPstmt.setString(4, "GR");
//    							LineLv2PriceEditPstmt.setString(5, KeyData);
//    							LineLv2PriceEditPstmt.executeUpdate();
//							}
//							break;
//						}
//					}
//				}
//			}	
    		System.out.println("끝끝끝");
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return result;
	}
	
	
}
