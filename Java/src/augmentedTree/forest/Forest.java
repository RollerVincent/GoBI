package augmentedTree.forest;

import gtf.Gene;
import sam.ReadPair;
import java.util.List;

public interface Forest {

    void add(Gene g);

    List<Gene> getOuterGenes(ReadPair p);

    boolean hasContainedGene(ReadPair p);

    int getNeighbourDistance(ReadPair p);

    boolean getAntisense(ReadPair p);

}
