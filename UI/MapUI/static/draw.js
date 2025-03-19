var objectDic = {}
var signalDic = {}
var objectsDic = {}

function drawObject(type, objects, weight = 3) {
    for (var object in objects) {
        points = objects[object]["points"]
        obj = objects[object]
        var color = "#FFF"
        switch (type) {
            case "signals":
                color = "#3F9"
                weight = 3
                break;
        }

        var myExtData = objects[object]
        myExtData["oldColor"] = color
        myExtData["type"] = type
        myExtData["oldWeight"] = weight
        var myObj = new AMap.Polyline({
            cursor: "ani",
            map: map,
            path: points,
            showDir: false,
            lineJoin: "round", //折线拐点样式
            // strokeColor: lanes[lane]["color"], //线颜色
            strokeColor: color, //线颜色
            strokeOpacity: 1, //线透明度
            strokeWeight: weight, //线宽
            strokeStyle: "solid", //线样式
            extData: myExtData,
            zIndex: 9999
        })

        switch (type) {
            case "signals":
                signalDic[obj["signalId"]] = myObj
                break;
            case "junctions":
                objectDic[obj["junctionId"]] = myObj
                break;
            default:
                objectDic[obj["objectId"]] = myObj
                break;
        }


        myObj.on("click", function (e) {
            point = new Array(e.lnglat.getLng(), e.lnglat.getLat())
            // this.setOptions({"strokeColor": "red"})
            setWinInfo(this.getExtData())
        });
        myObj.on("mouseover", function () {
            var extData = this.getExtData()
            this.setOptions({
                "strokeColor": "#00BFFF"
            })
            switch (extData["type"]) {
                case "signals":
                    stoplineList = JSON.parse(extData["stoplineIds"])
                    for (var stoplineId in stoplineList) {
                        stoplineObj = objectDic[stoplineList[stoplineId]]
                        stoplineObj.setOptions({
                            "strokeColor": "red"
                        })
                    }
                    break;
            }
        })
        myObj.on("mouseout", function () {
            var extData = this.getExtData()
            this.setOptions({
                "strokeColor": extData["oldColor"]
            })
            switch (extData["type"]) {
                case "signals":
                    stoplineList = JSON.parse(extData["stoplineIds"])
                    for (var stoplineId in stoplineList) {
                        stoplineObj = objectDic[stoplineList[stoplineId]]
                        stoplineObj.setOptions({
                            "strokeColor": stoplineObj.getExtData()["oldColor"]
                        })
                    }
                    break;
            }
        })
    }
}

var centerLaneDic = {}

function drawCenterLane(lanes, weight = 1) {
    for (var lane in lanes) {
        color = "#0eea08"
        points = lanes[lane]["points"]
        var myExeData = lanes[lane]
        myExeData["oldColor"] = color
        var line = new AMap.Polyline({
            title: lanes[lane]["uid"],
            cursor: "ani",
            map: map,
            path: points,
            // path: points,
            showDir: false,
            lineJoin: "round", //折线拐点样式
            // strokeColor: lanes[lane]["color"], //线颜色
            strokeColor: color, //线颜色
            strokeOpacity: 1, //线透明度
            strokeWeight: showCenter ? 0 : 1, //线宽
            strokeStyle: "solid", //线样式
            extData: myExeData
        })
        centerLaneDic[myExeData["laneUid"]] = line
    }
}

//原始数据
var lineDic = {}
var centerRefLaneDic = {}
//只保存原始数据的key
var roadDic = {}
var roadSectionDic = {}
// var sectionDic = {}
var globalLineColor = []

function changeDomColorAttr(laneUids, color, domDic) {
    
    if (laneUids === undefined || laneUids.length === 0)
        return
    for (const index in laneUids) {
        let laneUid = laneUids[index]
        if (globalLineColor.includes(laneUid)) {
            continue
        }
        try {
            if (domDic === undefined) {
                domDic = lineDic[laneUid] ? lineDic : centerRefLaneDic
            }
            let thisLine = domDic[laneUid]
            thisLine.setOptions({
                "strokeColor": color
            })
        } catch (TypeError) {

        }
    }
}

function recoverDomColorAttr(laneUids, domDic) {
    for (const index in laneUids) {
        let laneUid = laneUids[index]
        if (globalLineColor.includes(laneUid)) {
            continue
        }
        try {
            if (domDic === undefined) {
                domDic = lineDic[laneUid] ? lineDic : centerRefLaneDic
            }
            let thisLine = domDic[laneUid]
            const extData = thisLine.getExtData();
            thisLine.setOptions({
                "strokeColor": extData['childColor'] || extData["oldColor"]
            })
        } catch (TypeError) {
            continue
        }
    }
}

function drawLane(lanes, weight = 2) {
    for (var lane in lanes) {
        // console.log(lanes[lane].points)
        var color = lanes[lane]["color"]
        var lines = lanes[lane].laneBorderType
        if (lanes[lane]["isVirtual"] === "TRUE" || lines === 'virtual' || lanes[lane]["type"]=="leftBorder") {
            color = "#B0C4DE"
        }
        if (lines === 'solid' || lines === 'curb') {
            lines = 'solid'
        } else {
            lines = 'dashed'
        }
        // console.log(lanes[lane]["points"])
        points = lanes[lane]["points"]
        if (points == null) {
            continue
        }

        var myExeData = lanes[lane]
        myExeData["oldColor"] = color
        myExeData["oldWeight"] = weight
        var line = new AMap.Polyline({
            title: lanes[lane]["uid"],
            cursor: "ani",
            map: map,
            path: points,
            // path: points,
            showDir: false,
            lineJoin: "round", //折线拐点样式
            // strokeColor: lanes[lane]["color"], //线颜色
            strokeColor: color, //线颜色
            strokeOpacity: 1, //线透明度
            strokeWeight: weight, //线宽
            strokeStyle: lines, //线样式
            extData: myExeData
        })
        line.on("click", function (e) {
            point = new Array(e.lnglat.getLng(), e.lnglat.getLat())
            // this.setOptions({"strokeColor": "red"})
            setWinInfo(this.getExtData())
        });
        lane_uid = lanes[lane]["uid"]
        if (lanes[lane]["laneId"] == "0") {
            centerRefLaneDic[lane_uid] = line
        } else {
            lineDic[lane_uid] = line
            id_list = lane_uid.split("_")
            road_section_id = id_list[0] + "_" + id_list[1]
            road_id = id_list[0]
            if (!roadDic.hasOwnProperty(road_id)) {
                roadDic[road_id] = new Array()
            }
            if (!roadSectionDic.hasOwnProperty(road_section_id)) {
                roadSectionDic[road_section_id] = new Array()
            }
            roadDic[road_id].push(lane_uid)
            roadSectionDic[road_section_id].push(lane_uid)
        }

        line.on("mouseover", function (e) {
            // console.log('mouseover',this.getExtData().isVirtual, this.getExtData(), lineDic, centerLaneDic)
            let extData = this.getExtData()
            changeDomColorAttr(JSON.parse(extData["signalOverlapIds"]), "red", signalDic)
            changeDomColorAttr(JSON.parse(extData["objectOverlapIds"]), "red", objectDic)


            changeDomColorAttr([extData["uid"]], "#00BFFF")
            changeDomColorAttr(extData["pre"].split(";"), "#FFA500")
            changeDomColorAttr(extData["suc"].split(";"), "#00FF00")
            changeDomColorAttr([extData["leftUid"]], "#8A2BE2")
            changeDomColorAttr([extData["rightUid"]], "#FFC0CB")
        });

        line.on("mouseout", function (e) {
            let extData = this.getExtData()

            recoverDomColorAttr(JSON.parse(extData["signalOverlapIds"]), signalDic)
            recoverDomColorAttr(JSON.parse(extData["objectOverlapIds"]), objectDic)

            recoverDomColorAttr(([extData["uid"]]))
            recoverDomColorAttr(extData["pre"].split(";"))
            recoverDomColorAttr(extData["suc"].split(";"))
            recoverDomColorAttr([extData["leftUid"]])
            recoverDomColorAttr([extData["rightUid"]])
        });
    }
}

function drawPoints(lanes) {
    // 临时绘制不清除原有图层数据，便于对比
    // clearMapAndCache()
    // console.log(lanes)
    temp_lanes = new Array()
    for (var lane in lanes) {
        temp_line = new AMap.Polyline({
            map: map,
            path: lanes[lane],
            showDir: false,
            // strokeColor: lanes[lane]["color"], //线颜色
            strokeColor: "red", //线颜色
            strokeOpacity: 1, //线透明度
            strokeWeight: 2, //线宽
            strokeStyle: "solid" //线样式
        })
        temp_lanes.push(temp_line)
    }
    map.setFitView(temp_lanes)
}

function clearAllLane() {
    tempDelList = []
    for (var uid in lineDic) {
        tempDelList.push(lineDic[uid])
    }
    lineDic = {}
    roadDic = {}
    roadSectionDic = {}
    map.remove(tempDelList)
}

function drawCoordinateLane(lanes) {
    tempDelList = []
    for (var uid in lineDic) {
        tempDelList.push(lineDic[uid])
    }
    lineDic = {}
    roadDic = {}
    roadSectionDic = {}
    map.remove(tempDelList)
    drawLane(lanes)
}

function drawAllLane(lanes) {
    tempDelList = []
    for (var uid in lineDic) {
        tempDelList.push(lineDic[uid])
    }
    lineDic = {}
    roadDic = {}
    roadSectionDic = {}
    map.remove(tempDelList)
    drawLane(lanes)
}


function drawCoordinateCenterLane(lanes) {
    tempDelList = []
    for (var uid in centerLaneDic) {
        tempDelList.push(centerLaneDic[uid])
    }
    // centerLaneDic.clear()
    centerLaneDic = {}
    map.remove(tempDelList)
    drawCenterLane(lanes)
}
