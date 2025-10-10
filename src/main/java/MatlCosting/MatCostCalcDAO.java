package MatlCosting;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.apache.tomcat.jni.Local;
import org.json.JSONArray;
import org.json.JSONObject;

public class MatCostCalcDAO {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public String CostCalcFun(String GetMEDate, String ComCode, String RealDate) {
		String result = "No";
		connDB();
		String CurrVal = GetMEDate; // 202501
		String ComCodeVal = ComCode;
		String RealDateVal = RealDate;
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMM");
			LocalDate date = LocalDate.parse(CurrVal+ "01", DateTimeFormatter.ofPattern("yyyyMMdd")); 
			
			LocalDate PastDate = date.minusMonths(1);
			String PastVal = PastDate.format(formatter);
			
			System.out.println(CurrVal + "," + PastVal + "," + ComCodeVal);
			
			String GKSql = "SELECT * FROM sumrawmamtKeeper WHERE KeyValue = ?";
			PreparedStatement GKPstmt = conn.prepareStatement(GKSql);
			GKPstmt.setString(1, CurrVal);
			ResultSet GKRs = GKPstmt.executeQuery();
			if(GKRs.next()) {
				result = "Impossible";
			}else {
				String CalcAdmin = RegitAdmin(CurrVal, RealDateVal, ComCodeVal);
				if(CalcAdmin.equals("Pos")) {
					System.out.println(CalcAdmin);
					String sql = "SELECT * FROM sumrawmamt WHERE closingMon = ? AND comcode = ?"; 
					pstmt = conn.prepareStatement(sql);
					pstmt.setString(1, PastVal);
					pstmt.setString(2, ComCodeVal);
					ResultSet rs = pstmt.executeQuery();
					while(rs.next()) { // 원래는 while
						String MCd = rs.getString("matcode");
						String MCdDes = rs.getString("matdesc");
						String MSpec = rs.getString("spec");
						BigDecimal MEsQty = rs.getBigDecimal("EndStocQty");
						BigDecimal MEsAmt = rs.getBigDecimal("EsAmt");
						RegitRawmAmt(PastVal, CurrVal, ComCodeVal, MCd, MCdDes, MSpec, MEsQty, MEsAmt); 
					}
					String GIUpdateResult = GICalcResult(CurrVal);
					if(GIUpdateResult.equals("Yes")) {
						result = "Yes";
					}else {
						result = "No";
					}
				}
			}
		} catch (SQLException e) {
	        e.printStackTrace();
	        System.out.println("SQL 실행 오류: " + e.getMessage());
	    }
		return result;
	}
	
	private void RegitRawmAmt(String PastDate, String CurrDate, String ComCode, String MCd, String MCdDes, String MSpec, BigDecimal MEsQty, BigDecimal MEsAmt) {
		BigDecimal SumTableBeginQty = BigDecimal.ZERO; // SumTable 기초수량
		BigDecimal BsQty = BigDecimal.ZERO; // 기초수량
		BigDecimal BsAmt = BigDecimal.ZERO;
		BigDecimal GrTransacQty = BigDecimal.ZERO;  // 입고수량
		BigDecimal GrPurAmt = BigDecimal.ZERO;
		BigDecimal GrSubAmt = BigDecimal.ZERO;
		BigDecimal GrSumAmt = BigDecimal.ZERO; // 최종 입고 금액
		BigDecimal GrTransferQty = BigDecimal.ZERO; // 이동입고 수량
		BigDecimal GiTransferQty = BigDecimal.ZERO; // 이동출고 수량	
		BigDecimal GiTransacQty = BigDecimal.ZERO; // 출고수량
		BigDecimal GiAmt = BigDecimal.ZERO; // 최종 출고 금액
		BigDecimal EndStocQty = BigDecimal.ZERO; // 기말 수량
		BigDecimal EsAmt = BigDecimal.ZERO;
		BigDecimal UnitPrice = BigDecimal.ZERO;
		
		String MatDataSearchSql = "SELECT "
			    + "s.closingMon AS s_closingMon, "
			    + "s.comcode AS s_comcode, "
			    + "s.matcode AS s_matcode, "
			    + "s.matdesc AS s_matdesc, "
			    + "s.mattype AS s_mattype, "
			    + "s.spec AS s_spec, "
			    + "SUM(s.beginStocqty) AS beginStocqty_sum, "
			    + "SUM(s.GrTransacQty) AS GrTransacQty_sum, "
			    + "SUM(s.GrTransferQty) AS GrTransferQty_sum, "
			    + "SUM(s.GiTransferQty) AS GiTransferQty_sum, "
			    + "SUM(s.GiTransacQty) AS GiTransacQty_sum, "
			    + "SUM(s.EndStocQty) AS EndStocQty_sum "
			    + "FROM sumtable s "
			    + "WHERE s.closingMon = ? "
			    + "AND s.comcode = ? "
			    + "AND s.matcode = ? "
			    + "AND s.matdesc = ? "
			    + "AND s.mattype = 'RAWM' "
			    + "GROUP BY "
			    + "s.closingMon, "
			    + "s.comcode, "
			    + "s.matcode, "
			    + "s.matdesc, "
			    + "s.mattype, "
			    + "s.spec"; 
		PreparedStatement MDSS_Pstmt = null;
		ResultSet MSDD_Rs = null;
		
		PreparedStatement Price_Pstmt = null;
		ResultSet Price_Rs = null;
		
		PreparedStatement GiQuantity_Pstmt = null;
		ResultSet GiQuantity_Rs = null;
		
		PreparedStatement Insert_Pstmt = null;
		try {
			MDSS_Pstmt =  conn.prepareStatement(MatDataSearchSql);
			MDSS_Pstmt.setString(1, CurrDate);
			MDSS_Pstmt.setString(2, ComCode);
			MDSS_Pstmt.setString(3, MCd);
			MDSS_Pstmt.setString(4, MCdDes);
			MSDD_Rs = MDSS_Pstmt.executeQuery();
			if(MSDD_Rs.next()) {
				SumTableBeginQty = MSDD_Rs.getBigDecimal("beginStocqty_sum"); // 수불수량합계테이블에서 가져온 결산하는 달의 기초 수량 202504
				GrTransacQty = MSDD_Rs.getBigDecimal("GrTransacQty_sum");
				BsQty = MEsQty; // 원자재원가테이블에서 가져온 결산전 달의 기말 수량 202503
				BsAmt = MEsAmt;
				
				// 입고금액 입력하는 부분
				String PriceSql = "SELECT movetype, closingmon, matcode, matdesc, spec, lotnum, mattype, quantity, SUM(amount) as SumAmount "
					+ "FROM InvenLogl WHERE closingmon = ? AND matcode = ? AND matdesc = ? AND LEFT(movetype, 2) = ? AND mattype = ? AND comcode = ? "
					+ "GROUP BY matcode, matdesc "
					+ "HAVING SUM(amount) > 0";
				Price_Pstmt = conn.prepareStatement(PriceSql);
				Price_Pstmt.setString(1, CurrDate);
				Price_Pstmt.setString(2, MCd);
				Price_Pstmt.setString(3, MCdDes);
				Price_Pstmt.setString(4, "GR");
				Price_Pstmt.setString(5, "RAWM");
				Price_Pstmt.setString(6, ComCode);
				Price_Rs = Price_Pstmt.executeQuery();
				if(Price_Rs.next()) {
					GrPurAmt = Price_Rs.getBigDecimal("SumAmount"); 
				}
				GrSumAmt = GrSubAmt.add(GrPurAmt);
				
				String GiQuantity_Sql = "SELECT movetype, closingmon, matcode, matdesc, spec, lotnum, mattype, SUM(quantity) as SumQuantity "
						+ "FROM InvenLogl "
						+ "WHERE closingmon = ? AND matcode = ? AND matdesc = ? AND LEFT(movetype, 2) = ? AND mattype = ? AND comcode = ? "
						+ "GROUP BY matcode, matdesc";
				GiQuantity_Pstmt = conn.prepareStatement(GiQuantity_Sql);
				GiQuantity_Pstmt.setString(1, CurrDate);
				GiQuantity_Pstmt.setString(2, MCd);
				GiQuantity_Pstmt.setString(3, MCdDes);
				GiQuantity_Pstmt.setString(4, "GI");
				GiQuantity_Pstmt.setString(5, "RAWM");
				GiQuantity_Pstmt.setString(6, ComCode);
				GiQuantity_Rs = GiQuantity_Pstmt.executeQuery();
				if(GiQuantity_Rs.next()) {
					GiTransacQty = GiQuantity_Rs.getBigDecimal("SumQuantity");
				}
				EndStocQty = BsQty.add(GrTransacQty).subtract(GiTransacQty);
				BigDecimal denominator = BsQty.add(GrTransacQty);
				if (denominator.compareTo(BigDecimal.ZERO) > 0) {
					UnitPrice = BsAmt.add(GrSumAmt).divide(denominator, 2, RoundingMode.HALF_UP);
				} else {
				    UnitPrice = BigDecimal.ZERO;
				}
				EsAmt = EndStocQty.multiply(UnitPrice).setScale(2, RoundingMode.HALF_UP);
				
				String InsertSql = "INSERT INTO sumrawmamt VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				Insert_Pstmt = conn.prepareStatement(InsertSql);
				Insert_Pstmt.setString(1, CurrDate);
				Insert_Pstmt.setString(2, ComCode);
				Insert_Pstmt.setString(3, MCd);
				Insert_Pstmt.setString(4, MCdDes.replace(",", "&"));
				Insert_Pstmt.setString(5, "RAWM");
				Insert_Pstmt.setString(6, MSpec);
				Insert_Pstmt.setBigDecimal(7, BsQty);
				Insert_Pstmt.setBigDecimal(8, BsAmt);
				Insert_Pstmt.setBigDecimal(9, GrTransacQty);
				Insert_Pstmt.setBigDecimal(10, GrPurAmt);
				Insert_Pstmt.setBigDecimal(11, GrSubAmt);
				Insert_Pstmt.setBigDecimal(12, GrSumAmt);
				Insert_Pstmt.setBigDecimal(13, GrTransferQty);
				Insert_Pstmt.setBigDecimal(14, GiTransferQty);
				Insert_Pstmt.setBigDecimal(15, GiTransacQty);
				
				BigDecimal diff = BsAmt.add(GrSumAmt).subtract(EsAmt);
				if (diff.compareTo(BigDecimal.ZERO) == 0) {
					if(GiTransacQty.compareTo(BigDecimal.ZERO) > 0) {
						Insert_Pstmt.setBigDecimal(16, BsAmt.add(GrSumAmt));
					}else {
						Insert_Pstmt.setBigDecimal(16, BigDecimal.ZERO);
					}
				} else {
				    Insert_Pstmt.setBigDecimal(16, diff);
				}
				Insert_Pstmt.setBigDecimal(17, EndStocQty);
				Insert_Pstmt.setBigDecimal(18, EsAmt);
				Insert_Pstmt.setBigDecimal(19, UnitPrice);
				if(BsQty.subtract(SumTableBeginQty).compareTo(BigDecimal.ZERO) == 0) { // 실수타입을 비교하는 코드 Double.compare(실수, 실수), '=='를 쓰면 같은 값이라도 오차가 발생할 수 있어 실수를 비교할 때 사용하면 안됨
					Insert_Pstmt.setString(20, "X"); // -> 문제 없음
				}else {
					System.out.println("자재코드 : " + MCd + "(" + MCdDes + "), " + BsQty + ", " + SumTableBeginQty);
					Insert_Pstmt.setString(20, "O"); // -> 문제 있음
				}
				Insert_Pstmt.executeUpdate();
			}
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		}finally {
			try {
	            if (MSDD_Rs != null) MSDD_Rs.close();
	            if (MDSS_Pstmt != null) MDSS_Pstmt.close();
	            if (Price_Rs != null) Price_Rs.close();
	            if (Price_Pstmt != null) Price_Pstmt.close();
	            if (GiQuantity_Rs != null) GiQuantity_Rs.close();
	            if (GiQuantity_Pstmt != null) GiQuantity_Pstmt.close();
	            if (Insert_Pstmt != null) Insert_Pstmt.close();
	        } catch (SQLException e2) {
	            e2.printStackTrace();
	        }
		}
	}

	private String RegitAdmin(String CurrDate, String RealDate, String ComCode) {
		// TODO Auto-generated method stub
		String GKRegSql = "INSERT INTO sumrawmamtKeeper VALUES(?,?,?,?)";
		PreparedStatement GKRegPstmt = null;
		String result = "Pos";
		try {
			GKRegPstmt = conn.prepareStatement(GKRegSql);
			GKRegPstmt.setString(1, CurrDate);
			GKRegPstmt.setString(2, RealDate);
			GKRegPstmt.setString(3, "2008S0001");
			GKRegPstmt.setString(4, ComCode);
			
			int rowsAffected = GKRegPstmt.executeUpdate();

	        if (rowsAffected > 0) {
	            result = "Pos";
	        } else {
	            System.out.println("⚠️ RegitAdmin 실패: 영향받은 행이 없습니다.");
	        }
		} catch (SQLException e) {
			System.out.println("❌ RegitAdmin 실행 중 오류 발생:");
	        e.printStackTrace();  // 콘솔에 자세한 스택 출력
	        result = "Neg";
		} finally {
	        // 자원 정리
	        if (GKRegPstmt != null) {
	            try {
	                GKRegPstmt.close();
	            } catch (SQLException e) {
	                System.out.println("⚠️ RegitAdmin: PreparedStatement 닫기 중 오류 발생");
	                e.printStackTrace();
	            }
	        }
	    }
		return result;
	}

	public String GICalcResult(String MonthDate) {
		System.out.println("시작");
		String Result = "No";
		String CurrDate = MonthDate; // 지금 날짜
		String MatCode = null; // 원자재 코드
		BigDecimal MatPrice = BigDecimal.ZERO; // SumRawmTalbe에 저장된 단가
		BigDecimal TotalGiAmt = BigDecimal.ZERO; // SumRawmTalbe에 저장된 최종 값
		BigDecimal TotalPrice = BigDecimal.ZERO;
		ResultSet rs = null;
		sql = "SELECT * FROM sumrawmamt WHERE closingMon = ?";
		PreparedStatement pstmt = null;
		
		PreparedStatement LineGiSearchSql_Pstmt = null;
		ResultSet LineGISearchSql_Pstmt_Rs = null;
		
		PreparedStatement LineGIUpdateSql_Pstmt = null;
		
		PreparedStatement RenewSearchSql_Pstmt = null;
		ResultSet RenewSearchSql_Pstmt_Rs = null;
		
		PreparedStatement RenewUpdateSql_Pstmt = null;
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, CurrDate);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				MatCode = rs.getString("matcode"); // 자재코드
				MatPrice = rs.getBigDecimal("UnitPrice"); // 단가
				TotalGiAmt = rs.getBigDecimal("GiAmt"); // SumRawmTalbe에 저장된 출고 값
				System.out.println("1. " + MatCode + " : " + TotalGiAmt + ", " + MatPrice);
				
				String LineGiSearchSql = "SELECT * FROM InvenLogl WHERE LEFT(movetype,2) = ? AND mattype = ? AND closingmon = ? AND matcode = ?";
				LineGiSearchSql_Pstmt = conn.prepareStatement(LineGiSearchSql);
				LineGiSearchSql_Pstmt.setString(1, "GI");
				LineGiSearchSql_Pstmt.setString(2, "RAWM");
				LineGiSearchSql_Pstmt.setString(3, CurrDate);
				LineGiSearchSql_Pstmt.setString(4, MatCode);
				LineGISearchSql_Pstmt_Rs = LineGiSearchSql_Pstmt.executeQuery();
				TotalPrice = BigDecimal.ZERO;
				while(LineGISearchSql_Pstmt_Rs.next()) {
					BigDecimal GiQty = LineGISearchSql_Pstmt_Rs.getBigDecimal("quantity");
					String GIKeyValue = LineGISearchSql_Pstmt_Rs.getString("keyvalue");
					BigDecimal GiAmount = GiQty.multiply(MatPrice); // SumRawmTalbe에 저장된 단가와 Line테이블에 있는 수량의 곱
					
					String LineGIUpdateSql = "UPDATE InvenLogl SET amount = ? WHERE closingmon = ? AND matcode = ? AND keyvalue = ?";
					LineGIUpdateSql_Pstmt = conn.prepareStatement(LineGIUpdateSql);
					LineGIUpdateSql_Pstmt.setBigDecimal(1, GiAmount);
					LineGIUpdateSql_Pstmt.setString(2, CurrDate);
					LineGIUpdateSql_Pstmt.setString(3, MatCode);
					LineGIUpdateSql_Pstmt.setString(4, GIKeyValue);
					LineGIUpdateSql_Pstmt.executeUpdate();
					TotalPrice = TotalPrice.add(GiAmount);
				}
				System.out.println("2. " + MatCode + " : " + TotalPrice);
				if(TotalGiAmt.subtract(TotalPrice).compareTo(BigDecimal.ZERO) == 0) {
					System.out.println("3-1. " + MatCode + " : " + TotalGiAmt.subtract(TotalPrice));
					Result = "Yes";
				}else {
					System.out.println("3-2. " + MatCode + " : " + TotalGiAmt.subtract(TotalPrice));
					BigDecimal Gap = TotalGiAmt.subtract(TotalPrice);
					BigDecimal AbsGap = Gap.abs();
					String RenewSearchSql = "SELECT * FROM InvenLogl WHERE LEFT(movetype,2) = ? AND mattype = ? AND closingmon = ? AND matcode = ?";
					RenewSearchSql_Pstmt = conn.prepareStatement(RenewSearchSql);
					RenewSearchSql_Pstmt.setString(1, "GI");
					RenewSearchSql_Pstmt.setString(2, "RAWM");
					RenewSearchSql_Pstmt.setString(3, CurrDate);
					RenewSearchSql_Pstmt.setString(4, MatCode);
					RenewSearchSql_Pstmt_Rs = RenewSearchSql_Pstmt.executeQuery();
					if(RenewSearchSql_Pstmt_Rs.next()) {
						String UpdateTgtKeyValue = RenewSearchSql_Pstmt_Rs.getString("keyvalue");
						BigDecimal pdateTgtAmount = RenewSearchSql_Pstmt_Rs.getBigDecimal("amount");
						String RenewUpdateSql = "UPDATE InvenLogl SET amount = ? WHERE closingmon = ? AND matcode = ? AND keyvalue = ?";
						RenewUpdateSql_Pstmt = conn.prepareStatement(RenewUpdateSql);
						if(Gap.signum() > 0) {
							RenewUpdateSql_Pstmt.setBigDecimal(1, pdateTgtAmount.add(AbsGap));
						}else {
							RenewUpdateSql_Pstmt.setBigDecimal(1, pdateTgtAmount.subtract(AbsGap));
						}
						RenewUpdateSql_Pstmt.setString(2, CurrDate);
						RenewUpdateSql_Pstmt.setString(3, MatCode);
						RenewUpdateSql_Pstmt.setString(4, UpdateTgtKeyValue);
						RenewUpdateSql_Pstmt.executeUpdate();
					}
					Result = "Yes";
				}
				Result = "Yes";
			}
		}catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
			Result = "No";
		}finally {
			try {
	            if (LineGiSearchSql_Pstmt != null) LineGiSearchSql_Pstmt.close();
	            if (LineGISearchSql_Pstmt_Rs != null) LineGISearchSql_Pstmt_Rs.close();
	            if (LineGIUpdateSql_Pstmt != null) LineGIUpdateSql_Pstmt.close();
	            if (RenewSearchSql_Pstmt_Rs != null) RenewSearchSql_Pstmt_Rs.close();
	            if (RenewSearchSql_Pstmt != null) RenewSearchSql_Pstmt.close();
	            if (RenewUpdateSql_Pstmt != null) RenewUpdateSql_Pstmt.close();
			} catch (SQLException e2) {
			// TODO: handle exception
				e2.printStackTrace();
			}
		}
		return Result;
	}

	public String DataLoadFun(String clDate, String comCode) {
		connDB();
		String result = null;
		System.out.println(clDate + ", " + comCode);
		try {
			sql = "SELECT * FROM sumrawmamt WHERE closingMon = ? AND comcode = ?";
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = null;
			pstmt.setString(1, clDate);
			pstmt.setString(2, comCode);
			rs = pstmt.executeQuery();
			JSONArray jsonArray = new JSONArray();
			while(rs.next()) {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("matcode", rs.getString("matcode"));
				jsonObject.put("matdesc", rs.getString("matdesc"));
				jsonObject.put("mattype", rs.getString("mattype"));
				jsonObject.put("spec", rs.getString("spec"));
				jsonObject.put("beginStocqty", rs.getBigDecimal("beginStocqty"));
				jsonObject.put("BsAmt", rs.getBigDecimal("BsAmt"));
				jsonObject.put("GrTransacQty", rs.getBigDecimal("GrTransacQty"));
				jsonObject.put("GrPurAmt", rs.getBigDecimal("GrPurAmt"));
				jsonObject.put("GrSubAmt", rs.getBigDecimal("GrSubAmt"));
				jsonObject.put("GrSumAmt", rs.getBigDecimal("GrSumAmt"));
				jsonObject.put("GrTransferQty", rs.getBigDecimal("GrTransferQty"));
				jsonObject.put("GiTransferQty", rs.getBigDecimal("GiTransferQty"));
				jsonObject.put("GiTransacQty", rs.getBigDecimal("GiTransacQty"));
				jsonObject.put("GiAmt", rs.getBigDecimal("GiAmt"));
				jsonObject.put("EndStocQty", rs.getBigDecimal("EndStocQty"));
				jsonObject.put("EsAmt", rs.getBigDecimal("EsAmt"));
				jsonObject.put("UnitPrice", rs.getBigDecimal("UnitPrice"));
				jsonObject.put("ErrorOX", rs.getString("ErrorOX"));
				jsonArray.put(jsonObject);
			}
			if (jsonArray.length() == 0) {
		        result = null;
		    } else {
		        result = jsonArray.toString();
		    }
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
	}

	public String LineDataLoadFun(JSONObject jsonObj) {
		// TODO Auto-generated method stub
		connDB();
	    String result = null;
	    ResultSet rs = null;
	    try {
	        boolean hasMatCode = jsonObj.has("MatCode") && jsonObj.get("MatCode") != null && !jsonObj.get("MatCode").toString().trim().isEmpty();

	        if (hasMatCode) {
	            sql = "SELECT * FROM InvenLogl WHERE "
	                + "comcode = ? AND "
	                + "mattype = ? AND "
	                + "plant = ? AND "
	                + "matcode = ? AND "
	                + "movetype = ? AND "
	                + "transactiondate >= ? AND transactiondate <= ?";
	            pstmt = conn.prepareStatement(sql);
	            pstmt.setString(1, jsonObj.getString("ComCode"));
	            pstmt.setString(2, jsonObj.getString("MatType"));
	            pstmt.setString(3, jsonObj.getString("PlantCode"));
	            pstmt.setString(4, jsonObj.getString("MatCode"));
	            pstmt.setString(5, jsonObj.getString("MVtype"));
	            pstmt.setString(6, jsonObj.getString("FromDate").replace("-", ""));
	            pstmt.setString(7, jsonObj.getString("EndDate").replace("-", ""));
	        } else {
	            sql = "SELECT * FROM InvenLogl WHERE "
	                + "comcode = ? AND "
	                + "mattype = ? AND "
	                + "plant = ? AND "
	                + "movetype = ? AND "
	                + "transactiondate >= ? AND transactiondate <= ?";
	            pstmt = conn.prepareStatement(sql);
	            pstmt.setString(1, jsonObj.getString("ComCode"));
	            pstmt.setString(2, jsonObj.getString("MatType"));
	            pstmt.setString(3, jsonObj.getString("PlantCode"));
	            pstmt.setString(4, jsonObj.getString("MVtype"));
	            pstmt.setString(5, jsonObj.getString("FromDate").replace("-", ""));
	            pstmt.setString(6, jsonObj.getString("EndDate").replace("-", ""));
	        }
			rs = pstmt.executeQuery();
			JSONArray jsonArray = new JSONArray();
			while(rs.next()) {
				System.out.println("docnum : " + rs.getString("docnum"));
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("docnum", rs.getString("docnum"));
				jsonObject.put("transactiondate", rs.getString("transactiondate"));
				jsonObject.put("matcode", rs.getString("matcode"));
				jsonObject.put("matdesc", rs.getString("matdesc"));
				jsonObject.put("movetype", rs.getString("movetype"));
				jsonObject.put("mattype", rs.getString("mattype"));
				jsonObject.put("lotnum", rs.getString("lotnum"));
				jsonObject.put("quantity", rs.getString("quantity"));
				jsonObject.put("workordnum", rs.getString("workordnum"));
				jsonObject.put("procuordnum", rs.getString("procuordnum"));
				jsonObject.put("salesordnum", rs.getString("salesordnum"));
				jsonObject.put("vendcode", rs.getString("vendcode"));
				jsonObject.put("vendDesc", rs.getString("vendDesc"));
				jsonObject.put("storcode", rs.getString("storcode"));
				jsonObject.put("stordesc", rs.getString("stordesc"));
				jsonObject.put("plant", rs.getString("plant"));
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
}
