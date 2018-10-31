package parser;

import gtf.*;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Parser {

    public static ArgParser ArgParser(String[] args){
        ArgParser a = new ArgParser();
        a.args=args;
        return a;
    }
    public static class ArgParser {
        String[] args;
        HashMap<String,boolean[]> ArgDescriptions = new HashMap<String,boolean[]>();
        HashMap<String,String> Values = new HashMap<String,String>();

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
            String out="usage:\t";
            for(HashMap.Entry<String, boolean[]> entry : ArgDescriptions.entrySet()) {
                String k = entry.getKey();
                boolean[] v = entry.getValue();
                out+="["+k;
                if(v[1]){
                    out+=" file";
                }
                out+="] ";
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
