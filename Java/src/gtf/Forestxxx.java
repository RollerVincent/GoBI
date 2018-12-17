package gtf;

import augmentedTree.IntervalTree;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Forestxxx {

    private HashMap<String, IntervalTree<Gene>[]> content = new HashMap<>();

    public void add(Gene g){

        IntervalTree[] its = content.get(g.chromosome);
        if(its==null){
            IntervalTree<Gene>[] ts = new IntervalTree[]{new IntervalTree(),new IntervalTree()};
            if(g.strand.charAt(0)=='+'){
                ts[0].add(g);
            }else{
                ts[1].add(g);
            }
            content.put(g.chromosome,ts);
        }else{
            if(g.strand.charAt(0)=='+'){
                its[0].add(g);
            }else{
                its[1].add(g);
            }
        }

    }

    public List<Gene> getIntersecting(String chr, boolean positiveStrand, int start, int end){

        int i;
        if(positiveStrand){
            i=0;
        }else{
            i=1;
        }

        List<Gene> out = new ArrayList<>();
        content.get(chr)[i].getIntervalsIntersecting(start,end,out);


        return out;
    }


}
