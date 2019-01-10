package parser;

import gtf.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Parser {


    public static void drawRegionVectors(List<RegionVector> regionVectors, List<String> strands, List<String> colors,String path, int width){
        HashMap<Integer,String> colormap = new HashMap<>();
        for (int i = 0; i < colors.size(); i++) {
            colormap.put(i,colors.get(i));
        }
        int min = Integer.MAX_VALUE;
        int max = Integer.MIN_VALUE;
        for (RegionVector rv :regionVectors){
            Region r = rv.toRegion();
            if(r.start<min){
                min=r.start;
            }
            if(r.end>max){
                max=r.end;
            }
        }
        int dif = max-min+1;
        int id=0;
        BufferedWriter writer = Writer(path);
        try {
            writer.write("<html><body><div style='text-align:center;'><div style='display:inline-block; margin-top:20px; max-width:95vw;  border-radius:4px; box-shadow: inset 0px 0px 5px #bbbbbb; padding:10px;  background-color:#f4f4f4;'><div id='strand_container' style='float:left; color:#aaaaaa; font-weight:bolder; font-family:monospace; width:20px; padding-top:10px;'></div><div id='container' style='overflow:auto; padding-bottom:20px; padding-top:10px; white-space:nowrap; background-color:#f4f4f4;'></div>\n");
            writer.write(
                    "<script>\n" +
                            "    var w = "+width+";\n" +
                            "    var step = w/"+dif+".0;\n" +

                            "    function addDiv(id){\n" +
                            "        document.getElementById('container').innerHTML+='<div id=\"'+id+'\" style=\"text-align:left; margin-top:2px; margin-bottom:2px; height:10px;\"></div>';\n" +
                            "    }\n" +
                            "    function addStrand(strand){\n" +
                            "        document.getElementById('strand_container').innerHTML+='<div style=\"margin-top:2px; margin-bottom:2px; height:10px; line-height:10px;\">'+strand+'</div>';\n" +
                            "    }\n" +
                            "    function addRegion(id,l,c,h){\n" +
                            "        document.getElementById(id).innerHTML+='<div style=\"display:inline-block; width:'+(l*step)+'px; height:'+h+'px; position:relative; bottom:'+((10-h)/2)+'px; background-color:'+c+';\"></div>';\n" +
                            "    }\n"

            );
            for(String s:strands){
                writer.write("    addStrand('"+s+"');\n");
            }

            for (RegionVector rv :regionVectors){
                writer.write("    addDiv("+id+");\n");
                writer.write("    addRegion("+id+","+(rv.toRegion().start-min)+",'#f4f4f4',10);\n");
                for (int i = 0; i < rv.length()-1; i++) {
                    Region r = rv.get(i);
                    Region n = rv.get(i+1);
                    writer.write("    addRegion("+id+","+(r.end-r.start+1)+",'"+colormap.get(id)+"',10);\n");
                    writer.write("    addRegion("+id+","+((n.start-1)-(r.end+1)+1)+",'"+colormap.get(id)+"',2);\n");
                }
                Region r = rv.get(rv.length()-1);
                writer.write("    addRegion("+id+","+(r.end-r.start+1)+",'"+colormap.get(id)+"',10);\n");
                id+=1;
            }
            writer.write("</script>\n</div></div></body></html>");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




    public static ArgParser ArgParser(String[] args){
        ArgParser a = new ArgParser();
        a.args=args;
        return a;
    }
    public static class ArgParser {
        String[] args;
        HashMap<String,boolean[]> ArgDescriptions = new HashMap<String,boolean[]>();
        HashMap<String,String> Values = new HashMap<String,String>();
        HashMap<String,String> Info = new HashMap<String,String>();

        public void addOption(String flag,boolean hasValue, boolean optional, String info){
            addOption(flag,hasValue,optional);
            Info.put(flag,info);
        }

        public void addOption(String flag,boolean hasValue, boolean optional){
            ArgDescriptions.put(flag,new boolean[]{optional,hasValue});
        }

        public boolean Compile(){
            for (int i = 0; i < args.length-1; i++) {
                if (ArgDescriptions.containsKey(args[i])){
                    if(ArgDescriptions.get(args[i])[1]){
                        Values.put(args[i],args[i+1]);
                    }else{
                        Values.put(args[i],"");
                    }
                }
            }
            for(HashMap.Entry<String, boolean[]> entry : ArgDescriptions.entrySet()) {
                if(!entry.getValue()[0] && !Values.containsKey(entry.getKey())){
                    System.out.println(toString());
                    return false;
                }
            }
            return true;
        }

        public String getArgument(String flag){
            if(ArgDescriptions.containsKey(flag)) {
                boolean[] v = ArgDescriptions.get(flag);
                if (v[1]) {
                    return Values.get(flag);
                }
            }return toString();
        }

        public boolean hasArgument(String flag){
            if(Values.containsKey(flag)) {
                return true;
            }
            return false;
        }

        @Override
        public String toString() {
            String out="usage:\n";
            for(HashMap.Entry<String, boolean[]> entry : ArgDescriptions.entrySet()) {
                String k = entry.getKey();
                boolean[] v = entry.getValue();

                if (v[1]) {
                    out += "\t\trequired";
                } else {
                    out += "\t\toptional";
                }

                out+="\t"+k;

                String i = Info.get(k);


                if(i!=null) {
                    out += " ("+i+")";
                }
                out+="\n";

            }
            return out;
        }
    }


    public static void StringToFile(String string,String path){
        try {

            BufferedWriter writer = Writer(path);
            writer.write(string);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static BufferedWriter Writer(String path){
        try {
            String[] s = path.split("/");
            String f = s[s.length-1];
            if(s.length>1) {
                File file = new File(path.substring(0, path.length() - 1 - f.length()));
                file.mkdirs();
            }
            return new BufferedWriter(new FileWriter(path));


        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }



}
