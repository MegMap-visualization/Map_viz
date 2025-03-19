var cankaoxian = []
var chedaoxian = []
var chedao = []

// 点坐标搜索
function searchLatLon() {
	if ($("#lat").val() == "Lat" || $("#lon").val() == "Lon") {
		window.alert("请输入点坐标")
		return
	}
	lat = $("#lat").val()
	lon = $("#lon").val()
	setMarker(lon, lat)
}

var globalMarkList = new Array()

// 点坐标标记
function setMarker(lon, lat) {
	map.remove(globalMarkList)
	wgsStr = "\nwgs: " + lat + "," + lon
	gcjArr = wgs2gcj(lon, lat)
	$.get(baseUrl + "/transfer/wgs2utm?lon=" + lon + "&lat=" + lat, function(data) {
		utm_x_y = data.data
		utmStr = "utm: " + utm_x_y[0] + "," + utm_x_y[1] + "; utm_id：" + utm_x_y[2]
	}).then(function() {
		var icon = new AMap.Icon({
			// 图标尺寸
			size: new AMap.Size(37, 49),
			// 图标的取图地址
			image: '//a.amap.com/jsapi_demos/static/demo-center/icons/poi-marker-default.png',
			// 图标所用图片大小
			imageSize: new AMap.Size(37, 49),
		})
		var content =
			"<ul style='list-style: none;padding: 3px;margin: 0;border-color: none;'><li style='margin-bottom: 2px'>" +
			wgsStr + "</li><li>" + utmStr + "</li></ui>"
		var marker = new AMap.Marker({
			position: [gcjArr[0], gcjArr[1]], // 经纬度对象，也可以是经纬度构成的一维数组[116.39, 39.9]
			icon: icon,
			anchor: 'bottom-center',
			label: {
				content: content,
				direction: "top"
			}
		});
		// 将创建的点标记添加到已有的地图实例：
		globalMarkList.push(marker)
		map.add(marker)
		map.setCenter([gcjArr[0], gcjArr[1]])
	})
}


// utm_id查找
function searchUtmXy() {
	if ($("#lat").val() == "Lat" || $("#lon").val() == "Lon") {
		window.alert("请输入点坐标")
		return
	}
	if ($("#utm_id").val() == "utm区") {
		window.alert("请输入utm_id")
		return
	}
	x = $("#lat").val()
	y = $("#lon").val()
	utm_id = $("#utm_id").val()
	$.get(baseUrl + "/transfer/utm2wgs?x=" + x + "&y=" + y + "&utm_id=" + utm_id, function(data) {
		wgsLonLat = data.data
	}).then(function() {
		setMarker(wgsLonLat[0], wgsLonLat[1])
	})
}


function tempDrawingPoints() {
	addTitleCircle()
	try {
		re1 = new RegExp("'", "g")
		re2 = new RegExp('"', "g")
		var points = $("#points").val().replace(re1, "").replace(re2, "")
		console.log(points)
		var lanesPoints = points.split("|")
		var lanesArr = new Array()
		for (var lane in lanesPoints) {
			pointList = lanesPoints[lane].split(";")
			var pointsArr = new Array()
			for (var row in pointList) {
				lonLat = pointList[row].split(",")

				lonLatArr = wgs2gcj(lonLat[0], lonLat[1])

				pointsArr.push(lonLatArr)
			}
			lanesArr.push(pointsArr)
		}
		drawPoints(lanesArr)
		removeTitleCircle()
	} catch (err) {
		window.alert("请正确输入点序列！")
		removeTitleCircle()
	}
}

var loadSuccess = false
var lanes

function drawFromCsv() {
	$("#btn_file").click()
	var file1 = document.getElementById("btn_file")
	try {

		file1.onchange = function() {
			addTitleCircle()
			var file = file1.files[0]
			// console.log(file.name)
			if (!file.name.endsWith(".csv")) {
				window.alert("请上传CSV格式文件")
				removeTitleCircle()
				return
			}
			//读取为二进制
			var reader = new FileReader()
			reader.readAsText(file, 'utf-8')
			reader.onload = function() {
				var r = new RegExp("\r", "")
				var str = reader.result
				lanes = str.split(";")

				determineDraw()
			}
		}
	} catch (err) {
		window.alert("请按照要求设置csv文件")
		removeTitleCircle()
	}
}

function determineDraw() {
	var lanesArr = new Array()
	for (var index in lanes) {
		var lanePoints = lanes[index].split('\n')
		var lanePointArr = new Array()
		for (var i = 0; i < lanePoints.length; i++) {
			var lonLat = lanePoints[i].split(',')
			lon = lonLat[0]
			lat = lonLat[1]
			utm_id = lonLat[2]
			if (lon === "" || lat === "" || lon == null || lat == null) {
				continue
			}
			$.ajax({
				type: "get",
				url: baseUrl + "/transfer/utm2wgs?x=" + lon + "&y=" + lat + "&utm_id=" + utm_id,
				async: false,
				success: function(data) {
					wgsLonLat = data.data
					lanePointArr.push(wgs2gcj(wgsLonLat[0], wgsLonLat[1]))
				}
			});

		}
		lanesArr.push(lanePointArr)
	}
	new Promise((resolve) => {
		drawPoints(lanesArr)
		resolve();
	}).then(() => removeTitleCircle())

}

function drawFromTxt() {
	$("#btn_file_txt").click()
	const file1 = document.getElementById("btn_file_txt")
	try {

		file1.onchange = function() {
			addTitleCircle()
			const file = file1.files[0]
			// console.log(file.name)
			if (!file.name.endsWith(".txt")) {
				window.alert("请上传txt格式文件")
				removeTitleCircle()
				return
			}
			//读取为二进制
			const reader = new FileReader();
			reader.readAsText(file, 'utf-8')
			reader.onload = function() {
				const str = reader.result
				lanes = str.split("\n")
				determineDrawTxt()
			}
		}
	} catch (err) {
		window.alert("请按照要求设置txt文件")
		removeTitleCircle()
	}
}

function determineDrawTxt() {
	const laneArr = [];
	for (const index in lanes) {
		const lanePoints = lanes[index].split(' ');
		if (lanePoints.length < 2) {
			continue
		}
		const lanePointArr = wgs2gcj(lanePoints[0], lanePoints[1]);
		if (!isNaN(lanePointArr[0]) && !isNaN(lanePointArr[1])) {
			laneArr.push(lanePointArr)
		}
	}
	const lanesArr = [];
	lanesArr.push(laneArr)
	new Promise((resolve) => {
		drawPoints(lanesArr)
		resolve();
	}).then(() => removeTitleCircle())

}


function drawFromTxt() {
	$("#btn_file_txt").click()
	const file1 = document.getElementById("btn_file_txt")
	try {

		file1.onchange = function () {
			addTitleCircle()
			const file = file1.files[0]
			// console.log(file.name)
			if (!file.name.endsWith(".txt")) {
				window.alert("请上传txt格式文件")
				removeTitleCircle()
				return
			}
			//读取为二进制
			const reader = new FileReader();
			reader.readAsText(file, 'utf-8')
			reader.onload = function () {
				const str = reader.result
				lanes = str.split("\n")
				determineDrawTxt()
			}
		}
	} catch (err) {
		window.alert("请按照要求设置txt文件")
		removeTitleCircle()
	}
}


function determineDrawTxt() {
	const laneArr = [];
	for (const index in lanes) {
		const lanePoints = lanes[index].split(' ');
		if (lanePoints.length < 2) {
			continue
		}
		const lanePointArr = wgs2gcj(lanePoints[0], lanePoints[1]);
		if (!isNaN(lanePointArr[0]) && !isNaN(lanePointArr[1])) {
			laneArr.push(lanePointArr)
		}
	}
	const lanesArr = [];
	lanesArr.push(laneArr)
	new Promise((resolve) => {
		drawPoints(lanesArr)
		resolve();
	}).then(() => removeTitleCircle())

}
