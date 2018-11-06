/* Copyright © 2018 Vincent Roller. All rights reserved. */

const nice_as_class = class {
	
	load_d3(){
		if (!window.d3){
			var script = document.createElement('script');
			script.type = 'text/javascript';
			script.src = 'https://d3js.org/d3.v4.min.js';
			document.getElementsByTagName('head')[0].appendChild(script);
		}
	}
	
	menu(container,title){

		this.init = function(){
			this.container = document.getElementById(container);	
			this.height = 0;
			
			this.container.innerHTML  =  "<div style='text-anchor:middle; position:relative; z-index:1; box-shadow: 0px 0px 5px #888888; display:flex; align-items:center; padding-left:6px; font-weight:bold; font-family:monospace; color:#666666; height:24px; line-height:24px; background-color:#dfdfdf;'>"+
						   		"<svg style='width:24px; height:24px;'>"+
						   			"<circle id='"+container+"_circle' onclick='nice.menu_click(evt,"+container+","+this.height+")' cx='12px' cy='12px' r='6px' fill='#aaaaaa' onmouseover='nice.menu_mouseover(evt,"+container+")' onmouseout='nice.menu_mouseout(evt,"+container+")'/>"+
								"</svg>"+
							title+
							"</div>"+
							"<div id='"+container+"_collapsible' collapsed='true' style='line-height:10px; overflow:hidden; z-index:0; font-family:monospace; color:#666666; padding:6px; box-shadow: 0px 0px 5px #888888; padding:6px; height:0px; position:absolute; top:6px; left:6px; border-radius:10px; background-color:#dfdfdf; -webkit-transition: top 1s, height 0.5s; transition: top 1s, height 0.5s;'>"+
							
							"</div>" + this.container.innerHTML;
			
			this.collapsible = document.getElementById(container+'_collapsible');	
			this.circle = document.getElementById(container+'_circle');	
				
		}
		this.init();
				
		this.add = function(description,icon='//:0',w = 12,h = 24){
			this.collapsible.innerHTML += 	"<div style='display:flex; align-items:center;'>"+
								"<img src='"+icon+"' style='float:left; width:"+w+"px; height:"+h+"px;'>"+
								"<div style='margin-left:6px; text-anchor:middle;'>"+description+"</div>"+
							"</div>";
			this.height += 24; 	
			this.collapsible.style.width = 'auto';
			this.circle.setAttribute('onclick',"nice.menu_click(evt,"+container+","+this.height+")");
			
					
		}
		
		this.menu_click = function (evt,container,height){	
			var c = document.getElementById(container.id+'_collapsible');
			var collapsed = c.getAttribute("collapsed");
			if (collapsed == 'true'){
				c.style.top = '30px';
				c.style.height = height+'px';//c.getAttribute('mheight');
				evt.target.setAttribute('fill', '#f2982b');
				c.setAttribute('collapsed','false');
			}else{
				c.style.top = "6px";
				c.style.height='0px';
				evt.target.setAttribute('fill', '#a4d85f');
				c.setAttribute('collapsed','true');
			}
	
		}
		this.menu_mouseover = function (evt,container){
			var collapsed = document.getElementById(container.id+'_collapsible').getAttribute('collapsed');
			if (collapsed == 'true'){
				evt.target.setAttribute('fill', '#a4d85f');
			}
		}
		this.menu_mouseout = function (evt,container){
			var collapsed = document.getElementById(container.id+'_collapsible').getAttribute('collapsed');
			if (collapsed == 'true'){
				evt.target.setAttribute('fill', '#aaaaaa');
			}
		}
		
		return this;	
	}
	
	
}
var nice = new nice_as_class();
console.log("maio");


