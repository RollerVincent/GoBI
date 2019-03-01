package dag;

import parser.Parser;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Mapping {



    public static HashMap<String,List<String>> construct(String type, String path, DAG dag){
        if(type.equals("go")){
            return goMapper(path, dag);
        }else if(type.equals("ensembl")){
            return ensemblMapper(path, dag);
        }
        return null;
    }

    static HashMap<String,List<String>> goMapper(String path, DAG dag){
        HashMap<String,List<String>> out = new HashMap<>();
        BufferedReader reader = Parser.GzipReader(path);
        try {
            String line = reader.readLine();
            while(line.startsWith("!")){
                line = reader.readLine();
            }
            List<String> nodes = new ArrayList<>();
            String[] split;
            String n;
            String gene="";
            while(line!=null){
                split = line.split("\t");
                if(split[3].equals("")) {
                    if(!split[2].equals(gene)){
                        if(!gene.equals("") && nodes.size()!=0){
                            out.put(gene,nodes);
                        }
                        gene = split[2];
                        nodes = new ArrayList<>();

                    }
                    n = split[4].substring(3);
                    if(!nodes.contains(n)){
                        if(dag.map.get(n)!=null) {
                            nodes.add(n);
                        }
                    }
                }
                line = reader.readLine();
            }
            if(nodes.size()!=0){
                out.put(gene,nodes);
            }
            reader.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return out;
    }



    static HashMap<String,List<String>> ensemblMapper(String path, DAG dag){
        HashMap<String,List<String>> out = new HashMap<>();
        BufferedReader reader = Parser.Reader(path);
        try {
            reader.readLine();
            List<String> nodes;
            String line;
            String[] split;
            String[] gos;
            String n;
            while((line=reader.readLine())!=null){
                split = line.split("\t");
                gos = split[2].split("\\|");
                nodes = new ArrayList<>();
                for(String g:gos){
                    n=g.substring(3);
                    if(dag.map.get(n)!=null) {
                        nodes.add(g.substring(3));
                    }
                }
                if(nodes.size()!=0) {
                    if (split[1].equals("")) {
                        split[1] = split[0];
                    }
                    out.put(split[1], nodes);
                }
            }
            reader.close();
        }catch(Exception e){
            e.printStackTrace();
        }
        return out;

    }

    public static void checkSorting(HashMap<String,List<String>> mapping){
        for (String key:mapping.keySet()) {
            int x = 0;
            for (String go:mapping.get(key)) {
                int y = Integer.parseInt(go);
                if (y<=x){
                    System.out.println("Verification of mapping failed:  GO classes not sorted in ascending order.");
                    return;
                }
                x=y;
            }
        }
        System.out.println("Successfully checked sorting of mapped GO classes.");
    }

    public static void asString(HashMap<String,List<String>> mapping){
        for (String key:mapping.keySet()) {
            System.out.println(key+"\t\t"+mapping.get(key).size()+" : "+mapping.get(key));
        }

    }
}
