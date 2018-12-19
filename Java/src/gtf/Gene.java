package gtf;

import augmentedTree.Interval;

import java.util.ArrayList;
import java.util.List;

public class Gene extends AnnotationType implements Interval {

    public List<Transcript> transcripts = new ArrayList<Transcript>();
    public List<Transcript> codingTranscripts = new ArrayList<Transcript>();
    public String chromosome;
    public String strand = null;

    public void setRegion(){
        region = transcripts.get(0).regionVector.toRegion();
        int e;
        for (int i = 1; i < transcripts.size(); i++) {
            e = transcripts.get(i).regionVector.toRegion().end;
            if(e>region.end){
                region.end=e;
            }
        }
    }

    public RegionVector mergedTranscripts(){ //TODO: in gtf parser, treemap with exons per gene
        RegionVector out = transcripts.get(0).regionVector;
        RegionVector a;
        RegionVector b;
        for (int t = 1; t < transcripts.size(); t++) {
            a=out;
            out = new RegionVector();
            b=transcripts.get(t).regionVector;
            int i=0;
            int j=0;
            int mod = 0;
            int s = 0;
            Region piv;
            Region A = null;
            Region B = null;
            boolean newPiv = true;
            while(i<a.length() && j<b.length()){
                A = a.get(i);
                B = b.get(j);
                if(newPiv){
                    if(A.start<B.start){
                        mod = 0;
                        s = A.start;
                    }else{
                        mod = 1;
                        s = B.start;
                    }
                    newPiv = false;
                }
                if(mod==0){
                    if(B.end<=A.end){
                        j += 1;
                    }else if(B.start<=A.end){
                        mod = 1;
                        i += 1;
                    }else{
                        out.add(new Region(s,A.end));
                        i+=1;
                        newPiv = true;
                    }
                }else{
                    if(A.end<=B.end){
                        i += 1;
                    }else if(A.start<=B.end){
                        mod = 0;
                        j += 1;
                    }else{
                        out.add(new Region(s,B.end));
                        j+=1;
                        newPiv = true;
                    }
                }
            }
            if(i==a.length()){
                if(mod==1){
                    out.add(new Region(s,B.end));
                    j += 1;
                }
                for (int k = j; k < b.length() ; k++) {
                    out.add(b.get(k));
                }
            }else if(j==b.length()){
                if(mod==0){
                    out.add(new Region(s,A.end));
                    i += 1;
                }
                for (int k = i; k < a.length() ; k++) {
                    out.add(a.get(k));
                }
            }
        }
        return out;
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
