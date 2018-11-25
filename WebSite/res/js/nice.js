/* Copyright © 2018 Vincent Roller. All rights reserved. */


const Plot = class {
	
	constructor(container, data){
		this.container = document.getElementById(container);
		this.data = data;
		this.colors = ['#779edd','#dd7a5f','#92b74d','#6189a5','#e09b3a','#dbb6ef','#a9cc7a','#95bbe5','#edc690'];
		this.chart_type = 'basic';
	}
	
	include_d3(callback){
		if (!window.d3){
			nice.load_script('res/js/d3.v5.min.js',callback);
		}else{
			callback();
		}
	}
	
	empty_chart(width,height, xlabel,ylabel){
		d3.select(this.container).select('svg').remove();
		this.svg = d3.select(this.container).append('svg');
		
		// dimensions and margins
		this.svg.attr("width", width)
	    		.attr("height", height);
		var w = (+this.svg.attr("width")) -120;
		var h = (+this.svg.attr("height")) -50;
		this.margin = {top: (10), right: (60), bottom: (40), left: (60)};
		
		this.svg.append("rect")
    			.attr("width", w)
    			.attr("height", h)
    			.attr("fill", "#f9f9f9")
			.attr('transform', 'translate(' + this.margin.left + ',' + this.margin.top + ')');
		
		// create a clipping region 
		this.svg.append("defs").append("clipPath")
			.attr("id", "clip")
	  		.append("rect")
			.attr("width", w)
			.attr("height", h);
			
			
		// create scalings
		var Xmax=0;
		var Ymax=0;

		this.xScale = d3.scaleLinear()
		  	.domain([0,Xmax])
		  	.range([0, w]);
		this.yScale = d3.scaleLinear()
		    .domain([0, Ymax])
		    .range([h, 0]);
			
			
		// line generator
		var plt=this;
		if(this.chart_type=='line'){
			this.line = d3.line()
    			.x(function(d) { return plt.xScale(d.x); }) // set the x values for the line generator
    			.y(function(d) { return plt.yScale(d.y); }) // set the y values for the line generator 
				.curve(d3.curveMonotoneX); // apply smoothing to the line
		}
			
		
			
		// create axis objects
		this.xAxis = d3.axisBottom(this.xScale)
			.ticks(6, "s");
		
		this.yAxis = d3.axisLeft(this.yScale)
		    .ticks(6, "s");
			
		
		this.points_g = this.svg.append("g")
		    .attr('transform', 'translate(' + this.margin.left + ',' + this.margin.top + ')')
		    .attr("clip-path", "url(#clip)")
		    .classed("points_g", true);
		
		this.groups = {};
		
		this.data.forEach(function(d){
			if(!Object.keys(plt.groups).includes(d.group)){
				plt.groups[d.group] = [];
			}
			plt.groups[d.group].push(d);
		  	d.x = parseInt(d.x);
		  	if (d.x>Xmax){
			 	 Xmax=d.x;
		 	}
		  	d.y = parseInt(d.y);
		  	if (d.y>Ymax){
			 	 Ymax=d.y;
		 	}
		});
		
		
	  	this.yScale.domain([0, Ymax]);
	  	this.xScale.domain([0, Xmax]);
		
		
		// Draw Axis
		this.gX = this.svg.append('g')
			.attr('transform', 'translate(' + this.margin.left + ',' + (this.margin.top + h) + ')')
			.call(this.xAxis);
		
		
		
		this.gY = this.svg.append('g')
			.attr('transform', 'translate(' + this.margin.left + ',' + this.margin.top + ')')
			.call(this.yAxis)
		
		
		
		this.gX.append('text')
		.text(xlabel)
		.attr("transform", "translate("+(w/2)+"," + (40) + ")")
		.style('text-anchor','middle');
		
		this.gY.append('text')
		.text(ylabel)
		.attr("transform", "rotate(-90)")
		.attr("transform", "translate("+(-50)+"," + (h/2) + "),rotate(-90)")
		.style('text-anchor','middle');
		
		
		this.gX.select('path').style('stroke','none');
		this.gX.selectAll('line').style('stroke','grey');
		this.gX.selectAll('text').style('fill','grey');
		
		this.gY.select('path').style('stroke','none');
		this.gY.selectAll('line').style('stroke','grey');
		this.gY.selectAll('text').style('fill','grey');
			
		

		// Pan and zoom
		var zoom = d3.zoom()
		    .scaleExtent([0, 500])
		    .extent([[0, 0], [w, h]])
		    .on("zoom", function(){
		    	
				var new_xScale = d3.event.transform.rescaleX(plt.xScale);
				var new_yScale = d3.event.transform.rescaleY(plt.yScale);
				// update axes
				plt.gX.call(plt.xAxis.scale(new_xScale));
				plt.gY.call(plt.yAxis.scale(new_yScale));

				if (plt.chart_type=='line'){
					plt.line
						.x(function(d) { return new_xScale(d.x); })
						.y(function(d) { return new_yScale(d.y); });
 
					plt.points_g.selectAll('path').each(function(d,i) {
						d3.select(this).attr('d', plt.line(plt.groups[d3.select(this).attr('group')]));
					});
				}
				
				plt.points_g.selectAll('text').each(function(d,i) {
					d3.select(this).attr('x',new_xScale(d3.select(this).attr('ox')));
					var y = new_yScale(d3.select(this).attr('oy'));
					d3.select(this).attr('y',y+3);
				});
				
				plt.gX.selectAll('line').style('stroke','grey');
				plt.gX.selectAll('text').style('fill','grey');
			
				plt.gY.selectAll('line').style('stroke','grey');
				plt.gY.selectAll('text').style('fill','grey');
				
		});
		this.svg.append("rect")
		    .attr("width", w)
		    .attr("height", h)
		    .style("fill", "none")
		    .style("pointer-events", "all")
	  		.style("stroke", "grey")
	    	.style("stroke-width", "1px")	 
	    	.style("cursor", "crosshair")	    
			.attr('transform', 'translate(' + this.margin.left + ',' + this.margin.top + ')')
		    .call(zoom);
			
			
			
			
		
					
	}
		
	line_chart(w, h, xlabel, ylabel, callback=null, dot="&#x25CF;"){
		var plt = this;
		var calb = callback;
		plt.include_d3(function(){
			plt.chart_type='line';
			plt.empty_chart(w, h, xlabel, ylabel);
			
			var keys=Object.keys(plt.groups);
			var I=0;
			for(var i in keys){
				var c = plt.colors[I%(plt.colors.length)];
				I+=1;
				var g = plt.groups[keys[i]];
				if (g.length>1){
					plt.points_g.append('path')
		  				.attr('d', plt.line(g))
		  				.attr('stroke', c)
		  				.attr('stroke-width', 3)
		  				.attr('fill', 'none')
						.attr('group', keys[i]);
				}else{
					plt.points_g.append('text')
			  			.attr('x', plt.xScale(g[0].x))
						.attr('y', plt.yScale(g[0].y)+3)
			  			.attr('ox', g[0].x)
						.attr('oy', g[0].y)
			  			.attr('fill', c)
			  			.style('font-size', '9').style('text-anchor', 'middle').style('line-height', '9')
						.attr('group', keys[i]).node().innerHTML = dot;
				}
			}
			if (calb!=null){
				calb();
			}
		});
		
	}
	
	scatter_chart(w, h, xlabel, ylabel, dot="&#x25CF;"){
		var plt = this;
		plt.include_d3(function(){
			plt.chart_type='scatter';
			plt.empty_chart(w, h, xlabel, ylabel);
			
			var keys=Object.keys(plt.groups);
			for (var i in keys){
				var c = plt.colors[keys.indexOf(keys[i])%plt.colors.length];
				for (var j in plt.groups[keys[i]]){
					var d = plt.groups[keys[i]][j];
					plt.points_g.append('text')
			  			.attr('x', plt.xScale(d.x))
						.attr('y', plt.yScale(d.y)+3)
						.attr('ox', d.x)
						.attr('oy', d.y)
			  			.attr('fill', c)
			  			.style('font-size', '9').style('text-anchor', 'middle').style('line-height', '9')
						.attr('group', keys[i]).node().innerHTML = dot;
				}
				
			}
			
		});
		
	}

}


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
	
	
	test(){}
	
	// Events //
	
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
	
	
	// Tools //
	
	load_script(path, callback){
		var script = document.createElement('script');
		script.type = 'text/javascript';
		script.onload = function () {
		    if (callback){
				callback();
			}
		};
		script.src = path;
		document.getElementsByTagName('head')[0].appendChild(script);
	}
	
	make_sticky(id, offset){
		
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
	
	add_section(link, description, id, callback){
		var co = document.getElementById(id);
		co.innerHTML += "<div  style='cursor:pointer;' onclick='"+callback+"(\""+link+"\")' onmouseover='nice.change_color(event,\"666666\")' onmouseout='nice.change_color(event,\"aaaaaa\")'>"+
						 	"&#x25CF;&nbsp;"+description+
						"</div>";
	}
	
	
	// Components //
	
	menu(container,title, callback){
		return new Menu(container,title, callback);
	}	
	
	plot(container, data){
		return new Plot(container, data);
	}
	
	toggle(container,opt1,opt2,callback){
		var container = document.getElementById(container);
		container.innerHTML += `
			<div style='white-space: nowrap; align-items:center;' >
				<style>
					input[type=checkbox]{
						height: 0;
						width: 0;
						visibility: hidden;
					}
					label {
						cursor: pointer;
						text-indent: -9999px;
						width: 15px;
						height: 34px;
  						background: #dddddd;
						display: inline-block;
						border-radius: 15px;
						position: relative;
						
					}
					label:after {
						content: '';
						position: absolute;
						top: 2px;
						left: 2px;
						width: 11px;
						height: 11px;
						background: #fff;
						border-radius: 11px;
						transition: 0.5s;
					}
					input:checked + label {
						background: #dddddd;
					}
					input:checked + label:after {
						top: calc(100% - 2px);
						transform: translateY(-100%);
					}
					label:active:after {
						height: 15px;
					}
				</style>
				<input type="checkbox" id="`+container.id+`_switch" onclick='`+callback+`()'/>
				<label for="`+container.id+`_switch"></label>
				<div style='position:relative; bottom:4px; left:-2px; display:inline-block; font-family:courier; font-size:11px; line-height:11px; text-anchor:middle; color:#666666;'>
					<div style=''>`+opt1+`</div>
					<div style='height:8px;'></div>
					<div style=''>`+opt2+`</div>
				</div>
			</div>
		`;
	}
	
}

var nice = new nice_as_class();
