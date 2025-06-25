package MatlCosting;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

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
		String CurrVal = DateVal;// 202501
		String ComCodeVal = ComCode;
		try {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMM");
			Date dateVal = sdf.parse(CurrVal);
			Calendar cal = Calendar.getInstance();
			cal.setTime(dateVal);
			cal.add(Calendar.MONTH, -1);
			String PastVal = sdf.format(cal.getTime()); // 202412
			
			Double beginStocqty = 0.0; // 기초수량
			Double BsAmt = 0.0;
			Double GrTransacQty = 0.0;  // 입고수량
			Double GrPurAmt = 0.0;
			Double GrSubAmt = 0.0;
			Double GrSumAmt = 0.0;
			Double GrTransferQty = 0.0; // 이동입고 수량
			Double GiTransferQty = 0.0; // 이동출고 수량	
			Double GiTransacQty = 0.0; // 출고수량
			Double GiAmt = 0.0;
			Double EndStocQty = 0.0; // 기말 수량
			Double EsAmt = 0.0;
			Double UnitPrice = 0.0;

			sql = "SELECT s.closingMon AS s_closingMon, "
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
			           + "SUM(s.EndStocQty) AS EndStocQty_sum, "
			           + "r.EndStocQty AS Rawm_EndStocQty,"
			           + "r.EsAmt AS Rawm_EsAmt "
			           + "FROM sumtable s "
			           + "JOIN sumrawmamt r ON s.matcode = r.matcode "
			           + "WHERE s.closingMon = ? "
			           + "  AND r.closingMon = ? "
			           + "  AND s.comcode = ? "
			           + "  AND s.mattype = 'RAWM' "
			           + "GROUP BY s.matcode, s.matdesc";
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, CurrVal);
			pstmt.setString(2, PastVal);
			pstmt.setString(3, ComCodeVal);
			ResultSet rs = pstmt.executeQuery();
			
			while(rs.next()) {
				beginStocqty += rs.getDouble("Rawm_EndStocQty");
				BsAmt += rs.getDouble("Rawm_EsAmt");
				GrTransacQty += rs.getDouble("GrTransacQty_sum");
				
				String InsertSql = "INSERT INTO sumrawmamt VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
				PreparedStatement Insert_Pstmt = conn.prepareStatement(InsertSql);
				Insert_Pstmt.setString(1, CurrVal);
				Insert_Pstmt.setString(2, ComCodeVal);
				Insert_Pstmt.setString(3, rs.getString("s_matcode"));
				Insert_Pstmt.setString(4, rs.getString("s_matdesc"));
				Insert_Pstmt.setString(5, "RAWM");
				Insert_Pstmt.setString(6, rs.getString("s_spec"));
				
				Insert_Pstmt.setDouble(7, beginStocqty);
				Insert_Pstmt.setDouble(8, BsAmt);
				
				Insert_Pstmt.setDouble(9, GrTransacQty);
				String PriceSql = "SELECT movetype, closingmon, matcode, matdesc, spec, lotnum, mattype, quantity, SUM(amount) as SumAmount "
					+ "FROM InvenLogl WHERE closingmon = ? AND matcode = ? AND LEFT(movetype, 2) = ? AND mattype = ? AND comcode = ? "
					+ "GROUP BY matcode, matdesc "
					+ "HAVING SUM(amount) > 0";
				PreparedStatement Price_Pstmt = conn.prepareStatement(PriceSql);
				Price_Pstmt.setString(1, CurrVal);
				Price_Pstmt.setString(2, rs.getString("s_matcode"));
				Price_Pstmt.setString(3, "GR");
				Price_Pstmt.setString(4, "RAWM");
				Price_Pstmt.setString(5, ComCodeVal);
				ResultSet Price_Rs = Price_Pstmt.executeQuery();
				if(Price_Rs.next()) {
					GrTransacQty += Price_Rs.getDouble("SumAmount");
					Insert_Pstmt.setDouble(10, Price_Rs.getDouble("SumAmount"));
				}
				GrSumAmt += GrSubAmt + GrTransacQty;
				Insert_Pstmt.setDouble(11, GrSubAmt);
				Insert_Pstmt.setDouble(12, GrSumAmt);
				
				String GiQuantity_Sql = "SELECT movetype, closingmon, matcode, matdesc, spec, lotnum, mattype, SUM(quantity) as SumQuantity "
						+ "FROM InvenLogl "
						+ "WHERE closingmon = ? AND matcode = ? AND LEFT(movetype, 2) = ? AND mattype = ? AND comcode = ? "
						+ "GROUP BY matcode, matdesc";
				PreparedStatement GiQuantity_Pstmt = conn.prepareStatement(GiQuantity_Sql);
				GiQuantity_Pstmt.setString(1, CurrVal);
				GiQuantity_Pstmt.setString(2, rs.getString("s_matcode"));
				GiQuantity_Pstmt.setString(3, "GI");
				GiQuantity_Pstmt.setString(4, "RAWM");
				GiQuantity_Pstmt.setString(5, ComCodeVal);
				ResultSet GiQuantity_Rs = GiQuantity_Pstmt.executeQuery();
				if(GiQuantity_Rs.next()) {
					GiTransacQty += GiQuantity_Rs.getDouble("SumQuantity");
				}
				Insert_Pstmt.setDouble(13, GrTransferQty);
				Insert_Pstmt.setDouble(14, GiTransferQty);
				Insert_Pstmt.setDouble(15, GiTransacQty);
				
				EndStocQty = beginStocqty + GrTransacQty - GiTransacQty;
				UnitPrice = (BsAmt + GrSumAmt) / (beginStocqty + GrTransacQty);
				EsAmt += Math.round(EndStocQty * UnitPrice);
				
				Insert_Pstmt.setDouble(16, BsAmt + GrSumAmt - EsAmt);
				Insert_Pstmt.setDouble(17, EndStocQty);
				Insert_Pstmt.setDouble(18, EsAmt);
				Insert_Pstmt.setDouble(19, UnitPrice);
				Insert_Pstmt.executeUpdate();
			}
		} catch (ParseException e) {
	        System.out.println("날짜 파싱 오류: " + e.getMessage());
	        e.printStackTrace();
	    } catch (SQLException e) {
	        System.out.println("SQL 실행 오류: " + e.getMessage());
	        e.printStackTrace();
	    }
	
		return "";
	}
}
