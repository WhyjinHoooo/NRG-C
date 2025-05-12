<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>테스트</title>
</head>
<body>
<form action="upload.do" method="POST" enctype="multipart/form-data">
	<input type="file" name="textFile" accept=".txt" required>
	<input type="submit" value="업로드">
</form>
</body>
</html>