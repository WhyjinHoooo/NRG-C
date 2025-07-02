package AcCalcFunction;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;

public class AcCalcDAO {
	private Connection conn;
	private PreparedStatement pstmt;
	private DataSource dataFactory;
	public String InsertSql;
	public String SelectSql;
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
	
	public String CostAllocation(JSONObject jsonObj) {
		connDB();
		
		String[] keyOrder = {"AIDMonth", "OP10", "OP20", "OP30", "OP40", "OP50", "ComCode"};
		String[] DataList = new String[keyOrder.length];
		String result = null;
		for (int i = 0; i < keyOrder.length; i++) {
	        DataList[i] = jsonObj.has(keyOrder[i]) ? jsonObj.get(keyOrder[i]).toString() : "";
	        System.out.println(DataList[i]);
	    }
		InsertSql = "INSERT INTO actable VALUES(?,?,?,?,?,?,?)";
		try {
			pstmt = conn.prepareStatement(InsertSql);
			pstmt.setString(1, DataList[0].trim());
			pstmt.setString(2, DataList[1].trim().replace(",", ""));
			pstmt.setString(3, DataList[2].trim().replace(",", ""));
			pstmt.setString(4, DataList[3].trim().replace(",", ""));
			pstmt.setString(5, DataList[4].trim().replace(",", ""));
			pstmt.setString(6, DataList[5].trim().replace(",", ""));
			pstmt.setString(7, DataList[6].trim());
			pstmt.executeUpdate();
			
			String CalcResult = FirstProcess(DataList[0].trim(), DataList[1].trim(), DataList[2].trim(), DataList[3].trim(), DataList[4].trim());
			if(CalcResult.equals("Yes")) {
				result = "Success";
			}else {
				result = "Fail";
			}
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return "Success";
	}

	public String CostAlloDataLoading(JSONObject jsonObj) {
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
	    	SelectSql = "SELECT * FROM actable WHERE ComCode = ? AND ClosingDate = ?";
	    	pstmt = conn.prepareStatement(SelectSql);
	    	pstmt.setString(1, DataList[0].trim());
	    	pstmt.setString(2, DataList[2].trim());
	    	rs = pstmt.executeQuery();
	    	while(rs.next()) {
	    		JSONObject jsonObject = new JSONObject();
	    		jsonObject.put("ClosingDate", rs.getString("ClosingDate"));
		    	jsonObject.put("OP10", rs.getString("OP10"));
		    	jsonObject.put("OP20", rs.getString("OP20"));
		    	jsonObject.put("OP30", rs.getString("OP30"));
		    	jsonObject.put("OP40", rs.getString("OP40"));
		    	jsonObject.put("OP50", rs.getDouble("OP50"));
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
	
	public String FirstProcess(String ClosingMonth, String MC, String IC, String PC, String CMC) {
		// MC : 교반 가공비, IC : 검사 가공비, PC : 소분 가공비, CMC : 공통 재료비
		String ClosingMon = ClosingMonth;
		int OP10 = Integer.parseInt(MC.replace(",", ""));
		int OP20 = Integer.parseInt(IC.replace(",", ""));
		int OP30 = Integer.parseInt(PC.replace(",", ""));
		int OP40 = Integer.parseInt(CMC.replace(",", ""));
		int RawMatCost = 0;
		String OK = null;
		SelectSql = "SELECT * FROM processcosttable WHERE ClosingMon = ? AND CostingLev = ? AND ProcessCode = ? AND InOutType = ?";
		try {
			PreparedStatement SelectPstmt = conn.prepareStatement(SelectSql);
			SelectPstmt.setString(1, ClosingMon);
			SelectPstmt.setString(2, "1");
			SelectPstmt.setString(3, "OP10");
			SelectPstmt.setString(4, "OC");
			ResultSet SelectRs = SelectPstmt.executeQuery();
			while(SelectRs.next()) {
				String ManufLot =  SelectRs.getString("WorkOrd");
				String LineSearchSql = "SELECT * FROM InvenLogl WHERE workordnum = ? AND process = ? AND mattype = ?";
				PreparedStatement LineSearchPstmt = conn.prepareStatement(LineSearchSql);
				LineSearchPstmt.setString(1, ManufLot);
				LineSearchPstmt.setString(2, "OP10");
				LineSearchPstmt.setString(3, "RAWM");
				ResultSet LineSearchRs = LineSearchPstmt.executeQuery();
				RawMatCost = 0;
				while(LineSearchRs.next()) {
					RawMatCost += LineSearchRs.getInt("amount");
				}
				String RawMatCoUpdateSql = "UPDATE processcosttable SET RawMatCost = ? WHERE WorkOrd = ? AND ProcessCode = ? AND InOutType = ?";
				PreparedStatement RawMatCoUpdatePstmt = conn.prepareStatement(RawMatCoUpdateSql);
				RawMatCoUpdatePstmt.setInt(1, RawMatCost);
				RawMatCoUpdatePstmt.setString(2, ManufLot);
				RawMatCoUpdatePstmt.setString(3, "OP10");
				RawMatCoUpdatePstmt.setString(4, "OC");
				RawMatCoUpdatePstmt.executeUpdate();
			}
			
			String PriceCalcSql = "SELECT SUM(RawMatCost) as SumOfRawMatCost FROM processcosttable WHERE ClosingMon = ? AND CostingLev = ? AND ProcessCode = ? AND InOutType = ?";
			PreparedStatement PriceCalcPstmt = conn.prepareStatement(PriceCalcSql);
			PriceCalcPstmt.setString(1, ClosingMon);
			PriceCalcPstmt.setString(2, "1");
			PriceCalcPstmt.setString(3, "OP10");
			PriceCalcPstmt.setString(4, "OC");
			ResultSet PriceCalcRs = PriceCalcPstmt.executeQuery();
			if(PriceCalcRs.next()) {
				BigDecimal TotalCost = new BigDecimal(PriceCalcRs.getString("SumOfRawMatCost"));
				String ItemSearchSql = "SELECT * FROM processcosttable WHERE ClosingMon = ? AND CostingLev = ? AND ProcessCode = ? AND InOutType = ?";
				PreparedStatement ItemSearchPstmt = conn.prepareStatement(ItemSearchSql);
				ItemSearchPstmt.setString(1, ClosingMon);
				ItemSearchPstmt.setString(2, "1");
				ItemSearchPstmt.setString(3, "OP10");
				ItemSearchPstmt.setString(4, "OC");
				ResultSet ItemSearchRs = ItemSearchPstmt.executeQuery();
				while(ItemSearchRs.next()) {
					String ManufItemCode = ItemSearchRs.getString("WorkOrd");
					BigDecimal RawmatCost = new BigDecimal(ItemSearchRs.getString("RawMatCost"));
					BigDecimal NewNeoOP30 = new BigDecimal(OP40);
					BigInteger OthMatCost = NewNeoOP30
						    .multiply(RawmatCost.divide(TotalCost, 10, RoundingMode.HALF_UP))
						    .setScale(0, RoundingMode.HALF_UP)
						    .toBigInteger();
					BigInteger MatCostSum = RawmatCost.toBigInteger().add(OthMatCost);
					String MatCostUpdateSql = "UPDATE processcosttable SET OthMatCost = ?, MatCostSum = ? WHERE "
							+ "ClosingMon = ? AND CostingLev = ? AND ProcessCode = ? AND InOutType = ? AND WorkOrd = ?";
					PreparedStatement MatCostUpdatePstmt = conn.prepareStatement(MatCostUpdateSql);
					MatCostUpdatePstmt.setDouble(1, OthMatCost.doubleValue());
					MatCostUpdatePstmt.setDouble(2, MatCostSum.doubleValue());
					MatCostUpdatePstmt.setString(3, ClosingMon);
					MatCostUpdatePstmt.setString(4, "1");
					MatCostUpdatePstmt.setString(5, "OP10");
					MatCostUpdatePstmt.setString(6, "OC");
					MatCostUpdatePstmt.setString(7, ManufItemCode);
					MatCostUpdatePstmt.executeUpdate();
				}
			}
			
			String ManufCalcSql = "SELECT * FROM processcosttable WHERE ClosingMon = ? AND CostingLev = ? AND InOutType = ? ORDER BY ProcessCode";
			PreparedStatement ManufCalcPstmt = conn.prepareStatement(ManufCalcSql);
			ManufCalcPstmt.setString(1, ClosingMon);
			ManufCalcPstmt.setString(2, "1");
			ManufCalcPstmt.setString(3, "OC");
			ResultSet MaunfCalcRs = ManufCalcPstmt.executeQuery();
			while(MaunfCalcRs.next()) {
				String ProcessType = MaunfCalcRs.getString("ProcessCode");
				
				String SwitchSql = null;
				PreparedStatement SwitecPstmt = null;
				ResultSet SwitchRs = null;
				
				String SwSearSql = null;
				PreparedStatement SwSearPstmt = null;
				ResultSet SwSearRs = null;
				
				String SwUpSql = null;
				PreparedStatement SwUpPstmt = null;
				ResultSet SwUpRs = null;
				
				int SumOption = 0;
				String ManufItem = null;
				switch(ProcessType) {
				case"OP10":
					SumOption = 0;
					SwitchSql = "SELECT SUM(MixTime) AS SumTime FROM processcosttable WHERE ClosingMon = ? AND CostingLev = ? AND InOutType = ? AND ProcessCode = ?";
					SwitecPstmt = conn.prepareStatement(SwitchSql);
					SwitecPstmt.setString(1, ClosingMon);
					SwitecPstmt.setString(2, "1");
					SwitecPstmt.setString(3, "OC");
					SwitecPstmt.setString(4, ProcessType);
					SwitchRs = SwitecPstmt.executeQuery();
					if(SwitchRs.next()) {
						SumOption = SwitchRs.getInt("SumTime");
					}
					SwSearSql = "SELECT * FROM processcosttable WHERE ClosingMon = ? AND CostingLev = ? AND InOutType = ? AND ProcessCode = ?";
					SwSearPstmt.setString(1, ClosingMon);
					SwSearPstmt.setString(2, "1");
					SwSearPstmt.setString(3, "OC");
					SwSearPstmt.setString(4, ProcessType);
					SwSearRs = SwitecPstmt.executeQuery();
					while(SwSearRs.next()) {
						ManufItem = SwSearRs.getString("WorkOrd");
						int MixTime = SwSearRs.getInt("MixTime");
						int ManufCost = (int) Math.round((double)OP10 * MixTime / SumOption);
						
						SwUpSql = "UPDATE processcosttable SET ManufCost = ? WHERE ClosingMon = ? AND CostingLev = ? AND InOutType = ? AND ProcessCode = ? AND WorkOrd = ?";
						SwUpPstmt.setInt(1, ManufCost);
						SwUpPstmt.setString(2, ClosingMon);
						SwUpPstmt.setString(3, "1");
						SwUpPstmt.setString(4, "OC");
						SwUpPstmt.setString(5, ProcessType);
						SwUpPstmt.setString(6, ManufItem);
						SwUpPstmt.executeUpdate();
					}
					break;
				case"OP20":
					SumOption = 0;
					SwitchSql = "SELECT COUNT(*) AS SumCount FROM processcosttable WHERE ClosingMon = ? AND CostingLev = ? AND InOutType = ? AND ProcessCode = ?";
					SwitecPstmt = conn.prepareStatement(SwitchSql);
					SwitecPstmt.setString(1, ClosingMon);
					SwitecPstmt.setString(2, "1");
					SwitecPstmt.setString(3, "OC");
					SwitecPstmt.setString(4, ProcessType);
					SwitchRs = SwitecPstmt.executeQuery();
					if(SwitchRs.next()) {
						SumOption = SwitchRs.getInt("SumCount");
					}
					SwSearSql = "SELECT * FROM processcosttable WHERE ClosingMon = ? AND CostingLev = ? AND InOutType = ? AND ProcessCode = ?";
					SwSearPstmt.setString(1, ClosingMon);
					SwSearPstmt.setString(2, "1");
					SwSearPstmt.setString(3, "OC");
					SwSearPstmt.setString(4, ProcessType);
					SwSearRs = SwitecPstmt.executeQuery();
					while(SwSearRs.next()) {
						ManufItem = SwSearRs.getString("WorkOrd");
						int MixTime = SwSearRs.getInt("MixTime");
						int ManufCost = (int) Math.round((double)OP20 * 1 / SumOption);
						
						SwUpSql = "UPDATE processcosttable SET ManufCost = ? WHERE ClosingMon = ? AND CostingLev = ? AND InOutType = ? AND ProcessCode = ? AND WorkOrd = ?";
						SwUpPstmt.setInt(1, ManufCost);
						SwUpPstmt.setString(2, ClosingMon);
						SwUpPstmt.setString(3, "1");
						SwUpPstmt.setString(4, "OC");
						SwUpPstmt.setString(5, ProcessType);
						SwUpPstmt.setString(6, ManufItem);
						SwUpPstmt.executeUpdate();
					}
					break;
				case"OP30":
					break;
				}
			}
			
			OK = "Yes";
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			OK = "No";
		}
		
		return OK;
	}
	
}
