package tools;


import gtf.Gene;
import htsjdk.samtools.*;
import sam.PCRindex;
import sam.ReadPair;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class ReadAnnotation {

    HashMap<String, ReadPair> pairs;
    SAMRecordIterator records;
    SamReader samReader;
    String currentReference = " ";
    public PCRindex pcr;
    boolean newRef = false;



    public ReadAnnotation(String path, boolean strandness){
        pairs = new HashMap<>();
        samReader = SamReaderFactory.makeDefault().validationStringency(ValidationStringency.SILENT).open(new File(path));
        records = samReader.iterator();
        pcr = new PCRindex(strandness);
    }


    public ReadPair nextPair() {
        SAMRecord sr;
        String ref;
        String readName;
        boolean firstOfPair;
        ReadPair p;
        ReadPair newPair;
        while (records.hasNext()) {
            sr = records.next();

            if(!((sr.getReadNegativeStrandFlag()==sr.getMateNegativeStrandFlag()) | !sr.getReadPairedFlag() | sr.getReadUnmappedFlag() | sr.getMateUnmappedFlag() | sr.getNotPrimaryAlignmentFlag())) {  // Preliminary ignoring conditions

                readName = sr.getReadName();
                firstOfPair = sr.getFirstOfPairFlag();
                ref = sr.getReferenceName();


                if(!ref.equals(currentReference)) {
                    newRef = true;
                    currentReference = ref;
                    pairs = new HashMap<>();
                }

                p = pairs.get(readName);
                if (p != null) {
                    if (firstOfPair) {
                        p.first = sr; //TODO: left and right instead of first and second
                    } else {
                        p.second = sr;
                    }
                    pairs.remove(readName);

                    if(newRef){
                        p.newRef = newRef;
                        newRef = false;
                    }
                    return p;
                } else {
                    newPair = new ReadPair(readName);

                    if (firstOfPair) {
                        newPair.first = sr;
                    } else {
                        newPair.second = sr;
                    }
                    pairs.put(readName, newPair);
                }

            }

        }
        return null;
    }


    public int splitincons = 0;
    public int gcount0 = 0;

    public String processPair(ReadPair pair, List<Gene> genes){
        pair.updateRegions();
        if(!pair.inconsistent()){
            pair.mergeRegions(); //TODO: merge only after no transcript matched, needs nsplit method + pcrindex
            pair.updatePCRindex(pcr);
            pair.updateMisMatchCount();
            pair.updateClipping();
            String m = pair.matchedTranscripts(genes);
            if(m==null){
                m = pair.mergedGenes(genes);
                if(m==null){
                    m = pair.intronicGenes(genes);
                }
            }
            return pair.readName+"\tmm:"+pair.mm+"\tclipping:"+pair.clipping+"\tnsplit:"+pair.nsplit+"\tgcount:"+pair.gcount+"\t"+m+"\tpcrindex: "+pair.pcrindex;
        }else{
            splitincons+=1;
            return pair.readName+"\tsplit-inconsistent:true";
        }
    }


    public String processPair(ReadPair pair, int gdist, boolean antisense){
        pair.updateRegions();
        if(!pair.inconsistent()){
            pair.mergeRegions();
            pair.updatePCRindex(pcr);
            pair.updateMisMatchCount();
            pair.updateClipping();
            gcount0+=1;
            return pair.readName+"\tmm:"+pair.mm+"\tclipping:"+pair.clipping+"\tnsplit:"+pair.nsplit+"\tgcount:0\tgdist:"+gdist+"\tantisense:"+antisense+"\tpcrindex: "+pair.pcrindex;
        }else{
            splitincons+=1;
            return pair.readName+"\tsplit-inconsistent:true";
        }
    }


    public void close(){
        records.close();
        try {
            samReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
