
function getPersonTable(objText,type){
    var obj = "";
    if(objText == "" || objText.length == 0){
        if(type == "c"){
            return "<tr><td colspan='2'>" + "无负责人" + "</td></tr>";
        }else{
            return "<tr><td colspan='2'>" + "无维保人员" + "</td></tr>";
        }
        //return "";
    }else{
        obj = JSON.parse(objText);
    }
    var html ="";
        for(var j = 0; j < obj.length;j++){
        	var str = "<tr>";
        	var o = obj[j];
        	if(type == "c"){
        		str += "<td>" + o.name + "(负责人)" + "</td>";
        		str += "<td>" + o.mobile + "</td>";
        	}
        	else{
        		str += "<td>" + o.name  + "</td>";
        		str += "<td>" + o.mobile + "</td>";
        	}
        		
//            for(var key in o){
//                if(type == "c" && key == "name"){
//                    str += "<td>" + o[key] + "(负责人)" + "</td>";
//                }else{
//                    str += "<td>" + o[key] + "</td>";
//                }
//            }
            str += "<tr>";
            html += str;
        }
    return html;
}

function setSiteMenu(obj){
	var menu = $("#"+obj.id);
	var ul = menu.next();
	if(ul.children("li").length>0)
		return;
	var idsite = obj.id.replace("ids_","");
	var liStr = "<li><a href='alarm.jsp?idsite="+idsite+"' target='_blank' class='dropdown-item' id='ac_menu'>活动报警</a></li>";
	liStr += "<li><a href='resetAlarm.jsp?idsite="+idsite+"' target='_blank' class='dropdown-item' id='re_menu'>复位报警</a></li>";
	liStr += "<li><a href='deviceList.jsp?idsite="+idsite+"' target='_blank' class='dropdown-item' id='de_menu'>设备列表</a></li>";
	liStr += "<li><a href='alarmStatistics.jsp?idsite="+idsite +"' target='_blank' class='dropdown-item' id='al_menu'>报警分析</a></li>";
	if(menu.next().attr("energy")>0)
		liStr += "<li><a href='energyStatistics.jsp?idsite="+idsite+"' target='_blank' class='dropdown-item' id='en_menu'>电量分析</a></li>";
	liStr += "<li><a href='reportManager.jsp?idsite="+idsite+"&type=w' target='_blank' class='dropdown-item' id='we_menu'>周报表</a></li>";
	liStr += "<li><a href='reportManager.jsp?idsite="+idsite+"&type=m' target='_blank' class='dropdown-item' id='mon_menu'>月报表</a></li>";
	menu.next().append(liStr);
}