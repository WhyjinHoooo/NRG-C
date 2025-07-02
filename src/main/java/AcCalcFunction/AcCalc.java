package AcCalcFunction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * Servlet implementation class AcCalc
 */
@WebServlet("/AcCalc/*")
public class AcCalc extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AcCalc() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doHandle(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doHandle(request, response);
	}

	private void doHandle(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter writer = response.getWriter();

        String action = request.getPathInfo(); // 예: "/StockInfoLoading.do"
        System.out.println("action : " + action);
        
        StringBuilder sb = new StringBuilder();
        String line;
        try (BufferedReader reader = request.getReader()) {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        String jsonString = sb.toString();
        String ResultData = null;
        
        AcCalcDAO dao = new AcCalcDAO();
        switch(action) {
        case "/CalcCostAllocation.do":
        	try {
            	JSONObject jsonObj = new JSONObject(jsonString);
                Iterator<String> keys = jsonObj.keys();
                while(keys.hasNext()) {
                    String key = keys.next();
                    Object value = jsonObj.get(key);
                }
                ResultData = dao.CostAllocation(jsonObj);
                if(ResultData == null || ResultData.equals("Fail")) {
                	writer.print("{\"result\":\"fail\"}");
                }else {
                	writer.print("{\"result\":\"success\"}");
                }
        	} catch (Exception e) {
				// TODO: handle exception
        		e.printStackTrace();
        		writer.print("{\"result\":\"fail\", \"message\":\"" + e.getMessage().replace("\"","\\\"") + "\"}");
			}
        	break;
        case "/CostAlloLoad.do":
        	try {
            	JSONObject jsonObj = new JSONObject(jsonString);
                Iterator<String> keys = jsonObj.keys();
                while(keys.hasNext()) {
                    String key = keys.next();
                    Object value = jsonObj.get(key);
                }
                ResultData = dao.CostAlloDataLoading(jsonObj);
                if(ResultData == null) {
                	writer.print("{\"result\":\"fail\"}");
                }else {
                	writer.print("{\"result\":\"success\", \"List\":" + ResultData + "}");
                }
        	} catch (Exception e) {
				// TODO: handle exception
        		e.printStackTrace();
        		writer.print("{\"result\":\"fail\", \"message\":\"" + e.getMessage().replace("\"","\\\"") + "\"}");
			}
        	break;
        }
        writer.flush();
	}
}
