package augmentedTree.forest;

import augmentedTree.IntervalTree;
import gtf.Gene;
import sam.ReadPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class StrandedForest implements Forest {

    private HashMap<String, IntervalTree<Gene>[]> content = new HashMap<>();
    private List<Gene> genes;
    private int firststrand;
    private int scndstrand;
    private int firstStrandIndex;

    public StrandedForest(boolean frstrand){
        if (frstrand){
            firststrand = 0;
            scndstrand = 1;
        }else{
            firststrand = 1;
            scndstrand = 0;
        }
    }

    @Override
    public void add(Gene g){
        IntervalTree[] its = content.get(g.chromosome);
        if(its==null){
            IntervalTree<Gene>[] ts = new IntervalTree[]{new IntervalTree(),new IntervalTree()};
            if(g.strand.charAt(0)=='+'){
                ts[firststrand].add(g);
            }else{
                ts[scndstrand].add(g);
            }
            content.put(g.chromosome,ts);
        }else{
            if(g.strand.charAt(0)=='+'){
                its[firststrand].add(g);
            }else{
                its[scndstrand].add(g);
            }
        }
    }

    @Override
    public List<Gene> getOuterGenes(ReadPair p) {
        if(!p.first.getReadNegativeStrandFlag()){
            firstStrandIndex = 0;
        }else{
            firstStrandIndex = 1;
        }
        genes = new ArrayList<>();
        content.get(p.first.getReferenceName())[firstStrandIndex].getIntervalsSpanning(p.alignmentStart,p.alignmentEnd,genes);
        return genes;
    }

    @Override
    public boolean hasContainedGene(ReadPair p) {
        genes = new ArrayList<>();
        content.get(p.first.getReferenceName())[firstStrandIndex].getIntervalsSpannedBy(p.alignmentStart,p.alignmentEnd,genes);
        return genes.size()>0;
    }

    @Override
    public int getNeighbourDistance(ReadPair p) {
        genes = new ArrayList<>();
        int d1;
        int d2;
        content.get(p.first.getReferenceName())[firstStrandIndex].getIntervalsLeftNeighbor(p.alignmentStart,p.alignmentEnd,genes);
        if(genes.size()>0){
            if(genes.get(0).region.start>=p.alignmentStart | genes.get(0).region.end>=p.alignmentStart){
                return 0;
            }else{
                d1 = p.alignmentStart - genes.get(0).region.end;
            }
            content.get(p.first.getReferenceName())[firstStrandIndex].getIntervalsRightNeighbor(p.alignmentStart,p.alignmentEnd,genes);
            if(genes.size()>1){
                if(genes.get(1).region.start<=p.alignmentEnd | genes.get(1).region.end<=p.alignmentEnd){
                    return 0;
                }else{
                    d2 = genes.get(1).region.start - p.alignmentEnd;
                    return d1<d2 ? d1-1 : d2-1;
                }
            }else{
                return d1-1;
            }
        }else{
            content.get(p.first.getReferenceName())[firstStrandIndex].getIntervalsRightNeighbor(p.alignmentStart,p.alignmentEnd,genes);
            if(genes.get(0).region.start<=p.alignmentEnd | genes.get(0).region.end<=p.alignmentEnd){
                return 0;
            }else{
                d2 = genes.get(0).region.start - p.alignmentEnd;
                return d2-1;
            }
        }
    }

    @Override
    public boolean getAntisense(ReadPair p) {
        genes = new ArrayList<>();
        content.get(p.first.getReferenceName())[1-firstStrandIndex].getIntervalsSpanning(p.alignmentStart,p.alignmentEnd,genes);
        return genes.size()>0;
    }

}
