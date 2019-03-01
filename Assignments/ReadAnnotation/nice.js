/* Copyright © 2018 Vincent Roller. All rights reserved. */


const Plot = class {
	
	constructor(container){
		this.container = document.getElementById(container);
		this.colors = ['#779edd','#dd7a5f','#92b74d','#6189a5','#e09b3a','#dbb6ef','#a9cc7a','#95bbe5','#edc690'];
		this.legendOffset = 0;
		this.id  = 0;
		this.plt=this;
	}
	
	include_d3(callback){
		if (!window.d3){
			nice.load_script('d3.v5.min.js',callback);
		}else{
			callback();
		}
	}
	
	init(width, height, xlabel, ylabel, title, minimized, zoomable, callback=null){
		var plt = this;
		plt.include_d3(function(){
			plt.empty_chart(xlabel,ylabel, title, callback, minimized);
		});
		this.width = width;
		this.height = height;
		this.zoomable = zoomable;
	}
	
	min(x,y){
		this.Xmax = x;
		this.Ymax = y;
	}
	
	empty_chart(xlabel,ylabel,title, callback=null, minimizeticks=true){
		d3.select(this.container).select('svg').remove();
		this.svg = d3.select(this.container).append('svg');
		
		// dimensions and margins
		this.svg.attr("width", this.width)
	    		.attr("height", this.height);
		this.w = (+this.svg.attr("width")) -180;
		this.h = (+this.svg.attr("height")) -80;
		this.margin = {top: (40), right: (120), bottom: (40), left: (60)};
		
		this.svg.append("rect")
    			.attr("width", this.w)
    			.attr("height", this.h)
    			.attr("fill", "#f7f7f7")
				.attr('transform', 'translate(' + this.margin.left + ',' + this.margin.top + ')');

		
		
		
		// create a clipping region 
		this.svg.append("defs").append("clipPath")
			.attr("id", "clip")
	  		.append("rect")
			.attr("width", this.w)
			.attr("height", this.h);
			
			
		// create scalings
		this.Xmax=0;
		this.Ymax=0;
		this.Xmin= Number.MAX_SAFE_INTEGER;
		this.Ymin= Number.MAX_SAFE_INTEGER;

		this.xScale = d3.scaleLinear()
		  	.domain([0,1])
		  	.range([0, this.w]);
		this.yScale = d3.scaleLinear()
		    .domain([0, 1])
		    .range([this.h, 0]);
			
		this.scaleFactor = this.xScale(1)-this.xScale(0);	
		
		// line generator
		var plt=this;
		
		this.line = d3.line()
    		.x(function(d) { return plt.xScale(d.x); }) // set the x values for the line generator
    		.y(function(d) { return plt.yScale(d.y); }) // set the y values for the line generator
			.curve(d3.curveMonotoneX); // apply smoothing to the line
		
			
		// create axis objects
		this.xAxis = d3.axisBottom(this.xScale)
		   // .ticks(6,".0s")
			.tickSize(-this.h);
		if(minimizeticks){
		    this.xAxis.ticks(6,".0s");
		}
		
		this.yAxis = d3.axisLeft(this.yScale)
		   // .ticks(6,".0s")
			.tickSize(-this.w);
		if(minimizeticks){
		    this.yAxis.ticks(6,".0s");
		}
		
		
		
		this.groups = {};
		
		
		
		// Draw Axis
		this.gX = this.svg.append('g')
			.attr('transform', 'translate(' + this.margin.left + ',' + (this.margin.top + this.h) + ')')
			.style('font-family','Courier New')
			.call(this.xAxis);
		
		this.gY = this.svg.append('g')
			.attr('transform', 'translate(' + (this.margin.left) + ',' + this.margin.top + ')')
			.style('font-family','Courier New')
			.call(this.yAxis)
		
		this.canvas = this.svg.append("g")
		    .attr('transform', 'translate(' + this.margin.left + ',' + this.margin.top + ')')
		    .attr("clip-path", "url(#clip)")
		    .classed("canvas", true);
		
		
		this.gX.append('text')
		.text(xlabel)
		.attr("transform", "translate("+(this.w/2)+"," + (35) + ")")
		.style('text-anchor','middle')
		.style('font-size','12px')
		.style('font-weight','bold');
		
		this.gX.append('text')
		.text(title)
		.attr("transform", "translate("+(this.w/2)+"," + (-this.h-10) + ")")
		.style('text-anchor','middle')
		.style('font-size','14px')
		.style('font-weight','bold');
		
		
		
		this.gY.append('text')
		.text(ylabel)
		.attr("transform", "rotate(-90)")
		.attr("transform", "translate("+(-40)+"," + (this.h/2) + "),rotate(-90)")
		.style('text-anchor','middle')
		.style('font-size','12px')
		.style('font-weight','bold');
		
		
		
		this.gX.select('path').style('stroke','none');
		this.gX.selectAll('line').style('stroke','#ffffff');
		this.gX.selectAll('text').style('fill','grey');
		
		this.gY.select('path').style('stroke','none');
		this.gY.selectAll('line').style('stroke','#ffffff');
		this.gY.selectAll('text').style('fill','grey');
			
		

		// Pan and zoom
		this.zoom = d3.zoom()
		    .scaleExtent([0, 500])
		    .extent([[0, 0], [this.w, this.h]])
		    .on("zoom", function(){
		    	
				var new_xScale = d3.event.transform.rescaleX(plt.xScale);
				var new_yScale = d3.event.transform.rescaleY(plt.yScale);

				plt.gX.call(plt.xAxis.scale(new_xScale));
				plt.gY.call(plt.yAxis.scale(new_yScale));
				
				plt.gX.selectAll('line').style('stroke','#ffffff');
				plt.gX.selectAll('text').style('fill','grey');
			
				plt.gY.selectAll('line').style('stroke','#ffffff');
				plt.gY.selectAll('text').style('fill','grey');

				var factor = (new_xScale(1)-new_xScale(0))/plt.scaleFactor;
				
				plt.line
					.x(function(d) { return new_xScale(d.x); })
					.y(function(d) { return new_yScale(d.y); });
 
				plt.canvas.selectAll('path').each(function() {
					var p = d3.select(this);
					p.attr('d', plt.line(plt.groups[p.attr('group')]));
					if (p.attr('dx')!=0){
						var x = p.attr('dx')*factor + (p.attr('dy')*factor) - 3;
						if (x>=6){
							p.style("stroke-dasharray", (x+", 3"));
						}else{
							p.style("stroke-dasharray", (p.attr('dx')*factor)+", "+(p.attr('dy')*factor));
						
						}
					}
				});
				
				
				plt.canvas.selectAll('text').each(function() {
					var d = d3.select(this);
					d.attr('x',new_xScale(d.attr('ox')));
					var y = new_yScale(d.attr('oy'));
					d.attr('y',y+3.5);
				});
				
				
				
		});
		
		if(this.zoomable){
			this.zrect = this.svg.append("rect")
		    	.attr("width", this.w)
		    	.attr("height", this.h)
		    	.style("fill", "none")
		    	.style("pointer-events", "all")
	  			.style("stroke", "#aaaaaa")
	    		.style("stroke-width", "1px")	 
				.style("cursor", "crosshair")	    
				.attr('transform', 'translate(' + this.margin.left + ',' + this.margin.top + ')');
		    
			this.zrect.call(this.zoom);
			
			this.svg.append("text")
				.attr("fill", "#666666")
				.attr('transform', 'translate(' + (this.margin.left+this.w-11) + ',' + (this.margin.top+11) + ')')
				.style('font-size','10')
				.style('cursor','pointer')
				.on('click', function(){ 
					plt.zrect.transition().duration(750).call(plt.zoom.transform, d3.zoomIdentity);
				})
				.node().innerHTML="&#8635;";
		}else{
			this.zrect = this.svg.append("rect")
	    		.attr("width", this.w)
	    		.attr("height", this.h)
	    		.style("fill", "none")
	    		.style("pointer-events", "all")
  				.style("stroke", "#aaaaaa")
    			.style("stroke-width", "1px")	 	    
				.attr('transform', 'translate(' + this.margin.left + ',' + this.margin.top + ')');
		}
		
		if (callback!=null){
			callback();
		}
	}





	legend(labels,symbols,cols, title, colorOffset=0, width=100, contentbold=true){
        this.svg = d3.select(this.container).select('svg');
        this.w = (+this.svg.attr("width")) -180;
		this.h = (+this.svg.attr("height")) -80;
		this.margin = {top: (40), right: (120), bottom: (40), left: (60)};
        this.svg.append("rect")
    			.attr("width", width)
    			.attr("height", symbols.length*14)
    			.attr("fill", "#f7f7f7")
				.attr('transform', 'translate(' + (this.margin.left+this.w+10) + ',' + (this.margin.top+this.legendOffset+10) + ')')
				.attr('stroke', '#aaaaaa');

        if (cols==null){

            cols=[]
            for (var i=0;i<symbols.length;i++){
                cols.push(this.colors[(i+colorOffset)%this.colors.length]);
            }

        }


		for (var i=0;i<symbols.length;i++){
			var te = this.svg.append("text");
			te
                .attr('text-anchor','left-middle')

				.attr('transform', 'translate(' + (this.margin.left+this.w+10+3) + ',' + (this.margin.top+this.legendOffset+i*14+7+3+10) + ')')
				.style('font-size', 10).style('font-weight', 'bolder').style('font-family', 'Courier New').style('fill', cols[i])

				.node().innerHTML=symbols[i];


				this.svg.append("text")
                .attr('text-anchor','left-middle')

				.attr('transform', 'translate(' + (this.margin.left+this.w+10+3+14) + ',' + (this.margin.top+this.legendOffset+i*14+7+3+10) + ')')
				.style('font-size', 10).style('font-family', 'Courier New').style('fill', 'grey')

				.text(labels[i]);
			
			if(contentbold){
				te.style('font-weight', 'bold');
			}
				
				
				
		}


		this.svg.append("text")
                .attr('text-anchor','middle')

				.attr('transform', 'translate(' + (this.margin.left+this.w+10+width/2) + ',' + (this.margin.top+this.legendOffset+6) + ')')
				.style('font-size', 10).style('font-weight', 'bold').style('font-family', 'Courier New').style('fill', 'grey')

				.node().innerHTML=title;

		this.legendOffset += symbols.length*14+5+10+5;


	}


	letters(data, letter="&#9679;", color=null, colorOffset=0, alpha=1, textAnchor='middle'){
		var grs = this.data_to_groups(data);
		data = null;
		var keys = Object.keys(grs);
		for (var i in keys){
			var c = color;
			if (color==null){
				c = this.plt.colors[(keys.indexOf(keys[i])+colorOffset)%this.plt.colors.length];
			}
			for (var j in grs[keys[i]]){
				var d = grs[keys[i]][j];
				this.plt.canvas.append('text')
			 		.attr('x', this.plt.xScale(d.x))
					.attr('y', this.plt.yScale(d.y)+3.5)
					.attr('ox', d.x)
					.attr('oy', d.y)
					.attr('fill', c)
					.style('opacity', alpha)
					.style('font-size', 12).style('font-family','monospace').style('text-anchor', textAnchor).style('line-height', 12)
					.attr('group', keys[i]).node().innerHTML = letter;
			}		
		}
		grs = null;
	}
	
	shape(data, color=null, colorOffset=0, alpha=1){
		var grs = this.data_to_groups(data);
		data = null;
		var keys = Object.keys(grs);
		var I=0;
		var plt = this;
		for(var i in keys){
			var c = color;
			if (color==null){
				c = plt.colors[(I+colorOffset)%(plt.colors.length)];
			}
			I+=1;
			var g = grs[keys[i]];
			
			var f = {};
			f["x"]=g[0]["x"];
			f["y"]=0;
			f["group"]=g[0]["group"];
			var l = {};
			l["x"]=g[g.length-1]["x"];
			l["y"]=0;
			l["group"]=g[0]["group"];
			g.push(l);
			g.push(f);
			
			plt.canvas.append('path')
				.attr('d', plt.line(g))
				//.attr('stroke', c)
				//.attr('stroke-width', width)
				.attr('fill', c)
				.style('opacity', alpha)
				//.style('stroke-opacity', alpha)
				.attr('group', keys[i]);
			
			grs[keys[i]] = g;
			
		}
		this.groups = Object.assign({}, this.groups, grs);
		grs=null;
	}
	
	path(data, color=null, colorOffset=0, alpha=1, dot="&#x25CF;", width=3){
		this.drawline(data, color, colorOffset, alpha, dot, width);
	}
	
	dashed(data, color=null, colorOffset=0, alpha=1, dot="&#x25CF;", width=3){
		this.drawline(data, color, colorOffset, alpha, dot, width, 6, 3);
	}
	
	drawline(data, color=null, colorOffset=0, alpha=1, dot="&#x25CF;", width=3, dx=0, dy=0){
		var grs = this.data_to_groups(data);
		data = null;
		var keys = Object.keys(grs);
		var I=0;
		var plt = this;
		
		
		
		
		for(var i in keys){
			var c = color;
			if (color==null){
				c = plt.colors[(I+colorOffset)%(plt.colors.length)];
			}
			I+=1;
			var g = grs[keys[i]];
			if (g.length>1){
				
				plt.canvas.append('path')
	  				.attr('d', plt.line(g))
	  				.attr('stroke', c)
	  				.attr('stroke-width', width)
	  				.attr('fill', 'none')
					.style('stroke-opacity', alpha)
					.style("stroke-dasharray", dx+", "+dy)
			  		.attr('dx', dx)
					.attr('dy', dy)
					.attr('group', keys[i]);
				
			}else{
				this.plt.canvas.append('text')
			  		.attr('x', plt.xScale(g[0].x))
					.attr('y', plt.yScale(g[0].y)+3)
			  		.attr('ox', g[0].x)
					.attr('oy', g[0].y)
			  		.attr('fill', c)
					.style('opacity', alpha)
			  		.style('font-size', '9').style('text-anchor', 'middle').style('line-height', '9')
					.attr('group', keys[i]).node().innerHTML = dot;
			}
		}
		
		this.groups = Object.assign({}, this.groups, grs);
		grs=null;
	}
	
	data_to_groups(data){
		var groups = {};
		var plt = this;
		data.forEach(function(d){
			var s = plt.id+"#"+d.group;
			if(!Object.keys(groups).includes(s)){
				groups[s] = [];
			}
			groups[s].push(d);
		  	//d.x = parseInt(d.x);

		  	if (d.x>plt.Xmax){
			 	 plt.Xmax=d.x;
		 	}
			if (d.x<plt.Xmin){
				plt.Xmin=d.x;
			}
		  	//d.y = parseInt(d.y);
		  	if (d.y>plt.Ymax){
			 	 plt.Ymax=d.y;
		 	}
			if (d.y<plt.Ymin){
				plt.Ymin=d.y;
			}
		});
		plt.reposition(plt);
		plt.id+=1;
		return groups;
	}
		
	reposition(plt){
		plt.yScale = d3.scaleLinear()
			.domain([plt.Ymin, plt.Ymax])
	  		.range([plt.h, 0]);
		plt.xScale = d3.scaleLinear()
			.domain([plt.Xmin, plt.Xmax])
			.range([0, plt.w]);
		plt.gX.call(plt.xAxis.scale(plt.xScale));
		plt.gY.call(plt.yAxis.scale(plt.yScale));
		plt.gX.selectAll('line').style('stroke','#ffffff');
		plt.gX.selectAll('text').style('fill','grey');
		plt.gY.selectAll('line').style('stroke','#ffffff');
		plt.gY.selectAll('text').style('fill','grey');
		
		
		
		plt.scaleFactor = plt.xScale(1)-plt.xScale(0);	
		
		
		
		plt.line
			.x(function(d) { return plt.xScale(d.x); })
			.y(function(d) { return plt.yScale(d.y); });

			plt.canvas.selectAll('path').each(function() {
				var p = d3.select(this);
				p.attr('d', plt.line(plt.groups[p.attr('group')]));
				
			});
	
	
		plt.canvas.selectAll('text').each(function() {
			var d = d3.select(this);
			d.attr('x',plt.xScale(d.attr('ox')));
			var y = plt.yScale(d.attr('oy'));
			d.attr('y',y+3);
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
