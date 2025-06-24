package MatlCosting;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		// TODO Auto-generated method stub
		LocalDateTime now = LocalDateTime.now();
		//String ClDate = (now.format(DateTimeFormatter.ofPattern("yyyy-MM"))).replace("-", ""); //이렇게 하면 202504처럼 저장됨
		String ClDate = "202504"; // 일단은 강제로 설정해줌
		String ComCode = request.getParameter("ComCode");
		String action = request.getPathInfo();
		
		MatCostCalcDAO Dao = new MatCostCalcDAO();
		String DaoResult = null;
		switch(action) {
		case"/RawmPriceCalc.do":
			DaoResult = Dao.CostCalcFun(ClDate, ComCode);
		}
	}

}
