package gtf.forest;

import augmentedTree.IntervalTree;
import gtf.Gene;
import sam.ReadPair;

import java.util.HashMap;
import java.util.List;

public interface Forest {

    public HashMap<String, IntervalTree<Gene>> content = new HashMap<>();

    void add(Gene g);

    void delete(String ref);

    List<Gene> getOuterGenes(ReadPair p);

    boolean hasContainedGene(ReadPair p);

    int getNeighbourDistance(ReadPair p);

    boolean getAntisense(ReadPair p);

    void selectTree(String ref);

    boolean currentTreeNull();

}
