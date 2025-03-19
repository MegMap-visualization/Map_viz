var allowRightclick = true
map.on("rightclick", function (ev) {
    if (allowRightclick) {
        var lnglat = ev.lnglat;
        // 在地图上指定位置打开右键菜
        // ev获取的坐标就是wgs不是gcj
        wgsLonLat = gcj2wgs(lnglat.lng, lnglat.lat)
        // wgsLonLat = [lnglat.lng, lnglat.lat]
        wgsStr = "\nwgs: " + wgsLonLat[1] + "," + wgsLonLat[0]
        $.get(baseUrl + "/transfer/wgs2utm?lon=" + wgsLonLat[0] + "&lat=" + wgsLonLat[1], function (data) {
            utm_x_y = data.data
            console.log(utm_x_y)
            utmStr = "utm: " + utm_x_y[0] + "," + utm_x_y[1] + "; utm_id：" + utm_x_y[2]
        }).then(function () {
            contextMenu.addItem(utmStr, function () {
                navigator.clipboard.writeText(utmStr)
            }, 0);
        })

        var contextMenu = new AMap.ContextMenu();
        contextMenu.addItem(wgsStr, function () {
            navigator.clipboard.writeText(utm_x_y[0] + "," + utm_x_y[1])
        }, 0);
        contextMenu.open(map, [lnglat.lng, lnglat.lat]);
    }
})

function getInfoByCoordinate() {
    if (!allowClickAndMove) {
        return;
    }
    if (forbidRefreshPartXml) {
        promptBox("请打开局部刷新后再试", "warning", 500)
        return
    }
    // console.log(map.getZoom())
    ltRbLonLat = getContainerLtRbLonLat()
    // console.log(ltRbLonLat)
    // console.log(globalRemark)
    if (map.getZoom() > partZoom) {
        addTitleCircle()
        if (showLine) {
            removeTitleCircle()
            return
        }
        $.get(baseUrl + "/getInfoByCoordinate?ltLonLat=" + ltRbLonLat[0] + "&rbLonLat=" + ltRbLonLat[1] +
            "&remark=" + globalRemark,
            function (data) {
                zipdata = data.data
                if (zipdata == null) {
                    promptBox("该区域无数据", "danger", 500)
                    removeTitleCircle()
                } else {
                    console.log(zipdata)
                    // console.log([...zipdata.lanes,...zipdata.leftBorders])
                    // drawCoordinateLane([...zipdata.lanes,...zipdata.leftBorders])
                    // drawCenterLane(zipdata.centerLanes)
                    if(zipdata.leftBorders) drawCoordinateLane([...zipdata.lanes,...zipdata.leftBorders])
                    else drawCoordinateLane(zipdata.lanes)
                    drawCoordinateCenterLane(zipdata.centerLanes)
                    removeTitleCircle()
                }
            });
    } else {
        promptBox("Zoom大于" + partZoom + "时区域加载生效", "warning", 500)
    }
}

map.on("click", function (e) {
    getInfoByCoordinate()
})

map.on('dragend', function (ev) {
    getInfoByCoordinate()
});


map.on('mousemove', function (ev) {
    var lnglat = ev.lnglat;
    // console.log(lnglatg)
    // ev获取的坐标就是wgs不是gcj
    // wgsLonLat = gcj2wgs(lnglat.lng, lnglat.lat)
    wgsLonLat = [lnglat.lng, lnglat.lat]
    $("#latDiv").text("Lat: " + String(wgsLonLat[1]).substring(0, 12))
    $("#lonDiv").text("Lon: " + String(wgsLonLat[0]).substring(0, 12))
});

$("#confirm").on('click', function() {
    // 点击确认按钮时的操作，可以根据需要自定义
    globalRemark = $("#xmlList .li-selected").text();
    getAllCenterLaneByRemark(globalRemark)
    $("#dialog").dialog("close");
});


$(document).on("mousewheel DOMMouseScroll", function (event) {
    zoom = map.getZoom()
    $("#zoom").text("Zoom：" + zoom)
});
