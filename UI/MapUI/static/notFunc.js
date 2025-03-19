var buttons = document.getElementsByName('button')

// for (var i in buttons) {
for (var i = 4; i < buttons.length; i++) {
    $("#" + $(buttons[i]).children().attr('id')).hide()
    button_id = $(buttons[i]).attr("id")
    $("#" + button_id).on("mouseover", function () {
        let that = this
        $(this).children().show()
        $("#" + $(this).children().attr('id')).text(button_info_map[$(this).attr("id")])
        $(that).children().on("mouseover", function (e) {
            $(that).children().hide()
            e.stopPropagation()
        })

    })

    $("#" + button_id).on("mouseout", function () {
        $(this).children().hide()
        $("#" + $(this).children().attr('id')).text("")
    })
}

//针对左侧导航栏设定的鼠标事件
var left_bar_buttons = document.getElementsByName('left_bar_button')
var button_info_show = document.getElementsByName('button_info_show')
for (var i = 0; i < left_bar_buttons.length; i++) {
    button_id = $(left_bar_buttons[i]).attr("id")
    button_show = $(button_info_show[i]).attr("id")
    $("#" + button_show).hide()
    $("#" + button_id).on("mouseover", function () {
        let that = this
        $(this).css("cursor", "pointer")
        if (Object.keys(displays).includes($(this).attr("id")) && $(this).attr("id") !== left_bar_select_id)
            $(this).css("color", left_bar_click_color)
        $("#" + $(this).children().attr('id')).show()
        $("#" + $(that).children().attr('id')).on('mouseover', function (e) {
            $("#" + $(that).children().attr('id')).hide()
            e.stopPropagation()
        })
        $("#" + $(this).children().attr('id')).text(button_info_map[$(this).attr("id")])
    })

    $("#" + button_id).on("mouseout", function () {
        if (Object.keys(displays).includes($(this).attr("id")) && $(this).attr("id") !== left_bar_select_id)
            $(this).css("color", left_bar_color)
        $("#" + $(this).children().attr('id')).hide()
        $("#" + $(this).children().attr('id')).text("")
    })
}

