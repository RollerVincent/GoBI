package parser;

import augmentedTree.Interval;
import augmentedTree.IntervalTree;
import gtf.forest.Forest;
import gtf.*;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class GtfParser implements Iterable<Gene> {

    private BufferedReader reader;
    private Gene gene = new Gene();
    private Transcript transcript = new Transcript();
    private Exon exon = new Exon();
    private CodingSequence codingSequence = new CodingSequence();
    private boolean done = false;
    private List<String> encounteredExons = new ArrayList<String>();
    private boolean currentNegativeStrand = false;
    private Gene lastGene;
    private Gene tmpGene;
    private Transcript tmpTranscript;
    private boolean excludeCDS = false;

    private Forest forest;
    private Iterator<Gene> gtfIterator;
    private  Gene cursor;
    private String lastRef = null;

    public GtfParser(String path){
        try {
            reader = new BufferedReader(new FileReader(path));
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.charAt(0)!='#') {
                    String[] data = line.split("\t");
                    if (data[2].charAt(0) == 'e') {// EXON ANNOTATION
                        exon = new Exon();
                        exon.region = new Region(Integer.valueOf(data[3]), Integer.valueOf(data[4]));
                        tmpGene = new Gene();
                        tmpTranscript = new Transcript();
                        Annotation.setAttributes(tmpGene,tmpTranscript,exon,data[8]);
                        loadGene(data,tmpGene,tmpTranscript);
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

    public void excludeCodingSequences(){
        excludeCDS = true;
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

    public void fillForest(Forest f){

        for(Gene g : this){
            g.setRegion();
            f.add(g);
        }


    }


    public void initForest(Forest f){
        forest = f;
        gtfIterator = this.iterator();
        cursor = gtfIterator.next();
    }

    public void seekTree(String ref){
        forest.selectTree(ref);
        if(forest.currentTreeNull()) {
            if(lastRef!=null){
                forest.delete(lastRef);
                System.out.println("deleting trees for ref: "+lastRef);
            }
            System.out.println("seeking: "+ref);
            while (!cursor.chromosome.equals(ref) && gtfIterator.hasNext()) {
                cursor.setRegion();
                forest.add(cursor);
                cursor = gtfIterator.next();
            }
            while (cursor.chromosome.equals(ref) && gtfIterator.hasNext()) {
                cursor.setRegion();
                forest.add(cursor);
                cursor = gtfIterator.next();
            }
            lastRef = ref;
            if (!gtfIterator.hasNext()) {
                cursor.setRegion();
                forest.add(cursor);
            }
            forest.selectTree(ref);

        }
    }




    public void loadGene(String[] data,Gene tmpGene, Transcript tmpTranscript){

        lastGene = gene;

        gene = new Gene();
        transcript = new Transcript();

        gene.id = tmpGene.getAttribute("gene_id");
        transcript.id = tmpTranscript.getAttribute("transcript_id");
        exon.id = exon.getAttribute("exon_id");

        if(exon.id==null){
            exon.id = gene.id+"_"+exon.region.start+":"+exon.region.end;
        }

        gene.attributes = tmpGene.attributes;
        gene.chromosome = data[0];
        gene.strand = data[6];
        if(gene.strand.charAt(0)=='-'){
            currentNegativeStrand = true;
        }else{
            currentNegativeStrand = false;
        }

        transcript.regionVector.add(exon.region);
        gene.transcripts.add(transcript);
        transcript.exons.add(exon);

    }

    private void loadTranscript(Transcript tmp){
        transcript = tmp;
        transcript.id = transcript.getAttribute("transcript_id");
        gene.transcripts.add(transcript);
    }

    private void loadCodingSequence(String[] data){
        codingSequence = new CodingSequence();
        Annotation.setAttributes(codingSequence,data[8]);
        codingSequence.id = codingSequence.getAttribute("protein_id");
        if(codingSequence.id==null){
            codingSequence.id = codingSequence.getAttribute("ccsd_id");
        }
        if(codingSequence.id==null){
            codingSequence.id = codingSequence.getAttribute("ccsdid");
        }
        if(codingSequence.id==null){
            codingSequence.id = transcript.id+"_CDS";
        }
        transcript.codingSequence = codingSequence;
        gene.codingTranscripts.add(transcript);
    }

    public boolean processLine(String[] data){
        exon = new Exon();
        exon.region = new Region(Integer.valueOf(data[3]), Integer.valueOf(data[4]));
        tmpGene = new Gene();
        tmpTranscript = new Transcript();
        Annotation.setAttributes(tmpGene,tmpTranscript,exon,data[8]);
        if(!tmpGene.getAttribute("gene_id").equals(gene.id)){
            loadGene(data,tmpGene,tmpTranscript);
            return false;
        }else{
            if(!tmpTranscript.getAttribute("transcript_id").equals(transcript.id)){
                loadTranscript(tmpTranscript);
            }
            exon.id = exon.getAttribute("exon_id");
            if(exon.id==null){
                exon.id = gene.id+"_"+exon.region.start+":"+exon.region.end;
            }
            if(currentNegativeStrand) {
                transcript.exons.add(0, exon);
                transcript.regionVector.add(0,exon.region);

            }else {
                transcript.exons.add(exon);
                transcript.regionVector.add(exon.region);

            }
            return true;
        }
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
                            if (!processLine(data)) {
                                return lastGene;
                            }
                        } else if (!excludeCDS) {
                            if (data[2].charAt(0) == 'C') {
                                if (transcript.codingSequence == null) {
                                    loadCodingSequence(data);
                                }
                                transcript.codingSequence.regionVector.add(new Region(Integer.valueOf(data[3]), Integer.valueOf(data[4])));
                            }
                        }

                    }if(line==null){
                        done=true;
                        reader.close();
                        return gene;
                    }
                }catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
        };
    }
}
