var xmlDic = {}
var mapResult = {}
let globalPoints = ''
let testResults = null
let selectTestItem = []
let testMarkerObjs = {}
function clearAll() {
    map.clearMap();
    xmlDic = {}
    globalSearchObject = []
    objectDic = {}
    signalDic = {}
    objectsDic = {}
    centerLaneDic = {}
    lineDic = {}
    centerRefLaneDic = {}
    roadDic = {}
    roadSectionDic = {}
}

async function getAllCenterLaneByRemark(remark) {
    addTitleCircle()
    clearAll()
    clearShowTest()
    const zipdata = await searchByDB(remark, 1)
    removeTitleCircle()
    console.log(zipdata)
    xmlDic = zipdata.xml
    drawObject("objects", zipdata.objects)
    drawObject("signals", zipdata.signals)
    drawObject("junctions", zipdata.junctions)
    drawLane(zipdata.lanes)
    globalPoints = zipdata.lanes[0].points[0][0].join(',')
    map.setFitView();
    getSubMaps()
}

async function getSubMaps() {
    mapResult.code = null
    mapResult.message = '请求中...'
    const res = await request(`/python/map-routing-inspector/all-submaps?map_path=${xmlDic[0].ossPath}&map_md5=${xmlDic[0].fileMd5}`)
    mapResult.code = res.code
    mapResult.message = res.message
    selectSubMaps = []
    selectIsolatedRoads = []
    if (res.code === 200) {
        isolatedRoads = res.data.isolated_roads
        subMaps = res.data.submaps
    } else {
        isolatedRoads = {}
        subMaps = {}
    }
}

function clearShowTest() {
    for(let i = 0; i < selectTestItem.length; i++) {
        map.remove(testMarkerObjs[selectTestItem[i]])
    }
    document.getElementById('test_list').innerHTML = ''
    $('#show_test_path').prop('value', '')
    testResults = null
    selectTestItem = []
    testMarkerObjs = {}
}

var showAllLanes = false

async function getAllLanes() {
    console.log(globalRemark)
    if (!showAllLanes && globalRemark !== null) {
        showAllLanes = true
        allowClickAndMove=false
        $("#all_button").css("color", left_bar_click_color)
        addTitleCircle()
        forbidRefresh()
        const driveLanes = await searchByDB(globalRemark, 2)
        removeTitleCircle()
        drawAllLane(driveLanes)
        map.setFitView();
    } else {
        showAllLanes = false
        allowClickAndMove=true
        $("#all_button").css("color",left_bar_color)
        clearAllLane()
        forbidRefresh()
        promptBox("请先选择xml", "danger", 1000)
    }
}

function removeSearchObject() {
    for (lane in globalSearchObject) {
        thisObject = globalSearchObject[lane]
        // console.log(thisLane.getExtData()["uid"])
        // console.log(thisLane.getExtData()["oldColor"])
        // console.log(thisLane.getOptions()["strokeColor"])
        oldColor = thisObject.getExtData()["oldColor"]
        thisObject.setOptions({
            "strokeColor": oldColor
        })
    }
    globalSearchLane = []
}

var globalSearchObject = []

function searchPartobiectUid() {
    objectUids = $("#object_uid").val().replace(/[\r\n]/g, "").replace(" ", '')
    if ("object Uid" == objectUids) {
        window.alert("请输入车道objectUids，多个使用分号隔开")
    }
    uids = objectUids.split(";")
    addTitleCircle()
    removeSearchObject()
    objectsDic = Object.assign(signalDic, objectDic)
    // console.log(signalDic,objectDic,objectsDic)
    console.log(objectsDic)
    noSearchObjectSet = new Set()
    for (var uid in uids) {
        if (!objectsDic.hasOwnProperty(uids[uid])) {
            noSearchObjectSet.add(uids[uid])
            continue
        }
        thisObject = objectsDic[uids[uid]]
        extData = thisObject.getExtData()
        extData["oldColor"] = thisObject.getOptions()["strokeColor"]
        thisObject.setExtData(extData)
        //设置颜色
        thisObject.setOptions({
            "strokeColor": "red"
        })
        globalSearchObject.push(thisObject)
        map.setFitView(globalSearchObject)
    }
    if (noSearchObjectSet.size != 0) {
        tempStr = ""
        for (var item of noSearchObjectSet)
            tempStr = tempStr + item + " "
        alert("未搜索到的objectId： " + tempStr)
    }
    removeTitleCircle()
}

removeTitleCircle()


var globalSearchLane = []

function removeSearchLane() {
    for (lane in globalSearchLane) {
        thisLane = globalSearchLane[lane]
        // console.log(thisLane.getExtData()["uid"])
        // console.log(thisLane.getExtData()["oldColor"])
        // console.log(thisLane.getOptions()["strokeColor"])
        oldColor = thisLane.getExtData()["oldColor"]
        thisLane.setOptions({
            "strokeColor": oldColor
        })
    }
    globalSearchLane = []
}

function setSearchLaneColor(thisLine) {
    // 获取自身颜色保存，后续恢复使用
    extData = thisLine.getExtData()
    extData["oldColor"] = thisLine.getOptions()["strokeColor"]
    thisLine.setExtData(extData)
    thisLine.setOptions({
        "strokeColor": "red"
    })
    globalSearchLane.push(thisLine)
}

function setSearchRefLaneColor(way_section_id) {
    // 获取自身颜色保存，后续恢复使用
    thisLine = centerRefLaneDic[way_section_id + "_0"]
    extData = thisLine.getExtData()
    extData["oldColor"] = thisLine.getOptions()["strokeColor"]
    thisLine.setExtData(extData)
    thisLine.setOptions({
        "strokeColor": "red"
    })
    globalSearchLane.push(thisLine)
}

function searchPartLaneUid() {
    laneUids = $("#lane_uid").val().replace(/[\r\n]/g, "").replace(" ", '')
    if ("Lane Uid" == laneUids) {
        window.alert("请输入车道uid，多个使用分号隔开")
    }
    uids = laneUids.split(";")
    addTitleCircle()
    removeSearchLane()
    noSearchLane = new Array()
    for (var uid in uids) {
        laneUid = uids[uid]
        id_list = laneUid.split("_")
        if (id_list.length >= 3) {
            if (!lineDic.hasOwnProperty(laneUid) && !centerRefLaneDic.hasOwnProperty(laneUid)) {
                noSearchLane.push(laneUid)
                continue
            }
            if (id_list[id_list.length - 1] === "0")
                setSearchRefLaneColor(id_list[0] + "_" + id_list[1])
            else
                setSearchLaneColor(lineDic[laneUid])
        } else if (id_list.length == 2) {
            lane_road_section_id = id_list[0] + "_" + id_list[1]
            if (!roadSectionDic.hasOwnProperty(lane_road_section_id) && !centerRefLaneDic.hasOwnProperty(
                lane_road_section_id + "_0")) {
                noSearchLane.push(lane_road_section_id)
                continue
            }
            setSearchRefLaneColor(id_list[0] + "_" + id_list[1])
            tempLanes = roadSectionDic[lane_road_section_id]
            for (var lane in tempLanes) {
                setSearchLaneColor(lineDic[tempLanes[lane]])
            }
        } else if (id_list.length == 1) {
            lane_road_id = id_list[0]
            console.log(lane_road_id)
            if (!roadDic.hasOwnProperty(lane_road_id)) {
                noSearchLane.push(lane_road_id)
                continue
            }
            tempLanes = roadDic[lane_road_id]
            tempSet = new Set()
            for (var lane in tempLanes) {
                road_section_lane_id = tempLanes[lane].split("_")
                tempSet.add(road_section_lane_id[0] + "_" + road_section_lane_id[1])
                setSearchLaneColor(lineDic[tempLanes[lane]])
            }

            for (var item of tempSet) {
                setSearchRefLaneColor(item)
            }
        }
    }

    map.setFitView(globalSearchLane)
    removeTitleCircle()
    if (noSearchLane.length !== 0)
        window.alert("未搜索到的车道：" + noSearchLane.toString() + "  请尝试全局搜索！")
}

// 全局搜索
function searchGlobalLaneUid() {
    laneUids = $("#lane_uid").val().replace(/[\r\n]/g, "").replace(" ", '').split(";")
    if (laneUids.size == 0) {
        promptBox("不能为空！", "warning", 1000)
        return
    }
    if (globalRemark == null) {
        promptBox("请先选择xml", "warning", 1000)
        return
    }
    addTitleCircle()
    centerRefLanes = []
    tempErrUid = []
    reqLis = []
    for (var uid of laneUids) {
        uidList = uid.split("_")
        if (uidList.length != 3) {
            tempErrUid.push(uid)
            continue
        }
        if (uidList[2] == "0") {
            centerRefLanes.push(uid)
            continue
        }
        reqLis.push(uid)
    }
    $.get(baseUrl + "/getPartLaneByUids?uids=" + reqLis.join(";") + "&remark=" + globalRemark, function (data) {
        removeSearchLane()
        if (data.data == null) {
            promptBox(globalRemark + "地图中未搜索任何相关信息", "warning", 1000)
            removeTitleCircle()
            return
        }
        drawLane(data.data.lanes)
    }).then(function (data) {
        for (var uid of centerRefLanes) {
            uidList = uid.split("_")
            setSearchRefLaneColor(uidList[0] + "_" + uidList[1])
        }

        laneUidList = data.data.lanes
        for (var lane of laneUidList) {
            // 获取自身颜色保存，后续恢复使用
            thisLine = lineDic[lane["uid"]]
            setSearchLaneColor(thisLine)
        }
        noSearchArr = []
        for (var uid in laneUids) {
            if (!lineDic.hasOwnProperty(uid))
                noSearchArr.push(uid)
        }
        if (tempErrUid.length != 0)
            alert("输入不合法！请输入完整的way_section_lane_id：" + tempErrUid.join(", "))
        promptBox(globalRemark + "中未搜索到的车道：" + noSearchArr.join(", "), "warning", 2500)
        map.setFitView(globalSearchLane)
        removeTitleCircle()
    })

}
