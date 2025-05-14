package InfoData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import org.json.*;

public class InfoLoadingDAO {
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
	
	public String StockDataLoading(JSONObject jsonObj) {
		connDB();
	    String[] keyOrder = {"ComCode", "PlantCode", "UploadDataCode", "CalcMonth"};
	    String[] DataList = new String[keyOrder.length];

	    for (int i = 0; i < keyOrder.length; i++) {
	        DataList[i] = jsonObj.has(keyOrder[i]) ? jsonObj.get(keyOrder[i]).toString() : "";
	    }
	    ResultSet rs = null;
	    JSONArray jsonArray = new JSONArray();
	    try {
		    String sql = "SELECT * FROM matstock WHERE company = ? AND plant = ? AND keydata = ? AND period = ?";
		    pstmt = conn.prepareStatement(sql);
		    pstmt.setString(1, DataList[0]);
		    pstmt.setString(2, DataList[1]);
		    pstmt.setString(3, DataList[2]);
		    pstmt.setString(4, DataList[3]);
		    rs = pstmt.executeQuery();
		    while(rs.next()) {
		    	JSONObject jsonObject = new JSONObject();
		    	jsonObject.put("MatCode", rs.getString("type"));
		    	jsonObject.put("MatCode", rs.getString("period"));
		    	jsonObject.put("MatCode", rs.getString("delivery"));
		    	jsonObject.put("MatCode", rs.getString("itemno"));
		    	jsonObject.put("MatCode", rs.getString("item"));
		    	jsonObject.put("MatCode", rs.getString("spec"));
		    	jsonObject.put("MatCode", rs.getString("lot"));
		    	jsonObject.put("MatCode", rs.getString("stocktype"));
		    	jsonObject.put("MatCode", rs.getString("weight"));
		    	jsonObject.put("MatCode", rs.getString("amount"));
		    	jsonObject.put("MatCode", rs.getString("whcode"));
		    	jsonObject.put("MatCode", rs.getString("warehouse"));
		    	jsonObject.put("MatCode", rs.getString("pono"));
		    	jsonObject.put("MatCode", rs.getString("vendor"));
		    	jsonObject.put("MatCode", rs.getString("vendorname"));
		    	jsonObject.put("MatCode", rs.getString("plant"));
		    	jsonObject.put("MatCode", rs.getString("company"));
		    	jsonArray.put(jsonObject);
		    }
	    }catch (Exception e) {
			// TODO: handle exception
		}
	    return jsonArray.toString();
	}
}
