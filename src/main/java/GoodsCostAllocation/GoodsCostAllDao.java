package GoodsCostAllocation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.tomcat.jni.Local;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mysql.cj.x.protobuf.MysqlxPrepare.Prepare;
import com.mysql.cj.xdevapi.Result;

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
	private String Cd; // 기업코드
	private String Pd; // 공장코드
	private String Cm; // 결산월
	
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
		Cd = DataList[0].trim();
		Pd = DataList[1].trim();
		Cm = DataList[2].trim();
		ResultSet rs = null;
	    JSONArray jsonArray = new JSONArray();
	    try {
	    	String SelectSql = "SELECT ClosingMon, ComCode, PlantCode, WorkType, CostingLev, WorkOrd, "
	    			+ "SUM(WipMatCost) as SumOfWMC, SUM(WipMnaufCost) as SumOfWMfC, SUM(FertMatCost) as SumOfFMC, SUM(FertManufCost) as SumOfFMfC "
	    			+ "FROM processcosttable WHERE ComCode = ? AND PlantCode = ? AND ClosingMon = ? GROUP BY WorkOrd ORDER BY CostingLev ASC";
	    	pstmt = conn.prepareStatement(SelectSql);
	    	pstmt.setString(1, Cd);
	    	pstmt.setString(2, Pd);
	    	pstmt.setString(3, Cm);
	    	rs = pstmt.executeQuery();
	    	while(rs.next()) {
	    		JSONObject jsonObject = new JSONObject();
	    		jsonObject.put("ClosingMon", rs.getString("ClosingMon"));
		    	jsonObject.put("ComCode", rs.getString("ComCode"));
		    	jsonObject.put("PlantCode", rs.getString("PlantCode"));
		    	jsonObject.put("WorkType", rs.getString("WorkType"));
		    	jsonObject.put("CostingLev", rs.getString("CostingLev"));
		    	jsonObject.put("WorkOrd", rs.getString("WorkOrd"));
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
		Cd = DataList[0].trim();
		Pd = DataList[1].trim();
		Cm = DataList[2].trim();
		ResultSet rs = null;
		try {
			String FirstProcess = AProcess(Cd, Pd, Cm);
			if(FirstProcess.equals("success")) {
				String Lv1Procedd = CProcess(Cm, 1);
			}else {
				result = FirstProcess;
			}
			result = "Good";
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return result;
	}

	private String CProcess(String CalcMon, int Lv) {
		// TODO Auto-generated method stub
		this.Cm = CalcMon;
		int CalcLevel = Lv;
		String FindLv1Data = "SELECT * FROM processcosttable_Copy WHERE ClosingMon = ? AND CostingLev = ? ORDER BY ProcessCode ASC";
		PreparedStatement FindLv1Pstmt = null;
		ResultSet FindLv1Rs = null;
		
		PreparedStatement Lv1UpdatePstmt = null;
		try {
			FindLv1Pstmt = conn.prepareStatement(FindLv1Data);
			FindLv1Pstmt.setString(1, Cm);
			FindLv1Pstmt.setInt(2, CalcLevel);
			FindLv1Rs = FindLv1Pstmt.executeQuery();
			
			BigDecimal MatCostSum = BigDecimal.ZERO;
			BigDecimal ManufCostSum = BigDecimal.ZERO;
			
			BigDecimal WipQty = BigDecimal.ZERO;
			BigDecimal ProdQty = BigDecimal.ZERO;
			
			BigDecimal WipMatCost = BigDecimal.ZERO;
			BigDecimal WipMnaufCost = BigDecimal.ZERO;
			
			String KeyValue = null;
			
			while(FindLv1Rs.next()) {
				MatCostSum = FindLv1Rs.getBigDecimal("MatCostSum");
				if(MatCostSum == null) MatCostSum = BigDecimal.ZERO;
				ManufCostSum = FindLv1Rs.getBigDecimal("ManufCostSum");
				if(ManufCostSum == null) ManufCostSum = BigDecimal.ZERO;
				
				WipQty = FindLv1Rs.getBigDecimal("WipQty");
				KeyValue = FindLv1Rs.getString("KeyValue");
				String Lv1UpdateSql = null;
				if(WipQty.compareTo(BigDecimal.ZERO) == 0) {
					Lv1UpdateSql = "UPDATE processcosttable_Copy SET FertMatCost = ?, FertManufCost = ? WHERE KeyValue = ?";
					Lv1UpdatePstmt = conn.prepareStatement(Lv1UpdateSql);
					
					Lv1UpdatePstmt.setBigDecimal(1, MatCostSum);
					Lv1UpdatePstmt.setBigDecimal(2, ManufCostSum);
					Lv1UpdatePstmt.setString(3, KeyValue);
				}else if(WipQty.compareTo(BigDecimal.ZERO) > 0) {
					ProdQty = FindLv1Rs.getBigDecimal("ProdQty");
					Lv1UpdateSql = "UPDATE processcosttable_Copy SET FertMatCost = ?, FertManufCost = ?, WipMatCost = ?, WipMnaufCost = ? WHERE KeyValue = ?";
					Lv1UpdatePstmt = conn.prepareStatement(Lv1UpdateSql);
					
					WipMatCost = MatCostSum.multiply(WipQty.divide(WipQty.add(ProdQty), 10, RoundingMode.HALF_UP)).setScale(0, RoundingMode.HALF_UP);
					WipMnaufCost = ManufCostSum.multiply(WipQty.divide(WipQty.add(ProdQty), 10, RoundingMode.HALF_UP)).setScale(0, RoundingMode.HALF_UP);
					
					Lv1UpdatePstmt.setBigDecimal(1, MatCostSum.subtract(WipMatCost));
					Lv1UpdatePstmt.setBigDecimal(2, ManufCostSum.subtract(WipMnaufCost));
					Lv1UpdatePstmt.setBigDecimal(3, WipMatCost);
					Lv1UpdatePstmt.setBigDecimal(4, WipMnaufCost);
					Lv1UpdatePstmt.setString(5, KeyValue);
				}
				Lv1UpdatePstmt.executeUpdate();
			}
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
			System.err.println("❌ CProcess 첫 번쨰 SQL 오류: " + e.getMessage());
			return "fail";
		}catch (NullPointerException e) {
		    e.printStackTrace();
		    System.err.println("❌ NullPointer 오류: PreparedStatement 미생성 등 확인 필요");
		    return "fail";
		} catch (Exception e) {
		    e.printStackTrace();
		    System.err.println("❌ 알 수 없는 오류: " + e.getMessage());
		    return "fail";
		} finally {
			if(FindLv1Rs != null) try { FindLv1Rs.close(); } catch(SQLException e) {}
			if(FindLv1Pstmt != null) try { FindLv1Pstmt.close(); } catch(SQLException e) {}
			if(Lv1UpdatePstmt != null) try { Lv1UpdatePstmt.close(); } catch(SQLException e) {}
		}
		
		FindLv1Data = "SELECT WorkOrd, SUM(FertMatCost) as FertSumMCost, SUM(FertManufCost) as FertSumMfCost FROM processcosttable_Copy "
				+ "WHERE ClosingMon = ? AND CostingLev = ? GROUP BY WorkOrd";
		
		PreparedStatement FindLinePstmt = null;
		ResultSet FindLineRs = null;
		
		PreparedStatement LineUpdatePstmt = null;
		
		PreparedStatement SumQrtPstmt = null;
		ResultSet SumQtyRs = null;
		
		PreparedStatement CalcAmtPstmt = null;
		ResultSet CalcAmtRs = null;
		
		PreparedStatement AmtUpdatePstmt = null;
		
		ResultSet ChkSumRs = null;
		PreparedStatement ChkSumPstmt = null;
		
		PreparedStatement EditLineAmtPstmt = null;
		
		PreparedStatement EditLineAmtOhcPstmt = null;
		try {
			FindLv1Pstmt = conn.prepareStatement(FindLv1Data);
			FindLv1Pstmt.setString(1, Cm);
			FindLv1Pstmt.setInt(2, CalcLevel);
			FindLv1Rs = FindLv1Pstmt.executeQuery();
			
			BigDecimal FertMatCost = BigDecimal.ZERO;
			BigDecimal FertManufCost = BigDecimal.ZERO;
			String WoOrd = null;
			while(FindLv1Rs.next()) {
				WoOrd = FindLv1Rs.getString("WorkOrd");
				FertMatCost = FindLv1Rs.getBigDecimal("FertSumMCost");
				FertManufCost = FindLv1Rs.getBigDecimal("FertSumMfCost");
				int Ct = 0;
				BigDecimal SumQty = BigDecimal.ZERO;
				BigDecimal Qty = BigDecimal.ZERO;
				
				BigDecimal EditedAmt = BigDecimal.ZERO;
				BigDecimal EditedAmtOhc = BigDecimal.ZERO;
				
				BigDecimal ChkSumAmt = BigDecimal.ZERO;
				BigDecimal ChkSumAmtOhc = BigDecimal.ZERO;
				String EditKeyvalue = null;
				
				BigDecimal gapAmt = BigDecimal.ZERO;
				BigDecimal gapAmtOhc = BigDecimal.ZERO;
				
				String FindLineItem = "SELECT COUNT(*) as OrdCount FROM InvenLogl_Copy WHERE workordnum = ? AND movetype = ?";
				FindLinePstmt = conn.prepareStatement(FindLineItem);
				FindLinePstmt.setString(1, WoOrd);
				FindLinePstmt.setString(2, "GR11");
				FindLineRs = FindLinePstmt.executeQuery();
				if(FindLineRs.next()) {
					Ct = FindLineRs.getInt("OrdCount");
					if(Ct == 1) {
						String LineUpdate = "UPDATE InvenLogl_Copy SET amount = ?, amtOhC = ? WHERE workordnum = ?";
						LineUpdatePstmt = conn.prepareStatement(LineUpdate);
						LineUpdatePstmt.setBigDecimal(1, FertMatCost);
						LineUpdatePstmt.setBigDecimal(2, FertManufCost);
						LineUpdatePstmt.setString(3, WoOrd);
						LineUpdatePstmt.executeUpdate();
					}else {
						String SumQtySql = "SELECT SUM(quantity) as SumOfQty FROM invenlogl_copy WHERE movetype = ? AND workordnum = ?";
						SumQrtPstmt = conn.prepareStatement(SumQtySql);
						SumQrtPstmt.setString(1, "GR11");
						SumQrtPstmt.setString(2, WoOrd);
						SumQtyRs = SumQrtPstmt.executeQuery();
						if(SumQtyRs.next()) {
							SumQty = SumQtyRs.getBigDecimal("SumOfQty");
						}
						String CalcAmt = "SELECT * FROM invenlogl_copy WHERE movetype = ? AND workordnum = ?";
						CalcAmtPstmt = conn.prepareStatement(CalcAmt);
						CalcAmtPstmt.setString(1, "GR11");
						CalcAmtPstmt.setString(2, WoOrd);
						CalcAmtRs = CalcAmtPstmt.executeQuery();
						while(CalcAmtRs.next()) {
							String KeyVal = CalcAmtRs.getString("keyvalue");
							Qty = CalcAmtRs.getBigDecimal("quantity");
							EditedAmt = FertMatCost.multiply(Qty.divide(SumQty, 10, RoundingMode.HALF_UP)).setScale(0, RoundingMode.HALF_UP);
							EditedAmtOhc = FertManufCost.multiply(Qty.divide(SumQty, 10, RoundingMode.HALF_UP)).setScale(0, RoundingMode.HALF_UP);
							
							String AmtUpdate = "UPDATE invenlogl_copy SET amount = ?, amtOhC = ? WHERE keyvalue = ?";
							AmtUpdatePstmt = conn.prepareStatement(AmtUpdate);
							AmtUpdatePstmt.setBigDecimal(1, EditedAmt);
							AmtUpdatePstmt.setBigDecimal(2, EditedAmtOhc);
							AmtUpdatePstmt.setString(3, KeyVal);
							AmtUpdatePstmt.executeUpdate();
						} // 내일 위의 코드 실행해서 문제점 찾고 수정 후 아래 수정
						
						String ChkSumSql = "SELECT SUM(amount) AS SUMAMT, SUM(amtOhC) AS SUMAMTOHC, keyvalue FROM invenlogl_copy WHERE movetype = ? AND workordnum = ?";
						ChkSumPstmt = conn.prepareStatement(ChkSumSql);
						ChkSumPstmt.setString(1, "GR11");
						ChkSumPstmt.setString(2, WoOrd);
						ChkSumRs = ChkSumPstmt.executeQuery();
						if(ChkSumRs.next()) {
							ChkSumAmt = ChkSumRs.getBigDecimal("SUMAMT");
							if (ChkSumAmt == null) ChkSumAmt = BigDecimal.ZERO;
							ChkSumAmtOhc = ChkSumRs.getBigDecimal("SUMAMTOHC");
							if (ChkSumAmtOhc == null) ChkSumAmtOhc = BigDecimal.ZERO;
							EditKeyvalue = ChkSumRs.getString("keyvalue");
						}
						gapAmt = FertMatCost.subtract(ChkSumAmt);
						
						gapAmtOhc = FertManufCost.subtract(ChkSumAmtOhc);
						
						int gapAmtState = gapAmt.compareTo(BigDecimal.ZERO);
						int gapAmtOhcState = gapAmtOhc.compareTo(BigDecimal.ZERO);
						
						String EditLineItemAmt = null;
						String EditLineItemAmtOhc = null;
						
						if(gapAmtState > 0) {
							EditLineItemAmt = "UPDATE invenlogl_copy SET amount = (amount + ?) WHERE movetype = ? AND keyvalue = ?";
							EditLineAmtPstmt = conn.prepareStatement(EditLineItemAmt);
						}else {
							EditLineItemAmt = "UPDATE invenlogl_copy SET amount = (amount - ?) WHERE movetype = ? AND keyvalue = ?";
							EditLineAmtPstmt = conn.prepareStatement(EditLineItemAmt);
						}
						EditLineAmtPstmt.setBigDecimal(1, gapAmt.abs());
						EditLineAmtPstmt.setString(2, "GR11");
						EditLineAmtPstmt.setString(3, EditKeyvalue);
						EditLineAmtPstmt.executeUpdate();
						if(gapAmtOhcState > 0) {
							EditLineItemAmtOhc = "UPDATE invenlogl_copy SET amtOhC = (amtOhC + ?) WHERE movetype = ? AND keyvalue = ?";
							EditLineAmtOhcPstmt = conn.prepareStatement(EditLineItemAmtOhc);
						}else {
							EditLineItemAmtOhc = "UPDATE invenlogl_copy SET amtOhC = (amtOhC - ?) WHERE movetype = ? AND keyvalue = ?";
							EditLineAmtOhcPstmt = conn.prepareStatement(EditLineItemAmtOhc);
						}
						EditLineAmtOhcPstmt.setBigDecimal(1, gapAmtOhc.abs());
						EditLineAmtOhcPstmt.setString(2, "GR11");
						EditLineAmtOhcPstmt.setString(3, EditKeyvalue);
						EditLineAmtOhcPstmt.executeUpdate();
					}
				}
			}
		}catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
			System.err.println("❌ CProcess 두 번째 SQL 오류: " + e.getMessage());
			return "fail";
		}catch (NullPointerException e) {
		    e.printStackTrace();
		    System.err.println("❌ NullPointer 오류: PreparedStatement 미생성 등 확인 필요");
		    return "fail";
		} catch (Exception e) {
		    e.printStackTrace();
		    System.err.println("❌ 알 수 없는 오류: " + e.getMessage());
		    return "fail";
		}finally {
			if(FindLineRs != null) try { FindLineRs.close(); } catch(SQLException e) {}
			if(FindLinePstmt != null) try { FindLinePstmt.close(); } catch(SQLException e) {}
			if(LineUpdatePstmt != null) try { LineUpdatePstmt.close(); } catch(SQLException e) {}
			if(SumQtyRs != null) try { SumQtyRs.close(); } catch(SQLException e) {}
			if(SumQrtPstmt != null) try { SumQrtPstmt.close(); } catch(SQLException e) {}
			if(CalcAmtRs != null) try { CalcAmtRs.close(); } catch(SQLException e) {}
			if(CalcAmtPstmt != null) try { CalcAmtPstmt.close(); } catch(SQLException e) {}
			if(AmtUpdatePstmt != null) try { AmtUpdatePstmt.close(); } catch(SQLException e) {}
			
			if(ChkSumRs != null) try { ChkSumRs.close(); } catch(SQLException e) {}
			if(ChkSumPstmt != null) try { ChkSumPstmt.close(); } catch(SQLException e) {}
			if(EditLineAmtPstmt != null) try { EditLineAmtPstmt.close(); } catch(SQLException e) {}
			if(EditLineAmtOhcPstmt != null) try { EditLineAmtOhcPstmt.close(); } catch(SQLException e) {}
		}
		
		String FindItem = "SELECT * FROM productcost WHERE closingmon = ?";
		
		PreparedStatement FindPstmt = null;
		ResultSet FindRs = null;
		
		PreparedStatement FindMatPstmt = null;
		ResultSet FindMatRs = null;
		
		PreparedStatement UpdateMatPstmt = null;
	
		BigDecimal SumOfAmt = BigDecimal.ZERO;
		BigDecimal SumOfAmtOhc = BigDecimal.ZERO;
		String MatCode = null;
		String MvType = null;
		int ItemLv = 0;
		BigDecimal BsQty = BigDecimal.ZERO;
		BigDecimal BSMatC = BigDecimal.ZERO;
		BigDecimal BSExpC = BigDecimal.ZERO;
		BigDecimal GRQty = BigDecimal.ZERO;
		BigDecimal GiQty = BigDecimal.ZERO;
		BigDecimal GiMatC = BigDecimal.ZERO;
		BigDecimal GiExpC = BigDecimal.ZERO;
		BigDecimal ESQty = BigDecimal.ZERO;
		BigDecimal ESMatC = BigDecimal.ZERO;
		BigDecimal ESExpC = BigDecimal.ZERO;
		BigDecimal UnitMatPrice = BigDecimal.ZERO;
		BigDecimal UnitManPrice = BigDecimal.ZERO;
		try {
			FindPstmt = conn.prepareStatement(FindItem);
			FindPstmt.setString(1, Cm);
			FindRs = FindPstmt.executeQuery();
			while(FindRs.next()) {
				SumOfAmt = BigDecimal.ZERO;
				SumOfAmtOhc = BigDecimal.ZERO;
				MatCode = null;
				MvType = null;
				ItemLv = 0;
				BsQty = BigDecimal.ZERO;
				BSMatC = BigDecimal.ZERO;
				BSExpC = BigDecimal.ZERO;
				GRQty = BigDecimal.ZERO;
				GiQty = BigDecimal.ZERO;
				GiMatC = BigDecimal.ZERO;
				GiExpC = BigDecimal.ZERO;
				ESQty = BigDecimal.ZERO;
				ESMatC = BigDecimal.ZERO;
				ESExpC = BigDecimal.ZERO;
				UnitMatPrice = BigDecimal.ZERO;
				UnitManPrice = BigDecimal.ZERO;
				
				MatCode = FindRs.getString("matcode");
				
				String FindMatData = "SELECT "
						+ "CASE "
						+ "WHEN movetype LIKE 'GI%' THEN 'GI' "
						+ "WHEN movetype LIKE 'GR%' THEN 'GR' "
						+ "ELSE movetype "
						+ "END AS movetype_group, "
						+ "matcode, "
						+ "CostingLv, "
						+ "matdesc, "
						+ "SUM(quantity) AS SumOQty, "
						+ "SUM(amount) AS SumOfAmt, "
						+ "SUM(amtOhc) AS SumOfAmtOhc "
						+ "FROM invenlogl_copy "
						+ "WHERE mattype <> 'RAWM' AND matcode = ? "
						+ "AND (movetype LIKE 'GI%' OR movetype LIKE 'GR%') "
						+ "GROUP BY movetype_group, matcode "
						+ "ORDER BY movetype_group DESC";
				FindMatPstmt = conn.prepareStatement(FindMatData);
				FindMatPstmt.setString(1, MatCode);
				FindMatRs = FindMatPstmt.executeQuery();
				if(FindMatRs.next()) {
					MvType = FindMatRs.getString("movetype_group");
					ItemLv = FindMatRs.getInt("CostingLv");
					BsQty = FindRs.getBigDecimal("BS_Qty");
					BSMatC = FindRs.getBigDecimal("BS_MatC");
					BSExpC = FindRs.getBigDecimal("BS_ExpC");
					if(MvType.equals("GR") && ItemLv == 1) {
						SumOfAmt = FindMatRs.getBigDecimal("SumOfAmt");
						SumOfAmtOhc = FindMatRs.getBigDecimal("SumOfAmtOhc");
					}else if(MvType.equals("GI") && ItemLv == 1) {
						SumOfAmt = BigDecimal.ZERO;
						SumOfAmtOhc = BigDecimal.ZERO;
					}if(ItemLv >= 2) {
						continue;
					}
					GRQty = FindRs.getBigDecimal("GR_Qty");
					GiQty = FindRs.getBigDecimal("Gi_Qty");
					ESQty = FindRs.getBigDecimal("ES_Qty");
					
					UnitMatPrice = (BSMatC.add(SumOfAmt)).divide(BsQty.add(GRQty), 10, RoundingMode.HALF_UP); // 기말재고단가 재료비
					UnitManPrice = (BSExpC.add(SumOfAmtOhc)).divide(BsQty.add(GRQty), 10, RoundingMode.HALF_UP); // 기말재고단가 경비
					
					ESMatC = ESQty.multiply(UnitMatPrice).setScale(0, RoundingMode.HALF_UP); // 기말재고 재료비
					ESExpC = ESQty.multiply(UnitManPrice).setScale(0, RoundingMode.HALF_UP); // 기말재고 경비
					
					GiMatC = BSMatC.add(SumOfAmt).subtract(ESMatC); // 출고 재료비
					GiExpC = BSExpC.add(SumOfAmtOhc).subtract(ESExpC); // 출고 경비
					
					String UpdateMatItem = "UPDATE productcost SET GR_MatC = ?, GR_ExpC = ?, Gi_MatC = ?, Gi_ExpC = ?, ES_MatC = ?, ES_ExpC = ? WHERE "
							+ "closingmon = ? AND matcode = ?";
					UpdateMatPstmt = conn.prepareStatement(UpdateMatItem);
					UpdateMatPstmt.setBigDecimal(1, SumOfAmt);
					UpdateMatPstmt.setBigDecimal(2, SumOfAmtOhc);
					UpdateMatPstmt.setBigDecimal(3, GiMatC);
					UpdateMatPstmt.setBigDecimal(4, GiExpC);
					UpdateMatPstmt.setBigDecimal(5, ESMatC);
					UpdateMatPstmt.setBigDecimal(6, ESExpC);
					UpdateMatPstmt.setString(7, Cm);
					UpdateMatPstmt.setString(8, MatCode);
					UpdateMatPstmt.executeUpdate();
				}else {
					BsQty = FindRs.getBigDecimal("BS_Qty");
					BSMatC = FindRs.getBigDecimal("BS_MatC");
					BSExpC = FindRs.getBigDecimal("BS_ExpC");
					ESQty = FindRs.getBigDecimal("ES_Qty");
					
					UnitMatPrice = BSMatC.divide(BsQty, 10, RoundingMode.HALF_UP); // 기말재고단가 재료비
					UnitManPrice = BSExpC.divide(BsQty, 10, RoundingMode.HALF_UP); // 기말재고단가 경비
					
					ESMatC = ESQty.multiply(UnitMatPrice).setScale(0, RoundingMode.HALF_UP); // 기말재고 재료비
					ESExpC = ESQty.multiply(UnitManPrice).setScale(0, RoundingMode.HALF_UP); // 기말재고 경비
					
					String UpdateMatItem = "UPDATE productcost SET GR_MatC = ?, GR_ExpC = ?, Gi_MatC = ?, Gi_ExpC = ?, ES_MatC = ?, ES_ExpC = ? WHERE "
							+ "closingmon = ? AND matcode = ?";
					UpdateMatPstmt = conn.prepareStatement(UpdateMatItem);
					UpdateMatPstmt.setBigDecimal(1, BigDecimal.ZERO);
					UpdateMatPstmt.setBigDecimal(2, BigDecimal.ZERO);
					UpdateMatPstmt.setBigDecimal(3, BigDecimal.ZERO);
					UpdateMatPstmt.setBigDecimal(4, BigDecimal.ZERO);
					UpdateMatPstmt.setBigDecimal(5, ESMatC);
					UpdateMatPstmt.setBigDecimal(6, ESExpC);
					UpdateMatPstmt.setString(7, Cm);
					UpdateMatPstmt.setString(8, MatCode);
					UpdateMatPstmt.executeUpdate();
					System.out.println("2 : " + MatCode + " : " + UnitMatPrice + " : " + UnitManPrice);
					System.out.println("2 : " + MatCode + " : " + ESMatC + " : " + ESExpC);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("❌ CProcess 세 번째 SQL 오류: " + e.getMessage());
			return "fail";
		}catch (NullPointerException e) {
		    e.printStackTrace();
		    System.err.println("❌ NullPointer 오류: PreparedStatement 미생성 등 확인 필요");
		    return "fail";
		} catch (Exception e) {
		    e.printStackTrace();
		    System.err.println("❌ 알 수 없는 오류: " + e.getMessage());
		    return "fail";
		}finally {
			if(FindRs != null) try { FindRs.close(); } catch(SQLException e) {}
			if(FindPstmt != null) try { FindPstmt.close(); } catch(SQLException e) {}
			if(FindMatRs != null) try { FindMatRs.close(); } catch(SQLException e) {}
			if(FindMatPstmt != null) try { FindMatPstmt.close(); } catch(SQLException e) {}
			if(UpdateMatPstmt != null) try { UpdateMatPstmt.close(); } catch(SQLException e) {}
		}
		return "success";
	}

	private String AProcess(String ComCode, String PlantCode, String CalcMon) {
		connDB();
		// TODO Auto-generated method stub
		this.Cd = ComCode;
		this.Pd = PlantCode;
		this.Cm = CalcMon;
		
		BigDecimal OP30InputQty = BigDecimal.ZERO;
		BigDecimal InputQty = BigDecimal.ZERO;
		int Month = 0;
		
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
		LocalDate date = LocalDate.parse(Cm + "01", DateTimeFormatter.ofPattern("yyyyMMdd")); // 20020401의 형태로 변경해준
		
		Month = date.getMonthValue();
		LocalDate PastDate = date.minusMonths(1); // 한 달을 감소 -> 20020301로 변경
		String CalcPastMon = PastDate.format(formatter); // 200203의 형태로 변경
		
		String PastMonDataSearch = "SELECT * FROM processcosttable_Copy WHERE ComCode = ? AND PlantCode = ? AND ClosingMon = ?";
		PreparedStatement PastMonDataPstmt = null;
		ResultSet PastData = null;
		
		PreparedStatement TransferPstmt = null;
		
		PreparedStatement RenewPstmt = null;
		ResultSet RenewRs = null;
		
		PreparedStatement FindOP30Pstmt = null;
		ResultSet findOP30Rs = null;
		
		PreparedStatement UpdatePstmt = null;
		try {
			PastMonDataPstmt = conn.prepareStatement(PastMonDataSearch);
			PastMonDataPstmt.setString(1, Cd);
			PastMonDataPstmt.setString(2, Pd);
			PastMonDataPstmt.setString(3, CalcPastMon);
			PastData = PastMonDataPstmt.executeQuery();
			if(PastData.next()) {
				String TransferData = "INSERT INTO processcosttable_Copy (ComCode, PlantCode, ClosingMon, WorkOrd, WorkType, ManufLot, ManufCode, "
						+ "ManufDesc, CostingLev, WorkSeq, ProcessCode, ProcessDesc, InOutType, InputQty, ProdQty, WipQty, MixTime, PackStartMon, " 
						+ "PackClosMon, RawMatCost, HalbMatCost, OthMatCost, MatCostSum, ManufCost, HalbManufCost, ManufCostSum, WipMatCost, WipMnaufCost, "
						+ "FertMatCost, FertManufCost, KeyValue) "
						+ "SELECT ?, ?, ?, P.WorkOrd, P.WorkType, P.ManufLot, P.ManufCode, " // 3개(1,2,3)
						+ "P.ManufDesc, P.CostingLev, P.WorkSeq, P.ProcessCode, P.ProcessDesc, ?, P.WipQty, ?, ?, P.MixTime, P.PackStartMon, " // 3개(4,5,6)
						+ "P.PackClosMon, P.WipMatCost, ?, ?, P.WipMatCost, P.WipMnaufCost, ?, P.WipMnaufCost, ?, ?, " // 3개
						+ "P.WipMatCost, P.WipMnaufCost, CONCAT(?, P.WorkOrd, P.ProcessCode, ?) " // 2개
						+ "FROM processcosttable_Copy AS P "
						+ "WHERE ComCode = ? AND PlantCode = ? AND ClosingMon = ?";
				TransferPstmt = conn.prepareStatement(TransferData);
				TransferPstmt.setString(1, Cd);
				TransferPstmt.setString(2, Pd);
				TransferPstmt.setString(3, Cm);
				TransferPstmt.setString(4, "BW"); // 13
				TransferPstmt.setBigDecimal(5, BigDecimal.ZERO);
				TransferPstmt.setBigDecimal(6, BigDecimal.ZERO);
				TransferPstmt.setBigDecimal(7, BigDecimal.ZERO);
				TransferPstmt.setBigDecimal(8, BigDecimal.ZERO);
				TransferPstmt.setBigDecimal(9, BigDecimal.ZERO);
				TransferPstmt.setBigDecimal(10, BigDecimal.ZERO);
				TransferPstmt.setBigDecimal(11, BigDecimal.ZERO);
				TransferPstmt.setString(12, Cm);
				TransferPstmt.setString(13, "BW");
				TransferPstmt.setString(14, Cd);
				TransferPstmt.setString(15, Pd);
				TransferPstmt.setString(16, CalcPastMon);
				TransferPstmt.executeUpdate();
			}
			String RenewSql = "SELECT * FROM processcosttable_Copy WHERE ClosingMon = ? AND InOutType = ? AND ProcessCode <> ?";
			/*
			 * | 단일 값 제외 |`<>` | SQL 표준, 가장 안전 | 
			 * | 여러 값 제외 | `NOT IN (?, ?, ?)` | 여러 코드 제외 가능 | 
			 * |`NULL`도 포함해서 제외 | `(ProcessCode IS NULL OR ProcessCode <> ?)` | `NULL` 안전 |
			 */
			RenewPstmt = conn.prepareStatement(RenewSql);
			RenewPstmt.setString(1, Cm);
			RenewPstmt.setString(2, "BW");
			RenewPstmt.setString(3, "OP30");
			RenewRs = RenewPstmt.executeQuery();
			while(RenewRs.next()) {
				String WorkOrd = RenewRs.getString("WorkOrd");
				InputQty = RenewRs.getBigDecimal("InputQty");
				String KeyValue = RenewRs.getString("KeyValue");

				
				String FindOP30 = "SELECT * FROM processcosttable_Copy WHERE ClosingMon = ? AND InOutType = ? AND ProcessCode = ? AND WorkOrd = ?";
				FindOP30Pstmt = conn.prepareStatement(FindOP30);
				FindOP30Pstmt.setString(1, Cm);
				FindOP30Pstmt.setString(2, "OC");
				FindOP30Pstmt.setString(3, "OP30");
				FindOP30Pstmt.setString(4, WorkOrd);
				findOP30Rs = FindOP30Pstmt.executeQuery();
				if(findOP30Rs.next()) {
					OP30InputQty = findOP30Rs.getBigDecimal("InputQty");
				}
				String UpdateSql = "UPDATE processcosttable_Copy SET ProdQty = ?, WipQty = ? WHERE KeyValue = ?";
				UpdatePstmt = conn.prepareStatement(UpdateSql);
				UpdatePstmt.setBigDecimal(1, OP30InputQty);
				if(RenewRs.getInt("PackClosMon") == Month) {
					UpdatePstmt.setBigDecimal(2, BigDecimal.ZERO);
				}else if(RenewRs.getInt("PackClosMon") > Month){
					UpdatePstmt.setBigDecimal(2, InputQty.subtract(OP30InputQty));
				}else {
					UpdatePstmt.setBigDecimal(2, BigDecimal.ZERO);
				}
				UpdatePstmt.setString(3, KeyValue);
				UpdatePstmt.executeUpdate();
			}
		}catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
			 return "error_A01";
		}catch (NullPointerException e) {
		    e.printStackTrace();
		    System.err.println("❌ NullPointer 오류: PreparedStatement 미생성 등 확인 필요");
		    return "fail";
		} catch (Exception e) {
		    e.printStackTrace();
		    System.err.println("❌ 알 수 없는 오류: " + e.getMessage());
		    return "fail";
		}finally{
			if(PastData != null) try { PastData.close(); } catch(SQLException e) {}
			if(PastMonDataPstmt != null) try { PastMonDataPstmt.close(); } catch(SQLException e) {}
			if(RenewRs != null) try { RenewRs.close(); } catch(SQLException e) {}
			if(RenewPstmt != null) try { RenewPstmt.close(); } catch(SQLException e) {}
			if(findOP30Rs != null) try { findOP30Rs.close(); } catch(SQLException e) {}
			if(FindOP30Pstmt != null) try { FindOP30Pstmt.close(); } catch(SQLException e) {}
			if(UpdatePstmt != null) try { UpdatePstmt.close(); } catch(SQLException e) {}
		}// A01 ~ A02 process

		String Line_ProductDataCalc = "SELECT "
		        + "CASE "
		        + "WHEN movetype LIKE 'GI%' THEN 'GI' "
		        + "WHEN movetype LIKE 'GR%' THEN 'GR' "
		        + "ELSE movetype "
		        + "END AS movetype_group, "
		        + "matcode, matdesc, spec, mattype, SUM(quantity) AS total_qty "
		        + "FROM invenlogl_copy "
		        + "WHERE mattype != 'RAWM' "
		        + "AND (movetype LIKE 'GI%' OR movetype LIKE 'GR%') "
		        + "GROUP BY matcode, mattype, movetype_group, matdesc, spec "
		        + "ORDER BY matcode ASC";
		
		PreparedStatement Line_ProDataCalc_pstmt = null;
		ResultSet Line_ProDataCalc_rs = null;
		try {
			Line_ProDataCalc_pstmt = conn.prepareStatement(Line_ProductDataCalc);
			Line_ProDataCalc_rs = Line_ProDataCalc_pstmt.executeQuery();
			
			while(Line_ProDataCalc_rs.next()) {
				 String movetypeGroup = Line_ProDataCalc_rs.getString("movetype_group");
			     String matcode = Line_ProDataCalc_rs.getString("matcode");
			     String mattype = Line_ProDataCalc_rs.getString("mattype");
			     String matdesc = Line_ProDataCalc_rs.getString("matdesc");
			     String spec = Line_ProDataCalc_rs.getString("spec");
			     
			     BigDecimal totalQty = Line_ProDataCalc_rs.getBigDecimal("total_qty");
			     if(totalQty == null) totalQty = BigDecimal.ZERO;
			     
			     String KeyValue = Cm + matcode + mattype;
			     
			     PreparedStatement PDS = null;
			     ResultSet PDS_Rs = null;
			     PreparedStatement PDU = null;
			     PreparedStatement PDI = null;
			     try {
			    	 String ProductDataSelect = "SELECT * FROM productcost WHERE KeyVal = ?";
			    	 PDS = conn.prepareStatement(ProductDataSelect);
			    	 PDS.setString(1, KeyValue);
			    	 PDS_Rs = PDS.executeQuery();

			    	 if(PDS_Rs.next()) {
			    		 BigDecimal SavedQty = movetypeGroup.equals("GR") ? PDS_Rs.getBigDecimal("GR_Qty") : PDS_Rs.getBigDecimal("Gi_Qty");
			    		 String updateCol = movetypeGroup.equals("GR") ? "GR_Qty" : "Gi_Qty";
			    		 String ProductDataUpdate = "UPDATE productcost SET " + updateCol + " = ? WHERE KeyVal = ?";
			    		 PDU = conn.prepareStatement(ProductDataUpdate);
			    		 PDU.setBigDecimal(1, totalQty.add(SavedQty));
			    		 PDU.setString(2, KeyValue);
			    		 PDU.executeUpdate(); // 🔹 executeUpdate 추가
			    	 } else {
			    		 String insertCol = movetypeGroup.equals("GR") ? "GR_Qty" : "Gi_Qty";
			             String ProductDataInsert = "INSERT INTO productcost (closingmon, comcode, plant, matcode, matdesc, spec, matType, "
			            		 + insertCol + ", KeyVal) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
			             PDI = conn.prepareStatement(ProductDataInsert);
			             PDI.setString(1, Cm);
			             PDI.setString(2, Cd);
			             PDI.setString(3, Pd);
			             PDI.setString(4, matcode);
			             PDI.setString(5, matdesc);
			             PDI.setString(6, spec);
			             PDI.setString(7, mattype);
			             PDI.setBigDecimal(8, totalQty);
			             PDI.setString(9, KeyValue);
			             PDI.executeUpdate();
			    	 }
			     } catch(SQLException e) {
			    	 e.printStackTrace();
			    	 System.err.println("Error for matcode: " + matcode + ", movetype_group: " + movetypeGroup);
			     }catch (NullPointerException e) {
					    e.printStackTrace();
					    System.err.println("❌ NullPointer 오류: PreparedStatement 미생성 등 확인 필요");
					    return "fail";
			     } catch (Exception e) {
					    e.printStackTrace();
					    System.err.println("❌ 알 수 없는 오류: " + e.getMessage());
					    return "fail";
			     }finally {
			    	 if(PDS_Rs != null) try { PDS_Rs.close(); } catch(SQLException e) {}
			    	 if(PDS != null) try { PDS.close(); } catch(SQLException e) {}
			    	 if(PDU != null) try { PDU.close(); } catch(SQLException e) {}
			    	 if(PDI != null) try { PDI.close(); } catch(SQLException e) {}
			     }
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "error_A02";
		} catch (NullPointerException e) {
		    e.printStackTrace();
		    System.err.println("❌ NullPointer 오류: PreparedStatement 미생성 등 확인 필요");
		    return "fail";
		} catch (Exception e) {
		    e.printStackTrace();
		    System.err.println("❌ 알 수 없는 오류: " + e.getMessage());
		    return "fail";
		} finally {
			if(Line_ProDataCalc_rs != null) try { Line_ProDataCalc_rs.close(); } catch(SQLException e) {}
		    if(Line_ProDataCalc_pstmt != null) try { Line_ProDataCalc_pstmt.close(); } catch(SQLException e) {}
		} // A03 프로세스 끝
		
		String DataMatchSql = "SELECT * FROM productcost WHERE closingmon = ? AND ES_Qty > 0";
		PreparedStatement DataMatchPstmt = null;
		ResultSet DataMatchRs = null;
		try {
			DataMatchPstmt = conn.prepareStatement(DataMatchSql);
			DataMatchPstmt.setString(1, CalcPastMon);
			DataMatchRs = DataMatchPstmt.executeQuery();
			while(DataMatchRs.next()) {
				String MatCode = DataMatchRs.getString("matcode");
				String MatType = DataMatchRs.getString("matType");
				String KeyValue = Cm + MatCode + MatType;
				
				BigDecimal EsQty = DataMatchRs.getBigDecimal("ES_Qty");
				BigDecimal EsMatC = DataMatchRs.getBigDecimal("ES_MatC");
				BigDecimal EsLabC = DataMatchRs.getBigDecimal("ES_LabC");
				BigDecimal EsExpC = DataMatchRs.getBigDecimal("ES_ExpC");
				
				PreparedStatement SelectSqlPstmt = null;
				ResultSet SelectSqlRs = null;
				PreparedStatement UpdateBsPstmt = null;
				PreparedStatement InsertBsPstmt = null;
				try {
					String SelectSql = "SELECT * FROM productcost WHERE KeyVal = ?";
					SelectSqlPstmt = conn.prepareStatement(SelectSql);
					SelectSqlPstmt.setString(1, KeyValue);
					SelectSqlRs = SelectSqlPstmt.executeQuery();
					if(SelectSqlRs.next()) {
						BigDecimal Gr_Qty = SelectSqlRs.getBigDecimal("GR_Qty");
						BigDecimal Gi_Qty = SelectSqlRs.getBigDecimal("Gi_Qty");
						
						String UpdataBs = "UPDATE productcost SET BS_Qty = ?, BS_MatC = ?, BS_LabC = ?, BS_ExpC = ?, ES_Qty = ? WHERE KeyVal = ?";
						UpdateBsPstmt = conn.prepareStatement(UpdataBs);
						UpdateBsPstmt.setBigDecimal(1, EsQty);
						UpdateBsPstmt.setBigDecimal(2, EsMatC);
						UpdateBsPstmt.setBigDecimal(3, EsLabC);
						UpdateBsPstmt.setBigDecimal(4, EsExpC);
						UpdateBsPstmt.setBigDecimal(5, EsQty.add(Gr_Qty).subtract(Gi_Qty));
						UpdateBsPstmt.setString(6, KeyValue);
						UpdateBsPstmt.executeUpdate();
					}else {
						String InsertBs = "INSERT INTO productcost (closingmon, comcode, plant, matcode, matdesc, spec, matType, BS_Qty, BS_MatC, BS_LabC, BS_ExpC, "
								+ "ES_Qty, KeyVal) "
								+ "SELECT ?, ?, ?, PC.matcode, PC.matdesc, PC.spec, PC.matType, PC.ES_Qty, PC.ES_MatC, PC.ES_LabC, PC.ES_ExpC, "
								+ "PC.ES_Qty, CONCAT(?, PC.matcode, PC.matType) "
								+ "FROM productcost AS PC "
								+ "WHERE closingmon = ? AND matcode = ?";
						InsertBsPstmt = conn.prepareStatement(InsertBs);
						InsertBsPstmt.setString(1, Cm);
						InsertBsPstmt.setString(2, Cd);
						InsertBsPstmt.setString(3, Pd);
						InsertBsPstmt.setString(4, Cm);
						InsertBsPstmt.setString(5, CalcPastMon);
						InsertBsPstmt.setString(6, MatCode);
						InsertBsPstmt.executeUpdate();
					}
				}catch (SQLException e) {
					e.printStackTrace();
					System.err.println("Error processing matcode: " + MatCode);
				}finally {
					if(SelectSqlRs != null) try { SelectSqlRs.close(); } catch(SQLException e) {}
		            if(SelectSqlPstmt != null) try { SelectSqlPstmt.close(); } catch(SQLException e) {}
		            if(UpdateBsPstmt != null) try { UpdateBsPstmt.close(); } catch(SQLException e) {}
		            if(InsertBsPstmt != null) try { InsertBsPstmt.close(); } catch(SQLException e) {}
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
			 return "error_A03";
		}finally {
			if(DataMatchRs != null) try { DataMatchRs.close(); } catch(SQLException e) {}
		    if(DataMatchPstmt != null) try { DataMatchPstmt.close(); } catch(SQLException e) {}
		}
		return "success";
	}
}
