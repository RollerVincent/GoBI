package gtf.forest;

import augmentedTree.IntervalTree;
import gtf.Gene;
import sam.ReadPair;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class UnStrandedForest implements Forest {

    public HashMap<String, IntervalTree<Gene>> content = new HashMap<>();
    private List<Gene> genes;
    public IntervalTree<Gene> currentTree;

    @Override
    public void selectTree(String ref){
        currentTree = content.get(ref);
    }

    @Override
    public boolean currentTreeNull(){
        return currentTree == null;
    }

    @Override
    public void add(Gene g) {
        IntervalTree it = content.get(g.chromosome);
        if(it==null){
            it = new IntervalTree();
            it.add(g);
            content.put(g.chromosome,it);
        }else {
            it.add(g);
        }
    }


    @Override
    public void delete(String ref){
        content.remove(ref);
    }


    @Override
    public List<Gene> getOuterGenes(ReadPair p) {
        genes = new ArrayList<>();
        currentTree.getIntervalsSpanning(p.alignmentStart,p.alignmentEnd,genes);
        return genes;
    }


    @Override
    public boolean hasContainedGene(ReadPair p) {
        genes = new ArrayList<>();
        currentTree.getIntervalsSpannedBy(p.alignmentStart,p.alignmentEnd,genes);
        return genes.size()>0;
    }


    @Override
    public int getNeighbourDistance(ReadPair p) {
        genes = new ArrayList<>();
        int d1;
        int d2;
        currentTree.getIntervalsLeftNeighbor(p.alignmentStart,p.alignmentEnd,genes);
        if(genes.size()>0){
            if(genes.get(0).region.start>=p.alignmentStart | genes.get(0).region.end>=p.alignmentStart){
                return 0;
            }else{
                d1 = p.alignmentStart - genes.get(0).region.end;
            }
            currentTree.getIntervalsRightNeighbor(p.alignmentStart,p.alignmentEnd,genes);
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
            currentTree.getIntervalsRightNeighbor(p.alignmentStart,p.alignmentEnd,genes);
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
        return false;
    }

}
