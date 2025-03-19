var satellite = new AMap.TileLayer.Satellite({
    visible: true
});
var roadNet = new AMap.TileLayer.RoadNet({
    visible: false
});
var traffic = new AMap.TileLayer.Traffic({
    visible: false
});
var initZoom = 17
var preXml
var globalRemark
var xmlNameList
var subMaps = {}
var isolatedRoads = {}
var selectSubMaps = []
var selectIsolatedRoads = []
$("#zoom").text("Zoom：" + initZoom)
var map = new AMap.Map("container", {
    resizeEnable: true,
    zoom: initZoom,
    expandZoomRange: true,
    center: ["116.326468", "39.984477"],
    zooms: [3, 20],
    defaultCursor: "pointer",
    viewMode: "2D",
    pitch: 30,
    layers: [
        satellite,
        traffic,
        roadNet,
    ]
});

var autoOptions = {
    input: "tipinput",
};

$(document).ready(function() {
    $("#dialog").dialog({
      autoOpen: false, // 初始设置为不自动打开
      modal: true, // 设置为模态对话框
    })
})

$("#search_input").on('input propertychange', function(e) {
    const searchInfo = $(this).val()
    const reg = new RegExp(searchInfo, 'i')
    const xmlArr = $('#xmlList').children('div')
    for(let i = 0; i < xmlArr.length; i++) {
        if(reg.test(xmlArr[i].innerHTML)) {
            $('#xmlList').find('div').eq(i).show()
        } else {
            $('#xmlList').find('div').eq(i).hide()
        }
    }
})


AMap.plugin(['AMap.PlaceSearch', 'AMap.AutoComplete'], function () {
    var auto = new AMap.AutoComplete(autoOptions);
    var placeSearch = new AMap.PlaceSearch({
        map: map
    }); //构造地点查询类
    auto.on("select", select); //注册监听，当选中某条记录时会触发
    function select(e) {
        placeSearch.setCity(e.poi.adcode);
        placeSearch.search(e.poi.name); //关键字查询查询
    }
});

function xmlListButton() {
    $.get(baseUrl + "/getXmlList", function (data) {
        xmlNameList = data.data
        createXmlList(xmlNameList)
    })
}

function createXmlList(list) {
    var xmlList = document.getElementById("xmlList")
    let a = ""
    for (let i = 0; i < list.length; i++) {
        if(list[i] === globalRemark) {
            a += '<div class="li-selected">' + list[i] + '</div>'
        } else {
            a += '<div>' + list[i] + '</div>'
        }
    }
    xmlList.innerHTML = a
    $('#search_input').val('')
    $("#dialog").dialog("open");
    preXml = $("#xmlList .li-selected")

    $('#xmlList div').on('click', function(e) {
        if(preXml) preXml.removeClass('li-selected')
        preXml = $(this)
        $(this).addClass("li-selected");
    })
}

var clickCircle = $("#loading")

function addTitleCircle() {
    $(clickCircle).css("display", "inline-block")
}

function removeTitleCircle() {
    $(clickCircle).css("display", "none")
}


createInitWorker()
