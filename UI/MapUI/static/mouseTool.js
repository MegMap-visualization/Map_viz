
var mouseTool = new AMap.MouseTool(map);

globalPolyList = []
AMap.Event.addListener(mouseTool, 'draw', function (e) {
    gcjPath = e.obj.getPath();//获取路径/范围
    // console.log(gcjPath)
    wgsPath = []
    utmPath = []
    for (var i in gcjPath) {
        gcjPoint = gcjPath[i]
        wgsPoint = gcj2wgs(gcjPoint.lng, gcjPoint.lat)
        $.ajax({
            type: "get",
            url: baseUrl + "/transfer/wgs2utm?lon=" + wgsPoint[0] + "&lat=" + wgsPoint[1],
            async: false,
            success: function (data) {
                utmPath.push([parseFloat(data.data[0]),parseFloat(data.data[1])])
                wgsPath.push([parseFloat(wgsPoint[0]), parseFloat(wgsPoint[1])])
            }
        });
    }
    globalPolyList.push({"latlon": wgsPath, "utm": utmPath})
});

let countPoly = 0
let allowClickAndMove = true

function drawPolygonGetWgs() {
    countPoly = countPoly + 1
    if (countPoly % 2 == 0) {
        $('#frame_button').css("color", left_bar_color)
        if (globalPolyList.length != 0) {
            areaJson = JSON.stringify({"planc_areas": globalPolyList}, null, 4)
            var input = document.getElementById("input");
            input.value = areaJson; // 修改文本框的内容
            input.select(); // 选中文本
            document.execCommand("copy"); // 执行浏览器复制命令
            $('.alert').html('框选坐标结束（已复制到粘贴板）').addClass('alert-success').show().delay(2500).fadeOut();
            const file = new File([areaJson], "polygon.json", {
                type: "text/plain",
            });
            download(file);
        }
        mouseToolClear()
        allowClickAndMove = true
        allowRightclick = true
    } else {
        globalPolyList = []
        $('#frame_button').css("color", left_bar_click_color)
        allowRightclick = false
        allowClickAndMove = false
        mouseTool.polygon();
    }

}

async function chooseChildXml() {
    if(!globalRemark) {
        promptBox("请先选择一个xml", "warning", 1000)
        $('#child_xml_button').prop("hide", true)
        return
    }
    if (mapResult.code !== 200) {
        promptBox(mapResult.message, "warning", 1000)
        $('#child_xml_button').prop("hide", true)
        return
    }
    $('#child_xml_button').prop("hide", false)
    setSubMapsList(true)
}
function setSubMapsList(isSub) { // true 子图 false 孤岛
    const list = document.getElementById('sub_xml_list')
    const all = document.getElementById('sub_select_all')
    const keys = isSub ? Object.keys(subMaps) : isolatedRoads.roads
    let select = isSub ? selectSubMaps : selectIsolatedRoads
    let selectAll = '<input type="checkbox" id="select_all" /><label>全选</label>'
    all.innerHTML = selectAll
    let a = ''
    for(let i = 0; i < keys.length; i++) {
        const color = isSub ? subMaps[keys[i]].color : isolatedRoads.color 
        a += '<div><input type="checkbox" id=' + keys[i] 
        a += '' + select.indexOf(keys[i]) < 0 ? ' />' : ' checked />'
        a += ' <label style="color:' + color  + '">' + keys[i] + '</label></div>'
    }
    list.innerHTML = a

    $('#select_all').prop('checked', select.length === keys.length && keys.length > 0)

    $('#sub_xml_list input').on('click', function(e) {
        const id = $(this).prop('id')
        const checked = $(this).prop('checked')
        if (checked) {
            select.push(id)
            if (select.length === keys.length) {
                $('#select_all').prop('checked', true)
            }
        } else {
            const index = select.indexOf(id)
            select.splice(index, 1)
            $('#select_all').prop('checked', false)
        }
        const idList = isSub ? subMaps[id].roads : [id]
        const color = isSub ? subMaps[id].color: isolatedRoads.color
        changeSubMapsColor(checked, idList, color)
    }) 

    $('#select_all').on('click', function() {
        const checked = $(this).prop('checked')
        changeAllCheckbox(checked)
        let idList = []
        select = checked ? keys : []
        if (isSub) {
            for(let i = 0; i < keys.length; i++) {
                changeSubMapsColor(checked, subMaps[keys[i]].roads, subMaps[keys[i]].color)
            }
            selectSubMaps = select
        } else {
            idList = JSON.parse(JSON.stringify(keys))
            changeSubMapsColor(checked, idList, isolatedRoads.color)
            selectIsolatedRoads = select
        }
        map.setFitView()
    })
}

function changeSubMapsColor(flag, idList, color) {
    const obj = Object.assign(signalDic, objectDic, centerRefLaneDic, lineDic)
    const lines = []
    for(let i = 0; i < idList.length; i++) {
        const line = obj[idList[i]]
        lines.push(line)
        const childColor = flag ? color : null
        const extData = line.getExtData()
        extData['childColor'] = childColor
        line.setExtData(extData)
        line.setOptions({
            "strokeColor": childColor || extData['oldColor']
        })
    }
   if(flag) map.setFitView(lines)
}

function changeAllCheckbox(flag) {
   const list = $('#sub_xml_list input')
   for(let i = 0; i < list.length; i++) {
    $(list[i]).prop('checked', flag)
   }
}

async function showTest() {
    if(!globalRemark) {
        promptBox("请先选择一个xml", "warning", 1000)
        $('#test_button').prop("hide", true)
        return
    }
    $('#test_button').prop("hide", false)
    if(testResults) createShowTest()
}
function clearChooseTest() {
    for(let i = 0; i < selectTestItem.length; i++) {
        map.remove(testMarkerObjs[selectTestItem[i]])
    }
    selectTestItem = []
    const list = $('#test_list input')
    for(let i = 0; i < list.length; i++) {
        $(list[i]).prop('checked', false)
    }
}
async function getTestParse() {
    const path = $('#show_test_path').val()
    if (!path) {
        promptBox("请先输入s3路径", "warning", 1000)
        selectTestItem = []
        testMarkerObjs = {}
        testResults = null
        return
    }
    // globalPoints = '116.28342692091731,39.85008608516268'
    const res = await request(`/python/log-extracter?log_s3_path=${path}&auxilary_point=${globalPoints}`)
    selectTestItem = []
    testMarkerObjs = {}
    if(res.code !== 200) {
        testResults = null
        document.getElementById('test_list').innerHTML = ''
        promptBox(res.message, "warning", 1000)
        return
    }
    testResults = res.data
    createShowTest()
}

function createShowTest() {
    const keys = Object.keys(testResults)
    const list = document.getElementById('test_list')
    let a = ''
    for(let i = 0; i < keys.length; i++) {
        a += '<div><input type="checkbox" id=' + keys[i]
        a += '' + selectTestItem.indexOf(keys[i]) < 0 ? ' />' : ' checked />'
        a += ' <label style="color:' + testResults[keys[i]].color + '">' + testResults[keys[i]].name + '</label></div>'
    }
    list.innerHTML = a

    $('#test_list input').on('click', function(e) {
        const id = $(this).prop('id')
        const checked = $(this).prop('checked')
        if (checked) {
            selectTestItem.push(id)
            drawTest(testResults[id], id)
        } else {
            const index = selectTestItem.indexOf(id)
            selectTestItem.splice(index, 1)
            clearTest(id)
        }
    }) 
}

function markerContent(color) {
    const str = '<div class="circle_marker" style="background:' + color + '"></div>'
    return str
}

function drawTest(data, key) {
    const { color, name, points } = data
    let markers =  testMarkerObjs[name]
    if (markers) {
       for(let i = 0; i < markers[i].length; i++) {
            map.add(markers[i])
       }
       map.setFitView(markers)
       return
    }
    
    for (let i = 0; i < points.length; i++) {
        const extData = {
            key: key,
            point: points[i]
        }
        extData.show = "<div class='test_info_container'><div>UTM: " + points[i].utm_x + ',' + points[i].utm_y +"</div><div>WGS84: " + points[i].wgs84_lon + "," + points[i].wgs84_lat  + "</div><div>Error Message: " + name + "</div><div>"
        // console.log(points[i], extData.show)
        const marker = new AMap.Marker({
            map: map,
            position: [points[i].gcj_lon, points[i].gcj_lat],
            content: markerContent(color),
            extData
        })
        if(!testMarkerObjs[key]) testMarkerObjs[key] = []
        testMarkerObjs[key].push(marker)
        marker.on('click', function() {
            // console.log(this.getExtData())
            const label = this.getLabel()
            if(!!label.content) {
                this.setLabel({
                    offset: new AMap.Pixel(10, 35),
                    content: '',
                    direction: 'right'
                })
                return
            }
            const extData = this.getExtData()
            this.setLabel({
                offset: new AMap.Pixel(10, 35),
                content: extData.show,
                direction: 'right'
            })
        })
        marker.on('mouseover', function() {
            // console.log(this.getExtData())
            const extData = this.getExtData()
            this.setLabel({
                offset: new AMap.Pixel(10, 35),
                content: extData.show,
                direction: 'right'
            })
        })
    }
    map.setFitView(testMarkerObjs[key])
}

function clearTest(id) {
    const markers = testMarkerObjs[id]
    map.remove(markers)
}

// 检查routing

let count = 1
let lines = []
let markers = []
let noSuccLansObj = {}
let noSuccLines = []
function checkRouting() {
    if(!globalRemark) {
        promptBox("请先选择一个xml", "warning", 1000)
        $('#check_button').prop("hide", true)
        return
    }
}

function addPosition() {
    const values = []
    $('#routing_position_list input').map(function() {
        values.push(this.value)
    })
      
    const list = document.getElementById('routing_position_list')
    let a = '<div class="position_item"><span>position' + count + '：' + '</span>'
    a += '<input class="check_path_position" value=""/>'
    a += '<button name="' + count + '" onclick="deletePostion(this.name)">删除</button></div>'
    list.innerHTML += a
    count++ 


    inputs = $('#routing_position_list input')
    for(let i = 0; i < inputs.length-1; i++) {
        inputs[i].value = values[i]
    }
}

function deletePostion(data) {
    const positions = $('#routing_position_list .position_item')
    positions[data-1].remove()
    for(let i = data; i < positions.length; i++) {
        positions[i].firstChild.innerHTML = 'position' + i + '：'
        positions[i].lastChild.name = i.toString()
    }
    count--
}

async function confirmCheck() {
    const from = $('#check_path_from').val()
    const to = $('#check_path_to').val()
    if (!from) {
        promptBox("请输入From", "warning", 1000)
        return
    }
    if (!to) {
        promptBox("请输入To", "warning", 1000)
        return
    }
    clearCheck()
    lines = []
    markers = []
    noSuccLansObj = {}
    noSuccLines = []
    const inputs = $('#routing_position_list input')
    const rsid_list = []
    rsid_list.push(from)
    for(let i = 0; i < inputs.length; i++) {
        if(inputs[i].value) rsid_list.push(inputs[i].value)
    }
    rsid_list.push(to)
    // const temp = '13437597A6697b_1_0,13437601A9f304_0_0,e014e_1_0,cc9dd_1_0 '
    // const temp = '13437597Ac3e17_0_0,2df20_5_0,13639556A3678f_0_0'
    addTitleCircle()
    const res = await request(`/python/map-routing-inspector/routing?map_path=${xmlDic[0].ossPath}&map_md5=${xmlDic[0].fileMd5}&rsid_list=${rsid_list.join(',')}`)
    removeTitleCircle()
    if (res.code !== 200) {
        promptBox(res.message, "warning", 1000)
        return
    }
    const has_routing = res.data.summary.has_routing
    
    if (has_routing) {
        $('#check_show').hide()
    } else {
        $('#check_show').show()
        $('#check_show input').prop('checked', false)
        $('#check_show input').on('click', function() {
            const checked = $(this).prop('checked')
            if (checked) {
                changeCheckLineColor(noSuccLansObj.lanes, noSuccLansObj.color, true)
            } else {
                clearCheck(noSuccLines)
            }
        })
    }

    const start_id = res.data.summary.ref_lane_ids[0]
    const end_id = res.data.summary.ref_lane_ids[res.data.summary.ref_lane_ids.length-1]
    const details = res.data.details
    const first_failure = res.data.summary.has_routing ? null : res.data.summary.first_failure_road_segment
    for (let key of Object.keys(details)) {
        drawRoadSeg(details[key])
        hanldeMarker(details[key], start_id, end_id, first_failure)
    }
    map.add(markers)
    map.setFitView([...markers, ...lines])
}

function drawRoadSeg(seg) {
   if (!seg.has_routing) {
    noSuccLansObj =  {
        lanes: seg.no_succ_lanes,
        color: seg.color
    }
   } else if(!seg.has_multi_routing) {
     changeCheckLineColor(seg.path, seg.color)
   } else {
     for(let key of Object.keys(seg.path)) {
        changeCheckLineColor(seg.path[key], seg.color)
     }
   }
}

const markerIcon = {
    start: 'https://webapi.amap.com/theme/v1.3/markers/n/start.png',
    end: 'https://webapi.amap.com/theme/v1.3/markers/n/end.png',
    mid: 'https://webapi.amap.com/theme/v1.3/markers/n/mid.png',
    fail: 'https://a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-red.png'
}

function hanldeMarker(seg, start_id, end_id, first_failure) {
    const obj = Object.assign(signalDic, objectDic, centerRefLaneDic, lineDic)
    let markerPoints = []

    if (first_failure && first_failure.indexOf(seg.start_ref_lane_id) >= 0) { // 断路起点
        const points = obj[seg.start_ref_lane_id].getExtData().points
        const markerObj = {
            point: points[0][Math.floor(points[0].length / 2)],
            type: 'fail'
        }
        markerPoints.push(markerObj)
    } else if(seg.start_ref_lane_id === start_id) { // 起点
        const points = obj[start_id].getExtData().points
        const markerObj = {
            point: points[0][Math.floor(points[0].length / 2)],
            type: 'start'
        }
        markerPoints.push(markerObj)
        changeCheckLineColor([start_id] ,seg.color)
    }
    
    if (first_failure && first_failure.indexOf(seg.end_ref_lane_id) >= 0) { // 断路终点
        const points = obj[seg.end_ref_lane_id].getExtData().points
        const markerObj = {
            point: points[0][Math.floor(points[0].length / 2)],
            type: 'fail'
        }
        markerPoints.push(markerObj)
    } else if(seg.end_ref_lane_id === end_id) { // 终点
        const points = obj[end_id].getExtData().points
        const markerObj = {
            point: points[0][Math.floor(points[0].length / 2)],
            type: 'end'
        }
        markerPoints.push(markerObj)
    } else { // 途经点
        const points = obj[seg.end_ref_lane_id].getExtData().points
        const markerObj = {
            point: points[0][Math.floor(points[0].length / 2)],
            type: 'mid'
        }
        markerPoints.push(markerObj)
    }

//    console.log(markerPoints)
   for(let markerObj of markerPoints) {
    const Icon =  new AMap.Icon({
        size: new AMap.Size(19, 31), //图标大小
        imageSize: new AMap.Size(19, 31),
        image: markerIcon[markerObj.type],
        imageOffset: new AMap.Pixel(0, 0)
    })
    const marker = new AMap.Marker({
        position: new AMap.LngLat(markerObj.point[0], markerObj.point[1]),
        icon: Icon,
        offset: new AMap.Pixel(0, 0),
        anchor: 'bottom-center'
    })
    markers.push(marker)
   }
}

function changeCheckLineColor(lanes, color, flag) { // flag为true，渲染的是no_succ_lanes
   const obj = Object.assign(signalDic, objectDic, centerRefLaneDic, lineDic)
    for(let lane of lanes) {
        const line = obj[lane]
        lines.push(line)
        if(flag) noSuccLines.push(line)
        const extData = line.getExtData()
        if(extData['childColor']) extData['oldChildColor'] = extData['childColor']
        extData['childColor'] = color
        line.setExtData(extData)
        line.setOptions({
            "strokeColor": color,
            "strokeWeight": 5
        })
    }
    if(flag) map.setFitView(noSuccLines)
}

function clearCheck(lanes) {
    const changeLanes = lanes ? lanes : lines
    for(let line of changeLanes) {
        const extData = line.getExtData()
        if (extData['oldChildColor']) {
            extData['childColor'] = extData['oldChildColor']
            extData['oldChildColor'] = null
            line.setExtData(extData)
            line.setOptions({
                "strokeColor": extData['childColor'],
                "strokeWeight": extData['oldWeight']
            })
        } else {
            extData['childColor'] = null
            line.setExtData(extData)
            line.setOptions({
                "strokeColor": extData['oldColor'],
                "strokeWeight": extData['oldWeight']
            })
        }
    }
    if(!lanes) {
        lines = []
        map.remove(markers)
        markers = []
    } 
}

// 关键点查询
let uidCount = 1
let pointsObj = {}
let selectPoints = []
let pointMarkers = {}
let validUids = []
function findImportPoint() {
    if(!globalRemark) {
        promptBox("请先选择一个xml", "warning", 1000)
        $('#find_point_button').prop("hide", true)
        return
    }
    $('#find_point_button').prop("hide", false)
    $('#find_index').hide()
    $('#designate_index').on('click', function() {
        const checked = $(this).prop('checked')
        if (checked) {
            $('#find_index').show()
        } else {
            $('#find_index').hide()
            $('#find_index input').prop('value', '3')
        }
    })
}

function addFindUid() {
    const values = []
    $('#find_uid input').map(function() {
        values.push(this.value)
    })

    const list = document.getElementById('find_uid')
    let a = '<div class="uid_item"><span>lane uid ' + uidCount + '：' + '</span> '
    a += '<input /> '
    a += '<button name="' + uidCount + '" onclick="deleteUid(this.name)">-</button></div>'
    list.innerHTML += a
    uidCount++ 

    inputs = $('#find_uid input')
    for(let i = 0; i < inputs.length-1; i++) {
        inputs[i].value = values[i]
    }
}

function deleteUid(name) {
    const uids = $('#find_uid .uid_item')
    uids[name-1].remove()
    for(let i = name; i < uids.length; i++) {
        uids[i].firstChild.innerHTML = 'lane uid ' + i + '：'
        uids[i].lastChild.name = i.toString()
    }
    uidCount--
}

async function searchImportPoint() {
    const inputs = $('#find_uid input')
    const idx = $('#find_index input').val()
    const lane_uid_list = []
    for(let i = 0; i < inputs.length; i++) {
        if(inputs[i].value) lane_uid_list.push(inputs[i].value)
    }
    if(lane_uid_list.length === 0) {
        promptBox("请输入至少一个lane uid", "warning", 1000)
        return
    }
    const reg = /^[1-5]{1}$/
    if(!reg.test(idx)) {
        promptBox("请输入1~5的整数的指定位置", "warning", 1000)
        return
    } 
    addTitleCircle()
    initUidSearch()
    const res = await request(`/python/lane-key-point?map_path=${xmlDic[0].ossPath}&map_md5=${xmlDic[0].fileMd5}&lane_uid_list=${lane_uid_list.join(',')}&idx=${idx}`)
    removeTitleCircle()
    if (res.code !== 200) {
        promptBox(res.message, "warning", 2000)
        return
    }
    pointsObj = res.data
    validUids = Object.keys(pointsObj).filter(key => pointsObj[key])
    createPointsList()
}

function initUidSearch() {
    hideImportPoint(validUids)
    selectPoints = []
    pointMarkers = {}
    validUids = []
    pointsObj = {}
    document.getElementById('find_result').innerHTML = ''
}

function createPointsList() {
    const list = document.getElementById('find_result')
    let a = '<div><input type="checkbox" id="select_all_points" /><label>全选</label></div>'
    for(let key of Object.keys(pointsObj)) {
        a += '<div><input type="checkbox" id="' + key + '"' 
        a += !pointsObj[key] ? ' disabled ' : ''
        a += ' ' + (selectPoints.indexOf(key) < 0 ? '/>' : 'checked />') + '<label'
        a += !pointsObj[key] ? ' style="color: red"' : ''
        a += '>' + key + '</label></div>'
    }
    list.innerHTML = a

    $('#select_all_points').on('click', function() {
        const checked = $(this).prop('checked')
        if (checked) {
            drawImportPoint(validUids)
            selectPoints = validUids
        } else {
            hideImportPoint(validUids)
            selectPoints = []
        }
        changeAllPoint(checked)
    })

    $('#find_result input').on('click', function() {
        const id = $(this).prop('id')
        if(id === 'select_all_points') return
        const checked = $(this).prop('checked')
        if(checked) {
            selectPoints.push(id)
            if (selectPoints.length === validUids.length) {
                $('#select_all_points').prop('checked', true)
            }
            drawImportPoint([id])
        } else {
            selectPoints.splice(selectPoints.indexOf(id), 1)
            $('#select_all_points').prop('checked', false)
            hideImportPoint([id])
        }
    })
}

function changeAllPoint(type) {
    const list = $('#find_result input')
    for(let i = 0; i < list.length; i++) {
       if(!$(list[i]).prop('disabled')) $(list[i]).prop('checked', type)
    }
}

function drawImportPoint(list) {
    const curMarkers = []
    for(let i = 0; i < list.length; i++) {
        if (pointMarkers[list[i]]) {
            map.add(pointMarkers[list[i]])
            curMarkers.push(...pointMarkers[list[i]])
        } else if(pointsObj[list[i]]){
            const { head, tail } = pointsObj[list[i]] 
            curMarkers.push(createPointMarker(0, head, list[i]))
            curMarkers.push(createPointMarker(1, tail, list[i]))
        }
    }
    map.setFitView(curMarkers)
}

function createPointMarker(type, data, key) { // type 0 head 1 tail
    data.show = "<div class='test_info_container'><div id='" + key + "$" + type + "' class='label_delete' onclick='labelDelete(this)'>x</div><div style='margin-top: 10px;' onclick='copyInfo(this)'>UTM: " + data.utm_x + ',' + data.utm_y +"<span class='iconfont icon_color icon-fuzhi'></span></div><div onclick='copyInfo(this)'>WGS84: " + data.wgs84_lon + "," + data.wgs84_lat  + "<span class='iconfont icon_color icon-fuzhi'></span></div><div>"
    const marker = new AMap.Marker({
        map: map,
        position: [data.gcj_lon, data.gcj_lat],
        content: '<div class="circle_marker" style="background:' + (type ? '#0000ff' : '#ff0000') + '"></div>' ,
        extData: data
    })
    marker.on('mouseover', function(e) {
        const label = this.getLabel()
        const extData = this.getExtData()
        this.setLabel({
            offset: new AMap.Pixel(10, 35),
            content: extData.show,
            direction: 'right'
        })
    })
    if(!pointMarkers[key]) pointMarkers[key] = []
    pointMarkers[key].push(marker)
    return marker
}

function labelDelete(data) {
    const ids = $(data).prop('id').split('$')
    const marker = pointMarkers[ids[0]][ids[1]]
    marker.setLabel({
        offset: new AMap.Pixel(10, 35),
        content: '',
        direction: 'right'
    })
}

function copyInfo(data) {
    navigator.clipboard.writeText(data.innerHTML.split('<')[0])
    promptBox("复制成功", "success", 1000)
}

function hideImportPoint(list) {
    for(let i = 0; i < list.length; i++) {
        map.remove(pointMarkers[list[i]])
    }
}

function download(downfile) {
    const tmpLink = document.createElement("a");
    const objectUrl = URL.createObjectURL(downfile);
    // window.open(objectUrl)

    tmpLink.href = objectUrl;
    tmpLink.download = downfile.name;
    document.body.appendChild(tmpLink);
    tmpLink.click();

    document.body.removeChild(tmpLink);
    URL.revokeObjectURL(objectUrl);
}


function draw(type) {
    switch (type) {
        case 'rule': {
            mouseTool.rule({
                startMarkerOptions: { //可缺省
                    icon: new AMap.Icon({
                        size: new AMap.Size(19, 31), //图标大小
                        imageSize: new AMap.Size(19, 31),
                        image: "//webapi.amap.com/theme/v1.3/markers/b/start.png"
                    }),
                    offset: new AMap.Pixel(-9, -31)
                },
                endMarkerOptions: { //可缺省
                    icon: new AMap.Icon({
                        size: new AMap.Size(19, 31), //图标大小
                        imageSize: new AMap.Size(19, 31),
                        image: "//webapi.amap.com/theme/v1.3/markers/b/end.png"
                    }),
                    offset: new AMap.Pixel(-9, -31)
                },
                midMarkerOptions: { //可缺省
                    icon: new AMap.Icon({
                        size: new AMap.Size(19, 31), //图标大小
                        imageSize: new AMap.Size(19, 31),
                        image: "//webapi.amap.com/theme/v1.3/markers/b/mid.png"
                    }),
                    offset: new AMap.Pixel(-9, -31)
                },
                // lineOptions: { //可缺省
                // 	strokeStyle: "solid",
                // 	strokeOpacity: 1,
                // 	strokeWeight: 4
                // }
                //同 RangingTool 的 自定义 设置，缺省为默认样式
            });
            break;
        }
        case 'measureArea': {
            mouseTool.measureArea({
                strokeColor: '#25A5F7',
                fillColor: '#25A5F7',
                fillOpacity: 0.3
                //同 Polygon 的 Option 设置
            });
            break;
        }
    }
}

var setLine = true

function measureLine() {
    if (setLine) {
        $("#con_right_bottom_line").css("color", "#25A5F7").css("border-color", "#25A5F7")
        $("#con_right_bottom_area").css("color", "white")
        setArea = true
        //禁用右键拾取坐标
        allowRightclick = false
        //禁止局部刷新，并更新按钮颜色
        $("#forbidRefresh").css("color", "#25A5F7")
        forbidRefreshPartXml = true
        mouseToolClear()
        draw("rule")
        // mouseTool.rule();
        setLine = false
    } else {
        $("#con_right_bottom_line").css("color", "white")
        mouseToolClear()
        setLine = true
        allowRightclick = true
        //关闭禁止刷新需要重置颜色
        $("#forbidRefresh").css("color", "white")
        forbidRefreshPartXml = false
    }
}

var setArea = true

function measureArea() {
    if (setArea) {
        $("#con_right_bottom_area").css("color", "#25A5F7").css("border-color", "#25A5F7")
        $("#con_right_bottom_line").css("color", "white")
        setLine = true
        allowRightclick = false
        mouseToolClear
        // draw("measureArea")
        mouseTool.measureArea();
        setArea = false
        //禁止局部刷新，并更新按钮颜色
        $("#forbidRefresh").css("color", "#25A5F7")
        forbidRefreshPartXml = true
    } else {
        $("#con_right_bottom_area").css("color", "white")
        mouseToolClear()
        setArea = true
        allowRightclick = true
        //关闭禁止刷新需要重置颜色
        $("#forbidRefresh").css("color", "white")
        forbidRefreshPartXml = false
    }
}

var radios = new Array();
radios.push(document.getElementById('con_right_bottom_line'))
radios.push(document.getElementById('con_right_bottom_area'))

function mouseToolClear() {
    mouseTool.close(true) //关闭，并清除覆盖物
    for (var i = 0; i < radios.length; i += 1) {
        radios[i].checked = false;
    }
}

var useTool = true

function showTools() {
    if (useTool) {
        $("#con_right_bottom_line").slideDown()
        $("#con_right_bottom_area").slideDown()
        $("#con_right_bottom_ruler").css("color", "#25A5F7").css("border-color", "#25A5F7")
        useTool = false
    } else {
        mouseToolClear()
        $("#con_right_bottom_line").css("color", "white")
        setLine = true
        $("#con_right_bottom_area").css("color", "white")
        setArea = true
        $("#con_right_bottom_line").slideUp()
        $("#con_right_bottom_area").slideUp()
        $("#con_right_bottom_ruler").css("color", "white")
        useTool = true
        allowRightclick = true
        //关闭禁止刷新需要重置颜色
        $("#forbidRefresh").css("color", "white")
        forbidRefreshPartXml = false
    }
}
