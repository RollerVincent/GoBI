package tools;

import gtf.Gene;
import gtf.Region;
import htsjdk.samtools.util.Tuple;
import parser.GtfParser;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DifferentialAlternativeSplicing {

    HashMap<String, Tuple<String,List<Tuple<Region, String>>[]>> Data = new HashMap<>();
    HashMap<String, Integer[]> Counts = new HashMap<>();

    public DifferentialAlternativeSplicing(String gtf_path){

        ExonSkipping skipping = new ExonSkipping();
        GtfParser gtf = new GtfParser(gtf_path);

        for (Gene gene : gtf) {
            for (ExonSkipping.RawSkip rs : skipping.getRawSkips(gene)) {

                String hinc = rs.WT_transcript;
                String hexc = rs.SV_transcript;

                if (!Data.containsKey(hinc)) {
                    Data.put(hinc, new Tuple<>(hexc,new ArrayList[]{new ArrayList<>(), new ArrayList<>()}));
                }
                if (!Data.containsKey(hexc)) {
                    Data.put(hexc, new Tuple<>(hinc,new ArrayList[]{new ArrayList<>(), new ArrayList<>()}));
                }

                List<Tuple<Region, String>> dinc = Data.get(hinc).b[0];
                List<Tuple<Region, String>> dexc = Data.get(hexc).b[1];

                Tuple<Region, String> t;
                String s;
                for (Region r : rs.introns.inverse()) {
                    s=gene.id+"\t"+r.start+"-"+(r.end+1);
                    Counts.put(s,new Integer[]{0,0});
                    t = new Tuple<>(r, s);
                    dinc.add(t);
                    dexc.add(t);
                }
            }
        }
    }

    public void updateCount(String key, int value){
        if (value==1){
            Counts.get(key)[0] += 1;
        }else if(value==-1){
            Counts.get(key)[1] += 1;
        }
    }

    public Tuple<String,List<Tuple<Region, String>>[]> getData(String s){
        return Data.get(s);
    }

    public void write(BufferedWriter wr){
        try {
            wr.write("gene\texon\tnum_incl_reads\tnum_excl_reads\tnum_total_reads\tpsi\n");
            Integer[] v;
            for (String k : Counts.keySet()) {
                v = Counts.get(k);
                if(v[0]>0 || v[1]>0) {
                    wr.write(k + "\t" + v[0] + "\t" + v[1] + "\t" + (v[0] + v[1]) + "\t" + (1.0*v[0] / (v[0] + v[1])) + "\n");
                }
            }
            wr.close();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

}
