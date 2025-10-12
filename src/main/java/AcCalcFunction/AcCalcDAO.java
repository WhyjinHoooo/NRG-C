package AcCalcFunction;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.json.JSONArray;
import org.json.JSONObject;

import com.mysql.cj.x.protobuf.MysqlxPrepare.Prepare;

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
	
	public String CostAllocation(JSONObject jsonObj) {
		connDB();
		
		String[] keyOrder = {"AIDMonth", "OP10", "OP20", "OP30", "OP40", "OP50", "ComCode", "PlantCode"};
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
			String CalcGsp = StockVariance(DataList[0].trim(), DataList[1].trim(), DataList[2].trim(), DataList[3].trim(), DataList[4].trim());
			String WipCalc = WipmatCost(DataList[0].trim());
			
			if (CalcResult.equals("Yes") /* && CalcGsp.equals("Yes") && WipCalc.equals("Yes") */) {
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
	private String WipmatCost(String ClosingMonth) {
		// TODO Auto-generated method stub
		String result = null;
		String SelectSql = "SELECT * FROM processcosttable WHERE ClosingMon = ? AND InOutType = ? ORDER BY CostingLev, ProcessCode";
		
		PreparedStatement SelectPstmt = null;
		ResultSet SelectRs = null;
		
		String WipUpSql = null;
		PreparedStatement WipUpPstmt = null;
		
		try {
			SelectPstmt = conn.prepareStatement(SelectSql);
			SelectPstmt.setString(1, ClosingMonth);
			SelectPstmt.setString(2, "OC");
			SelectRs = SelectPstmt.executeQuery();
			while(SelectRs.next()) {
				String KeyValue = SelectRs.getString("KeyValue");
				
				int FertMatCost = 0; //재품재료비
				int FertManufCost = 0; // 재품가공비
				
				double WipQrt = SelectRs.getDouble("WipQty"); // 기말재공중량
				double ProdQty = SelectRs.getDouble("ProdQty"); // 제품중량
				
				double MatCostSum = SelectRs.getDouble("MatCostSum"); // 재료비 합계
				double ManufCostSum = SelectRs.getDouble("ManufCostSum"); // 가공비 합계 
				
				int WipMatCost = 0; // 재공재료비
				int WipMnaufCost = 0; // 재공가공비
				if(WipQrt > 0 && ProdQty > 0) {
					WipMatCost = (int)Math.round(MatCostSum * WipQrt / (WipQrt + ProdQty)); 
					WipMnaufCost = (int)Math.round(ManufCostSum * WipQrt / (WipQrt + ProdQty)); 
					 
					FertMatCost = (int)Math.round(MatCostSum - WipMatCost);
					FertManufCost = (int)Math.round(ManufCostSum - WipMnaufCost);
				}else if(WipQrt == 0) {
					WipMatCost = 0;
					WipMnaufCost = 0;
					FertMatCost = (int)MatCostSum;
					FertManufCost = (int)ManufCostSum;
				}else if(ProdQty == 0){
					WipMatCost = (int)MatCostSum;
					WipMnaufCost = (int)ManufCostSum;
					FertMatCost = 0;
					FertManufCost = 0;
				}
				
				WipUpSql = "UPDATE processcosttable SET WipMatCost = ?, WipMnaufCost = ?, FertMatCost = ?, FertManufCost = ? WHERE ClosingMon = ? AND KeyValue = ?";
				WipUpPstmt = conn.prepareStatement(WipUpSql);
				WipUpPstmt.setInt(1, WipMatCost);
				WipUpPstmt.setInt(2, WipMnaufCost);
				WipUpPstmt.setInt(3, FertMatCost);
				WipUpPstmt.setInt(4, FertManufCost);
				WipUpPstmt.setString(5, ClosingMonth);
				WipUpPstmt.setString(6, KeyValue);
				WipUpPstmt.executeUpdate();
			}
			result = "Yes";
		}catch (Exception e) {
			// TODO: handle exception
			result = "No";
			e.printStackTrace();
		}finally {
			try {
				if (WipUpPstmt != null) WipUpPstmt.close();
			} catch (SQLException e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
		}
		 
		return result;
	}

	private String StockVariance(String ClosingMonth, String MC, String IC, String PC, String CMC) {
		// MC : 교반 가공비, IC : 검사 가공비, PC : 소분 가공비, CMC : 공통 재료비
		String ClosingMon = ClosingMonth;
		BigDecimal OP10 = new BigDecimal(MC.replace(",", ""));
		BigDecimal OP20 = new BigDecimal(IC.replace(",", ""));
		BigDecimal OP30 = new BigDecimal(PC.replace(",", ""));
		BigDecimal OP40 = new BigDecimal(CMC.replace(",", ""));
		String result = null;
		BigDecimal[] DataArray = {OP10, OP20, OP30, OP40};
		for(int i = 0 ; i < DataArray.length ; i++) {
			System.out.println(DataArray[i]);
			String SwSql = null;
			PreparedStatement SwPstmt = null;
			ResultSet SwRs = null;
			
			String Sw_SearchSql = null;
			PreparedStatement Sw_SearchPstmt = null;
			ResultSet Sw_SearchRs = null;
		
			String Sw_UpSql = null;
			PreparedStatement Sw_UpPstmt = null;
			try {
				switch(i) {
				case 0:
					SwSql = "SELECT SUM(ManufCost) as SumOfManufCost FROM processcosttable WHERE ClosingMon = ? AND ProcessCode = ? AND InOutType = ?";
					SwPstmt = conn.prepareStatement(SwSql);
					SwPstmt.setString(1, ClosingMon);
					SwPstmt.setString(2, "OP10");
					SwPstmt.setString(3, "OC");
					SwRs = SwPstmt.executeQuery();
					if(SwRs.next()) {
						BigDecimal ManufCost = SwRs.getBigDecimal("SumOfManufCost");
						System.out.println("교반 가공비의 발생 가공비 OP10 : " + ManufCost);
						System.out.println("입력한 금액 OP10 : " + DataArray[i]);
						BigDecimal gap = DataArray[i].subtract(ManufCost);
						Sw_SearchSql = "SELECT * FROM processcosttable WHERE ClosingMon = ? AND ProcessCode = ? AND InOutType = ? ORDER BY CostingLev";
						Sw_SearchPstmt = conn.prepareStatement(Sw_SearchSql);
						Sw_SearchPstmt.setString(1, ClosingMon);
						Sw_SearchPstmt.setString(2, "OP10");
						Sw_SearchPstmt.setString(3, "OC");
						Sw_SearchRs = Sw_SearchPstmt.executeQuery();
						if(Sw_SearchRs.next()) {
							String WorkOrdDoc = Sw_SearchRs.getString("WorkOrd");
							BigDecimal UpValue = Sw_SearchRs.getBigDecimal("ManufCost");
							if(gap.compareTo(BigDecimal.ZERO) < 0) {
								UpValue = UpValue.subtract(gap.abs());
							}else {
								UpValue = UpValue.add(gap.abs());
							}
							Sw_UpSql = "UPDATE processcosttable SET ManufCost = ?, ManufCostSum = ? WHERE ClosingMon = ? AND ProcessCode = ? AND InOutType = ? AND WorkOrd = ?";
							Sw_UpPstmt = conn.prepareStatement(Sw_UpSql);
							Sw_UpPstmt.setBigDecimal(1, UpValue);
							Sw_UpPstmt.setBigDecimal(2, UpValue);
							Sw_UpPstmt.setString(3, ClosingMon);
							Sw_UpPstmt.setString(4, "OP10");
							Sw_UpPstmt.setString(5, "OC");
							Sw_UpPstmt.setString(6, WorkOrdDoc);
							Sw_UpPstmt.executeUpdate();
						}
					}
					break;
				case 1:
					SwSql = "SELECT SUM(ManufCost) as SumOfManufCost FROM processcosttable WHERE ClosingMon = ? AND ProcessCode = ? AND InOutType = ?";
					SwPstmt = conn.prepareStatement(SwSql);
					SwPstmt.setString(1, ClosingMon);
					SwPstmt.setString(2, "OP20");
					SwPstmt.setString(3, "OC");
					SwRs = SwPstmt.executeQuery();
					if(SwRs.next()) {
						BigDecimal ManufCost = SwRs.getBigDecimal("SumOfManufCost");
						System.out.println("검사 가공비의 발생 가공비 OP20 : " + ManufCost);
						System.out.println("입력한 금액 OP20 : " + DataArray[i]);
						BigDecimal gap = DataArray[i].subtract(ManufCost);
						Sw_SearchSql = "SELECT * FROM processcosttable WHERE ClosingMon = ? AND ProcessCode = ? AND InOutType = ? ORDER BY CostingLev";
						Sw_SearchPstmt = conn.prepareStatement(Sw_SearchSql);
						Sw_SearchPstmt.setString(1, ClosingMon);
						Sw_SearchPstmt.setString(2, "OP20");
						Sw_SearchPstmt.setString(3, "OC");
						Sw_SearchRs = Sw_SearchPstmt.executeQuery();
						if(Sw_SearchRs.next()) {
							String WorkOrdDoc = Sw_SearchRs.getString("WorkOrd");
							BigDecimal UpValue = Sw_SearchRs.getBigDecimal("ManufCost");
							if(gap.compareTo(BigDecimal.ZERO) < 0) {
								UpValue = UpValue.subtract(gap.abs());
							}else {
								UpValue = UpValue.add(gap.abs());
							}
							Sw_UpSql = "UPDATE processcosttable SET ManufCost = ?, ManufCostSum = ? WHERE ClosingMon = ? AND ProcessCode = ? AND InOutType = ? AND WorkOrd = ?";
							Sw_UpPstmt = conn.prepareStatement(Sw_UpSql);
							Sw_UpPstmt.setBigDecimal(1, UpValue);
							Sw_UpPstmt.setBigDecimal(2, UpValue);
							Sw_UpPstmt.setString(3, ClosingMon);
							Sw_UpPstmt.setString(4, "OP20");
							Sw_UpPstmt.setString(5, "OC");
							Sw_UpPstmt.setString(6, WorkOrdDoc);
							Sw_UpPstmt.executeUpdate();
						}
					}
					break;
				case 2:
					SwSql = "SELECT SUM(ManufCost) as SumOfManufCost FROM processcosttable WHERE ClosingMon = ? AND ProcessCode = ? AND InOutType = ?";
					SwPstmt = conn.prepareStatement(SwSql);
					SwPstmt.setString(1, ClosingMon);
					SwPstmt.setString(2, "OP30");
					SwPstmt.setString(3, "OC");
					SwRs = SwPstmt.executeQuery();
					if(SwRs.next()) {
						BigDecimal ManufCost = SwRs.getBigDecimal("SumOfManufCost");
						System.out.println("소분 가공비의 발생 가공비 OP30 : " + ManufCost);
						System.out.println("입력한 금액 OP30 : " + DataArray[i]);
						BigDecimal gap = DataArray[i].subtract(ManufCost);
						Sw_SearchSql = "SELECT * FROM processcosttable WHERE ClosingMon = ? AND ProcessCode = ? AND InOutType = ? ORDER BY CostingLev";
						Sw_SearchPstmt = conn.prepareStatement(Sw_SearchSql);
						Sw_SearchPstmt.setString(1, ClosingMon);
						Sw_SearchPstmt.setString(2, "OP30");
						Sw_SearchPstmt.setString(3, "OC");
						Sw_SearchRs = Sw_SearchPstmt.executeQuery();
						if(Sw_SearchRs.next()) {
							String WorkOrdDoc = Sw_SearchRs.getString("WorkOrd");
							BigDecimal UpValue = Sw_SearchRs.getBigDecimal("ManufCost");
							if(gap.compareTo(BigDecimal.ZERO) < 0) {
								UpValue = UpValue.subtract(gap.abs());
							}else {
								UpValue = UpValue.add(gap.abs());
							}
							Sw_UpSql = "UPDATE processcosttable SET ManufCost = ?, ManufCostSum = ? WHERE ClosingMon = ? AND ProcessCode = ? AND InOutType = ? AND WorkOrd = ?";
							Sw_UpPstmt = conn.prepareStatement(Sw_UpSql);
							Sw_UpPstmt.setBigDecimal(1, UpValue);
							Sw_UpPstmt.setBigDecimal(2, UpValue);
							Sw_UpPstmt.setString(3, ClosingMon);
							Sw_UpPstmt.setString(4, "OP30");
							Sw_UpPstmt.setString(5, "OC");
							Sw_UpPstmt.setString(6, WorkOrdDoc);
							Sw_UpPstmt.executeUpdate();
						}
					}
					break;
				case 3:
					SwSql = "SELECT SUM(OthMatCost) as SumOfOthMatCost FROM processcosttable WHERE ClosingMon = ? AND ProcessCode = ? AND InOutType = ?";
					SwPstmt = conn.prepareStatement(SwSql);
					SwPstmt.setString(1, ClosingMon);
					SwPstmt.setString(2, "OP10");
					SwPstmt.setString(3, "OC");
					SwRs = SwPstmt.executeQuery();
					if(SwRs.next()) {
						BigDecimal ManufCost = SwRs.getBigDecimal("SumOfOthMatCost");
						System.out.println("재료비 OP40 : " + ManufCost);
						System.out.println("입력한 금액 OP40 : " + DataArray[i]);
						BigDecimal gap = DataArray[i].subtract(ManufCost);
						Sw_SearchSql = "SELECT * FROM processcosttable WHERE ClosingMon = ? AND ProcessCode = ? AND InOutType = ? ORDER BY CostingLev";
						Sw_SearchPstmt = conn.prepareStatement(Sw_SearchSql);
						Sw_SearchPstmt.setString(1, ClosingMon);
						Sw_SearchPstmt.setString(2, "OP10");
						Sw_SearchPstmt.setString(3, "OC");
						Sw_SearchRs = Sw_SearchPstmt.executeQuery();
						if(Sw_SearchRs.next()) {
							String WorkOrdDoc = Sw_SearchRs.getString("WorkOrd");
							BigDecimal UpValue = Sw_SearchRs.getBigDecimal("OthMatCost");
							BigDecimal UpMatCostSum = Sw_SearchRs.getBigDecimal("MatCostSum");
							if(gap.compareTo(BigDecimal.ZERO) < 0) {
								UpValue = UpValue.subtract(gap.abs());
								UpMatCostSum = UpMatCostSum.subtract(gap.abs());
							}else {
								UpValue = UpValue.add(gap.abs());
								UpMatCostSum = UpMatCostSum.add(gap.abs());
							}
							
							Sw_UpSql = "UPDATE processcosttable SET OthMatCost = ?, MatCostSum = ? WHERE ClosingMon = ? AND ProcessCode = ? AND InOutType = ? AND WorkOrd = ?";
							Sw_UpPstmt = conn.prepareStatement(Sw_UpSql);
							Sw_UpPstmt.setBigDecimal(1, UpValue);
							Sw_UpPstmt.setBigDecimal(2, UpMatCostSum);
							Sw_UpPstmt.setString(3, ClosingMon);
							Sw_UpPstmt.setString(4, "OP10");
							Sw_UpPstmt.setString(5, "OC");
							Sw_UpPstmt.setString(6, WorkOrdDoc);
							Sw_UpPstmt.executeUpdate();
						}
					}
					break;
				}
				result = "Yes";
			}catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
				result = "No";
			}finally {
				try {
					if (SwPstmt != null) SwPstmt.close();
		            if (SwRs != null) SwRs.close();
		            if (Sw_SearchPstmt != null) Sw_SearchPstmt.close();
		            if (Sw_SearchRs != null) Sw_SearchRs.close();
		            if (Sw_UpPstmt != null) Sw_UpPstmt.close();
				}catch(SQLException e2) {
					e2.printStackTrace();
				}
			}
		}
		return result;
	}

	public String FirstProcess(String ClosingMonth, String MC, String IC, String PC, String CMC) {
		// MC : 교반 가공비, IC : 검사 가공비, PC : 소분 가공비, CMC : 공통 재료비
		String ClosingMon = ClosingMonth;
		BigDecimal OP10 = new BigDecimal(MC.replace(",", ""));
		BigDecimal OP20 = new BigDecimal(IC.replace(",", ""));
		BigDecimal OP30 = new BigDecimal(PC.replace(",", ""));
		BigDecimal OP40 = new BigDecimal(CMC.replace(",", ""));
		String OK = null;
		System.out.println();

		String B012 = SetRawmAmt_OutAmt(ClosingMon, OP40, OK);
		if(B012.equals("Yes")) {
			String B03 = SetProcessAmt(ClosingMon, OP10, OP20, OP30, B012);
			OK = B03;
		}
//		
		
		return OK;
	}
	private String SetRawmAmt_OutAmt(String ClosingMon, BigDecimal OP40, String Chk) {
		// TODO Auto-generated method stub

		BigDecimal RawMatCost = BigDecimal.ZERO;
		
		SelectSql = "SELECT * FROM processcosttable WHERE ClosingMon = ? AND ProcessCode = ? AND InOutType = ? ORDER BY CostingLev";
		PreparedStatement SelectPstmt = null;
		ResultSet SelectRs = null;
		
		PreparedStatement LineSearchPstmt = null;
		ResultSet LineSearchRs = null;
		
		PreparedStatement PriceCalcPstmt = null;
		ResultSet PriceCalcRs = null;
		
		PreparedStatement ItemSearchPstmt = null;
		ResultSet ItemSearchRs = null;
		
		PreparedStatement MatCostUpdatePstmt = null;
		try {
			SelectPstmt = conn.prepareStatement(SelectSql);
			SelectPstmt.setString(1, ClosingMon); // 결산하는 달 예: 202504
			SelectPstmt.setString(2, "OP10"); // 교반공
			SelectPstmt.setString(3, "OC");
			SelectRs = SelectPstmt.executeQuery();
			while(SelectRs.next()) {
				String WorkOrd =  SelectRs.getString("WorkOrd");
				String LineSearchSql = "SELECT SUM(amount) as SumAmt FROM InvenLogl WHERE workordnum = ? AND process = ? AND mattype = ?";
				LineSearchPstmt = conn.prepareStatement(LineSearchSql);
				LineSearchPstmt.setString(1, WorkOrd);
				LineSearchPstmt.setString(2, "OP10");
				LineSearchPstmt.setString(3, "RAWM");
				LineSearchRs = LineSearchPstmt.executeQuery();
				RawMatCost = BigDecimal.ZERO;
				if(LineSearchRs.next()) {
					RawMatCost = RawMatCost.add(LineSearchRs.getBigDecimal("SumAmt"));
				}
				String RawMatCoUpdateSql = "UPDATE processcosttable SET RawMatCost = ? WHERE WorkOrd = ? AND ProcessCode = ? AND InOutType = ?";
				PreparedStatement RawMatCoUpdatePstmt = conn.prepareStatement(RawMatCoUpdateSql);
				RawMatCoUpdatePstmt.setBigDecimal(1, RawMatCost);
				RawMatCoUpdatePstmt.setString(2, WorkOrd);
				RawMatCoUpdatePstmt.setString(3, "OP10");
				RawMatCoUpdatePstmt.setString(4, "OC");
				RawMatCoUpdatePstmt.executeUpdate();
			}
			
			String PriceCalcSql = "SELECT SUM(RawMatCost) as SumOfRawMatCost FROM processcosttable WHERE ClosingMon = ? AND ProcessCode = ? AND InOutType = ?";
			PriceCalcPstmt = conn.prepareStatement(PriceCalcSql);
			PriceCalcPstmt.setString(1, ClosingMon);
			PriceCalcPstmt.setString(2, "OP10");
			PriceCalcPstmt.setString(3, "OC");
			PriceCalcRs = PriceCalcPstmt.executeQuery();
			if(PriceCalcRs.next()) {
				BigDecimal TotalCost = PriceCalcRs.getBigDecimal("SumOfRawMatCost");
				String ItemSearchSql = "SELECT * FROM processcosttable WHERE ClosingMon = ? AND ProcessCode = ? AND InOutType = ?";
				ItemSearchPstmt = conn.prepareStatement(ItemSearchSql);
				ItemSearchPstmt.setString(1, ClosingMon);
				ItemSearchPstmt.setString(2, "OP10");
				ItemSearchPstmt.setString(3, "OC");
				ItemSearchRs = ItemSearchPstmt.executeQuery();
				while(ItemSearchRs.next()) {
					String ManufItemCode = ItemSearchRs.getString("WorkOrd");
					BigDecimal RawmatCost = ItemSearchRs.getBigDecimal("RawMatCost");
					BigDecimal OthMatCost = OP40.multiply(RawmatCost.divide(TotalCost, 10, RoundingMode.HALF_UP)).setScale(0, RoundingMode.HALF_UP);
					BigDecimal MatCostSum = RawmatCost.add(OthMatCost);
					String MatCostUpdateSql = "UPDATE processcosttable SET OthMatCost = ?, MatCostSum = ? WHERE "
							+ "ClosingMon = ? AND ProcessCode = ? AND InOutType = ? AND WorkOrd = ?";
					MatCostUpdatePstmt = conn.prepareStatement(MatCostUpdateSql);
					MatCostUpdatePstmt.setBigDecimal(1, OthMatCost);
					MatCostUpdatePstmt.setBigDecimal(2, MatCostSum);
					MatCostUpdatePstmt.setString(3, ClosingMon);
					MatCostUpdatePstmt.setString(4, "OP10");
					MatCostUpdatePstmt.setString(5, "OC");
					MatCostUpdatePstmt.setString(6, ManufItemCode);
					MatCostUpdatePstmt.executeUpdate();
				}
			}
			Chk = "Yes";
		}catch (SQLException e) {
			e.printStackTrace();
			Chk = "No";
		}finally {
			try {
				if (LineSearchPstmt != null) LineSearchPstmt.close();
	            if (LineSearchRs != null) LineSearchRs.close();
	            if (PriceCalcPstmt != null) PriceCalcPstmt.close();
	            if (PriceCalcRs != null) PriceCalcRs.close();
	            if (ItemSearchPstmt != null) ItemSearchPstmt.close();
	            if (ItemSearchRs != null) ItemSearchRs.close();
	            if (MatCostUpdatePstmt != null) MatCostUpdatePstmt.close();
			} catch (SQLException e2) {
				e2.printStackTrace();
			}
		}
		return Chk;
	}
	private String SetProcessAmt(String ClosingMon, BigDecimal OP10, BigDecimal OP20, BigDecimal OP30, String Chk) {
		// TODO Auto-generated method stub
		String ManufCalcSql = "SELECT * FROM processcosttable WHERE ClosingMon = ? AND InOutType = ? ORDER BY ProcessCode";
		
		PreparedStatement ManufCalcPstmt = null;
		ResultSet MaunfCalcRs = null;
		
		String SwitchSql = null;
		PreparedStatement SwitecPstmt = null;
		ResultSet SwitchRs = null;
		
		String SwSearSql = null;
		PreparedStatement SwSearPstmt = null;
		ResultSet SwSearRs = null;
		
		String SwUpSql = null;
		PreparedStatement SwUpPstmt = null;
		ResultSet SwUpRs = null;
		try {	
			ManufCalcPstmt = conn.prepareStatement(ManufCalcSql);
			ManufCalcPstmt.setString(1, ClosingMon);
			ManufCalcPstmt.setString(2, "OC");
			MaunfCalcRs = ManufCalcPstmt.executeQuery();
			while(MaunfCalcRs.next()) {
				String ProcessType = MaunfCalcRs.getString("ProcessCode");
				BigDecimal SumOption = BigDecimal.ZERO;
				String ManufItem = null;
				switch(ProcessType) {
				case"OP10":
					SumOption = BigDecimal.ZERO;
					SwitchSql = "SELECT SUM(MixTime) AS SumTime FROM processcosttable WHERE ClosingMon = ? AND InOutType = ? AND ProcessCode = ?";
					SwitecPstmt = conn.prepareStatement(SwitchSql);
					SwitecPstmt.setString(1, ClosingMon);
					SwitecPstmt.setString(2, "OC");
					SwitecPstmt.setString(3, ProcessType);
					SwitchRs = SwitecPstmt.executeQuery();
					if(SwitchRs.next()) {
						SumOption = SumOption.add(SwitchRs.getBigDecimal("SumTime"));
					}
					SwSearSql = "SELECT * FROM processcosttable WHERE ClosingMon = ? AND InOutType = ? AND ProcessCode = ?";
					SwSearPstmt = conn.prepareStatement(SwSearSql);
					SwSearPstmt.setString(1, ClosingMon);
					SwSearPstmt.setString(2, "OC");
					SwSearPstmt.setString(3, ProcessType);
					SwSearRs = SwSearPstmt.executeQuery();
					while(SwSearRs.next()) {
						ManufItem = SwSearRs.getString("WorkOrd");
						BigDecimal MixTime = SwSearRs.getBigDecimal("MixTime");
						BigDecimal ManufCost = OP10.multiply(MixTime.divide(SumOption, 10, RoundingMode.HALF_UP)).setScale(0, RoundingMode.HALF_UP);
						
						SwUpSql = "UPDATE processcosttable SET ManufCost = ?, ManufCostSum = ? WHERE ClosingMon = ? AND InOutType = ? AND ProcessCode = ? AND WorkOrd = ?";
						SwUpPstmt = conn.prepareStatement(SwUpSql);
						SwUpPstmt.setBigDecimal(1, ManufCost);
						SwUpPstmt.setBigDecimal(2, ManufCost);
						SwUpPstmt.setString(3, ClosingMon);
						SwUpPstmt.setString(4, "OC");
						SwUpPstmt.setString(5, ProcessType);
						SwUpPstmt.setString(6, ManufItem);
						SwUpPstmt.executeUpdate();
					}
					break;
				case"OP20":
					SumOption = BigDecimal.ZERO;
					SwitchSql = "SELECT COUNT(*) AS SumCount FROM processcosttable WHERE ClosingMon = ? AND InOutType = ? AND ProcessCode = ?";
					SwitecPstmt = conn.prepareStatement(SwitchSql);
					SwitecPstmt.setString(1, ClosingMon);
					SwitecPstmt.setString(2, "OC");
					SwitecPstmt.setString(3, ProcessType);
					SwitchRs = SwitecPstmt.executeQuery();
					if(SwitchRs.next()) {
						SumOption = SumOption.add(SwitchRs.getBigDecimal("SumCount"));  
					}
					SwSearSql = "SELECT * FROM processcosttable WHERE ClosingMon = ? AND InOutType = ? AND ProcessCode = ?";
					SwSearPstmt = conn.prepareStatement(SwSearSql);
					SwSearPstmt.setString(1, ClosingMon);
					SwSearPstmt.setString(2, "OC");
					SwSearPstmt.setString(3, ProcessType);
					SwSearRs = SwSearPstmt.executeQuery();
					while(SwSearRs.next()) {
						ManufItem = SwSearRs.getString("WorkOrd");
						BigDecimal ManufCost = OP20.divide(SumOption, 0, RoundingMode.HALF_UP); //(int) Math.round((double)OP20 * 1 / SumOption);
						
						SwUpSql = "UPDATE processcosttable SET ManufCost = ?, ManufCostSum = ? WHERE ClosingMon = ? AND InOutType = ? AND ProcessCode = ? AND WorkOrd = ?";
						SwUpPstmt = conn.prepareStatement(SwUpSql);
						SwUpPstmt.setBigDecimal(1, ManufCost);
						SwUpPstmt.setBigDecimal(2, ManufCost);
						SwUpPstmt.setString(3, ClosingMon);
						SwUpPstmt.setString(4, "OC");
						SwUpPstmt.setString(5, ProcessType);
						SwUpPstmt.setString(6, ManufItem);
						SwUpPstmt.executeUpdate();
					}
					break;
				case"OP30":
					SumOption = BigDecimal.ZERO;
					SwitchSql = "SELECT SUM(InputQty) AS SumQty FROM processcosttable WHERE ClosingMon = ? AND InOutType = ? AND ProcessCode = ?";
					SwitecPstmt = conn.prepareStatement(SwitchSql);
					SwitecPstmt.setString(1, ClosingMon);
					SwitecPstmt.setString(2, "OC");
					SwitecPstmt.setString(3, ProcessType);
					SwitchRs = SwitecPstmt.executeQuery();
					if(SwitchRs.next()) {
						SumOption = SumOption.add(SwitchRs.getBigDecimal("SumQty"));
					}
					SwSearSql = "SELECT * FROM processcosttable WHERE ClosingMon = ? AND InOutType = ? AND ProcessCode = ?";
					SwSearPstmt = conn.prepareStatement(SwSearSql);
					SwSearPstmt.setString(1, ClosingMon);
					SwSearPstmt.setString(2, "OC");
					SwSearPstmt.setString(3, ProcessType);
					SwSearRs = SwSearPstmt.executeQuery();
					while(SwSearRs.next()) {
						ManufItem = SwSearRs.getString("WorkOrd");
						BigDecimal InputQty = SwSearRs.getBigDecimal("InputQty");
						BigDecimal ManufCost = OP30.multiply(InputQty.divide(SumOption, 10, RoundingMode.HALF_UP)).setScale(0, RoundingMode.HALF_UP);//(int) Math.round((double)OP30 * InputQty / SumOption);
						SwUpSql = "UPDATE processcosttable SET ManufCost = ?, ManufCostSum = ? WHERE ClosingMon = ? AND InOutType = ? AND ProcessCode = ? AND WorkOrd = ?";
						SwUpPstmt = conn.prepareStatement(SwUpSql);
						SwUpPstmt.setBigDecimal(1, ManufCost);
						SwUpPstmt.setBigDecimal(2, ManufCost);
						SwUpPstmt.setString(3, ClosingMon);
						SwUpPstmt.setString(4, "OC");
						SwUpPstmt.setString(5, ProcessType);
						SwUpPstmt.setString(6, ManufItem);
						SwUpPstmt.executeUpdate();
					}
					break;
				}
			}
			Chk = "Yes";
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			Chk = "No";
		}
		return Chk;
	}	
}
