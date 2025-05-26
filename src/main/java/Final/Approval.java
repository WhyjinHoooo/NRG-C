package Final;

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
 * Servlet implementation class Approval
 */
@WebServlet("/Approval/*")
public class Approval extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Approval() {
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
		// TODO Auto-generated method stub
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
        ApprovalDAO dao = new ApprovalDAO();
        switch(action) {
        case "/PUR.do":
        	JSONObject jsonObj = new JSONObject(jsonString);
            Iterator<String> keys = jsonObj.keys();
            while(keys.hasNext()) {
                String key = keys.next();
                Object value = jsonObj.get(key);
                System.out.println(key + " : " + value);
            }
            LoadedData = dao.forPURdata(jsonObj);
        break;
        }
	}

}
