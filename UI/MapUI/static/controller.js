$("#open_button").mouseover(function () {
    $(this).attr("class", "iconfont icon-guanbi1")
})
$("#open_button").mouseout(function () {
    $(this).attr("class", "iconfont icon-zhankai")
})


var showOtherButton = true

function openOtherButton() {
    if (showOtherButton) {
        $("#bottonUl").fadeIn()
        $("#open_button").attr("class", "iconfont icon-guanbi1")
        showOtherButton = false
    } else {
        $("#bottonUl").fadeOut()
        $("#open_button").attr("class", "iconfont icon-zhankai")
        showOtherButton = true
    }
}

var delLayers = true;

function removeLayers() {
    if (delLayers) {
        $('#bottonUl #layers').css("color", "white")
        $("#layerTraffic").css("color", "white")
        traffic.hide()
        roadNet.hide()
        satellite.hide()
        delLayers = false
    } else {
        $('#bottonUl #layers').css("color", "#25A5F7")
        satellite.show()
        delLayers = true
    }
}

var layerRoute = true

function showLayersRoute() {
    if (layerRoute) {
        $("#layerRoute").attr("class", "iconfont icon-weixing")
        roadNet.show()
        map.setLayers([roadNet])
        layerRoute = false
    } else {
        $("#layerRoute").attr("class", "iconfont icon-route1-fill")
        roadNet.hide()
        map.setLayers([satellite])
        layerRoute = true
    }
}

var layerTraffic = true

function showLayerTraffic() {
    if (layerTraffic) {
        $("#layerTraffic").css("color", "#25A5F7")
        traffic.show()
        layerTraffic = false
    } else {
        $("#layerTraffic").css("color", "white")
        traffic.hide()
        layerTraffic = true
    }
}

var showLine = false

function hideMapContainer() {

    if (!showLine) {
        $("#hideMap").css("color", "#25A5F7")
        for (let tempUid in lineDic) {
            lineDic[tempUid].setOptions({
                "strokeWeight": 0
            })
        }
        for (let i in objectDic) {
            objectDic[i].setOptions({
                "strokeWeight": 0
            })
        }
        for (let i in signalDic) {
            signalDic[i].setOptions({
                "strokeWeight": 0
            })
        }
        showLine = true
    } else {
        $("#hideMap").css("color", "white")
        for (let tempUid in lineDic) {
            lineDic[tempUid].setOptions({
                "strokeWeight": 2
            })
        }
        for (let i in objectDic) {
            objectDic[i].setOptions({
                "strokeWeight": 3
            })
        }
        for (let i in signalDic) {
            signalDic[i].setOptions({
                "strokeWeight": 2
            })
        }
        showLine = false
    }
}

showCenter = true

function hidecenter() {
    if (!showCenter) {
        $("#hidecenter").css("color", "white")
        for (let i in centerLaneDic) {
            centerLaneDic[i].setOptions({
                "strokeWeight": 0
            })
        }
        showCenter = true
    } else {
        $("#hidecenter").css("color", "#25A5F7")
        for (let i in centerLaneDic) {
            centerLaneDic[i].setOptions({
                "strokeWeight": 1
            })
        }

        showCenter = false
    }
}

showGuides = true

function hideGuides() {
    if (!showGuides) {
        $("#hideGuides").css("color", "white")
        for (let i in centerRefLaneDic) {
            if (centerRefLaneDic[i]._opts !== null) {
                centerRefLaneDic[i].setOptions({
                    "strokeWeight": 2
                })
            }
        }
        showGuides = true
    } else {
        $("#hideGuides").css("color", "#25A5F7")
        for (let i in centerRefLaneDic) {
            if (centerRefLaneDic[i]._opts !== null) {
                centerRefLaneDic[i].setOptions({
                    "strokeWeight": 0
                })
            }

        }

        showGuides = false
    }
}


var forbidRefreshPartXml = false

function forbidRefresh() {
    if (forbidRefreshPartXml) {
        $("#forbidRefresh").css("color", "white")
        forbidRefreshPartXml = false
    } else {
        $("#forbidRefresh").css("color", "#25A5F7")
        forbidRefreshPartXml = true
    }
}

function help() {
    window.open("https://wiki.megvii-inc.com/pages/viewpage.action?pageId=415810938");
}

// left_bar_button
var displays = {
    //button_id: 对应的div展示框
    search_button: "#left_top_search",
    draw_button: "#left_top_draw",
    local_button: "#left_top_local_search",
    xmls_button: "#left_top_xml_list",
    child_xml_button: "#left_top_child_xml",
    test_button: "#left_top_test",
    check_button: '#left_top_check',
    find_point_button: '#left_top_find'
}

let left_bar_color = "#444"
let left_bar_click_color = "#25A5F7"
let left_bar_select_id = null

for (let domId of Object.keys(displays)) {
    $("#" + domId).on("click", function () {
        let domInfo = displays[domId]
        recoverLeftBarDiv()
        if ($(this).attr("id")!==left_bar_select_id){
            left_bar_select_id = domId
            $("#" + domId).css("color", left_bar_click_color)
            const hide = $(this).prop("hide")
            if(hide) return 
            $(domInfo).slideDown();
        }
    })
}

function recoverLeftBarDiv() {
    for (let domId of Object.keys(displays)) {
        let domInfo = displays[domId]
        $("#" + domId).css("color", left_bar_color)
        $(domInfo).slideUp();
    }
}

var showzhanshi = false

function showxiala() {
    if ($("#con_top ul").html() === '') {
        alert('请先选择要查看的车道!')
    } else {
        if (showzhanshi) {
            $('#con_top_bottom').text('隐藏车道信息')
            $('#con_top ul').slideDown()
            showzhanshi = false
        } else {
            $('#con_top_bottom').text('查看车道信息')
            $('#con_top ul').slideUp()
            showzhanshi = true
        }
    }

}

var doChangeColorRender = false

// 切换到道路颜色渲染方案
function changeColorRender() {
    let centerRefLane;
    if (!doChangeColorRender) {
        $("#changeColorRender").css("color", "#25A5F7")
        for (let uid in centerRefLaneDic) {
            centerRefLane = centerRefLaneDic[uid]
            centerRefLane.setOptions({"strokeColor": roadTypeColorMap[centerRefLane.getExtData()["roadType"]]})
            globalLineColor.push(uid)
        }
        doChangeColorRender = true
        console.log(globalLineColor)
    } else {
        $("#changeColorRender").css("color", "white")
        for (let uid in centerRefLaneDic) {
            centerRefLane = centerRefLaneDic[uid]
            // let amapPolygon = lineDic[referenceLine]
            centerRefLane.setOptions({"strokeColor": centerRefLane.getExtData()["oldColor"]})
        }
        globalLineColor = []
        doChangeColorRender = false
    }
}
