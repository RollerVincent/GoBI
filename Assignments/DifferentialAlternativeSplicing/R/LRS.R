LRS <- function(incl, total, groups){

	incl_1 <- 0
	incl_2 <- 0
	total_1 <- 0
	total_2 <- 0
	for (i in c(1:length(groups))){
		g <- groups[i]
		if(g==1){
			incl_1 <- incl_1 + incl[i]
			total_1 <- total_1 + total[i]
		}else{
			incl_2 <- incl_2 + incl[i]
			total_2 <- total_2 + total[i]
		}
		
	}

	p1 <- incl_1/total_1
	p2 <- incl_2/total_2
	p0 <- (incl_1+incl_2)/(total_1+total_2)

	Lr <- 1
	Lf <- 1
	for (i in c(1:length(groups))){
		g <- groups[i]
		t <- total[i]
		inc <- incl[i]
		pf <- 0
		if(g==1){
			pf <- p1
		}else{
			pf <- p2
		}
		Lr <- Lr * dbinom(inc,t,p0)
		Lf <- Lf * dbinom(inc,t,pf)
	}
	lrs <- -2*log(Lr/Lf)	
	pv <- pchisq(lrs, df=1, lower.tail=FALSE)
	
	out <- data.frame(p0,p1,p2,log(Lr),log(Lf),lrs,pv)
	names(out) <- c("p0","p1","p2","llreduced","llfull","lrs","pvalue")
	
	return (out)
}

diff.splicing <- function(psi.files, group){
	
	proceed <- function(d,k){
		l <- (length(d)-2)/4
		total <- vector(length=l)
		ic <- vector(length=l)
		for(j in c(1:l-1)){
			if(is.na(d[k,5+4*j])){
				total[j+1] <- 0
			}else{
				total[j+1] <- d[k,5+4*j]
			}
			if(is.na(d[k,3+4*j])){
				ic[j+1] <- 0
			}else{
				ic[j+1] <- d[k,3+4*j]
			}
		}
		return (LRS(ic,total,group))
	}
	
	data <- NULL
	for(i in c(1:length(psi.files))){
		tmp <- read.table(file = psi.files[i], sep = '\t', header = TRUE)
		names(tmp) <- c('gene','exon',paste('incl_',i, collapse=''),paste('excl_',i, collapse=''),paste('total_',i, collapse=''),paste('psi_',i, collapse=''))
		if(is.null(data)){
			data <- tmp
		}else{
			data <- merge(data,tmp,by=c("gene","exon"), all=T)
		}
		
	}	
	
	out <- read.csv(text="gene,exon,p0,p1,p2,llreduced,llfull,lrs,pvalue,padj")
	for(i in c(1:nrow(data))){
		e <- proceed(data,i)
		e <- data.frame(data[i,1],data[i,2],e[1],e[2],e[3],e[4],e[5],e[6],e[7],0)
		names(e) <- c("gene","exon","p0","p1","p2","llreduced","llfull","lrs","pvalue","padj")
		out <- rbind(out,e)
	}
	
	pvs <- as.vector(out$pvalue)
	adj <- p.adjust(pvs, method = 'fdr')
	out['padj'] <- adj
		
	return(out)
}



			











