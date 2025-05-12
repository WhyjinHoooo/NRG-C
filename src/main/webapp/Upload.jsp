<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>jQuery AJAX 파일 업로드</title>
<script src="https://code.jquery.com/jquery-3.6.0.min.js"></script> <!-- [추가] -->
</head>
<body>
<input type="file" id="textFile" accept=".txt" required>
<button id="uploadBtn">업로드</button>
<div id="result"></div>
<script>
$('#uploadBtn').click(function() {
    var fileInput = $('#textFile')[0];
    if (!fileInput.files.length) {
        alert('파일을 선택하세요!');
        return;
    }
    var formData = new FormData();
    formData.append("textFile", fileInput.files[0]);
    $.ajax({
        url: 'upload.do',
        type: 'POST',
        data: formData,
        dataType: 'json',
        processData: false, // [필수]
        contentType: false, // [필수]
        success: function(res) {
        	console.log(res.result)
            if(res.result === "success") {
                $('#result').html(
                    '<b>파일명:</b> ' + res.fileName + '<br>' +
                    '<b>행 개수:</b> ' + res.rowCount + '<br>' +
                    '<b>DB 저장 결과:</b> ' + res.dbResult + '<br>'
                );
            } else {
                $('#result').html('<span style="color:red;">오류: ' + res.message + '</span>');
            }
        },
        error: function(xhr) {
            $('#result').html('<span style="color:red;">업로드 실패: ' + xhr.statusText + '</span>');
        }
    });
});
</script>
</body>
</html>
