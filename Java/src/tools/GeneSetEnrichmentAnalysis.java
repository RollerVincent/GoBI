package tools;

import dag.DAG;
import dag.Mapping;
import dag.Node;
import dag.Overlap;
import htsjdk.samtools.util.Tuple;
import parser.OboParser;

import java.util.HashMap;
import java.util.List;

public class GeneSetEnrichmentAnalysis {

    public DAG dag;
    public HashMap<String,List<String>> mapping;
    public HashMap<String, Overlap> overlaps;

    public void loadDag(String path, String namespace){
        OboParser obo = new OboParser(path);
        this.dag = obo.construct(namespace);
        System.out.println("Loaded DAG structure for namespace: "+namespace+" , with "+dag.map.size()+" nodes and root: GO:"+dag.root.id+" ("+dag.root.name+")");
    }

    public void initMapping(String type, String path){
        mapping = Mapping.construct(type, path, dag);
        System.out.println("Initialized mapping with "+mapping.size()+" mapped genes");
    }

    public void initDag(){

        System.out.println("Initialising DAG:");


        // based on mapping: add genes to dag nodes, calculate non related overlaps
        overlaps = new HashMap<>();
        List<String> nodes;
        int l;
        Node a;
        Node b;
        String h;
        Overlap ov;
        for (String gene:mapping.keySet()){
            nodes = mapping.get(gene);
            l=nodes.size();
            for (int i = 0; i < l; i++) {
                a = dag.map.get(nodes.get(i));
                a.genes.add(gene);
                for (int j = i+1; j < l; j++) {
                    b = dag.map.get(nodes.get(j));
                    h = a.id+":"+b.id;
                    ov = overlaps.get(h);
                    if(ov==null){
                       // System.out.println(h);
                        ov = new Overlap(false);
                        overlaps.put(h,ov);
                    }
                    ov.increment();
                }
            }
        }
        System.out.println("\tAdded genes to nodes and generated "+overlaps.size()+" non-related overlaps");
        // ---------------------------------------------------------------------------------


        // set dag maxDepths values and find leaves
        dag.setMaxDepths();
        // ---------------------------------------------------------------------------------



        // upward propagation of genes and calculation of related overlaps

        dag.upwardPropagation(overlaps);
        System.out.println(overlaps.size());

    }


}
