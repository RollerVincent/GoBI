package tools;

import gtf.Gene;
import gtf.Region;
import gtf.Transcript;
import parser.Parser;
import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class ReadSimulation {

    int length;
    int frlength;
    int SD;
    float mutationrate;
    byte[] nucleotideBytes = new byte[4];
    public RandomAccessFile fastaRaf;
    long readcount=0;

    HashMap<String,HashMap<String,Integer>> readcounts = new HashMap<>();
    HashMap<String, Long[]> fastaIndex = new HashMap<>();
    HashMap<Byte,Byte> complementNucleotideBytes = new HashMap<>();

    BufferedWriter fw_writer;
    BufferedWriter rv_writer;
    BufferedWriter info_writer;

    String scoring;

    Random random;

    List<Integer> nonMutated;



    public ReadSimulation(int length, int frlength, int SD, float mutationrate) {

        this.length = length;
        this.frlength = frlength;
        this.SD = SD;
        this.mutationrate = mutationrate;
        this.random = new Random();


        byte[] nucleotideBytes = "ATGC\n".getBytes();

        complementNucleotideBytes.put(nucleotideBytes[0],nucleotideBytes[1]);
        complementNucleotideBytes.put(nucleotideBytes[1],nucleotideBytes[0]);
        complementNucleotideBytes.put(nucleotideBytes[2],nucleotideBytes[3]);
        complementNucleotideBytes.put(nucleotideBytes[3],nucleotideBytes[2]);
        complementNucleotideBytes.put(nucleotideBytes[4],nucleotideBytes[4]);

        this.scoring = "\n"+IntStream.range(0, this.length).mapToObj(i -> "I").collect(Collectors.joining(""))+"\n";


        this.nonMutated = new ArrayList<>();
        for (int i = 0; i < this.length; i++) {
            nonMutated.add(i);
        }


    }


    public void initFasta(String fastapath, String indexpath){
        try {
            this.fastaRaf = new RandomAccessFile(fastapath, "r");
            BufferedReader reader = new BufferedReader(new FileReader(indexpath));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] s = line.split("\t");
                this.fastaIndex.put(s[0],new Long[]{Long.parseLong(s[1]),Long.parseLong(s[2]),Long.parseLong(s[3]),Long.parseLong(s[4])});
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void initReadcounts(String path){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(path));
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] s = line.split("\t");
                if(readcounts.get(s[0])==null){
                    readcounts.put(s[0],new HashMap<String,Integer>());
                }
                readcounts.get(s[0]).put(s[1],Integer.parseInt(s[2]));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void initWriters(String path){
        fw_writer = Parser.Writer(path+"/fw.fastq");
        rv_writer = Parser.Writer(path+"/rw.fastq");
        info_writer = Parser.Writer(path+"/read.mappinginfo");
        try {
            info_writer.write("readid\tchr\tgene\ttranscript\tt_fw_regvec\tt_rw_regvec\tfw_regvec\trw_regvec\tfw_mut\trw_mut\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void closeWriters(){
        try {
            fw_writer.close();
            rv_writer.close();
            info_writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void simulateReads(Gene gene) {
        HashMap<String, Integer> rc = readcounts.get(gene.id);
        if (rc != null) {  // FOUND GENE

            Long[] indices = fastaIndex.get(gene.chromosome);
            for (Transcript t : gene.transcripts) {
                Integer counts = rc.get(t.id);
                if (counts != null) { // FOUND TRANSCRIPT

                    String[] sequence = getSequence(gene,t,indices);
                    byte[] s1 = sequence[0].getBytes();
                    byte[] s2 = sequence[1].getBytes();

                    int[] exonlengths = new int[t.regionVector.length()];
                    int c = 0;
                    for (Region r : t.regionVector) {
                        exonlengths[c] = r.end-r.start+1;
                        c+=1;
                    }



                    int l = sequence[0].length();
                    String cgt = "\t"+gene.chromosome+"\t"+gene.id+"\t"+t.id+"\t";
                    for (int i = 0; i < counts; i++) {
                          simulateRead(t,s1,s2, l, gene, cgt, exonlengths);
                          readcount += 1;
                    }
                }
            }
        }
    }


    private String[] getSequence (Gene gene, Transcript t, Long[] indices){
        StringBuilder seq = new StringBuilder();
        String reverseComplement = "";
        for (int i = 0; i < t.regionVector.regions.size(); i++) {

            Region r = t.regionVector.regions.get(i);
            long start = indices[1] + ((r.start - 1) / indices[2]) * indices[3] + ((r.start - 1) % indices[2]);
            long end = indices[1] + ((r.end - 1) / indices[2]) * indices[3] + ((r.end - 1) % indices[2]);
            try {

                byte[] exon = new byte[(int)(end-start+1)];
                byte[] revcomp;
                fastaRaf.seek(start);
                fastaRaf.read(exon);
                if(gene.strand.charAt(0)=='-'){
                    byte[] tmp = new byte[exon.length];
                    for (int j = 0; j < exon.length; j++) {
                        tmp[j] = complementNucleotideBytes.get(exon[exon.length-1-j]);
                    }
                    revcomp = exon;
                    exon = tmp;
                }else{
                    revcomp = new byte[exon.length];
                    for (int j = 0; j < exon.length; j++) {
                        revcomp[j] = complementNucleotideBytes.get(exon[exon.length-1-j]);
                    }
                }
                String e = new String(exon).replace("\n","");
                String rv = new String(revcomp).replace("\n","");
                seq.append(e);
                reverseComplement = rv + reverseComplement;

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return new String[]{seq.toString(),reverseComplement};
    }


    private void simulateRead(Transcript t, byte[] s, byte[] rc, int l, Gene g, String cgt, int[] exonlengths){

        String[] o = new String[2];

        int fl = Math.min(l,Math.max(this.length,(int)(this.random.nextGaussian()*this.SD+this.frlength)));
        int pos = this.random.nextInt(l-fl+1);
        int end = pos+fl;

        byte[] fw = new byte[this.length];
        byte[] rv = new byte[this.length];

        for (int i = 0; i < this.length; i++) {
            fw[i] = s[pos+i];
            rv[i] = rc[l-end+i];
        }

        String fw_mut = mutate(fw);
        String rw_mut = mutate(rv);

        String fw_regvec;
        String rw_regvec;

        if(g.strand.charAt(0)=='+') {
            fw_regvec = getGenomicRegion(t, pos, pos + this.length, exonlengths);
            rw_regvec = getGenomicRegion(t,end-this.length, end, exonlengths);
        }else{
            int reversedpos = l-pos;
            fw_regvec = getGenomicRegion(t, reversedpos-this.length, reversedpos, exonlengths);
            int reversedend = l-end;
            rw_regvec = getGenomicRegion(t, reversedend, reversedend + this.length, exonlengths);

        }

        String pre = "@"+readcount+"\n";
        String suf = "\n+"+readcount+this.scoring;

        // OPTIMIZATION build strings as byte[] ?

        try {

            fw_writer.write(pre);
            fw_writer.write(new String(fw));
            fw_writer.write(suf);

            rv_writer.write(pre);
            rv_writer.write(new String(rv));
            rv_writer.write(suf);

            info_writer.write(String.valueOf(readcount));
            info_writer.write(cgt);
            info_writer.write(pos+"-"+(pos+this.length)+"\t");
            info_writer.write((end-this.length)+"-"+end+"\t");
            info_writer.write(fw_regvec+"\t");
            info_writer.write(rw_regvec+"\t");
            info_writer.write(fw_mut+"\t");
            info_writer.write(rw_mut);
            info_writer.write("\n");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private String mutate(byte[] b){
        String o= "";
        int mutCount = poisson(this.mutationrate * this.length);
        if(mutCount>0) {
            List<Integer> mutated = new ArrayList<>();
            for (int i = 0; i < mutCount; i++) {
                int r = random.nextInt(this.nonMutated.size());
                int m = this.nonMutated.get(r);
                b[m] = complementNucleotideBytes.get(b[m]);
                o += m + ",";
                this.nonMutated.remove(r);
                mutated.add(m);
            }
            o = o.substring(0, o.length() - 1);
            this.nonMutated.addAll(mutated);
        }
        return o;
    }


    private String getGenomicRegion(Transcript t, int start, int end, int[] exl){
        String out = null;
        int count = 0;
        int i = 0;
        for (Region r : t.regionVector) {
            count += exl[i];
                if(count>start){
                    if(out==null){
                        int prev = count - exl[i];
                        int offset = start - prev;
                        out = ""+(r.start+offset);
                    }else{
                        out+=r.start;
                    }
                    if(count>=end){
                        int prev = count - exl[i];
                        int offset = end - prev;
                        out += "-"+(r.start+offset);
                        return out;

                    }else{
                        out += "-"+(r.end+1)+"|";
                    }
                }
            i+=1;
        }
        return null;
    }


    private final int poisson(float a) {
        double limit = Math.exp(-a), prod = this.random.nextDouble();
        int n;
        for (n = 0; prod >= limit; n++)
            prod *= this.random.nextDouble();
        return n;
    }

}
