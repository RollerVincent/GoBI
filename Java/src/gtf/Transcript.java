package gtf;

import java.util.ArrayList;
import java.util.List;

public class Transcript extends AnnotationType {

    public CodingSequence codingSequence = null;
    public List<Exon> exons = new ArrayList<Exon>();

}
