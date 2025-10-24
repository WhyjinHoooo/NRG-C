function InitialTable() {
    $('.ACInputTable-Body').empty();
    var HeaderLength = $('thead.ACInputTable-Header th').length;
    for (let i = 0; i < 1000; i++) {
        const row = $('<tr></tr>');
        for (let j = 0; j < HeaderLength; j++) {
            row.append('<td></td>');
        }
        $('.ACInputTable-Body').append(row);
    }
}

function DateSetting() {
    var CurrentDate = new Date();
    var today = CurrentDate.getFullYear() + '-' + ('0' + (CurrentDate.getMonth() + 1)).slice(-2) + '-' + ('0' + CurrentDate.getDate()).slice(-2);
    $('.RegistedDate').attr('max', today);
}

function PopupPosition(popupWidth, popupHeight) {
    var dualScreenLeft = window.screenLeft !== undefined ? window.screenLeft : window.screenX;
    var dualScreenTop = window.screenTop !== undefined ? window.screenTop : window.screenY;

    var width = window.innerWidth ? window.innerWidth : document.documentElement.clientWidth ? document.documentElement.clientWidth : screen.width;
    var height = window.innerHeight ? window.innerHeight : document.documentElement.clientHeight ? document.documentElement.clientHeight : screen.height;
    var xPos, yPos;
    if (width == 2560 && height == 1440) {
        xPos = (2560 / 2) - (popupWidth / 2);
        yPos = (1440 / 2) - (popupHeight / 2);
    } else if (width == 1920 && height == 1080) {
        xPos = (1920 / 2) - (popupWidth / 2);
        yPos = (1080 / 2) - (popupHeight / 2);
    } else {
        var monitorWidth = 2560;
        var monitorHeight = 1440;
        xPos = (monitorWidth / 2) - (popupWidth / 2) + dualScreenLeft;
        yPos = (monitorHeight / 2) - (popupHeight / 2) + dualScreenTop;
    }
    return { x: xPos, y: yPos };
}

function InfoSearch(field, event) {
    event.preventDefault();
    var popupWidth = 500;
    var popupHeight = 600;
    var ComCode = $('.ComCode').val();
    var position = PopupPosition(popupWidth, popupHeight);
    var MoveType = event.target.name;

    switch (field) {
        case "ComSearch":
            window.open(contextPath + "/Pop/ComSerach.jsp", "PopUp01",
                "width=" + popupWidth + ",height=" + popupHeight + ",left=" + position.x + ",top=" + position.y);
            break;
        case "PlantSearch":
            window.open(contextPath + "/Pop/PlantSearch.jsp?Comcode=" + ComCode, "PopUp02",
                "width=" + popupWidth + ",height=" + popupHeight + ",left=" + position.x + ",top=" + position.y);
            break;
    }
}

$(document).ready(function () {
    InitialTable();
    DateSetting();
    var FilterList = {};
    var CalcMonth = document.getElementById('CalcMonth');
    var RecentYear = new Date().getFullYear();
    var RecentMonth = new Date().getMonth() + 1;
    var EndYear = RecentYear - 30;

    for (let year = RecentYear, month = RecentMonth; year >= EndYear;) {
        var Option = document.createElement('option');
        var monthStr = month < 10 ? '0' + month : '' + month;
        Option.value = year + monthStr;
        Option.textContent = year + monthStr;
        CalcMonth.appendChild(Option);
        month--;
        if (month === 0) {
            month = 12;
            year--;
        }
    }

    $('.SearBtn').click(function () {
        $('.ACInputTable-Body').empty();
        $('.SearOp').each(function () {
            var name = $(this).attr('name');
            var value = $(this).val();
            FilterList[name] = value;
        });
        var pass = true;
        $.each(FilterList, function (key, value) {
            if (value == null || value === '') {
                pass = false;
                return false;
            }
        })
        if (!pass) {
            alert('모든 필수 항목을 모두 입력해주세요.');
        } else {
            $.ajax({
                url: contextPath + '/ProGoodsCostAlloSet/GoodsCostLoad.do',
                type: 'POST',
                data: JSON.stringify(FilterList),
                contentType: 'application/json; charset=utf-8',
                dataType: 'json',
                async: false,
                success: function (data) {
                    if (data.result === "success") {
                        if (data.List.length > 0) {
                            for (var i = 0; i < data.List.length; i++) {
                                var row = '<tr>' +
                                    '<td>' + (data.List[i].ClosingMon ?? 'N/A') + '</td>' +
                                    '<td>' + (data.List[i].ComCode ?? 'N/A') + '</td>' +
                                    '<td>' + (data.List[i].PlantCode ?? 'N/A') + '</td>' +
                                    '<td>' + (data.List[i].WorkType ?? 'N/A') + '</td>' +
                                    '<td>' + (data.List[i].CostingLev ?? 'N/A') + '</td>' +
                                    '<td>' + (data.List[i].WorkOrd ?? 'N/A') + '</td>' +
                                    '<td>' + (data.List[i].SumOfWMC ?? 'N/A') + '</td>' +
                                    '<td>' + (data.List[i].SumOfWMfC ?? 'N/A') + '</td>' +
                                    '<td>' + (data.List[i].SumOfFMC ?? 'N/A') + '</td>' +
                                    '<td>' + (data.List[i].SumOfFMfC ?? 'N/A') + '</td>' +
                                    '</tr>';
                                $('.ACInputTable-Body').append(row);
                            }
                        }
                    } else {
                        alert("실패: 검색 항목을 다시 선택해주세요.");
                    }
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    alert('조회 오류 발생: ' + textStatus + ', ' + errorThrown);
                }
            });
        }
    });

    $('.OkBtn').click(function () {
        $('.SearOp').each(function () {
            var name = $(this).attr('name');
            var value = $(this).val();
            FilterList[name] = value;
        });
        var pass = true;
        $.each(FilterList, function (key, value) {
            if (value == null || value === '') {
                pass = false;
                return false;
            }
        })
        if (!pass) {
            alert('모든 필수 항목을 모두 입력해주세요.');
        } else {
            $.ajax({
                url: contextPath + '/ProGoodsCostAlloSet/GoodsCostCalc.do',
                type: 'POST',
                data: JSON.stringify(FilterList),
                contentType: 'application/json; charset=utf-8',
                dataType: 'json',
                async: false,
                success: function (data) {
                    if (data.result === "success") {
                        alert('분배가 정상적으로 진행됐습니다.');
                    } else {
                        alert('분배 실패: ' + (data.message || '원인 불명'));
                    }
                    location.reload();
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    alert('분배 오류 발생: ' + textStatus + ', ' + errorThrown);
                }
            });
        }
    });

    $('.QueryBtn').click(function () {
        alert('asd1');
    });
});
