package MatlCosting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import Final.ApprovalDAO;

/**
 * Servlet implementation class MaterialCostCalc
 */
@WebServlet(name = "CostCalc", urlPatterns = { "/CostCalc/*" })
public class MaterialCostCalc extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    public MaterialCostCalc() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
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
        
		LocalDateTime now = LocalDateTime.now();
		String ClDate = (now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))).replace("-", ""); //이렇게 하면 202504처럼 저장됨
		String action = request.getPathInfo();
		
		MatCostCalcDAO Dao = new MatCostCalcDAO();
		String DaoResult = null;
		try {
			switch(action) {
			case "/RawmPriceCalc.do":
				String Cocd = request.getParameter("ComCode");
				String GetClDate = request.getParameter("IQData");
				DaoResult = Dao.CostCalcFun(GetClDate, Cocd, ClDate);
	    		System.out.println("업데이트 결과 : " + DaoResult);
		    	if (DaoResult == null || DaoResult.equals("No")) {
		    	    writer.print("{\"result\":\"fail\", \"message\":\"DaoResult null이거나 'No'입니다.\"}");
		    	} else if(DaoResult == null || DaoResult.equals("Impossible")){
		    		writer.print("{\"result\":\"fail\", \"message\":\"해당 결산월은 이미 결산되었습니다.\"}");
		    	}
		    	else{
		    	    writer.print("{\"result\":\"success\", \"message\":\"정상적으로 처리되었습니다.\"}");
		    	}
				break;
			case "/RawmDataLoading.do":
				BufferedReader br = request.getReader();
				StringBuilder sb = new StringBuilder();
				String line;
				while ((line = br.readLine()) != null) {
				    sb.append(line);
				}
				String body = sb.toString();
				System.out.println("body: " + body);
				JSONObject json = new JSONObject(body);
				String ComCode = json.getString("ComCode");
				String IQData = json.getString("IQData");
				DaoResult = Dao.DataLoadFun(IQData, ComCode);
                if(DaoResult == null) {
                	writer.print("{\"result\":\"fail\"}");
                }else {
                	writer.print("{\"result\":\"success\", \"List\":" + DaoResult + "}");
                }
				break;
			case "/RawmLineDataLoading.do":
				System.out.println("RawmLineDataLoading.do");
				BufferedReader br1 = request.getReader();
			    StringBuilder sb1 = new StringBuilder();
			    String line1;
			    while ((line1 = br1.readLine()) != null) {
			        sb1.append(line1);
			    }
			    String jsonString = sb1.toString();
			    try {
			        JSONObject jsonObj = new JSONObject(jsonString);
			        Iterator<String> keys = jsonObj.keys();
			        while(keys.hasNext()) {
			            String key = keys.next();
			            Object value = jsonObj.get(key);
			            System.out.println(key + " : " + value);
			        }
			        DaoResult = Dao.LineDataLoadFun(jsonObj);
			        System.out.println("DaoResult : " + DaoResult);
			        if(DaoResult == null) {
	                	writer.print("{\"result\":\"fail\"}");
	                }else {
	                	writer.print("{\"result\":\"success\", \"List\":" + DaoResult + "}");
	                }
			    } catch(Exception e) {
			        e.printStackTrace();
			    }
				break;
			}
			writer.flush();
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
}
