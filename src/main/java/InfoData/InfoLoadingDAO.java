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
	    String result = null;
	    for (int i = 0; i < keyOrder.length; i++) {
	        DataList[i] = jsonObj.has(keyOrder[i]) ? jsonObj.get(keyOrder[i]).toString() : "";
	        System.out.println("DataList[" + i + "] : " + DataList[i]);
	    }
	    ResultSet rs = null;
	    JSONArray jsonArray = new JSONArray();
	    try {
		    String sql = "SELECT * FROM matstock WHERE company = ? AND plant = ? AND LEFT(keydata, 3) IN (?) AND period = ?";
		    pstmt = conn.prepareStatement(sql);
		    pstmt.setString(1, DataList[0]);
		    pstmt.setString(2, DataList[1]);
		    pstmt.setString(3, DataList[2]);
		    pstmt.setString(4, DataList[3]);
		    rs = pstmt.executeQuery();
		    while(rs.next()) {
		    	JSONObject jsonObject = new JSONObject();
		    	jsonObject.put("typeData", rs.getString("type"));
		    	jsonObject.put("period", rs.getString("period"));
		    	jsonObject.put("delivery", rs.getString("delivery"));
		    	jsonObject.put("itemno", rs.getString("itemno"));
		    	jsonObject.put("item", rs.getString("item"));
			    jsonObject.put("spec", rs.getString("spec"));
			    jsonObject.put("lot", rs.getString("lot"));
			    jsonObject.put("stocktype", rs.getString("stocktype"));
			    jsonObject.put("weight", String.format("%.3f", rs.getDouble("weight")));
			    jsonObject.put("amount", String.format("%.3f", rs.getDouble("amount")));
			    jsonObject.put("UnitPrice", String.format("%.3f",
			        rs.getDouble("weight") == 0 ? 0.0 : rs.getDouble("amount") / rs.getDouble("weight")
			    ));
			    jsonObject.put("whcode", rs.getString("whcode"));
			    jsonObject.put("warehouse", rs.getString("warehouse"));
			    jsonObject.put("pono", rs.getString("pono"));
			    jsonObject.put("vendor", rs.getString("vendor"));
			    jsonObject.put("vendorname", rs.getString("vendorname"));
			    jsonObject.put("plant", rs.getString("plant"));
			    jsonObject.put("company", rs.getString("company"));
			    jsonArray.put(jsonObject);
		    }
		    if (jsonArray.length() == 0) {
		        // 결과 없음 메시지 반환
		        result = null;
		    } else {
		        result = jsonArray.toString();
		    }
	    }catch (SQLException e) {
			// TODO: handle exception
		}
	    return result;
	}
}
