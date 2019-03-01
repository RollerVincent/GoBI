package dag;

import htsjdk.samtools.util.Tuple;
import parser.Parser;

import java.io.BufferedReader;
import java.util.HashMap;

public class EnrichmentData {

    public BufferedReader reader;
    public HashMap<String,Tuple<Double,Boolean>> map = new HashMap<>();


    public EnrichmentData(String path){
        reader = Parser.Reader(path);
    }

    public void construct(DAG dag){
        String line;
        String[] split;
        Node node;
        try {
            line = reader.readLine();
            while(line.charAt(0) == '#'){
                node = dag.map.get(line.substring(1,11));
                node.label = true;
                line = reader.readLine();
            }
            while ((line = reader.readLine()) != null) {
                split = line.split("\t");
                map.put(split[0], new Tuple<>(Double.parseDouble(split[1]), Boolean.parseBoolean(split[2])));
                System.out.println(map.get(split[0]));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}
