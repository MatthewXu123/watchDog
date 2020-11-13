var counter = 0;
var MAXREFRESH = 50;
var refreshCounter = 0;
var f1 = function(){
	
	$.ajax({
		url : 'servlet/valuerefresh',
		type : 'post',
		data : {
			ip: $("#ip").val(),
			devCode : $("#devCode").val(),
			},
		 dataType : 'json', 
		 success: function(data){
			 if(data.lastQueryTime != undefined && data.lastQueryTime != null)
			 {
				 if(data.lastQueryTime != $("#lastQueryTime").val())
				 {
					 console.info("set");
					 if(dJson != null && dJson.length != 0){
						 for(var key in dJson){
							 var dkey = "#" + key;
							 if(data[key] != $(dkey).attr("value"))
							 {
								 $(dkey).removeClass("device_off");
								 $(dkey).removeClass("device_on");
								 if(data[key] == "1")
									 $(dkey).addClass("device_on");
								 else if(data[key] == "0")
									 $(dkey).addClass("device_off");
								 $(dkey).attr("value",data[key]);
							 }
							 
						 }
					 }
					 
					 if(mJson != null && mJson.length != 0){
						 for(var key in mJson){
							 var ele = document.getElementById(key);
							 ele.style.color="";
							 if(ele.innerText != data[key]){
								 ele.innerText = data[key];
								 ele.style.color="red";
							 }
						 }
					 }
					 if(sJson != null && sJson.length != 0){
						 for(var key in sJson){
							 var ele = document.getElementById(key);
							 ele.style.color="";
							 if(ele.innerText != data[key]){
								 ele.innerText = data[key];
								 ele.style.color="red";
							 }
						 }
					 }
					 $("#lastQueryTime").val(data.lastQueryTime);
				 }
			 }
			 refreshCounter++;
			 if(refreshCounter<MAXREFRESH)
				 window.setTimeout(f1,5000);
		 }
		});
}
/*$(document).ready(function(){
	f1();
});*/

