"use strict";

var satellite = new AMap.TileLayer.Satellite({
  visible: true
});
var roadNet = new AMap.TileLayer.RoadNet({
  visible: false
});
var traffic = new AMap.TileLayer.Traffic({
  visible: false
});
var map = new AMap.Map("container", {
  resizeEnable: true,
  zoom: 17,
  expandZoomRange: true,
  center: ["116.326468", "39.984477"],
  zooms: [3, 20],
  defaultCursor: "pointer",
  viewMode: "2D",
  pitch: 30,
  layers: [satellite, traffic, roadNet]
});
var autoOptions = {
  input: "tipinput"
};
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

function getInfo(zoom, center) {
  // console.log(center)
  addTitleCircle();

  try {
    map.setCenter(center.toString().split(","));
  } catch (err) {
    removeTitleCircle();
    window.alert("输入值不合法！");
    return;
  }

  $.ajax({
    type: "get",
    url: baseUrl + "/getInfoByCoordinate?zoom=" + zoom + "&center=" + center,
    async: false,
    success: function success(data) {
      map.setZoom(zoom);
      var lanes = data.data;
      clearMapAndCache();
      drawLane(lanes);
      removeTitleCircle();
    }
  });
}

var laness;

function getInfoByXml(xml) {
  // var circle = $("#" + xml).children(".circle")
  addTitleCircle(); // console.log($("#" + xml).children("div"))
  // $(circle).css("display", "inline-block")

  $.get(baseUrl + "/getInfoByXml?xml=" + xml, function (data) {
    if (data.code == 403) {
      window.alert("操作频繁!!!");
      return;
    }

    var lanes = data.data.lanes;
    laness = lanes;
    clearMapAndCache(); // var worker= new Worker("./line.js")
    // worker.postMessage(data);

    drawObject("objects", data.data.objects);
    drawObject("signals", data.data.signals);
    drawObject("junctions", data.data.junctions);
    drawCenterLane(data.data.centerLanes);
    drawLane(lanes);
    removeTitleCircle(); // $(circle).css("display", "none")

    map.setFitView();
  });
  showCenter = false;
  hidecenter();
  showGuides = false;
  hideGuides();
}

var lineDic = {};
var roadDic = {};
var roadSectionDic = {};
var sectionDic = {};
var objectDic = {};
var signalDic = {};
var centerDic = {};
var objectsDic = {};

function clearMapAndCache() {
  map.clearMap();
  lineDic = {};
  roadDic = {};
  roadSectionDic = {};
  objectDic = {};
  signalDic = {};
}

function drawObject(type, objects) {
  var weight = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : 3;

  for (var object in objects) {
    points = objects[object]["points"];
    obj = objects[object];
    var color = "#FFF";

    switch (type) {
      case "signals":
        color = "#3F9";
        weight = 3;
        break;
    }

    var myExtData = objects[object];
    myExtData["oldColor"] = color;
    myExtData["type"] = type;
    var myObj = new AMap.Polyline({
      cursor: "ani",
      map: map,
      path: points,
      showDir: false,
      lineJoin: "round",
      //折线拐点样式
      // strokeColor: lanes[lane]["color"], //线颜色
      strokeColor: color,
      //线颜色
      strokeOpacity: 1,
      //线透明度
      strokeWeight: weight,
      //线宽
      strokeStyle: "solid",
      //线样式
      extData: myExtData,
      zIndex: 9999
    });

    switch (type) {
      case "signals":
        signalDic[obj["signalId"]] = myObj;
        break;

      case "junctions":
        objectDic[obj["junctionId"]] = myObj;
        break;

      default:
        objectDic[obj["objectId"]] = myObj;
        break;
    }

    myObj.on("click", function (e) {
      point = new Array(e.lnglat.getLng(), e.lnglat.getLat()); // this.setOptions({"strokeColor": "red"})

      setWinInfo(this.getExtData()).open(map, point);
    });
    myObj.on("mouseover", function () {
      var extData = this.getExtData();
      this.setOptions({
        "strokeColor": "#00BFFF"
      });

      switch (extData["type"]) {
        case "signals":
          stoplineList = JSON.parse(objects[object]["stoplineIds"]);

          for (var stoplineId in stoplineList) {
            stoplineObj = objectDic[stoplineList[stoplineId]];
            stoplineObj.setOptions({
              "strokeColor": "red"
            });
          }

          break;
      }
    });
    myObj.on("mouseout", function () {
      var extData = this.getExtData();
      this.setOptions({
        "strokeColor": extData["oldColor"]
      });

      switch (extData["type"]) {
        case "signals":
          stoplineList = JSON.parse(objects[object]["stoplineIds"]);

          for (var stoplineId in stoplineList) {
            stoplineObj = objectDic[stoplineList[stoplineId]];
            stoplineObj.setOptions({
              "strokeColor": stoplineObj.getExtData()["oldColor"]
            });
          }

          break;
      }
    });
  }
}

function drawCenterLane(lanes) {
  var weight = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 1;

  for (var lane in lanes) {
    color = "#0eea08";
    points = lanes[lane]["points"];
    var myExeData = lanes[lane];
    myExeData["oldColor"] = color;
    var line = new AMap.Polyline({
      title: lanes[lane]["uid"],
      cursor: "ani",
      map: map,
      path: points,
      // path: points,
      showDir: false,
      lineJoin: "round",
      //折线拐点样式
      // strokeColor: lanes[lane]["color"], //线颜色
      strokeColor: color,
      //线颜色
      strokeOpacity: 1,
      //线透明度
      strokeWeight: 0,
      //线宽
      strokeStyle: "solid",
      //线样式
      extData: myExeData
    });
    centerDic[myExeData["laneUid"]] = line;
  }
}

function drawLane(lanes) {
  var weight = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 2;

  for (var lane in lanes) {
    // console.log(lanes[lane].points)
    var color = lanes[lane]["color"];
    var lines = lanes[lane].laneBorderType;

    if (lanes[lane]["isVirtual"] === "TRUE") {
      color = "#B0C4DE";
    }

    if (lines === 'solid' || lines === 'curb') {
      lines = 'solid';
    } else {
      lines = 'dashed';
    } // console.log(lanes[lane]["points"])


    points = lanes[lane]["points"];

    if (points == null) {
      continue;
    }

    var myExeData = lanes[lane];
    myExeData["oldColor"] = color;
    var line = new AMap.Polyline({
      title: lanes[lane]["uid"],
      cursor: "ani",
      map: map,
      path: points,
      // path: points,
      showDir: false,
      lineJoin: "round",
      //折线拐点样式
      // strokeColor: lanes[lane]["color"], //线颜色
      strokeColor: color,
      //线颜色
      strokeOpacity: 1,
      //线透明度
      strokeWeight: weight,
      //线宽
      strokeStyle: lines,
      //线样式
      extData: myExeData
    });
    line.on("click", function (e) {
      point = new Array(e.lnglat.getLng(), e.lnglat.getLat()); // this.setOptions({"strokeColor": "red"})

      setWinInfo(this.getExtData()).open(map, point);
    });
    lane_uid = lanes[lane]["uid"];
    lineDic[lane_uid] = line;
    id_list = lane_uid.split("_");
    road_section_id = id_list[0] + "_" + id_list[1];
    road_id = id_list[0];

    if (!roadDic.hasOwnProperty(road_id)) {
      roadDic[road_id] = new Array();
    }

    if (!roadSectionDic.hasOwnProperty(road_section_id)) {
      roadSectionDic[road_section_id] = new Array();
    }

    roadDic[road_id].push(lane_uid);
    roadSectionDic[road_section_id].push(lane_uid);
    line.on("mouseover", function (e) {
      extData = this.getExtData();
      pres = extData["pre"].split(";");
      sucs = extData["suc"].split(";");
      leftUid = extData["leftUid"];
      rightUid = extData["rightUid"];
      objectOverlaps = JSON.parse(extData["objectOverlapIds"]);
      signalOverlaps = JSON.parse(extData["signalOverlapIds"]);
      this.setOptions({
        "strokeColor": "#00BFFF"
      });

      for (var i in objectOverlaps) {
        objectOverLap = objectOverlaps[i]; // console.log(objectOverLap)
        // console.log(objectDic)

        if (objectDic[objectOverLap] != undefined) {
          // console.log(objectDic[objectOverLap])
          objectDic[objectOverLap].setOptions({
            "strokeColor": "red"
          });
        }
      }

      for (var i in signalOverlaps) {
        signalOverlap = signalOverlaps[i];

        if (signalDic[signalOverlap] != undefined) {
          signalDic[signalOverlap].setOptions({
            "strokeColor": "red"
          });
        }
      }

      for (var pre in pres) {
        try {
          thisLine = lineDic[pres[pre]];
          thisLine.setOptions({
            "strokeColor": "#FFA500"
          });
        } catch (TypeError) {
          continue;
        }
      }

      for (var suc in sucs) {
        try {
          thisLine = lineDic[sucs[suc]];
          thisLine.setOptions({
            "strokeColor": "#00FF00"
          });
        } catch (TypeError) {
          continue;
        }
      }

      try {
        thisLine = lineDic[leftUid];
        thisLine.setOptions({
          "strokeColor": "#8A2BE2"
        });
      } catch (TypeError) {}

      try {
        thisLine = lineDic[rightUid];
        thisLine.setOptions({
          "strokeColor": "#FFC0CB"
        });
      } catch (TypeError) {}
    });
    line.on("mouseout", function (e) {
      var extData = this.getExtData();
      this.setOptions({
        "strokeColor": extData["oldColor"]
      });
      pres = extData["pre"].split(";");
      sucs = extData["suc"].split(";");
      leftUid = extData["leftUid"];
      rightUid = extData["rightUid"];
      objectOverlaps = JSON.parse(extData["objectOverlapIds"]);
      signalOverlaps = JSON.parse(extData["signalOverlapIds"]);

      for (var i in objectOverlaps) {
        objectOverLap = objectOverlaps[i];

        if (objectDic[objectOverLap] != undefined) {
          objObj = objectDic[objectOverLap];
          objObj.setOptions({
            "strokeColor": objObj.getExtData()["oldColor"]
          });
        }
      }

      for (var i in signalOverlaps) {
        signalOverlap = signalOverlaps[i];

        if (signalDic[signalOverlap] != undefined) {
          signalObj = signalDic[signalOverlap];
          signalObj.setOptions({
            "strokeColor": signalObj.getExtData()["oldColor"]
          });
        }
      }

      for (var pre in pres) {
        try {
          var extData = lineDic[pres[pre]].getExtData();
          lineDic[pres[pre]].setOptions({
            "strokeColor": extData["oldColor"]
          });
        } catch (TypeError) {
          continue;
        }
      }

      for (var suc in sucs) {
        try {
          var extData = lineDic[sucs[suc]].getExtData();
          lineDic[sucs[suc]].setOptions({
            "strokeColor": extData["oldColor"]
          });
        } catch (TypeError) {
          continue;
        }
      }

      try {
        var extData = lineDic[leftUid].getExtData();
        lineDic[leftUid].setOptions({
          "strokeColor": extData["oldColor"]
        });
      } catch (TypeError) {}

      try {
        var extData = lineDic[rightUid].getExtData();
        lineDic[rightUid].setOptions({
          "strokeColor": extData["oldColor"]
        });
      } catch (TypeError) {}
    });
  } // map.setFitView();

}

function setWinInfo(extdata) {
  var filterKeys = ["gmtCreate", "gmtModified", "startLon", "startLat", "endLon", "endLat", "id", "roadSection", "laneId", "oldColor", "connection", "type", "pointsUid", "xmlUid"]; // console.log(extdata) 

  $('#con_top_bottom').text('隐藏车道信息');
  $('#con_top ul').slideDown();
  $('#con_top ul').css("padding", "8px");
  $("#con_top ul li").remove();
  var content = " ";

  for (var key in extdata) {
    if (filterKeys.includes(key)) {
      continue;
    }

    val = extdata[key];
    val = extdata[key] == null ? '' : extdata[key];
    content += "<li>" + key + " : " + val + "</li>";
  }

  $("#con_top ul").append(content);
  copy();
}

function copy() {
  $("#con_top ul li").click(function () {
    var text = $(this).html().match(/:(.*)/)[1];
    var input = document.getElementById("input");
    input.value = text; // 修改文本框的内容

    input.select(); // 选中文本

    document.execCommand("copy"); // 执行浏览器复制命令

    alert("复制成功");
  });
} // 点坐标搜索


function searchLatLon() {
  if ($("#lat").val() == "Lat" || $("#lon").val() == "Lon") {
    window.alert("请输入点坐标");
    return;
  }

  lat = $("#lat").val();
  lon = $("#lon").val(); // gcjArr = wgs2gcj(lon, lat)
  // point = gcjArr[0] + "," + gcjArr[1]
  // getInfo("16", point)

  setMarker(lon, lat);
}

var globalMarkList = new Array(); // 点坐标搜索

function setMarker(lon, lat) {
  map.remove(globalMarkList);
  wgsStr = "\nwgs: " + lat + "," + lon;
  $.get(baseUrl + "/transfer/wgs2utm?lon=" + lon + "&lat=" + lat, function (data) {
    utm_x_y = data.data;
    utmStr = "utm: " + utm_x_y[0] + "," + utm_x_y[1] + "; utm_id：" + utm_x_y[2];
  }).then(function () {
    var icon = new AMap.Icon({
      // 图标尺寸
      size: new AMap.Size(37, 49),
      // 图标的取图地址
      image: '//a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-default.png',
      // 图标所用图片大小
      imageSize: new AMap.Size(37, 49)
    });
    var content = "<ul style='list-style: none;padding: 3px;margin: 0;border-color: none;'><li style='margin-bottom: 2px'>" + wgsStr + "</li><li>" + utmStr + "</li></ui>";
    var marker = new AMap.Marker({
      position: [parseFloat(lon), parseFloat(lat)],
      // 经纬度对象，也可以是经纬度构成的一维数组[116.39, 39.9]
      icon: icon,
      anchor: 'bottom-center',
      label: {
        content: content,
        direction: "top"
      }
    }); // 将创建的点标记添加到已有的地图实例：

    globalMarkList.push(marker);
    map.add(marker);
    map.setCenter([parseFloat(lon), parseFloat(lat)]);
  });
} // utm_id查找


function searchUtmXy() {
  if ($("#lat").val() == "Lat" || $("#lon").val() == "Lon") {
    window.alert("请输入点坐标");
    return;
  }

  if ($("#utm_id").val() == "utm区") {
    window.alert("请输入utm_id");
    return;
  }

  x = $("#lat").val();
  y = $("#lon").val();
  utm_id = $("#utm_id").val();
  $.get(baseUrl + "/transfer/utm2wgs?x=" + x + "&y=" + y + "&utm_id=" + utm_id, function (data) {
    wgsLonLat = data.data;
  }).then(function () {
    setMarker(wgsLonLat[0], wgsLonLat[1]);
  });
}

var globalSearchLane = new Array();
var globalSearchObject = new Array();

function removeSearchLane() {
  for (lane in globalSearchLane) {
    thisLane = globalSearchLane[lane];
    console.log(thisLane.getExtData()["uid"]);
    console.log(thisLane.getExtData()["oldColor"]);
    console.log(thisLane.getOptions()["strokeColor"]);
    oldColor = thisLane.getExtData()["oldColor"];
    thisLane.setOptions({
      "strokeColor": oldColor
    });
  }

  globalSearchLane.length = 0;
}

function removeSearchObject() {
  for (lane in globalSearchObject) {
    thisObject = globalSearchObject[lane]; // console.log(thisLane.getExtData()["uid"])
    // console.log(thisLane.getExtData()["oldColor"])
    // console.log(thisLane.getOptions()["strokeColor"])

    oldColor = thisObject.getExtData()["oldColor"];
    thisObject.setOptions({
      "strokeColor": oldColor
    });
  }

  globalSearchLane.length = 0;
}

function searchPartLaneUid() {
  laneUids = $("#lane_uid").val().replace(/[\r\n]/g, "").replace(" ", '');

  if ("Lane Uid" == laneUids) {
    window.alert("请输入车道uid，多个使用分号隔开");
  }

  uids = laneUids.split(";");
  addTitleCircle();
  removeSearchLane();
  noSearchLane = new Array();

  for (var uid in uids) {
    laneUid = uids[uid];
    id_list = laneUid.split("_");

    if (id_list.length >= 3) {
      if (!lineDic.hasOwnProperty(laneUid)) {
        noSearchLane.push(laneUid);
        continue;
      } // 获取自身颜色保存，后续恢复使用


      thisLine = lineDic[laneUid];
      extData = thisLine.getExtData();
      extData["oldColor"] = thisLine.getOptions()["strokeColor"];
      thisLine.setExtData(extData); //设置颜色

      thisLine.setOptions({
        "strokeColor": "red"
      });
      globalSearchLane.push(thisLine);
    } else if (id_list.length == 2) {
      lane_road_section_id = id_list[0] + "_" + id_list[1];

      if (!roadSectionDic.hasOwnProperty(lane_road_section_id)) {
        noSearchLane.push(lane_road_section_id);
        continue;
      }

      tempLanes = roadSectionDic[lane_road_section_id];

      for (var lane in tempLanes) {
        // 获取自身颜色保存，后续恢复使用
        thisLine = lineDic[tempLanes[lane]];
        extData = thisLine.getExtData();
        extData["oldColor"] = thisLine.getOptions()["strokeColor"];
        thisLine.setExtData(extData);
        thisLine.setOptions({
          "strokeColor": "red"
        });
        globalSearchLane.push(thisLine);
      }
    } else if (id_list.length == 1) {
      lane_road_id = id_list[0]; // console.log(roadDic.hasOwnProperty(lane_road_id))
      // console.log(roadDic)

      if (!roadDic.hasOwnProperty(lane_road_id)) {
        noSearchLane.push(lane_road_id);
        continue;
      }

      tempLanes = roadDic[lane_road_id];

      for (var lane in tempLanes) {
        // 获取自身颜色保存，后续恢复使用
        thisLine = lineDic[tempLanes[lane]];
        extData = thisLine.getExtData();
        extData["oldColor"] = thisLine.getOptions()["strokeColor"];
        thisLine.setExtData(extData);
        thisLine.setOptions({
          "strokeColor": "red"
        });
        globalSearchLane.push(thisLine);
      }
    }
  }

  map.setFitView(globalSearchLane);
  removeTitleCircle();
  console.log(globalSearchLane);
  if (noSearchLane.length !== 0) window.alert("未搜索到的车道：" + noSearchLane.toString() + "  请尝试全局搜索！");
} // 全局搜索


function searchGlobalLaneUid() {
  laneUids = $("#lane_uid").val().replace(/[\r\n]/g, "").replace(" ", '');
  addTitleCircle();
  $.get(baseUrl + "/getPartLaneByUids?uids=" + laneUids, function (data) {
    clearMapAndCache();
    drawLane(data.data.allLanes);
  }).then(function (data) {
    laneUidList = data.data.selectLanes;
    lines = new Array();
    noSearchLane = new Array();

    for (var lane in laneUidList) {
      try {
        // 获取自身颜色保存，后续恢复使用
        thisLine = lineDic[laneUidList[lane]];
        extData = thisLine.getExtData();
        extData["oldColor"] = thisLine.getOptions()["strokeColor"];
        thisLine.setExtData(extData);
        thisLine.setOptions({
          "strokeColor": "red"
        });
        lines.push(thisLine);
        globalSearchLane.push(thisLane);
      } catch (TypeError) {
        noSearchLane.push(laneUidList[lane]);
      }
    }

    map.setFitView(lines);
    removeTitleCircle();
    if (noSearchLane.length !== 0) window.alert("未搜索到的车道：" + noSearchLane.toString());
  });
}

function searchPartobiectUid() {
  objectUids = $("#object_uid").val().replace(/[\r\n]/g, "").replace(" ", '');

  if ("object Uid" == objectUids) {
    window.alert("请输入车道objectUids，多个使用分号隔开");
  }

  uids = objectUids.split(";");
  addTitleCircle();
  removeSearchObject();
  objectsDic = Object.assign(signalDic, objectDic); // console.log(signalDic,objectDic,objectsDic)

  for (var uid in uids) {
    for (var i in Object.keys(objectsDic)) {
      if (Object.keys(objectsDic)[i] === uids[uid]) {
        thisObject = objectsDic[uids[uid]];
        extData = thisObject.getExtData();
        extData["oldColor"] = thisObject.getOptions()["strokeColor"];
        thisObject.setExtData(extData); //设置颜色

        thisObject.setOptions({
          "strokeColor": "red"
        });
        globalSearchObject.push(thisObject);
      }
    }
  }

  removeTitleCircle();
}

function getAllCenterLane() {
  addTitleCircle();
  $("#title").text("XML");
  $.get(baseUrl + "/getAllCenterLane", function (data, status) {
    clearMapAndCache(); // console.log(data.data)

    drawLane(data.data, 10);
    removeTitleCircle();
    map.setFitView();
  });
}

function drawPoints(lanes) {
  // 临时绘制不清除原有图层数据，便于对比
  // clearMapAndCache()
  // console.log(lanes)
  temp_lanes = new Array();

  for (var lane in lanes) {
    temp_line = new AMap.Polyline({
      map: map,
      path: lanes[lane],
      showDir: false,
      // strokeColor: lanes[lane]["color"], //线颜色
      strokeColor: "red",
      //线颜色
      strokeOpacity: 1,
      //线透明度
      strokeWeight: 2,
      //线宽
      strokeStyle: "solid" //线样式

    });
    temp_lanes.push(temp_line);
  }

  map.setFitView(temp_lanes);
}

function tempDrawingPoints() {
  addTitleCircle();

  try {
    re1 = new RegExp("'", "g");
    re2 = new RegExp('"', "g");
    var points = $("#points").val().replace(re1, "").replace(re2, "");
    console.log(points);
    var lanesPoints = points.split("|");
    var lanesArr = new Array();

    for (var lane in lanesPoints) {
      pointList = lanesPoints[lane].split(";");
      var pointsArr = new Array();

      for (var row in pointList) {
        lonLat = pointList[row].split(",");
        lonLatArr = wgs2gcj(lonLat[0], lonLat[1]);
        pointsArr.push(lonLatArr);
      }

      lanesArr.push(pointsArr);
    }

    drawPoints(lanesArr);
    removeTitleCircle();
  } catch (err) {
    window.alert("请正确输入点序列！");
    removeTitleCircle();
  }
}

var loadSuccess = false;
var lanes;

function drawFromCsv() {
  $("#btn_file").click();
  var file1 = document.getElementById("btn_file");

  try {
    file1.onchange = function () {
      addTitleCircle();
      var file = file1.files[0]; // console.log(file.name)

      if (!file.name.endsWith(".csv")) {
        window.alert("请上传CSV格式文件");
		    removeTitleCircle()
        return;
      } //读取为二进制


      var reader = new FileReader();
      reader.readAsText(file, 'utf-8');

      reader.onload = function () {
        var r = new RegExp("\r", "");
        var str = reader.result;
        lanes = str.split(";");
        determineDraw();
      };
    };
  } catch (err) {
    window.alert("请按照要求设置csv文件");
		removeTitleCircle()
  }
}

function determineDraw() {
  var lanesArr = new Array();

  for (var index in lanes) {
    var lanePoints = lanes[index].split('\n');
    var lanePointArr = new Array();

    for (var i = 0; i < lanePoints.length; i++) {
      var lonLat = lanePoints[i].split(',');
      lon = lonLat[0];
      lat = lonLat[1];
      utm_id = lonLat[2];

      if (lon === "" || lat === "" || lon == null || lat == null) {
        continue;
      }

      $.ajax({
        type: "get",
        url: baseUrl + "/transfer/utm2wgs?x=" + lon + "&y=" + lat + "&utm_id=" + utm_id,
        async: false,
        success: function success(data) {
          wgsLonLat = data.data;
          lanePointArr.push(wgs2gcj(wgsLonLat[0], wgsLonLat[1]));
        }
      });
    }

    lanesArr.push(lanePointArr);
  }

  new Promise(function (resolve) {
    drawPoints(lanesArr);
    resolve();
  }).then(function () {
    return removeTitleCircle();
  });
}

var pi = Math.PI;
var aa = 6378245.0;
var ee = 0.00669342162296594323;

function wgs2gcj(lon, lat) {
  dLat = transform_lat(lon - 105.0, lat - 35.0);
  dLon = transform_lon(lon - 105.0, lat - 35.0);
  radLat = lat / 180.0 * pi;
  magic = Math.sin(radLat);
  magic = 1 - ee * magic * magic;
  sqrtMagic = Math.sqrt(magic);
  dLat = dLat * 180.0 / (aa * (1 - ee) / (magic * sqrtMagic) * pi);
  dLon = dLon * 180.0 / (aa / sqrtMagic * Math.cos(radLat) * pi);
  mgLat = parseFloat(lat) + parseFloat(dLat);
  mgLon = parseFloat(lon) + parseFloat(dLon);
  arr = new Array();
  arr.push(mgLon.toString());
  arr.push(mgLat.toString());
  return arr;
}

function gcj2wgs(lon, lat) {
  dLat = transform_lat(lon - 105.0, lat - 35.0);
  dLon = transform_lon(lon - 105.0, lat - 35.0);
  radLat = lat / 180.0 * pi;
  magic = Math.sin(radLat);
  magic = 1 - ee * magic * magic;
  sqrtMagic = Math.sqrt(magic);
  dLat = dLat * 180.0 / (aa * (1 - ee) / (magic * sqrtMagic) * pi);
  dLon = dLon * 180.0 / (aa / sqrtMagic * Math.cos(radLat) * pi);
  mgLat = parseFloat(lat) + parseFloat(dLat);
  mgLon = parseFloat(lon) + parseFloat(dLon);
  arr = new Array();
  arr.push((parseFloat(lon) * 2 - mgLon).toString());
  arr.push((parseFloat(lat) * 2 - mgLat).toString());
  return arr;
}

function transform_lat(x, y) {
  ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
  ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
  ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
  ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
  return ret;
}

function transform_lon(x, y) {
  ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
  ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
  ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
  ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0;
  return ret;
}