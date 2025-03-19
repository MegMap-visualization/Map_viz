var baseUrl = "/api"
// var baseUrl = "/api"
var partZoom=16
$.ajaxSetup({
	beforeSend: function(jqXHR, settings) {
		//在请求前给修改url（增加一个时间戳参数）
		settings.url += settings.url.match(/\?/) ? "&" : "?";
		settings.url += "t=" + new Date().getTime();
	},
});

var roadTypeColorMap = {
	"Unkown": "#FFF8DC",
	"HighWay": "#4B0082",
	"CityRoad": "#00FFFF",
	"Park": "#19679F",
	"Ramp":"#FF4500",
	"Tunnel":"#8B4513"
}


var button_info_map = {
	"search_button": "坐标 or 车道搜索",
	"local_button": "地理位置搜索",
	"xmls_button": "xml列表",
	"draw_button": "临时绘制",
	"csv_read": "临时csv绘制",
	"txt_read": "临时txt绘制",
	"all_button": "展示所有中心线",
	"open_button": "按钮列表",
	"layers": "底图打开",
	"layerRoute": "底图切换",
	"layerTraffic": "实时交通流",
	"con_right_bottom_ruler": "测量工具",
	"con_right_bottom_line": "距离测量",
	"con_right_bottom_area": "面积测量",
	"lon_lat_button": "wgs坐标搜索",
	"uid_part_search_button": "当前绘制内车道搜素,多个车道使用分号隔开",
	"uid_global_search_button": "全局车道搜素，多个车道使用分号隔开",
	"uid_obj_part_search_button": "当前绘制内Object搜素，多个车道使用分号隔开",
	"forbidRefresh": "禁用单击加载功能",
	"hideMap": "隐藏路网",
	"hidecenter": "展示中心线",
	"hideGuides": "隐藏中心参考线",
	"help": "帮助文档",
	"changeColorRender": "高亮RoadType",
	"frame_button":"画框拾取坐标",
	"child_xml_button": "选取子图",
	"test_button": "测试结果可视化",
	"check_button": "检查routing",
	"find_point_button": "lane关键点查询"
}

$(function() {
	$.ajaxSetup({
		// type: "POST",
		error: function(jqXHR, textStatus, errorThrown) {
			// $.messager.show({
			// 	title: '提示信息',
			// 	msg: '<center style="color:red">网络异常，请稍后再试</center>',
			// 	timeout: 2000,
			// 	showType: 'slide'
			// });
			// console.log(jqXHR)
			switch (jqXHR.status) {
				case (500):
					alert("系统内部错误！！！请联系管理员");
					break;
				case (403):
					alert("操作频繁！！！")
					break;
				case (401):
					window.location.href = "./401.html"
					break;
				case (503):
					window.location.href = "./503.html"
					break;
				default:
					console.log("未知错误 Code:" + jqXHR.status);
			}
		}
	});
});
