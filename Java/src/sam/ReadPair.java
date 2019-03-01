package sam;

import gtf.*;
import htsjdk.samtools.AlignmentBlock;
import htsjdk.samtools.SAMRecord;
import java.util.ArrayList;
import java.util.List;

public class ReadPair {

    public String readName;
    public SAMRecord first;
    public SAMRecord second;
    public int alignmentStart;
    public int alignmentEnd;
    public List<Gene> genes;
    public RegionVector regionVector;

    public int mm;
    public int nsplit;
    public int clipping = 0;
    public int gcount;
    public int pcrindex;

    public RegionVector fwv;
    public RegionVector rwv;
    public int fwl;
    public int rwl;
    boolean turned;
    public boolean newRef = false;





    public ReadPair(String readName){
        this.readName = readName;
    }


    public void updateMisMatchCount(){ //TODO: firstPair --> predefine "NM" or "nM" or "xM"
        Integer m = (Integer) first.getAttribute("NM");
        if(m!=null){
            mm = m + (int)second.getAttribute("NM");
        }else{
            m = (Integer) first.getAttribute("nM");
            if(m!=null){
                mm = m + (int)second.getAttribute("nM");
            }else{
                mm = (int)second.getAttribute("XM") + (int)second.getAttribute("XM");
            }
        }
    }


    public void updateClipping(){
        clipping += first.getAlignmentStart()-first.getUnclippedStart();
        clipping += first.getUnclippedEnd()-first.getAlignmentEnd();
        clipping += second.getAlignmentStart()-second.getUnclippedStart();
        clipping += second.getUnclippedEnd()-second.getAlignmentEnd();
    }


    public void updatePCRindex(PCRindex pcr){
        pcrindex = pcr.getIndex(this);
    }


    public void updateRegions(){
        int s;
        int e;
        int lastEnd = -1;
        int i=-1;
        fwv = new RegionVector();
        for(AlignmentBlock ab : first.getAlignmentBlocks()){
            s = ab.getReferenceStart();
            e = s + ab.getLength()-1;
            if(s<=lastEnd+1){
                fwv.get(i).end = e;
            }else{
                fwv.add(new Region(s,e));
                i+=1;
            }
            lastEnd=e;
        }
        lastEnd = -1;
        i=-1;
        rwv = new RegionVector();
        for(AlignmentBlock ab : second.getAlignmentBlocks()){
            s = ab.getReferenceStart();
            e = s + ab.getLength()-1;
            if(s<=lastEnd+1){
                rwv.get(i).end = e;
            }else{
                rwv.add(new Region(s,e));
                i+=1;
            }
            lastEnd=e;
        }
        fwl = fwv.length();
        rwl = rwv.length();
    }


    public void updateBounds() {
        if (first.getAlignmentStart() < second.getAlignmentStart()) {
            alignmentStart = first.getAlignmentStart();
            turned=false;
        } else {
            alignmentStart = second.getAlignmentStart();
            turned=true;
            if(first.getAlignmentStart()==alignmentStart){
                if (first.getAlignmentEnd() < second.getAlignmentEnd()) {
                    turned=false;
                }
            }

        }
        if (first.getAlignmentEnd() > second.getAlignmentEnd()) {
            alignmentEnd = first.getAlignmentEnd();
        } else {
            alignmentEnd = second.getAlignmentEnd();
        }
    }


    boolean check_inconsistency(RegionVector a, RegionVector b, int al, int bl){
        int i=0;
        int j=0;
        while (i < al-1 && j<bl) {
            int s = a.get(i).end+1;
            int e = a.get(i+1).start-1;
            if(s<=e) {
                Region r = b.get(j);
                if (r.end < s) {
                    j += 1;
                } else {
                    if (r.end <= e | r.start <= e) {
                        return true;
                    } else {
                        i += 1;
                    }
                }
            }else{
                i+=1;
            }
        }
        return false;
    }


    public boolean inconsistent(){
        if(fwl>1){
            if(rwl>1){
                return (check_inconsistency(fwv,rwv,fwl,rwl) | check_inconsistency(rwv,fwv,rwl,fwl)); //TODO: optimizing only one function call
            }else{
                return check_inconsistency(fwv,rwv,fwl,rwl);
            }
        }else if(rwl>1){
            return check_inconsistency(rwv,fwv,rwl,fwl);
        }else{
            return false;
        }
    }


    public void mergeRegions() {
        regionVector = new RegionVector();
        RegionVector left;
        RegionVector right;
        int ll;
        int rl;



        if (turned) {
            right = fwv;
            left = rwv;
            rl = fwl;
            ll = rwl;
        } else {
            left = fwv;
            right = rwv;
            ll = fwl;
            rl = rwl;
        }
        int s;
        int e;
        nsplit=0;



        if(left.get(ll-1).end>=right.get(0).start-1){ // overlapping TODO: boolean overlaps as class variable ? split-inconsistency ?
            if(ll==1&&rl==1){
                regionVector.add(new Region(alignmentStart,alignmentEnd));
                nsplit=0;
                return;
            }

            for (int i = 0; i < ll; i++) {
                s = left.get(i).start;
                if (right.get(0).start <= left.get(i).end+1 && left.get(i).end<=right.get(0).end) {
                    e = right.get(0).end;
                    if(left.get(i).end!=right.get(0).end){
                        i = ll;
                    }else{
                        if(left.toRegion().end<=right.toRegion().end){
                            i=ll;
                        }
                    }
                } else {
                    e = left.get(i).end;
                }
                //nsplit+=1;
                regionVector.add(new Region(s, e));
            }
            if(left.toRegion().end<=right.toRegion().end) {
                for (int i = 1; i < rl; i++) {
                    regionVector.add(new Region(right.get(i).start, right.get(i).end));
                    //nsplit+=1;
                }
            }
            nsplit = regionVector.length()-1;
        }else{
            for (int i = 0; i < ll; i++) {
                regionVector.add(left.get(i));
            }
            for (int i = 0; i < rl; i++) {
                regionVector.add(right.get(i));
            }
            nsplit=rl+ll-2;

        }
    }


    public  List<String> matchedTranscriptsRaw(List<Gene> genes){
        RegionVector cut;
        List<String> transcripts = new ArrayList<>();
        for(Gene g : genes){
            for(Transcript t : g.codingTranscripts){
                cut = t.regionVector.cut(fwv.toRegion());
                if(cut.equals(fwv)){
                    cut = t.regionVector.cut(rwv.toRegion());
                    if(cut.equals(rwv)){
                        transcripts.add(t.id);
                    }
                }
            }
        }
        return transcripts;
    }


    public  String matchedTranscripts(List<Gene> genes){
        List<String> matches = new ArrayList<>();
        RegionVector cut;
        List<String> transcripts;
        for(Gene g : genes){
            transcripts = new ArrayList<>();
            for(Transcript t : g.transcripts){
                cut = t.regionVector.cut(fwv.toRegion());
                if(cut.equals(fwv)){
                    cut = t.regionVector.cut(rwv.toRegion());
                    if(cut.equals(rwv)){
                        transcripts.add(t.id);
                    }
                }

            }
            if(transcripts.size()!=0){
                String s = g.id+","+g.getAttribute("gene_biotype")+":";
                for (int i = 0; i < transcripts.size()-1; i++) {
                    s+=transcripts.get(i)+",";
                }
                s+=transcripts.get(transcripts.size()-1);
                matches.add(s);
            }
        }
        if(matches.size()!=0){
            gcount = matches.size();
            String s="";
            for (int i = 0; i < matches.size() - 1; i++) {
                s+=matches.get(i)+"|";
            }
            s+=matches.get(matches.size()-1);
            return s;
        }
        return null;
    }


    public String mergedGenes(List<Gene> genes){
        List<String> merges = new ArrayList<>();
        boolean merged;
        Region intr;
        Region piv;
        for(Gene g : genes){
            RegionVector m = g.mergedTranscripts();
            merged = true;
            int i=0;
            int j=0;
            while(i<m.length()-1 && j<regionVector.length()){
                intr = new Region(m.get(i).end+1,m.get(i+1).start-1);
                piv = regionVector.get(j);
                if(piv.end<intr.start){
                    j+=1;
                }
                else if(piv.start<=intr.end){
                    merged = false;
                    i=m.length();
                }
                else{
                    i+=1;
                }
            }
            if(merged){
                merges.add(g.id+","+g.getAttribute("gene_biotype")+":MERGED");
            }
        }
        if(merges.size()!=0){
            gcount = merges.size();
            String out = merges.get(0);
            for (int i = 1; i < merges.size(); i++) {
                out+="|"+merges.get(i);
            }
            return out;
        }
        return null;
    }


    public String intronicGenes(List<Gene> genes){
        String out = genes.get(0).id+","+genes.get(0).getAttribute("gene_biotype")+":INTRON";
        for (int i = 1; i < genes.size(); i++) {
            out+="|"+ genes.get(i).id+","+genes.get(i).getAttribute("gene_biotype")+":INTRON";
        }
        gcount = genes.size();
        return out;
    }

}


















