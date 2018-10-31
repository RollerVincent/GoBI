package parser;

import gtf.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GtfParser implements Iterable<Gene> {

    private Gene currentGene;
    private BufferedReader reader;
    private Gene gene = new Gene();
    private Transcript transcript = new Transcript();
    private Exon exon = new Exon();
    private CodingSequence codingSequence = new CodingSequence();
    private boolean done = false;
    private List<String> encounteredExons = new ArrayList<String>();


    public GtfParser(String path){
        try {
            reader = new BufferedReader(new FileReader(path));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.charAt(0)!='#') {
                    String[] data = line.split("\t");
                    if(data[2].charAt(0)=='e'){
                        CheckLine(data);                                        // INITIALISATION AT FIRST EXON ANNOTATION
                        break;
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Annotation All(){
        Annotation a = new Annotation();
        for (Gene g:this){
            a.genes.put(g.id,g);
            for(Transcript t : g.transcripts){
                a.transcripts.put(t.id,t);
                if(t.codingSequence!=null){
                    a.proteins.put(t.codingSequence.id,t.codingSequence);
                }
            }
        }
        return a;
    }

    private boolean CheckLine(String[] data){

        boolean newGene = false;
        exon = new Exon();
        exon.region = new Region(Integer.valueOf(data[3]), Integer.valueOf(data[4]));
        Gene tmpGene = new Gene();
        Transcript tmpTranscript = new Transcript();
        Annotation.setAttributes(tmpGene,tmpTranscript,exon,data[8]);

        if(!tmpGene.getAttribute("gene_id").equals(gene.getAttribute("gene_id"))){
            currentGene = gene;
            gene = tmpGene;
            encounteredExons = new ArrayList<String>();
            gene.chromosome = data[0];
            gene.strand = data[6];
            gene.id = gene.getAttribute("gene_id");
            newGene = true;

        }if(!tmpTranscript.getAttribute("transcript_id").equals(transcript.getAttribute("transcript_id"))){
            transcript = tmpTranscript;
            transcript.id = transcript.getAttribute("transcript_id");
            gene.transcripts.add(transcript);
        }

        exon.id = exon.getAttribute("exon_id");
        if(exon.id==null){
            exon.id = gene.id+"_"+exon.region.start+":"+exon.region.end;
        }
        if(!encounteredExons.contains(exon.id)){
            encounteredExons.add(exon.id);
            gene.exons.add(exon);
            gene.regionVector.add(exon.region);
        }else{
            exon = gene.exons.get(encounteredExons.indexOf(exon.id));
        }
        transcript.exons.add(exon);
        transcript.regionVector.add(exon.region);

        if(newGene){
            return false;
        }
        return true;
    }

    @Override
    public Iterator<Gene> iterator() {
        return new Iterator<Gene>() {
            @Override
            public boolean hasNext() {
                return !done;
            }
            @Override
            public Gene next() {
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] data = line.split("\t");
                        if (data[2].charAt(0) == 'e') {                                         // EXON ANNOTATION
                            if(!CheckLine(data)){
                                break;
                            }
                        }else if(data[2].charAt(0) == 'C'){
                            if(transcript.codingSequence==null){
                                codingSequence = new CodingSequence();
                                Annotation.setAttributes(codingSequence,data[8]);
                                codingSequence.id = codingSequence.getAttribute("protein_id");
                                if(codingSequence.id==null){
                                    codingSequence.id = codingSequence.getAttribute("ccsd_id");
                                }
                                if(codingSequence.id==null){
                                    codingSequence.id = codingSequence.getAttribute("ccsdid");
                                }
                                transcript.codingSequence = codingSequence;
                                gene.codingTranscripts.add(transcript);
                            }
                            transcript.codingSequence.regionVector.add(new Region(Integer.valueOf(data[3]), Integer.valueOf(data[4])));
                        }
                    }if(line==null){
                        currentGene = gene;
                        done=true;
                        reader.close();
                    }

                }catch (IOException e) {
                    e.printStackTrace();
                }
                return currentGene;
            }
        };
    }
}
