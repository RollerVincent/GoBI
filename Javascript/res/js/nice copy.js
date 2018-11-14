/* Copyright © 2018 Vincent Roller. All rights reserved. */


const Menu = class {

	constructor(container, title, callback){
		this.container = document.getElementById(container);
		this.title = title;
		this.height = 0;
		this.callback = callback
		this.container.innerHTML  =	"<div style='text-anchor:middle; position:relative; z-index:1; box-shadow: 0px 0px 5px #aaaaaa; display:flex; align-items:center; padding-left:6px; font-family:courier; color:#666666; height:24px; line-height:24px; background-color:#dfdfdf;'>"+
			   						"<svg style='width:24px; height:24px;'>"+
			   						"<circle id='"+container+"_circle' onclick='nice.menu_click(event,"+container+","+this.height+")' cx='12px' cy='12px' r='6px' fill='#aaaaaa' onmouseover='nice.change_color(event,\"a4d85f\",marker=\""+container+"_collapsible\")' onmouseout='nice.change_color(event,\"aaaaaa\",marker=\""+container+"_collapsible\")'/>"+
									"</svg><div id='"+container+"_title'>"+
									title+
									"</div></div>"+
									"<div id='"+container+"_collapsible' marked='true' style='line-height:10px; overflow:hidden; z-index:0; font-family:monospace; color:#aaaaaa; padding:6px; box-shadow: 0px 0px 5px #888888; padding:6px; height:0px; position:absolute; top:6px; left:6px; border-radius:10px; background-color:#dfdfdf; -webkit-transition: top 0.5s, height 0.5s; transition: top 0.5s, height 0.5s;'>"+
									"</div>" + this.container.innerHTML;
		this.collapsible = document.getElementById(container+'_collapsible');	
		this.circle = document.getElementById(container+'_circle');
	}
	
	hide(){	
		const s = this.collapsible.style.top;
		this.collapsible.style.top = "6px";
		this.collapsible.style.height='0px';
		this.collapsible.setAttribute('marked','true');
		this.circle.setAttribute('fill', '#aaaaaa');
	}
	
	refresh(tab){
		
		var t = document.getElementById(this.container.id+'_title');
		t.textContent = tab;
		
	}
	
	add(description, icon='//:0', w=12, h=30){
		this.collapsible.innerHTML += 	"<div style='display:flex; align-items:center;' onclick='"+this.callback+"(\""+description+"\")' onmouseover='nice.change_color(event,\"666666\")' onmouseout='nice.change_color(event,\"aaaaaa\")'>"+
									"<img src='"+icon+"' style='float:left; width:"+w+"px; height:"+h+"px;'>"+
									"<div style='margin-left:6px; text-anchor:middle;'>"+description+"</div>"+
								"</div>";
		this.height += h; 	
		this.collapsible.style.width = 'auto';
		this.circle.setAttribute('onclick',"nice.menu_click(evt,"+this.collapsible.id+","+this.height+")");			
	}
	
}


const nice_as_class = class {
	
	load_d3(){
		if (!window.d3){
			var script = document.createElement('script');
			script.type = 'text/javascript';
			script.src = 'https://d3js.org/d3.v4.min.js';
			document.getElementsByTagName('head')[0].appendChild(script);
		}
	}
	
	sticky_header(id, offset){
		
		var h = "position:absolute; top:"+offset+"px; width:100vw;";
		var s = "position:fixed; top:0; width:100vw;";
		
		window.onscroll = function() {scroll()};
		var header = document.getElementById(id);
		var sticky = header.offsetTop;
		function scroll() {
			if (window.pageYOffset >= sticky) {
				header.style=s;
			} else {
	  			header.style=h;
			}			  
		}
	}
	
	new_section(description, id, callback){
		var co = document.getElementById(id);
		co.innerHTML += "<div  style='cursor:pointer;' onclick='"+callback+"(\""+description+"\")' onmouseover='nice.change_color(event,\"666666\")' onmouseout='nice.change_color(event,\"aaaaaa\")'>"+
						 	"&#x25CF;&nbsp;"+description+
						"</div>";
	}
	
	menu_click(evt, collapsible, height){
				var c = document.getElementById(collapsible.id);
				var collapsed = c.getAttribute("marked");
				var color;
				if (collapsed == 'true'){
					c.style.top = '30px';
					c.style.height = height+'px';
					color = '#f2982b';
					c.setAttribute('marked','false');
				}else{
					c.style.top = "6px";
					c.style.height='0px';
					color = '#a4d85f';
					c.setAttribute('marked','true');
				}
				if(evt!='null'){
					evt.target.setAttribute('fill', color);
				}
	
	}
	
	change_color(evt,color,marker){
		
		if (marker){
			var marked = document.getElementById(marker).getAttribute('marked');
			if (marked == 'true'){
				evt.target.setAttribute('fill', '#'+color);
				evt.target.style.color = '#'+color;			
			}
		}else{
			evt.target.setAttribute('fill', '#'+color);
			evt.target.style.color = '#'+color;
			
		}
		
	}
	
	menu(container,title, callback){
		return new Menu(container,title, callback);
	}	
	
}

var nice = new nice_as_class();

