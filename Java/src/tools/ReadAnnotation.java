package tools;


import gtf.Gene;
import gtf.RegionVector;
import gtf.Transcript;
import htsjdk.samtools.*;
import parser.Parser;
import sam.ReadPair;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReadAnnotation {

    HashMap<String, ReadPair> pairs;
    SAMRecordIterator records;
    SamReader samReader;
    String currentReference = " ";



    public ReadAnnotation(String path){

        pairs = new HashMap<>();

        File bamFile = new File(path);
        samReader = SamReaderFactory.makeDefault().validationStringency(ValidationStringency.SILENT).open(bamFile);
        records = samReader.iterator();




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

                if(!ref.equals(currentReference)) { //TODO: optimize ref.equals (per pair only ?)
                    currentReference = ref;
                    pairs = new HashMap<>();
                }

                p = pairs.get(readName);
                if (p != null) {
                    if (firstOfPair) {
                        p.first = sr;
                    } else {
                        p.second = sr;
                    }
                    pairs.remove(readName);
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
            pair.mergeRegions();
            pair.updateMisMatchCount();
            pair.updateClipping();
            String m = pair.matchedTranscripts(genes);
            if(m!=null){
                System.out.println(m);
            }


            return pair.readName+"\tmm:"+pair.mm+"\tclipping:"+pair.clipping+"\tnsplit:"+pair.nsplit+"\tgcount:";
        }else{
            splitincons+=1;
            return pair.readName+"\tsplit-inconsistent:true";
        }

    }

    public String processPair(ReadPair pair, int gdist, boolean antisense){

        pair.updateRegions();
        if(!pair.inconsistent()){
            pair.mergeRegions();
            pair.updateMisMatchCount();
            pair.updateClipping();
            gcount0+=1;
            return pair.readName+"\tmm:"+pair.mm+"\tclipping:"+pair.clipping+"\tnsplit:"+pair.nsplit+"\tgcount:0\tgdist:"+gdist+"\tantisense:"+antisense;

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
