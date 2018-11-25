const Exon_Skipping = class {

	constructor(){
		this.container = document.getElementById('main_container');	
	}
	
	load(){
		
		this.container.innerHTML = "<div style='width:100vw;'>"+
				"<div id ='exon_skipping_selector' style='padding:6px; padding-left:34px;  position:absolute; left:10vw; top:25px; border-radius:10px; width:196px; background-color:#eeeeee; color:#aaaaaa; font-size:12px; line-height:25px; font-family:courier;'></div>"+
				"<div id ='plot_container' class='margeable' style='transition: width 0.3s; box-shadow:inset 0 0 4px #aaaaaa; padding-left:30px; padding-right:30px; overflow:scroll;  position:absolute; top:10px; left:12px; border-radius:10px; color:#444444; background-color:#fefefe; font-size:12px; font-family:courier;'></div>"+
				"<div id ='exon_skipping_arrow' onclick='tab_content.toggle_right_menu()' style='transition: left 0.35s; cursor:pointer; text-shadow:0 0 4px #aaaaaa; font-size:10px; line-height:10px; position:absolute; color:#666666;'>&#9654;</div>"+
			"</div>";	
			
		this.right_toggle = false;
		this.toggle_right_menu();
		var w = Math.max(document.documentElement.clientWidth,800);
		//var w = document.documentElement.clientWidth;
		
		document.getElementById('plot_container').style.height = (document.documentElement.clientHeight - 42 - 2) + "px";
		document.getElementById('plot_container').style.width = (w - 24 - 200 - 12 - 30 - 30) + "px";
		document.getElementById('exon_skipping_selector').style.left = (12 + w - 24 - 200 - 24 - 12) + "px";
		document.getElementById('exon_skipping_arrow').style.left = (12 + w - 24 - 200 - 24) + "px"
		document.getElementById('exon_skipping_arrow').style.top = (25-5+1) + "px";
		
		
		nice.add_section('Overview','Overview','exon_skipping_selector', 'tab_content.select');
		nice.add_section('Homo_sapiens.GRCh38.93.gtf','Homo_sapiens.GRCh38.93','exon_skipping_selector', 'tab_content.select');
		nice.add_section('Homo_sapiens.GRCh38.90.gtf','Homo_sapiens.GRCh38.90','exon_skipping_selector', 'tab_content.select');
		nice.add_section('Homo_sapiens.GRCh38.86.gtf','Homo_sapiens.GRCh38.86','exon_skipping_selector', 'tab_content.select');
		nice.add_section('Homo_sapiens.GRCh37.75.gtf','Homo_sapiens.GRCh37.75','exon_skipping_selector', 'tab_content.select');
		nice.add_section('Homo_sapiens.GRCh37.67.gtf','Homo_sapiens.GRCh37.67','exon_skipping_selector', 'tab_content.select');
		nice.add_section('gencode.v10.annotation.gtf','gencode.v10.annotation','exon_skipping_selector', 'tab_content.select');
		nice.add_section('gencode.v25.annotation.gtf','gencode.v25.annotation','exon_skipping_selector', 'tab_content.select');
		nice.add_section('Mus_musculus.GRCm38.75.gtf','Mus_musculus.GRCm38.75','exon_skipping_selector', 'tab_content.select');
		nice.add_section('Saccharomyces_cerevisiae.R64-1-1.75.gtf','Saccharomyces_cerevisiae','exon_skipping_selector', 'tab_content.select');
		
		this.select('Overview');
		
	}
	
	select(description){
		this.current = description;
		this.alt = true;
		document.getElementById('plot_container').innerHTML = "<div id='es_title' style='margin:30px; margin-left:0; color:#444444; font-size:24px; font-family:courier;'>"+description+"</div>";
		if (description=='Overview'){
			document.getElementById('plot_container').innerHTML += 'This section analyses the results of the exon-skipping calculations based on 10 files listed in the collapsible menu on the right of this page. A detailed summary of the skipping events for each file will be displayed after clicking on the respective file name. ';
		}else{
			var container = document.getElementById('plot_container');
			container.innerHTML += `
						<div style='font-size:18px;'>Plot</div>
						<div style='width:100%; height:2px; background-color:#666666;'></div>
			`;
			var plt = this;
			var plot;
			nice.load_script('res/js/plots/'+description + '.exons.js', function(){		
				container.innerHTML += "<div style='margin-top:30px; white-space: nowrap; text-align:center;'><div id='plot' style='display: inline-block; vertical-align: top;'></div><div id='plot_toggle' style='display: inline-block; vertical-align: top; position:relative; top:10px; left:-65px;'></div></div><div id='table_container'></div>";
				plot = nice.plot('plot',plot_data);
				plot.line_chart(700, 400, 'Genes','Skipped Exons', function(){
					var c = 1;
					d3.select('svg').selectAll('text').each(function(d,i){
						var d = d3.select(this);
						if (d.attr('group')!=null && d.attr('group').substring(0,7) == 'ranked_'){
							console.log('fu');
							if(c>=10){
								d.node().innerHTML = (c)+'&nbsp;'+d.node().innerHTML+'&nbsp;&nbsp;&nbsp;';
							}else{
								d.node().innerHTML = (c)+'&nbsp;'+d.node().innerHTML+'&nbsp;&nbsp;';
							}
							d.attr('fill', '#666666');
							c+=1;

						}
					});
					plt.toggle_ranks();
				});
				nice.toggle('plot_toggle','Exons','Bases', 'tab_content.toggle_plot');
				document.getElementById('table_container').innerHTML = `
							<div style='margin-top:30px; font-size:18px;'>Ranking Table</div>
							<div style='width:100%; height:2px; background-color:#666666;'></div>
							<div style='margin-top:30px; font-size:16px;'>
								<div style='width:50%; float:left; text-align:center;'>Exons
									<div id='ex_btn' onclick='tab_content.click_ranks(event,"exon")' style='margin-top:10px; border-radius:10px; font-size:12px; line-height:16px; background-color:#eeeeee; text-anchor:top; width:80%; position:relative; left:10%; color:#aaaaaa; cursor:pointer;'>Show in plot</div>
								</div>
								<div style='width:50%; float:left; text-align:center;'>Bases
									<div id='ba_btn' onclick='tab_content.click_ranks(event,"base")' style='margin-top:10px; border-radius:10px; font-size:12px; line-height:16px; background-color:#eeeeee; text-anchor:top; width:80%; position:relative; left:10%; color:#aaaaaa; cursor:pointer;'>Show in plot</div>
								</div>
							</div>
				`;
				
				
				
			});
			
			
		}
	}
	click_ranks(evt,categ){
		var target = event.target || event.srcElement;
		if (target.innerHTML=='Show in plot'){
			if (categ=='exon'){
				if (this.alt==false){
					document.getElementById('plot_toggle_switch').checked = false;
					this.toggle_plot(true);
				}else{
					this.toggle_ranks();	
				}
			}else{
				if (this.alt==true){
					document.getElementById('plot_toggle_switch').checked = true;
					this.toggle_plot(true);
				}else{
					this.toggle_ranks();
				}
				
			}
			document.getElementById('ex_btn').innerHTML = 'Show in plot';
			document.getElementById('ba_btn').innerHTML = 'Show in plot';
			target.innerHTML='Hide in plot';
			
		}else{
			target.innerHTML='Show in plot';
			this.toggle_ranks();
		}
		
	}
	
	toggle_plot(show_ranks=false){
		document.getElementById('ex_btn').innerHTML = 'Show in plot';
		document.getElementById('ba_btn').innerHTML = 'Show in plot';
		var plot;
		var plt = this;
		if(this.alt){
			nice.load_script('res/js/plots/'+this.current + '.bases.js', function(){
			
				plot = nice.plot('plot',plot_data);
				plot.line_chart(700, 400, 'Genes','Skipped Bases', function(){
					var c = 1;
					d3.select('svg').selectAll('text').each(function(d,i){
						var d = d3.select(this);
						if (d.attr('group')!=null && d.attr('group').substring(0,7) == 'ranked_'){
							console.log('fu');
							if(c>=10){
								d.node().innerHTML = (c)+'&nbsp;'+d.node().innerHTML+'&nbsp;&nbsp;&nbsp;';
							}else{
								d.node().innerHTML = (c)+'&nbsp;'+d.node().innerHTML+'&nbsp;&nbsp;';
							}
							d.attr('fill', '#666666');
							c+=1;

						}
					});
					if (!show_ranks){
						plt.toggle_ranks();
					}
					
				});
			
			});
			
		}else{
			nice.load_script('res/js/plots/'+this.current + '.exons.js', function(){
			
				plot = nice.plot('plot',plot_data);
				plot.line_chart(700, 400, 'Genes','Skipped Exons', function(){
					var c = 1;
					d3.select('svg').selectAll('text').each(function(d,i){
						var d = d3.select(this);
						if (d.attr('group')!=null && d.attr('group').substring(0,7) == 'ranked_'){
							console.log('fu');
							if(c>=10){
								d.node().innerHTML = (c)+'&nbsp;'+d.node().innerHTML+'&nbsp;&nbsp;&nbsp;';
							}else{
								d.node().innerHTML = (c)+'&nbsp;'+d.node().innerHTML+'&nbsp;&nbsp;';
							}
							d.attr('fill', '#666666');
							c+=1;

						}
					});
					if (!show_ranks){
						plt.toggle_ranks();
					}
				});
			
			});
		}
		
		
		this.alt = !this.alt;
	}
	
	toggle_ranks(){
		var c = 1;
		d3.select('svg').selectAll('text').each(function(d,i){
			var d = d3.select(this);
			if (d.attr('group')!=null && d.attr('group').substring(0,7) == 'ranked_'){
				if (d.style('display')=='none'){
					d.style('display','block');
				}else{
					d.style('display','none');
				}
			}
							
		});
	}
	
	toggle_right_menu(){
		var w = Math.max(document.documentElement.clientWidth,800);
		if (this.right_toggle){
			document.getElementById('plot_container').style.width = (w - 24 - 30 - 30) + "px";
			document.getElementById('exon_skipping_arrow').style.left = (w - 24) + "px";
			document.getElementById('exon_skipping_arrow').style.transform = 'scale(-1, 1)';
		}else{
			document.getElementById('plot_container').style.width = (w - 24 - 200 - 12 - 30 - 30) + "px";
			document.getElementById('exon_skipping_arrow').style.left = (12 + w - 24 - 200 - 24) + "px";
			document.getElementById('exon_skipping_arrow').style.transform = 'scale(1, 1)';
		}
		this.right_toggle = !this.right_toggle;
		
	}
	
	
	onresizeWindow(event){
		var plot_container = document.getElementById('plot_container');
		var w = Math.max(document.documentElement.clientWidth,800);
		plot_container.style.height = (document.documentElement.clientHeight - 42 - 2) + "px";
		document.getElementById('exon_skipping_selector').style.left = (12 + w - 24 - 200 - 24 - 12) + "px";
		if (this.right_toggle){
			plot_container.style.width = (w - 24 - 200 - 12 - 30 - 30) + "px";
			document.getElementById('exon_skipping_arrow').style.left = (12 + w - 24 - 200 - 24) + "px";
		}else{
			plot_container.style.width = (w - 24 - 30 - 30) + "px";
			document.getElementById('exon_skipping_arrow').style.left = (w - 24) + "px";
		}
		
	}
	
}




