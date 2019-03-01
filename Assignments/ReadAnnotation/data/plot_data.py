
labels = {}
with open("labels.txt") as f:
    for l in f:
        l = l.strip().split("\t")
        labels.update({l[0]:l[1]})


data = {} # {METHOD: [[GENE.ID], [log2FC], [RAW.PVAL], [ADJ.PVAL]]}			
def load(path):
    with open(path) as f:
        d = [[], [], [], []]
        p = [[], [], [], []]
        f.readline()
        for l in f:
            l = l.strip().split("\t")
            if l[2] != "NA":
                d[0].append(l[0])
                d[1].append(float(l[1]))
                d[2].append(float(l[2]))
                d[3].append(float(l[3]))
            else:
                p[0].append(l[0])
                p[1].append(float(l[1]))
                p[2].append(0)
                p[3].append(0)

        d[0] += p[0]
        d[1] += p[1]
        d[2] += p[2]
        d[3] += p[3]

        data[path[:-4]] = d
    

methods = ["DESeq","edgeR","limma"]

load("DESeq.out")
load("edgeR.out")
load("limma.out")



def a():
    bcount = 40
    with open("a.js", "w") as f:
        stats = open("stats_a.js","w")
        s = "var plot_data = [\n"
        stats.write(s)
        for m in methods:
            d = data[m]
            l = len(d[0])
            freq = 0
            out=[[0,0]]
            currentpv = 0.0
            st =1
            for i in range(l):
                j = l-i-1
                if d[0][j] in labels.keys() and labels[d[0][j]] == "false":
                    if d[2][j] < 1.0/bcount*st:
                        freq+=1
                    else:
                       #out.append([currentpv,freq])
                        out.append([currentpv+1.0/bcount, freq])
                        currentpv += 1.0/bcount
                        #freq=0
                        st+=1

           # out.append([1.0-1.0/bcount, freq])
            out.append([1.0, freq])

            for o in out:
                f.write(s)
                o[1] = 1.0*o[1]/freq
                s = '{"x":'+str(o[0])+',"y":'+str(o[1])+',"group":"'+m+'"},\n'

            stats.write('{"x":'+str(out[int(bcount/20)][0])+',"y":'+str(out[int(bcount/20)][1])+',"group":"'+m+'"}')
            if m == "limma":
                stats.write("];")
            else:
                stats.write(",\n")

        f.write(s[:-2] + "];")
        stats.close()

    with open("uniform_a.js", "w") as f:
        f.write("var plot_data = [\n")
        f.write('{"x":' + str(0) + ',"y":' + str(0) + ',"group":"uniform"},\n')

        for i in range(bcount-1):
            f.write('{"x":' + str(1.0/bcount*(i+1)) + ',"y":' + str(1.0/bcount*(i+1)) + ',"group":"uniform"},\n')

        f.write('{"x":' + str(1.0 / bcount * (bcount)) + ',"y":' + str(1.0 / bcount * (bcount)) + ',"group":"uniform"}];')

    with open("tresh_a.js", "w") as f:
        f.write("var plot_data = [\n")
        f.write('{"x":0.05,"y":0,"group":"alpha"},\n')
        f.write('{"x":0.05,"y":1,"group":"alpha"}];')

def b():
    step = 0.01
    out = []
    for m in methods:
        d = data[m]
        alpha = 0
        while alpha <= 1+step:
            TP, FP, TN, FN = 0, 0, 0, 0
            for i in range(len(d[0])):
                gene, pvalue = d[0][i], d[3][i]
                if pvalue <= alpha:
                    if gene in labels.keys() and labels[gene] == 'true': TP += 1
                    else: FP += 1
                else:
                    if gene in labels.keys() and labels[gene] == 'true': FN += 1
                    else: TN += 1
            out.append([TP/(TP+FN), 1-(TN/(TN+FP)), m])
            alpha += step
    
    s = "plot_data = [\n"
    for o in out:
        s += "{'x':"+str(o[1])+",'y':"+str(o[0])+",'group':'"+str(o[2])+"'},\n"
    s = s[:-2]+'];'
    
    with open('b.js', 'w') as f:
        f.write(s);
        
    lastGroup = ''
    lastX = 0
    lastY = 0    
    
    with open('auroc_b.js', 'w') as f:
        f.write("plot_data = [\n")
        auroc = 0
        for o in out:
            if o[2] != lastGroup:
                if lastGroup != '': f.write("{'x':"+str(auroc)+",'y':"+str(0)+",'group':'"+lastGroup+"'},\n")
                lastGroup = o[2]
                lastX = o[1]
                lastY = o[0]
                auroc = 0
            else:
                auroc += (o[1] - lastX) * lastY
                lastX = o[1]
                lastY = o[0]
        f.write("{'x':"+str(auroc)+",'y':"+str(0)+",'group':'"+lastGroup+"'}];")
    
    
def rates(value, method): # [[TP, FP, TN, FN] for gene in data]
    out = []
    d = data[method]
    TP, FP, TN, FN = 0, 0, 0, 0
    v = 3
    if(value == 'pvalue'):
        v = 2
    l = len(d[0])
    for i in range(l):
        gene = d[0][i]
        if gene in labels.keys() and labels[gene] == 'true': 
            FN += 1
        else:
            TN += 1
  #  out.append([TP, FP, TN, FN])
    for i in range(l):
        j = l-i-1
        gene, value = d[0][j], d[v][j]
        if gene in labels.keys() and labels[gene] == 'true': 
            TP += 1
            FN -= 1
        else:
            FP += 1
            TN -= 1
        out.append([TP, FP, TN, FN])
    return out
    
    
    
def c():
    
    out = []
    for m in methods:
        d = data[m]
        l = len(d[0])
        for i,rate in enumerate(rates('fdr', m)):
            
            TP, FP, TN, FN = rate
            j = l-i-1
            
            
            fdr =  FP/(FP+TP)
            out.append([d[3][j], fdr, m])
    
    
    
    s = "plot_data = [\n"
    for o in out:
        s += "{'x':"+str(o[0])+",'y':"+str(o[1])+",'group':'"+str(o[2])+"'},\n"
    s = s[:-2]+'];'
    
    with open('c.js', 'w') as f:
        f.write(s);
            
            
        
        
    
c()

