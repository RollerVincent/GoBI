import sys
import matplotlib.pyplot as plt
import matplotlib as mpl
import seaborn as sns



#value:group

args = sys.argv

x, y, structure = [], [], []
data = {}

with open(args[1], 'r') as f:
    for d in f:
        s = d.split('\t')
        if len(structure)==0:
            structure=s
        else:
            v = s[1].split(':')
            if v[1] not in data.keys():
                data.update({ v[1] : [[], [], []] })

            data.get(v[1])[0].append(int(s[0]))
            data.get(v[1])[1].append(int(v[0]))
            data.get(v[1])[2].append(int(v[2]))


sns.set_palette(sns.color_palette("Set2"))


textcolor = '#a0a0a0'
mpl.rcParams['font.family'] = 'Courier New'
mpl.rcParams['font.weight'] = 'bold'

mpl.rcParams['text.color'] = '#686868'
mpl.rcParams['axes.labelcolor'] = textcolor
mpl.rcParams['xtick.color'] = textcolor
mpl.rcParams['ytick.color'] = textcolor
mpl.rcParams['xtick.labelsize'] = 'small'
mpl.rcParams['ytick.labelsize'] = 'small'
mpl.rcParams['axes.labelweight'] = 'bold'
mpl.rcParams['axes.edgecolor'] = textcolor
mpl.rcParams['axes.facecolor'] = '#f2f2f2'
mpl.rcParams['axes.titleweight'] = 'bold'

plt.figure(figsize=(8,4))
plt.xlabel(structure[0])
plt.ylabel(structure[1])

plt.title(args[2])

for d in data.keys():
    v = data.get(d);
    if args[3]=='f':
        plt.fill_between(v[0], v[1])
        plt.plot(v[0], v[1], '-', alpha=0.8)
    else:
        plt.plot(v[0], v[1], args[3], alpha=0.8)


plt.show()