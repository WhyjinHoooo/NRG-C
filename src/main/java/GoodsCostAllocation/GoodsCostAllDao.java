package GoodsCostAllocation;

import java.math.BigDecimal;
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

			result = "Good";
		}catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return result;
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
						+ "P.PackClosMon, P.WipMatCost, ?, ?, P.WipMatCost, P.WipMnaufCost, ?, P.WipMnaufCost, P.WipMatCost, P.WipMnaufCost, " // 3개
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
				TransferPstmt.setString(10, Cm);
				TransferPstmt.setString(11, "BW");
				TransferPstmt.setString(12, Cd);
				TransferPstmt.setString(13, Pd);
				TransferPstmt.setString(14, CalcPastMon);
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
			    		 String updateCol = movetypeGroup.equals("GR") ? "GR_Qty" : "Gi_Qty";
			    		 String ProductDataUpdate = "UPDATE productcost SET " + updateCol + " = ? WHERE KeyVal = ?";
			    		 PDU = conn.prepareStatement(ProductDataUpdate);
			    		 PDU.setBigDecimal(1, totalQty);
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
			     } finally {
			    	 if(PDS_Rs != null) try { PDS_Rs.close(); } catch(SQLException e) {}
			    	 if(PDS != null) try { PDS.close(); } catch(SQLException e) {}
			    	 if(PDU != null) try { PDU.close(); } catch(SQLException e) {}
			    	 if(PDI != null) try { PDI.close(); } catch(SQLException e) {}
			     }
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return "error_A02";
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
						
						String UpdataBs = "UPDATE productcost SET BS_Qty = ?, BS_MatC = ?, BS_LabC = ?, BS_ExpC = ?, ES_Qty = ?, ES_MatC = ?, ES_LabC = ?, ES_ExpC = ? WHERE KeyVal = ?";
						UpdateBsPstmt = conn.prepareStatement(UpdataBs);
						UpdateBsPstmt.setBigDecimal(1, EsQty);
						UpdateBsPstmt.setBigDecimal(2, EsMatC);
						UpdateBsPstmt.setBigDecimal(3, EsLabC);
						UpdateBsPstmt.setBigDecimal(4, EsExpC);
						UpdateBsPstmt.setBigDecimal(5, EsQty.add(Gr_Qty).subtract(Gi_Qty));
						UpdateBsPstmt.setBigDecimal(6, EsMatC);
						UpdateBsPstmt.setBigDecimal(7, EsLabC);
						UpdateBsPstmt.setBigDecimal(8, EsExpC);
						UpdateBsPstmt.setString(9, KeyValue);
						UpdateBsPstmt.executeUpdate();
					}else {
						String InsertBs = "INSERT INTO productcost (closingmon, comcode, plant, matcode, matdesc, spec, matType, BS_Qty, BS_MatC, BS_LabC, BS_ExpC, "
								+ "ES_Qty, ES_MatC, ES_LabC, ES_ExpC, KeyVal) "
								+ "SELECT ?, ?, ?, PC.matcode, PC.matdesc, PC.spec, PC.matType, PC.ES_Qty, PC.ES_MatC, PC.ES_LabC, PC.ES_ExpC, "
								+ "PC.ES_Qty, PC.ES_MatC, PC.ES_LabC, PC.ES_ExpC, CONCAT(?, PC.matcode, PC.matType) "
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
