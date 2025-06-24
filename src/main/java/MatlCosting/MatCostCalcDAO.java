package MatlCosting;

import java.sql.Connection;
import java.sql.PreparedStatement;
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
			
			sql = "";
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return "";
	}
}
