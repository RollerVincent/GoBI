#!/usr/bin/env python3

import sys
from xml.etree import ElementTree


def each(node, depth):
    if node is not None:

        if node.tag not in colormap.keys():
            colormap.update({node.tag : colors[(len(colormap) % len(colors))]})

        c = ''
        if len(node.getchildren())!=0:
            c = '''<div style=" position:relative; top:-5px; float:left; width:2px; height:24px; background-color:#666666;"></div>'''

        # --------------------------------------------------------------------- BEFORE CHILDREN

        print('''<div onmouseout = "changebkgcolor(event,'#ededed')" onmouseover = "changebkgcolor(event,'#fafafa')" style=" background-color:#ededed; border: solid 1px #666666; box-shadow: 0px 0px 2px #6a6a6a; color:#666666; font-size:10px; line-height:12px; font-family:monospace; float:left; margin-top:4px; margin-bottom:4px; margin-right:2px; margin-left:2px;">
                    
                    
                    <div class = "colored" style="height:3px; background-color:#'''+colormap[node.tag]+''';"></div>

                    <div style="padding-left:2px; padding-right:2px;">

        ''')


        # ---------------------------------------------------------------------


        for item in node.getchildren():
            each(item, depth+1)

        # --------------------------------------------------------------------- AFTER CHILDREN

        print('''   </div>             
                 </div>
        ''')

        # ---------------------------------------------------------------------

    else:
        return 0


def main(args):
    root = ElementTree.parse(args[1]).getroot()
    each(root, 0)


if __name__ == "__main__":
    colors = ['eeeecc','eeaaaa','adcfe2','d3e2a3','d3d3f6','aad5ff']
    colormap = {}

    print('''
    
    <script>
        function changebkgcolor(evt,color){
            

            if (event.target.className != 'colored'){
                event.target.style.backgroundColor = color;
            }
        }
    </script>

    
    ''')

    main(sys.argv)
