package gtf;

import java.util.ArrayList;
import java.util.List;

public class Gene extends AnnotationType {

    public List<Transcript> transcripts = new ArrayList<Transcript>();
    public List<Transcript> codingTranscripts = new ArrayList<Transcript>();
    public List<Exon> exons = new ArrayList<Exon>();
    public String chromosome;
    public String strand = "";


}
