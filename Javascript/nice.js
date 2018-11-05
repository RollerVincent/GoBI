/* LOADING d3.js */
var script = document.createElement('script');
script.type = 'text/javascript';
script.src = 'https://d3js.org/d3.v4.min.js';
document.getElementsByTagName('head')[0].appendChild(script);
/* ------------- */


/*var menu=false;*/
function menu_click(evt,container){	
	var c = document.getElementById(container.id+'_collapsible');
	var collapsed = c.getAttribute("collapsed");
	if (collapsed == 'true'){
		c.style.top = "5vh";
		c.style.height = c.getAttribute('mheight');
		evt.target.setAttribute('fill', '#f2982b');
		c.setAttribute('collapsed','false');
	}else{
		c.style.top = "1vh";
		c.style.height='0vh';
		evt.target.setAttribute('fill', '#a4d85f');
		c.setAttribute('collapsed','true');
	}
	
}
function mouseover_menu(evt,container){
	var collapsed = document.getElementById(container.id+'_collapsible').getAttribute('collapsed');
	if (collapsed == 'true'){
		evt.target.setAttribute('fill', '#a4d85f');
	}
}
function mouseout_menu(evt,container){
	var collapsed = document.getElementById(container.id+'_collapsible').getAttribute('collapsed');
	if (collapsed == 'true'){
		evt.target.setAttribute('fill', '#aaaaaa');
	}
}

function nice_menu(container,title,icon_path = null){
	console.log(title);
	var b = document.getElementById(container);
	
	b.innerHTML  =  "<div style='position:relative; z-index:1; box-shadow: 0px 0px 5px #888888; display:flex; align-items:center; padding-left:1vh; font-weight:bold; font-family:monospace; color:#666666; height:4vh; background-color:#dfdfdf;'>"+
				   	"<svg style='width:4vh; height:4vh;'>"+
				   		"<circle onclick='menu_click(evt,"+container+")' cx='2vh' cy='2vh' r='1vh' fill='#aaaaaa' onmouseover='mouseover_menu(evt,"+container+")' onmouseout='mouseout_menu(evt,"+container+")'/>"+
						"</svg>"+
					title+
					"</div>"+
					"<div id='"+container+"_collapsible' collapsed='true' mwidth='0'; mheight='4vh'; style='overflow:hidden; display:flex; z-index:0; font-weight:bold; font-family:monospace; color:#666666; padding:1vh; box-shadow: 0px 0px 5px #888888; height:0vh; width:20vw; position:absolute; top:1vh; left:1vh; border-radius:10px; background-color:#dfdfdf; -webkit-transition: top 0.5s, height 1s; transition: top 0.5s, height 1s;'>"+
						"<div style='display:flex; align-items:center;'>"+
							"<img src='https://github.com/RollerVincent/GoBI/blob/master/Resources/Images/dna_logo.png?raw=true' style='float:left; width:2vh; height:4vh;'>"+
							"<div style='position:relative; left:1vh;'>Table Of Contents</div>"+
						"</div>"+
					"</div>" + b.innerHTML;
							
					
					
}