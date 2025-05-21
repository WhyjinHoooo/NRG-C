package InfoData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.*;

/**
 * Servlet implementation class InfoLoading
 */
@WebServlet("/InfoLoading/*")
public class InfoLoading extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InfoLoading() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doHandle(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doHandle(request, response);
	}

	private void doHandle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter writer = response.getWriter();

        String action = request.getPathInfo(); // 예: "/StockInfoLoading.do"
        System.out.println("action : " + action);
        if (action == null) action = "";

        // JSON 읽기
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        String jsonString = sb.toString();
        String LoadedData = null;
        switch(action) {
            case "/StockInfoLoading.do":
                try {
                	JSONObject jsonObj = new JSONObject(jsonString);
                    Iterator<String> keys = jsonObj.keys();
                    while(keys.hasNext()) {
                        String key = keys.next();
                        Object value = jsonObj.get(key);
                        System.out.println(key + " : " + value);
                    }
                    InfoLoadingDAO dao = new InfoLoadingDAO();
                    if(jsonObj.get("UploadDataCode").equals("PUR")) {
                    	LoadedData = dao.StockDataLoading(jsonObj);
                    }
                    if(LoadedData == null) {
                    	writer.print("{\"result\":\"fail\"}");
                    }else {
                    	writer.print("{\"result\":\"success\", \"List\":" + LoadedData + "}");
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    writer.print("{\"result\":\"fail\", \"message\":\"" + e.getMessage().replace("\"","\\\"") + "\"}");
                }
                break;
            case "/InputMatLoading.do":
                try {
                	JSONObject jsonObj = new JSONObject(jsonString);
                    Iterator<String> keys = jsonObj.keys();
                    while(keys.hasNext()) {
                        String key = keys.next();
                        Object value = jsonObj.get(key);
                        System.out.println(key + " : " + value);
                    }
                    InfoLoadingDAO dao = new InfoLoadingDAO();
                    if(jsonObj.get("UploadDataCode").equals("BFG")) {
                        LoadedData = dao.InputmatLoading(jsonObj);                    	
                    }
                    if(LoadedData == null) {
                    	writer.print("{\"result\":\"fail\"}");
                    }else {
                    	writer.print("{\"result\":\"success\", \"List\":" + LoadedData + "}");
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    writer.print("{\"result\":\"fail\", \"message\":\"" + e.getMessage().replace("\"","\\\"") + "\"}");
                }
                break;
            case "/SplitMatLoading.do":
                try {
                	JSONObject jsonObj = new JSONObject(jsonString);
                    Iterator<String> keys = jsonObj.keys();
                    while(keys.hasNext()) {
                        String key = keys.next();
                        Object value = jsonObj.get(key);
                        System.out.println(key + " : " + value);
                    }
                    InfoLoadingDAO dao = new InfoLoadingDAO();
                    if(jsonObj.get("UploadDataCode").equals("MGR")) {
                        LoadedData = dao.SplitmatLoading(jsonObj);                    	
                    }
                    if(LoadedData == null) {
                    	writer.print("{\"result\":\"fail\"}");
                    }else {
                    	writer.print("{\"result\":\"success\", \"List\":" + LoadedData + "}");
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    writer.print("{\"result\":\"fail\", \"message\":\"" + e.getMessage().replace("\"","\\\"") + "\"}");
                }
                break;
            case "/SalesDeliLoading.do":
                try {
                	JSONObject jsonObj = new JSONObject(jsonString);
                    Iterator<String> keys = jsonObj.keys();
                    while(keys.hasNext()) {
                        String key = keys.next();
                        Object value = jsonObj.get(key);
                        System.out.println(key + " : " + value);
                    }
                    InfoLoadingDAO dao = new InfoLoadingDAO();
                    if(jsonObj.get("UploadDataCode").equals("SDG")) {
                        LoadedData = dao.SalesDeliLoading(jsonObj);                    	
                    }
                    if(LoadedData == null) {
                    	writer.print("{\"result\":\"fail\"}");
                    }else {
                    	writer.print("{\"result\":\"success\", \"List\":" + LoadedData + "}");
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    writer.print("{\"result\":\"fail\", \"message\":\"" + e.getMessage().replace("\"","\\\"") + "\"}");
                }
                break;
            case "/Joborder.do":
                try {
                	JSONObject jsonObj = new JSONObject(jsonString);
                    Iterator<String> keys = jsonObj.keys();
                    while(keys.hasNext()) {
                        String key = keys.next();
                        Object value = jsonObj.get(key);
                        System.out.println(key + " : " + value);
                    }
                    InfoLoadingDAO dao = new InfoLoadingDAO();
                    if(jsonObj.get("UploadDataCode").equals("POL")) {
                        LoadedData = dao.JoborderLoading(jsonObj);                    	
                    }
                    if(LoadedData == null) {
                    	writer.print("{\"result\":\"fail\"}");
                    }else {
                    	writer.print("{\"result\":\"success\", \"List\":" + LoadedData + "}");
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                    writer.print("{\"result\":\"fail\", \"message\":\"" + e.getMessage().replace("\"","\\\"") + "\"}");
                }
                break;
            // case "/AnotherAction.do": ... 등 추가 가능
            default:
                writer.print("{\"result\":\"fail\", \"message\":\"Unknown action: " + action + "\"}");
        }
        writer.flush();
    }
}
