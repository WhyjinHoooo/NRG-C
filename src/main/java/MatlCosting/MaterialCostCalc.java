package MatlCosting;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
		//String ClDate = (now.format(DateTimeFormatter.ofPattern("yyyy-MM"))).replace("-", ""); //이렇게 하면 202504처럼 저장됨
		String ClDate = "202504"; // 일단은 강제로 설정해줌
		BufferedReader br = request.getReader();
		StringBuilder sb = new StringBuilder();
		String line;
		while ((line = br.readLine()) != null) {
		    sb.append(line);
		}
		String body = sb.toString();
		System.out.println("body: " + body);
		// body를 JSONObject로 파싱해서 ComCode, IQData 추출
		JSONObject json = new JSONObject(body);
		String ComCode = json.getString("ComCode");
		String IQData = json.getString("IQData");
		System.out.println(ComCode);
		
		String action = request.getPathInfo();
		
		MatCostCalcDAO Dao = new MatCostCalcDAO();
		String DaoResult = null;
		try {
			switch(action) {
			case "/RawmPriceCalc.do":
				DaoResult = Dao.CostCalcFun(ClDate, ComCode);
		    	if (DaoResult == null || DaoResult.equals("No")) {
		    	    writer.print("{\"result\":\"fail\", \"message\":\"DaoResult null이거나 'No'입니다.\"}");
		    	} else {
		    	    writer.print("{\"result\":\"success\", \"message\":\"정상적으로 처리되었습니다.\"}");
		    	}
				break;
			case "/RawmDataLoading.do":
				DaoResult = Dao.DataLoadFun(IQData, ComCode);
				System.out.println(DaoResult);
                if(DaoResult == null) {
                	writer.print("{\"result\":\"fail\"}");
                }else {
                	writer.print("{\"result\":\"success\", \"List\":" + DaoResult + "}");
                }
				break;
			}
			writer.flush();
		}catch (Exception e) {
			// TODO: handle exception
		}
	}

}
