const Exon_Skipping = class {

	constructor(){
		this.container = document.getElementById('main_container');	
	}
	
	load(){
		
		this.container.innerHTML = "<div style='width:100vw;'>"+
				"<div id ='exon_skipping_selector' style='padding:6px; padding-left:34px;  position:absolute; left:10vw; top:10px; border-radius:10px; width:196px; background-color:#eeeeee; color:#aaaaaa; font-size:12px; line-height:25px; font-family:courier;'></div>"+
				"<div id ='plot_container' class='margeable' style='box-shadow:inset 0 0 4px #aaaaaa; padding-left:30px; padding-right:30px; overflow:scroll;  position:absolute; top:10px; left:12px; width:80vw; border-radius:10px; color:#444444; background-color:#fefefe; font-size:12px; font-family:courier;'></div>"+
			"</div>";	
			
		var w = Math.max(document.documentElement.clientWidth,700);
		
		document.getElementById('plot_container').style.height = (document.documentElement.clientHeight - 42 - 2) + "px";
		document.getElementById('plot_container').style.width = (w - 24 - 200 - 12 - 30 - 30) + "px";
		document.getElementById('exon_skipping_selector').style.left = (12 + w - 24 - 200 - 24 - 12) + "px";
		
		nice.new_section('Overview','exon_skipping_selector', 'tab_content.select');
		nice.new_section('Homo_sapiens_CRh37.68','exon_skipping_selector', 'tab_content.select');
		nice.new_section('Saccharomyces_cerevisiae','exon_skipping_selector', 'tab_content.select');
		nice.new_section('gencode.v10.annotation','exon_skipping_selector', 'tab_content.select');
		
		this.select('Overview');
	}
	
	select(description){
		document.getElementById('plot_container').innerHTML = "<div id='es_title' style='margin:30px; margin-left:0; color:#444444; font-size:24px; font-family:courier;'>"+description+"</div>";
		if (description=='Overview'){
			document.getElementById('plot_container').innerHTML += 'This section analyses the results of the exon-skipping calculations based on 10 files listed on the right of this page. A detailed summary of the skipping events for each file will be displayed after clicking on the respective file name. ';
		}
	}
	
	onresizeWindow(event){
		var plot_container = document.getElementById('plot_container');
		var w = Math.max(document.documentElement.clientWidth,700);
		plot_container.style.height = (document.documentElement.clientHeight - 42 - 2) + "px";
		plot_container.style.width = (w - 24 - 200 - 12 - 30 - 30) + "px";
		document.getElementById('exon_skipping_selector').style.left = (12 + w - 24 - 200 - 24 - 12) + "px";
	}
	
}




