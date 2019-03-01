data = []
with open("R/roc.tsv") as f:
    f.readline()
    for l in f:
        l = l.split('\t')
        if(l[2] != 'NA'):
            data.append([float(l[2]), float(l[3]), True if l[4].strip() == 'TRUE' else False]) # [dexseq, lrs, label]
            
def datapoint(alpha):
    dex, lrs = [0,0,0,0], [0,0,0,0] # [TP, FP, TN, FN]
    for d in data:
        if d[0] <= alpha:
            if d[2]: dex[0] += 1
            else: dex[1] += 1
        else:
            if d[2]: dex[3] += 1 
            else: dex[2] += 1
        
        if d[1] <= alpha:
            if d[2]: lrs[0] += 1
            else: lrs[1] += 1
        else:
            if d[2]: lrs[3] += 1
            else: lrs[2] += 1
            
    out = [[dex[0]/(dex[0]+dex[3]), 1-(dex[2]/(dex[2]+dex[1]))], [lrs[0]/(lrs[0]+lrs[3]), 1-(lrs[2]/(lrs[2]+lrs[1]))]] # [dex[TPR, FPR], lrs[TPR, FPR]]
    return out
   
def plot_data(step):
    alpha = 0
    dex, lrs = [], []   
    while alpha <= 1+step:
        dp = datapoint(alpha)
        dex.append(dp[0])
        lrs.append(dp[1])
        alpha += step
    out = "plot_data = [\n"
    for d in dex:
        out += "{'x':"+str(d[1])+",'y':"+str(d[0])+",'group':'dex'},\n"
    for d in lrs:
        out += "{'x':"+str(d[1])+",'y':"+str(d[0])+",'group':'lrs'},\n"
    out = out[:-2]+"];"
    return out
    
with open("html/roc.js", 'w') as f:
    f.write(plot_data(0.01))
    