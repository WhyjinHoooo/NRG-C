

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		try {
            Part filePart = request.getPart("textFile");
            String fileName = getSubmittedFileName(filePart);
            System.out.println("fileName : " + fileName);
            
            if(!fileName.toLowerCase().endsWith(".txt")) {
                throw new ServletException(".txt 파일만 업로드 가능합니다");
            }

            // 임시 파일 생성 및 복사
            File tempFile = File.createTempFile("upload_", ".tmp");
            try (InputStream in = filePart.getInputStream();
                 OutputStream out = new FileOutputStream(tempFile)) {
                in.transferTo(out);
            }

            // 변환 작업 (수정된 부분)
            List<String[]> csvData;
            try (InputStream fileContent = new FileInputStream(tempFile)) {
                csvData = convertTxtToCsv(fileContent);
            }

            // 임시 파일 삭제 (반드시 실행)
            if(!tempFile.delete()) {
                System.err.println("임시 파일 삭제 실패: " + tempFile.getAbsolutePath());
            }
            
            UploadDAO dao = new UploadDAO();
            
            request.setAttribute("fileName", fileName);
            request.setAttribute("csvData", csvData);
            request.setAttribute("pass", dao.insertCsvData(csvData));
            request.getRequestDispatcher("/process.jsp").forward(request, response);
        } catch (Exception e) {
            request.setAttribute("error", "파일 처리 오류: " + e.getMessage());
            request.getRequestDispatcher("/errorPage.jsp").forward(request, response);
        }
	}
}
