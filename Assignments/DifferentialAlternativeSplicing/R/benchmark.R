benchmark <- function(dxs,lrs,skipped,o){
	out <- read.csv(text="gene,range,p_dex,p_lrs,flag")
	for(i in 1:nrow(lrs[1])){
		gene <- lrs[i,1]
		dx_entries <- dxs[dxs$groupID == gene, ]
		ex <- lrs[i,]$exon
		
		labels <- skipped[skipped$gene_id == gene]
	
		if(nrow(dx_entries)!=0){
			for (j in 1:nrow(dx_entries)){
				r <- ranges(dx_entries[j,]$genomicData)
				sr <- paste(c(start(r),end(r)+1),collapse="-")
				if(sr==ex){
					entry <- dx_entries[j,]
					hit <- length(ranges(labels[ranges(labels) == r]))
					positive <- F
					if(hit>0){
						positive <- T
					}
					
					df <- data.frame(gene, ex, entry$padj, lrs[i,]$padj, positive)
					names(df) <- c("gene","range","p_dex","p_lrs","flag")
					out <- rbind(out,df)				
				}
			}
		}
	}
	write.table(out, file=o, quote=FALSE, sep='\t', row.names = FALSE)
}

groups <- c(1,1,1,1,1,0,0,0,0,0)
psi <- c(	
			'data/psi/sample1.psi',
			'data/psi/sample2.psi',
			'data/psi/sample3.psi',
			'data/psi/sample4.psi',
			'data/psi/sample5.psi',
			'data/psi/sample6.psi',
			'data/psi/sample7.psi',
			'data/psi/sample8.psi',
			'data/psi/sample9.psi',
			'data/psi/sample10.psi')

source("LRS.R")
lrs <- diff.splicing(psi,groups)

source("dexseq_script.R")
dex <- dxr1

load("differential_exons.RData")
skp <- differential.skipped

benchmark(dex,lrs,skp,"roc.tsv")