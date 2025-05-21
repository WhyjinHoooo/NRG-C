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

	public String InputmatLoading(JSONObject jsonObj) {
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
		    String sql = "SELECT * FROM matinput WHERE company = ? AND plant = ? AND LEFT(keydata, 3) IN (?) AND period = ?";
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
			    jsonObject.put("amount", String.format("%.3f", rs.getDouble("amount")));
			    jsonObject.put("whcode", rs.getString("whcode"));
			    jsonObject.put("warehouse", rs.getString("warehouse"));
			    jsonObject.put("pono", rs.getString("pono"));
			    jsonObject.put("process", rs.getString("process"));
			    jsonObject.put("processdes", rs.getString("processdes"));
			    jsonObject.put("plant", rs.getString("plant"));
			    jsonObject.put("company", rs.getString("company"));
			    jsonArray.put(jsonObject);
		    }
		    if (jsonArray.length() == 0) {
		        result = null;
		    } else {
		        result = jsonArray.toString();
		    }
	    }catch (SQLException e) {
			// TODO: handle exception
		}
	    return result;
	}

	public String SplitmatLoading(JSONObject jsonObj) {
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
		    String sql = "SELECT * FROM matsplit WHERE company = ? AND plant = ? AND LEFT(keydata, 3) IN (?) AND period = ?";
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
			    jsonObject.put("stocktype", rs.getString("stocktype"));
			    jsonObject.put("weight", String.format("%.3f", rs.getDouble("weight")));
			    jsonObject.put("pono", rs.getString("pono"));
			    jsonObject.put("lot", rs.getString("lot"));
			    jsonObject.put("plant", rs.getString("plant"));
			    jsonObject.put("company", rs.getString("company"));
			    jsonArray.put(jsonObject);
		    }
		    if (jsonArray.length() == 0) {
		        result = null;
		    } else {
		        result = jsonArray.toString();
		    }
	    }catch (SQLException e) {
			// TODO: handle exception
		}
	    return result;
	}
	
	public String SalesDeliLoading(JSONObject jsonObj) {
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
		    String sql = "SELECT * FROM matdeli WHERE company = ? AND plant = ? AND LEFT(keydata, 3) IN (?) AND period = ?";
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
			    jsonObject.put("whcode", rs.getString("whcode"));
			    jsonObject.put("warehouse", rs.getString("warehouse"));
			    jsonObject.put("pono", rs.getString("pono"));
			    jsonObject.put("vencode", rs.getString("vencode"));
			    jsonObject.put("vender", rs.getString("vender"));
			    jsonObject.put("plant", rs.getString("plant"));
			    jsonObject.put("company", rs.getString("company"));
			    jsonArray.put(jsonObject);
		    }
		    if (jsonArray.length() == 0) {
		        result = null;
		    } else {
		        result = jsonArray.toString();
		    }
	    }catch (SQLException e) {
			// TODO: handle exception
		}
	    return result;
	}
	
	public String JoborderLoading(JSONObject jsonObj) {
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
	    	String sql = "SELECT * FROM matorderlist WHERE company = ? AND plant = ? AND LEFT(keydata, 3) = ? AND LEFT(document, 9) = ?";
		    pstmt = conn.prepareStatement(sql);
		    pstmt.setString(1, DataList[0].trim());
		    pstmt.setString(2, DataList[1].trim());
		    pstmt.setString(3, DataList[2].trim());
		    pstmt.setString(4, DataList[2].trim() + DataList[3].trim());
		    rs = pstmt.executeQuery();
		    while(rs.next()) {
		    	JSONObject jsonObject = new JSONObject();
		    	jsonObject.put("pono", rs.getString("pono"));
		    	jsonObject.put("MOType", rs.getString("MOType"));
		    	jsonObject.put("lotNum", rs.getString("lotNum"));
		    	jsonObject.put("delidiv", rs.getString("delidiv"));
		    	jsonObject.put("facLind", rs.getString("facLind"));
			    jsonObject.put("facility", rs.getString("facility"));
			    jsonObject.put("itemno", rs.getString("itemno"));
			    jsonObject.put("item", rs.getString("item"));
			    jsonObject.put("makedate", rs.getString("makedate"));
			    jsonObject.put("prokg", String.format("%.3f", rs.getDouble("prokg")));
			    jsonObject.put("prolt", String.format("%.3f", rs.getDouble("prolt")));
			    jsonObject.put("Runtime", rs.getString("Runtime"));
			    jsonObject.put("InputTime", rs.getString("InputTime"));
			    jsonObject.put("MixTime", rs.getString("MixTime"));
			    jsonObject.put("TestTime", rs.getString("TestTime"));
			    jsonObject.put("plant", rs.getString("plant"));
			    jsonObject.put("company", rs.getString("company"));
			    jsonArray.put(jsonObject);
		    }
		    if (jsonArray.length() == 0) {
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
