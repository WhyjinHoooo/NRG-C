package AcCalcFunction;

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
	
	
}
