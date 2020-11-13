function touches(obj,direction,fun){ 
        //obj:ID对象  
            //direction:swipeleft,swiperight,swipetop,swipedown,singleTap,touchstart,touchmove,touchend  
        //   划左， 划右， 划上， 划下，点击， 开始触摸， 触摸移动， 触摸结束  
        //fun:回调函数  
        var defaults = {x: 5,y: 5,ox:0,oy:0,nx:0,ny:0};  
        direction=direction.toLowerCase();  
        //配置：划的范围在5X5像素内当点击处理  
        obj.addEventListener("touchstart",function() {  
            defaults.ox = event.targetTouches[0].pageX;  
            defaults.oy = event.targetTouches[0].pageY;  
            defaults.nx = defaults.ox;  
            defaults.ny = defaults.oy;  
            if(direction.indexOf("touchstart")!=-1)fun();  
        }, false);  
        obj.addEventListener("touchmove",function() {  
            event.preventDefault();  
            defaults.nx = event.targetTouches[0].pageX;  
            defaults.ny = event.targetTouches[0].pageY;  
            if(direction.indexOf("touchmove")!=-1)fun();  
        }, false);  
        obj.addEventListener("touchend",function() {  
            var changeY = defaults.oy - defaults.ny;  
            var changeX = defaults.ox - defaults.nx;  
            if(Math.abs(changeX)>Math.abs(changeY)&&Math.abs(changeY)>defaults.y){  
                //左右事件  
                if(changeX > 0) {  
                    if(direction.indexOf("swipeleft")!=-1)fun();  
                }else{  
                    if(direction.indexOf("swiperight")!=-1)fun();  
                }  
            }else if(Math.abs(changeY)>Math.abs(changeX)&&Math.abs(changeX)>defaults.x){  
                //上下事件  
                if(changeY > 0) {  
                    if(direction.indexOf("swipetop")!=-1)fun();  
                }else{  
                    if(direction.indexOf("swipedown")!=-1)fun();  
                }  
            }else{  
                //点击事件  
                if(direction.indexOf("singleTap")!=-1)fun();  
            }  
            if(direction.indexOf("touchend")!=-1)fun();  
        }, false);  
    }

