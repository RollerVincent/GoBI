package gtf;

import augmentedTree.Interval;

import java.util.ArrayList;
import java.util.List;

public class Gene extends AnnotationType implements Interval {

    public List<Transcript> transcripts = new ArrayList<Transcript>();
    public List<Transcript> codingTranscripts = new ArrayList<Transcript>();
    public List<Exon> exons = new ArrayList<Exon>();
    public String chromosome;
    public String strand = null;

    public void setRegion(){

        int s = transcripts.get(0).regionVector.toRegion().start;
        int e = s;

        int c;
        for(Transcript t : transcripts) {

            c = t.regionVector.toRegion().end;
            if(c>e){
                e=c;
            }
        }
        region = new Region(s,e);

    }


    @Override
    public int getStart() {
        return region.start;
    }

    @Override
    public int getStop() {
        return region.end;
    }
}
