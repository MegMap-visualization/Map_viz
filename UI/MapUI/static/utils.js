var pi = Math.PI
var aa = 6378245.0
var ee = 0.00669342162296594323


function wgs2gcj(lon, lat) {
    dLat = transform_lat(lon - 105.0, lat - 35.0)
    dLon = transform_lon(lon - 105.0, lat - 35.0)
    radLat = lat / 180.0 * pi
    magic = Math.sin(radLat)
    magic = 1 - ee * magic * magic
    sqrtMagic = Math.sqrt(magic)
    dLat = (dLat * 180.0) / ((aa * (1 - ee)) / (magic * sqrtMagic) * pi)
    dLon = (dLon * 180.0) / (aa / sqrtMagic * Math.cos(radLat) * pi)
    mgLat = parseFloat(lat) + parseFloat(dLat)
    mgLon = parseFloat(lon) + parseFloat(dLon)
    arr = new Array()
    arr.push(mgLon.toString())
    arr.push(mgLat.toString())
    return arr
}

function gcj2wgs(lon, lat) {
    dLat = transform_lat(lon - 105.0, lat - 35.0)
    dLon = transform_lon(lon - 105.0, lat - 35.0)
    radLat = lat / 180.0 * pi
    magic = Math.sin(radLat)
    magic = 1 - ee * magic * magic
    sqrtMagic = Math.sqrt(magic)
    dLat = (dLat * 180.0) / ((aa * (1 - ee)) / (magic * sqrtMagic) * pi)
    dLon = (dLon * 180.0) / (aa / sqrtMagic * Math.cos(radLat) * pi)
    mgLat = parseFloat(lat) + parseFloat(dLat)
    mgLon = parseFloat(lon) + parseFloat(dLon)
    arr = new Array()
    arr.push((parseFloat(lon) * 2 - mgLon).toString())
    arr.push((parseFloat(lat) * 2 - mgLat).toString())
    return arr
}

function transform_lat(x, y) {
    ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x))
    ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0
    ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0
    ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0
    return ret
}


function transform_lon(x, y) {
    ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x))
    ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0
    ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0
    ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0
    return ret
}


// 获取地图四个脚的lnglat
function getContainerLtRbLonLat() {
    var northwest = map.getBounds().getNorthWest(); //西北 lt
    var eastsouth = map.getBounds().getSouthEast(); //东南 rb
    return [northwest.lng.toString().substring(0, 9) + "," + northwest.lat.toString().substring(0, 8), eastsouth.lng
        .toString().substring(0, 9) + "," + eastsouth.lat.toString().substring(0, 8)
    ]
}

let alertCss = ""

function promptBox(info, status, delay = 1500) {
    $('.alert').removeClass(alertCss)
    switch (status) {
        case "info":
            alertCss = "alert-info"
            break;
        case "warning":
            alertCss = "alert-warning"
            break;
        case "danger":
            alertCss = "alert-danger"
            break;
        case "success":
            alertCss = "alert-success"
            break;
        default:
            break;
    }
    $('.alert').html(info).addClass(alertCss).show().delay(delay).fadeOut();
}

function unzip(b64Data) {
    var strData = b64Data;
    // Convert binary string to character-number array
    var charData = strData.split('').map(function (x) {
        return x.charCodeAt(0);
    });
    // Turn number array into byte-array
    var binData = new Uint8Array(charData);
    // // unzip
    var data = pako.inflate(binData);
    // Convert gunzipped byteArray back to ascii string:
    // strData   = String.fromCharCode.apply(null, new Uint16Array(data));
    array = new Uint16Array(data)
    var res = '';
    var chunk = 8 * 1024;
    var i;
    for (i = 0; i < array.length / chunk; i++) {
        res += String.fromCharCode.apply(null, array.slice(i * chunk, (i + 1) * chunk));
    }
    res += String.fromCharCode.apply(null, array.slice(i * chunk));

    strData = res
    return strData;
}

function setWinInfo(extdata) {
    var arrext = []
    for (let i in extdata) {
        let o = {};
        o[i] = extdata[i]; //即添加了key值也赋了value值 o[i] 相当于o.name 此时i为变量
        arrext.push(o)
    }
    arrext.unshift({
        'xmlName': xmlDic[0].xmlName
    }, {
        'remark': xmlDic[0].remark
    }, {
        'ossPath': xmlDic[0].ossPath
    }, {
        'fileMd5': xmlDic[0].fileMd5
    },)

    var result = {}
    arrext.forEach((item) => {
        result[Object.keys(item)[0]] = Object.values(item)[0]
    })

    var filterKeys = ["gmtCreate", "gmtModified", "startLon", "startLat", "endLon", "endLat", "id",
        "roadSection", "laneId", "oldColor", "connection", "type", "pointsUid", "xmlUid",
    ]

    $('#con_top_bottom').text('隐藏车道信息')
    $('#con_top ul').slideDown()
    $('#con_top ul').css("padding", "8px")
    $("#con_top ul li").remove()
    var content = " "

    for (var key in result) {
        if (filterKeys.includes(key)) {
            continue
        }
        val = result[key]
        val = result[key] == null ? '' : result[key]
        content += "<li>" + key + " : " + val + "</li>";
    }
    $("#con_top ul").append(content)
    copy()
}

// 复制车道单独信息
function copy() {
    $("#con_top ul li").click(function () {
        var text = $(this).html().match(/:(.*)/)[1].replace(" ", "")
        var key = $(this).html().match(/(.*):/)[1]
        var input = document.getElementById("input");
        input.value = text; // 修改文本框的内容
        input.select(); // 选中文本
        document.execCommand("copy"); // 执行浏览器复制命令
        promptBox('已复制' + key, "info")
    })
}