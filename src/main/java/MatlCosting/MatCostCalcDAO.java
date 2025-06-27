package MatlCosting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

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
            System.out.println("DB 접속 성공");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public String CostCalcFun(String DateVal, String ComCode) {
		String result = "No";
		connDB();
		String CurrVal = DateVal;// 202501
		String ComCodeVal = ComCode;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			Date dateVal = sdf.parse(CurrVal);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateVal);
			cal.add(Calendar.MONTH, -1);
			String PastVal = sdf.format(cal.getTime()); // 202412
			System.out.println(CurrVal + "," + PastVal + "," + ComCodeVal);
			Double SumTableBeginQty = 0.0; // SumTable 기초수량
			Double beginStocqty = 0.0; // 기초수량
			Double BsAmt = 0.0;
			Double GrTransacQty = 0.0;  // 입고수량
			Double GrPurAmt = 0.0;
			Double GrSubAmt = 0.0;
			Double GrSumAmt = 0.0; // 최종 입고 금액
			Double GrTransferQty = 0.0; // 이동입고 수량
			Double GiTransferQty = 0.0; // 이동출고 수량	
			Double GiTransacQty = 0.0; // 출고수량
			Double GiAmt = 0.0; // 최종 출고 금액
			Double EndStocQty = 0.0; // 기말 수량
			Double EsAmt = 0.0;
			Double UnitPrice = 0.0;

			sql = "SELECT * FROM sumrawmamt WHERE closingMon = ? AND comcode = ?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, PastVal);
			pstmt.setString(2, ComCodeVal);
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
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
					    + "AND s.mattype = 'RAWM' "
					    + "GROUP BY "
					    + "s.closingMon, "
					    + "s.comcode, "
					    + "s.matcode, "
					    + "s.matdesc, "
					    + "s.mattype, "
					    + "s.spec";
				PreparedStatement MDSS_Pstmt = conn.prepareStatement(MatDataSearchSql);
				MDSS_Pstmt.setString(1, CurrVal);
				MDSS_Pstmt.setString(2, ComCodeVal);
				MDSS_Pstmt.setString(3, rs.getString("matcode"));
				ResultSet MSDD_Rs = MDSS_Pstmt.executeQuery();
				if(MSDD_Rs.next()) {
					SumTableBeginQty = MSDD_Rs.getDouble("beginStocqty_sum");
					beginStocqty = rs.getDouble("EndStocQty");
					BsAmt = rs.getDouble("EsAmt");
					GrTransacQty = MSDD_Rs.getDouble("GrTransacQty_sum");
					// 입고금액 입력하는 부분
					String PriceSql = "SELECT movetype, closingmon, matcode, matdesc, spec, lotnum, mattype, quantity, SUM(amount) as SumAmount "
						+ "FROM InvenLogl WHERE closingmon = ? AND matcode = ? AND LEFT(movetype, 2) = ? AND mattype = ? AND comcode = ? "
						+ "GROUP BY matcode, matdesc "
						+ "HAVING SUM(amount) > 0";
					PreparedStatement Price_Pstmt = conn.prepareStatement(PriceSql);
					Price_Pstmt.setString(1, CurrVal);
					Price_Pstmt.setString(2, rs.getString("matcode"));
					Price_Pstmt.setString(3, "GR");
					Price_Pstmt.setString(4, "RAWM");
					Price_Pstmt.setString(5, ComCodeVal);
					ResultSet Price_Rs = Price_Pstmt.executeQuery();
					if(Price_Rs.next()) {
						GrPurAmt = Price_Rs.getDouble("SumAmount");
					}
					
					GrSumAmt = GrSubAmt + GrPurAmt;
					
					// 출고수량 계산하는 부분
					String GiQuantity_Sql = "SELECT movetype, closingmon, matcode, matdesc, spec, lotnum, mattype, SUM(quantity) as SumQuantity "
							+ "FROM InvenLogl "
							+ "WHERE closingmon = ? AND matcode = ? AND LEFT(movetype, 2) = ? AND mattype = ? AND comcode = ? "
							+ "GROUP BY matcode, matdesc";
					PreparedStatement GiQuantity_Pstmt = conn.prepareStatement(GiQuantity_Sql);
					GiQuantity_Pstmt.setString(1, CurrVal);
					GiQuantity_Pstmt.setString(2, rs.getString("matcode"));
					GiQuantity_Pstmt.setString(3, "GI");
					GiQuantity_Pstmt.setString(4, "RAWM");
					GiQuantity_Pstmt.setString(5, ComCodeVal);
					ResultSet GiQuantity_Rs = GiQuantity_Pstmt.executeQuery();
					if(GiQuantity_Rs.next()) {
						GiTransacQty = GiQuantity_Rs.getDouble("SumQuantity");
					}

					EndStocQty = beginStocqty + GrTransacQty - GiTransacQty;
					double denominator = beginStocqty + GrTransacQty;
					if (denominator != 0) {
					    UnitPrice = (BsAmt + GrSumAmt) / denominator;
					} else {
					    UnitPrice = 0.0;
					}
					EsAmt = (double) Math.round(EndStocQty * UnitPrice);

					String InsertSql = "INSERT INTO sumrawmamt VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
					PreparedStatement Insert_Pstmt = conn.prepareStatement(InsertSql);
					Insert_Pstmt.setString(1, CurrVal);
					Insert_Pstmt.setString(2, ComCodeVal);
					Insert_Pstmt.setString(3, rs.getString("matcode"));
					Insert_Pstmt.setString(4, rs.getString("matdesc"));
					Insert_Pstmt.setString(5, "RAWM");
					Insert_Pstmt.setString(6, rs.getString("spec"));
					Insert_Pstmt.setDouble(7, beginStocqty);
					Insert_Pstmt.setDouble(8, BsAmt);
					Insert_Pstmt.setDouble(9, GrTransacQty);
					Insert_Pstmt.setDouble(10, GrPurAmt);
					Insert_Pstmt.setDouble(11, GrSubAmt);
					Insert_Pstmt.setDouble(12, GrSumAmt);
					Insert_Pstmt.setDouble(13, GrTransferQty);
					Insert_Pstmt.setDouble(14, GiTransferQty);
					Insert_Pstmt.setDouble(15, GiTransacQty);
					Insert_Pstmt.setDouble(16, BsAmt + GrSumAmt - EsAmt);
					Insert_Pstmt.setDouble(17, EndStocQty);
					Insert_Pstmt.setDouble(18, EsAmt);
					Insert_Pstmt.setDouble(19, UnitPrice);
					if(Double.compare(beginStocqty, SumTableBeginQty) == 0) {
						System.out.println("Good");
						Insert_Pstmt.setString(20, "X"); // -> 문제 없음
					}else {
						System.out.println("Bad");
						Insert_Pstmt.setString(20, "O"); // -> 문제 있음
					}
					Insert_Pstmt.executeUpdate();
				}	
			}
			result = "Yes";
		} catch (ParseException e) {
	        System.out.println("날짜 파싱 오류: " + e.getMessage());
	        e.printStackTrace();
	    } catch (SQLException e) {
	        System.out.println("SQL 실행 오류: " + e.getMessage());
	        e.printStackTrace();
	    }
	
		return result;
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
				jsonObject.put("beginStocqty", rs.getString("beginStocqty"));
				jsonObject.put("BsAmt", rs.getString("BsAmt"));
				jsonObject.put("GrTransacQty", rs.getString("GrTransacQty"));
				jsonObject.put("GrPurAmt", rs.getString("GrPurAmt"));
				jsonObject.put("GrSubAmt", rs.getString("GrSubAmt"));
				jsonObject.put("GrSumAmt", rs.getString("GrSumAmt"));
				jsonObject.put("GrTransferQty", rs.getString("GrTransferQty"));
				jsonObject.put("GiTransferQty", rs.getString("GiTransferQty"));
				jsonObject.put("GiTransacQty", rs.getString("GiTransacQty"));
				jsonObject.put("GiAmt", rs.getString("GiAmt"));
				jsonObject.put("EndStocQty", rs.getString("EndStocQty"));
				jsonObject.put("EsAmt", rs.getString("EsAmt"));
				jsonObject.put("UnitPrice", rs.getString("UnitPrice"));
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
