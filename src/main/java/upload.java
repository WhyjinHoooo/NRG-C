

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
/*import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;*/
import javax.servlet.http.*;
/**
 * Servlet implementation class upload
 */
@WebServlet(name = "upload.do", urlPatterns = { "/upload.do" })
@MultipartConfig(
	    location = "c:/temp",
	    maxFileSize = 1024 * 1024 * 5,
	    maxRequestSize = 1024 * 1024 * 10
	)
public class upload extends HttpServlet {
	private static final long serialVersionUID = 1L;
	

    private String detectEncoding(byte[] fileBytes) {
        String[] encodings = {"CP949", "UTF-8"};
        
        for (String encoding : encodings) {
            try {
                new String(fileBytes, encoding).getBytes(encoding);
                return encoding;
            } catch (UnsupportedEncodingException e) {
                continue;
            }
        }
        return "UTF-8"; // 기본값
    }
    private List<String[]> convertTxtToCsv(InputStream input) throws IOException {
        List<String[]> result = new ArrayList<>();
        
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int nRead;
        byte[] data = new byte[1024];
        while ((nRead = input.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        buffer.flush();
        byte[] fileBytes = buffer.toByteArray();

        String detectedEncoding = detectEncoding(fileBytes);
        String convertedString = new String(fileBytes, detectedEncoding);

        if(!"UTF-8".equals(detectedEncoding)) {
            convertedString = new String(convertedString.getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
        }

        try (BufferedReader reader = new BufferedReader(new StringReader(convertedString))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if(!line.isEmpty()) {
                	String[] row = line.split("\\t+");
                    //System.out.println("변환된 행: " + Arrays.toString(row));
                    result.add(row);
                }
            }
        }
        return result;
    }

	private String getSubmittedFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
	@Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // [수정] JSP로 forward하지 않고 JSON으로 직접 응답
        response.setContentType("application/json; charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {
            Part filePart = request.getPart("textFile");
            String fileName = getSubmittedFileName(filePart);
            System.out.println("fileName : " + fileName);
            if(!fileName.toLowerCase().endsWith(".txt")) {
                out.print("{\"result\":\"fail\",\"message\":\".txt 파일만 업로드 가능합니다\"}");
                return;
            }

            // 임시 파일 생성 및 복사
            File tempFile = File.createTempFile("upload_", ".tmp");
            try (InputStream in = filePart.getInputStream();
                 OutputStream outFile = new FileOutputStream(tempFile)) {
                in.transferTo(outFile);
            }

            List<String[]> csvData;
            try (InputStream fileContent = new FileInputStream(tempFile)) {
                csvData = convertTxtToCsv(fileContent);
            }

            if(!tempFile.delete()) {
                System.err.println("임시 파일 삭제 실패: " + tempFile.getAbsolutePath());
            }

            UploadDAO dao = new UploadDAO();
			String dbResult = dao.insertCsvData(csvData);

            // [수정] JSON으로 결과 반환
            out.print("{");
            out.print("\"result\":\"success\",");
            out.print("\"fileName\":\"" + fileName + "\",");
            out.print("\"rowCount\":" + csvData.size() + ",");
            //out.print("\"dbResult\":\"" + dbResult + "\"");
            out.print("}");
        } catch (Exception e) {
            out.print("{\"result\":\"fail\",\"message\":\"파일 처리 오류: " + e.getMessage().replace("\"","\\\"") + "\"}");
        } finally {
            out.flush();
            out.close();
        }
    }
}
